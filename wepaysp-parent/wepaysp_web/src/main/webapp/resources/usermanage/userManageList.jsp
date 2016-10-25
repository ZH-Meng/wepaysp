<%--
/*
* 
* 创建者：mazw
* 创建日期：2015年6月11日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="manage" uri="/permission-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>用户权限管理</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：系统用户管理&gt;用户权限管理</div>
		<div class="bgtable">
			<s:form method="post">
				<manage:permission validateUrl="/resources/usermanage/usermanage!goToCreateUser.action">
					<manage:pass>
						<ul class="title_button">
							<li><a href="javascript:void(0);" onclick="toCreateUser()">创建用户</a></li>
						</ul>
					</manage:pass>
				</manage:permission>
				<manage:permission validateUrl="/resources/usermanage/usermanage!goToUpdateUser.action">
					<manage:pass>
						<s:set var="hasUpdatePermission">yes</s:set>
					</manage:pass>
					<manage:notPass>
						<s:set var="hasUpdatePermission">no</s:set>
					</manage:notPass>
				</manage:permission>
				<ul class="bg_all">
					<li class="bg_table">
						<table class="bg_odd">
							<thead>
								<tr>
									<th nowrap="nowrap">序号</th>
									<th>登录名</th>
									<th>真实姓名</th>
									<th nowrap="nowrap">性别</th>
									<th nowrap="nowrap">年龄</th>
									<th>角色</th>
									<th>部门</th>
									<th>职务</th>
									<th>联系电话</th>
									<th>邮箱</th>
									<th>最后修改时间</th>
									<th>最后修改人</th>
									<th>状态</th>
									<th>备注</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody>
								<s:if test="userVoList.size() > 0">
									<s:iterator value="userVoList" var="userInfo" status="rowStatus">
										<tr>
											<td><s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" /></td>
											<td title="<s:property value="#userInfo.userId"/>"><s:property value="#userInfo.userId" /></td>
											<td title="<s:property value="#userInfo.userName"/>"><s:property value="#userInfo.userName" /></td>
											<s:if test="#userInfo.gender == 0">
												<s:set var="sexStr">男</s:set>
											</s:if>
											<s:elseif test="#userInfo.gender == 1">
												<s:set var="sexStr">女</s:set>
											</s:elseif>
											<td style="text-align: center;" 
												<s:if test="#userInfo.gender !=null "> title="<s:property value="#sexStr"/>"</s:if>>
												<s:if test="#userInfo.gender == 0">男</s:if>
												<s:elseif test="#userInfo.gender == 1">女</s:elseif>
											</td>
											<td title="<s:property value="#userInfo.age"/>"><s:property value="#userInfo.age" /></td>
											<s:set var="roleStr" value=""></s:set>
											<s:iterator value="#userInfo.userRoleList" var="role">
												<s:set name="roleStr">
													<s:property value="#roleStr" /><s:property value="#role.roleName" />
												</s:set>
											</s:iterator>
											<td title="<s:property value="#roleStr" />">
												<s:iterator value="#userInfo.userRoleList" var="role">
													<s:property value="#role.roleName" />&nbsp;
				  								</s:iterator>
											</td>
											<td title="<s:property value="#userInfo.department"/>"><s:property value="#userInfo.department" /></td>
											<td title="<s:property value="#userInfo.position"/>"><s:property value="#userInfo.position" /></td>
											<td title="<s:property value="#userInfo.lineTel"/>"><s:property value="#userInfo.lineTel" /></td>
											<td title="<s:property value="#userInfo.email"/>"><s:property value="#userInfo.email" /></td>
											<td style="text-align: center;" title="<s:date name="#userInfo.modifyTime" format="yyyy-MM-dd HH:mm:ss" />">
												<s:date name="#userInfo.modifyTime" format="yyyy-MM-dd HH:mm:ss" />
											</td>
											<td title="<s:property value="#userInfo.modifier"/>"><s:property value="#userInfo.modifier" /></td>
											<s:if test="#userInfo.state == 0">
												<s:set var="stateStr">正常</s:set>
											</s:if>
											<s:elseif test="#userInfo.state == 1">
												<s:set var="stateStr">冻结</s:set>
											</s:elseif>
											<s:elseif test="#userInfo.state == 2">
												<s:set var="stateStr">注销</s:set>
											</s:elseif>
											<td style="text-align: center;" title="<s:property value="#stateStr"/>">
												<s:if test="#userInfo.state == 0">正常</s:if>
												<s:elseif test="#userInfo.state == 1">冻结</s:elseif>
												<s:elseif test="#userInfo.state == 2">注销</s:elseif>
											</td>
											<td style="text-align: left;" title="<s:property value="#userInfo.remark"/>">
												<s:property value="#userInfo.remark" />
											</td>
											<td style="text-align: center;" title="修改">
												<s:if test="#userInfo.state == 2">
													<strong>修改</strong>
												</s:if>
												<s:else>
													<s:if test="#hasUpdatePermission == 'yes'">
														<a href="javascript:void(0);" onclick="toUpdateUser('<s:property value="#userInfo.iwoid"/>')">修改</a>
													</s:if>
													<s:else>
														<strong>修改</strong>
													</s:else>
												</s:else>
											</td>
										</tr>
									</s:iterator>
								</s:if>
							</tbody>
						</table>
					</li>
				</ul>
				<ul>
					<li class="t-center">
						<input type="hidden" id="iwoid" name="sysUser.iwoid" value=""/>
						<s:include value="/resources/include/page.jsp"></s:include>
						<span class="bg_pagebutton">
							<a href="<%=request.getContextPath()%>/resources/usermanage/usermanage!exportUser.action">导出Excel</a>
						</span>
					</li>
				</ul>
			</s:form>
		</div>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		function toCreateUser(){
			invokeAction('goToCreateUser');
		}
		function toUpdateUser(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToUpdateUser');
		}
	</script>
</body>
</html>