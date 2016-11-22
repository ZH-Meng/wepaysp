<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="com.zbsp.wepaysp.manage.web.security.ManageUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>微盘支付后台管理系统</title>
</head>
<%
	ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
%>
<frameset rows="77,*" frameborder="no" border="0" framespacing="0">
	<frame src="<%=request.getContextPath()%>/resources/include/top.jsp" name="topFrame" scrolling="no" noresize="noresize" id="topFrame" title="topFrame" />
		<frameset cols="250,22,*" frameborder="no" border="0" framespacing="0" id="contentFrame">
			<frame src="<%=request.getContextPath()%>/resources/leftMenu.action" name="leftFrame" scrolling="auto" noresize="noresize" id="leftFrame" style="overflow-x:hidden;"/>
			<frame src="<%=request.getContextPath()%>/resources/include/control.jsp" name="lineFrame" scrolling="no" noresize="noresize" id="lineFrame" style="overflow-x:hidden;"/>
			<%-- <frame src="<%=request.getContextPath()%><%= manageUser.getRoleIndex() %>" name="mainFrame" scrolling="auto"id="mainFrame" /> --%>
			<frame src="" name="mainFrame" scrolling="auto"id="mainFrame" />
  		</frameset>
</frameset>
<noframes>
</noframes>

</html>