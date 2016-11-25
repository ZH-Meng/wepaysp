<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>微信安全支付</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
</head>
<body>
	<s:if test="tradeStatus == 1">
		支付成功<br/>
		商户：${weixinPayDetailsVO.dealerName }<br/>
		支付金额：<fmt:formatNumber value="${weixinPayDetailsVO.totalFee/100}" pattern="###,###,###,##0.00"/>
	</s:if>
	<s:elseif test="tradeStatus == 0">
		订单处理中
	</s:elseif>
	<s:elseif test="tradeStatus == 4">
		订单已取消
	</s:elseif>
	<s:elseif test="tradeStatus == 2">
		支付失败，请重试！
	</s:elseif>
</body>
</html>