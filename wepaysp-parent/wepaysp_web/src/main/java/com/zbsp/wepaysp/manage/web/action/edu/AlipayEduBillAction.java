package com.zbsp.wepaysp.manage.web.action.edu;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduTotalBillService;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.vo.edu.AlipayEduBillVO;
import com.zbsp.wepaysp.vo.edu.AlipayEduTotalBillVO;

/**教育缴费账单明细Action*/
public class AlipayEduBillAction extends PageAction {

	private static final long serialVersionUID = -8734218055007641937L;
	private String childName;// 学生姓名
	private String userName;// 家长姓名
	private String orderStatus;
	private String totalBillOid;
	private AlipayEduTotalBillVO alipayEduTotalBillVO;
	private List<AlipayEduBillVO> alipayEduBillVOList;
	private AlipayEduBillService alipayEduBillService;
	private AlipayEduTotalBillService alipayEduTotalBillService;

	@SuppressWarnings("unchecked")
	@Override
	protected String query(int start, int size) {
		// 检查参数
		
		// 根据当前用户的类型进行不同颗粒的查询
		Map<String, Object> paramMap = new HashMap<String, Object>();
		ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (manageUser.getUserLevel() == SysUser.UserLevel.school.getValue()) {// 学校账户
			paramMap.put("schoolNo", manageUser.getDataSchool().getSchoolNo());
			paramMap.put("childName", childName);
			paramMap.put("userName", userName);
			paramMap.put("orderStatus", orderStatus);
			paramMap.put("totalBillOid", totalBillOid);
		} else {
			logger.warn("当前用户无权查看缴费账单明细！");
			setAlertMessage("当前用户无权查看缴费账单明细！");
			return "accessDenied";
		}
		alipayEduTotalBillVO = alipayEduTotalBillService.doJoinTransQueryAlipayEduTotalBillByOid(totalBillOid);
		rowCount = alipayEduBillService.doJoinTransQueryAlipayEduBillCount(paramMap);
		if (rowCount > 0) {
			Map<String, Object> resultMap = alipayEduBillService.doJoinTransQueryAlipayEduBill(paramMap, 0, -1);
			alipayEduBillVOList = (List<AlipayEduBillVO>) MapUtils.getObject(resultMap, "billList");
		}
		return "billList";
	}

	/**查询账单明细列表*/
	public String list() {
		initPageData(PageAction.defaultLargePageSize);
		return goCurrent();
	}

	/**下载账单*/
	public String downloadBill() {
		return "getBillData";
	}
	
	public InputStream getBillData() {
		return null;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getTotalBillOid() {
		return totalBillOid;
	}

	public void setTotalBillOid(String totalBillOid) {
		this.totalBillOid = totalBillOid;
	}

	public AlipayEduTotalBillVO getAlipayEduTotalBillVO() {
		return alipayEduTotalBillVO;
	}

	public List<AlipayEduBillVO> getAlipayEduBillVOList() {
		return alipayEduBillVOList;
	}
	
    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
		this.childName = childName;
	}

	public void setAlipayEduBillService(AlipayEduBillService alipayEduBillService) {
		this.alipayEduBillService = alipayEduBillService;
	}
    
    public void setAlipayEduTotalBillService(AlipayEduTotalBillService alipayEduTotalBillService) {
        this.alipayEduTotalBillService = alipayEduTotalBillService;
    }

}
