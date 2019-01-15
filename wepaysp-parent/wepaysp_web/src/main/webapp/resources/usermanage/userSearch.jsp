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
	<title>用户权限查询</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：系统用户管理&gt;用户权限查询</div>
		<s:form method="post">
			<input type="hidden" name="conditionRoleName" value="全部"/>
	        <input type="hidden" name="conditionStateName" value="全部"/>
			<div class="bgtj">
				<input type="hidden" name="userOid" value="" />
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
									<th>状态</th>
									<td>
										<select name="state" id="state">
											<option value="">全部</option>
											<option value="0">正常</option>
											<option value="1">冻结</option>
											<option value="2">注销</option>
										</select>
									</td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<a href="javascript:void(0);" onclick="invokeAction('list');">查询</a>
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
									<th>性别</th>
 									<th>年龄</th>
									<th>角色</th>
 									<th>部门</th>
									<th>职务</th>
 									<th>联系电话</th>
 									<th>邮箱</th>
									<!-- <th>创建时间</th>
									<th>创建人</th> -->
									<th>最后修改时间</th>
									<th>最后修改人</th>
									<th>状态</th>
									<th>备注</th>
									<th class="eight">操作</th>
								</tr>
							</thead>
							<tbody>
								<s:if test="sysUserVOList!=null && sysUserVOList.size() > 0">
									<s:iterator value="sysUserVOList" var="sysUserVO" status="rowStatus">
										<tr>
											<td><s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" /></td>
											<td title="<s:property value="#sysUserVO.userId" />"><s:property value="#sysUserVO.userId" /></td>
											<td title="<s:property value="#sysUserVO.userName" />"><s:property value="#sysUserVO.userName" /></td>
 											
 											<s:set var="gender" value=""/>
 											<s:if test="#sysUserVO.gender == 0">
												<s:set var="gender">男</s:set>
 											</s:if>
											<s:elseif test="#sysUserVO.gender == 1">
												<s:set var="gender">女</s:set>
											</s:elseif>
 											<td title="<s:property value="#gender" />">
 												<s:property value="#gender" />
 											</td>
 											<td title="<s:property value="#sysUserVO.age" />"><s:property value="#sysUserVO.age" /></td>
											
											<s:set var="roleNameStr" value=""/>
											<s:iterator value="#sysUserVO.userRoleList" var="sysRole" status="roleRow">
												<s:set var="roleNameStr">
													<s:property value="#roleNameStr" /><s:property value="#sysRole.roleName" />
												</s:set>
												<s:if test="#sysUserVO.userRoleList.size() != (#roleRow.index+1)">
													<s:set var="roleNameStr" value="#roleNameStr" />&nbsp;
												</s:if>
											</s:iterator>
											<td title="<s:property value="#roleNameStr" />"><s:property value="#roleNameStr" escape="false"/></td>
											
											<td title="<s:property value="#sysUserVO.department" />"><s:property value="#sysUserVO.department" /></td>
											<td title="<s:property value="#sysUserVO.position" />"><s:property value="#sysUserVO.position" /></td>
											<td title="<s:property value="#sysUserVO.lineTel" />"><s:property value="#sysUserVO.lineTel" /></td>
 											<td title="<s:property value="#sysUserVO.email" />"><s:property value="#sysUserVO.email" /></td>
											<%-- <td>
												<s:date name="#sysUserVO.createTime" format="yyyy-MM-dd HH:mm:ss" />
											</td>
											<td title="<s:property value="#sysUserVO.creator" />"><s:property value="#sysUserVO.creator" /></td> --%>
											<td title="<s:date name="#sysUserVO.modifyTime" format="yyyy-MM-dd HH:mm:ss" />">
												<s:date name="#sysUserVO.modifyTime" format="yyyy-MM-dd HH:mm:ss" />
											</td>
											<td title="<s:property value="#sysUserVO.modifier" />"><s:property value="#sysUserVO.modifier" /></td>
											
											<s:set var="stateStr" value=""/>
											<s:if test="#sysUserVO.state == 0">
												<s:set var="stateStr">正常</s:set>
											</s:if>
											<s:elseif test="#sysUserVO.state == 1">
												<s:set var="stateStr">冻结</s:set>
											</s:elseif>
											<s:elseif test="#sysUserVO.state == 2">
												<s:set var="stateStr">注销</s:set>
											</s:elseif>
											<td title="<s:property value="#stateStr" />"><s:property value="#stateStr" /></td>
											<td style="text-align: left;" title="<s:property value="#sysUserVO.remark" />"><s:property value="#sysUserVO.remark" /></td>
											<td title="查看用户功能"><a href="javascript:void(0);" onclick="checkUserFunction('<s:property value="#sysUserVO.iwoid" />');">查看用户功能</a></td>
										</tr>
									</s:iterator>
								</s:if>
								<s:if test="sysUserVOList!=null && sysUserVOList.size()==0">
									<tr>
										<td colspan="15">无符合条件的查询结果！</td>
									</tr>
								</s:if>
							</tbody>
						</table>
					</li>
				</ul>
				<ul>
					<li class="t-center">
						<s:include value="/resources/include/page.jsp"></s:include>
						<span class="bg_pagebutton"><a href="javascript:void(0);" onclick="exportFile()">导出Excel</a></span>
					</li>
				</ul>
			</div>
		</s:form>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		function exportFile() {
			var listSize = '${fn:length(sysUserVOList)}';
			if (listSize > 0) {
				var conditionRoleName = $("#roleOid").find("option:selected").text();
				$("input[name='conditionRoleName']").val(conditionRoleName);
				var conditionStateName = $("#state").find("option:selected").text();
				$("input[name='conditionStateName']").val(conditionStateName);
				
				invokeAction('exportFile');
			}
		}
		
		$(function() {
			$("#userId").val("${requestScope.userId}");
			$("#userName").val("${requestScope.userName}");
			$("#roleOid").val("${requestScope.roleOid}");
			$("#state").val("${requestScope.state}");
		});
		
		function checkUserFunction(iwoid) {
			$("input[name='userOid']").val(iwoid);
			invokeAction('checkUserFunction');
		}
	</script>
</body>
</html>