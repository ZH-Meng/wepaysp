package com.zbsp.wepaysp.mobile.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.zbsp.wepaysp.common.security.AesHelper;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.security.exception.AesDecryptException;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mobile.common.CommonResultCode;
import com.zbsp.wepaysp.mobile.model.base.MobileRequest;
import com.zbsp.wepaysp.mobile.model.base.MobileResponse;
import com.zbsp.wepaysp.mobile.model.base.RequestHead;
import com.zbsp.wepaysp.mobile.model.base.ResponseHead;

/**
 * 移动端请求验证拦截器 
 */
public class MobileBodyVerifyInterceptor
    extends HandlerInterceptorAdapter {
    
    protected Log logger = LogFactory.getLog(getClass());
    
    protected static final String STATIC_AESKEY = ResourceBundle.getBundle("config").getString("aes_key");
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        String content = getRequestContent(request, "UTF-8");
        logger.info("[接收来自" + request.getRemoteAddr() + "的请求]");
        logger.info("[请求内容]:" + content);
        
        // 解包
        MobileRequest mobileRequest = JSONUtil.parseObject(content, MobileRequest.class);

        MobileResponse mobileResponse = null;
        // 头部简单校验
        mobileResponse = verifyMobileRequestHead(mobileRequest);

        if (mobileResponse == null) {
            RequestHead requestHead = (RequestHead) mobileRequest.getHead().clone();
            requestHead.setSignature("null");
            
            // 解密Body（静态密钥）
            Map<String, Object> decryptResultMap = decryptMobileRequestBody(mobileRequest);
            mobileResponse = (MobileResponse) decryptResultMap.get("mobileResponse");

            if (mobileResponse == null) {
                String bodyStr = (String) decryptResultMap.get("requestBody");
                logger.info("[body内容]:" + bodyStr);
                
                // FIXME
                /*if (mobileResponse == null) {
                    // 校验Body信息
                    Map<String, Object> verifyBodyResultMap = verifyMobileRequestBody(mobileRequest, bodyStr);
                    mobileResponse = (MobileResponse) verifyBodyResultMap.get("mobileResponse");

                    if (mobileResponse == null) {
                        Object requestBody = verifyBodyResultMap.get("requestBody");
                        MobileRequest tempRequest = new MobileRequest();
                        tempRequest.setHead(requestHead);
                        tempRequest.setBody((Serializable) requestBody);

                        mobileResponse = verifyMobileRequestHeadSignature(mobileRequest,
                            JSONUtil.toJSONString(tempRequest, true));

                        if (mobileResponse == null) {
                            // 处理请求
                            mobileResponse = dealProcess(mobileRequest, requestBody);
                        }
                    }
                }*/
            }
        }
        
        if (mobileResponse == null) {
            String resonseBeanString = JSONUtil.toJSONString(mobileResponse, true);
            logger.info("[响应内容]:" + resonseBeanString);
            outputResponse(response, resonseBeanString, "text/json;charset=UTF-8");
            return false;
        }
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
        throws Exception {
        // TODO Auto-generated method stub
        super.postHandle(request, response, handler, modelAndView);
    }
    
    private String getRequestContent(HttpServletRequest request, String charsetName) throws IOException, UnsupportedEncodingException {
        StringBuilder sb;
        BufferedReader br = null;
        InputStream is = null;
        InputStreamReader lsr = null;
        try {
            is = request.getInputStream();
            lsr = new InputStreamReader(is, charsetName);
            br = new BufferedReader(lsr);
            String line = null;
            sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
            if (lsr != null) {
                lsr.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return sb.toString();
    }
    
    private void outputResponse(HttpServletResponse response, String outputContent, String contentType) throws IOException {
        response.setContentType(contentType);
        
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(outputContent);
            out.flush();
        } catch (IOException e) {
            throw e;
        } finally{
            if(out != null){
                out.close();
            }
        }
    }
    
    /**
     * 校验请求头，未通过校验返回响应对象.
     * 
     * @param mobileRequest 请求对象
     * @return 响应对象
     */
    private MobileResponse verifyMobileRequestHead(MobileRequest mobileRequest) {
        MobileResponse mobileResponse = null;

        if (mobileRequest == null || mobileRequest.getHead() == null || mobileRequest.getBody() == null) {
            ResponseHead responseHead = new ResponseHead(Generator.generateIwoid(), 
                CommonResultCode.parseError.getValue(), "数据包解析错误");

            mobileResponse = new MobileResponse(responseHead, "null").buildHead().buildBody(STATIC_AESKEY, "null");
        } else {
            RequestHead requestHead = mobileRequest.getHead();

            if (StringUtils.isBlank(requestHead.getBusinessId()) || StringUtils.isBlank(requestHead.getRequestId())
                || StringUtils.isBlank(requestHead.getSignature()) || StringUtils.isBlank(requestHead.getDeviceId()) 
                || StringUtils.isBlank(requestHead.getDeviceModel()) || StringUtils.isBlank(requestHead.getDeviceOPSysInfo()) 
                || requestHead.getDeviceId().length() < 15) {
                ResponseHead responseHead = new ResponseHead(Generator.generateIwoid(), 
                    CommonResultCode.verifyError.getValue(), "请求头缺少必要参数");

                mobileResponse = new MobileResponse(responseHead, "null").buildHead().buildBody(STATIC_AESKEY, "null");
            } else {
                String deviceId = mobileRequest.getHead().getDeviceId();

                if (!Validator.contains(RequestHead.AppType.class, requestHead.getAppType())) {
                    ResponseHead responseHead = new ResponseHead(requestHead.getRequestId(),
                        CommonResultCode.invalidAppType.getValue(), "无效的客户端类型");

                    mobileResponse = new MobileResponse(responseHead, "null").buildHead().buildBody(STATIC_AESKEY, deviceId);
                } /*else if (!requestHead.getBusinessId().equals(businessCode)) {
                    ResponseHead responseHead = new ResponseHead(requestHead.getRequestId(),
                        CommonResultCode.invalidBusinessId.getValue(), "非法的业务识别码");

                    mobileResponse = new MobileResponse(responseHead, "null").buildHead().buildBody(STATIC_AESKEY, deviceId);
                }*/ // FIXME
            }
        }

        return mobileResponse;
    }
    
    /**
     * 解密Body内容.
     * 如果解密失败或body内容为空，返回Map中的key:mobileResponse对应Value为响应对象. 
     * 如果通过解密返回Map中的key:requestBody对应值为body值的字符串.
     * 
     * @param mobileRequest 请求对象
     * @return 处理结果
     */
    private Map<String, Object> decryptMobileRequestBody(MobileRequest mobileRequest) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        MobileResponse mobileResponse = null;
        String deviceId = mobileRequest.getHead().getDeviceId();
        
        try {
            String decryptBody = AesHelper.decryptBase64(DigestHelper.md5Hex(STATIC_AESKEY
                + StringUtils.right(deviceId, 15)).substring(8, 24) , mobileRequest.getBody().toString(), 0);

            if (StringUtils.isBlank(decryptBody)) {
                ResponseHead responseHead = new ResponseHead(mobileRequest.getHead().getRequestId(),
                    CommonResultCode.parseError.getValue(), "请求体为空");

                mobileResponse = new MobileResponse(responseHead, "null").buildHead().buildBody(STATIC_AESKEY, deviceId);
            } else {
                resultMap.put("requestBody", decryptBody);
            }
        } catch (AesDecryptException e) {
            ResponseHead responseHead = new ResponseHead(mobileRequest.getHead().getRequestId(),
                CommonResultCode.parseError.getValue(), "请求体解密错误");

            mobileResponse = new MobileResponse(responseHead, "null").buildHead().buildBody(STATIC_AESKEY, deviceId);
            logger.error("解析请求体错误", e);
        }

        resultMap.put("mobileResponse", mobileResponse);
        return resultMap;
    }
    
    /**
     * 校验移动端请求头签名，通过校验返回null，否则返回响应对象
     * 
     * @param mobileRequest 请求头对象
     * @param bodyStr 解密后的请求Body字符串
     * @return 校验结果
     */
    private MobileResponse verifyMobileRequestHeadSignature(MobileRequest mobileRequest, String requestStr) {
        MobileResponse mobileResponse = null;

        if (!mobileRequest.getHead().getSignature().equals(DigestHelper.md5Hex("prvnterminalapp" + requestStr))) {
            ResponseHead responseHead = new ResponseHead(mobileRequest.getHead().getRequestId(),
                CommonResultCode.verifyError.getValue(), "签名校验失败");

            String deviceId = mobileRequest.getHead().getDeviceId();
            mobileResponse = new MobileResponse(responseHead, "null").buildHead().buildBody(STATIC_AESKEY, deviceId);
        }

        return mobileResponse;
    }
    
}
