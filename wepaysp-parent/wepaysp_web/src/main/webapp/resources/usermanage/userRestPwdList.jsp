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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>重置用户密码</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<s:form method="post">
		<div class="rightbg">
			<div class="bgposition">您现在的位置：系统用户管理&gt;重置用户密码</div>
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>登录名</th>
									<td><s:textfield name="userId"></s:textfield></td>
									<th>真实姓名</th>
									<td><s:textfield name="userName"></s:textfield></td>
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
									<td colspan="3">
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
					<li class="bg_table bg_table1">
						<table class="bg_odd">
							<thead>
								<tr>
									<th>序号</th>
									<th>登录名</th>
									<th>真实姓名</th>
									<th>角色</th>
									<th>组织机构</th>
									<th>部门</th>
									<th>职务</th>
									<th>最后修改时间</th>
									<th>最后修改人</th>
									<th>状态</th>
									<th>备注</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody>
								<s:if test="userVoList != null && userVoList.size() > 0">
									<s:iterator value="userVoList" var="userInfo"
										status="rowStatus">
										<tr>
											<td><s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" /></td>
											<td title="<s:property value="#userInfo.userId"/>"><s:property value="#userInfo.userId" /></td>
											<td title="<s:property value="#userInfo.userName"/>"><s:property value="#userInfo.userName" /></td>
											
											<s:set var="roleStr" value=""/>
											<s:iterator value="#userInfo.userRoleList" var="role">
												<s:set name="roleStr">
													<s:property value="#roleStr" />&nbsp;<s:property value="#role.roleName" />&nbsp;</s:set>
											</s:iterator>
											<td title="<s:property value="#roleStr"/>">
												<s:property value="#roleStr" escape="fasle"/>
											</td>
											
											<s:set name="dataPermision" value=""/>
											<s:if test="#userInfo.dataPermisionType ==0 ">
												<s:set name="dataPermision">无</s:set>
											</s:if>
											<s:elseif test="#userInfo.dataPermisionType ==1 ">
												<s:set name="dataPermision">全国</s:set>
											</s:elseif>
											<s:elseif test="#userInfo.dataPermisionType ==2 ">
												<s:set name="dataPermision"><s:property value="#userInfo.dataPermisionProvince.provinceName" /></s:set>
											</s:elseif>
											<s:elseif test="#userInfo.dataPermisionType ==3 ">
												<s:set name="dataPermision"><s:property value="#userInfo.dataPermisionProvince.provinceName" /> <s:property value="#userInfo.dataPermisionCity.cityName" /></s:set>
											</s:elseif>
											<td title="<s:property value="#dataPermision" />"><s:property value="#dataPermision" /></td>
											<td title="<s:property value="#userInfo.department"/>"><s:property value="#userInfo.department" /></td>
											<td title="<s:property value="#userInfo.position"/>"><s:property value="#userInfo.position" /></td>
											<td title="<s:date name="#userInfo.modifyTime" format="yyyy-MM-dd HH:mm:ss" />">
												<s:date name="#userInfo.modifyTime" format="yyyy-MM-dd HH:mm:ss" />
											</td>
											<td title="<s:property value="#userInfo.modifier"/>"><s:property value="#userInfo.modifier" /></td>
											
											<s:set var="stateStr" value=""/>
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
												<s:property value="#stateStr"/>
											</td>
											<td style="text-align: left;" title="<s:property value="#userInfo.remark"/>"><s:property value="#userInfo.remark" /></td>
											<td style="text-align: center;" title="重置密码">
												<s:if test="#userInfo.state == 2">
													<strong>重置密码</strong>
												</s:if>
												<s:else>
													<a href="javascript:void(0);" onclick="toRestUserPwd('<s:property value="#userInfo.iwoid"/>')">重置密码</a>
												</s:else>
											</td>
										</tr>
									</s:iterator>
								</s:if>
								<s:elseif test="userVoList!=null">
									<tr>
										<td colspan="12">无符合条件的查询结果！</td>
									</tr>
								</s:elseif>
							</tbody>
						</table>
					</li>
				</ul>
				<ul>
					<li class="t-center">
						<s:include value="/resources/include/page.jsp"></s:include>
						<input type="hidden" id="iwoid" name="sysUser.iwoid" value=""/>
					</li>
				</ul>
			</div>
		</div>
	</s:form>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		function toRestUserPwd(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('toRestUserPwd');
		}
		$(function() {
			$("#userId").val("${userId}");
			$("#userName").val("${userName}");
			$("#roleOid").val("${roleOid}");
			$("#state").val("${state}");
		});
	</script>
</body>
</html>