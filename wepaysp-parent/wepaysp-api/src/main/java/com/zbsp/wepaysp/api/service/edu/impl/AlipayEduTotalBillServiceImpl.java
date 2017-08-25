package com.zbsp.wepaysp.api.service.edu.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.zbsp.alipay.trade.model.ChargeItems;
import com.zbsp.alipay.trade.utils.Utils;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduTotalBillService;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.TimeUtil;
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
        logger.info("支付宝教育缴费账单excel模板列头：{}", fixedExcelHeaders);
    }
	
    @SuppressWarnings("unchecked")
    @Override
    public List<AlipayEduTotalBillVO> doJoinTransQueryAlipayEduTotalBill(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<AlipayEduTotalBillVO> resultList = new ArrayList<AlipayEduTotalBillVO>();
        StringBuilder jpqlBuilder = new StringBuilder("from AlipayEduTotalBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);

        jpqlBuilder.append(" order by a.createTime desc");
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
            jpqlMap.put("BEGINTIME", TimeUtil.getDayStart(beginTime));
        }
        if (endTime != null) {
            jpqlBuilder.append(" and a.sendTime<=:ENDTIME");
            jpqlMap.put("ENDTIME", TimeUtil.getDayEnd(endTime));
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
            if (StringUtils.isNotBlank(totalBill.getChargeItemHeaders())) {
            	totalBillVO.setChargeItemHeaders(totalBill.getChargeItemHeaders().split(","));
            }
        }
        return totalBillVO;
    }

    @Override
    public Map<String, Object> doTransSaveTotalBill(String schoolNo, String billName, String endTime, String excelPath,List<ArrayList<String>> dataList) {
    	Validator.checkArgument(StringUtils.isBlank(billName), "billName为空");
        Validator.checkArgument(StringUtils.isBlank(excelPath), "excelPath为空");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", "success");
        resultMap.put("msg", "缴费账单上传成功！");

        Map<String, Object> jpqlMap = new HashMap<>();
        // 根据账单名称查重
        jpqlMap.put("SCHOOLNO", schoolNo);
        jpqlMap.put("BILLNAME", billName);
        AlipayEduTotalBill existBill = commonDAO.findObject("from AlipayEduTotalBill a where a.schoolNo=:SCHOOLNO and a.billName=:BILLNAME", jpqlMap, false);
        jpqlMap.clear();

        if (existBill != null) {
            resultMap.put("code", "billExisted");
            resultMap.put("msg", "缴费账单（" + billName + ")已存在！");
            logger.warn("billExisted：缴费账单（" + billName + ")已存在！");
        } else {
            // 查找学校
            jpqlMap.put("SCHOOLNO", schoolNo);
            School school = commonDAO.findObject("from School s where s.schoolNo=:SCHOOLNO", jpqlMap, false);
            jpqlMap.clear();
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
            // totalBill.setSendTime(new Date());// 定时任务发送
            totalBill.setBillName(billName);
            totalBill.setCloseTime(DateUtil.getDate(endTime, "yyyy-MM-dd"));// 账单过期（关闭）时间
            totalBill.setExcelPath(excelPath);
            totalBill.setReceiptCount(0);
            totalBill.setReceiptMoney(0);

            // 检查数据合法及完整性，并设置应缴费人数和金额
            checkExcelData(resultMap, dataList, school, totalBill, billList);
            if ("success".equals(MapUtils.getString(resultMap, "code"))) { // 检查通过
                logger.info("检查通过，开始保存缴费账单和账单明细");
                // 保存总账单
                commonDAO.save(totalBill, false);

                // 批量保存账单明细
                alipayEduBillService.doTransBatchSaveAlipayEduBills(billList);
            }
        }
        return resultMap;
    }
    
    /**检查表头及明细行数据合法性并封装账单明细集合*/
    private void checkExcelData(Map<String, Object> resultMap, List<ArrayList<String>> dataList, School school, AlipayEduTotalBill totalBill, List<AlipayEduBill> billList) {
        logger.info("检查excel明细行数据 - 开始");
        if (dataList == null || dataList.size() <= 1) {
            logger.warn("fileContentInvalid：缴费账单文件至少包含列头和一行数据！");
            resultMap.put("code", "fileContentInvalid");
            resultMap.put("msg", "缴费账单文件至少包含列头和一行数据！");
            return;
        }
        // 检查表头
        ArrayList<String> headers = dataList.get(0);
        if (headers.size() < fixedExcelHeaderArr.length) {
            logger.warn("fileColumnHeaderError：缴费账单文件列头个数缺少！");
            resultMap.put("code", "fileColumnHeaderError");
            resultMap.put("msg", "缴费账单文件列头个数不能小于" + fixedExcelHeaderArr.length + "！");
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
                    logger.warn("缴费账单文件第"+ (columnIndex+1) +"列名称{}错误，应该是：" + fixedExcelHeaderArr[columnIndex] + "！", headers.get(columnIndex));
                    resultMap.put("code", "fileColumnHeaderError");
                    resultMap.put("msg", "缴费账单文件第"+ (columnIndex+1) +"列名称错误，应该是：" + fixedExcelHeaderArr[columnIndex] + "！");
                    return;
                }
            } else {
                // 非固定字段，校验列头不能为空
                if (StringUtils.isBlank(header)) {
                    logger.warn("fileColumnHeaderError：缴费账单文件列头为空！");
                    resultMap.put("code", "fileColumnHeaderError");
                    resultMap.put("msg", "缴费账单文件第" + (columnIndex + 1) + "列列头不为空！");
                    return;
                }
                if (columnIndex < headers.size() - 2)
                    chargeItemHeaders += header + ",";
                else if (columnIndex == headers.size() - 2)
                    chargeItemHeaders += header;
            }
            columnIndex++;
        }

        // 设置明细项列头，页面动态展示td需要
        totalBill.setChargeItemHeaders(chargeItemHeaders);

        if (!fixedExcelHeaderArr[fixedExcelHeaderArr.length - 1].equalsIgnoreCase(headers.get(headers.size() - 1))) {
            logger.warn("fileColumnHeaderError：缴费账单文件最后一列不是合计！");
            resultMap.put("code", "fileColumnHeaderError");
            resultMap.put("msg", "缴费账单文件最后一列名称错误，应该是：合计！");
            return;
        }

        dataList.remove(0);// 移除列头

        // 校验并封装明细
        int totalCount = dataList.size();
        int totalMoney = 0;
        int rowIndex = 1;

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        for (ArrayList<String> row : dataList) {
            if (row.size() != headers.size()) {
                logger.warn("fileRowDataError：缴费账单文件明细行列个数与列头列个数不一致！");
                resultMap.put("code", "fileRowDataError");
                resultMap.put("msg", "缴费账单文件明细行列个数与列头列个数不一致！");
                return;
            }
            BigDecimal totalAmount = new BigDecimal(row.get(row.size() - 1));

            AlipayEduBill bill = new AlipayEduBill();
            // FIXME 固定字段暂硬编码
            bill.setClassIn(StringUtils.trimToEmpty(row.get(0)));
            bill.setChildName(StringUtils.trimToEmpty(row.get(1)));
            bill.setUserMobile(StringUtils.trimToEmpty(row.get(2)));
            bill.setChargeBillTitle(StringUtils.isBlank(StringUtils.trimToEmpty(row.get(3))) ? totalBill.getBillName() : StringUtils.trimToEmpty(row.get(3)));

            if (StringUtils.isBlank(bill.getClassIn()) || StringUtils.isBlank(bill.getChildName()) || StringUtils.isBlank(bill.getUserMobile())) {
                logger.warn("fileRowDataInvalid：缴费账单文件明细行单元格有空！");
                resultMap.put("code", "fileRowDataInvalid");
                resultMap.put("msg", "缴费账单文件明细行单元格不能为空！");
                return;
            }

            bill.setIwoid(Generator.generateIwoid());
            bill.setAlipayEduTotalBillOid(totalBill.getIwoid());

            // 获取 支付订单ID下一个序列值
            String sql = "select nextval('" + SysSequenceCode.PAY_ORDER + "') as sequence_value";
            Object seqObj = commonDAO.findObject(sql, sqlMap, true);
            if (seqObj == null) {
                throw new IllegalArgumentException("支付订单Id对应序列记录不存在");
            }
            bill.setOutTradeNo(Generator.generateSequenceYYYYMMddNum((Integer) seqObj, SysSequenceMultiple.PAY_ORDER));

            bill.setPartner1Oid(totalBill.getPartner1Oid());
            bill.setPartner2Oid(totalBill.getPartner2Oid());
            bill.setPartner3Oid(totalBill.getPartner3Oid());
            bill.setPartnerEmployeeOid(totalBill.getPartnerEmployeeOid());
            bill.setPartnerOid(totalBill.getPartnerOid());
            bill.setPartnerLevel(school.getPartnerLevel());
            bill.setOrderStatus(OrderStatus.INIT.name());
            bill.setLineNum(rowIndex);
            bill.setGmtEnd(Utils.toDate(totalBill.getCloseTime()));
            bill.setEndEnable(totalBill.getCloseTime() == null ? "N" : "Y");
            bill.setAmount(totalAmount.multiply(SysEnvKey.BIG_100).intValue());
            bill.setSchoolPid(school.getSchoolPid());
            bill.setSchoolNo(school.getSchoolNo());
            bill.setIsvPartnerId(school.getIsvPid());
            bill.setAppId(SysConfig.appId4Edu);
            // bill.setAppAuthToken();

            if (chanageItemExist) {
                BigDecimal totalAmountTemp = new BigDecimal(0);
                List<ChargeItems> chargeItems = new ArrayList<>();

                for (int index = fixHeaderCount; index < row.size() - 1; index++) {
                    if (!NumberUtils.isCreatable(row.get(index).trim())) {
                        logger.warn("fileRowDataError：缴费账单文件明细行单元格有不是数字！");
                        resultMap.put("code", "fileRowDataError");
                        resultMap.put("msg", "缴费账单文件明细行第" + (rowIndex) + "行" + headers.get(index) + "不是数字！");
                        return;
                    }
                    BigDecimal money = new BigDecimal(row.get(index));
                    totalAmountTemp = totalAmountTemp.add(money);

                    ChargeItems item = new ChargeItems(headers.get(index), money.toString());// 明细项金额为元，string类型保存，便于发送账单时直接反序列化
                    chargeItems.add(item);
                }
                bill.setChargeItem(JSONUtil.toJSONString(chargeItems, false));

                // 验证合计
                if (!NumberUtils.isCreatable(row.get(row.size() - 1))) {
                    logger.warn("fileRowDataError：缴费账单文件明细行单元格合计有不是数字！");
                    resultMap.put("code", "fileRowDataError");
                    resultMap.put("msg", "缴费账单文件明细行第" + (rowIndex) + "行" + headers.get(headers.size() - 1) + "不是数字！");
                    return;
                }

                if (totalAmount.compareTo(totalAmountTemp) != 0) {
                    logger.warn("fileRowDataError：缴费账单文件明细行各项费用和合计不等！");
                    resultMap.put("code", "fileRowDataError");
                    resultMap.put("msg", "缴费账单文件明细行第" + (rowIndex) + "行各项费用总和与合计项不等！");
                    return;
                }
            }
            billList.add(bill);
            totalMoney += totalAmount.multiply(SysEnvKey.BIG_100).intValue();// 合计金额：单位为分
            rowIndex++;
        }
        totalBill.setTotalCount(totalCount);
        totalBill.setTotalMoney(totalMoney);
        logger.info("检查excel明细行数据 - 通过");
    }

    @Override
    public void doTransTotalBillSent(Set<String> totalBillOids, Date time) {
        List<AlipayEduTotalBill> totalBills = new ArrayList<>();
        
        for (String totaBillOid : totalBillOids) {
            AlipayEduTotalBill totalBill = commonDAO.findObject(AlipayEduTotalBill.class, totaBillOid);
            totalBill.setSendTime(time == null ? new Date() : time);
            totalBill.setOrderStatus(OrderStatus.SEND_SUCCESS.name());
            totalBills.add(totalBill);
            logger.info("更新缴费账单（{}）为发送成功，发送时间：{}", totalBill.getBillName(), DateUtil.getDate(totalBill.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        commonDAO.updateList(totalBills);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AlipayEduTotalBill> doJoinTransQueryTotalBillOfWaitingSend(Integer validMins) {
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AlipayEduTotalBill a where 1=1 and a.orderStatus=:STATUS ";
        jpqlMap.put("STATUS", OrderStatus.INIT.name());

        if (validMins != null) {
            Date minCreateTime = TimeUtil.plusSeconds(new Date(), validMins * -60);
            jpql += " and a.createTime>=:MINCREATETIME";
            jpqlMap.put("MINCREATETIME", minCreateTime);
        }
        return (List<AlipayEduTotalBill>) commonDAO.findObjectList(jpql, jpqlMap, false);
    }

    @Override
    public void doTransUpdateTotalBillList(List<AlipayEduTotalBill> sendSuceessList) {
        Validator.checkArgument(sendSuceessList == null || sendSuceessList.isEmpty(), "sendSuceessList不能为空");
        commonDAO.updateList(sendSuceessList);
    }
	
    public void setAlipayEduBillService(AlipayEduBillService alipayEduBillService) {
        this.alipayEduBillService = alipayEduBillService;
    }

    public void setFixedExcelHeaders(String fixedExcelHeaders) {
		this.fixedExcelHeaders = fixedExcelHeaders;
	}

}
