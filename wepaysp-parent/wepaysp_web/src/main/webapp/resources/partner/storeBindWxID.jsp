<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>门店信息管理</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：商户信息管理&gt;门店信息管理&gt;绑定支付通知</div>
		<div class="bgtj">
			<ul class="tj_title">
				<li>二维码</li>
			</ul>
			<ul>
				<li>
					<div class="qrCode_block">
						<img src="<%=request.getContextPath()%>/resources/partner/storemanage!loadBindQRCode.action?storeOid=${storeOid}" alt="支付通知二维码" width="200" height="200"/>
						<div>绑定支付通知二维码</div>
					</div>
				</li>
			</ul>
		</div>
<!-- 		<div class="qrCode_block"> -->
<%-- 			<img src="<%=request.getContextPath()%>/resources/partner/storemanage!loadBindQRCode.action?storeOid=${storeOid}" alt="支付通知二维码" width="200" height="200"/> --%>
<!-- 		</div> -->
		
		<form id="bindListForm" action="<%=request.getContextPath()%>/resources/partner/storemanage!batchUpdateBindWxID.action"" method="post">
	    	<div class="bgtable">
	            <ul class="bg_all">
	                <li class="bg_table bg_table1">
	                    <table class="bg_odd">
	                        <thead>
	                            <tr>
	                                <th style="width: 40px;">序号</th>
	                                <th>绑定人</th>
	                                <th>收银员</th>
	                                <th>状态</th>
	                                <th>操作</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="payNoticeBindWeixinVoList != null && payNoticeBindWeixinVoList.size() > 0">
			  					<s:iterator value="payNoticeBindWeixinVoList" var="payNoticeBindWeixinVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#payNoticeBindWeixinVo.nickname" />">
						  				<s:property value="#payNoticeBindWeixinVo.nickname" />
						  			</td>
						  			<td title="<s:property value="#payNoticeBindWeixinVo.nickname" />">
						  				<s:property value="#payNoticeBindWeixinVo.nickname" />
						  			</td>
						  			<td title="<s:property value="#payNoticeBindWeixinVo.state" />">
						  				<s:property value="#payNoticeBindWeixinVo.state" />
						  			</td>
						  			<td title="操作">
	  									<a href="javascript:void(0);" onclick="toDeleteBind('<s:property value="#payNoticeBindWeixinVo.iwoid" />')">删除</a>
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="5">无绑定记录！</td></tr>
					  		</s:else>
	                    	</tbody>
	               		</table>
	                </li>
	                <s:if test="payNoticeBindWeixinVoList != null && payNoticeBindWeixinVoList.size() > 0">
		                <li class="bg_button">
		                    <a href="javascript:void(0);" onclick="toBatchUpdate();return false;">保存</a>
		                </li>
	                </s:if>
	            </ul>
	            <ul>
	            	<li class="t-center">
	                	<s:include value="/resources/include/noPage.jsp"></s:include>
	                </li>
	            </ul>
	    	</div>
	    </form>
	</div>
	<div>
		说明：
		<p>1、商家可设置1名管理员和最多5名店员；</p>
		<p>顾客付款后，店员可收到支付通知，用于交易确认；</p>
		<p>店员通过微信扫描“收款二维码”，可在移动端查看收款列表和收款汇总；管理员还可以进行店员和收款码等功能管理；</p>		
	</div>
	
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript">
		function toDeleteBind(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToDeleteBindWxID');
		}
		function toBatchUpdate(iwoid){
			$("#iwoid").val(iwoid);
			$("#bindListForm").submit();
		}
	</script>
</body>
</html>