package com.zbsp.wepaysp.service.partner.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
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
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysLogService;
import com.zbsp.wepaysp.service.partner.PartnerService;
import com.zbsp.wepaysp.vo.partner.PartnerVO;

public class PartnerServiceImpl extends BaseService implements PartnerService {

	private SysLogService sysLogService;

	@SuppressWarnings("unchecked")
	@Override
	public PartnerVO doJoinTransQueryPartnerByOid(String partnerOid) {
		Validator.checkArgument(StringUtils.isBlank(partnerOid), "代理商Oid不能为空");
		PartnerVO partnerVO = new PartnerVO();
		Partner partner = commonDAO.findObject(Partner.class, partnerOid);
		BeanCopierUtil.copyProperties(partner, partnerVO);
		// 查找用户
		String sqlStr = "from SysUser s where s.partner.iwoid = :IWOID";
		Map<String, Object> sqlMap = new HashMap<String, Object>();
		sqlMap.put("IWOID", partner.getIwoid());
		List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sqlStr, sqlMap, false);
		if (userList != null && !userList.isEmpty()) {
			partnerVO.setLoginId(userList.get(0).getUserId());
			//partnerVO.setLoginPwd(userList.get(0).getLoginPwd());
		}
		return partnerVO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PartnerVO> doJoinTransQueryPartnerList(Map<String, Object> paramMap, int startIndex, int maxResult) {
		String loginId = MapUtils.getString(paramMap, "loginId");
		String contactor = MapUtils.getString(paramMap, "contactor");
		String parentPartnerOid = MapUtils.getString(paramMap, "parentPartnerOid");
		String currentUserOid = MapUtils.getString(paramMap, "currentUserOid");
		String state = MapUtils.getString(paramMap, "state");
		// FIXME
		String parentCompany = MapUtils.getString(paramMap, "parentCompany");

		StringBuffer sql = new StringBuffer("from Partner p  where 1=1");
		Map<String, Object> sqlMap = new HashMap<String, Object>();

		if (StringUtils.isNotBlank(loginId)) {
			sql.append(" and p.loginId like :LOGINID");
			sqlMap.put("LOGINID", "%" + loginId + "%");
		}

		if (StringUtils.isNotBlank(contactor)) {
			sql.append(" and p.contactor like :CONTACTOR");
			sqlMap.put("CONTACTOR", "%" + contactor + "%");
		}

		sql.append(" and p.parentPartner.iwoid = :PARENTPARTNEROID");
		if (StringUtils.isBlank(parentPartnerOid)) {// 当前用户的代理商oid
			SysUser user = commonDAO.findObject(SysUser.class, currentUserOid);
			// 查找父代理商
			if (user != null && user.getPartner() != null) {
				parentPartnerOid = user.getPartner().getIwoid();
			}
		}
		sqlMap.put("PARENTPARTNEROID", parentPartnerOid);

		if (StringUtils.isNotBlank(state)) {
			sql.append(" and p.state = :STATE");
			sqlMap.put("STATE", state);
		}

		if (StringUtils.isNotBlank(parentCompany)) {
			// TODO
		}

		sql.append(" order by p.createTime desc");

		List<PartnerVO> resultList = new ArrayList<PartnerVO>();
		List<Partner> partnerList = (List<Partner>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex,
				maxResult);
		if (partnerList != null && !partnerList.isEmpty()) {
			for (Partner partner : partnerList) {
				PartnerVO vo = new PartnerVO();

				BeanCopierUtil.copyProperties(partner, vo);

				String sqlStr = "from SysUser s where s.partner.iwoid = :IWOID";
				sqlMap.clear();
				sqlMap.put("IWOID", partner.getIwoid());
				List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sqlStr, sqlMap, false);
				if (userList != null && !userList.isEmpty()) {
					vo.setLoginId(userList.get(0).getUserId());
				}
				vo.setParentCompany(partner.getParentPartner().getCompany());
				resultList.add(vo);
			}
		}

