<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>缴费账单详情</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：缴费账单管理&gt;缴费账单详情</div>
		<s:form method="post">
			<s:hidden name="totalBillOid"/>
			<div class="bgtj">
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>收费名称：</th>
									<td>${alipayEduTotalBillVO.billName }</td>
									<th>账单发送时间：</th>
									<td><s:date name="alipayEduTotalBillVO.sendTime"  format="yyyy-MM-dd HH:mm:ss"/></td>
									<th>账单到期时间：</th>
									<td><s:date name="alipayEduTotalBillVO.closeTime"  format="yyyy-MM-dd HH:mm:ss"/></td>
								</tr>	
								<tr>
									<th>缴费人数：</th>
									<td>${alipayEduTotalBillVO.totalCount }</td>
									<th>账单总计金额:</th>
									<td><fmt:formatNumber value="${alipayEduTotalBillVO.totalMoney/100}" pattern="###,###,###,##0.00"/> 元</td>
									<th>缴费成功金额：</th>
									<td><fmt:formatNumber value="${alipayEduTotalBillVO.receiptMoney/100}" pattern="###,###,###,##0.00"/> 元</td>
								</tr>
							</tbody>
						</table>
					</li>
				</ul>
				
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>学生姓名</th>
									<td><s:textfield name="childName" id="childName" maxlength="20" /></td>
									<th>学生家长</th>
									<td><s:textfield name="userName" id="userName" maxlength="20" /></td>
									<th>状态</th>
									<td><s:select list="#{'': '','INIT':'待发送','NOT_PAY':'待缴费','PAYING':'支付中','PAY_SUCCESS':' 支付成功，处理中','BILLING_SUCCESS':'缴费成功','TIMEOUT_CLOSED':'逾期关闭账单','ISV_CLOSED':'账单关闭' }" name="orderStatus" id="orderStatus"></s:select></td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<a href="javascript:void(0);" onclick="invokeAction('list');">查询</a>
						<a href="javascript:void(0);" onclick="history.back();">返回</a>
					</li>
				</ul>
			</div>
			<s:set var="changeItemCount" value="4" />
	    	<div class="bgtable">
	            <ul class="bg_all">
	                <li class="bg_table bg_table1">
	                    <table class="bg_odd">
	                        <thead>
	                            <tr>
	                                <th style="width: 40px;">序号</th>
	                                <th>缴费单号</th>
	                                <th>年级/班级</th>
	                                <th>学生姓名</th>
	                                <th>家长手机号</th>
	                                <th>缴费账单名称</th>
	                                
	                                <%--动态列  chargeItem--%>
	                                <s:iterator value="alipayEduTotalBillVO.chargeItemHeaders" var="thName">
	                                	<th><s:property value="#thName" /></th>
	                                </s:iterator>
	                                
	                                <th>合计</th>
	                                <th>账单状态</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="alipayEduBillVOList != null && alipayEduBillVOList.size() > 0">
			  					<s:iterator value="alipayEduBillVOList" var="vo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#vo.outTradeNo" />">
						  				<s:property value="#vo.outTradeNo" />
						  			</td>
						  			<td title="<s:property value="#vo.classIn" />">
						  				<s:property value="#vo.classIn" />
						  			</td>
						  			<td title="<s:property value="#vo.childName" />">
						  				<s:property value="#vo.childName" />
						  			</td>
						  			<td title="<s:property value="#vo.userMobile" />">
						  				<s:property value="#vo.userMobile" />
						  			</td>
						  			<td title="<s:property value="#vo.chargeBillTitle" />">
						  				<s:property value="#vo.chargeBillTitle" />
						  			</td>
						  			<%-- 动态明细金额 --%>
						  			<s:iterator value="chargeItems" var="item">
						  				<td title="<s:property value="#item.itemPrice" />元">
						  					<fmt:formatNumber value="${item.itemPrice}" pattern="###,###,###,##0.00"/> 元
						  				</td>
						  			</s:iterator>
						  			
						  			<td title="<fmt:formatNumber value="${vo.amount/100}" pattern="###,###,###,##0.00"/> 元">
						  				<fmt:formatNumber value="${vo.amount/100}" pattern="###,###,###,##0.00"/> 元
						  			</td>
						  			
						  			<s:if test="#vo.orderStatus == 'INIT'">
						  				<s:set var="orderStatus">待发送</s:set>
						  			</s:if>
						  			<s:elseif test="#vo.orderStatus == 'NOT_PAY'">
						  				<s:set var="orderStatus">待缴费</s:set>
						  			</s:elseif>
					  				<s:elseif test="#vo.orderStatus == 'PAYING'">
						  				<s:set var="orderStatus">支付中</s:set>
						  			</s:elseif>
					  				<s:elseif test="#vo.orderStatus == 'PAY_SUCCESS'">
						  				<s:set var="orderStatus">支付成功，处理中</s:set>
						  			</s:elseif>
					  				<s:elseif test="#vo.orderStatus == 'BILLING_SUCCESS'">
						  				<s:set var="orderStatus">缴费成功</s:set>
						  			</s:elseif>
					  				<s:elseif test="#vo.orderStatus == 'TIMEOUT_CLOSED'">
						  				<s:set var="orderStatus">逾期关闭账单</s:set>
						  			</s:elseif>
					  				<s:elseif test="#vo.orderStatus == 'ISV_CLOSED'">
						  				<s:set var="orderStatus">账单关闭</s:set>
						  			</s:elseif>
						  			
						  			<td title="<s:property value="orderStatus" />">
						  				<s:property value="#orderStatus" />
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="${changeItemCount +8}">无符合条件的查询结果！</td></tr>
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
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
</body>
</html>