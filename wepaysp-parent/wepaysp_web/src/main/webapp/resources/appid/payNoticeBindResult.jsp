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
		<title>绑定结果</title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/weui.min.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/font/iconfont.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/main.css"/>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	</head>
	<body>
	    <s:if test="bindResult.result == 'success' ">
			<p class="succ-title">
				<i class="weui_icon_msg weui_icon_success"></i>绑定成功
			</p>
			<div class="succ-info">
				<p class="info1">${payNoticeBindWeixinVO.nickname }</p>
				<div class="break-line"></div>
				<div class="succ-item">
					门&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;店<span class="fr">${payNoticeBindWeixinVO.storeName }</span>
				</div>
				<s:if test="bindType == 2">
					<div class="succ-item">
						收银员<span class="fr">${payNoticeBindWeixinVO.payDealerEmployeeName }</span>
					</div>
				</s:if>
				<div class="succ-item">
					绑定时间<span class="fr"><s:date name="payNoticeBindWeixinVO.createTime" format="yyyy-MM-dd HH:mm:ss"/></span>
				</div>
	     	 </div>
		</s:if>
		<s:elseif test="bindResult.result == 'bound' ">
			<p class="error-title">
				 <i class="weui_icon_warn"></i>已绑定
			</p>		
			<div class="succ-info">
				<div class="error-bottom">
					<img class="yzf-img" src="<%=request.getContextPath()%>/images/pay-error.png" alt="" />
				</div>			
				<p class="info2">${bindResult.desc }</p>
			</div>
		</s:elseif>
		<s:else>
			<p class="error-title">
				 <i class="weui_icon_warn"></i>绑定失败
			</p>		
			<div class="succ-info">
				<div class="error-bottom">
					<img class="yzf-img" src="<%=request.getContextPath()%>/images/pay-error.png" alt="" />
				</div>			
				<p class="info2">绑定失败请重试</p>
			</div>
		</s:else>
	</body>
</html>
