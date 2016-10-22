package com.zbsp.wepaysp.manage.web.action.partner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.joda.time.format.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.service.partner.PartnerService;
import com.zbsp.wepaysp.vo.partner.PartnerVO;

public class PartnerAction extends PageAction implements SessionAware {

	private static final long serialVersionUID = -7078956274536886116L;

	private Map<String, Object> session;
	private PartnerVO partnerVO;
	private String contractBegin;
	private String contractEnd;
	private List<PartnerVO> partnerVoList;
	private PartnerService partnerService;

	@Override
	protected String query(int start, int size) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (partnerVO != null) {
				paramMap.put("parentPartnerOid", partnerVO.getParentPartnerOid());
			}
			paramMap.put("currentUserOid", manageUser.getIwoid());

			partnerVoList = partnerService.doJoinTransQueryPartnerList(paramMap, start, size);
			rowCount = partnerService.doJoinTransQueryPartnerCount(paramMap);
		} catch (Exception e) {
			logger.error("代理商查询列表错误：" + e.getMessage());
			setAlertMessage("代理商查询列表错误：" + e.getMessage());
		}
		return "partnerList";
	}

	public String list() {
		initPageData(100);
		return goCurrent();
	}

	public String goToCreatePartner() {
		logger.info("跳转创建代理商页面.");
		ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (partnerVO == null) {
			partnerVO = new PartnerVO();
		}
		partnerVO.setParentPartnerOid(manageUser.getIwoid());
		return "createPartner";
	}

	public String createPartner() {
		logger.info("开始创建代理商.");
		try {
			if (partnerVO == null) {
				logger.warn("创建代理商失败，参数" + partnerVO + "为空！");
				setAlertMessage("创建代理商失败！");
			}
			partnerVO.setContractBegin(getContractBegin());
			partnerVO.setContractEnd(getContractEnd());

			ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			partnerService.doTransAddPartner(partnerVO, manageUser.getUserId(), manageUser.getIwoid(),
					(String) session.get("currentLogFunctionOid"));
			logger.info("代理商" + partnerVO.getCompany() + "添加成功");
			setAlertMessage("代理商" + partnerVO.getCompany() + "添加成功");
		} catch (AlreadyExistsException e) {
			logger.error("代理商添加失败：" + e.getMessage());
			setAlertMessage("代理商添加失败：" + e.getMessage());
		} catch (Exception e) {
			logger.error("代理商添加错误：" + e.getMessage());
			setAlertMessage("代理商添加错误：" + e.getMessage());
		}
		return list();
	}

	public String goToUpdatePartner() {
		logger.info("跳转修改代理商页面.");
		if (partnerVO != null && StringUtils.isNotBlank(partnerVO.getIwoid())) {
			partnerVO = partnerService.doJoinTransQueryPartnerByOid(partnerVO.getIwoid());
		} else {
			logger.warn("非法修改代理商，参数partnerVO为空，partnerVO.getIwoid()或者！");
			setAlertMessage("非法修改代理商！");
		}
		return "updatePartner";
	}

	public String updatePartner() {
		logger.info("开始修改代理商.");
		try {
			if (partnerVO == null) {
				logger.warn("修改代理商失败，参数" + partnerVO + "为空！");
				setAlertMessage("修改代理商失败！");
			}
			partnerVO.setContractBegin(getContractBegin());
			partnerVO.setContractEnd(getContractEnd());

			ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			partnerService.doTransUpdatePartner(partnerVO, manageUser.getUserId(), manageUser.getIwoid(),
					(String) session.get("currentLogFunctionOid"));
			logger.info("代理商" + partnerVO.getCompany() + "修改成功");
			setAlertMessage("代理商" + partnerVO.getCompany() + "修改成功");
		} catch (NotExistsException e) {
			logger.error("代理商修改失败：" + e.getMessage());
			setAlertMessage("代理商修改失败：" + e.getMessage());
		} catch (Exception e) {
			logger.error("代理商添加错误：" + e.getMessage());
			setAlertMessage("代理商添加错误：" + e.getMessage());
		}
		return list();
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public PartnerVO getPartnerVO() {
		return partnerVO;
	}

	public void setPartnerVO(PartnerVO partnerVO) {
		this.partnerVO = partnerVO;
	}

	public Date getContractBegin() {
		return DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(contractBegin).toDate();
	}

	public void setContractBegin(String contractBegin) {
		this.contractBegin = contractBegin;
	}

	public Date getContractEnd() {
		return DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(contractEnd).toDate();
	}

	public void setContractEnd(String contractEnd) {
		this.contractEnd = contractEnd;
	}

	public List<PartnerVO> getPartnerVoList() {
		return partnerVoList;
	}

	public void setPartnerVoList(List<PartnerVO> partnerVoList) {
		this.partnerVoList = partnerVoList;
	}

	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
	}

}
