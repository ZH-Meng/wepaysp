<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>添加商户</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<style>
		.bg_tjall th {width: 40%;}
	</style>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：代理商管理&gt;商户信息管理&gt;添加学校</div>
	    <div class="bgtj">
	    	<form action="<%=request.getContextPath()%>/resources/partner/schoolmanage!createSchool.action" method="post" id="schoolForm">
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
                    			<tr>
	                            	<th>业务员</th>
	                                <td>
			                    		<s:if test="partnerEmployeeVoList != null">
	                                		<s:select list="partnerEmployeeVoList" listKey="iwoid" listValue="employeeName" name="schoolVO.partnerEmployeeOid"  id="partnerEmployeeOid" headerKey="" headerValue="请选择"/>
                    					</s:if>
	                                	<s:else>
			                    			<s:hidden id="partnerEmployeeOid" name="schoolVO.partnerEmployeeOid"/>
			                    			<s:property value="schoolVO.partnerEmployeeName"/>
			                    		</s:else>
	                                	<span class="tj_bt">*</span>
	                                </td>
	                            	</tr>
	                        	<tr>
	                            	<th>登录名</th>
	                                <td><s:textfield id="loginId" maxlength="20" name="schoolVO.loginId" /><span class="tj_bt">*</span><span>1-20位长度的字母或数字，保存后不能修改！</span></td>
	                            </tr>
	                            <tr>
	                            	 <th>登录密码</th>
	                                <td><s:password id="loginPwd" maxlength="20" name="schoolVO.loginPwd" /><span class="tj_bt">*</span><span>6-20位长度的字母或数字！</span></td>
	                            </tr>
	                            <tr>
	                            	<th>学校名称</th>
	                                <td><s:textfield id="shcoolName" maxlength="32" name="schoolVO.shcoolName" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>学校类型</th>
	                               	<td>		  			
	                                	 <s:select list="#{1:'托儿所',2:'幼儿园',3:'小学',4:'初中',5:'高中'}" listKey="key" listValue="value" name="schoolVO.schoolType"  id="schoolType" headerKey="" headerValue="请选择"/>	              
	                                	 <span class="tj_bt">*</span>
	                                </td>
	                            </tr>
	                            <tr>
	                            	<th>学校支付宝PID</th>  			
									<td><s:textfield id="schoolPid" maxlength="32" name="schoolVO.schoolPid" /><span class="tj_bt">*</span></td>	                       
	                            </tr>	                            
	                            <tr>	                            
	                            	<th>省</th>
	                                <td>
		                                <select id="provinceCode" name="schoolVO.provinceCode">  
		                                	<option value="">请选择...</option>        
		                                </select><span class="tj_bt">*</span>
	                                </td>
	                            </tr>
	                            <tr>
	                            	<th>地市</th>
	                                <td>
		                                <select id="cityCode" name="schoolVO.cityCode">  
									    	<option value="">请选择...</option>  								  
										</select><span class="tj_bt">*</span>  
	                                </td>
	                            </tr>
	                            <tr>
	                            	<th>区县</th>
	                                <td>
	                                <select id="districtCode" name="schoolVO.districtCode">  
									    <option value="">请选择...</option>  									  
									</select><span class="tj_bt">*</span>  
	                                </td>
	                            </tr>
	                            <tr>
	                            	<th>联系人</th>
	                                <td><s:textfield id="techSupportPerson" maxlength="32" name="schoolVO.techSupportPerson" /></td>
	                            </tr>
	                            <tr>
	                            	<th>联系电话</th>
	                                <td><s:textfield id="techSupportPhone" maxlength="32" name="schoolVO.techSupportPhone" /></td>
	                            </tr>
	                            <tr>
	                                <th>备注</th>
	                                <td>
	                                	<s:textarea id="remark" cols="25" rows="4" name="schoolVO.remark"></s:textarea>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="createSchool();return false;">保存</a><a onclick="returnList()" href="javascript:void(0);">返回列表</a>
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
	
	    //自动加载省信息  
	    $(function() {  
	        var url = "${pageContext.request.contextPath}/resources/partner/schoolmanage!ajaxProvince.action";  
	        $.getJSON(url, function(data) {  
 
	            for (var i = 0; i < data.length; i++) {  
	            	
	                var provinceCode = data[i].provinceCode;  
	                var provinceName = data[i].provinceName;  
	                $("#provinceCode").append(  
	                        "<option value='"+provinceCode+"'>" + provinceName  
	                                + "</option>");  
	            }  
	        });  
	    });  
	    
	    //联动处理地市、区县信息  
	    $(function() {  
	        $("#provinceCode").change(function() {  
	            var val = $(this).val();  
	            $("#cityCode :not(:first)").remove();  
	            $("#districtCode :not(:first)").remove();  
	            var url = "${pageContext.request.contextPath}/resources/partner/schoolmanage!ajaxCity.action";  
	            var args = {"provinceCode" : val};  
	            $.getJSON(url, args, function(data) {  
	                for (var i = 0; i < data.length; i++) {  
	                    var cityCode = data[i].cityCode;  
	                    var cityName = data[i].cityName;  
	                    $("#cityCode").append(  
	                            "<option value='"+cityCode+"'>"  
	                                    + cityName + "</option>");  
	                }  
	            });  
	        });  
	  
	        $("#cityCode").change(function() {  
	            var val = $(this).val();  
	            $("#districtCode :not(:first)").remove();  
	            var url = "${pageContext.request.contextPath}/resources/partner/schoolmanage!ajaxDistrict.action";  
	            var args = {"cityCode" : val};  
	            $.getJSON(url, args, function(data) {  
	                for (var i = 0; i < data.length; i++) {  
	                    var districtCode = data[i].districtCode;  
	                    var districtName = data[i].districtName;  
	                    $("#districtCode").append(  
	                            "<option value='"+districtCode+"'>"  
	                                    + districtName + "</option>");  
	                }  
	            });  
	        });  
	    }); 
	   
		
		function createSchool() {
			var partnerEmployeeOid = $("#partnerEmployeeOid").val();
			var loginId = $("#loginId").val();
			var loginPwd = $("#loginPwd").val();
			var shcoolName =$("#shcoolName").val();
			var schoolType =$("#schoolType").val();			
			var schoolPid =$("#schoolPid").val();
			var selectProvince =$("#provinceCode").val();
			var selectCity =$("#cityCode").val();
			var selectDistrict =$("#districtCode").val();
			var techSupportPhone = $("#techSupportPhone").val();
			
			if (isBlank(partnerEmployeeOid)) {
				alert("业务员不能为空！");
				$("#partnerEmployeeOid").focus();
				return false;
			} else if (isBlank(loginId)) {
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
			} else if (isBlank(shcoolName)) {
				alert("学校名称不能为空！");
				$("#shcoolName").focus();
				return false;
			} else if (isBlank(schoolType)) {
				alert("学校类型不能为空！");
				$("#schoolType").focus();
				return false;
			} else if (isBlank(schoolPid)) {
				alert("学校支付宝PID不能为空！");
				$("#schoolPid").focus();
				return false;
			}  else if (isBlank(selectProvince)) {
				alert("省份不能为空！");
				$("#provinceCode").focus();
				return false;
			} else if (isBlank(selectCity)) {
				alert("地市不能为空！");
				$("#selectCity").focus();
				return false;
			} else if (isBlank(selectDistrict)) {
				alert("区县不能为空！");
				$("#selectDistrict").focus();
				return false;
			} else if (!isBlank(techSupportPhone) && (!isMobile(techSupportPhone) && !islineTel(techSupportPhone))) {
				alert("联系电话应为固定电话或者手机！");
				$("#telephone").focus();
				return false;
			} else if (remark.length > 256) {
				alert("备注长度不能大于256！");
				$("#remark").focus();
				return false;
			}
			
			if (!window.confirm("确认添加？")) {
				return false;
			}
			
			$("#schoolForm").submit();
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