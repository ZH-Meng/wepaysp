<%--
/*
* 
* 创建者：侯建玮
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
	<title>登录密码修改</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<style>
		.bg_tjall th {width: 40%;}
	</style>
</head>
<body class="bgbj" onkeydown="formkeydown();">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：修改登录密码</div>
		<div class="bgtj">
			<form id="modifyForm" action="<%=request.getContextPath()%>/resources/user/modifyPwd.action" method="post">
				<ul class="tj_title">
	                <li>修改密码</li>
	            </ul>
				<ul class="bg_tjtab bg-block">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>旧密码</th>
									<td>
										<input type="password" id="oldPwd" name="oldPwd" maxlength="12" value="" /><span class="tj_bt">*</span>
									</td>
								</tr>
								<tr>
									<th>新密码</th>
									<td >
										<input type="password" id="newPwd" name="newPwd" maxlength="12" value="" /><span class="tj_bt">*</span>
									</td>
								</tr>
								<tr>
									<th>重新确认新密码</th>
									<td >
										<input type="password" id="confirmPwd" name="confirmPwd" maxlength="12" value="" /><span class="tj_bt">*</span>
									</td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<a id="submitForm" href="javascript:void(0);" onclick="modify();" style="width: 100px;">保存</a>
						<a class="mgl-20" onclick="reset();" href="javascript:void(0);" style="width: 100px;">重写</a>
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
				if (confirm("确认修改登录密码？")) {
					$("#modifyForm").submit();
				}
			} else {
				alert(mess);
			}
		}
		
		function checkPwd() {
			var oldPwd = $("#oldPwd").val();
			var newPwd = $("#newPwd").val();
			var confirmPwd = $("#confirmPwd").val();
			if(oldPwd==null || oldPwd==""){
				return "请输入旧密码！";
			}
			if(newPwd==null || newPwd==""){
				return "请输入新密码！";
			}
			if(confirmPwd==null || confirmPwd==""){
				return "请输入重新确认新密码！";
			}
			if(!isAlphaNumeric(newPwd)){
				return "新密码只能为字母或数字";
			}
			
			if (!checkLength(oldPwd))
				return "旧密码输入错误！";
			if (!checkLength(newPwd))
				return "新密码长度应在6-12位之间";
			if (!checkLength(confirmPwd))
				return "确认码长度应在6-12位之间";
			if (newPwd != confirmPwd)
				return "两次输入的新密码不一致，请重新输入！";
			return "SUCC";
		}
		
		function checkLength(pwd) {
			if (pwd.length<6 || pwd.length>12) {
				return false;
			} else {
				return true;
			}
		}
		
		function reset() {
			$("#modifyForm")[0].reset();
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
	</script>
</body>
</html>
