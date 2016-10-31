<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>微信退款明细</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：分润计算&gt;微信退款明细</div>
		<s:form method="post">
			<s:hidden id="iwoid" name="dealerEmployeeVO.iwoid"/>
			<s:hidden id="resetFlag" name="resetFlag"/>
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>代理商</th>
	                                <td>
	                                	<s:select list="partnerVoList" listKey="iwoid" listValue="company" name="weixinPayDetailsVO.partnerOid"  id="partnerOid" headerKey="" headerValue="全部"/>
	                                	<span class="tj_bt">*</span>
	                                </td>
									<th>业务员</th>
									<td><s:textfield name="weixinPayDetailsVO.partnerEmployeeName" id="partnerEmployeeName" maxlength="20"/></td>
									<th>商家</th>
									<td><s:textfield name="weixinPayDetailsVO.dealerName" id="dealerName" maxlength="20"/></td>
									<th>门店</th>
									<td><s:textfield name="weixinPayDetailsVO.storeName" id="storeName" maxlength="20"/></td>
									<th>收银员</th>
									<td><s:textfield name="weixinPayDetailsVO.dealerEmployeeName" id="dealerEmployeeName" maxlength="20"/></td>
									<th>交易时间</th>
									<td>
										<input type="text" name="beginTime" id="beginTime" class="Wdate" readonly="readonly" value="<s:property value="beginTime"/>"
												onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'})"/>
										<span class="tj_bt">*</span>
										<span>至</span>
										<input type="text" name="endTime" id="endTime"	class="Wdate" readonly="readonly" value="<s:property value="endTime"/>"
													onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'beginTime\')}'})"/>
										<span class="tj_bt">*</span>
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
	                                <th class="six">序号</th>
	                                <th>商户订单号</th>
	                                <th>服务商名称</th>
	                                <th>业务员</th>
	                                <th>商家名称</th>
	                                <th>门店名称</th>
	                                <th>收银员</th>
	                                <th>退款人</th>
	                                <th>订单金额</th>
	                                <th>退款金额</th>
	                                <th>状态</th>
	                                <th>时间</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="weixinPayDetailsVoList != null && weixinPayDetailsVoList.size() > 0">
			  					<s:iterator value="weixinPayDetailsVoList" var="weixinPayDetailsVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.outTradeNo" />">
						  				<s:property value="#weixinPayDetailsVo.outTradeNo" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.partnerName" />">
						  				<s:property value="#weixinPayDetailsVo.partnerName" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.partnerEmployeeName" />">
						  				<s:property value="#weixinPayDetailsVo.partnerEmployeeName" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.dealerName" />">
						  				<s:property value="#weixinPayDetailsVo.dealerName" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.storeName" />">
						  				<s:property value="#weixinPayDetailsVo.storeName" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.dealerEmployeeName" />">
						  				<s:property value="#weixinPayDetailsVo.dealerEmployeeName" />
						  			</td>
									<td title="<s:property value="#weixinPayDetailsVo.refundEmployeeName" />">
						  				<s:property value="#weixinPayDetailsVo.refundEmployeeName" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.totalFee" />">
						  				<s:property value="#weixinPayDetailsVo.totalFee" />
						  			</td>
									<td title="<s:property value="#weixinPayDetailsVo.refundFee" />">
						  				<s:property value="#weixinPayDetailsVo.refundFee" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.resultCode" />">
						  				<s:property value="#weixinPayDetailsVo.resultCode" />
						  			</td>
									<td title="<s:property value="#weixinPayDetailsVo.timeEnd" />">
						  				<s:property value="#weixinPayDetailsVo.timeEnd" />
						  			</td>
						  			
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="12">无符合条件的查询结果！</td></tr>
					  		</s:else>
	                    	</tbody>
	               		</table>
	                </li>
	            </ul>
	            <ul>
	            	<li class="t-center">
	                	<s:include value="/resources/include/page.jsp"></s:include>
	                </li>
	            </ul>
	    	</div>
	    </s:form>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
	
	</script>
</body>
</html>