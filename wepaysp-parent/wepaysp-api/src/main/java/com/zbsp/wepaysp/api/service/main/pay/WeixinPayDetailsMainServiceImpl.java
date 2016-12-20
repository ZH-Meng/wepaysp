package com.zbsp.wepaysp.api.service.main.pay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.tencent.WXPay;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.protocol.appid.send_template_msg_protocol.SendTemplateMsgResData;
import com.tencent.protocol.unified_order_protocol.JSPayReqData;
import com.tencent.protocol.unified_order_protocol.WxPayNotifyData;
import com.tencent.protocol.unified_order_protocol.WxPayNotifyResultData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.EnumDefine.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.EnumDefine.OrderQueryErr;
import com.zbsp.wepaysp.common.constant.EnumDefine.ResultCode;
import com.zbsp.wepaysp.common.constant.EnumDefine.ReturnCode;
import com.zbsp.wepaysp.common.constant.EnumDefine.SendTempMsgErr;
import com.zbsp.wepaysp.common.constant.EnumDefine.WxPayResult;
import com.zbsp.wepaysp.common.exception.DataStateException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.api.util.WeixinPackConverter;
import com.zbsp.wepaysp.api.util.WeixinUtil;
import com.zbsp.wepaysp.api.listener.DefaultCloseOrderBusinessResultListener;
import com.zbsp.wepaysp.api.listener.DefaultOrderQueryBusinessResultListener;
import com.zbsp.wepaysp.api.listener.DefaultScanPayBusinessResultListener;
import com.zbsp.wepaysp.api.listener.DefaultUnifiedOrderBusinessResultListener;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails.PayType;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails.TradeStatus;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;
import com.zbsp.wepaysp.vo.weixin.PayNoticeBindWeixinVO;


