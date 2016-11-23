<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<s:if test="listType == 'dealer'">
		<s:set name="title">商户门店资金结算</s:set>
	</s:if>
	<s:elseif test="listType == 'dealerEmployee'">
		<s:set name="title">商户员工资金结算</s:set>
	</s:elseif>
	<title><s:property value="#title"/></title>
	<title></title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：资金结算&gt;<s:property value="#title"/></div>
		<s:form id="queryForm" method="post">
			<s:hidden name="listType"/>
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<s:set name="queryCols"  value="1"/>
								<%-- 商户 --%>
								<s:if test="userLevel == 3">
									<tr>
										<th>门店</th>
		                                <td>
		                                	<s:select list="storeVoList" listKey="iwoid" listValue="storeName" name="rptDealerStatVO.storeOid"  id="storeOid" headerKey="" headerValue="全部"/>
		                                </td>
		                                <s:if test="listType == 'dealerEmployee'">
		                                	<s:set name="queryCols"  value="3"/>
											<th>收银员ID</th>
											<td><s:textfield name="rptDealerStatVO.dealerEmployeeId" id="dealerEmployeeId" maxlength="10"/>
												<span>输入收银员ID精确搜索</span>
											</td>
		                                </s:if>
									</tr>
								</s:if>
								<tr>
									<th>时间</th>
									<td colspan="<s:property value ='#queryCols'/>">
										<strong class="timetj">
	                                        <input class="dxbtn" type="radio" id="queryType1" name="queryType" value="day"/>自定义时段
	                                        <input onclick="typeChange('day');" type="text" class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'})" 
											name="beginTime" id="beginTime" maxlength="20" value="<s:property value="beginTime"/>" />
	                                        <span>至</span>
	                                        <input onclick="typeChange('day');" type="text" class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d-1}'})"
											name="endTime" id="endTime" maxlength="20" value="<s:property value="endTime"/>" onchange="endTimeChange();" />
	                                        <span class="tj_bt">*</span>
	                                    </strong>
	                                    <strong class="timetj">
	                                    	<input class="dxbtn" type="radio"  id="queryType2" name="queryType" value="month" />按月查询
											<input onclick="typeChange('month');" type="text" class="Wdate" runat="server" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM',maxDate:'%y-{%M-1}-%d'})" 
											name="monthTime" id="monthTime" maxlength="20" value="<s:property value="monthTime"/>" />	                                    	
											<span class="tj_bt">*</span>
	                                    </strong>
									</td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<a href="javascript:void(0);" onclick="query('list');">查询</a>
						<s:if test="userLevel ==3">
							<a href="javascript:void(0);" onclick="reset();" >重写</a>
						</s:if>
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
		                                <s:set name="listCols"  value="9"/>
		                                <th>收银员ID</th>
		                                <th>收银员姓名</th>
	                                </s:if>
	                                <s:else>
	                                	<s:set name="listCols"  value="7"/>
	                                </s:else>
	                                <th>总笔数</th>
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
									<td class="bgright"  title="<s:property value="#rptDealerStatVo.totalAmount" />">
						  				<s:property value="#rptDealerStatVo.totalAmount" />
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
		$(function() {
			typeChange("<s:property value='queryType' />");
		});
		
		function typeChange(type){
			if(type=="day"){
				$("#queryType1").attr("checked","checked");
			}else if(type=="month"){
				$("#queryType2").attr("checked","checked");
			}
		}
		
		function query(method) {
			if (!checkParam()) {
				return;
			}
 			invokeAction(method);
		}
	
		function reset() {
			$("#storeOid").val("");
			$("#dealerEmployeeId").val("");
		}
		
		function checkParam() {
			var queryType = $(":radio:checked").val();
			if (queryType == "day") {
				var beginTimeStr = $("input[name='beginTime']").val();
				var endTimeStr = $("input[name='endTime']").val();
				if(isBlank(beginTimeStr)){
					alert('开始时间不能为空！');
					return false;
				}
				if(isBlank(endTimeStr)){
					alert('结束时间不能为空！');
					return false;
				}
				if (DateDiff(endTimeStr, beginTimeStr) < 0) {
					alert('开始时间必须小于结束时间！');
					return false;
				}
                if (monthDiff(endTimeStr, beginTimeStr) > 2) {
                	alert('开始时间与结束时间的间隔不能大于2个月！');
					return false;
                }
			} else if (queryType == "month") {
				var monthTime = $("#monthTime").val();
				if (isBlank(monthTime)) {
					alert("月份不能为空！");
					return false;
				}
			} else {
				return false;
			}
			return true;
		}
		
		function endTimeChange() {
			var beginTime = $("#beginTime").val();
			var endTime = $("#endTime").val();
			if (endTime < beginTime) {
				$("#beginTime").val(endTime);
			}
		}
	</script>
</body>
</html>