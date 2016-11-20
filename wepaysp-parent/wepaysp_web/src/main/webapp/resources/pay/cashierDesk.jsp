<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>收款</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<style>
		.cashier-wrapper{
			width:400px;
			height: 250px;
			margin: 0 auto;
			border:0 solid #CDCDCD;
			background-color: #fff;
			box-shadow:0px 1px 5px RGBA(0,0,0,0.5);
		}
		.cashier-container {
			width:250px;
			height:200px;
			margin: 0 auto;
			padding-top: 20px;
		}
		.cashier-container .cashier-item{
			padding: 2px 0;
			 border: 1px solid #BAC7D2;
			 background-color:#ECF5FA;
			 /*box-shadow: 0 0 2px RGBA(0,46,115,.25) inset;*/
			 height: 30px;
			 margin-top: 10px;
			 position: relative;
		}
		.cashier-item-icon {
			position: absolute;
			left: 0;
			top:2;
			width: 14px;
			display: inline-block;
			text-align: center;
			vertical-align: middle;
			height: 30px;
			line-height: 30px;
			background-color:#fff;
		}
		.input-cashier{
			width:100%;
			height: 30px;
			padding: 0px;
			border: 0px;
			line-height: 30px;
		}
		.cashier-button {
			vertical-align:middle;
			display:inline-block;
			width:100%;
			height:30px;
			line-height:30px;
			text-align:center;
			cursor:pointer;
			border:none;
		}
		.cashier-button:ACTIVE {
			color: #000;
		}
		.cashier-code{
			color: #999;
		}
		.cashier-code-active{
			color:#000;
		}
	</style>
