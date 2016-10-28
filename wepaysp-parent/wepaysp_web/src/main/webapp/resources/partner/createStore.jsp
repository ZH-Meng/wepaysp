<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>创建门店</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：商户信息管理&gt;门店信息管理&gt;创建门店</div>
	    <div class="bgtj">
	    	<form action="<%=request.getContextPath()%>/resources/partner/storemanage!createStore.action" method="post" id="storeForm">
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
	                        	<tr>
	                            	<th>门店名称</th>
	                                <td><s:textfield id="storeName" maxlength="32" name="storeVO.storeName" /><span class="tj_bt">*</span></td>
	                            </tr>
	                            <tr>
	                            	<th>联系电话</th>
	                                <td><s:textfield id="storeTel" maxlength="32" name="storeVO.storeTel" /><span>固定电话或者手机号码</span></td>
	                            </tr>
	                            <tr>
	                            	<th>地址</th>
	                                <td><s:textfield id="storeAddress" maxlength="32" name="storeVO.storeAddress" /></td>
	                            </tr>
	                            <tr>
	                                <th>备注</th>
	                                <td>
	                                	<s:textarea id="remark" cols="25" rows="4" name="storeVO.remark"></s:textarea>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="createStore();return false;">新增</a><a onclick="returnList()" href="javascript:void(0);">返回列表</a>
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
			$("#storeName").focus();
		});	
	
		function createStore() {
			var storeName = $("#storeName").val();
			var storeTel = $("#storeTel").val();
			var storeAddress = $("#storeAddress").val();
			var remark = $("#remark").val();
			
			if (isBlank(storeName)) {
				alert("门店名称不能为空！");
				$("#storeName").focus();
				return false;
			} else if (!isBlank(storeTel) && (!isMobile(storeTel) || !islineTel(storeTel))) {
				alert("联系电话应为固定电话或者手机！");
				$("#storeTel").focus();
				return false;
			} else if (remark.length > 256) {
				alert("备注长度不能大于256！");
				$("#remark").focus();
				return false;
			}
			
			if (!window.confirm("确认新增？")) {
				return false;
			}
			
			$("#storeForm").submit();
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