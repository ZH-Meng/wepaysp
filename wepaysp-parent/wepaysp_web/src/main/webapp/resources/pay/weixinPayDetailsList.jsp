<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>微信交易明细</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<s:if test="partnerVoListLevel == 2 || partnerVoListLevel==3">
		<style>
			.bg_tjall th{width:80px;}
			.bg_tjall input{width:95%;}
		</style>
	</s:if>
	<s:else>
		<style>
			.bg_tjall th{width:10%;}
			.bg_tjall input{width:90%;}
		</style>
	</s:else>
</head>
<body class="bgbj">
	<div class="rightbg">
		<s:if test="userLevel == 1 || userLevel == 2">
			<s:set name="navTag">分润计算</s:set>
		</s:if>
		<s:elseif test="userLevel == 3 || userLevel == 5">
			<s:set name="navTag">资金结算</s:set>
		</s:elseif>
		<div class="bgposition">您现在的位置：<s:property value="#navTag"/>&gt;微信交易明细</div>
		<s:form id="queryForm" method="post">
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<%-- 服务商、业务员、商户 --%>
								<tr>
									<s:if test="userLevel  > 0 && userLevel  <= 3">
										<s:set name="resetFlag" value="true"/>
										<s:if test="userLevel  == 1">
											<s:if test="partnerVoListLevel == 2">
												<th>服务商</th>
				                                <td>
				                                	<s:select list="partnerVoList" listKey="iwoid" listValue="company" name="weixinPayDetailsVO.partner2Oid"  id="partner2Oid" headerKey="" headerValue="全部"/>
				                                	<s:set name="queryCols"  value="9"/>
				                                </td>
											</s:if>
											<s:elseif test="partnerVoListLevel == 3">
												<th>服务商</th>
				                                <td>
				                                	<s:select list="partnerVoList" listKey="iwoid" listValue="company" name="weixinPayDetailsVO.partner3Oid"  id="partner3Oid" headerKey="" headerValue="全部"/>
				                                	<span class="tj_bt">*</span>
				                                	<s:set name="queryCols"  value="9"/>
				                                </td>
											</s:elseif>
											<s:else>
												<s:set name="queryCols"  value="7"/>
											</s:else>
											<th>业务员ID</th>
											<td><s:textfield name="weixinPayDetailsVO.partnerEmployeeId" id="partnerEmployeeId" maxlength="10"/></td>
										</s:if>
										<s:if test="userLevel  == 1 || userLevel  == 2">	
											<th>商家ID</th>
											<td><s:textfield name="weixinPayDetailsVO.dealerId" id="dealerId" maxlength="9"/></td>
											<s:if test="#queryCols == null">
												<s:set name="queryCols"  value="5"/>
											</s:if>
										</s:if>
										<s:if test="#queryCols == null">
											<s:set name="queryCols"  value="3"/>
										</s:if>
										<th>门店ID</th>
										<td><s:textfield name="weixinPayDetailsVO.storeId" id="storeId" maxlength="9"/></td>
										<th>收银员ID</th>
										<td><s:textfield name="weixinPayDetailsVO.dealerEmployeeId" id="dealerEmployeeId" maxlength="10"/></td>
									</s:if>
									<s:if test="#queryCols == null">
										<s:set name="queryCols"  value="1"/>
									</s:if>
								</tr>
								<tr>
									<th>交易开始时间</th>
									<td colspan="<s:property value='#queryCols' />">
										<input style="width:185px;" type="text" name="beginTime" id="beginTime" class="Wdate" readonly="readonly" value="<s:property value="beginTime"/>"
												onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'})"/>
									<span class="tj_bt">*</span>
									<span>交易截止时间</span>
										<input style="width:185px;" type="text" name="endTime" id="endTime" class="Wdate" readonly="readonly" value="<s:property value="endTime"/>"
													onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})" onchange="endTimeChange();"/>
									<span class="tj_bt">*</span>
									</td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<s:if test="userLevel  < 3">
							<a href="javascript:void(0);" onclick="query('list');">查询</a>
						</s:if>
						<s:elseif test="userLevel  >=3 && userLevel <=5">
							<a href="javascript:void(0);" onclick="query('list4Dealer');">查询</a>
						</s:elseif>
						<s:if test="resetFlag">
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
	                                <th>商户订单号</th>
	                                <s:if test="userLevel  < 3">
		                                <th>服务商ID</th>
		                                <th>服务商名称</th>
		                                <th>业务员ID</th>
		                                <th>业务员名称</th>
		                            </s:if>
		                            <th>商家ID</th>
	                                <th>商家名称</th>
	                                <th>门店ID</th>
	                                <th>门店名称</th>
	                                <th>收银员ID</th>
	                                <th>收银员姓名</th>
	                                <th>支付类型</th>
	                                <th>订单金额</th>
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
						  			<s:if test="userLevel  < 3">
						  				<td title="<s:property value="#weixinPayDetailsVo.partnerId" />">
							  				<s:property value="#weixinPayDetailsVo.partnerId" />
							  			</td>
							  			<td title="<s:property value="#weixinPayDetailsVo.partnerName" />">
							  				<s:property value="#weixinPayDetailsVo.partnerName" />
							  			</td>
							  			<td title="<s:property value="#weixinPayDetailsVo.partnerEmployeeId" />">
							  				<s:property value="#weixinPayDetailsVo.partnerEmployeeId" />
							  			</td>
							  			<td title="<s:property value="#weixinPayDetailsVo.partnerEmployeeName" />">
							  				<s:property value="#weixinPayDetailsVo.partnerEmployeeName" />
							  			</td>
							  		</s:if>
							  		<td title="<s:property value="#weixinPayDetailsVo.dealerId" />">
						  				<s:property value="#weixinPayDetailsVo.dealerId" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.dealerName" />">
						  				<s:property value="#weixinPayDetailsVo.dealerName" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.storeId" />">
						  				<s:property value="#weixinPayDetailsVo.storeId" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.storeName" />">
						  				<s:property value="#weixinPayDetailsVo.storeName" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.dealerEmployeeId" />">
						  				<s:property value="#weixinPayDetailsVo.dealerEmployeeId" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.dealerEmployeeName" />">
						  				<s:property value="#weixinPayDetailsVo.dealerEmployeeName" />
						  			</td>
						  			
						  			<s:if test="#weixinPayDetailsVo.payType == 1">
						  				<s:set var="payTypeStr">刷卡支付</s:set>
						  			</s:if>
						  			<s:elseif test="#weixinPayDetailsVo.payType == 2">
						  				<s:set var="payTypeStr">公众号支付</s:set>
						  			</s:elseif>
						  			<s:elseif test="#weixinPayDetailsVo.payType == 3">
						  				<s:set var="payTypeStr">扫码支付</s:set>
									</s:elseif>
									<s:elseif test="#weixinPayDetailsVo.payType == 4">
										<s:set var="payTypeStr">微信买单</s:set>
									</s:elseif>
						  			<td title="<s:property value="#payTypeStr" />">
						  				<s:property value="#payTypeStr" />
						  			</td>
						  			<td class="bgright" title="<fmt:formatNumber value="${weixinPayDetailsVo.totalFee/100}" pattern="###,###,###,###.00"/>">
						  				<fmt:formatNumber value="${weixinPayDetailsVo.totalFee/100}" pattern="###,###,###,###.00"/>
						  			</td>
						  			<s:if test="#weixinPayDetailsVo.resultCode == 'SUCCESS'">
						  				<s:set var="resultCodeStr">交易成功</s:set>
						  			</s:if>
						  			<s:elseif test="#weixinPayDetailsVo.resultCode == ''FAIL">
						  				<s:set var="resultCodeStr">交易失败</s:set>
						  			</s:elseif>
						  			<s:elseif test="#weixinPayDetailsVo.resultCode == NULL || #weixinPayDetailsVo.resultCode == '' ">
						  				<s:set var="resultCodeStr">处理中</s:set>
									</s:elseif>
						  			<td title="<s:property value="resultCodeStr" />">
						  				<s:property value="#resultCodeStr" />
						  			</td>
									<td title="<s:date name="#weixinPayDetailsVo.transBeginTime" format="yyyy-MM-dd HH:mm:ss"/>">
						  				<s:date name="#weixinPayDetailsVo.transBeginTime" format="yyyy-MM-dd HH:mm:ss"/>
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="<s:property value='userLevel<3?16:12'/>">无符合条件的查询结果！</td></tr>
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
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		function query(method) {
			var beginTime = $("#beginTime").val();
			var endTime = $("#endTime").val();
			
			if(isBlank(beginTime)){
				alert('交易开始时间不能为空！');
				return false;
			}
			if(isBlank(endTime)){
				alert('交易截止时间不能为空！');
				return false;
			}
			if (beginTime.substring(0, 7) != endTime.substring(0, 7)) {
				alert("不能跨月查询");
				return;
			}
			invokeAction(method);
		}
	
		function reset() {
			$("#partner2Oid").val("");
			$("#partner3Oid").val("");
			$("#partnerEmployeeId").val("");
			$("#dealerId").val("");
			$("#storeId").val("");
			$("#dealerEmployeeId").val("");
			//$("#beginTime").val("");
			//$("#endTime").val("");
			//$("#queryForm")[0].reset();
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