</head>
<body class="bgbj" onkeydown="formkeydown();">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：资金结算&gt;收银台</div>
		<div class="cashier-wrapper">
			<div class="cashier-container">
				<form id="cashierForm" method="post" action="<%=request.getContextPath()%>/resources/pay/payment!cashier.action" >
					<h1 align="center">收银台</h1>
					<div class="cashier-item" style="padding-left: 14px;">
						<span class="cashier-item-icon" >￥</span>
						<s:textfield name="money" id="money" cssClass="input-cashier"/>
					</div>
					<div class="cashier-item">
						<s:textfield name="authCode" id="code" cssClass="input-cashier cashier-code" value="请输入收款码" onfocus="codeFocus();" onblur="codeBlur();"/>
					</div>
					<div class="cashier-item">
						<a href="javascript:void(0);" onclick="submit();" id="submitForm" class="cashier-button">输入金额按回车键完成</a>
					</div>
				</form>
			</div>
		</div>
    	<div class="bgtable">
    		<ul class="title_text">
		       <li>收款数据</li>
			</ul>
            <ul class="bg_all">
                <li class="bg_table bg_table1">
                    <table class="bg_odd">
                        <thead>
                            <tr>
                                <th class="six">序号</th>
                                <th>订单号</th>
	                            <th>设备号</th>
                                <th>支付方式</th>
                                <th>交易银行</th>
                                <th>订单状态</th>
                                <th>订单金额</th>
                                <th>时间</th>
                                <th>退款</th>
                                <th>打印</th>
                            </tr>
                        </thead>
                        <tbody>
                        <s:if test="weixinPayDetailsVoList != null && weixinPayDetailsVoList.size() > 0">
		  					<s:iterator value="weixinPayDetailsVoList" var="weixinPayDetailsVo" status="rowStatus">
					  		<tr>
					  			<td>
					  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
					  			</td>
					  			<td title="<s:property value="#weixinPayDetailsVo.outTradeNo" />">
					  				<s:property value="#weixinPayDetailsVo.outTradeNo" />
					  			</td>
						  		<td title="<s:property value="#weixinPayDetailsVo.deviceInfo" />">
					  				<s:property value="#weixinPayDetailsVo.deviceInfo" />
					  			</td>
					  			
								<s:if test="#weixinPayDetailsVo.payType == 1">
					  				<s:set var="payTypeStr">刷卡支付</s:set>
					  			</s:if>
					  			<s:elseif test="#weixinPayDetailsVo.payType == 2">
					  				<s:set var="payTypeStr">公众号支付</s:set>
					  			</s:elseif>
					  			<s:elseif test="#weixinPayDetailsVo.payType == 3">
					  				<s:set var="payTypeStr">扫码支付</s:set>
								</s:elseif>
								<s:elseif test="#weixinPayDetailsVo.payType == 4">
									<s:set var="payTypeStr">微信买单</s:set>
								</s:elseif>
					  			<td title="<s:property value="#payTypeStr" />">
					  				<s:property value="#payTypeStr" />
					  			</td>
					  			<td title="<s:property value="#weixinPayDetailsVo.bankType" />">
					  				<s:property value="#weixinPayDetailsVo.bankType" />
					  			</td>
					  			<td title="<s:property value="#weixinPayDetailsVo.resultCode" />">
					  				<s:property value="#weixinPayDetailsVo.resultCode" />
					  			</td>
					  			<td class="bgright" title="<fmt:formatNumber value="${weixinPayDetailsVo.totalFee/100}" pattern="###,###,###,###.00"/>">
					  				<fmt:formatNumber value="${weixinPayDetailsVo.totalFee/100}" pattern="###,###,###,###.00"/>
					  			</td>
					  			<td title="<s:date name="#weixinPayDetailsVo.transBeginTime" format="yyyy-MM-dd HH:mm:ss"/>">
					  				<s:date name="#weixinPayDetailsVo.transBeginTime" format="yyyy-MM-dd HH:mm:ss"/>
					  			</td>
					  			<td class="bgright" title="<fmt:formatNumber value="${weixinPayDetailsVo.refundFee/100}" pattern="###,###,###,###.00"/>">
						  				<fmt:formatNumber value="${weixinPayDetailsVo.refundFee/100}" pattern="###,###,###,###.00"/>
						  			</td>
					  			<td title="<s:property value="#weixinPayDetailsVo.printFlag" />">
					  				<s:property value="#weixinPayDetailsVo.printFlag" />
					  			</td>
					  		</tr>
					  		</s:iterator>
		  				</s:if>
                    	</tbody>
               		</table>
                </li>
            </ul>
            <ul>
            	<li class="t-center">
                	<s:include value="/resources/include/noPage.jsp"></s:include>
                </li>
            </ul>
    	</div>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		$(function(){
			$("#money").focus();
		});
		function codeFocus() {
			var val = $("#code").val();
			if ("请输入收款码" == val) {
				$("#code").val("");
				$("#code").removeClass("cashier-code");
				$("#code").addClass("cashier-code-active");
			}
		}
		function codeBlur() {
			var val = $("#code").val();
			if ("" == $.trim(val)) {
				$("#code").removeClass("cashier-code-active");
				$("#code").addClass("cashier-code");
				$("#code").val("请输入收款码");
			}
		}
		
		function submit() {
			var money = $.trim($("#money").val());
			var code = $("#code").val();
			var numReg = /^(0|([1-9]\d*))(\.\d+)?$/;
			if (isBlank(money)) {
				alert('请输入金额！');
				return;
			} else if (!numReg.test(money)) {
				alert("金额必须大于0！");
				$("#money").focus();
				return;
			} else if (isBlank(code) || "请输入收款码" == code) {
				alert('请输入收款码！');
				return;
			}
			$("#cashierForm").submit();
		}
		
		function formkeydown(){
			if(window.event){
				if (window.event.keyCode == 13){
					$("#submitForm").click();
				}	
			} else {
				var event = arguments.callee.caller.arguments[0];
				if (event.keyCode == 13){
					$("#submitForm").click();
				}
			}
		}
	</script>
</body>
</html>