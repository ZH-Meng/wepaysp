<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>{商户}订单</title>
<style type="text/css">
.m-app-wrapper {
	padding: 10px;
	max-width:500px;
	width: 90%;
	margin: 0 auto;
}

.m-app-line{
	padding: 10px 0;
	width: 100%;
}

.m-app-btn {
	margin: 0 auto;
	position: relative;
	display: inline-block;
	vertical-align: middle;
	white-space: nowrap;
	width: 100%;
	height: 35px;
	line-height: 35px;
	font-size: 14px;
	text-align: center;
	cursor: pointer;
	text-decoration: none;
	border: 1px solid #5aad35;
	background-image: -webkit-linear-gradient(#91dd70, #55ae2e);
	background-image: linear-gradient(#91dd70, #55ae2e);
	text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.15);
	border-radius: 3px;
	background-color: #6cc644;
	color: #fff;
}
.m-app-input-money{
	text-align: right;
	padding-right:5px;	
	border: 0;
	width:80%;
	height: 27px;
	line-height: 27px;
	position: absolute;
	right: 0;
}
.m-app-input{
	position:relative;
	display:inline-block;
    height: 30px;
    width: 100%;
    font-size: 12px;
    line-height: 30px;
    color: #333;
    vertical-align: middle;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 3px;
    outline: none;
    box-shadow: inset 0 1px 2px rgba(0,0,0,0.075);
}
.m-app-label{
	padding-left: 10px;
	letter-spacing: 2px;
}
.m-app-title{
	display:inline-block;
	width:100%;
	text-align:center;
	font-weight:bold;
	font-family: Segoe UI,Lucida Grande,Helvetica,Arial,Microsoft YaHei,FreeSans,Arimo,Droid Sans,wenquanyi micro hei,Hiragino Sans GB,Hiragino Sans GB W3,FontAwesome,sans-serif;
}
</style>
</head>
<body>
	<div class="m-app-wrapper">
		<form name="orderForm" action="<%=request.getContextPath()%>/resources/pay/payment!cashier.action"  method="post">
			<div class="m-app-line">
				<span class="m-app-title">商户订单</span>
			</div>
			<div class="m-app-line">
				<span class="m-app-input">
					<span class="m-app-label">消费总额：</span>
					<input id="money" name="money" placeholder="输入金额" class="m-app-input-money" />
				</span>
			</div>
			<div class="m-app-line">
				<a href="javascript:void(0);" onclick="submitOrder();" class="m-app-btn">微信支付</a>
			</div>
		</form>
	</div>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<script type="text/javascript">
		$(function(){
			$("#money").focus();
			
			// 校验微信版本
			var wechatInfo = navigator.userAgent.match(/MicroMessenger\/([\d\.]+)/i) ;
			if( !wechatInfo ) {
			    alert("订单支付仅支持微信") ;
			} else if ( wechatInfo[1] < "5.0" ) {
			    alert("订单支付仅支持微信5.0以上版本") ;
			}
		});
		function submitOrder() {
			var money = $.trim($("#money").val());
			var numReg = /^(0|([1-9]\d*))(\.\d+)?$/;
			if (isBlank(money)) {
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