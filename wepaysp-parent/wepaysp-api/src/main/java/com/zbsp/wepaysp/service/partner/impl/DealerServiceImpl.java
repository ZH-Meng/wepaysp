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
            //dealerVO.setPartnerCompany(dealer.getPartner().getCompany());
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

        String currentUserOid = MapUtils.getString(paramMap, "currentUserOid");
        Validator.checkArgument(StringUtils.isBlank(currentUserOid), "当前用户Oid不能为空！");
        // 获取当前用户
        SysUser currentUser = commonDAO.findObject(SysUser.class, currentUserOid);
        if (currentUser == null) {
            throw new IllegalStateException("非法操纵：当前用户不存在！");
        } else if (currentUser.getPartner() == null) {
            throw new IllegalStateException("非法操纵：当前用户不是服务商，不能查看商户列表！");
        }

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

        Partner currentPartner = currentUser.getPartner();
        // 当前用户为顶级服务商时，查看管辖商户及下级服务商管辖商户...
        if (currentPartner.getLevel() == 1) {
            sql.append(" and d.partner1Oid = :PARTNER1OID");
            sqlMap.put("PARTNER1OID", currentPartner.getIwoid());
        } else {// 默认为非顶级服务商，该服务商所辖的所有商户
            sql.append(" and d.partner.iwoid = :PARTNEROID");
            sqlMap.put("PARTNEROID", currentPartner.getIwoid());
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

        String currentUserOid = MapUtils.getString(paramMap, "currentUserOid");
        Validator.checkArgument(StringUtils.isBlank(currentUserOid), "当前用户Oid不能为空！");
        // 获取当前用户
        SysUser currentUser = commonDAO.findObject(SysUser.class, currentUserOid);
        if (currentUser == null) {
            throw new IllegalStateException("非法操纵：当前用户不存在！");
        } else if (currentUser.getPartner() == null) {
            throw new IllegalStateException("非法操纵：当前用户不是服务商，不能查看商户列表！");
        }

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

        Partner currentPartner = currentUser.getPartner();
        // 当前用户为顶级服务商时，查看管辖商户及下级服务商管辖商户...
        if (currentPartner.getLevel() == 1) {
            sql.append(" and d.partner1Oid = :PARTNER1OID");
            sqlMap.put("PARTNER1OID", currentPartner.getIwoid());
        } else {// 默认为非顶级服务商，该服务商所辖的所有商户
            sql.append(" and d.partner.iwoid = :PARTNEROID");
            sqlMap.put("PARTNEROID", currentPartner.getIwoid());
        }

        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @Override
    public DealerVO doTransAddDealer(DealerVO dealerVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException, IllegalAccessException {
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
        Validator.checkArgument(dealerVO.getFeeRate() == null, "分润比率不能为空");

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
        String dealerId = Generator.generateSequenceNum((Integer) seqObj);
        dealer.setDealerId(dealerId);// 商户ID

        Partner partner = null;
        SysUser user = commonDAO.findObject(SysUser.class, operatorUserOid);
        // 查找所属服务商
        if (user != null) {
            partner = user.getPartner();
        } else {
            throw new IllegalAccessException("非法操作：非服务商用户不能添加商户");
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
        
        //TODO 商户号
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
        String storeId = Generator.generateSequenceNum((Integer) seqObj);
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
        newUser.setPartner(partner);
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
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建商户[商户ID=" + dealer.getDealerId() + ", 公司=" + dealer.getCompany() + ", 联系人=" + dealer.getContactor() + "]", processTime, processTime, null, dealer.toString(), SysLog.State.success.getValue(), partner.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        // 添加用户日志logFunctionOid 存 商户添加按钮oid
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建用户[用户ID=" + newUser.getUserId() + ", 用户名称=" + newUser.getUserName() + "]", processTime, processTime, null, newUser.toString(), SysLog.State.success.getValue(), newUser.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());

        return dealerVO;
    }

    @Override
    public DealerVO doTransUpdateDealer(DealerVO dealerVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
        Validator.checkArgument(dealerVO == null, "服务商对象不能为空");
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
        Validator.checkArgument(dealerVO.getFeeRate() == null, "分润比率不能为空");

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
        dealer.setFeeRate(dealerVO.getFeeRate());
        dealer.setState(dealerVO.getState());
        dealer.setEmail(dealerVO.getEmail());
        dealer.setQqNumber(dealerVO.getQqNumber());
        dealer.setTechSupportPerson(dealerVO.getTechSupportPerson());
        dealer.setTechSupportPhone(dealerVO.getTechSupportPhone());
        dealer.setRemark(dealerVO.getRemark());

        dealer.setModifier(modifier);
        commonDAO.update(dealer);

        String newDealerStr = dealer.toString();
        Date processEndTime = new Date();
        // 记录日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "修改商户[商户联系人：" + dealer.getContactor() + ",商户公司：" + dealer.getCompany() + "]", processBeginTime, processEndTime, dealerStr, newDealerStr, SysLog.State.success.getValue(), dealer.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());

        BeanCopierUtil.copyProperties(dealer, dealerVO);
        return dealerVO;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
