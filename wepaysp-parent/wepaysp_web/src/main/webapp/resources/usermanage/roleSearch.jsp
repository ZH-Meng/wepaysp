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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>角色权限查询</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：系统用户管理&gt;角色权限查询</div>
		<s:form method="post">
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>角色名称</th>
									<td>
										<select name="roleName" id="roleName">
											<option value="">全部</option>
											<s:if test="roleNameList.size() > 0">
												<s:iterator value="roleNameList" var="roleNameStr">
													<option value='<s:property value="#roleNameStr" />'><s:property value="#roleNameStr" /></option>
												</s:iterator>
											</s:if>
										</select>
										<input type="hidden" id="roleNames" name="roleNames" value="" />
										<input type="hidden" id="roleOid" name="roleOid" value="" />
									</td>
									<th>角色级别</th>
									<td>
										<select name="roleLevel" id="roleLevel">
											<option value="">全部</option>
											<option value="1">管理级别</option>
											<option value="0">应用级别</option>
										</select>
									</td>
								</tr>
								<tr>
									<th>状态</th>
									<td>
										<select name="state" id="state">
											<option value="">全部</option>
											<option value="0">正常</option>
											<option value="1">冻结</option>
											<option value="2">注销</option>
										</select>
									</td>
									<th></th>
									<td></td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button"><a href="javascript:void(0);" onclick="invokeAction('list');">查询</a></li>
				</ul>
			</div>
			<div class="bgtable">
				<ul class="bg_all">
					<li class="bg_table bg_table1">
						<table class="bg_odd">
							<thead>
								<tr>
									<th>序号</th>
									<th>角色代码</th>
									<th>角色名称</th>
									<th>角色级别</th>
									<th>角色描述</th>
									<th>最后修改时间</th>
									<th>最后修改人</th>
									<th>状态</th>
									<th>备注</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody>
								<s:if test="sysRoleList!=null && sysRoleList.size() > 0">
									<s:iterator value="sysRoleList" var="sysRoleInfo" status="rowStatus">
										<tr>
											<td><s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" /></td>
											<td title="<s:property value="#sysRoleInfo.roleId" />"><s:property value="#sysRoleInfo.roleId" /></td>
											<td title="<s:property value="#sysRoleInfo.roleName" />"><s:property value="#sysRoleInfo.roleName" /></td>
											<s:if test="#sysRoleInfo.roleLevel == 0">
												<s:set var="roleLevelStr">应用级别</s:set>
											</s:if>
											<s:elseif test="#sysRoleInfo.roleLevel == 1">
												<s:set var="roleLevelStr">管理级别</s:set>
											</s:elseif>
											<td title="<s:property value="#roleLevelStr" />"><s:property value="#roleLevelStr" /></td>
											<td style="text-align: left;" title="<s:property value="#sysRoleInfo.description" />"><s:property value="#sysRoleInfo.description" /></td>
											<td title="<s:date name="#sysRoleInfo.modifyTime" format="yyyy-MM-dd HH:mm:ss" />">
												<s:date name="#sysRoleInfo.modifyTime" format="yyyy-MM-dd HH:mm:ss" />
											</td>
											<td title="<s:property value="#sysRoleInfo.modifier" />"><s:property value="#sysRoleInfo.modifier" /></td>
											<s:if test="#sysRoleInfo.state == 0">
												<s:set var="stateStr">正常</s:set>
											</s:if>
											<s:elseif test="#sysRoleInfo.state == 1">
												<s:set var="stateStr">冻结</s:set>
											</s:elseif>
											<s:elseif test="#sysRoleInfo.state == 2">
												<s:set var="stateStr">注销</s:set>
											</s:elseif>
											<td title="<s:property value="#stateStr" />"><s:property value="#stateStr" /></td>
											<td style="text-align: left;" title="<s:property value="#sysRoleInfo.remark" />"><s:property value="#sysRoleInfo.remark" /></td>
											<td title="查看角色功能">
												<a href="javascript:void(0)" onclick="checkRoleFunction('<s:property value="#sysRoleInfo.iwoid" />')">查看角色功能</a>
											</td>
										</tr>
									</s:iterator>
								</s:if>
								<s:if test="sysRoleList!=null && sysRoleList.size()==0">
									<tr>
										<td colspan="10">无符合条件的查询结果！</td>
									</tr>
								</s:if>
							</tbody>
						</table>
					</li>
				</ul>
				<ul>
					<li class="t-center">
						<s:include value="/resources/include/page.jsp"></s:include>
						<span class="bg_pagebutton"><a href="javascript:void(0);" id="excel" onclick="checkAction();">导出Excel</a></span>
					</li>
				</ul>
			</div>
		</s:form>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		
		function checkAction(){
			var roleName = $("#roleName").find("option:selected").text();
			$("input[name='roleName']").val(roleName);
			
			var listSize = '${fn:length(sysRoleList)}';
			if (listSize > 0) {
				invokeAction('exportFile');
			}
		}
		
		$(function(){
			$("#roleName").val("${requestScope.roleName}");
			$("#roleLevel").val("${requestScope.roleLevel}");
			$("#state").val("${requestScope.state}");
		});
		
		function checkRoleFunction(iwoid){
			var roleName = $("#roleName").find("option:selected").text();
			$("#roleNames").val(roleName);
			$("#roleOid").val(iwoid);
			invokeAction('checkRoleFunction');
		}
	</script>
</body>
</html>