public class WeixinPayDetailsMainServiceImpl
    extends BaseService
    implements WeixinPayDetailsMainService {

	private SysConfigService sysConfigService;
    private WeixinPayDetailsService weixinPayDetailsService;
    private PayNoticeBindWeixinService payNoticeBindWeixinService;
	
    @Override
    public Map<String, Object> createPayAndInvokeWxPay(WeixinPayDetailsVO weixinPayDetailsVO, String creator, String operatorUserOid, String logFunctionOid) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String resCode = WxPayResult.ERROR.getCode();
        String resDesc = WxPayResult.ERROR.getDesc();
        String outTradeNo = null; 
        logger.info("创建微信支付明细开始");
        try {
            weixinPayDetailsService.doTransCreatePayDetails(weixinPayDetailsVO, creator, operatorUserOid, logFunctionOid);
            outTradeNo = weixinPayDetailsVO.getOutTradeNo();
            logger.info("微信支付明细保存成功！");
        } catch (Exception e) {
            logger.warn("创建微信支付明细失败");
            throw e;
        }
        
        String payType = weixinPayDetailsVO.getPayType();
        
        if (StringUtils.equals(PayType.MICROPAY.getValue(), payType)) {// 刷卡支付
            logger.info("开始微信刷卡支付！");
            String transactionId = null;
            try {
                DefaultScanPayBusinessResultListener scanPayListener = new DefaultScanPayBusinessResultListener(this);// 刷卡支付监听器

                // 组包、调用刷卡API
                WXPay.doScanPayBusiness(WeixinPackConverter.weixinPayDetailsVO2ScanPayReq(weixinPayDetailsVO), scanPayListener, weixinPayDetailsVO.getCertLocalPath(), weixinPayDetailsVO.getCertPassword(), weixinPayDetailsVO.getKeyPartner());
                
                transactionId = scanPayListener.getTranscationID();// 正常情况业务结果为成功时返回

                // 查询支付结果
                Map<String, Object> jpqlMap = new HashMap<String, Object>();
                String jpql = "from WeixinPayDetails w where w.outTradeNo=:OUTTRADENO";
                jpqlMap.put("OUTTRADENO", outTradeNo);

                WeixinPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false);
                if (payDetails == null) {
                    throw new NotExistsException("系统支付订单不存在！");
                }

                String wxResultCode = payDetails.getResultCode();
                resCode = payDetails.getErrCode();
                resDesc = payDetails.getErrCodeDes();

                if (StringUtils.equalsIgnoreCase(WxPayResult.SUCCESS.getCode(), wxResultCode)) {// 业务结果：成功
                    resCode = WxPayResult.SUCCESS.getCode();
                    resDesc = WxPayResult.SUCCESS.getDesc();
                    logger.info("系统订单ID=" + outTradeNo + "微信刷卡支付成功，微信支付订单ID=" + payDetails.getTransactionId());
                } else {// 支付失败或错误
                    logger.warn("系统订单ID" + outTradeNo + "微信刷卡支付失败，错误码：" + resCode + "，错误描述：" + resDesc);
                }
                resultMap.put("transactionId", transactionId);
            } catch (Exception e) {
                logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxPayAPIErr.getValue(), 
                    "系统支付订单(ID=" + outTradeNo + "）支付错误", "，异常信息：" + e.getMessage()));
            }
        } else if (StringUtils.equals(PayType.JSAPI.getValue(), payType)) {// 公众号支付
            logger.info("开始微信公众号下单！");
            String prepayId = null;
            try {
                DefaultUnifiedOrderBusinessResultListener orderListener = new DefaultUnifiedOrderBusinessResultListener(weixinPayDetailsService);// 公众号下单监听器
                
                // 组包、调用下单API
                WXPay.doUnifiedOrderBusiness(WeixinPackConverter.weixinPayDetailsVO2UnifiedOrderReq(weixinPayDetailsVO), orderListener, weixinPayDetailsVO.getCertLocalPath(), weixinPayDetailsVO.getCertPassword(), weixinPayDetailsVO.getKeyPartner());
                
                String listenerResult = orderListener.getResult();
                if (StringUtils.equalsIgnoreCase(listenerResult, DefaultScanPayBusinessResultListener.ON_SUCCESS)) {
                    prepayId = orderListener.getPrepayId();// 预支付标识
                    if (StringUtils.isBlank(orderListener.getPrepayId())) {
                        logger.warn("公众号下单结果prepayId缺失");
                        resDesc = "公众号下单结果prepayId缺失";
                    } else {
                        resCode = WxPayResult.SUCCESS.getCode();
                        resDesc = WxPayResult.SUCCESS.getDesc();
                        // 组装支付请求包
                        JSPayReqData jsPayReqData = new JSPayReqData(weixinPayDetailsVO.getAppid(), prepayId, weixinPayDetailsVO.getKeyPartner());
                        resultMap.put("jsPayReqData", jsPayReqData);
                    }
                }
                resultMap.put("prepayId", prepayId);
            } catch (Exception e) {
                logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxPayAPIErr.getValue(), 
                    "系统支付订单(ID=" + outTradeNo + "）公众号下单错误", "，异常信息：" + e.getMessage()));
            }
        }
        
        resultMap.put("resultCode", resCode);
        resultMap.put("resultDesc", resDesc);
        resultMap.put("outTradeNo", outTradeNo);
        resultMap.put("wexinPayDetailsVO", weixinPayDetailsVO);
        
        return resultMap;
    }

    @Override
    public String handleWxPayNotify(String respXmlString) {
        Validator.checkArgument(StringUtils.isBlank(respXmlString), "支付结果通知字串不能为空");
        logger.info("微信支付结果通知处理API开始处理.");
        WxPayNotifyResultData result=null;
        WxPayNotifyData wxNotify = (WxPayNotifyData) Util.getObjectFromXML(respXmlString, WxPayNotifyData.class);
        if (wxNotify == null || StringUtils.isBlank(wxNotify.getAppid())) {
            logger.error("微信支付结果通知，解析参数格式失败，结果内容：" + respXmlString);
            result=new WxPayNotifyResultData("FAIL");
            result.setReturn_msg("解析参数格式失败");
        } else {
        	String key = null;
        	// 从内存中查找key 
        	Map<String, Object> partnerMap = sysConfigService.getPartnerCofigInfoByAppid(wxNotify.getAppid());
			if (partnerMap != null && !partnerMap.isEmpty()) {
				key = MapUtils.getString(partnerMap, SysEnvKey.WX_KEY);
			}
            //检查xml是否有效        
            boolean flag = false;
            try {
                flag = Signature.checkIsSignValidFromResponseString(respXmlString, key);
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
                result=new WxPayNotifyResultData("FAIL");
                result.setReturn_msg("失败");
            }
            if (flag) {
                String returnCode = wxNotify.getReturn_code();
                String resultCode = wxNotify.getResult_code();
                try {
	                if (StringUtils.equalsIgnoreCase(ReturnCode.SUCCESS.toString(), returnCode)) {
	                	
	                    WeixinPayDetailsVO payResultVO;
						try {
							payResultVO = weixinPayDetailsService.doTransUpdatePayResult(returnCode, resultCode, WeixinPackConverter.payNotify2weixinPayDetailsVO(wxNotify));
							if (StringUtils.equalsIgnoreCase(ResultCode.SUCCESS.toString(), resultCode)) {
								logger.info("微信异步通知支付成功，向收银员/商户发送支付成功通知");
								// 发送支付结果公众号信息（支付成功）
								try {
									sendPayResultNotice(payResultVO);
								} catch (Exception e) {
									logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(), 
											"发送支付成功通知错误，异常信息：" + e.getMessage()));
								}
							}
						} catch (DataStateException e) {
							logger.warn(e.getMessage());
						} catch (Exception e) {
							throw e;
						}
						result = new WxPayNotifyResultData("SUCCESS");
						result.setReturn_msg("成功");
	                } else {
	                    logger.warn("微信支付结果通知，returnCode 为 FAIL");
	                    result = new WxPayNotifyResultData("FAIL");
	                    result.setReturn_msg("returnCode为FAIL");
	                }
                } catch (Exception e) {
                    logger.error(StringHelper.combinedString(AlarmLogPrefix.handleWxPayResultErr.getValue(), 
                        "微信支付结果通知错误，结果内容：" + respXmlString, "，异常信息：" + e.getMessage()));
                    logger.error(e.getMessage(), e);
                	result = new WxPayNotifyResultData("FAIL");
                	result.setReturn_msg("失败");
                }
            } else {
                logger.error("微信支付结果通知，签名失败，结果内容：" + respXmlString);
                result = new WxPayNotifyResultData("FAIL");
                result.setReturn_msg("签名失败");
            }
        }
        XStream xStreamForResponsetData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));

        //将要提交给微信支付结果通知处理结果对象转换成XML格式数据
        String resultXML = xStreamForResponsetData.toXML(result);
        logger.info("处理结束，返回结果：" + resultXML);
        return resultXML;
    }

	@Override
    public Map<String, Object> checkPayResult(String payResult, String weixinPayDetailOid) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        WeixinPayDetailsVO payDetailVO =  weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsByOid(weixinPayDetailOid);
        int tradeStatus = payDetailVO.getTradeStatus();// 处理前订单状态
        
        String certLocalPath = null;
        String certPassword = null;
        String keyPartner = null;
        Map<String, Object> partnerMap = sysConfigService.getPartnerCofigInfoByPartnerOid(payDetailVO.getPartner1Oid());
		if (partnerMap != null && !partnerMap.isEmpty()) {
			certLocalPath = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH);
			certPassword = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD);
			keyPartner = MapUtils.getString(partnerMap, SysEnvKey.WX_KEY);
			payDetailVO.setKeyPartner(keyPartner);
		} else {
			throw new RuntimeException("系统数据异常，服务商配置信息不存在");
		}
        
        if (tradeStatus == TradeStatus.TRADEING.getValue()) {// 处理中，代表系统没有收到微信支付结果通知
            if ("cancel".equalsIgnoreCase(payResult)) {// 用户取消支付
                // 关闭订单
                // 由于立即关闭订单提示 支付锁定，扣款和撤销间隔要在10s以上，所以先将订单设置为待关闭，由定时任务处理
                /*logger.info("系统支付订单状态处理中，用户取消支付，现主动发起关闭订单请求");
                try {
                    WXPay.doCloseOrderBusiness(WeixinPackConverter.weixinPayDetailsVO2CloseOrderReq(payDetailVO),
                        new DefaultCloseOrderBusinessResultListener(weixinPayDetailsService), certLocalPath, certPassword, keyPartner);
                } catch (Exception e) {
                    logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxPayAPIErr.getValue(), 
                        "系统支付订单(ID=" + payDetailVO.getOutTradeNo() + "）关闭错误", "，异常信息：" + e.getMessage()));
                    logger.error(e.getMessage(), e);
                }*/
                logger.info("系统支付订单状态处理中，用户取消支付，设置订单状态为待关闭");
                tradeStatus = TradeStatus.TRADE_TO_BE_CLOSED.getValue();
                try {
                    weixinPayDetailsService.doTransCancelPay(weixinPayDetailOid);
                } catch (Exception e) {
                    logger.error("取消订单（设置状态为待关闭）失败，异常信息：" + e.getMessage());
                }
            } else {
                logger.info("系统支付订单状态处理中，系统暂未收到微信支付结果通知，现主动发起订单查询请求");
                // 主动查询微信支付结果
                try {
                    DefaultOrderQueryBusinessResultListener orderQueryListener = new DefaultOrderQueryBusinessResultListener(this);// 订单查询监听器
                    
                    // 订单查询
                    WXPay.doOrderQueryBusiness(WeixinPackConverter.weixinPayDetailsVO2OrderQueryReq(payDetailVO), orderQueryListener, certLocalPath, certPassword, payDetailVO.getKeyPartner());
                    
                    payDetailVO = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsByOid(weixinPayDetailOid);
                    tradeStatus = payDetailVO.getTradeStatus();// 当前订单状态
                } catch (Exception e) {
                    logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxPayAPIErr.getValue(), 
                        "系统支付订单(ID=" + payDetailVO.getOutTradeNo() + "）查询错误", "，异常信息：" + e.getMessage()));
                    logger.error(e.getMessage(), e);
                }
            }
        }
        
        if ("ok".equalsIgnoreCase(payResult)) {
            if (tradeStatus != TradeStatus.TRADE_SUCCESS.getValue()) {
                logger.warn("微信H5支付结果为ok，系统支付交易状态为：" + tradeStatus + "系统订单ID：" + payDetailVO.getOutTradeNo());
            }
        } else if ("cancel".equalsIgnoreCase(payResult)) {// 用户取消支付
            //weixinPayDetailsService.doTransCancelPay(weixinPayDetailOid);
            if (tradeStatus != TradeStatus.TRADE_TO_BE_CLOSED.getValue()) {
                logger.warn("微信H5支付结果为cancel，系统支付交易状态为：" + tradeStatus + "系统订单ID：" + payDetailVO.getOutTradeNo());
            }
        } else if ("error".equalsIgnoreCase(payResult)) {// 用户取消支付
            if (tradeStatus != TradeStatus.TRADE_FAIL.getValue()) {
                logger.warn("微信H5支付结果为error，系统支付交易状态为：" + tradeStatus + "系统订单ID：" + payDetailVO.getOutTradeNo());
            }
        }
        
        resultMap.put("tradeStatus", tradeStatus);
        resultMap.put("weixinPayDetailsVO", payDetailVO);
        return resultMap;
    }

    @Override
    public void handleOrderQueryResult(String resultCode, WeixinPayDetailsVO queryResultVO) {
        Validator.checkArgument(queryResultVO == null, "调用订单查询API结果queryResultVO不能为空");
        Validator.checkArgument(StringUtils.isBlank(queryResultVO.getOutTradeNo()), "系统订单ID不能为空");
        if (StringUtils.isBlank(resultCode)) {
            Validator.checkArgument(StringUtils.isBlank(queryResultVO.getResultCode()), "resultCode不能空");
            resultCode = queryResultVO.getResultCode();
        }
        
        if (StringUtils.equalsIgnoreCase(ResultCode.SUCCESS.toString(), resultCode)) {// 查询成功
            logger.info("调用订单查询API结果成功，更新系统订单状态");
            WeixinPayDetailsVO payResultVO = weixinPayDetailsService.doTransUpdateOrderQueryResult(queryResultVO);
            
            if (payResultVO != null && WeixinPayDetails.TradeStatus.TRADE_SUCCESS.getValue() == payResultVO.getTradeStatus()) {
                logger.info("订单查询结果为支付成功，向收银员/商户发送支付成功通知");
                try {
                    sendPayResultNotice(payResultVO);
                } catch (Exception e) {
                    logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(), 
                            "发送支付成功通知错误，异常信息：" + e.getMessage()));
                }
            }
        } else {
            if (StringUtils.equalsIgnoreCase(OrderQueryErr.ORDERNOTEXIST.toString(), queryResultVO.getErrCode())) {// 订单不存在
                logger.info("调用订单查询API结果【订单不存在】，调用关闭订单API");
                if (StringUtils.isBlank(queryResultVO.getAppid())) {
                	throw new RuntimeException("queryResultVO缺失appid");
                }
                // 从内存中获取服务商配置信息
                String certLocalPath = null;
                String certPassword = null;
                String keyPartner = null;
                Map<String, Object> partnerMap = sysConfigService.getPartnerCofigInfoByAppid(queryResultVO.getAppid());
        		if (partnerMap != null && !partnerMap.isEmpty()) {
        			certLocalPath = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH);
        			certPassword = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD);
        			keyPartner = MapUtils.getString(partnerMap, SysEnvKey.WX_KEY);
        			queryResultVO.setKeyPartner(keyPartner);
        		} else {
        			throw new RuntimeException("系统数据异常，服务商配置信息不存在");
        		}
                
                // 关闭订单
                try {
                    WXPay.doCloseOrderBusiness(WeixinPackConverter.weixinPayDetailsVO2CloseOrderReq(queryResultVO),
                        new DefaultCloseOrderBusinessResultListener(weixinPayDetailsService), certLocalPath, certPassword, keyPartner);
                } catch (Exception e) {
                    logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxPayAPIErr.getValue(), 
                        "系统支付订单(ID=" + queryResultVO.getOutTradeNo() + "）关闭错误", "，异常信息：" + e.getMessage()));
                    logger.error(e.getMessage(), e);
                }
            } else if (StringUtils.equalsIgnoreCase(OrderQueryErr.SYSTEMERROR.toString(), queryResultVO.getErrCode())) {// 微信系统异常
                logger.warn("系统支付订单(ID=" + queryResultVO.getOutTradeNo() + ")调用订单查询API结果错误码：SYSTEMERROR，错误描述：" + queryResultVO.getErrCodeDes() +"，【由定时器再发起查询】");
            } else {
                logger.error(StringHelper.combinedString(AlarmLogPrefix.handleWxPayResultException.getValue(), "系统订单(ID=" + queryResultVO.getOutTradeNo() + ")调用订单查询API结果错误未知", 
                    "错误码为" + queryResultVO.getErrCode() + "，错误描述为：" + queryResultVO.getErrCodeDes()));
            }
        }
        
    }
    
    @Override
    public void updateScanPayResult(String returnCode, String resultCode, WeixinPayDetailsVO payResultVO) {
    	// 更新扫码支付结果
    	WeixinPayDetailsVO payDetailsVO = weixinPayDetailsService.doTransUpdatePayResult(returnCode, resultCode, payResultVO);
        
        if (StringUtils.equalsIgnoreCase(ResultCode.SUCCESS.toString(), resultCode)) {
        	logger.info("扫码支付成功，向收银员/商户发送支付成功通知");
        	// 发送支付结果公众号信息（支付成功）
        	try {
        		sendPayResultNotice(payDetailsVO);
			} catch (Exception e) {
				logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(), 
                        "发送支付成功通知错误，异常信息：" + e.getMessage()));
				logger.error(e.getMessage(), e);
			}
        }
    }

    private void sendPayResultNotice(WeixinPayDetailsVO payDetailsVO) throws Exception {
    	// 检查发送支付信息完整性
    	Validator.checkArgument(payDetailsVO == null, "payDetailsVO不能为空");
    	Validator.checkArgument(payDetailsVO.getTotalFee() == null, "金额不能为空");
    	Validator.checkArgument(payDetailsVO.getTransBeginTime() == null, "金额不能为空");
    	Validator.checkArgument(StringUtils.isBlank(payDetailsVO.getOutTradeNo()), "系统订单号不能为空");
    	Validator.checkArgument(StringUtils.isBlank(payDetailsVO.getPartner1Oid()), "partner1Oid不能为空");
    	
    	// 查找需要收到支付通知的人员touser
    	Map<String, Object> queryMap = new HashMap<String, Object>(); 
    	if (StringUtils.isNotBlank(payDetailsVO.getStoreOid())) {
    		if (StringUtils.isNotBlank(payDetailsVO.getDealerEmployeeOid())) {// 收银员级别扫码
	    		queryMap.put("type", PayNoticeBindWeixin.Type.dealerEmployee.getValue());
	    		queryMap.put("dealerEmployeeOid", payDetailsVO.getDealerEmployeeOid());
    		} else {// 门店级别扫码
    			queryMap.put("type", PayNoticeBindWeixin.Type.store.getValue());
    			queryMap.put("storeOid", payDetailsVO.getStoreOid());
    		}
    	} else {
    		throw new RuntimeException("支付订单数据异常：storeOid不能为空");
    	}
    	queryMap.put("state", PayNoticeBindWeixin.State.open.getValue());
    	List<PayNoticeBindWeixinVO> toUserList = payNoticeBindWeixinService.doJoinTransQueryPayNoticeBindWeixinList(queryMap);
    	
    	if (toUserList != null && !toUserList.isEmpty()) {
    		int count = 0;
    		logger.info("订单（ID=" + payDetailsVO.getOutTradeNo() + "）需要向（" + toUserList.size() + "人）发送支付成功通知");
    		// 准备发送消息的参数（服务商配置信息）
    		String certLocalPath = null;
    		String certPassword = null;
    		String accessToken = null;
    		Map<String, Object> partnerMap = sysConfigService.getPartnerCofigInfoByPartnerOid(payDetailsVO.getPartner1Oid());
    		if (partnerMap != null && !partnerMap.isEmpty()) {
    			certLocalPath = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH);
    			certPassword = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD);
        		// 获取Base_acction_token
    			accessToken = new WeixinUtil().getBaseAccessToken(payDetailsVO.getPartner1Oid());
    		} else {
    			throw new RuntimeException("系统数据异常，服务商配置信息不存在");
    		}
    		
    		for(PayNoticeBindWeixinVO toUser : toUserList) {
    			if (toUser != null && StringUtils.isNotBlank(toUser.getOpenid())) {
					SendTemplateMsgResData sendResult = WeixinUtil.sendPaySuccessNotice(payDetailsVO, toUser.getOpenid(), certLocalPath, certPassword, accessToken);
					if (SendTempMsgErr.SUCCESS.getValue().equals(sendResult.getErrcode())) {// 发送成功
						logger.info("订单（ID=" + payDetailsVO.getOutTradeNo() + "）向（" + toUser.getNickname() + "(openid=" + toUser.getOpenid() + ")）发送支付成功通知成功");
						count ++;
					} else {
						logger.info("订单（ID=" + payDetailsVO.getOutTradeNo() + "）向（" + toUser.getNickname() + "(openid=" + toUser.getOpenid() + ")）发送支付成功通知失败");
					}
    			}
    		}
    		logger.info("订单（ID=" + payDetailsVO.getOutTradeNo() + "）成功向（" + count + "人）发送支付成功通知");
    	}
	}
    
    public void setSysConfigService(SysConfigService sysConfigService) {
		this.sysConfigService = sysConfigService;
	}

	public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
    }

	public void setPayNoticeBindWeixinService(PayNoticeBindWeixinService payNoticeBindWeixinService) {
		this.payNoticeBindWeixinService = payNoticeBindWeixinService;
	}
    
}
