<%--
/*
* 
* 创建者：杨帆
* 创建日期：2015年6月12日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>创建角色</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：角色管理&gt;创建角色</div>
	    <div class="bgtj">
	    	<form action="<%=request.getContextPath()%>/resources/usermanage/rolemanage!createRole.action" method="post" id="roleForm">
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
	                        	<tr>
	                            	<th>角色代码</th>
	                                <td><s:textfield id="roleId" maxlength="20" name="sysRole.roleId" /><span class="tj_bt">*</span></td>
	                                <th>角色名称</th>
	                                <td><s:textfield id="roleName" maxlength="20" name="sysRole.roleName" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>角色级别</th>
	                                <td>
	                                	<select id="roleLevel" name="sysRole.roleLevel">
	                                		<option value="">请选择</option>
	                                		<option value="0">应用级别</option>
	                                		<option value="1">管理级别</option>
	                                	</select>
	                                	 <span class="tj_bt">*</span>
	                                </td>
	                                <th>状态</th>
	                                <td>
	                                	<select name="sysRole.state">
	                                		<option value="0">正常</option>
	                                		<option value="1">冻结</option>
	                                		<option value="2">注销</option>
	                                	</select>
	                                    <span class="tj_bt">*</span>
	                                </td>
	                            </tr>
	                            <tr>
	                            	<th>角色首页</th>
	                                <td colspan="3"><s:textfield id="roleIndex" style="width:779px" maxlength="100" name="sysRole.roleIndex" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>角色描述</th>
	                                <td>
	                                	<s:textarea id="description" cols="25" rows="4" name="sysRole.description"></s:textarea>
	                                </td>
	                                <th>备注</th>
	                                <td>
	                                	<s:textarea id="remark" cols="25" rows="4" name="sysRole.remark"></s:textarea>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="createRole();return false;">新增</a><a onclick="returnList()" href="javascript:void(0);">返回列表</a>
	                </li>
	            </ul>
	        </form>
	        <s:form method="post">
				<div style="display: none;">
					<s:include value="/resources/include/page.jsp"/>
				</div>
			</s:form>
	    </div>
	</div>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#roleId").focus();
		});	
	
		function createRole() {
			var roleId = $("#roleId").val();
			var roleName = $("#roleName").val();
			var description = $("#description").val();
			var remark = $("#remark").val();
			var roleIndex = $("#roleIndex").val();
			var roleLevel = $("#roleLevel").val();
			
			if (isBlank(roleId)) {
				alert("角色代码不能为空！");
				$("#roleId").focus();
				return false;
			} else if (!isAlphaNumeric(roleId)) {
				alert("角色代码应为1-20位长度的字母或数字！");
				$("#roleId").focus();
				return false;
			} else if (isBlank(roleName)) {
				alert("角色名称不能为空！");
				$("#roleName").focus();
				return false;
			} else if (!isWord(roleName)) {
				alert("角色名称只能输入数字字母或汉字！");
				$("#roleName").focus();
				return false;
			} else if (description.length > 100) {
				alert("角色描述长度不能大于100！");
				$("#description").focus();
				return false;
			} else if (remark.length > 100) {
				alert("备注长度不能大于100！");
				$("#remark").focus();
				return false;
			} else if (isBlank(roleIndex)) {
				alert("角色首页不能为空！");
				$("#roleIndex").focus();
				return false;
			} else if (isBlank(roleLevel)) {
				alert("请选择角色级别！");
				$("#roleLevel").focus();
				return false;
			}
			
			if (!window.confirm("确认新增？")) {
				return false;
			}
			
			$("#roleForm").submit();
		}
		
		function returnList() {
			var formObj = document.forms[1];
			var actionURL = formObj.action;
			var lastPoint = actionURL.lastIndexOf(".");
			var lastLine = actionURL.lastIndexOf("/");

			if (actionURL.indexOf("!") > 0) {
				lastPoint = actionURL.lastIndexOf("!");
			}

			var prefix = actionURL.substring(lastLine + 1, lastPoint);

			formObj.action = prefix + "!goCurrent.action";
			formObj.submit();
		}
	</script>	
	<s:property value="#request.messageBean.alertMessage" escape="false" />
</body>
</html>