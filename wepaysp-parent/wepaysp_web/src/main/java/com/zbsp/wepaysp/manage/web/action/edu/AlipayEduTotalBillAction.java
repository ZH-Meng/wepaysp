package com.zbsp.wepaysp.manage.web.action.edu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.api.service.edu.AlipayEduTotalBillService;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.util.PoiExcel2k3Helper;
import com.zbsp.wepaysp.manage.web.util.PoiExcel2k7Helper;
import com.zbsp.wepaysp.manage.web.util.PoiExcelHelper;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.vo.edu.AlipayEduTotalBillVO;

/** 教育缴费总账单Action */
public class AlipayEduTotalBillAction
    extends PageAction {

    private static final long serialVersionUID = -8734218055007641937L;
    private static final String LOG_PREFIX  ="教育缴费总账单 - [{}] - [{}]";
    /** 账单文件存放根目录 */
    private String billFileRootDir;
    /** 账单模板文件绝对路径 */
    private String billTemplateAbsolutePath;
    private String billTemplateName;

    private String beginTime;
    private String endTime;
    private String billName;
    private List<AlipayEduTotalBillVO> alipayEduTotalBillVOList;
    private AlipayEduTotalBillService alipayEduTotalBillService;

    /** 账单上传参数 */
    private File billFile;
    private String billFileFileName;
    private String billFileContentType;
    private Map<String, Object> dataMap;
    private String saveName;
    

    public void init() {
        if (StringUtils.isBlank(billFileRootDir) || StringUtils.isBlank(billTemplateAbsolutePath)) {
            throw new RuntimeException("缺少参数");
        }
        File template = new File(billTemplateAbsolutePath);
        if (!template.isFile() || !template.exists())
            throw new RuntimeException("账单模板文件不存在");
        File billDir = new File(billFileRootDir);
        if (!billDir.exists()) {
            billDir.mkdirs();
        }
    }

    @Override
    protected String query(int start, int size) {
        // 检查参数
        try {
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
                alipayEduTotalBillVOList = alipayEduTotalBillService.doJoinTransQueryAlipayEduTotalBill(paramMap, start, size);
            }
        } catch (Exception e) {
            logger.error("查看缴费账单错误：{}", e.getMessage(), e);
        }
        return "totalBillList";
    }

    /** 查询总账单列表 */
    public String list() {
        initPageData(PageAction.defaultSmallPageSize);
        return goCurrent();
    }

    /** 下载账单模版 */
    public String downloadBillTemplate() {
        return "getBillTemplate";
    }

    public InputStream getBillTemplate()
        throws FileNotFoundException, UnsupportedEncodingException {
        //billTemplateName = new String("缴费账单模版.xls".getBytes("GBK"), "ISO8859-1");
        billTemplateName = new String("ChargeTemplate.xls");
        return new FileInputStream(new File(billTemplateAbsolutePath));
    }

    /** 上传缴费账单（待定时发送） */
    public String uploadExcel() {
        logger.info(LOG_PREFIX, "上传", "开始");
        logger.info("账单上传文件名：{}", billFileFileName);
        String code = "success";
        String msg = "缴费账单上传成功！";
        dataMap = new HashMap<>();
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (manageUser.getUserLevel() == SysUser.UserLevel.school.getValue()) {// 学校账户
                if (billFile == null) {
                    code = "fileIsNull";
                    msg = "请选择缴费账单文件并上传！";
                } else {
                    // 检查文件类型
                    String excelSuffix = "";
                    if ("application/vnd.ms-excel".equalsIgnoreCase(billFileContentType)) {
                        excelSuffix = ".xls";
                    } else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(billFileContentType)) {
                        excelSuffix = ".xlsx";
                    } else {
                        code = "fileTypeInvalid";
                        msg = "账单文件类型无效，只支持后缀未.xls或.xlsx的excel！";
                        logger.warn(LOG_PREFIX + "原因：{}", "上传", "失败", "fileTypeInvalid");
                    }

                    if (StringUtils.isNotBlank(excelSuffix)) {
                        // 保存上传的excel账单文件
                        String billFileSaveDir = billFileRootDir.concat(File.separator).concat(manageUser.getDataSchool().getSchoolNo());// 保存路径
                        String billFileSaveName = DateUtil.getDate(new Date(), "yyyyMMddHHmmssSSS").concat(excelSuffix); // 保存名称
                        File billExcelFile = new File(billFileSaveDir, billFileSaveName);
                        try {
                            FileUtils.copyFile(billFile, billExcelFile);
                        } catch (IOException e) {
                            code = "fileUploadFail";
                            msg = "缴费账单上传失败！";
                            logger.error(LOG_PREFIX + "\n{}", "上传", "失败", e.getMessage(), e);
                        }

                        dataMap.put("displayName", billFileFileName);
                        dataMap.put("saveName", billFileSaveName);
                    }
                }
            } else {
                code = "accessDenied";
                msg = "当前用户无权上传缴费账单！";
                logger.warn(LOG_PREFIX + "原因：{}", "上传", "失败", "accessDenied");
            }
        } catch (Exception e) {
            logger.error(LOG_PREFIX + "原因：{}", "上传", "失败", e.getMessage(), e);
            code = "error";
            msg = "上传缴费账单失败！";
        }
        // 返回上传结果json串
        dataMap.put("code", code);
        dataMap.put("msg", msg);
        return "uploadResult";
    }
    
    public String newBill() {
		String code = "fail";
		String msg = "缴费账单上传失败！";
		dataMap = new HashMap<>();
        logger.info("收费名称：{}, 账单文件保存名：{}", billName, saveName);
        try {
            PoiExcelHelper excelHelper = null;
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (manageUser.getUserLevel() == SysUser.UserLevel.school.getValue()) {// 学校账户
                if (StringUtils.isBlank(saveName)) {
                } else if (saveName.endsWith(".xls")) {
                    excelHelper = new PoiExcel2k3Helper();
                } else if (saveName.endsWith(".xlsx")) {
                    excelHelper = new PoiExcel2k7Helper();
                } else {
                }
                if (excelHelper != null) {
                    String billFileSaveDir = billFileRootDir.concat(File.separator).concat(manageUser.getDataSchool().getSchoolNo());// 保存路径
                    File billExcelFile = new File(billFileSaveDir, saveName);
                    // 读取并封装EXCEL数据
                    List<ArrayList<String>> dataList = excelHelper.readExcel(billExcelFile, 0, "1-", 1);

                    // 保存账单
                    Map<String, Object> resultMap = alipayEduTotalBillService.doTransSaveTotalBill(manageUser.getDataSchool().getSchoolNo(), billName, endTime, billExcelFile.getAbsolutePath(),
                        dataList);
                    code = MapUtils.getString(resultMap, "code");
                    msg = MapUtils.getString(resultMap, "msg");
                    logger.info(LOG_PREFIX + "- code : {}, msg : {}", "newBill", "结果", code, msg);
                }
            } else {
                code = "accessDenied";
                msg = "当前用户无权上传缴费账单！";
                logger.warn(LOG_PREFIX + "原因：{}", "newBill", "失败", "accessDenied");
            }
        } catch (Exception e) {
            logger.error(LOG_PREFIX + "原因：{}", "newBill", "失败", e.getMessage(), e);
            code = "error";
            msg = "上传缴费账单失败！";
        }
		// 返回上传结果json串
		dataMap.put("code", code);
		dataMap.put("msg", msg);
		return "uploadResult";
    }

    public void setBillFileRootDir(String billFileRootDir) {
        this.billFileRootDir = billFileRootDir;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBillName() {
        return billName;
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

    public void setBillFileFileName(String billFileFileName) {
        this.billFileFileName = billFileFileName;
    }

    public void setBillFileContentType(String billFileContentType) {
        this.billFileContentType = billFileContentType;
    }

    public void setBillTemplateAbsolutePath(String billTemplateAbsolutePath) {
        this.billTemplateAbsolutePath = billTemplateAbsolutePath;
    }

    public String getBillTemplateName() {
        return billTemplateName;
    }

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public String getSaveName() {
		return saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

}
