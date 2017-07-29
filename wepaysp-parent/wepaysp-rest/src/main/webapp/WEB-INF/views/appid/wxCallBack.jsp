<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>微信支付</title>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/weui.min.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/font/iconfont.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/main.css"/>
	<script>
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "https://hm.baidu.com/hm.js?f3f509231e1e5858b8e914790914f29e";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	</script>
	<script  src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script>
	<script src="<%=request.getContextPath()%>/resources/js/keyboard.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/check.js"></script>
</head>
<body>
	<form id="orderForm" >
		<input type="hidden" value="${callBackVO.dealerOid}"  name="dealerOid" id="dealerOid" />
		<input type="hidden" value="${callBackVO.storeOid}"  name="storeOid" id="storeOid" />
		<input type="hidden" value="${callBackVO.dealerEmployeeOid}"  name="dealerEmployeeOid" id="dealerEmployeeOid" />
		<input type="hidden" value="${callBackVO.openid}"  name="openid" id="openid" />

		<div class="weui-panel">
			<div class="company-info">
				<p class="title1">${callBackVO.dealerName}订单</p>
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
			
			
			pushHistory();
		    window.addEventListener("popstate", function(e) { 
		        //alert("我监听到了浏览器的返回按钮事件啦");//根据自己的需求实现自己的功能 
		        WeixinJSBridge.call('closeWindow');
		    }, false); 
		    function pushHistory() { 
		        var state = { 
		            title: "title", 
		            url: "#"
		        }; 
		        window.history.pushState(state, "title", "#"); 
		    } 
		});

		function submitOrder() {
			var dealerOid = $.trim($("#dealerOid").val());
			var openid = $.trim($("#openid").val());				
			var money = $.trim($("#money").val());
			var numReg = /^(0|([1-9]\d*))(\.\d+)?$/;
			if (isBlank(dealerOid) || isBlank(openid)) {
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
			
			$.post("<%=request.getContextPath()%>/appid/pay/createOrder",
				  {
					"dealerOid":dealerOid,"openid":openid,"money":money,
				  	"storeOid":$.trim($("#storeOid").val()),
				  	"dealerEmployeeOid":$.trim($("#dealerEmployeeOid").val())
				  },
				  function(data,status){
				    if (status == "success" || status == "SUCCESS") {
				    	if (data.result == "success" || data.result == "SUCCESS") {
					    	callPay(data.jSPayReqData, data.weixinPayDetailOid);
				    	} else {
				    		alert("系统错误，下单失败");
				    		/* if (data.desc != null && data.desc != "") {
					    		alert(data.desc);
				    		} else {
				    			alert("系统错误，下单失败");
				    		} */
				    	}
				    } else {
				    	alert("系统异常，请重试！");
				    }
			  }, "json");
		}
		
		function onBridgeReady(param, payDetailOid) {
		    WeixinJSBridge.invoke(
		       'getBrandWCPayRequest', {
		           "appId" : param.appId,     //公众号名称，由商户传入     
		           "timeStamp" : param.timeStamp,         //时间戳，自1970年以来的秒数     
		           "nonceStr" : param.nonceStr, //随机串     
		           "package" : param.dataPackage,     
		           "signType" : "MD5",         //微信签名方式：     
		           "paySign" : param.paySign //微信签名 
		        },
		       
		        function(res){     
		            if (res.err_msg == "get_brand_wcpay_request:ok" ) {// 微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
	                	window.location.href="<%=request.getContextPath()%>/appid/pay/jsPayResult?weixinPayDetailOid="+payDetailOid+"&payResult=ok";
		            } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
			           	window.location.href="<%=request.getContextPath()%>/appid/pay/jsPayResult?weixinPayDetailOid="+payDetailOid+"&payResult=cancel";
		            } else {
		            	alert('支付失败');
			           	window.location.href="<%=request.getContextPath()%>/appid/pay/jsPayResult?weixinPayDetailOid="+payDetailOid+"&payResult=error";
		            }
		        }
		   ); 
		}
		
		function callPay(param, payDetailOid) {
			if (typeof WeixinJSBridge == "undefined"){
				   if( document.addEventListener ){
				       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
				   }else if (document.attachEvent){
				       document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
				       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
				   }
				}else{
				   onBridgeReady(param, payDetailOid);
			}
		}
	</script>
</body>
</html>