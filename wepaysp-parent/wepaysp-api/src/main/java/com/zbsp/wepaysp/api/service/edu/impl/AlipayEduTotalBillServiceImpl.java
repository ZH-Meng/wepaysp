package com.zbsp.wepaysp.api.service.edu.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduTotalBillService;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill;
import com.zbsp.wepaysp.vo.edu.AlipayEduTotalBillVO;

public class AlipayEduTotalBillServiceImpl
    extends BaseService
    implements AlipayEduTotalBillService {

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
            jpqlBuilder.append(" and a.billName=:BILLNAME");
            jpqlMap.put("BILLNAME", billName);
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
    public Map<String, Object> doTransSaveTotalBill(String billName, String endTime, String excelPath,List<ArrayList<String>> dataList) {
    	Validator.checkArgument(StringUtils.isBlank(billName), "billName为空");
    	Validator.checkArgument(StringUtils.isBlank(excelPath), "excelPath为空");
    	Map<String, Object> resultMap = new HashMap<>();
    	String code = "success";
    	String msg = "缴费账单上传成功！";
    	
    	List<AlipayEduBill> billList = new ArrayList<>();
    	
    	// [[年级/班级, 学生姓名, 家长手机号, 缴费账单名称, 保教费, 代办费, 校车非, 伙食费, 合计], [五年级3班, 张三, , 2017年秋季费用, 200, 300, 200, 300, 1000]]
    	checkExcelData(dataList, code, msg, billList);
    	if ("success".equals(code)) {
    		// 保存总账单、批量保存账单明细
    		
    		// 建议增加列个数字段
    		AlipayEduTotalBill totalBill = new AlipayEduTotalBill();
    		totalBill.setBillName(billName);
    		totalBill.setCloseTime(DateUtil.getDate(endTime, "yyyy-MM-dd"));
    		totalBill.setExcelPath(excelPath);
    		//TODO
    	}
    	resultMap.put("code", code);
    	resultMap.put("msg", msg);
        return resultMap;
    }
    
    /**检查表头及明细行数据合法性并封装账单明细集合*/
    private void checkExcelData(List<ArrayList<String>> dataList, String code, String msg, List<AlipayEduBill> billList) {
    	if (dataList == null || dataList.size() == 1) {
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
		if (headers.size() - fixedExcelHeaderArr.length> 0) 
			chanageItemExist = true;
		
		int columnIndex = 0;
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
			}
			columnIndex++;
		}
		
		if (!fixedExcelHeaderArr[fixedExcelHeaderArr.length-1].equalsIgnoreCase(headers.get(headers.size()-1))) {
			code = "fileColumnHeaderError";
			msg = "缴费账单文件最后一列名称错误，应该是：合计！";
			return;
		}
		
		dataList.remove(0);
		
		// 校验并封装明细
		int rowIndex = 0;
		for (ArrayList<String> row : dataList) {
			if (row.size() != headers.size()) {
				code = "fileRowDataError";
				msg = "缴费账单文件明细行列个数与列头列个数不一致！";
				return;
			}
			AlipayEduBill bill = new AlipayEduBill();
			// FIXME 固定字段暂硬编码
			bill.setClassIn(row.get(0).trim());
			bill.setChildName(row.get(1).trim());
			bill.setUserMobile(row.get(2).trim());
			bill.setChargeBillTitle(row.get(3).trim());
			bill.setIwoid(Generator.generateIwoid());
			// TODO
			if (chanageItemExist) {
				BigDecimal totalAmountTemp = new BigDecimal(0);
				Map<String, Object> changeIemMap = new HashMap<>();
				for (int index = fixHeaderCount -2; index < row.size() - 2; index ++) {
					if (!NumberUtils.isCreatable(row.get(index))) {
						code = "fileRowDataError";
						msg = "缴费账单文件明细行第" + (rowIndex + 1) + "行" + headers.get(index) + "不是数字！";
						return;
					}
					BigDecimal money = new BigDecimal(row.get(index));
					changeIemMap.put(headers.get(index), money);
					totalAmountTemp.add(money);
				}
				bill.setChargeItem(JSONUtil.toJSONString(changeIemMap, false));
				
				// 验证合计
				if (!NumberUtils.isCreatable(row.get(row.size() -1))) {
					code = "fileRowDataError";
					msg = "缴费账单文件明细行第" + (rowIndex + 1) + "行" + headers.get(headers.size() - 1) + "不是数字！";
					return;
				}
				
				BigDecimal totalAmount = new BigDecimal(row.get(row.size() -1)); 
				if (totalAmount.compareTo(totalAmountTemp) != 0) {
					code = "fileRowDataError";
					msg = "缴费账单文件明细行第" + (rowIndex + 1) + "行各项费用总和与合计项不等！";
					return;
				}
			}
			billList.add(bill);
			rowIndex++;
		}
		
    }

	public void setFixedExcelHeaders(String fixedExcelHeaders) {
		this.fixedExcelHeaders = fixedExcelHeaders;
	}

}
