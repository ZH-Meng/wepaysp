package com.zbsp.wepaysp.manage.web.action.edu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zbsp.wepaysp.api.service.main.edu.AlipayEduBillMainService;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.manage.web.util.SpringContextUtil;

/** 
 * 教育缴费账单明细异步通知处理，为了验签而单独设置GBK编码 
 */
public class AlipayEduBillAsynNotifyServlet
    extends HttpServlet {

    private static final long serialVersionUID = 4873650609593637328L;

    // 日志对象
    protected Logger logger = LogManager.getLogger(getClass());

    private AlipayEduBillMainService alipayEduBillMainService;

    @Override
    public void init(ServletConfig config)
        throws ServletException {
        super.init(config);
        alipayEduBillMainService = (AlipayEduBillMainService) SpringContextUtil.getBean("alipayEduBillMainService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String logPrefix = "处理支付宝教育缴费账单异步通知请求 - ";
        logger.info(logPrefix + "开始");
        try {
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
            response.getWriter().write(result.toLowerCase()); // 返回非 success 也无意义，支付宝有重发机制
            logger.info(logPrefix + "处理结果为({})", result);
        } catch (Exception e) {
            logger.error(logPrefix + "异常 : {}", e.getMessage(), e);
        }
        logger.info(logPrefix + "结束");
    }
}
