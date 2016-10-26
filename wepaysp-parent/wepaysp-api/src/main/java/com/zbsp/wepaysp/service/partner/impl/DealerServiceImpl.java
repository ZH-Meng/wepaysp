package com.zbsp.wepaysp.service.partner.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.config.NestedRoleCode;
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
import com.zbsp.wepaysp.po.manage.SysUser.userLevel;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysLogService;
import com.zbsp.wepaysp.service.partner.DealerService;
import com.zbsp.wepaysp.vo.partner.DealerVO;

public class DealerServiceImpl extends BaseService implements DealerService {

	private SysLogService sysLogService;
	
	@Override
	public DealerVO doJoinTransQueryDealerByOid(String dealerOid) {
		Validator.checkArgument(StringUtils.isBlank(dealerOid), "商戶Oid不能为空！");
		DealerVO dealerVO = new DealerVO();
		Dealer dealer = commonDAO.findObject(Dealer.class, dealerOid);
		if (dealer != null) {
			BeanCopierUtil.copyProperties(dealer, dealerVO);
			dealerVO.setPartnerName(dealer.getPartner().getCompany());
		}
		return dealerVO;
	}

	@Override
	public List<DealerVO> doJoinTransQueryDealerList(Map<String, Object> paramMap, int startIndex, int maxResult) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int doJoinTransQueryDealerCount(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return 0;
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
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getTelephone()), "固定电话不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getCompany()), "公司不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerVO.getAddress()), "地址不能为空");
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

        SysUser newUser = new SysUser();
        Dealer dealer = new Dealer();
        Partner partner = null;
        SysUser user = commonDAO.findObject(SysUser.class, operatorUserOid);
        // 查找服务商
        if (user != null) {
        	partner = user.getPartner();
        } else {
        	throw new IllegalAccessException("非法操作：非服务商用户不能添加商户");
        }

        // 保存商户
        BeanCopierUtil.copyProperties(dealerVO, dealer);
        dealer.setPartner(partner);
        
        dealer.setIwoid(Generator.generateIwoid());
        // TODO 商户ID
        dealer.setCreator(creator);
        commonDAO.save(dealer, false);
        
        // TODO  创建默认门店

        // 保存用户
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
        newUser.setUserLevel(userLevel.dealer.getValue());
        newUser.setDealer(dealer);
        newUser.setCreator(creator);
        commonDAO.save(newUser, false);

        // 设置默认角色
        String roleCode = NestedRoleCode.DEALER;

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
	public DealerVO doTransUpdateDealer(DealerVO dealer, String modifier, String operatorUserOid, String logFunctionOid)
			throws AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}
	
}
