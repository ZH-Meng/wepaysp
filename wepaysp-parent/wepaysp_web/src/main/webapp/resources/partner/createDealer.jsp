<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>创建商户</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：代理商管理&gt;商户信息管理&gt;创建商户</div>
	    <div class="bgtj">
	    	<form action="<%=request.getContextPath()%>/resources/partner/dealermanage!createDealer.action" method="post" id="dealerForm">
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
	                        	<tr>
	                            	<th>登录名</th>
	                                <td><s:textfield id="loginId" maxlength="20" name="dealerVO.loginId" /><span class="tj_bt">*</span><span>1-20位长度的字母或数字，保存后不能修改！</span></td>
	                            </tr>
	                            <tr>
	                            	 <th>登录密码</th>
	                                <td><s:password id="loginPwd" maxlength="20" name="dealerVO.loginPwd" /><span class="tj_bt">*</span><span>6-20位长度的字母或数字！</span></td>
	                            </tr>
	                            <tr>
	                                <th>分润比率</th>
	                                <td><s:textfield id="feeRate" maxlength="20" name="dealerVO.feeRate" /><span class="tj_bt">*</span><span>分润费率只能填正整数，例如：千分之三点八填38  千分之四填40！</span></td>
	                            </tr>
	                            <tr>
	                            	<th>联系人</th>
	                                <td><s:textfield id="contactor" maxlength="32" name="dealerVO.contactor" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>公司名称</th>
	                                <td><s:textfield id="company" maxlength="32" name="dealerVO.company" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>手机</th>
	                                <td><s:textfield id="moblieNumber" maxlength="32" name="dealerVO.moblieNumber" /><span class="tj_bt">*</span><span>比如：13111110101</span></td>
	                            </tr>
	                            <tr>
	                            	<th>固定电话</th>
	                                <td><s:textfield id="telephone" maxlength="32" name="dealerVO.telephone" /><span>比如： 010-88888888</span></td>
	                            </tr>
	                             <tr>
	                            	<th>qq</th>
	                                <td><s:textfield id="qqNumber" maxlength="16" name="dealerVO.qqNumber" /></td>
	                            </tr>
	                           	<tr>
	                            	<th>邮箱</th>
	                                <td><s:textfield id="email" maxlength="16" name="dealerVO.email" /></td>
	                            </tr>
	                            <tr>
	                            	<th>状态</th>
	                                <td>
	                                	<s:select list="#{1:'未使用',2:'使用中',3:'冻结'}" listKey="key" listValue="value" name="dealerVO.state"  id="state" headerKey="" headerValue="请选择"/>
	                                	<%-- <select id="state" name="dealerVO.state">
	                                		<option value="">请选择</option>
	                                		<option value="1">未使用</option>
	                                		<option value="2">使用中</option>
	                                		<option value="3">冻结</option>
	                                	</select> --%>
	                                	 <span class="tj_bt">*</span>
	                                </td>
	                            </tr>	
	                            <tr>
	                            	<th>地址</th>
	                                <td><s:textfield id="address" maxlength="32" name="dealerVO.address" /></td>
	                            </tr>
	                            <tr>
	                            	<th>技术联系人</th>
	                                <td><s:textfield id="techSupportPerson" maxlength="32" name="dealerVO.techSupportPerson" /></td>
	                            </tr>
	                            <tr>
	                            	<th>技术联系电话</th>
	                                <td><s:textfield id="techSupportPhone" maxlength="32" name="dealerVO.techSupportPhone" /></td>
	                            </tr>
	                            <tr>
	                                <th>备注</th>
	                                <td>
	                                	<s:textarea id="remark" cols="25" rows="4" name="dealerVO.remark"></s:textarea>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="createPartner();return false;">新增</a><a onclick="returnList()" href="javascript:void(0);">返回列表</a>
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
			$("#loginId").focus();
		});	
	
		//TODO 校验QQ
		function createPartner() {
			var loginId = $("#loginId").val();
			var loginPwd = $("#loginPwd").val();
			var contactor = $("#contactor").val();
			var moblieNumber = $("#moblieNumber").val();
			var state = $("#state").val();
			var feeRate = $("#feeRate").val();
			var company = $("#company").val();			
			var remark = $("#remark").val();
			
			var telephone = $("#telephone").val();
			var qqNumber = $("#qqNumber").val();
			var email = $("#email").val();
			var email = $("#email").val();
			var techSupportPhone = $("#techSupportPhone").val();
			
			if (isBlank(loginId)) {
				alert("登录名不能为空！");
				$("#loginId").focus();
				return false;
			} else if (!isAlphaNumeric(loginId)) {
				alert("登录名应为1-20位长度的字母或数字！");
				$("#loginId").focus();
				return false;
			} else if (isBlank(loginPwd)) {
				alert("登陆密码不能为空！");
				$("#loginPwd").focus();
				return false;
			} else if (!isAlphaNumeric(loginPwd) || loginPwd.length<6) {
				alert("登陆密码应为6-20位长度的字母或数字！");
				$("#loginPwd").focus();
				return false;
			} else if (isBlank(feeRate)) {
				alert("分润费率不能为空！");
				$("#feeRate").focus();
				return false;
			} else if (!isPositiveInteger1(feeRate)) {
				alert("分润费率只能填正整数！");
				$("#feeRate").focus();
				return false;
			} else if (isBlank(contactor)) {
				alert("联系人不能为空！");
				$("#contactor").focus();
				return false;
			} else if (isBlank(company)) {
				alert("公司不能为空！");
				$("#company").focus();
				return false;
			} else if (isBlank(moblieNumber)) {
				alert("手机号码不能为空！");
				$("#moblieNumber").focus();
				return false;
			} else if (!isMobile(moblieNumber)) {
				alert("手机号码格式不正确！");
				$("#moblieNumber").focus();
				return false;
			} else if (!isBlank(telephone) && !islineTel(telephone)) {
				alert("固定电话应为7-18位数字或-！");
				$("#telephone").focus();
				return false;
			} else if (!isBlank(email) && !isEmail(email)) {
				alert("邮箱格式不正确！");
				$("#email").focus();
				return false;
			} else if (isBlank(state)) {
				alert("状态不能为空！");
				$("#state").focus();
				return false;
			} else if (!isBlank(techSupportPhone) && (!isMobile(moblieNumber) || !islineTel(techSupportPhone))) {
				alert("技术联系电话应为固定电话或者手机！");
				$("#telephone").focus();
				return false;
			} else if (remark.length > 256) {
				alert("备注长度不能大于256！");
				$("#remark").focus();
				return false;
			}
			
			if (!window.confirm("确认新增？")) {
				return false;
			}
			
			$("#dealerForm").submit();
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