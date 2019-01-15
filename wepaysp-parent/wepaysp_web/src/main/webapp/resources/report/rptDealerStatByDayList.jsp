<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>按天查看资金结算</title>
	<title></title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：资金结算&gt;按天查看资金结算</div>
		<s:form id="queryForm" method="post">
			<s:hidden name="listType"/>
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li>
						<div class="query_condition">
							<div class="condition_field">
								<span class="field_label">日期</span>
								<strong class="timetj">
                                       <input style="width:185px;" type="text" class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d}'})" 
										name="queryDate" id="queryDate" maxlength="20" value="<s:property value='queryDate'/>" />
                                 </strong>
                            </div>
	                     </div>
					</li>
					<li class="bg_button">
						<a href="javascript:void(0);" onclick="query('listByDay');">查询</a>
					</li>
				</ul>
			</div>
	    	<div class="bgtable">
	            <ul class="bg_all">
	                <li class="bg_table bg_table1">
	                    <table class="bg_odd">
	                        <thead>
	                            <tr>
	                                <th class="six">序号</th>
		                            <th>商家ID</th>
	                                <th>商家名称</th>
	                                <th>门店ID</th>
	                                <th>门店名称</th>
	                                <s:if test="listType == 'dealerEmployee'">
		                                <s:set var="listCols"  value="12"/>
		                                <th>收银员ID</th>
		                                <th>收银员姓名</th>
	                                </s:if>
	                                <s:else>
	                                	<s:set var="listCols"  value="10"/>
	                                </s:else>
	                                <th>退款总笔数</th>
	                                <th>退款总金额</th>
	                                <th>收款总笔数</th>
	                                <th>收款总金额</th>
	                                <th>总金额</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="rptDealerStatVoList != null && rptDealerStatVoList.size() > 0">
			  					<s:iterator value="rptDealerStatVoList" var="rptDealerStatVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#rptDealerStatVo.dealerId" />">
						  				<s:property value="#rptDealerStatVo.dealerId" />
						  			</td>
						  			<td title="<s:property value="#rptDealerStatVo.dealerName" />">
						  				<s:property value="#rptDealerStatVo.dealerName" />
						  			</td>
						  			<td title="<s:property value="#rptDealerStatVo.storeId" />">
						  				<s:property value="#rptDealerStatVo.storeId" />
						  			</td>
						  			<td title="<s:property value="#rptDealerStatVo.storeName" />">
						  				<s:property value="#rptDealerStatVo.storeName" />
						  			</td>
						  			<s:if test="listType == 'dealerEmployee'">
							  			<td title="<s:property value="#rptDealerStatVo.dealerEmployeeId" />">
							  				<s:property value="#rptDealerStatVo.dealerEmployeeId" />
							  			</td>
							  			<td title="<s:property value="#rptDealerStatVo.dealerEmployeeName" />">
							  				<s:property value="#rptDealerStatVo.dealerEmployeeName" />
							  			</td>
						  			</s:if>
									<td class="bgright"  title="<s:property value="#rptDealerStatVo.refundAmount" />">
						  				<s:property value="#rptDealerStatVo.refundAmount" />
						  			</td>
						  			<td class="bgright" title="<fmt:formatNumber value="${rptDealerStatVo.refundMoney/100}" pattern="###,###,###,##0.00"/>">
						  				<fmt:formatNumber value="${rptDealerStatVo.refundMoney/100}" pattern="###,###,###,##0.00"/>
						  			</td>
						  			<td class="bgright"  title="<s:property value="#rptDealerStatVo.payAmount" />">
						  				<s:property value="#rptDealerStatVo.payAmount" />
						  			</td>
						  			<td class="bgright" title="<fmt:formatNumber value="${rptDealerStatVo.payMoney/100}" pattern="###,###,###,##0.00"/>">
						  				<fmt:formatNumber value="${rptDealerStatVo.payMoney/100}" pattern="###,###,###,##0.00"/>
						  			</td>
						  			<td class="bgright" title="<fmt:formatNumber value="${rptDealerStatVo.totalMoney/100}" pattern="###,###,###,##0.00"/>">
						  				<fmt:formatNumber value="${rptDealerStatVo.totalMoney/100}" pattern="###,###,###,##0.00"/>
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="<s:property value ='#listCols'/>">无符合条件的查询结果！</td></tr>
					  		</s:else>
	                    	</tbody>
	               		</table>
	                </li>
	            </ul>
	            <ul>
	            	<li class="t-center">
	                	<s:include value="/resources/include/noPage.jsp"></s:include>
	                </li>
	            </ul>
	    	</div>
	    </s:form>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		function query(method) {
			var queryDate = $("#queryDate").val();
			if(isBlank(queryDate)) {
				alert('查询日期不能为空！');
				return false;
			}
 			invokeAction(method);
		}
	</script>
</body>
</html>