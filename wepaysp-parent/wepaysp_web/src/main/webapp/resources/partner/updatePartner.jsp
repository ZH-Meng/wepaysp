<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>添加子代理商</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<style>
		.bg_tjall th {width: 35%;}
	</style>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：代理商管理&gt;子代理商管理&gt;修改子代理商</div>
	    <div class="bgtj">
	    	<form action="<%=request.getContextPath()%>/resources/partner/partnermanage!updatePartner.action" method="post" id="partnerForm">
	    		<s:hidden name="partnerVO.iwoid"/>
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
	                        	<tr>
	                            	<th>登录名</th>
	                                <td>
	                                	<s:textfield id="loginId" maxlength="20" name="partnerVO.loginId" readonly="true"/><span class="tj_bt">*</span>此项不可修改
	                                </td>
	                            </tr>
	                            <tr>
	                            	<th>代理商编号</th>
	                                <td>
	                                	<s:textfield id="partnerId" name="partnerVO.partnerId" readonly="true"/><span class="tj_bt">*</span>此项不可修改
	                                </td>
	                            </tr>
	                            <tr>
	                                <th>分润比率</th>
	                                <td><s:textfield id="feeRate" maxlength="20" name="partnerVO.feeRate" /><span class="tj_bt">*</span><span>分润费率只能填正整数，例如：千分之三点八填38  千分之四填40！</span></td>
	                            </tr>
	                            <tr>
	                            	<th>余额</th>
	                                <td><s:textfield id="balance" maxlength="20" name="partnerVO.balance" /><span class="tj_bt">*</span><span>余额只能填整数！</span></td>
	                            </tr>
	                            <tr>
									<th>使用期限</th>
									<td>
										<input type="text" name="contractBegin" id="contractBegin" class="Wdate" readonly="readonly" value="<s:property value="contractBegin"/>"
												onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'contractEnd\')}'})"/>
										<span class="tj_bt">*</span>
										<span>至</span>
										<input type="text" name="contractEnd" id="contractEnd"	class="Wdate" readonly="readonly" value="<s:property value="contractEnd"/>"
													onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'contractBegin\')}'})"/>
										<span class="tj_bt">*</span>
									</td>
								</tr>
	                            <tr>
	                            	<th>联系人</th>
	                                <td><s:textfield id="contactor" maxlength="32" name="partnerVO.contactor" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>固定电话</th>
	                                <td><s:textfield id="telephone" maxlength="32" name="partnerVO.telephone" /><span class="tj_bt">*</span><span>比如： 010-88888888，没有固定电话填手机号</span></td>
	                            </tr>
	                            <tr>
	                            	<th>手机</th>
	                                <td><s:textfield id="moblieNumber" maxlength="32" name="partnerVO.moblieNumber" /><span class="tj_bt">*</span><span>比如：13111110101</span></td>
	                            </tr>
	                            <tr>
	                            	<th>状态</th>
	                                <td>
	                                	<s:select list="#{1:'未使用',2:'使用中',3:'冻结'}" listKey="key" listValue="value" name="partnerVO.state"  id="state"/>
	                                	 <span class="tj_bt">*</span>
	                                </td>
	                            </tr>	
	                            <tr>
	                            	<th>公司名称</th>
	                                <td><s:textfield id="company" maxlength="32" name="partnerVO.company" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>地址</th>
	                                <td><s:textfield id="address" maxlength="32" name="partnerVO.address" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>版权</th>
	                                <td><s:textfield id="copyright" maxlength="32" name="partnerVO.copyright" /><span>例如: © 2013-2020 ******有限公司</span></td>
	                            </tr>
	                            <tr>
	                            	<th>版权URL</th>
	                                <td><s:textfield id="copyrightUrl" maxlength="32" name="partnerVO.copyrightUrl" /><span>例如: http://www.****.com</span></td>
	                            </tr>
	                            <tr>
	                                <th>备注</th>
	                                <td>
	                                	<s:textarea id="remark" cols="25" rows="4" name="partnerVO.remark"></s:textarea>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="updatePartner();return false;">保存</a><a onclick="returnList()" href="javascript:void(0);">返回列表</a>
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
			$("#feeRate").focus();
		});	
	
		function updatePartner() {
			var feeRate = $("#feeRate").val();
			var balance = $("#balance").val();
			var contractBegin = $("#contractBegin").val();
			var contractEnd = $("#contractEnd").val();
			var contactor = $("#contactor").val();
			var telephone = $("#telephone").val();
			var moblieNumber = $("#moblieNumber").val();
			var state = $("#state").val();			
			var company = $("#company").val();
			var address = $("#address").val();
			var remark = $("#remark").val();
			
			 if (isBlank(feeRate)) {
				alert("分润费率不能为空！");
				$("#feeRate").focus();
				return false;
			 } else if (!isPositiveInteger1(feeRate) || feeRate >= 1000) {
				alert("分润费率只能填小于1000的正整数！");
				$("#feeRate").focus();
				return false;
			} else if (isBlank(balance)) {
				alert("余额不能为空！");
				$("#balance").focus();
				return false;
			} else if (!isPositiveInteger(balance)) {
				alert("余额只能填整数！");
				$("#balance").focus();
				return false;
			} else if (isBlank(contractBegin)) {
				alert("使用期限不能为空！");
				$("#contractBegin").focus();
				return false;
			} else if (isBlank(contractEnd)) {
				alert("使用期限不能为空！");
				$("#contractEnd").focus();
				return false;
			} else if (isBlank(contactor)) {
				alert("联系人不能为空！");
				$("#contactor").focus();
				return false;
			} else if (isBlank(telephone)) {
				alert("固定电话不能为空！");
				$("#telephone").focus();
				return false;
			} else if (!islineTel(telephone)) {
				alert("固定电话应为7-18位数字或-！");
				$("#telephone").focus();
				return false;
			} else if (isBlank(moblieNumber)) {
				alert("手机号码不能为空！");
				$("#moblieNumber").focus();
				return false;
			} else if (!isMobile(moblieNumber)) {
				alert("手机号码格式不正确！");
				$("#moblieNumber").focus();
				return false;
			} else if (isBlank(state)) {
				alert("状态不能为空！");
				$("#state").focus();
				return false;
			} else if (isBlank(company)) {
				alert("公司不能为空！");
				$("#company").focus();
				return false;
			} else if (isBlank(address)) {
				alert("地址不能为空！");
				$("#address").focus();
				return false;
			} else if (remark.length > 256) {
				alert("备注长度不能大于256！");
				$("#remark").focus();
				return false;
			}
			
			if (!window.confirm("确认修改？")) {
				return false;
			}
			
			$("#partnerForm").submit();
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