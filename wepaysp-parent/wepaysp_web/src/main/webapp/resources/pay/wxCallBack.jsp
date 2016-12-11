<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
	<head>
		<meta charset="utf-8" />
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<title>${dealerName}订单</title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/weui.min.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/font/iconfont.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/main.css"/>
		<script  src="<%=request.getContextPath()%>/js/jquery-1.9.1.min.js"></script>
		<script src="<%=request.getContextPath()%>/js/keyboard.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	    <script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	</head>
</head>
<body>
		<form id="orderForm" action="<%=request.getContextPath()%>/nostate/pay/appidpay!createOrder.action"  method="post">
			<%-- <s:hidden name="partnerOid"/> --%>
			<s:hidden name="dealerOid"/>
			<s:hidden name="storeOid"/>
			<s:hidden name="dealerEmployeeOid"/>
			<s:hidden name="openid"/>

			<div class="weui-panel">
				<div class="company-info">
					<p class="title1">${dealerName}订单</p>
				</div>
				<div class="input-box">
					<p class="input-title">消费总额</p>
					<p class="input-amt">
						<span class="fuhao">&yen;</span>
						<input id="money" name="money" type="number" type="hidden" readonly="readonly" contenteditable="true" />
					</p>
					<div class="break-line"></div>
					<p class="input-explain">可询问服务员消费总额</p>
				</div>
				<input id="ok-btn" type="button" class="weui_btn weui_btn_primary" onclick="submitOrder();"  value="确认买单" />			
			</div>
			<div class="space-bar"></div>
		</form>
		<script>
			$(function () {
				$("#money").focus();
				
				// 校验微信版本
				var wechatInfo = navigator.userAgent.match(/MicroMessenger\/([\d\.]+)/i) ;
				if( !wechatInfo ) {
				    alert("订单支付仅支持微信") ;
				} else if ( wechatInfo[1] < "5.0" ) {
				    alert("订单支付仅支持微信5.0以上版本") ;
				}
				
				var input1 = document.getElementById('money');
				new KeyBoard(input1);
				$("#ok-btn").attr("disabled", true);
			});
 
			function submitOrder() {
				var dealerOid = $.trim($("#dealerOid").val());
				var openid = $.trim($("#openid").val());				
				var money = $.trim($("#money").val());
				var numReg = /^(0|([1-9]\d*))(\.\d+)?$/;
				if (isBlank(dealerOid) || isBlank(openid)) {
					alert('参数缺失！');
				} else if (isBlank(money)) {
					alert('请输入金额！');
					return;
				} else if (!numReg.test(money)) {
					alert("金额必须大于0的数字！");
					$("#money").focus();
					return;
				}
				$("#orderForm").submit();
			}
		</script>
</body>
</html>