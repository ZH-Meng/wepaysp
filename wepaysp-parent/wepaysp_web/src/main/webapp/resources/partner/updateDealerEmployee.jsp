<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>修改商户员工</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<style>
		.bg_tjall th {width: 30%;}
	</style>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：商户员工管理&gt;员工管理&gt;修改商户员工</div>
	    <div class="bgtj">
	    	<form action="<%=request.getContextPath()%>/resources/partner/dealeremployeemanage!updateDealerEmployee.action" method="post" id="dealerEmployeeForm">
	    		<s:hidden id="iwoid" name="dealerEmployeeVO.iwoid"/>
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
	                    		<tr>
	                            	<th>登录名</th>
	                                <td>
	                                	<s:textfield id="loginId" maxlength="20" name="dealerEmployeeVO.loginId" readonly="true"/>
		                                <span class="tj_bt">*</span>此项不可修改
	                                </td>
	                            </tr>
	                            <tr>
	                            	<th>员工编号</th>
	                                <td>
	                                	<s:textfield id="dealerEmployeeId" maxlength="20" name="dealerEmployeeVO.dealerEmployeeId" readonly="true"/>
		                                <span class="tj_bt">*</span>此项不可修改
	                                </td>
	                            </tr>
	                        	<tr>
	                            	<th>姓名</th>
	                                <td><s:textfield id="employeeName" maxlength="32" name="dealerEmployeeVO.employeeName" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>手机号码</th>
	                                <td><s:textfield id="moblieNumber" maxlength="32" name="dealerEmployeeVO.moblieNumber" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>门店</th>
	                                <td>
	                                	<s:select list="%{storeVoList}" listKey="iwoid" listValue="storeName" name="dealerEmployeeVO.storeOid" id="storeOid" headerKey="" headerValue="请选择"/>
	                                	<span class="tj_bt">*</span>
	                                </td>
	                            </tr>
	                            <tr>
	                            	<th>状态</th>
	                                <td>
	                                	<s:select list="#{1:'未使用',2:'使用中',3:'冻结'}" listKey="key" listValue="value" name="dealerEmployeeVO.state" id="state" headerKey="" headerValue="请选择"/>
	                                	<span class="tj_bt">*</span>
	                                </td>
	                            </tr>	
	                            <tr>
	                                <th>备注</th>
	                                <td>
	                                	<s:textarea id="remark" cols="25" rows="4" name="dealerEmployeeVO.remark"></s:textarea>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="updateDealerEmployee();return false;">修改</a><a onclick="returnList()" href="javascript:void(0);">返回列表</a>
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

	function updateDealerEmployee() {
		var loginId = $("#loginId").val();
		var employeeName = $("#employeeName").val();
		var dealerEmployeeId = $("#dealerEmployeeId").val();
		var moblieNumber = $("#moblieNumber").val();
		var storeOid = $("#storeOid").val();
		var state = $("#state").val();
		var remark = $("#remark").val();
		if (isBlank(loginId)) {
			alert("登录名不能为空！");
			$("#loginId").focus();
			return false;
		} else if (isBlank(dealerEmployeeId)) {
			alert("编号不能为空！");
			$("#dealerEmployeeId").focus();
			return false;
		} else if (isBlank(employeeName)) {
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
		} else if (isBlank(storeOid)) {
			alert("门店不能为空！");
			$("#storeOid").focus();
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
		
		$("#dealerEmployeeForm").submit();
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