<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>微信安全支付</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
	$(function(){
		callPay();
		
		function onBridgeReady() {
		    WeixinJSBridge.invoke(
		       'getBrandWCPayRequest', {
		           "appId" : "wx2421b1c4370ec43b",     //公众号名称，由商户传入     
		           "timeStamp" : "1395712654",         //时间戳，自1970年以来的秒数     
		           "nonceStr" : "e61463f8efa94090b1f366cccfbbb444", //随机串     
		           "package" : "prepay_id=u802345jgfjsdfgsdg888",     
		           "signType" : "MD5",         //微信签名方式：     
		           "paySign" : "70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名 
		        },
		       
		        function(res){     
		            if (res.err_msg == "get_brand_wcpay_request:ok" ) {// 微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
	        		alert("支付成功");
	                window.location.href="/student/student_booking";
		            } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
			           	alert("支付过程中用户取消");
			           	window.location.href="student_pay.jsp";
		            } else {
		                alert('支付失败');
		                window.location.href="student_pay.jsp";
		            }
		        }
		   ); 
		}
		
		function callPay() {
			if (typeof WeixinJSBridge == "undefined"){
				   if( document.addEventListener ){
				       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
				   }else if (document.attachEvent){
				       document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
				       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
				   }
				}else{
				   onBridgeReady();
			}
		}
		
	});
</script>
</head>
<body>
</body>
</html>