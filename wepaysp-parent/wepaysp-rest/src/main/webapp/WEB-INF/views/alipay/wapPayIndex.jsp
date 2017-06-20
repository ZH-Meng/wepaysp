<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>

<!DOCTYPE html>
<html>
<head>
	<head>
		<meta charset="utf-8" />
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<title>${indexVO.dealerName}订单</title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/weui.min.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/font/iconfont.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/main.css"/>
		<script  src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script>
		<script src="<%=request.getContextPath()%>/resources/js/keyboard.js"></script>
	    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/check.js"></script>
	</head>
</head>
<body>
		<form id="orderForm" action="<%=request.getContextPath()%>/alipay/wappay/createOrder"  method="post">
			<input type="hidden" value="${indexVO.dealerOid}"  name="dealerOid" id="dealerOid">
			<input type="hidden" value="${indexVO.storeOid}"  name="storeOid" id="storeOid">
			<input type="hidden" value="${indexVO.dealerEmployeeOid}"  name="dealerEmployeeOid" id="dealerEmployeeOid">

			<div class="weui-panel">
				<div class="company-info">
					<p class="title1">${indexVO.dealerName}订单</p>
				</div>
				<div class="input-box">
					<p class="input-title">消费总额</p>
					<p class="input-amt">
						<span class="fuhao">&yen;</span>
						<input id="money" name="money" type="text" type="hidden" readonly="readonly" contenteditable="true" />
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
				
				var input1 = document.getElementById('money');
				new KeyBoard(input1);
				$("#ok-btn").attr("disabled", true);
				
			});
			
			function submitOrder() {
				var dealerOid = $.trim($("#dealerOid").val());
				var money = $.trim($("#money").val());
				var numReg = /^(0|([1-9]\d*))(\.\d+)?$/;
				if (isBlank(dealerOid)) {
					alert('参数缺失！');
					return;
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