package com.zbsp.wepaysp.timer.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.AliPayEnums.BillType;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.TimeUtil;

/**
 * 支付宝交易账单下载任务
 * 
 * @author 孟郑宏
 */
@Component
public class AliPayBillDownloadTask
    extends TimerBasicTask {

    private static String LOG_PREFIX = "[定时任务] - [支付宝交易账单下载] - ";

    @Value("${alipayBillFilePath}")
    private String billFilePath;
    
    @Value("${billDate}")
    private String configDate;

    private String billFileNamePrefix = "alipay_trade_bill";

    private String billFileNamePostfix = ".csv";

    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        Date billDate  = null;
        if (StringUtils.isBlank(configDate)) {
            billDate = TimeUtil.getBeforeDayStart();
        } else {
            billDate = DateUtil.getDate(configDate, "yyyy-MM-dd");
        }
        
        String appAuthToken = null;
        String billDateType = "day";
        AlipayDataDataserviceBillDownloadurlQueryResponse urlQueryResponse = AliPayUtil.billDowndloadUrlQuery(BillType.trade, "day", billDate, appAuthToken);

        if (urlQueryResponse != null && urlQueryResponse.isSuccess()) {
            logger.info(StringHelper.combinedString(LOG_PREFIX, "[查询账单下载地址]", "-[成功]"));
            // 下载账单
            String billDateStr = "";
            if ("month".equalsIgnoreCase(billDateType))
                billDateStr = DateUtil.getDate(billDate, "yyyyMMdd");
            else
                billDateStr = DateUtil.getDate(billDate, "yyyyMM");

            // 指定希望保存的文件路径
            String filePath = billFilePath.concat(billFileNamePrefix).concat(billDateStr).concat(billFileNamePostfix);
            URL url = null;
            HttpURLConnection httpUrlConnection = null;
            InputStream fis = null;
            FileOutputStream fos = null;
            try {
                url = new URL(urlQueryResponse.getBillDownloadUrl());
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
                fos = new FileOutputStream(new File(filePath));
                while ((b = fis.read(temp)) != -1) {
                    fos.write(temp, 0, b);
                    fos.flush();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                    if (fos != null)
                        fos.close();
                    if (httpUrlConnection != null)
                        httpUrlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            logger.warn(StringHelper.combinedString(LOG_PREFIX, "[查询账单下载地址]", "-[失败]"));
        }

        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }

}
