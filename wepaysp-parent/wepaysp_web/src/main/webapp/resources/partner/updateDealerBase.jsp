<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>商户信息维护</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<style>
		.bg_tjall th {width: 45%;}
		.qrCode_wrapper{min-height:220px;text-align:center;}
		.qrCode_block{float:left; padding:5px 0px;width:30%;}
		.qrCode_remark_block{text-align:left;padding-top:10px;line-height: 30px;color:#999999;}
		.qrCode_title{display:block;font-weight:bold;}
	</style>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：商户信息管理&gt;商户信息维护</div>
	    <div class="bgtj">
	    	<s:form method="post">
	    		<s:hidden id="iwoid" name="dealerVO.iwoid"/>
	    		<s:hidden id="dealerOid" name="dealerOid"/>
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
	                            <tr>
	                            	<th>商户名称</th>
	                                <td><s:textfield id="company" name="dealerVO.company" readonly="true"/><span class="tj_bt">*</span>此项不可修改</td>
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
	                                <td><s:textfield id="email" maxlength="100" name="dealerVO.email" /><span class="tj_bt">*</span></td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="updateDealerBase();return false;">保存</a>
	                    <a href="javascript:void(0);" onclick="toBindWxID('<s:property value="dealerVO.iwoid" />');return false;">绑定微信</a>
	                </li>
	            </ul>
	            <ul>
	            	<li class="t-center">
	                	<s:include value="/resources/include/noPage.jsp"></s:include>
	                </li>
	            </ul>
	        </s:form>
	    </div>
	</div>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#moblieNumber").focus();
		});	
		
		function updateDealerBase() {
			var moblieNumber = $("#moblieNumber").val();
			var qqNumber = $("#qqNumber").val();
			var email = $("#email").val();
			
			if (isBlank(moblieNumber)) {
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
			invokeAction('updateDealerBase');
		}
		
		function toBindWxID(iwoid){
			$("#dealerOid").val(iwoid);
			invokeAction('goToBindWxID');
		}
	</script>	
	<s:property value="#request.messageBean.alertMessage" escape="false" />
</body>
</html>