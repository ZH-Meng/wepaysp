package com.zbsp.wepaysp.manage.web.action.edu;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduTotalBillService;
import com.zbsp.wepaysp.api.service.main.edu.AlipayEduBillMainService;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.util.ExcelUtil;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.vo.edu.AlipayEduBillVO;
import com.zbsp.wepaysp.vo.edu.AlipayEduTotalBillVO;

/**教育缴费账单明细Action*/
public class AlipayEduBillAction extends PageAction {

	private static final long serialVersionUID = -8734218055007641937L;
	private String childName;// 学生姓名
	private String userMobile;// 家长手机号
	private String orderStatus;
	private String totalBillOid;
	private String billOid;
	private AlipayEduTotalBillVO alipayEduTotalBillVO;
	private List<AlipayEduBillVO> alipayEduBillVOList;
	private AlipayEduBillService alipayEduBillService;
	private AlipayEduTotalBillService alipayEduTotalBillService;
	private AlipayEduBillMainService alipayEduBillMainService;

	private String billDataName;
    private Map<String, Object> dataMap;
    
	@Override
	protected String query(int start, int size) {
		// 检查参数
		
	    try {
	        // 根据当前用户的类型进行不同颗粒的查询
	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        if (manageUser.getUserLevel() == SysUser.UserLevel.school.getValue()) {// 学校账户
	            paramMap.put("schoolNo", manageUser.getDataSchool().getSchoolNo());
	            paramMap.put("childName", childName);
	            paramMap.put("userMobile", userMobile);
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
	            alipayEduBillVOList = alipayEduBillService.doJoinTransQueryAlipayEduBill(paramMap, 0, -1);
	        }
        } catch (Exception e) {
            logger.error("查看缴费账单明细错误：{}", e.getMessage(), e);
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
        logger.info("下载账单明细-开始");
        String path = AlipayEduBillAction.class.getResource("eduBillList.xlsx").getPath();

        // 导出条件：无
        // 导出结果
        List<List<?>> exportList = new ArrayList<List<?>>();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("totalBillOid", totalBillOid);
        List<AlipayEduBillVO> billList = alipayEduBillService.doJoinTransQueryAlipayEduBill(paramMap, 0, -1);
        exportList.add(billList);

        String fileName = "缴费账单明细-".concat(totalBillOid).concat(".xlsx");
        try {
            billDataName = new String(fileName.getBytes("GBK"), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.info("下载账单明细-结束");
        return new ExcelUtil().writeData(exportList, path, null, true);
    }
	
    /**
     * 账单异步通知
     */
    public void asynNotify() {
        String logPrefix = "处理支付宝教育缴费账单异步通知请求 - ";
        logger.info(logPrefix + "开始");
        try {
            // 校验签名
            HttpServletRequest request = ServletActionContext.getRequest();
            HttpServletResponse response = ServletActionContext.getResponse();

            // 获取所有请求参数
            @SuppressWarnings("unchecked")
            Map<String, String[]> parameterMap = request.getParameterMap();

            Map<String, String> paramMap = new HashMap<String, String>();
            for (Map.Entry<String, String[]> parameter : parameterMap.entrySet()) {
                paramMap.put(parameter.getKey(), parameter.getValue()[0]);
            }
            logger.info(logPrefix + "异步通知请求参数：{}", JSONUtil.toJSONString(paramMap, true));

            Map<String, Object> resultMap = alipayEduBillMainService.handleAlipayPayEduBillNotify(paramMap);
            String result = MapUtils.getString(resultMap, "result");
            response.getWriter().write(result); // 返回非 success 也无意义，支付宝有重发机制
            logger.info(logPrefix + "处理结果为({})", result);
        } catch (Exception e) {
            logger.error(logPrefix + "异常 : {}", e.getMessage(), e);
        }
        logger.info(logPrefix + "结束");
    }
    
    public String close() {
        logger.info("关闭账单-开始");
        try {
            dataMap = alipayEduBillMainService.closeEduBill(null, billOid);
        } catch (Exception e) {
            logger.error("关闭账单-错误: {}", e.getMessage(), e);
            dataMap = new HashMap<>();
            dataMap.put("code", "error");
            dataMap.put("msg", "操作失败");
        }
        logger.info("关闭账单-结束");
    	return "jsonResult";
    }
    
    public String closeAll() {
        logger.info("关闭全部账单-开始");
        try {
            dataMap = alipayEduBillMainService.closeEduBill(totalBillOid, null);
        } catch (Exception e) {
            logger.error("关闭全部账单-错误: {}", e.getMessage(), e);
            dataMap = new HashMap<>();
            dataMap.put("code", "error");
            dataMap.put("msg", "操作失败");
        }
        logger.info("关闭全部账单-结束");
        return "jsonResult";
    }
	
    public String getUserMobile() {
        return userMobile;
    }
    
    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
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
    
    public void setAlipayEduBillMainService(AlipayEduBillMainService alipayEduBillMainService) {
        this.alipayEduBillMainService = alipayEduBillMainService;
    }
    
    public String getBillDataName() {
        return billDataName;
    }
    
    public void setBillOid(String billOid) {
        this.billOid = billOid;
    }
    
    public Map<String, Object> getDataMap() {
        return dataMap;
    }
    
}
