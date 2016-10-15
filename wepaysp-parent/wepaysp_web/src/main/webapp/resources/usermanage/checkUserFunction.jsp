<%--
/*
* 
* 创建者：侯建玮
* 创建日期：2015年6月12日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>用户权限查询</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/tools/dtree/dtree.css" type="text/css" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/dtree/dtree.js"></script>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：系统用户管理&gt;用户权限查询</div>
		<div class="bgtj">
			<div class="clear juese_m">
				<div class="header-1">角色名称：<s:property value="checkUserId" escape="false" /></div>
				<div class="block">
					<script type="text/javascript">
						d = new dTree('d', '<%=request.getContextPath()%>', '/tools/dtree/', '<s:property value="functionStr" escape="false" />', 'mainFrame');
						d.build();
						document.write(d);
					</script>
				</div>
				<div style="text-align: center;">
					<input class="btn btn-1" type="button" onclick="goHistory();" value="返回列表" />
				</div>
				<br />
			</div>
		</div>
	</div>
	<s:form method="post">
		<input type="hidden" name="userId" value="${requestScope.userId}" />
		<input type="hidden" name="userName" value="${requestScope.userName}" />
		<input type="hidden" name="roleName" value="${requestScope.roleName}" />
		<input type="hidden" name="roleOid" value="${requestScope.roleOid}" />
		<input type="hidden" name="state" value="${requestScope.state}" />
		<div style="display: none;">
			<s:include value="/resources/include/page.jsp"/>
		</div>
	</s:form>
	<script type="text/javascript">
		function goHistory() {
			invokeAction('goCurrent');
		}
		
		function invokeAction(methodName) {
			var formObj = document.forms[0];
			var actionURL = formObj.action;
			var lastPoint = actionURL.lastIndexOf(".");
			var lastLine = actionURL.lastIndexOf("/");

			if (actionURL.indexOf("!") > 0) {
				lastPoint = actionURL.lastIndexOf("!");
			}

			var prefix = actionURL.substring(lastLine + 1, lastPoint);

			formObj.action = prefix + "!" + methodName + ".action";
			formObj.submit();
		}
	</script>
</body>
</html>
