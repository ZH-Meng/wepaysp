<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<title>访问失败</title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/weui.min.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/font/iconfont.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/main.css"/>
	</head>
	<body>
		<p class="error-title">
			 <i class="weui_icon_warn"></i>访问失败
		</p>		
		<div class="succ-info">
			<div class="error-bottom">
				<img class="yzf-img" src="<%=request.getContextPath()%>/resources/images/pay-error.png" alt="" />
			</div>			
			<p class="info2">
				<c:choose> 
					<c:when test="${errResult != null && errResult.errDesc !=  '' }">   
						${errResult.errDesc  }  
					</c:when> 
					<c:otherwise>   
						访问失败  
					</c:otherwise> 
				</c:choose> 
			</p>
		</div>
	</body>
</html>
