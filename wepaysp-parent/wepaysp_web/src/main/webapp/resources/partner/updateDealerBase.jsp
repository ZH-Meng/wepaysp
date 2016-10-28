<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>商户信息维护</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<style>
		.bg_tjall th {width: 45%;}
	</style>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：商户信息管理&gt;商户信息维护</div>
	    <div class="bgtj">
	    	<form action="<%=request.getContextPath()%>/resources/partner/dealermanage!updateDealerBase.action" method="post" id="dealerForm">
	    		<s:hidden id="iwoid" name="dealerVO.iwoid"/>
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
	                            <tr>
	                            	<th>商户名称</th>
	                                <td><s:textfield id="company" maxlength="32" name="dealerVO.company" readonly="true"/></td>
	                            </tr>
	                            <tr>
	                            	<th>手机号码</th>
	                                <td><s:textfield id="moblieNumber" maxlength="32" name="dealerVO.moblieNumber" /><span class="tj_bt">*</span></td>
	                            </tr>
	                             <tr>
	                            	<th>常用QQ号码</th>
	                                <td><s:textfield id="qqNumber" maxlength="16" name="dealerVO.qqNumber" /><span class="tj_bt">*</span></td>
	                            </tr>
	                           	<tr>
	                            	<th>常用email</th>
	                                <td><s:textfield id="email" maxlength="16" name="dealerVO.email" /><span class="tj_bt">*</span></td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="updateDealerBase();return false;">修改</a>
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
		
		function updateDealerBase() {
			var moblieNumber = $("#moblieNumber").val();
			var company = $("#company").val();
			var qqNumber = $("#qqNumber").val();
			var email = $("#email").val();
			
			if (isBlank(company)) {
				alert("商户名称不能为空！");
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
			} else if (isBlank(qqNumber)) {
				alert("QQ号码不能为空！");
				$("#qqNumber").focus();
				return false;
			} else if (!isQQ(qqNumber)) {
				alert("QQ号码格式不正确！");
				$("#qqNumber").focus();
				return false;
			} else if (isBlank(email)) {
				alert("邮箱不能为空！");
				$("#email").focus();
				return false;
			} else if (!isEmail(email)) {
				alert("邮箱格式不正确！");
				$("#email").focus();
				return false;
			}
			
			if (!window.confirm("确认修改？")) {
				return false;
			}
			$("#dealerForm").submit();
		}
		
	</script>	
	<s:property value="#request.messageBean.alertMessage" escape="false" />
</body>
</html>