package com.zbsp.wepaysp.timer.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.AliPayEnums.BillType;
import com.zbsp.wepaysp.common.util.CHZipUtils;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.common.util.ZipUtil;

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

    private final String billFileNamePrefix = "alipay_trade_bill";

    private static final String FILE_POSTFIX_ZIP = ".csv.zip";
    private static final String FILE_POSTFIX_CSV = ".csv";
    
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

        String appAuthToken = null;
        String billDateType = "day";
        int days = downloadBillDays;
        while (true) {
            days--;
            
            Integer compareValue = TimeUtil.timeCompare(TimeUtil.getDayStart(billDate), TimeUtil.getDayStart(new Date()));
            if (compareValue == 1 || compareValue == 0) {
                logger.warn(StringHelper.combinedString(LOG_PREFIX, "只能下载当前之前的账单"));
                break;
            }
            
            // 查询账单下载地址
            AlipayDataDataserviceBillDownloadurlQueryResponse urlQueryResponse = AliPayUtil.billDowndloadUrlQuery(BillType.signcustomer, "day", billDate, appAuthToken);

            String billDateStr = "";
            if ("month".equalsIgnoreCase(billDateType))
                billDateStr = DateUtil.getDate(billDate, "yyyyMM");
            else
                billDateStr = DateUtil.getDate(billDate, "yyyyMMdd");

            if (urlQueryResponse != null && urlQueryResponse.isSuccess()) {
                logger.info(StringHelper.combinedString(LOG_PREFIX, "[查询账单({})下载地址]", "-[成功]"), billDateStr);

                // 指定希望保存的文件路径
                String zipFilePath = zipFileDir.getAbsolutePath().concat(File.separator).concat(billFileNamePrefix).concat(billDateStr).concat(FILE_POSTFIX_ZIP);
                String csvFilePath = csvFileDir.getAbsolutePath().concat(File.separator).concat(billDateStr).concat(File.separator);

                boolean flag = false;
                try { // 下载账单
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "下载账单{}", "-[开始]"), billDateStr);
                    downloadBill(urlQueryResponse.getBillDownloadUrl(), zipFilePath);
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "下载账单{}", "-[成功]"), billDateStr);
                    flag = true;
                } catch (Exception e) {
                    logger.error(StringHelper.combinedString(LOG_PREFIX, "下载账单-{}", "[失败]"), billDateStr, e);
                }

                if (flag) {// 解压账单ZIP
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "解压账单-{}", "[开始]"), billDateStr);
                    try {
                        CHZipUtils.unZip(zipFilePath, csvFilePath);
                        logger.info(StringHelper.combinedString(LOG_PREFIX, "解压账单-{}", "[成功]"), billDateStr);
                    } catch (Exception e) {
                        logger.error(StringHelper.combinedString(LOG_PREFIX, "解压账单-{}", "[失败]"), billDateStr, e);
                        flag = false;
                    }
                }
                // CSV导致DB
                if (flag) {

                }
            } else {
                logger.warn(StringHelper.combinedString(LOG_PREFIX, "[查询账单({})下载地址]", "-[失败]"), billDateStr);
            }
            
            if (days == 0) {
                break;
            }
            billDate = TimeUtil.plusSeconds(billDate, 60 * 60 * 24);
        }

        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
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
