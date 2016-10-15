<%--
/*
* 
* 创建者：马宗旺
* 创建日期：2015年6月9日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>重置用户密码</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj" onkeydown="formkeydown();">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：系统用户管理&gt;重置用户密码</div>
		<div class="bgtj">
			<form id="modifyForm" action="<%=request.getContextPath()%>/resources/usermanage/userrestpwd!restUserPwd.action" method="post">
				<ul class="tj_title">
                	<li>修改密码</li>
            	</ul>
				<s:hidden name="sysUser.iwoid"></s:hidden>
				<ul class="bg_tjtab bg-block">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>登录名</th>
									<td><s:property value="sysUser.userId" /></td>
								</tr>
								<tr>
									<th>新密码</th>
									<td>
										<input type="password" id="newPwd" name="sysUser.loginPwd" maxlength="12" /><span class="tj_bt">*</span>
									</td>
								</tr>
								<tr>
									<th>确认新密码</th>
									<td>
										<input type="password" id="confirmPwd" maxlength="12" value="" /><span class="tj_bt">*</span>
									</td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<a id="submitForm" href="javascript:void(0);" onclick="modify();" style="width: 100px;">重置密码</a>
						<a class="mgl-20" href="javascript:void(0);" onclick="goHistory();" style="width: 100px;">返回列表</a>
						<input type="hidden" name="userId" value="${userId}" />
						<input type="hidden" name="userName" value="${userName}" />
						<%-- <input type="hidden" name="roleName" value="${roleName}" /> --%>
						<input type="hidden" name="roleOid" value="${roleOid}" />
						<input type="hidden" name="state" value="${state}" />
						<div style="display: none;">
							<s:include value="/resources/include/page.jsp"/>
						</div>
					</li>
					
				</ul>
			</form>
		</div>
		<s:property value="#request.messageBean.alertMessage" escape="false" />
	</div>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<script type="text/javascript">
		function modify() {
			var mess = checkPwd();
			if (mess == "SUCC") {
				if (confirm("确认修改？")) {
					$("#modifyForm").submit();
				}
			} else {
				alert(mess);
			}
		}
		
		function checkPwd() {
			var newPwd = $("#newPwd").val();
			var confirmPwd = $("#confirmPwd").val();
			if (!checkLength(newPwd))
				return "新密码长度应在6-12位之间";
			if (!checkLength(confirmPwd))
				return "确认新密码长度应在6-12位之间";
			if (newPwd != confirmPwd)
				return "新密码和确认新密码不一致";
			if(!isAlphaNumeric(newPwd)){
				return "密码只能为字母或数字。"
			}
			return "SUCC";
		}
		
		function checkLength(pwd) {
			if (pwd.length<6 || pwd.length>12) {
				return false;
			} else {
				return true;
			}
		}
		
		function formkeydown() {
			if (window.event) {
				if (window.event.keyCode == 13) {
					$("#submitForm").click();
				}
			} else {
				if (arguments[0].charCode == 13) {
					$("#submitForm").click();
				}
			}
		}
		
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