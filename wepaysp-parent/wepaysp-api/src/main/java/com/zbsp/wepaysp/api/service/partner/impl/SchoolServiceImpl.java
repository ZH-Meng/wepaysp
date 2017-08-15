package com.zbsp.wepaysp.api.service.partner.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayEcoEduKtSchoolinfoModifyResponse;
import com.zbsp.alipay.trade.config.Constants;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.partner.SchoolService;
import com.zbsp.wepaysp.api.util.AliPayEduUtil;
import com.zbsp.wepaysp.common.config.SysNestedRoleCode;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.dic.zone.AlipayEduRegion;
import com.zbsp.wepaysp.po.manage.SysAuthority;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.po.partner.School;
import com.zbsp.wepaysp.vo.partner.SchoolVO;

public class SchoolServiceImpl
    extends BaseService
    implements SchoolService {
    
    private SysLogService sysLogService;
   

    @SuppressWarnings("unchecked")
    @Override
    public List<SchoolVO> doJoinTransQuerySchoolList(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<SchoolVO> resultList = new ArrayList<SchoolVO>();

        /* 在当前用户所属服务商的管辖的所有学校集合中模糊查询 */

        String shcoolName = StringUtils.trim(MapUtils.getString(paramMap, "shcoolName"));
        String partnerEmployeeName = StringUtils.trim(MapUtils.getString(paramMap, "partnerEmployeeName"));

        String coreDataFlag = StringUtils.trim(MapUtils.getString(paramMap, "coreDataFlag"));
        String partnerOid = MapUtils.getString(paramMap, "partnerOid");
        
        StringBuffer sql = new StringBuffer("select distinct(d) from School d, SysUser u where u.school=d");
        Map<String, Object> sqlMap = new HashMap<String, Object>();


        if (StringUtils.isNotBlank(shcoolName)) {
            sql.append(" and d.shcoolName like :SCHOOLNAME");
            sqlMap.put("SCHOOLNAME", "%" + shcoolName + "%");
        }
        if (StringUtils.isNotBlank(partnerEmployeeName)) {
            sql.append(" and d.partnerEmployee.employeeName like :PARTNEREMPLOYEENAME");
            sqlMap.put("PARTNEREMPLOYEENAME", partnerEmployeeName+ "%");
        }
        
        if (StringUtils.isNotBlank(partnerOid)) {
        	if ("on".equals(coreDataFlag)) {
        		 // 当前用户为顶级服务商时，查看管辖商户及下级服务商管辖商户...
        		sql.append(" and d.partner1Oid = :PARTNER1OID");
                sqlMap.put("PARTNER1OID", partnerOid);
        	} else {// 默认为非顶级服务商，该服务商所辖的所有商户
        		 sql.append(" and d.partner.iwoid = :PARTNEROID");
                 sqlMap.put("PARTNEROID", partnerOid);
        	}
        }

        sql.append(" order by d.createTime desc");
        List<School> schoolList = (List<School>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (schoolList != null && !schoolList.isEmpty()) {
            for (School school : schoolList) {
                SchoolVO vo = new SchoolVO();
                BeanCopierUtil.copyProperties(school, vo);

                String sqlStr = "from SysUser s where s.school.iwoid = :IWOID";
                sqlMap.clear();
                sqlMap.put("IWOID", school.getIwoid());
                List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sqlStr, sqlMap, false);
                if (userList != null && !userList.isEmpty()) {
                    vo.setLoginId(userList.get(0).getUserId());// 商户登陆名
                }
                // 所属代理商
                vo.setPartnerCompany(school.getPartner() != null ? school.getPartner().getCompany() : null);
                // 关联业务员
                vo.setPartnerEmployeeName(school.getPartnerEmployee() != null ? school.getPartnerEmployee().getEmployeeName() : null);
                resultList.add(vo);
                if (!"on".equals(coreDataFlag)) {// 屏蔽掉公众号ID、微信支付商户号
                    vo.setSubAppid(null);
                    vo.setSubMchId(null);
                }
            }
        }

        return resultList;
    }

    @Override
    public int doJoinTransQueryDealerCount(Map<String, Object> paramMap) {
        /* 在当前用户所属服务商的管辖的所有商户集合中模糊查询 */
        String state = MapUtils.getString(paramMap, "state");
        String company = StringUtils.trim(MapUtils.getString(paramMap, "company"));
        String loginId = StringUtils.trim(MapUtils.getString(paramMap, "loginId"));
        String moblieNumber = StringUtils.trim(MapUtils.getString(paramMap, "moblieNumber"));
        String partnerOid = MapUtils.getString(paramMap, "partnerOid");
        String partnerEmployeeOid = MapUtils.getString(paramMap, "partnerEmployeeOid");
        Validator.checkArgument(StringUtils.isBlank(partnerOid), "服务商Oid不能为空！");
        
        StringBuffer sql = new StringBuffer("select count(distinct d.iwoid) from Dealer d, SysUser u where u.dealer=d");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(loginId)) {
            sql.append(" and u.userId like :LOGINID");
            sqlMap.put("LOGINID", "%" + loginId + "%");
        }
        if (StringUtils.isNotBlank(moblieNumber)) {
            sql.append(" and d.moblieNumber like :MOBLIENUMBER");
            sqlMap.put("MOBLIENUMBER", "%" + moblieNumber + "%");
        }
        if (StringUtils.isNotBlank(state)) {
            sql.append(" and d.state = :STATE");
            sqlMap.put("STATE", state);
        }
        if (StringUtils.isNotBlank(company)) {
            sql.append(" and d.company like :COMPANY");
            sqlMap.put("COMPANY", "%" + company + "%");
        }
        if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            sql.append(" and d.partnerEmployee.iwoid = :PARTNEREMPLOYEEOID");
            sqlMap.put("PARTNEREMPLOYEEOID", partnerEmployeeOid);
        }

        // 获取服务商
        Partner partner = commonDAO.findObject(Partner.class, partnerOid);
        if (partner == null) {
            throw new NotExistsException("服务商不存在！");
        }

        // 当前用户为顶级服务商时，查看管辖商户及下级服务商管辖商户...
        if (partner.getLevel() == 1) {
            sql.append(" and d.partner1Oid = :PARTNER1OID");
            sqlMap.put("PARTNER1OID", partner.getIwoid());
        } else {// 默认为非顶级服务商，该服务商所辖的所有商户
            sql.append(" and d.partner.iwoid = :PARTNEROID");
            sqlMap.put("PARTNEROID", partner.getIwoid());
        }

        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @Override
    public SchoolVO doTransAddSchool(SchoolVO schoolVO, String creator, String operatorUserOid, String logFunctionOid, String schoolNo)
        throws AlreadyExistsException, NotExistsException, AlipayApiException {
        Validator.checkArgument(schoolVO == null, "商户对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(schoolVO.getLoginId()), "登录名不能为空");
        Validator.checkArgument(StringUtils.isBlank(schoolVO.getLoginPwd()), "登录密码不能为空");

        //Validator.checkArgument(StringUtils.isBlank(dealerVO.getPartnerOid()), "服务商Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(schoolVO.getPartnerEmployeeOid()), "业务员Oid不能为空");

        String sql = "select count(u.iwoid) from SysUser u where u.userId = :USERID and u.state <> :CANCELSTATE ";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USERID", StringUtils.trim(schoolVO.getLoginId()));
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());

        int idResult = commonDAO.queryObjectCount(sql, paramMap, false);
        if (idResult > 0) {
            throw new AlreadyExistsException("登录名重复！");
        }

        // 创建学校信息
        School school = new School();
        BeanCopierUtil.copyProperties(schoolVO, school);
        //设置学校所属省地市名称
        String districtCode = school.getDistrictCode();
        Map<String, Object> districtMap = new HashMap<String, Object>();
        districtMap.put("districtCode",districtCode);
        AlipayEduRegion region = commonDAO.findObject("select a from AlipayEduRegion a where a.districtCode =  :districtCode ", districtMap, false);
        school.setProvinceName(region.getProvinceName());
        school.setCityName(region.getCityName());
        school.setDistrictName( region.getDistrictName());
        school.setIwoid(Generator.generateIwoid());
        
        // 学校NO
        school.setSchoolNo(schoolNo);

        Partner partner = null;
        PartnerEmployee partnerEmployee = commonDAO.findObject(PartnerEmployee.class, schoolVO.getPartnerEmployeeOid());
        // 查找所属业务员
        if (partnerEmployee == null) {
            throw new NotExistsException("未找到业务员！");
        }  else {
            school.setPartnerEmployee(partnerEmployee);
            partner = partnerEmployee.getPartner();
        }
        if (partner == null) {
            throw new NotExistsException("未找到服务商！");
        }
        school.setPartner(partner);// 所属服务商
        try {
            school.setPartnerLevel(partner.getLevel());
            if (partner.getLevel() == 1) {
                school.setPartner1Oid(partner.getIwoid());
                school.setIsvName(partner.getCompany());
                school.setIsvPhone(partner.getMoblieNumber());
                school.setIsvPid(partner.getIsvPartnerId());
            } else if (partner.getLevel() == 2) {
            	logger.warn("所属服务商级别错误");
//                school.setPartner1Oid(partner.getParentPartner().getIwoid());
//                school.setPartner2Oid(partner.getIwoid());
            } else if (partner.getLevel() == 3) {
            	logger.warn("所属服务商级别错误");
//                school.setPartner1Oid(partner.getParentPartner().getParentPartner().getIwoid());
//                school.setPartner2Oid(partner.getParentPartner().getIwoid());
//                school.setPartner3Oid(partner.getIwoid());
            } else {
                logger.warn("所属服务商级别错误");
            }
        } catch (NullPointerException e) {
            logger.warn("所属服务商级别错误：所属服务商的上级或者上级的上级不存在");
        }

		school.setCreator(creator);

		AlipayEcoEduKtSchoolinfoModifyResponse response = AliPayEduUtil.schoolInfoModify(school);
		logger.info("创建学校结果:{}", JSONUtil.toJSONString(response, true));
		if (response == null || !Constants.SUCCESS.equals(response.getCode())) {// 交易或者结束
			throw new AlipayApiException("创建学校失败");
		} else {
			logger.info("创建成功");
			school.setSchoolNo(response.getSchoolNo());
		}

		commonDAO.save(school, false);
     

        // 保存用户
        SysUser newUser = new SysUser();
        newUser.setIwoid(Generator.generateIwoid());
        newUser.setState(SysUser.State.normal.getValue());
        newUser.setUserId(StringUtils.trim(schoolVO.getLoginId()));
        newUser.setUserName(school.getShcoolName());
        newUser.setLoginPwd(DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(StringUtils.trim(schoolVO.getLoginPwd()))));
        
 
        newUser.setBuildType(SysUser.BuildType.create.getValue());
        newUser.setLastLoginTime(null);
        newUser.setDataPermisionType(SysUser.DataPermisionType.none.getValue());
        newUser.setUserLevel(SysUser.UserLevel.school.getValue());
        newUser.setSchool(school);
        newUser.setCreator(creator);
        commonDAO.save(newUser, false);

        // 设置默认角色
        String roleCode = SysNestedRoleCode.SCHOOL;

        sql = "from SysRole r where r.roleId=:ROLEID and r.state <> :CANCELSTATE";
        paramMap.clear();
        paramMap.put("ROLEID", roleCode);
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());
        SysRole role = commonDAO.findObject(sql, paramMap, false);

        if (role == null) {
            throw new NotExistsException("未找到角色信息");
        } else {
            // 创建角色用户关系
            SysAuthority sysAuthority = new SysAuthority();
            sysAuthority.setIwoid(Generator.generateIwoid());
            sysAuthority.setSysRole(role);
            sysAuthority.setSysUser(newUser);

            commonDAO.save(sysAuthority, false);

            if (role.getUseState().intValue() != SysRole.UseState.used.getValue()) {
                role.setUseState(SysRole.UseState.used.getValue());
                commonDAO.update(role);
            }
        }

        BeanCopierUtil.copyProperties(school, schoolVO);

        Date processTime = new Date();

        // 增加商户日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建学校[学校No=" + school.getSchoolNo() + ", 学校名称=" + school.getShcoolName() + ", 联系人=" + school.getTechSupportPhone() + "]", processTime, processTime, null, school.toString(), SysLog.State.success.getValue(), partner.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        // 添加用户日志logFunctionOid 存 商户添加按钮oid
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建用户[用户ID=" + newUser.getUserId() + ", 用户名称=" + newUser.getUserName() + "]", processTime, processTime, null, newUser.toString(), SysLog.State.success.getValue(), newUser.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());

        return schoolVO;
    }
 
    
    @Override
    public String doJoinTransGetTopPartnerOid(String dealerOid) {
        Validator.checkArgument(StringUtils.isBlank(dealerOid), "dealerOid不能为空！");
        String topPartnerOid = null;
        Dealer d = commonDAO.findObject(Dealer.class, dealerOid);
        if (d != null) {
            topPartnerOid = d.getPartner1Oid();
        }
        return topPartnerOid;
    }    

 
    
    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
