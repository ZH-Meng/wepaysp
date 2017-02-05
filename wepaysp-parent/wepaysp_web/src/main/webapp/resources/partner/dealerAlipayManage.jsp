<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>商户支付宝管理</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<style>
		.qrCode_title{display:block;font-weight:bold;}
		#div_auth div{padding: 5px;}
	</style>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：代理商管理&gt;商户信息管理&gt;商户支付宝管理</div>
		<div class="bgtj">
			<ul class="tj_title">
				<li>支付宝授权</li>
			</ul>
			<div id="div_auth">
				<div>
					邀请支付宝用户授权给应用的URL(在PC电脑上授权)：<br/><a href="${alipayAuthUrl}">${alipayAuthUrl}</a>
				</div>
				<div>
					邀请支付宝用户授权给应用的二维码(用支付宝APP扫码授权)：
				</div> 
				<div>
					<img src="<%=request.getContextPath()%>/resources/partner/dealermanage!loadAliapyAppAuthCode.action?dealerOid=${dealerOid}&devMode=${devMode}" alt="支付宝授权二维码" width="200" height="200"/>
					<span class="qrCode_title">支付宝授权二维码</span>
				</div>
				<div>
					状态：已授权/未授权
				</div>
			</div>
		</div>
		
	</div>
	
	<s:property value="#request.messageBean.alertMessage" escape="false" />
</body>
</html>