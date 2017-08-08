package com.zbsp.wepaysp.api.service.edu.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduTotalBillService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill;
import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill.OrderStatus;
import com.zbsp.wepaysp.po.partner.School;
import com.zbsp.wepaysp.vo.edu.AlipayEduTotalBillVO;

public class AlipayEduTotalBillServiceImpl
    extends BaseService
    implements AlipayEduTotalBillService {

    private AlipayEduBillService alipayEduBillService;
    
    /** 账单文件excel固定列头 */
    private String fixedExcelHeaders;;
    private String[] fixedExcelHeaderArr;
    private static final int fixHeaderCount = 4;// 从左向右，除了最后一个合计，固定的列头个数
    
    public void init() {
        if (StringUtils.isBlank(fixedExcelHeaders)) {
            throw new RuntimeException("缺少参数：fixedExcelHeaders");
        }
        fixedExcelHeaderArr = fixedExcelHeaders.split(",");
    }
	
    @SuppressWarnings("unchecked")
    @Override
    public List<AlipayEduTotalBillVO> doJoinTransQueryAlipayEduTotalBill(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<AlipayEduTotalBillVO> resultList = new ArrayList<AlipayEduTotalBillVO>();
        StringBuilder jpqlBuilder = new StringBuilder("from AlipayEduTotalBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);

        List<AlipayEduTotalBill> totalBillList = (List<AlipayEduTotalBill>) commonDAO.findObjectList(jpqlBuilder.toString(), jpqlMap, false, startIndex, maxResult);
        if (totalBillList != null && !totalBillList.isEmpty()) {
            for (AlipayEduTotalBill totalBill : totalBillList) {
                AlipayEduTotalBillVO billVO = new AlipayEduTotalBillVO();
                BeanCopierUtil.copyProperties(totalBill, billVO);
                resultList.add(billVO);
            }
        }
        return resultList;
    }

    @Override
    public int doJoinTransQueryAlipayEduTotalBillCount(Map<String, Object> paramMap) {
        StringBuilder jpqlBuilder = new StringBuilder("select count(a.iwoid) from AlipayEduTotalBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);
        return commonDAO.queryObjectCount(jpqlBuilder.toString(), jpqlMap, false);
    }

    /** 返回jpql查询参数 */
    private Map<String, Object> assembleQueryCondition(StringBuilder jpqlBuilder, Map<String, Object> paramMap) {
        String schoolNo = MapUtils.getString(paramMap, "schoolNo");
        String billName = MapUtils.getString(paramMap, "billName");
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");

        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(schoolNo)) {
            jpqlBuilder.append(" and a.schoolNo=:SCHOOLNO");
            jpqlMap.put("SCHOOLNO", schoolNo);
        }
        if (StringUtils.isNotBlank(billName)) {
            jpqlBuilder.append(" and a.billName like :BILLNAME");
            jpqlMap.put("BILLNAME", "%" + billName + "%");
        }
        if (beginTime != null) {
            jpqlBuilder.append(" and a.sendTime>=:BEGINTIME");
            jpqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null) {
            jpqlBuilder.append(" and a.sendTime<:ENDTIME");
            jpqlMap.put("ENDTIME", endTime);
        }
        return jpqlMap;
    }

    @Override
    public AlipayEduTotalBillVO doJoinTransQueryAlipayEduTotalBillByOid(String totalBillOid) {
        Validator.checkArgument(StringUtils.isBlank(totalBillOid), "totalBillOid 不能为空");
        AlipayEduTotalBillVO totalBillVO = new AlipayEduTotalBillVO();
        AlipayEduTotalBill totalBill = commonDAO.findObject(AlipayEduTotalBill.class, totalBillOid);
        if (totalBill != null) {
            BeanCopierUtil.copyProperties(totalBill, totalBillVO);
            String[] headers = totalBill.getChargeItemHeaders().split(",");
            totalBillVO.setChargeItemHeaders(headers);
        }
        return totalBillVO;
    }

    @Override
    public Map<String, Object> doTransSaveTotalBill(String schoolNo, String billName, String endTime, String excelPath,List<ArrayList<String>> dataList) {
    	Validator.checkArgument(StringUtils.isBlank(billName), "billName为空");
    	Validator.checkArgument(StringUtils.isBlank(excelPath), "excelPath为空");
    	Map<String, Object> resultMap = new HashMap<>();
    	String code = "success";
    	String msg = "缴费账单上传成功！";

        // 根据账单名称查重
        resultMap.put("SCHOOLNO", schoolNo);
        resultMap.put("BILLNAME", billName);
        AlipayEduTotalBill existBill = commonDAO.findObject("from AlipayEduTotalBill a where a.schoolNo=:SCHOOLNO and a.billName=:BILLNAME", resultMap, false);
        resultMap.clear();
    	
        if (existBill != null) {
            code = "billExisted";
            msg = "缴费账单（" + billName + ")已存在！";
        } else {
            // 查找学校
            resultMap.put("SCHOOLNO", schoolNo);
            School school = commonDAO.findObject("from School s where s.schoolNo=:SCHOOLNO", resultMap, false);
            resultMap.clear();
            if (school == null) {
                throw new NotExistsException("学校不存在，schoolNo=" + schoolNo);
            }
            
            // dataList 示例：[[年级/班级, 学生姓名, 家长手机号, 缴费账单名称, 保教费, 代办费, 校车非, 伙食费, 合计], [五年级3班, 张三, , 2017年秋季费用, 200, 300, 200, 300, 1000]]
            
            List<AlipayEduBill> billList = new ArrayList<>();// 待封装明细集合
            AlipayEduTotalBill totalBill = new AlipayEduTotalBill();
            totalBill.setIwoid(Generator.generateIwoid());
            totalBill.setSchoolNo(schoolNo);
            totalBill.setPartner1Oid(school.getPartner1Oid());
            totalBill.setPartner2Oid(school.getPartner2Oid());
            totalBill.setPartner3Oid(school.getPartner3Oid());
            totalBill.setPartnerEmployeeOid(school.getPartnerEmployee() == null ? null : school.getPartnerEmployee().getIwoid());
            totalBill.setPartnerOid(school.getPartner() == null ? null : school.getPartner().getIwoid());
            totalBill.setPartnerLevel(school.getPartnerLevel());
            totalBill.setOrderStatus(OrderStatus.INIT.name());// 账单新建
            //totalBill.setSendTime(new Date());// 定时任务发送
            totalBill.setBillName(billName);
            totalBill.setCloseTime(DateUtil.getDate(endTime, "yyyy-MM-dd"));// 账单过期（关闭）时间
            totalBill.setExcelPath(excelPath);
            
            checkExcelData(dataList, code, msg, school, totalBill, billList);// 检查数据合法及完整性
            
            if ("success".equals(code)) {
                // 检查通过，保存总账单、批量保存账单明细
                
                // TODO 建议增加列个数字段，便于控制详情展示列数
                commonDAO.save(totalBill, false);
                
                alipayEduBillService.doTransBatchSaveAlipayEduBills(billList);
            }
        }
    	
    	resultMap.put("code", code);
    	resultMap.put("msg", msg);
        return resultMap;
    }
    
    /**检查表头及明细行数据合法性并封装账单明细集合*/
    private void checkExcelData(List<ArrayList<String>> dataList, String code, String msg, School school, AlipayEduTotalBill totalBill, List<AlipayEduBill> billList) {
        if (dataList == null || dataList.size() <= 1) {
            code = "fileContentInvalid";
            msg = "缴费账单文件至少包含列头和一行数据！";
            return;
        }
        // 检查表头
        ArrayList<String> headers = dataList.get(0);
        if (headers.size() < fixedExcelHeaderArr.length) {
            code = "fileColumnHeaderError";
            msg = "缴费账单文件列头个数不能小于" + fixedExcelHeaderArr.length + "！";
            return;
        }
        boolean chanageItemExist = false;
        if (headers.size() - fixedExcelHeaderArr.length > 0)
            chanageItemExist = true;

        int columnIndex = 0;
        
        String chargeItemHeaders = "";
        for (String header : headers) {
            if (columnIndex < fixHeaderCount) {
                if (!fixedExcelHeaderArr[columnIndex].equalsIgnoreCase(headers.get(columnIndex))) {
                    code = "fileColumnHeaderError";
                    msg = "缴费账单文件第+ (columnIndex+1) +列名称错误，应该是：" + fixedExcelHeaderArr[columnIndex] + "！";
                    return;
                }
            } else {
                // 非固定字段，校验列头不能为空
                if (StringUtils.isBlank(header)) {
                    code = "fileColumnHeaderError";
                    msg = "缴费账单文件第+ (columnIndex+1) +列列头不为空！";
                    return;
                }
                if (columnIndex < headers.size() -2)
					chargeItemHeaders += header + ",";
				else if (columnIndex == headers.size() - 2)
					chargeItemHeaders += header;
            }
            columnIndex++;
        }
        totalBill.setChargeItemHeaders(chargeItemHeaders);

        if (!fixedExcelHeaderArr[fixedExcelHeaderArr.length - 1].equalsIgnoreCase(headers.get(headers.size() - 1))) {
            code = "fileColumnHeaderError";
            msg = "缴费账单文件最后一列名称错误，应该是：合计！";
            return;
        }

        dataList.remove(0);// 移除列头

        // 校验并封装明细
        int totalCount = dataList.size();
        int totalMoney = 0;
        int rowIndex = 1;
        for (ArrayList<String> row : dataList) {
            if (row.size() != headers.size()) {
                code = "fileRowDataError";
                msg = "缴费账单文件明细行列个数与列头列个数不一致！";
                return;
            }
            BigDecimal totalAmount = new BigDecimal(row.get(row.size() - 1));
            
            AlipayEduBill bill = new AlipayEduBill();
            // FIXME 固定字段暂硬编码
            // TODO 明细完整性校验
            bill.setClassIn(row.get(0).trim());
            bill.setChildName(row.get(1).trim());
            bill.setUserMobile(row.get(2).trim());
            bill.setChargeBillTitle(row.get(3).trim());

            bill.setIwoid(Generator.generateIwoid());
            bill.setAlipayEduTotalBillOid(totalBill.getIwoid());
            bill.setSchoolNo(totalBill.getSchoolNo());
            bill.setPartner1Oid(totalBill.getPartner1Oid());
            bill.setPartner2Oid(totalBill.getPartner2Oid());
            bill.setPartner3Oid(totalBill.getPartner3Oid());
            bill.setPartnerEmployeeOid(totalBill.getPartnerEmployeeOid());
            bill.setPartnerOid(totalBill.getPartnerOid());
            bill.setPartnerLevel(school.getPartnerLevel());
            bill.setOrderStatus(OrderStatus.INIT.name());
            bill.setLineNum(rowIndex);
            bill.setGmtEnd(DateUtil.getDate(totalBill.getCloseTime(), "yyyy-MM-dd HH:mm:ss"));
            bill.setEndEnable(totalBill.getCloseTime() == null ? "N" : "Y");            
            bill.setAmount(totalAmount.multiply(SysEnvKey.TIMES_100).intValue());
            bill.setSchoolPid(school.getSchoolPid());
            bill.setIsvPartnerId(school.getIsvPid());
            //bill.setAppId();
            //bill.setAppAuthToken();
            
            if (chanageItemExist) {
                BigDecimal totalAmountTemp = new BigDecimal(0);
                Map<String, Object> changeIemMap = new LinkedHashMap<>(); // 确保各项明细有序与列头一致对应
                for (int index = fixHeaderCount; index < row.size() - 1; index++) {
                    if (!NumberUtils.isCreatable(row.get(index).trim())) {
                        code = "fileRowDataError";
                        msg = "缴费账单文件明细行第" + (rowIndex) + "行" + headers.get(index) + "不是数字！";
                        return;
                    }
                    BigDecimal money = new BigDecimal(row.get(index));
                    changeIemMap.put(headers.get(index), money.multiply(SysEnvKey.TIMES_100));
                    totalAmountTemp = totalAmountTemp.add(money);
                }
                bill.setChargeItem(JSONUtil.toJSONString(changeIemMap, false));

                // 验证合计
                if (!NumberUtils.isCreatable(row.get(row.size() - 1))) {
                    code = "fileRowDataError";
                    msg = "缴费账单文件明细行第" + (rowIndex) + "行" + headers.get(headers.size() - 1) + "不是数字！";
                    return;
                }

                if (totalAmount.compareTo(totalAmountTemp) != 0) {
                    code = "fileRowDataError";
                    msg = "缴费账单文件明细行第" + (rowIndex) + "行各项费用总和与合计项不等！";
                    return;
                }
            }
            billList.add(bill);
            totalMoney += totalAmount.multiply(SysEnvKey.TIMES_100).intValue();
            rowIndex++;
        }
        totalBill.setTotalCount(totalCount);
        totalBill.setTotalMoney(totalMoney);
    }
	
    public void setAlipayEduBillService(AlipayEduBillService alipayEduBillService) {
        this.alipayEduBillService = alipayEduBillService;
    }

    public void setFixedExcelHeaders(String fixedExcelHeaders) {
		this.fixedExcelHeaders = fixedExcelHeaders;
	}

}
