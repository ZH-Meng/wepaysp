<%--
/*
* 
* 创建者：杨帆
* 创建日期：2015年7月1日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>角色权限管理</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/tools/dtree/dtree.css" type="text/css" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/dtree/dtree.js"></script>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：系统用户管理&gt;角色权限管理</div>
    	<div class="bgtj">
    		<form action="#" method="post">
    			<input type="hidden" name="sysRoleName" value="<s:property value="sysRoleName" />" />
    			<div class="clear juese_m">
    				<div class="js-m-left">
    					<ul>
    						<s:if test="sysRoleList != null && sysRoleList.size() > 0">
			  					<s:iterator value="sysRoleList" var="sysRoleInfo">
			  					<li>
			  						<a href="<%=request.getContextPath()%>/resources/usermanage/rolepermissionmanage.action?sysRoleOid=<s:property value="#sysRoleInfo.iwoid" />">
			  							<s:property value="#sysRoleInfo.roleName" />
			  						</a>
			  					</li>
						  		</s:iterator>
			  				</s:if>
	    				</ul>
    				</div>
    				<div class="js-m-right" style="padding : 0">
    					<s:if test="functionStr != null">
    					<div class="header-1">角色名称：<s:property value="sysRoleName" /></div>
    					<div style="padding: 2%"><script type="text/javascript">
							d = new dTree('d', '<%=request.getContextPath()%>', '/tools/dtree/', '<s:property value="functionStr" escape="false" />', 'mainFrame', true, '<s:property value="roleFunctionStr" escape="false" />');
							d.build();
							document.write(d);
						</script>
    					</div>
    					<p class="mg-30" style="margin: 0 0 2% 2% ">
    						<s:submit name="assign" value="确定" method="assignResource" cssClass="btn btn-1" onclick="return checkFormData()"/>
    					</p>	
    					</s:if>
    				</div>
    			</div>
        	</form>
    	</div>
	</div>
	<script type="text/javascript">
		function checkFormData(){
			if (!confirm("确认授权？")) {
				return false;
			}
			
			var hasChoose = false;
			var itemArr = document.getElementsByName("nodeId");
				
			for(var i = 0; i < itemArr.length; i++){
				if(itemArr[i].checked){
					hasChoose = true;
				}
			}
				
			if(!hasChoose){
				alert("请至少选择一个功能项");
				return false;
			}
			
			return true;
		}
	</script>
	<s:property value="#request.messageBean.alertMessage" escape="false" /> 
</body>
</html>