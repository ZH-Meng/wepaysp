<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>子代理商管理</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：代理商管理&gt;子代理商管理</div>
	    <div class="bgtable">
	        <s:form method="post">
	        	<manage:permission validateUrl="/resources/usermanage/rolemanage!goToCreateRole.action">
	        		<manage:pass>
	        			<ul class="title_button">
			                <li><a href="javascript:void(0);" onclick="toCreateRole()">创建子代理商</a></li>
			            </ul>
	        		</manage:pass>
	            </manage:permission>
	            <manage:permission validateUrl="/resources/usermanage/rolemanage!goToUpdateRole.action">
	        		<manage:pass>
	        			<s:set var="hasUpdatePermission">yes</s:set>
	        		</manage:pass>
	        		<manage:notPass>
	        			<s:set var="hasUpdatePermission">no</s:set>
	        		</manage:notPass>
	            </manage:permission>
	            <ul class="bg_all">
	                <li class="bg_table bg_table1">
	                    <table class="bg_odd">
	                        <thead>
	                            <tr>
	                                <th>序号</th>
	                                <th>子代理商代码</th>
	                                <th>子代理商名称</th>
	                                <th>子代理商级别</th>
	                                <th>子代理商描述</th>
	                                <th>最后修改时间</th>
	                                <th>最后修改人</th>
	                                <th>状态</th>
	                                <th>备注</th>
	                                <th>操作</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="sysRoleList != null && sysRoleList.size() > 0">
			  					<s:iterator value="sysRoleList" var="sysRoleInfo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#sysRoleInfo.roleId" />">
						  				<s:property value="#sysRoleInfo.roleId" />
						  			</td>
						  			<td title="<s:property value="#sysRoleInfo.roleName" />">
						  				<s:property value="#sysRoleInfo.roleName" />
						  			</td>
						  			<s:if test="#sysRoleInfo.roleLevel == 0">
						  				<s:set var="roleLevelStr">应用级别</s:set>
						  			</s:if>
						  			<s:elseif test="#sysRoleInfo.roleLevel == 1">
						  				<s:set var="roleLevelStr">管理级别</s:set>
						  			</s:elseif>
						  			<td title="<s:property value="#roleLevelStr" />">
						  				<s:property value="#roleLevelStr" />
						  			</td>
						  			<td style="text-align: left" title="<s:property value="#sysRoleInfo.description" />">
						  				<s:property value="#sysRoleInfo.description" />
						  			</td>
						  			<td title="<s:date name="#sysRoleInfo.modifyTime" format="yyyy-MM-dd HH:mm:ss" />">
						  				<s:date name="#sysRoleInfo.modifyTime" format="yyyy-MM-dd HH:mm:ss" />
						  			</td>
						  			<td title="<s:property value="#sysRoleInfo.modifier" />">
						  				<s:property value="#sysRoleInfo.modifier" />
						  			</td>
						  			<s:if test="#sysRoleInfo.state == 0">
						  				<s:set var="stateStr">正常</s:set>
						  			</s:if>
						  			<s:elseif test="#sysRoleInfo.state == 1">
						  				<s:set var="stateStr">冻结</s:set>
						  			</s:elseif>
						  			<s:elseif test="#sysRoleInfo.state == 2">
						  				<s:set var="stateStr">注销</s:set>
									</s:elseif>
						  			<td title="<s:property value="#stateStr" />">
						  				<s:property value="#stateStr" />
						  			</td>
						  			<td style="text-align: left" title="<s:property value="#sysRoleInfo.remark" />">
						  				<s:property value="#sysRoleInfo.remark" />
						  			</td>
						  			<td title="修改">
						  				<s:if test="#hasUpdatePermission eq 'yes' && #sysRoleInfo.state != 2 && #sysRoleInfo.buildType != 0">
						  					<a href="javascript:void(0);" onclick="toUpdateRole('<s:property value="#sysRoleInfo.iwoid" />')">修改</a>
						  				</s:if>
						  				<s:else><strong>修改</strong></s:else>
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="10">无符合条件的查询结果！</td></tr>
					  		</s:else>
	                    	</tbody>
	               		</table>
	                </li>
	            </ul>
	            <ul>
	            	<li class="t-center">
	            		<input type="hidden" id="iwoid" name="sysRole.iwoid" value=""/>
	                	<s:include value="/resources/include/page.jsp"></s:include>
                    	<span class="bg_pagebutton"><a href="<%=request.getContextPath()%>/resources/usermanage/rolemanage!exportFile.action">导出Excel</a></span>
	                </li>
	            </ul>
	        </s:form>
	    </div>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		function toCreateRole(){
			invokeAction('goToCreateRole');
		}
		function toUpdateRole(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToUpdateRole');
		}
	</script>
	
</body>
</html>