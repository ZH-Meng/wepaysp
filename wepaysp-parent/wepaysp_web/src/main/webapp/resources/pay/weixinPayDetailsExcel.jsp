<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%@ page import="java.net.URLEncoder" %>
<%
	String d = (String) request.getAttribute("nowDateStr");// yyyyMMdd
	response.setContentType("application/vnd.ms-Excel; charset=utf-8");
	//response.setHeader("Content-disposition","attachment; filename="+new String(("微信交易明细数据导出"+ d ).getBytes("gbk"),"iso8859-1")+".xls");
	String fileName = "微信交易明细数据-" + d + ".xls";
	response.setHeader("Content-disposition","attachment; ;filename*=UTF-8''" + URLEncoder.encode(fileName,"UTF-8")); 
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:x="urn:schemas-microsoft-com:office:excel">
	<head>
	     <xml>
            <x:ExcelWorkbook>
                <x:ExcelWorksheets>
                    <x:ExcelWorksheet>
                        <x:Name>微信交易明细数据</x:Name>
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
		<title>微信交易明细数据</title>
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
			<%-- <tr>
				<td height="32">
					<span>您现在的位置：交易查询&gt;微信交易明细</span>
				</td>
			</tr> --%>
			
			<tr>
				<td>
					总笔数：${totalVO.totalAmount }，支付总金额：
				<s:if test="totalVO.totalMoney == 0">0元</s:if>	
					<s:else>
						<fmt:formatNumber value="${totalVO.totalMoney / 100}" pattern="###,###,###,##0.00"/>元
					</s:else>				
				</td>
			</tr>
			<tr class=xl70>
				<td nowrap="nowrap" style="text-align: right">金额单位：元</td>
			</tr>
			<tr>
				<td valign="top">	
					<table width="95%" border="1" align="center" cellpadding="0" cellspacing="1" >
						<tr class=xl70>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">序号</td>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">商户订单号</td>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">微信订单号</td>
								<s:if test="userLevel  < 3">
									<td align="center" nowrap="nowrap" style="font-weight: bold;">服务商</td>
									<td align="center" nowrap="nowrap" style="font-weight: bold;">业务员</td>
								</s:if>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">商家</td>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">门店</td>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">收银员</td>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">支付类型</td>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">订单金额</td>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">状态</td>
								<td align="center" nowrap="nowrap" style="font-weight: bold;">时间</td>
						</tr>
						
						<s:if test="weixinPayDetailsVoList != null && weixinPayDetailsVoList.size() > 0">
			  					<s:iterator value="weixinPayDetailsVoList" var="weixinPayDetailsVo" status="rowStatus">
					 			<tr>
					 				<td align="center"><s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" /></td>
						  			<td align="center" style="mso-number-format: '\@';"><s:property value="#weixinPayDetailsVo.outTradeNo" /></td>
						  			<td align="center" style="mso-number-format: '\@';"><s:property value="#weixinPayDetailsVo.transactionId" /></td>
						  			<s:if test="userLevel  < 3">
							  			<td align="center" style="mso-number-format: '\@';">
							  				<s:property value="#weixinPayDetailsVo.partnerId" /><br />
							  				<s:property value="#weixinPayDetailsVo.partnerName" />
						  				</td>
							  			<td align="center" style="mso-number-format: '\@';">
							  				<s:property value="#weixinPayDetailsVo.partnerEmployeeId" /><br />
							  				<s:property value="#weixinPayDetailsVo.partnerEmployeeName" />
						  				</td>
					  				</s:if>
						  			<td align="center" style="mso-number-format: '\@';">
						  				<s:property value="#weixinPayDetailsVo.dealerId" /><br />
						  				<s:property value="#weixinPayDetailsVo.dealerName" />
					  				</td>
						  			<td align="center"  style="mso-number-format: '\@';">
						  				<s:property value="#weixinPayDetailsVo.storeId" /><br />
						  				<s:property value="#weixinPayDetailsVo.storeName" />
					  				</td>
						  			<td align="center"  style="mso-number-format: '\@';">
						  				<s:property value="#weixinPayDetailsVo.dealerEmployeeId" /><br />
						  				<s:property value="#weixinPayDetailsVo.dealerEmployeeName" />
					  				</td>
									<s:if test="#weixinPayDetailsVo.payType == 1">
						  				<s:set var="payTypeStr">刷卡支付</s:set>
						  			</s:if>
						  			<s:elseif test="#weixinPayDetailsVo.payType == 2">
						  				<%--公众号 --%>
						  				<s:set var="payTypeStr">扫码支付</s:set>
						  			</s:elseif>
						  			<s:elseif test="#weixinPayDetailsVo.payType == 3">
						  				<s:set var="payTypeStr">扫码支付</s:set>
									</s:elseif>
									<s:elseif test="#weixinPayDetailsVo.payType == 4">
										<s:set var="payTypeStr">微信买单</s:set>
									</s:elseif>
									<s:else>
										<s:set var="payTypeStr">未知</s:set>
									</s:else>
						  			<td align="center" style="mso-number-format: '\@';"><s:property value="#payTypeStr" /></td>
						  			
						  			<td align="right"  style="mso-number-format: '\@';"><fmt:formatNumber value="${weixinPayDetailsVo.totalFee/100}" pattern="###,###,###,##0.00"/></td>
						  			
									<s:if test="#weixinPayDetailsVo.tradeStatus == 0">
					  				<s:set var="tradeStatusStr">交易中</s:set>
						  			</s:if>
						  			<s:elseif test="#weixinPayDetailsVo.tradeStatus == 1">
						  				<s:set var="tradeStatusStr">交易成功</s:set>
						  			</s:elseif>
						  			<s:elseif test="#weixinPayDetailsVo.tradeStatus == 2">
						  				<s:set var="tradeStatusStr">交易失败</s:set>
						  			</s:elseif>
						  			<s:elseif test="#weixinPayDetailsVo.tradeStatus == 3">
						  				<s:set var="tradeStatusStr">交易撤销</s:set>
						  			</s:elseif>
						  			<s:elseif test="#weixinPayDetailsVo.tradeStatus == 4">
						  				<s:set var="tradeStatusStr">交易关闭</s:set>
									</s:elseif>
									<s:elseif test="#weixinPayDetailsVo.tradeStatus == 5">
										<%--待关闭 --%>
						  				<s:set var="tradeStatusStr">交易取消</s:set>
									</s:elseif>
									<s:else>
										<s:set var="tradeStatusStr">未知</s:set>
									</s:else>
						  			<td align="center" style="mso-number-format: '\@';"><s:property value="#tradeStatusStr" /></td>
						  			
						  			<td align="center" style="mso-number-format: '\@';"><s:date name="#weixinPayDetailsVo.transBeginTime" format="yyyy-MM-dd HH:mm:ss"/></td>
					 			</tr>
					 		</s:iterator>
		  				</s:if>
		  				<s:else>
				  			<tr><td align="center" colspan="<s:property value='userLevel<3?12:10'/>">无符合条件的查询结果！</td></tr>
				  		</s:else>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>
