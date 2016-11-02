<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>商户门店资金结算</title>
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
		<div class="bgposition">您现在的位置：资金结算&gt;商户门店资金结算</div>
		<s:form id="queryForm" method="post">
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>时间</th>
									<td>
										<strong class="timetj">
	                                        <input class="dxbtn" type="radio" id="queryType1" name="queryType" value="day"/>自定义时段
	                                        <input onclick="typeChange('1');" type="text" class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d-1}'})" 
											name="beginTime" id="beginTime" maxlength="20" value="" />
	                                        <span>至</span>
	                                        <input onclick="typeChange('1');" type="text" class="Wdate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d-1}'})"
											name="endTime" id="endTime" maxlength="20" value="" />
	                                        <span class="tj_bt">*</span>
	                                    </strong>
	                                    <strong class="timetj">
	                                    	<input class="dxbtn" type="radio"  id="queryType2" name="queryType" value="month" />按月查询
											<input onclick="typeChange('2');" type="text" class="Wdate" runat="server" readonly="readonly" onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM',maxDate:'%y-{%M-1}-%d'})" 
											name="monthTime" id="monthTime" maxlength="20" value="<s:date name="monthTime" format="yyyy-MM"/>" />	                                    	
											<span class="tj_bt">*</span>
	                                    </strong>
									</td>
									<th>门店</th>
	                                <td>
	                                	<s:select list="storeVoList" listKey="iwoid" listValue="storeName" name="dealerEmployeeVO.storeOid"  id="storeOid" headerKey="" headerValue="请选择"/>
	                                	<span class="tj_bt">*</span>
	                                </td>
									<th>收银员ID</th>
									<td><s:textfield name="weixinPayDetailsVO.dealerEmployeeId" id="dealerEmployeeId" maxlength="20"/></td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<a href="javascript:void(0);" onclick="query('list');">查询</a>
						<a href="javascript:void(0);" onclick="reset();" >重写</a>
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
	                                <th>收银员ID</th>
	                                <th>收银员姓名</th>
	                                <th>总笔数</th>
	                                <th>总金额</th>
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
							  			<td title="<s:property value="#weixinPayDetailsVo.partnerName" />">
							  				<s:property value="#weixinPayDetailsVo.partnerName" />
							  			</td>
							  			<td title="<s:property value="#weixinPayDetailsVo.partnerEmployeeName" />">
							  				<s:property value="#weixinPayDetailsVo.partnerEmployeeName" />
							  			</td>
							  		</s:if>
						  			<td title="<s:property value="#weixinPayDetailsVo.dealerName" />">
						  				<s:property value="#weixinPayDetailsVo.dealerName" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.storeName" />">
						  				<s:property value="#weixinPayDetailsVo.storeName" />
						  			</td>
						  			<td title="<s:property value="#weixinPayDetailsVo.dealerEmployeeName" />">
						  				<s:property value="#weixinPayDetailsVo.dealerEmployeeName" />
						  			</td>
						  			
						  			<s:if test="#weixinPayDetailsVo.payType == 1">
						  				<s:set var="payTypeStr" value="刷卡支付" />
						  			</s:if>
						  			<s:elseif test="#weixinPayDetailsVo.payType == 2">
						  				<s:set var="payTypeStr" value="公众号支付" />
						  			</s:elseif>
						  			<s:elseif test="#weixinPayDetailsVo.payType == 3">
						  				<s:set var="payTypeStr" value="扫码支付 " />
									</s:elseif>
									<s:elseif test="#weixinPayDetailsVo.payType == 4">
						  				<s:set var="payTypeStr" value="微信买单 " />
									</s:elseif>
						  			<td title="<s:property value="#payTypeStr" />">
						  				<s:property value="#payTypeStr" />
						  			</td>
						  			
						  			<td title="<s:property value="#weixinPayDetailsVo.totalFee" />">
						  				<s:property value="#weixinPayDetailsVo.totalFee" />
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
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		function typeChange(type){
			if(type=="1"){
				$("#queryType1").attr("checked","checked");
			}else if(type=="2"){
				$("#queryType2").attr("checked","checked");
			}
		}
		
		function query(method) {
			var beginTime = $("#beginTime").val();
			var endTime = $("#endTime").val();
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
			$("#beginTime").val("");
			//$("#endTime").val("");
			//$("#queryForm")[0].reset();
		}
	</script>
</body>
</html>