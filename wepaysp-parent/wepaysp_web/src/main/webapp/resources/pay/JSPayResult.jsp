<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<title>支付结果</title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/weui.min.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/font/iconfont.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/main.css"/>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	</head>
	<body>
	
	    <s:if test="tradeStatus == 1">
			<p class="succ-title">
				<i class="weui_icon_msg weui_icon_success"></i>恭喜您支付成功
			</p>
			<div class="succ-info">
				<p class="info1">${weixinPayDetailsVO.dealerName }</p>
				<p class="info2">&yen;<fmt:formatNumber value="${weixinPayDetailsVO.totalFee/100}" pattern="###,###,###,##0.00"/></p>
				<div class="break-line"></div>
			</div>
		</s:if>
		<s:elseif test="tradeStatus == 0">	
			<p class="error-title">
				 <i class="weui_icon_warn"></i>订单处理中
			</p>	
			<div class="succ-info">
				<div class="error-bottom">
					<img class="yzf-img" src="<%=request.getContextPath()%>/images/pay-error.png" alt="" />
				</div>			
				<p class="info2">订单处理中</p>
	
				<div class="error-item">
					请联系商家核实支付结果
				</div>	
				<div class="break-line"></div>
				<div class="succ-item">
					金&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;额<span class="fr">${weixinPayDetailsVO.totalFee/100}</span>
				</div>
			</div>				
		</s:elseif>
		
		<s:elseif test="tradeStatus == 4">	
			<p class="error-title">
				 <i class="weui_icon_warn"></i>订单已取消
			</p>
			<div class="succ-info">
				<div class="error-bottom">
					<img class="yzf-img" src="<%=request.getContextPath()%>/images/pay-error.png" alt="" />
				</div>			
				<p class="info2">订单已取消</p>
	
				<div class="error-item">
					请联系商家核实支付结果
				</div>	
				<div class="break-line"></div>
				<div class="succ-item">
					金&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;额<span class="fr">${weixinPayDetailsVO.totalFee/100}</span>
				</div>
			</div>
		</s:elseif>
		<s:elseif test="tradeStatus == 2">
			<p class="error-title">
				 <i class="weui_icon_warn"></i>支付失败请重试
			</p>		
			<div class="succ-info">
				<div class="error-bottom">
					<img class="yzf-img" src="<%=request.getContextPath()%>/images/pay-error.png" alt="" />
				</div>			
				<p class="info2">支付失败请重试</p>
				<div class="error-item">
					请联系商家核实支付结果
				</div>	
				<div class="break-line"></div>				
				<div class="succ-item">
					金&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;额<span class="fr">${weixinPayDetailsVO.totalFee/100}</span>
				</div>
			</div>
		</s:elseif>
			
		<div class="succ-item">
			门&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;店<span class="fr">${weixinPayDetailsVO.storeName }</span>
		</div>
		<div class="succ-item">
			交易单号<span class="fr">${weixinPayDetailsVO.outTradeNo }</span>
		</div>
		<div class="succ-item">
			交易时间<span class="fr"><s:date name="weixinPayDetailsVO.transBeginTime" format="yyyy-MM-dd HH:mm:ss"/></span>
		</div>
		<div class="pay-method">
			<i class="icon iconfont icon-weixinzhifu"></i>微信支付
		</div>
	</body>
</html>


