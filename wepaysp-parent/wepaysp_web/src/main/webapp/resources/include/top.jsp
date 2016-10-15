<%--
/*
* 
* 创建者：杨帆
* 创建日期：2015年6月9日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="com.zbsp.wepaysp.manage.web.security.ManageUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>省级积分服务管理后台</title>
	<link href="<%=request.getContextPath()%>/css/zxstyle.css" rel="stylesheet" />
</head>
<%
	ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
%>
<body class="bgbj">
	<div class="top">
	    <ul class="logocon clear">
	        <li class="top_logo"><img src="<%=request.getContextPath()%>/images/top-logo.jpg" alt="logo" /></li>
	        <li class="top_button clear">
	        	<p class="topuser"><a href="javascript:modifyPwd(0);" onclick="modifyPwd();"></a></p>
	        	<p>
            		<span class="toppass"><%= manageUser.getUsername() %></span>
            		<a class="topexit" href="javascript:void(0);" onclick="logout();">安全退出</a>
            	</p>
	        </li>
	    </ul>
	</div>
	<script type="text/javascript">
		function logout(){
			if (confirm("确认退出？")) {
				window.top.location.href = '<%=request.getContextPath()%>/logout';
			}
		}
		
		function modifyPwd(){
			window.parent.mainFrame.location.href = '<%=request.getContextPath()%>/resources/user/modifyPwd.jsp';
		}
	</script>
</body>
</html>