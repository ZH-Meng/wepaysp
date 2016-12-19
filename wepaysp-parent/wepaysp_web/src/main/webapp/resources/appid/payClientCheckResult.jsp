<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<s:if test="payClient != 1">
			<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/weui.min.css"/>
			<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/font/iconfont.css"/>
			<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/main.css"/>
		</s:if>
		<title>安全支付</title>
		<script type="text/javascript">
			function onload() {
				var client = "${payClient}";
				if (client == "1") {// 微信
		        	window.location.href="${payUrl}";
		        } else if (client == "2") {// 支付宝
		           	//alert("支付宝支付正在研发中...");
		        } else {
		        	
		        }
			}
		</script>
	</head>
<body onload="onload()">
	<s:if test="payClient == 1"></s:if>
	<s:elseif test="payClient == 2">
		<div class="succ-info">
			<div class="error-bottom">
				<img class="yzf-img" src="<%=request.getContextPath()%>/images/pay-error.png" alt="" />
			</div>			
			<p class="info2">支付宝支付正在研发中，敬请期待!</p>
		</div>
	</s:elseif>
	<s:else>
		<div class="succ-info">
			<div class="error-bottom">
				<img class="yzf-img" src="<%=request.getContextPath()%>/images/pay-error.png" alt="" />
			</div>			
			<p class="info2">请使用微信支付！</p>
		</div>
	</s:else>
</body>
</html>