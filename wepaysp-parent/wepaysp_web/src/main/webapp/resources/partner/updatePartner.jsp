<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>创建子代理商</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：子代理商管理&gt;创建子代理商&gt;修改子代理商</div>
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
	                                	<s:if test="partnerVO.state == 1">
			                                <s:set var="readFlag" value="readonly"/>
	                                	</s:if>  
		                                <s:textfield id="loginId" maxlength="20" name="partnerVO.loginId" readonly="<s:property value='#readFlag' />"/>
		                                <span class="tj_bt">*</span>
	                                </td>
	                                <%-- <th>登录密码</th>
	                                <td><s:password id="loginPwd" maxlength="20" name="partnerVO.loginPwd" /><span class="tj_bt">*</span></td> --%>
	                            </tr>
	                            <tr>
	                                <th>分润比率</th>
	                                <td><s:textfield id="feeRate" maxlength="20" name="partnerVO.feeRate" /><span class="tj_bt">*</span></td>
	                                <th>余额</th>
	                                <td><s:textfield id="balance" maxlength="20" name="partnerVO.balance" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
									<th>使用期限</th>
									<td colspan="3">
										<input type="text" class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})" 
											name="contractBegin" id="contractBegin" maxlength="20" value="" /><span class="tj_bt">*</span>
									<span>-</span>
										<input type="text" class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})"
											name="contractEnd" id="contractEnd" maxlength="20" value="" /><span class="tj_bt">*</span>
									</td>
								</tr>
	                            <tr>
	                            	<th>联系人</th>
	                                <td><s:textfield id="contactor" maxlength="32" name="partnerVO.contactor" /><span class="tj_bt">*</span></td>
	                                <th>固定电话</th>
	                                <td><s:textfield id="telephone" maxlength="32" name="partnerVO.telephone" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>手机</th>
	                                <td><s:textfield id="moblieNumber" maxlength="32" name="partnerVO.moblieNumber" /><span class="tj_bt">*</span></td>
	                            	<th>状态</th>
	                                <td>
	                                	<select id="state" name="partnerVO.state">
	                                		<option value="">请选择</option>
	                                		<option value="1">未使用</option>
	                                		<option value="2">使用中</option>
	                                		<option value="3">冻结</option>
	                                	</select>
	                                	 <span class="tj_bt">*</span>
	                                </td>
	                            </tr>	
	                            <tr>
	                            	<th>公司名称</th>
	                                <td colspan="3"><s:textfield id="company" maxlength="32" name="partnerVO.company" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>地址</th>
	                                <td colspan="3"><s:textfield id="address" maxlength="32" name="partnerVO.address" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>版权</th>
	                                <td colspan="3"><s:textfield id="copyright" maxlength="32" name="partnerVO.copyright" /></td>
	                            </tr>
	                            <tr>
	                            	<th>版权URL</th>
	                                <td colspan="3"><s:textfield id="copyrightUrl" maxlength="32" name="partnerVO.copyrightUrl" /></td>
	                            </tr>
	                            <tr>
	                                <th>备注</th>
	                                <td colspan="3" >
	                                	<s:textarea id="remark" cols="25" rows="4" name="partnerVO.remark"></s:textarea>
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
	
		//TODO
		function createPartner() {
			var loginId = $("#loginId").val();
			var loginPwd = $("#loginPwd").val();
			var contactor = $("#contactor").val();
			var telephone = $("#telephone").val();
			var moblieNumber = $("#moblieNumber").val();
			var contractBegin = $("#contractBegin").val();
			var contractEnd = $("#contractEnd").val();
			var state = $("#state").val();
			var feeRate = $("#feeRate").val();
			var balance = $("#balance").val();
			var company = $("#company").val();
			var address = $("#address").val();
			
			var remark = $("#remark").val();
			
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
			} else if (!isWord(loginPwd)) {
				alert("登陆密码只能输入数字字母或汉字！");
				$("#loginPwd").focus();
				return false;
			} else if (isBlank(contactor)) {
				alert("联系人不能为空！");
				$("#contactor").focus();
				return false;
			} else if (isBlank(telephone)) {
				alert("固定电话不能为空！");
				$("#telephone").focus();
				return false;
			} else if (isBlank(moblieNumber)) {
				alert("手机号码不能为空！");
				$("#moblieNumber").focus();
				return false;
			} else if (isBlank(contractBegin)) {
				alert("使用期限不能为空！");
				$("#contractBegin").focus();
				return false;
			} else if (isBlank(contractEnd)) {
				alert("使用期限不能为空！");
				$("#contractEnd").focus();
				return false;
			} else if (isBlank(state)) {
				alert("状态不能为空！");
				$("#state").focus();
				return false;
			} else if (isBlank(feeRate)) {
				alert("分润费率不能为空！");
				$("#feeRate").focus();
				return false;
			} else if (isBlank(balance)) {
				alert("余额不能为空！");
				$("#balance").focus();
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
			
			if (!window.confirm("确认新增？")) {
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