<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
	response.setContentType("application/vnd.ms-Excel; charset=utf-8");
	response.setHeader("Content-disposition","attachment; filename=roleListExport.xls");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:x="urn:schemas-microsoft-com:office:excel">
	<head>
	     <xml>
            <x:ExcelWorkbook>
                <x:ExcelWorksheets>
                    <x:ExcelWorksheet>
                        <x:Name>角色权限查询</x:Name>
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
		<title>角色权限查询</title>
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
					<span>您现在的位置：系统管理 &gt; 系统用户管理 &gt; 角色权限查询</span>
				</td>
			</tr>
			<tr>
				<td>
					<span>角色名称：<s:property value="conditionRoleName" /></span>
					<span>角色级别：<s:property value="conditionRoleLevel" /></span>
					<span>状态：<s:property value="conditionRoleStatus" /></span>
				</td>
			</tr>
			<tr>
				<td valign="top">	
					<table width="95%" border="1" align="center" cellpadding="0" cellspacing="1" >
						<tr class=xl70>
							<td nowrap="nowrap">序号</td>
							<td nowrap="nowrap">角色代码</td>
							<td nowrap="nowrap">角色名称</td>
							<td nowrap="nowrap">角色级别</td>
							<td nowrap="nowrap">角色描述</td>
							<td nowrap="nowrap">最后修改时间</td>
							<td nowrap="nowrap">最后修改人</td>
							<td nowrap="nowrap">状态</td>
							<td nowrap="nowrap">备注</td>
						</tr>
						<s:if test="sysRoleList != null && sysRoleList.size() > 0">
			  				<s:iterator value="sysRoleList" var="sysRoleInfo" status="rowStatus">
							<tr class="bodycolor">
								<td style="mso-number-format: '\@';"><s:property value="#rowStatus.index + 1" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysRoleInfo.roleId" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysRoleInfo.roleName" /></td>
					  			<td style="mso-number-format: '\@';">
					  				<s:if test="#sysRoleInfo.roleLevel == 0">应用级别</s:if>
					  				<s:elseif test="#sysRoleInfo.roleLevel == 1">管理级别</s:elseif>
					  			</td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysRoleInfo.description" /></td>
					  			<td style="mso-number-format: '\@';"><s:date name="#sysRoleInfo.modifyTime" format="yyyy-MM-dd HH:mm:ss" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysRoleInfo.modifier" /></td>
					  			<td style="mso-number-format: '\@';">
					  				<s:if test="#sysRoleInfo.state == 0">正常</s:if>
					  				<s:elseif test="#sysRoleInfo.state == 1">冻结</s:elseif>
					  				<s:elseif test="#sysRoleInfo.state == 2">注销</s:elseif>
					  			</td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysRoleInfo.remark" /></td>
					  		</tr>
					  		</s:iterator>
		  				</s:if>
				  		<s:else>
				  			<tr><td colspan="9">无符合条件的查询结果！</td></tr>
				  		</s:else>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>
