<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>修改代理商员工</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<style>
		.bg_tjall th {width: 40%;}
	</style>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：代理商员工管理&gt;员工管理&gt;修改代理商员工</div>
	    <div class="bgtj">
	    	<form action="<%=request.getContextPath()%>/resources/partner/partneremployeemanage!updatePartnerEmployee.action" method="post" id="partnerEmployeeForm">
	    		<s:hidden id="iwoid" name="partnerEmployeeVO.iwoid"/>
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
	                    		<tr>
	                            	<th>登录名</th>
	                                <td>
	                                	<s:textfield id="loginId" maxlength="20" name="partnerEmployeeVO.loginId" readonly="true"/>
		                                <span class="tj_bt">*</span>此项不可修改
	                                </td>
	                            </tr>
	                            <tr>
	                            	<th>员工编号</th>
	                                <td>
	                                	<s:textfield id="partnerEmployeeId" maxlength="20" name="partnerEmployeeVO.partnerEmployeeId" readonly="true"/>
		                                <span class="tj_bt">*</span>此项不可修改
	                                </td>
	                            </tr>
	                        	<tr>
	                            	<th>姓名</th>
	                                <td><s:textfield id="employeeName" maxlength="32" name="partnerEmployeeVO.employeeName" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>手机号码</th>
	                                <td><s:textfield id="moblieNumber" maxlength="32" name="partnerEmployeeVO.moblieNumber" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                                <th>分润比率</th>
	                                <td><s:textfield id="feeRate" maxlength="4" name="partnerEmployeeVO.feeRate" /><span class="tj_bt">*</span><span>分润费率只能填正整数，例如：千分之三点八填38  千分之四填40！</span></td>
	                            </tr>
	                            <tr>
	                            	<th>状态</th>
	                                <td>
	                                	<s:select list="#{1:'未使用',2:'使用中',3:'冻结'}" listKey="key" listValue="value" name="partnerEmployeeVO.state"  id="state" />
	                                	<span class="tj_bt">*</span>
	                                </td>
	                            </tr>	
	                            <tr>
	                                <th>备注</th>
	                                <td>
	                                	<s:textarea id="remark" cols="25" rows="4" name="partnerEmployeeVO.remark"></s:textarea>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="updatePartnerEmployee();return false;">保存</a><a onclick="returnList()" href="javascript:void(0);">返回列表</a>
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
		$("#employeeName").focus();
	});	

	function updatePartnerEmployee() {
		var employeeName = $("#employeeName").val();
		var moblieNumber = $("#moblieNumber").val();
		var feeRate = $("#feeRate").val();
		var state = $("#state").val();
		var remark = $("#remark").val();
		
		 if (isBlank(employeeName)) {
			alert("姓名不能为空！");
			$("#employeeName").focus();
			return false;
		} else if (isBlank(moblieNumber)) {
			alert("手机号码不能为空！");
			$("#moblieNumber").focus();
			return false;
		} else if (!isMobile(moblieNumber)) {
			alert("手机号码格式不正确！");
			$("#moblieNumber").focus();
			return false;
		} else if (isBlank(feeRate)) {
			alert("分润费率不能为空！");
			$("#feeRate").focus();
			return false;
		} else if (!isPositiveInteger1(feeRate) || feeRate >= 10000) {
			alert("分润费率只能填小于1000的正整数！");
			$("#feeRate").focus();
			return false;
		} else if (isBlank(state)) {
			alert("状态不能为空！");
			$("#state").focus();
			return false;
		} else if (remark.length > 256) {
			alert("备注长度不能大于256！");
			$("#remark").focus();
			return false;
		}
		
		if (!window.confirm("确认修改？")) {
			return false;
		}
		
		$("#partnerEmployeeForm").submit();
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