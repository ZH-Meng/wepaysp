package com.zbsp.wepaysp.service.partner.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.config.SysNestedRoleCode;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysAuthority;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysLogService;
import com.zbsp.wepaysp.service.partner.DealerService;
import com.zbsp.wepaysp.vo.partner.DealerVO;

public class DealerServiceImpl
    extends BaseService
    implements DealerService {

    private SysLogService sysLogService;

    @SuppressWarnings("unchecked")
    @Override
    public DealerVO doJoinTransQueryDealerByOid(String dealerOid) {
        Validator.checkArgument(StringUtils.isBlank(dealerOid), "商戶Oid不能为空！");
        DealerVO dealerVO = new DealerVO();
        Dealer dealer = commonDAO.findObject(Dealer.class, dealerOid);
        if (dealer != null) {
            BeanCopierUtil.copyProperties(dealer, dealerVO);
            // 查找用户
            String sqlStr = "from SysUser s where s.dealer.iwoid = :IWOID";
            Map<String, Object> sqlMap = new HashMap<String, Object>();
            sqlMap.put("IWOID", dealer.getIwoid());
            List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sqlStr, sqlMap, false);
            if (userList != null && !userList.isEmpty()) {
                dealerVO.setLoginId(userList.get(0).getUserId());
                // partnerVO.setLoginPwd(userList.get(0).getLoginPwd());
            }
            // dealerVO.setPartnerCompany(dealer.getPartner().getCompany());
        }
        return dealerVO;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DealerVO> doJoinTransQueryDealerList(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<DealerVO> resultList = new ArrayList<DealerVO>();

        /* 在当前用户所属服务商的管辖的所有商户集合中模糊查询 */
        String state = MapUtils.getString(paramMap, "state");
        String company = MapUtils.getString(paramMap, "company");
        String loginId = MapUtils.getString(paramMap, "loginId");
        String moblieNumber = MapUtils.getString(paramMap, "moblieNumber");
        String coreDataFlag = MapUtils.getString(paramMap, "coreDataFlag");
        String partnerOid = MapUtils.getString(paramMap, "partnerOid");
        String partnerEmployeeOid = MapUtils.getString(paramMap, "partnerEmployeeOid");
        Validator.checkArgument(StringUtils.isBlank(partnerOid), "服务商Oid不能为空！");
        
        StringBuffer sql = new StringBuffer("select distinct(d) from Dealer d, SysUser u where u.dealer=d");
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

        sql.append(" order by d.dealerId desc");
        List<Dealer> dealerList = (List<Dealer>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (dealerList != null && !dealerList.isEmpty()) {
            for (Dealer dealer : dealerList) {
                DealerVO vo = new DealerVO();
                BeanCopierUtil.copyProperties(dealer, vo);

                String sqlStr = "from SysUser s where s.dealer.iwoid = :IWOID";
                sqlMap.clear();
                sqlMap.put("IWOID", dealer.getIwoid());
                List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sqlStr, sqlMap, false);
                if (userList != null && !userList.isEmpty()) {
                    vo.setLoginId(userList.get(0).getUserId());// 商户登陆名
                }
                // 所属代理商
                vo.setPartnerCompany(dealer.getPartner().getCompany());
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
        String company = MapUtils.getString(paramMap, "company");
        String loginId = MapUtils.getString(paramMap, "loginId");
        String moblieNumber = MapUtils.getString(paramMap, "moblieNumber");
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
    public DealerVO doTransAddDealer(DealerVO dealerVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException, NotExistsException {
        Validator.checkArgument(dealerVO == null, "商户对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getLoginId()), "登录名不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getLoginPwd()), "登录密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getContactor()), "联系人不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getMoblieNumber()), "手机号不能为空");
        // Validator.checkArgument(StringUtils.isBlank(dealerVO.getTelephone()), "固定电话不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getCompany()), "公司不能为空");
        // Validator.checkArgument(StringUtils.isBlank(dealerVO.getAddress()), "地址不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getState()), "状态不能为空");
        //Validator.checkArgument(StringUtils.isBlank(dealerVO.getPartnerOid()), "服务商Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getPartnerEmployeeOid()), "业务员Oid不能为空");

        String sql = "select count(u.iwoid) from SysUser u where u.userId = :USERID and u.state <> :CANCELSTATE ";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USERID", dealerVO.getLoginId());
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());

        int idResult = commonDAO.queryObjectCount(sql, paramMap, false);
        if (idResult > 0) {
            throw new AlreadyExistsException("登录名重复！");
        }

        // 创建商户
        Dealer dealer = new Dealer();
        BeanCopierUtil.copyProperties(dealerVO, dealer);
        dealer.setIwoid(Generator.generateIwoid());

        // 获取 商户ID下一个序列值
        sql = "select nextval('" + SysSequenceCode.DEALER + "') as sequence_value";
        paramMap.clear();
        Object seqObj = commonDAO.findObject(sql, paramMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("商户Id对应序列记录不存在");
        }
        String dealerId = Generator.generateSequenceNum((Integer) seqObj, SysSequenceMultiple.DEALER);
        dealer.setDealerId(dealerId);// 商户ID

        Partner partner = null;
        PartnerEmployee partnerEmployee = commonDAO.findObject(PartnerEmployee.class, dealerVO.getPartnerEmployeeOid());
        // 查找所属业务员
        if (partnerEmployee == null) {
            throw new NotExistsException("业务员不存在！");
        }  else {
            dealer.setPartnerEmployee(partnerEmployee);
            partner = partnerEmployee.getPartner();
        }
        if (partner == null) {
            throw new NotExistsException("服务商不存在");
        }
        dealer.setPartner(partner);// 所属服务商
        try {
            dealer.setPartnerLevel(partner.getLevel());
            if (partner.getLevel() == 1) {
                dealer.setPartner1Oid(partner.getIwoid());
            } else if (partner.getLevel() == 2) {
                dealer.setPartner1Oid(partner.getParentPartner().getIwoid());
                dealer.setPartner2Oid(partner.getIwoid());
            } else if (partner.getLevel() == 3) {
                dealer.setPartner1Oid(partner.getParentPartner().getParentPartner().getIwoid());
                dealer.setPartner2Oid(partner.getParentPartner().getIwoid());
                dealer.setPartner3Oid(partner.getIwoid());
            } else {
                logger.warn("所属服务商级别错误");
            }
        } catch (NullPointerException e) {
            logger.warn("所属服务商级别错误：所属服务商的上级或者上级的上级不存在");
        }
        
        dealer.setCreator(creator);
        commonDAO.save(dealer, false);

        // 创建默认门店
        Store defaultStore = new Store();
        defaultStore.setIwoid(Generator.generateIwoid());

        // 获取 门店ID下一个序列值
        sql = "select nextval('" + SysSequenceCode.STORE + "') as sequence_value";
        paramMap.clear();
        seqObj = commonDAO.findObject(sql, paramMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("商户Id对应序列记录不存在");
        }
        String storeId = Generator.generateSequenceNum((Integer) seqObj, SysSequenceMultiple.DEALER);
        defaultStore.setStoreId(storeId);
        defaultStore.setDealer(dealer);
        defaultStore.setCreator(creator);
        defaultStore.setStoreAddress(dealerVO.getAddress());
        defaultStore.setStoreName(dealerVO.getCompany());
        defaultStore.setStoreTel(StringUtils.isNotBlank(dealerVO.getTelephone()) ? dealerVO.getTelephone() : dealerVO.getMoblieNumber());
        commonDAO.save(defaultStore, false);

        // 保存用户
        SysUser newUser = new SysUser();
        newUser.setIwoid(Generator.generateIwoid());
        newUser.setState(SysUser.State.normal.getValue());
        newUser.setUserId(dealerVO.getLoginId());
        newUser.setUserName(dealerVO.getContactor());
        newUser.setLoginPwd(DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(dealerVO.getLoginPwd())));
        newUser.setLineTel(StringUtils.isNotBlank(dealerVO.getMoblieNumber()) ? dealerVO.getMoblieNumber() : dealerVO.getTelephone());
        newUser.setEmail(dealerVO.getEmail());
        newUser.setBuildType(SysUser.BuildType.create.getValue());
        newUser.setLastLoginTime(null);
        newUser.setDataPermisionType(SysUser.DataPermisionType.none.getValue());
        newUser.setUserLevel(SysUser.UserLevel.dealer.getValue());
        newUser.setDealer(dealer);
        newUser.setStore(defaultStore);
        newUser.setCreator(creator);
        commonDAO.save(newUser, false);

        // 设置默认角色
        String roleCode = SysNestedRoleCode.DEALER;

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

        BeanCopierUtil.copyProperties(dealer, dealerVO);

        Date processTime = new Date();

        // 增加商户日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建商户[商户ID=" + dealer.getDealerId() + ", 商户名称=" + dealer.getCompany() + ", 联系人=" + dealer.getContactor() + "]", processTime, processTime, null, dealer.toString(), SysLog.State.success.getValue(), partner.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        // 添加用户日志logFunctionOid 存 商户添加按钮oid
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建用户[用户ID=" + newUser.getUserId() + ", 用户名称=" + newUser.getUserName() + "]", processTime, processTime, null, newUser.toString(), SysLog.State.success.getValue(), newUser.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());

        return dealerVO;
    }

    @Override
    public DealerVO doTransUpdateDealer(DealerVO dealerVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
        Validator.checkArgument(dealerVO == null, "商户对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getIwoid()), "服务商Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(modifier), "修改人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        // 登录名、登录密码不允许修改
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getContactor()), "联系人不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getMoblieNumber()), "手机号不能为空");
        // Validator.checkArgument(StringUtils.isBlank(dealerVO.getTelephone()), "固定电话不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getCompany()), "公司不能为空");
        // Validator.checkArgument(StringUtils.isBlank(dealerVO.getAddress()), "地址不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getState()), "状态不能为空");

        Date processBeginTime = new Date();
        // 查找商户
        Dealer dealer = commonDAO.findObject(Dealer.class, dealerVO.getIwoid());
        if (dealer == null) {
            throw new NotExistsException("未找到要修改的商户对象");
        } else if (Partner.State.frozen.getValue().equals(dealer.getState())) {// 冻结
            throw new IllegalStateException("非法修改：商户冻结状态不允许修改！");
        }

        String dealerStr = dealer.toString();

        dealer.setContactor(dealerVO.getContactor());
        dealer.setCompany(dealerVO.getCompany());
        dealer.setMoblieNumber(dealerVO.getMoblieNumber());
        dealer.setAddress(dealerVO.getAddress());
        dealer.setTelephone(dealerVO.getTelephone());
        dealer.setState(dealerVO.getState());
        dealer.setEmail(dealerVO.getEmail());
        dealer.setQqNumber(dealerVO.getQqNumber());
        dealer.setTechSupportPerson(dealerVO.getTechSupportPerson());
        dealer.setTechSupportPhone(dealerVO.getTechSupportPhone());
        dealer.setRemark(dealerVO.getRemark());

        if ("on".equals(dealerVO.getCoreDataFlag())) {// 核心数据修改
            dealer.setSubAppid(dealerVO.getSubAppid());
            dealer.setSubMchId(dealerVO.getSubMchId());
        }
        // TODO 提单商户信息

        dealer.setModifier(modifier);
        commonDAO.update(dealer);

        String newDealerStr = dealer.toString();
        Date processEndTime = new Date();
        // 记录日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "修改商户[商户名称：" + dealer.getCompany() + " 手机号：" + dealer.getMoblieNumber() + "商户联系人：" + dealer.getContactor() + "]", processBeginTime, processEndTime, dealerStr, newDealerStr, SysLog.State.success.getValue(), dealer.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());

        //BeanCopierUtil.copyProperties(dealer, dealerVO);
        return dealerVO;
    }

    @Override
    public DealerVO doTransUpdateDealerBase(DealerVO dealerVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
        Validator.checkArgument(dealerVO == null, "服务商对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getIwoid()), "服务商Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(modifier), "修改人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        // 商户名称不可修改
        //Validator.checkArgument(StringUtils.isBlank(dealerVO.getCompany()), "公司不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getMoblieNumber()), "手机号不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getEmail()), "邮箱不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getQqNumber()), "QQ号码不能为空");

        Date processBeginTime = new Date();
        // 查找商户
        Dealer dealer = commonDAO.findObject(Dealer.class, dealerVO.getIwoid());
        if (dealer == null) {
            throw new NotExistsException("未找到要修改的商户对象");
        } else if (Partner.State.frozen.getValue().equals(dealer.getState())) {// 冻结
            throw new IllegalStateException("非法修改：商户冻结状态不允许修改！");
        }

        String dealerStr = dealer.toString();

        //dealer.setCompany(dealerVO.getCompany());
        dealer.setMoblieNumber(dealerVO.getMoblieNumber());
        dealer.setEmail(dealerVO.getEmail());
        dealer.setQqNumber(dealerVO.getQqNumber());

        dealer.setModifier(modifier);
        commonDAO.update(dealer);

        String newDealerStr = dealer.toString();
        Date processEndTime = new Date();
        // 记录日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "修改商户基本信息[商户名称：" + dealer.getCompany() + " 手机号：" + dealer.getMoblieNumber() + " 邮箱：" + dealer.getEmail() + " QQ号码：" + dealer.getQqNumber() + "]", processBeginTime, processEndTime, dealerStr, newDealerStr, SysLog.State.success.getValue(), dealer.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());
        return dealerVO;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
