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
		<title>支付宝应用授权结果</title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/weui.min.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/font/iconfont.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/main.css"/>
	</head>
</head>
<body>
	<p class="succ-title">
		<i class="weui_icon_msg weui_icon_success"></i>授权成功
	</p>
	<div class="succ-info">
		<p class="info1">${appAuthDetailsVO.dealerName }</p>
		<div class="break-line"></div>
		<div class="succ-item">
			授权应用<span class="fr">${appAuthDetailsVO.appName } [${appAuthDetailsVO.appId} ]</span>
		</div>
		<div class="succ-item">
			授权生效时间<span class="fr"><fmt:formatDate value="${appAuthDetailsVO.authStart}" pattern="yyyy-MM-dd HH:mm:ss" /></span>
		</div>
		<%-- <div class="succ-item">
			授权失效时间<span class="fr"><fmt:formatDate value="${appAuthDetailsVO.authEnd}" pattern="yyyy-MM-dd HH:mm:ss" /></span>
		</div> --%>
   	 </div> 
</body>
</html>