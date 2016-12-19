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
		<title>访问失败</title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/weui.min.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/font/iconfont.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/main.css"/>
	</head>
	<body>
		<p class="error-title">
			 <i class="weui_icon_warn"></i>访问失败
		</p>		
		<div class="succ-info">
			<div class="error-bottom">
				<img class="yzf-img" src="<%=request.getContextPath()%>/images/pay-error.png" alt="" />
			</div>			
			<p class="info2">
				<s:if test="errResult != null && errResult.errDesc !=  '' ">
					${errResult.errDesc  }
				</s:if>
				<s:else>
					访问失败
				</s:else>
			</p>
		</div>
	</body>
</html>
