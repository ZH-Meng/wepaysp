<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
	response.setContentType("application/vnd.ms-Excel; charset=utf-8");
	response.setHeader("Content-disposition","attachment; filename=userListExport.xls");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:x="urn:schemas-microsoft-com:office:excel">
	<head>
	     <xml>
            <x:ExcelWorkbook>
                <x:ExcelWorksheets>
                    <x:ExcelWorksheet>
                        <x:Name>用户权限查询</x:Name>
                        <x:WorksheetOptions>
                            <x:Print>
                                <x:ValidPrinterInfo />
                            </x:Print>
                        </x:WorksheetOptions>
                    </x:ExcelWorksheet>
                </x:ExcelWorksheets>
            </x:ExcelWorkbook>
        </xml>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>用户权限查询</title>
	</head>
	<style type="text/css">
		body {
			font-family: Arial, Helvetica, sans-serif;
			font-size: 9pt;
			background: #DBDBDB;
			color: #000000;
			margin-left: 0px;
			margin-top: 0px;
			margin-right: 0px;
			margin-bottom: 0px;
		}
		
		td {
			font-size: 9pt;
			background: #FFFFFF;
		}
		.xl70
			{mso-style-parent:style0;
			color:black;
			font-size:9.0pt;
			border:.5pt solid black;
			background:#BFBFBF;
			mso-pattern:black none;
			white-space:normal;}
	</style>
	<body>
		<table width="100%" border="1" align="left" cellpadding="0" class="tablecolor" cellspacing="1">
			<tr>
				<td height="32">
					<span>您现在的位置：系统管理 &gt; 系统用户管理 &gt; 用户权限查询</span>
				</td>
			</tr>
			<tr>
				<td>
					<s:if test="userId!=''"><span>登录名：<s:property value="userId" /></span></s:if>
					<s:if test="userName!=''"><span>真实姓名：<s:property value="userName" /></span></s:if>
					<span>角色：<s:property value="conditionRoleName" /></span>
					<span>状态：<s:property value="conditionStateName" /></span>
				</td>
			</tr>
			<tr>
				<td valign="top">	
					<table width="95%" border="1" align="center" cellpadding="0" cellspacing="1" >
						<tr class=xl70>
							<td nowrap="nowrap">序号</td>
                            <td nowrap="nowrap">登录名</td>
                            <td nowrap="nowrap">真实姓名</td>
                            <td nowrap="nowrap">性别</td>
                            <td nowrap="nowrap">年龄</td>
                            <td nowrap="nowrap">角色</td>
                            <td nowrap="nowrap">部门</td>
                            <td nowrap="nowrap">职务</td>
                            <td nowrap="nowrap">联系电话</td>
                            <td nowrap="nowrap">邮箱</td>
							<!-- <td nowrap="nowrap">创建时间</td>
							<td nowrap="nowrap">创建人</td> -->
                            <td nowrap="nowrap">最后修改时间</td>
                            <td nowrap="nowrap">最后修改人</td>
                            <td nowrap="nowrap">状态</td>
                            <td nowrap="nowrap">备注</td>
						</tr>
						<s:if test="sysUserVOList != null && sysUserVOList.size() > 0">
			  				<s:iterator value="sysUserVOList" var="sysUserVO" status="rowStatus">
							<tr class="bodycolor">
								<td style="mso-number-format: '\@';"><s:property value="#rowStatus.index + 1" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.userId" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.userName" /></td>
					  			<td style="mso-number-format: '\@';">
 					  				<s:if test="#sysUserVO.gender == 0">男</s:if>
 				  					<s:elseif test="#sysUserVO.gender == 1">女</s:elseif>
 					  			</td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.age" /></td>
					  			<td style="mso-number-format: '\@';">
					  				<s:iterator value="#sysUserVO.userRoleList" var="sysRole" status="roleRow">
					  					<s:property value="#sysRole.roleName" />
					  					<s:if test="#sysUserVO.userRoleList.size() != (roleRow.index+1)">，</s:if>
					  				</s:iterator>
								</td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.department" /></td>
 					  			<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.position" /></td>
 					  			<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.lineTel" /></td>
 					  			<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.email" /></td>
 					  			<%-- <td style="mso-number-format: '\@';">
									<s:date name="#sysUserVO.createTime" format="yyyy-MM-dd HH:mm:ss" />
								</td>
								<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.creator" /></td> --%>
					  			<td style="mso-number-format: '\@';"><s:date name="#sysUserVO.modifyTime" format="yyyy-MM-dd HH:mm:ss" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.modifier" /></td>
					  			<td style="mso-number-format: '\@';">
					  				<s:if test="#sysUserVO.state == 0">正常</s:if>
					  				<s:elseif test="#sysUserVO.state == 1">冻结</s:elseif>
					  				<s:elseif test="#sysUserVO.state == 2">注销</s:elseif>
								</td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysUserVO.remark" /></td>
					  		</tr>
					  		</s:iterator>
		  				</s:if>
				  		<s:else>
				  			<tr><td colspan="14">无符合条件的查询结果！</td></tr>
				  		</s:else>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>
