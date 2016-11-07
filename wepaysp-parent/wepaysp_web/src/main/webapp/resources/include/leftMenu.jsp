<%--
/*
* 
* 创建者：杨帆
* 创建日期：2015年6月10日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="com.zbsp.wepaysp.manage.web.security.ManageUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>功能菜单</title>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/zxstyle.css" type="text/css" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/tree.js"></script>
</head>
<%
	ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
%>
<body class="menu_bj" style="overflow-x:hidden;">
		<div class="leftmenu">
    		<div class="leftmenuone">
        		<ul class="topmenu">
        		</ul>
        	</div>
        </div>
	<script type="text/javascript">
		$(".leftmenu").height($(window).height());
		var obj = <s:property value="functionStr" escape="false" />;
			var reqHead = '<%=request.getContextPath()%>';
			$(function(){
				var defaultSet = {
					//数据
					data:obj,
					//target属性
					tar:"mainFrame",
					//显示的节点
					pos:".leftmenuone",
					reqHead :reqHead,
					//显示到几级菜单
					limit:"2"
					<%-- ,jumpTo:"<%= manageUser.getRoleIndex() %>" --%>
				}
				stc.tree.init(defaultSet);
			});
		//iniNav(obj, '<%=request.getContextPath()%>', 'mainFrame');
		
		
	</script>
</body>
</html>