<%--
/*
* 
* 创建者：侯建玮
* 创建日期：2015年6月11日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>系统用户操作日志</title>
	<link href="<%=request.getContextPath()%>/css/jquery.resizableColumns.css" rel="stylesheet" />
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：日志管理&gt;系统用户操作日志</div>
		<s:form method="post">
			<div class="bgtj">
				<input type="hidden" name="conditionRoleName" value="全部" />
				<input type="hidden" name="conditionLogTypeName" value="全部" />
				<input type="hidden" name="conditionFunctionName" value="全部" />
				<input type="hidden" name="conditionActionTypeName" value="全部" />
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>登录名</th>
									<td><input type="text" name="userId" id="userId" maxlength="20" value="" /></td>
									<th>真实姓名</th>
									<td><input type="text" name="userName" id="userName" maxlength="20" value="" /></td>
								</tr>
								<tr>
									<th>角色</th>
									<td>
										<select name="roleOid" id="roleOid">
											<option value="">全部</option>
											<s:if test="sysRoleAll.size() > 0">
												<s:iterator value="sysRoleAll" var="sysRoleInfo" status="rowStatus">
													<option value='<s:property value="#sysRoleInfo.iwoid" />'><s:property value="#sysRoleInfo.roleName" /></option>
												</s:iterator>
											</s:if>
										</select>
									</td>
									<th>日志类型</th>
									<td>
										<select name="logType" id="logType">
											<option value="">全部</option>
											<option value="0">用户操作</option>
											<option value="1">用户登录操作</option>
											<option value="2">用户退出操作</option>
										</select>
									</td>
								</tr>
								<tr>
									<th>功能名称</th>
									<td>
										<select name="functionOid" id="functionOid">
											<option value="">全部</option>
											<s:if test="sysFunctionAll.size() > 0">
												<s:iterator value="sysFunctionAll" var="sysFunction" status="rowStatus">
													<option value='<s:property value="#sysFunction.iwoid" />'><s:property value="#sysFunction.functionName" /></option>
												</s:iterator>
											</s:if>
										</select>
									</td>
									<th>操作名称</th>
									<td>
										<select name="actionType" id="actionType">
											<option value="">全部</option>
											<option value="0">新建</option>
											<option value="1">修改</option>
											<option value="2">导出</option>
											<option value="3">删除</option>
											<option value="4">重置密码</option>
											<option value="5">充值</option>
											<option value="6">批量导入</option>
										</select>
									</td>
								</tr>
								<tr>
									<th>操作开始日期</th>
									<td>
										<input type="text" class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})" 
											name="processBeginTime" id="processBeginTime" maxlength="20" value="" /><span class="tj_bt">*</span>
									</td>
									<th>操作结束日期</th>
									<td>
										<input type="text" class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})"
											name="processEndTime" id="processEndTime" maxlength="20" value="" /><span class="tj_bt">*</span>
									</td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<a href="javascript:void(0);" onclick="checkSub('list');">查询</a>
					</li>
				</ul>
			</div>
			<div class="bgtable">
				<ul class="bg_all">
					<li class="bg_table">
						<table class="bg_odd">
							<thead>
								<tr>
									<th>序号</th>
									<th>登录名</th>
									<th>真实姓名</th>
									<th>角色</th>
									<th>日志类型</th>
									<th>功能名称</th>
									<th>操作名称</th>
									<th class="twenty">操作前数据</th>
									<th class="twenty">操作后数据</th>
									<th class="ten">日志描述</th>
									<th>操作开始时间</th>
									<th>操作结束时间</th>
									<th>执行状态</th>
								</tr>
							</thead>
							<tbody>
								<s:if test="sysLogVoList != null && sysLogVoList.size() > 0">
									<s:iterator value="sysLogVoList" var="sysLogVo" status="rowStatus">
										<tr>
											<td><s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" /></td>
											<td title="<s:property value="#sysLogVo.sysUser.userId" />"><s:property value="#sysLogVo.sysUser.userId" /></td>
											<td title="<s:property value="#sysLogVo.sysUser.userName" />"><s:property value="#sysLogVo.sysUser.userName" /></td>
											<td title="<s:property value="#sysLogVo.roleName" />"><s:property value="#sysLogVo.roleName" /></td>
											<s:if test="#sysLogVo.logType== 0">
												<s:set var="logTypeStr">用户操作</s:set>
											</s:if>
											<s:elseif test="#sysLogVo.logType == 1">
												<s:set var="logTypeStr">用户登录操作</s:set>
											</s:elseif>
											<s:elseif test="#sysLogVo.logType== 2">
												<s:set var="logTypeStr">用户退出操作</s:set>
											</s:elseif>
											<td title="<s:property value="#logTypeStr" />"><s:property value="#logTypeStr" /></td>
											<td style="text-align: left;" title="<s:property value="#sysLogVo.functionName" />"><s:property value="#sysLogVo.functionName" /></td>
											<s:if test="#sysLogVo.actionType== 0">
												<s:set var="actionTypeStr">新建</s:set>
											</s:if>
											<s:elseif test="#sysLogVo.actionType == 1">
												<s:set var="actionTypeStr">修改</s:set>
											</s:elseif>
											<s:elseif test="#sysLogVo.actionType == 2">
												<s:set var="actionTypeStr">导出</s:set>
											</s:elseif>
											<s:elseif test="#sysLogVo.actionType == 3">
												<s:set var="actionTypeStr">删除</s:set>
											</s:elseif>
											<s:elseif test="#sysLogVo.actionType == 4">
												<s:set var="actionTypeStr">重置密码</s:set>
											</s:elseif>
											<s:elseif test="#sysLogVo.actionType == 5">
												<s:set var="actionTypeStr">充值</s:set>
											</s:elseif>
											<s:elseif test="#sysLogVo.actionType == 6">
												<s:set var="actionTypeStr">批量导入</s:set>
											</s:elseif>
											<%-- <td style="text-align: left;"  title="<s:property value="#logInfoVo.dateBefore" />">
								  				<s:property value="#logInfoVo.dateBefore.substring(0,30)" />
								  				<s:if test="null != #logInfoVo.dateBefore && '' != #logInfoVo.dateBefore">
								  				<s:if test="#logInfoVo.dateBefore.length()>=30"><s:property value="#logInfoVo.dateBefore.substring(0,30)" />...</s:if>
												<s:if test="#logInfoVo.dateBefore.length()<30}"><s:property value="#logInfoVo.dateBefore.substring(0,30)" /></s:if>
								  				</s:if>
								  			</td> --%>
								  			<s:if test="#sysLogVo.actionType == null">
												<td ></td>
											</s:if>
											<s:else>
												<td title="<s:property value="#actionTypeStr" />"><s:property value="#actionTypeStr" /></td>
											</s:else>
											<td style="text-align: left;" title="<s:property value="#sysLogVo.dataBefore" />">
												<c:if test="${fn:length(sysLogVo.dataBefore)>=28}">${fn:substring(sysLogVo.dataBefore, 0, 28)}...</c:if>
												<c:if test="${fn:length(sysLogVo.dataBefore)<28}">${sysLogVo.dataBefore}</c:if>
												<%-- <s:property value="#sysLogVo.dataBefore" /> --%>
											</td>
											<td style="text-align: left;" title="<s:property value="#sysLogVo.dataAfter" />">
												<c:if test="${fn:length(sysLogVo.dataAfter)>=28}">${fn:substring(sysLogVo.dataAfter, 0, 28)}...</c:if>
												<c:if test="${fn:length(sysLogVo.dataAfter)<28}">${sysLogVo.dataAfter}</c:if>
												<%-- <s:property value="#sysLogVo.dataAfter" /> --%>
											</td>
											<td style="text-align: left;" title="<s:property value="#sysLogVo.logAbstract" />">
												<c:if test="${fn:length(sysLogVo.logAbstract)>=10}">${fn:substring(sysLogVo.logAbstract, 0, 10)}...</c:if>
												<c:if test="${fn:length(sysLogVo.logAbstract)<10}">${sysLogVo.logAbstract}</c:if>
											</td>
											<td title="<s:date name="#sysLogVo.processTimeBegin" format="yyyy-MM-dd HH:mm:ss" />">
												<s:date name="#sysLogVo.processTimeBegin" format="yyyy-MM-dd HH:mm:ss" />
											</td>
											<td title="<s:date name="#sysLogVo.processTimeEnd" format="yyyy-MM-dd HH:mm:ss" />">
												<s:date name="#sysLogVo.processTimeEnd" format="yyyy-MM-dd HH:mm:ss" />
											</td>
											<s:if test="#sysLogVo.state== 1">
												<s:set var="stateStr">成功</s:set>
											</s:if>
											<s:elseif test="#sysLogVo.state == 0">
												<s:set var="stateStr">受理中</s:set>
											</s:elseif>
											<s:elseif test="#sysLogVo.state == 2">
												<s:set var="stateStr">受理失败</s:set>
											</s:elseif>
											<td title="<s:property value="#stateStr" />"><s:property value="#stateStr" /></td>
										</tr>
									</s:iterator>
								</s:if>
								<s:if test="sysLogVoList != null && sysLogVoList.size() == 0">
									<tr>
										<td colspan="13">无符合条件的查询结果！</td>
									</tr>
								</s:if>
							</tbody>
						</table>
					</li>
				</ul>
				<ul>
					<li class="t-center">
						<s:include value="/resources/include/page.jsp"></s:include>
						<span class="bg_pagebutton"><a href="javascript:void(0);" onclick="exportFile();">导出Excel</a></span>
					</li>
				</ul>
			</div>
		</s:form>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		
		function checkSub(methodName) {
			var beginTimeStr = $("input[name='processBeginTime']").val();
			var endTimeStr = $("input[name='processEndTime']").val();
			if (DateDiff(endTimeStr, beginTimeStr) < 0) {
				alert('开始日期不能大于结束日期！');
				return;
			}
			
			publicAction(methodName);
		}
		
		function exportFile() {
			var listSize = '${fn:length(sysLogVoList)}';
// 			if (listSize == 0) {
// 				alert("没有符合条件的查询结果，无法导出！");
// 				return;
// 			}
			
			var beginTimeStr = $("input[name='processBeginTime']").val();
			var endTimeStr = $("input[name='processEndTime']").val();
			if (DateDiff(endTimeStr, beginTimeStr) < 0) {
				alert('开始日期不能大于结束日期！');
				return;
			}
			var conditionRoleName = $("#roleOid").find("option:selected").text();
			$("input[name='conditionRoleName']").val(conditionRoleName);
			var conditionLogTypeName = $("#logType").find("option:selected").text();
			$("input[name='conditionLogTypeName']").val(conditionLogTypeName);
			var conditionFunctionName = $("#functionOid").find("option:selected").text();
			$("input[name='conditionFunctionName']").val(conditionFunctionName);
			var conditionActionTypeName = $("#actionType").find("option:selected").text();
			$("input[name='conditionActionTypeName']").val(conditionActionTypeName);
			
			publicAction('exportFile');
		}
		
		$(function() {
			$("#userId").val("${requestScope.userId}");
			$("#userName").val("${requestScope.userName}");
			$("#roleOid").val("${requestScope.roleOid}");
			$("#logType").val("${requestScope.logType}");
			$("#functionOid").val("${requestScope.functionOid}");
			$("#actionType").val("${requestScope.actionType}");
			$("#processBeginTime").val("<s:date name='#request.processBeginTime' format='yyyy-MM-dd'/>");
			$("#processEndTime").val("<s:date name='#request.processEndTime' format='yyyy-MM-dd'/>");
		});
		
		function invokeAction(methodName) {
			checkSub(methodName);
		}
		
		function publicAction(methodName) {
			var formObj = document.forms[0];
			var actionURL = formObj.action;
			var lastPoint = actionURL.lastIndexOf(".");
			var lastLine = actionURL.lastIndexOf("/");

			if (actionURL.indexOf("!") > 0) {
				lastPoint = actionURL.lastIndexOf("!");
			}

			var prefix = actionURL.substring(lastLine + 1, lastPoint);

			formObj.action = prefix + "!" + methodName + ".action";
			formObj.submit();
		}
	</script>
</body>
</html>