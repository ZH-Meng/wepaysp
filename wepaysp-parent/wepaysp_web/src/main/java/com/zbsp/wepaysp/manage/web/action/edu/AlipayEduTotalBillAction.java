package com.zbsp.wepaysp.manage.web.action.edu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.api.service.pay.AlipayEduTotalBillService;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.util.PoiExcel2k7Helper;
import com.zbsp.wepaysp.manage.web.util.PoiExcelHelper;
import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.vo.edu.AlipayEduTotalBillVO;

/**教育缴费总账单Action*/
public class AlipayEduTotalBillAction extends PageAction {

	private static final long serialVersionUID = -8734218055007641937L;
	private String billFileRootDir;
	private String beginTime;
	private String endTime;
	private String billName;
	private List<AlipayEduTotalBillVO> alipayEduTotalBillVOList;
	private AlipayEduTotalBillService alipayEduTotalBillService;
	/**表格固定列头*/
	private String[] fixedExcelHeaders = {"年级/班级", "学生姓名", "家长手机号", "缴费账单名称", "合计"};// TOOD
	
	private File billFile;
    private String billFileName;
    private String billFileContentType;
    
	@SuppressWarnings("unchecked")
	@Override
	protected String query(int start, int size) {
		// 检查参数
		
		// 根据当前用户的类型进行不同颗粒的查询
		Map<String, Object> paramMap = new HashMap<String, Object>();
		ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (manageUser.getUserLevel() == SysUser.UserLevel.school.getValue()) {// 学校账户
			paramMap.put("schoolNo", manageUser.getDataSchool().getSchoolNo());
			paramMap.put("billName", billName);
			paramMap.put("beginTime", DateUtil.getDate(beginTime, "yyyy-MM-dd"));
			paramMap.put("endTime", DateUtil.getDate(endTime, "yyyy-MM-dd"));
		} else {
			logger.warn("当前用户无权查看缴费账单！");
			setAlertMessage("当前用户无权查看缴费账单！");
			return "accessDenied";
		}
		rowCount = alipayEduTotalBillService.doJoinTransQueryAlipayEduTotalBillCount(paramMap);
		if (rowCount > 0) {
			Map<String, Object> resultMap = alipayEduTotalBillService.doJoinTransQueryAlipayEduTotalBill(paramMap, start, size);
			alipayEduTotalBillVOList = (List<AlipayEduTotalBillVO>) MapUtils.getObject(resultMap, "alipayEduTotalBillVOList");
		}
		return "totalBillList";
	}

	/**查询总账单列表*/
	public String list() {
		initPageData(PageAction.defaultLargePageSize);
		return goCurrent();
	}
	
	/**下载账单模版*/
	public String downloadBillTemplate() {
		return "getBillTemplate";
	}
	
	public InputStream getBillTemplate() {
		return null;
	}

	/**上传缴费账单（待定时发送）*/
	public String uploadBill() {
		ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (manageUser.getUserLevel() == SysUser.UserLevel.school.getValue()) {// 学校账户
			// 检查文件类型
			
			String billFileSaveDir = billFileRootDir.concat(File.separator).concat(manageUser.getDataSchool().getSchoolNo());// 保存路径
			String billFileSaveName = DateUtil.getDate(new Date(), "yyyyMMddHHmmssSSS").concat(billFileContentType); // 保存名称
			File billExcelFile = new File(billFileSaveDir, billFileSaveName);
			try {
				FileUtils.copyFile(billFile, billExcelFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 读取并封装EXCEL数据
			PoiExcelHelper helper = new PoiExcel2k7Helper();
			List<ArrayList<String>> dataList = helper.readExcel(billExcelFile, 0, "1-", 1);
			
			// 检查表头及表格合法性
			
			// 封装表单明细
			
			// 保存总账单、批量保存账单明细
			AlipayEduTotalBill totalBill = new AlipayEduTotalBill();
			totalBill.setBillName(billFileName);
			totalBill.setCloseTime(DateUtil.getDate(endTime, "yyyy-MM-dd"));
			totalBill.setExcelPath(billExcelFile.getAbsolutePath());
			
			// 上传成功返回列表
			return list();
		} else {
			logger.warn("当前用户无权查看缴费账单！");
			setAlertMessage("当前用户无权查看缴费账单！");
			return "accessDenied";
		}
	}

	public void setBillFileRootDir(String billFileRootDir) {
		this.billFileRootDir = billFileRootDir;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setBillName(String billName) {
		this.billName = billName;
	}

	public List<AlipayEduTotalBillVO> getAlipayEduTotalBillVOList() {
		return alipayEduTotalBillVOList;
	}

	public void setAlipayEduTotalBillService(AlipayEduTotalBillService alipayEduTotalBillService) {
		this.alipayEduTotalBillService = alipayEduTotalBillService;
	}

	public void setBillFile(File billFile) {
		this.billFile = billFile;
	}

	public void setBillFileName(String billFileName) {
		this.billFileName = billFileName;
	}

	public void setBillFileContentType(String billFileContentType) {
		this.billFileContentType = billFileContentType;
	}

}