		return resultList;
	}

	@Override
	public int doJoinTransQueryPartnerCount(Map<String, Object> paramMap) {
		String loginId = MapUtils.getString(paramMap, "loginId");
		String contactor = MapUtils.getString(paramMap, "contactor");
		String parentPartnerOid = MapUtils.getString(paramMap, "parentPartnerOid");
		String currentUserOid = MapUtils.getString(paramMap, "currentUserOid");
		String state = MapUtils.getString(paramMap, "state");
		// FIXME
		String parentCompany = MapUtils.getString(paramMap, "parentCompany");

		StringBuffer sql = new StringBuffer("select count(p.iwoid) from Partner p  where 1=1");
		Map<String, Object> sqlMap = new HashMap<String, Object>();

		if (StringUtils.isNotBlank(loginId)) {
			sql.append(" and p.loginId like :LOGINID");
			sqlMap.put("LOGINID", "%" + loginId + "%");
		}

		if (StringUtils.isNotBlank(contactor)) {
			sql.append(" and p.contactor like :CONTACTOR");
			sqlMap.put("CONTACTOR", "%" + contactor + "%");
		}

		sql.append(" and p.parentPartner.iwoid = :PARENTPARTNEROID");
		if (StringUtils.isBlank(parentPartnerOid)) {// 当前用户的代理商oid
			SysUser user = commonDAO.findObject(SysUser.class, currentUserOid);
			// 查找父代理商
			if (user != null && user.getPartner() != null) {
				parentPartnerOid = user.getPartner().getIwoid();
			}
		}
		sqlMap.put("PARENTPARTNEROID", parentPartnerOid);

		if (StringUtils.isNotBlank(state)) {
			sql.append(" and p.state = :STATE");
			sqlMap.put("STATE", state);
		}

		if (StringUtils.isNotBlank(parentCompany)) {
			// TODO
		}
		return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
	}

	@Override
	public PartnerVO doTransAddPartner(PartnerVO partnerVO, String creator, String operatorUserOid,
			String logFunctionOid) throws AlreadyExistsException {
		Validator.checkArgument(partnerVO == null, "代理商对象不能为空");
		// TODO 校验参数
		Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
		Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
		Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
		Validator.checkArgument(StringUtils.isBlank(partnerVO.getLoginId()), "日志记录项Oid不能为空");

		String sql = "select count(u.iwoid) from SysUser u where u.userId = :USERID and u.state <> :CANCELSTATE ";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("USERID", partnerVO.getLoginId());
		paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());

		int idResult = commonDAO.queryObjectCount(sql, paramMap, false);

		if (idResult > 0) {
			throw new AlreadyExistsException("登录名重复！");
		}

		SysUser newUser = new SysUser();
		Partner partner = new Partner();
		Partner parentPartner = null;
		SysUser user = commonDAO.findObject(SysUser.class, operatorUserOid);
		// 查找父代理商
		if (user != null && user.getPartner() != null) {
			parentPartner = commonDAO.findObject(Partner.class, user.getPartner().getIwoid());
		}

		// 保存代理商
		BeanCopierUtil.copyProperties(partnerVO, partner);
		if (parentPartner != null) {
			partner.setParentPartner(parentPartner);
			if (parentPartner.getLevel() == 0) {
				partner.setLevel(1);
			} else {
				partner.setLevel(parentPartner.getLevel() + 1);
			}
		} else {
			partner.setLevel(0);// FIXME 服务商
		}
		partner.setIwoid(Generator.generateIwoid());
		partner.setCreator(creator);
		commonDAO.save(partner, false);

		// 保存用户
		newUser.setIwoid(Generator.generateIwoid());
		newUser.setState(SysUser.State.normal.getValue());
		newUser.setUserId(partnerVO.getLoginId());
		newUser.setUserName(partnerVO.getContactor());
		newUser.setLoginPwd(DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(partnerVO.getLoginPwd())));
		newUser.setBuildType(SysUser.BuildType.create.getValue());
		newUser.setLastLoginTime(null);
		newUser.setUserLevel(userLevel.partner.getValue());
		newUser.setPartner(partner);
		newUser.setCreator(creator);
		commonDAO.save(newUser, false);

		// 设置默认角色
		String roleCode = "";
		if (partner.getLevel() == 0) {
			roleCode = NestedRoleCode.SERVICE_PROVIDER;
		} else if (partner.getLevel() == 1) {
			roleCode = NestedRoleCode.FIRST_LEVEL_AGENT;
		} else if (partner.getLevel() == 2) {
			roleCode = NestedRoleCode.SECOND_LEVEL_AGENT;
		}

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

		BeanCopierUtil.copyProperties(partner, partnerVO);

		Date processTime = new Date();

		sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid,
				"创建用户[用户ID=" + newUser.getUserId() + ", 用户名称=" + newUser.getUserName() + "]", processTime, processTime,
				null, newUser.toString(), SysLog.State.success.getValue(), newUser.getIwoid(), logFunctionOid,
				SysLog.ActionType.create.getValue());

		return partnerVO;
	}

	@Override
	public PartnerVO doTransUpdatePartner(PartnerVO partnerVO, String modifier, String operatorUserOid,
			String logFunctionOid) throws AlreadyExistsException {
		Validator.checkArgument(partnerVO == null, "代理商对象不能为空");
		Validator.checkArgument(StringUtils.isBlank(partnerVO.getIwoid()), "代理商Oid不能为空");
		// TODO 校验参数
		Validator.checkArgument(StringUtils.isBlank(modifier), "修改人不能为空");
		Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
		Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
		Validator.checkArgument(StringUtils.isBlank(partnerVO.getLoginId()), "日志记录项Oid不能为空");

		Date processBeginTime = new Date();
		// 查找代理商
		Partner oldPartner = commonDAO.findObject(Partner.class, partnerVO.getIwoid());
		if (oldPartner == null) {
			throw new NotExistsException("未找到要修改的代理商对象");
		} // TODO 是否需要判断状态

		// TODO 检查重复
		String oldPartnerStr = oldPartner.toString();
		oldPartner.setContactor(partnerVO.getContactor());
		oldPartner.setCompany(partnerVO.getCompany());
		oldPartner.setMoblieNumber(partnerVO.getMoblieNumber());
		oldPartner.setAddress(partnerVO.getAddress());
		oldPartner.setTelephone(partnerVO.getTelephone());
		oldPartner.setBalance(partnerVO.getBalance());
		oldPartner.setContractBegin(partnerVO.getContractBegin());
		oldPartner.setContractEnd(partnerVO.getContractEnd());
		oldPartner.setFeeRate(partnerVO.getFeeRate());
		oldPartner.setState(partnerVO.getState());
		oldPartner.setRemark(partnerVO.getRemark());
		oldPartner.setEmail(partnerVO.getEmail());
		oldPartner.setModifier(modifier);
		
		String newPartnerStr = oldPartner.toString();

		Date processEndTime = new Date();
		// 记录日志
		sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid,
				"修改代理商[代理商联系人：" + oldPartner.getContactor() + ",代理商公司：" + oldPartner.getCompany() + "]",
				processBeginTime, processEndTime, oldPartnerStr, newPartnerStr, SysLog.State.success.getValue(),
				oldPartner.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());
		return null;
	}

	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

}
