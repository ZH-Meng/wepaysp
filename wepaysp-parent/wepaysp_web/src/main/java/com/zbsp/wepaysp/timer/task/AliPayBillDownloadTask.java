package com.zbsp.wepaysp.timer.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.alipay.AlipayAppAuthDetailsService;
import com.zbsp.wepaysp.api.service.pay.AlipayBillDetailsService;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.AliPayEnums;
import com.zbsp.wepaysp.common.constant.AliPayEnums.BillType;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnums.RefundType;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.util.CHZipUtils;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.alipay.AlipayAppAuthDetails;
import com.zbsp.wepaysp.po.pay.AlipayBillDetails;
import com.zbsp.wepaysp.po.pay.AlipayBillDetails.SourceId;
import com.zbsp.wepaysp.po.pay.AlipayRefundDetails;

/**
 * 支付宝交易账单下载任务
 * 
 * @author 孟郑宏
 */
@Component
public class AliPayBillDownloadTask
    extends TimerBasicTask {

    private static String LOG_PREFIX = "[定时任务] - [支付宝交易账单下载] - ";
    private File zipFileDir;
    private File csvFileDir;
    
    @Value("${alipayBillZipFilePath}")
    private String billZipFilePath;
    @Value("${alipayBillCsvFilePath}")
    private String billCsvFilePath;

    @Value("${alipayBillDate}")
    private String downloadBillDate;
    @Value("${downloadBillDays}")
    private Integer downloadBillDays = 1;
    @Value("${alipayBillDealerId}")
    private String dealerId;

    private final String billFileNamePrefix = "alipay_trade_bill_";

    private static final String FILE_POSTFIX_ZIP = ".csv.zip";
    
    @Autowired
    private AlipayAppAuthDetailsService alipayAppAuthDetailsService;
    @Autowired
    private AlipayBillDetailsService alipayBillDetailsService;
    
    @PostConstruct
    public void init() {
        zipFileDir = new  File(billZipFilePath.concat(File.separator));
        csvFileDir = new  File(billCsvFilePath.concat(File.separator));
        if (!zipFileDir.exists() || !zipFileDir.isDirectory()) {
            zipFileDir.mkdir();
        }
        if (!csvFileDir.exists() || !csvFileDir.isDirectory()) {
            csvFileDir.mkdir();
        }
    }

    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        Validator.checkArgument(downloadBillDays == null || downloadBillDays < 1, "downloadBillDays最小为1");
        Date billDate = null;
        if (StringUtils.isBlank(downloadBillDate)) {
            billDate = TimeUtil.getBeforeDayStart();
        } else {
            billDate = DateUtil.getDate(downloadBillDate, "yyyy-MM-dd");
        }
        
        List<AlipayAppAuthDetails> appAuthList = alipayAppAuthDetailsService.doJoinTransQueryValidAppAuthDetails(SysConfig.appId4Face2FacePay, dealerId);
        
        if (appAuthList == null || appAuthList.isEmpty()) {
            logger.warn(StringHelper.combinedString(LOG_PREFIX, "当前应用没有被商户{}授权"), dealerId);
        } else {
            if (StringUtils.isNotBlank(dealerId)) {
                logger.info(StringHelper.combinedString(LOG_PREFIX, "指定下载商户{}的账单"), dealerId);
            } else {
                logger.info(StringHelper.combinedString(LOG_PREFIX, "当前应用被{}个商户授权"), appAuthList.size());
            }
        	
            String billDateType = "day";
            int days = downloadBillDays;
            while (true) {
                days--;
                Integer compareValue = TimeUtil.timeCompare(TimeUtil.getDayStart(billDate), TimeUtil.getDayStart(new Date()));
                if (compareValue == 1 || compareValue == 0) {
                    logger.warn(StringHelper.combinedString(LOG_PREFIX, "只能下载当前之前的账单"));
                    break;
                }
                
                for (AlipayAppAuthDetails appAuth : appAuthList) {
                    String billDateStr = "";
                    if ("month".equalsIgnoreCase(billDateType))
                        billDateStr = DateUtil.getDate(billDate, "yyyyMM");
                    else
                        billDateStr = DateUtil.getDate(billDate, "yyyyMMdd");
                    
                    String billZipName = billFileNamePrefix.concat(billDateStr).concat("-").concat(appAuth.getDealer().getDealerId()).concat(FILE_POSTFIX_ZIP);
                    // 指定希望保存的文件路径
                    String zipFilePath = zipFileDir.getAbsolutePath().concat(File.separator).concat(billZipName);
                    String csvFilePath = csvFileDir.getAbsolutePath().concat(File.separator).concat(billDateStr).concat(File.separator).concat(appAuth.getDealer().getDealerId()).concat(File.separator);
                    
                    String mark = appAuth.getDealer().getCompany() + "(" + appAuth.getDealer().getDealerId() + ")--" + billDateStr;
                    
                    boolean flag = false;
                    if (!new File(zipFilePath).exists()) {
                        // 查询账单下载地址
                        AlipayDataDataserviceBillDownloadurlQueryResponse urlQueryResponse = AliPayUtil.billDowndloadUrlQuery(BillType.trade, "day", billDate, appAuth.getAppAuthToken());
                        
                        if (urlQueryResponse == null) {
                            logger.error(StringHelper.combinedString(LOG_PREFIX, AlarmLogPrefix.invokeAliPayAPIErr, "[查询账单({})下载地址]", "-[失败]"), mark);
                        } else if (urlQueryResponse.isSuccess()) {
                            logger.info(StringHelper.combinedString(LOG_PREFIX, "[查询账单({})下载地址]", "-[成功]"), mark);

                            int times = 0;
                            while(true) {
                                if (times == 2) break;
                                try { // 下载账单
                                    logger.info(StringHelper.combinedString(LOG_PREFIX, "下载账单{}", "-[开始]"), mark);
                                    downloadBill(urlQueryResponse.getBillDownloadUrl(), zipFilePath);
                                    logger.info(StringHelper.combinedString(LOG_PREFIX, "下载账单{}", "-[成功]"), mark);
                                    flag = true;
                                    break;
                                } catch (Exception e) {
                                    logger.error(StringHelper.combinedString(LOG_PREFIX, "下载账单-{}", "-[失败]"), mark, e);
                                    times++;
                                }
                            }
                        } else if (AliPayEnums.BillDownloadUrlQueryResult.BILL_NOT_EXIST.getCode().equalsIgnoreCase(urlQueryResponse.getSubCode())){
                            logger.warn(StringHelper.combinedString(LOG_PREFIX, "[查询账单({})下载地址]", "-[不存在]"), mark);
                        } else {
                            logger.warn(StringHelper.combinedString(LOG_PREFIX, "[查询账单({})下载地址]", "-[失败]"), mark);
                        }
                    } else {// 账单已下载
                        flag = true;
                        logger.info(StringHelper.combinedString(LOG_PREFIX, "账单{}，已存在({}), 忽略下载"), mark, billZipName);
                    }

                    if (flag) {// 解压账单ZIP
                        logger.info(StringHelper.combinedString(LOG_PREFIX, "解压账单-{}", "-[开始]"), mark);
                        try {
                            CHZipUtils.unZip(zipFilePath, csvFilePath);
                            logger.info(StringHelper.combinedString(LOG_PREFIX, "解压账单-{}", "-[成功]"), mark);
                        } catch (Exception e) {
                            logger.error(StringHelper.combinedString(LOG_PREFIX, "解压账单-{}", "-[失败]"), mark, e);
                            flag = false;
                        }
                    }
                    // CSV导致DB
                    if (flag) {
                        readAndImport(csvFilePath, billDateStr);
                    }
				}
                
                if (days == 0) {
                	break;
                }
                billDate = TimeUtil.plusSeconds(billDate, 60 * 60 * 24);
            }
        }
        logger.info(StringHelper.combinedString(LOG_PREFIX, "-[结束]"));
    }

    private void readAndImport(String csvFilePath, String billDate) {
        File csvDir = new File(csvFilePath);
        // 查找业务明细文件
        File[] files = csvDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.indexOf("业务明细.csv") != -1)
                    return true;
                else
                    return false;
            }
        });
        if (files != null && files.length == 1) {
            CSVParser csvParser = null;
            boolean doSuccess = false;  
            try {
                logger.info(StringHelper.combinedString(LOG_PREFIX, "读取{}", "-[开始]"), files[0].getName());
                csvParser = CSVParser.parse(files[0], Charset.forName("GBK"), CSVFormat.DEFAULT);
                
                List<CSVRecord> records = csvParser.getRecords();
                List<AlipayBillDetails> billList = new ArrayList<AlipayBillDetails>();
                List<AlipayRefundDetails> refundList = new ArrayList<AlipayRefundDetails>();
                for (CSVRecord record :  records) {
                    if (record.getRecordNumber() > 5 && record.getRecordNumber() <= (records.size() - 4)) {
                        AlipayBillDetails billDetails = new AlipayBillDetails();
                        billDetails.setIwoid(Generator.generateIwoid());
                        billDetails.setTradeNo(StringUtils.trim(record.get(0)));
                        billDetails.setOutTradeNo(StringUtils.trim(record.get(1)));
                        billDetails.setSubject(StringUtils.trim(record.get(3)));
                        billDetails.setGmtCreate(DateUtil.getDate(StringUtils.trim(record.get(4)), "yyyy-MM-dd HH:mm:ss"));
                        billDetails.setGmtClose(DateUtil.getDate(StringUtils.trim(record.get(5)), "yyyy-MM-dd HH:mm:ss"));
                        billDetails.setStoreId(StringUtils.trim(record.get(6)));
                        billDetails.setStoreName(StringUtils.trim(record.get(7)));
                        billDetails.setOperatorId(StringUtils.trim(record.get(8)));
                        billDetails.setTerminalId(StringUtils.trim(record.get(9)));
                        billDetails.setBuyerLogon(StringUtils.trim(record.get(10)));
                        billDetails.setTotalAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(11)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setReceiptAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(12)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setAlipayRedEnvelopAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(13)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setPointAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(14)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setAlipayDiscountableAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(15)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setDealerDiscountableAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(16)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setCouponAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(17)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setCouponName(StringUtils.trim(record.get(18)));
                        billDetails.setDealerRedEnvelopAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(19)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setCardAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(20)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setRefundNo(StringUtils.trim(record.get(21)));
                        billDetails.setServiceAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(22)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setProfitAmount(new BigDecimal(Double.valueOf(StringUtils.trim(record.get(23)))).multiply(new BigDecimal(100)).intValue());
                        billDetails.setBillRemark(StringUtils.trim(record.get(24)));
                        
                        if ("交易".equals(StringUtils.trim(record.get(2)))) {
                            billDetails.setBillType("1");
                        } else if ("退款".equals(StringUtils.trim(record.get(2)))) {
                            billDetails.setBillType("2");
                            AlipayRefundDetails refundDetails = new AlipayRefundDetails();
                            refundDetails.setIwoid(Generator.generateIwoid());
                            refundDetails.setTradeNo(billDetails.getTradeNo());
                            refundDetails.setOutTradeNo(billDetails.getOutTradeNo());
                            refundDetails.setOutRefundNo(billDetails.getRefundNo());// 退款批次号
                            refundDetails.setTransBeginTime(billDetails.getGmtCreate());
                            refundDetails.setTransEndTime(billDetails.getGmtClose());
                            refundDetails.setGmtRefundPay(billDetails.getGmtClose());
                            refundDetails.setRefundFee(billDetails.getTotalAmount());// 退款金额
                            refundDetails.setRefundType(RefundType.REFUND.getValue());
                            refundDetails.setTradeStatus(TradeStatus.TRADE_SUCCESS.getValue());
                            refundList.add(refundDetails);
                        }
                        
                        if (billDetails.getOutTradeNo().startsWith(billDate)) {// 本系统的订单
                            billDetails.setSourceId(SourceId.self.getValue());
                        } else {// 外部的订单（支付宝官方或者别的服务商发起的订单）
                            billDetails.setSourceId(SourceId.outer.getValue());
                        }
                        
                        billList.add(billDetails);
                    }
                }
                logger.info(StringHelper.combinedString(LOG_PREFIX, "读取{}", "-[成功]"), files[0].getName());
                
                logger.info(StringHelper.combinedString(LOG_PREFIX, "批量保存账单明细{}", "-[开始]"), files[0].getName());
                try {
                    alipayBillDetailsService.doTransBatchAdd(billList, refundList);
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "批量保存账单明细{}", "-[成功]"), files[0].getName());
                    doSuccess = true;
                } catch (Exception e) {
                    logger.error(StringHelper.combinedString(LOG_PREFIX, "批量保存账单明细{}", "-[失败]"), files[0].getName(), e);
                }
            } catch (IOException e) {
                logger.error(StringHelper.combinedString(LOG_PREFIX, "读取{}", "-[失败]"), files[0].getName(), e);
            } catch (Exception e) {
                logger.error(StringHelper.combinedString(LOG_PREFIX, "读取处理{}", "-[失败]"), files[0].getName(), e);
            } finally {
                if (csvParser != null) {
                    try {
                        csvParser.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // 删除解压后的目录
                if (doSuccess) {
                    try {
                        logger.info(StringHelper.combinedString(LOG_PREFIX, "读取并处理{}成功", "-[删除账单]"), files[0].getName());
                        FileUtils.deleteDirectory(csvDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            logger.warn(StringHelper.combinedString(LOG_PREFIX, "目录：{}不存在“******业务明细.csv”"), csvFilePath);
        }
    }

    private void downloadBill(String downloadUrl, String pathName)
        throws Exception {
        URL url = null;
        HttpURLConnection httpUrlConnection = null;
        InputStream fis = null;
        FileOutputStream fos = null;
        try {
            url = new URL(downloadUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(5 * 1000);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setRequestProperty("Charsert", "UTF-8");
            httpUrlConnection.connect();
            fis = httpUrlConnection.getInputStream();
            byte[] temp = new byte[1024];
            int b;
            fos = new FileOutputStream(new File(pathName));
            while ((b = fis.read(temp)) != -1) {
                fos.write(temp, 0, b);
                fos.flush();
            }
        } catch (MalformedURLException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (fos != null)
                    fos.close();
                if (httpUrlConnection != null)
                    httpUrlConnection.disconnect();
            } catch (IOException e) {
                throw e;
            }
        }
    }

}
