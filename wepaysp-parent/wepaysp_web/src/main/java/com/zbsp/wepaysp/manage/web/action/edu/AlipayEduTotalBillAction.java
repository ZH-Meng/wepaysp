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
import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.vo.edu.AlipayEduTotalBillVO;

/** 教育缴费总账单Action */
public class AlipayEduTotalBillAction
    extends PageAction {

    private static final long serialVersionUID = -8734218055007641937L;
    /** 账单文件存放根目录 */
    private String billFileRootDir;
    /** 账单模板文件绝对路径 */
    private String billTemplateAbsolutePath;
    private String billTemplateName;
    /** 账单文件excel固定列头 */
    private String fixedExcelHeaders;
    private String[] fixedExcelHeaderArr;

    private String beginTime;
    private String endTime;
    private String billName;
    private List<AlipayEduTotalBillVO> alipayEduTotalBillVOList;
    private AlipayEduTotalBillService alipayEduTotalBillService;

    /** 账单上传参数 */
    private File billFile;
    private String billFileFileName;
    private String billFileContentType;

    public void init() {
        if (StringUtils.isBlank(billFileRootDir) || StringUtils.isBlank(billTemplateAbsolutePath) || StringUtils.isBlank(fixedExcelHeaders)) {
            throw new RuntimeException("缺少参数");
        }
        File template = new File(billTemplateAbsolutePath);
        if (!template.isFile() || !template.exists())
            throw new RuntimeException("账单模板文件不存在");
        File billDir = new File(billFileRootDir);
        if (!billDir.exists()) {
            billDir.mkdirs();
        }
        fixedExcelHeaderArr = fixedExcelHeaders.split(",");
    }

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
            alipayEduTotalBillVOList = alipayEduTotalBillService.doJoinTransQueryAlipayEduTotalBill(paramMap, start, size);
        }
        return "totalBillList";
    }

    /** 查询总账单列表 */
    public String list() {
        initPageData(PageAction.defaultLargePageSize);
        return goCurrent();
    }

    /** 下载账单模版 */
    public String downloadBillTemplate() {
        return "getBillTemplate";
    }

    public InputStream getBillTemplate()
        throws FileNotFoundException, UnsupportedEncodingException {
        billTemplateName = new String("缴费账单模版.xls".getBytes("GBK"), "ISO8859-1");
        return new FileInputStream(new File(billTemplateAbsolutePath));
    }

    /** 上传缴费账单（待定时发送） */
    public String uploadBill() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (manageUser.getUserLevel() == SysUser.UserLevel.school.getValue()) {// 学校账户
            // 检查文件类型
            PoiExcelHelper excelHelper = null;
            String excelPrefix = "";
            if ("application/vnd.ms-excel".equalsIgnoreCase(billFileContentType)) {
                excelPrefix = ".xls";
                excelHelper = new PoiExcel2k3Helper();
            } else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(billFileContentType)) {
                excelPrefix = ".xlsx";
                excelHelper = new PoiExcel2k7Helper();
            } else {
                // TODO
                return ERROR;
            }

            String billFileSaveDir = billFileRootDir.concat(File.separator).concat(manageUser.getDataSchool().getSchoolNo());// 保存路径
            String billFileSaveName = DateUtil.getDate(new Date(), "yyyyMMddHHmmssSSS").concat(excelPrefix); // 保存名称
            File billExcelFile = new File(billFileSaveDir, billFileSaveName);
            try {
                FileUtils.copyFile(billFile, billExcelFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 读取并封装EXCEL数据
            List<ArrayList<String>> dataList = excelHelper.readExcel(billExcelFile, 0, "1-", 1);
            
            alipayEduTotalBillService.doTransSaveTotalBill(billName, endTime, billExcelFile.getAbsolutePath(), dataList);

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

    public void setFixedExcelHeaders(String fixedExcelHeaders) {
        this.fixedExcelHeaders = fixedExcelHeaders;
    }

    public void setBillTemplateAbsolutePath(String billTemplateAbsolutePath) {
        this.billTemplateAbsolutePath = billTemplateAbsolutePath;
    }

    public String getBillTemplateName() {
        return billTemplateName;
    }

}
