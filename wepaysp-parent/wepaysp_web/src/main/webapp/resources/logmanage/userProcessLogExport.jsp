<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
	response.setContentType("application/vnd.ms-Excel; charset=utf-8");
	response.setHeader("Content-disposition","attachment; filename=userProcessLogExport.xls");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:x="urn:schemas-microsoft-com:office:excel">
	<head>
	     <xml>
            <x:ExcelWorkbook>
                <x:ExcelWorksheets>
                    <x:ExcelWorksheet>
                        <x:Name>系统用户操作日志</x:Name>
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
		<title>系统用户操作日志</title>
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
					<span>您现在的位置：系统管理 &gt; 日志管理 &gt; 系统用户操作日志</span>
				</td>
			</tr>
			<tr>
				<td>
					<s:if test="userId!=''"><span>登录名：<s:property value="userId" /></span></s:if>
					<s:if test="userName!=''"><span>真实姓名：<s:property value="userName" /></span></s:if>
					<span>角色：<s:property value="conditionRoleName" /></span>
					<span>日志类型：<s:property value="conditionLogTypeName" /></span>
					<span>功能名称：<s:property value="conditionFunctionName" /></span>
					<span>操作名称：<s:property value="conditionActionTypeName" /></span>
					<span>操作开始日期：<s:date name='#request.processBeginTime' format='yyyy-MM-dd' /></span>
					<span>操作结束日期：<s:date name='#request.processEndTime' format='yyyy-MM-dd' /></span>
				</td>
			</tr>
			<tr>
				<td valign="top">	
					<table width="95%" border="1" align="center" cellpadding="0" cellspacing="1" >
						<tr class=xl70>
							<td nowrap="nowrap">序号</td>
                            <td nowrap="nowrap">登录名</td>
                            <td nowrap="nowrap">真实姓名</td>
                            <td nowrap="nowrap">角色</td>
                            <td nowrap="nowrap">日志类型</td>
                            <td nowrap="nowrap">功能名称</td>
                            <td nowrap="nowrap">操作名称</td>
                            <td nowrap="nowrap">操作前数据</td>
                            <td nowrap="nowrap">操作后数据</td>
                            <td nowrap="nowrap">日志描述</td>
                            <td nowrap="nowrap">操作开始时间</td>
                            <td nowrap="nowrap">操作结束时间</td>
                            <td nowrap="nowrap">执行状态</td>
						</tr>
						<s:if test="sysLogVoList != null && sysLogVoList.size() > 0">
			  				<s:iterator value="sysLogVoList" var="sysLogVo" status="rowStatus">
							<tr class="bodycolor">
								<td style="mso-number-format: '\@';"><s:property value="#rowStatus.index + 1" /></td>
								<td style="mso-number-format: '\@';"><s:property value="#sysLogVo.sysUser.userId" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysLogVo.sysUser.userName" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysLogVo.roleName" /></td>
					  			<td style="mso-number-format: '\@';">
					  				<s:if test="#sysLogVo.logType== 0">用户操作</s:if>
					  				<s:elseif test="#sysLogVo.logType == 1">用户登录操作</s:elseif>
					  				<s:elseif test="#sysLogVo.logType == 2">用户退出操作</s:elseif>
					  				<s:elseif test="#sysLogVo.logType == 3">用户处理</s:elseif>
								</td>
								<td style="mso-number-format: '\@';"><s:property value="#sysLogVo.functionName" /></td>
					  			<td style="mso-number-format: '\@';">
					  				<s:if test="#sysLogVo.actionType== 0">新建</s:if>
					  				<s:elseif test="#sysLogVo.actionType == 1">修改</s:elseif>
					  				<s:elseif test="#sysLogVo.actionType == 2">导出</s:elseif>
					  				<s:elseif test="#sysLogVo.actionType == 3">删除</s:elseif>
					  				<s:elseif test="#sysLogVo.actionType == 4">重置密码</s:elseif>
					  				<s:elseif test="#sysLogVo.actionType == 5">充值</s:elseif>
					  				<s:elseif test="#sysLogVo.actionType == 6">批量导入</s:elseif>
								</td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysLogVo.dataBefore" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysLogVo.dataAfter" /></td>
					  			<td style="mso-number-format: '\@';"><s:property value="#sysLogVo.logAbstract" /></td>
					  			<td style="mso-number-format: '\@';"><s:date name="#sysLogVo.processTimeBegin" format="yyyy-MM-dd HH:mm:ss" /></td>
					  			<td style="mso-number-format: '\@';"><s:date name="#sysLogVo.processTimeEnd" format="yyyy-MM-dd HH:mm:ss" /></td>
					  			<td style="mso-number-format: '\@';">
					  				<s:if test="#sysLogVo.state== 1">成功</s:if>
					  				<s:elseif test="#sysLogVo.state == 0">受理中</s:elseif>
					  				<s:elseif test="#sysLogVo.state == 2">受理失败</s:elseif>
					  			</td>
					  		</tr>
					  		</s:iterator>
		  				</s:if>
				  		<s:else>
				  			<tr><td colspan="13">无符合条件的查询结果！</td></tr>
				  		</s:else>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>
