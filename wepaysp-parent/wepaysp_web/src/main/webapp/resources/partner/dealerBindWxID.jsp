<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>商户绑定微信</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<style>
		.qrCode_wrapper{min-height:220px;text-align:center;}
		.qrCode_block{float:left; padding:5px 0px;width:30%;}
		.qrCode_remark_block{text-align:left;padding-top:10px;line-height: 30px;color:#999999;}
		.qrCode_title{display:block;font-weight:bold;}
	</style>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：商户信息管理&gt;>商户信息维护&gt;绑定支付通知</div>
		<div class="bgtj">
			<ul class="tj_title">
				<li>二维码</li>
			</ul>
			<div class="qrCode_wrapper">
				<div class="qrCode_block">
					<img src="<%=request.getContextPath()%>/resources/partner/dealermanage!loadAppidQRCode.action?dealerOid=${dealerVO.iwoid}" alt="公众号二维码" width="200" height="200"/>
					<span class="qrCode_title">公众号二维码</span>
				</div>
				<div class="qrCode_block">
					<img src="<%=request.getContextPath()%>/resources/partner/dealermanage!loadBindQRCode.action?dealerOid=${dealerVO.iwoid}" alt="绑定二维码" width="200" height="200"/>
					<span class="qrCode_title">商户二维码</span>
				</div>
				<div class="qrCode_remark_block">
					说明：
					<p>1、商户在关注公众号后，通过微信扫描“绑定商户二维码”绑定商户</p>
					<p>2、绑定成功后，可在微信查看收款列表和收款汇总信息</p>
				</div>
			</div>
		</div>
		
		<manage:permission validateUrl="/resources/partner/dealermanage!deleteBindWxID.action">
     		<manage:pass>
     			<s:set var="hasDeleteBindWxIDPermission">yes</s:set>
     		</manage:pass>
     		<manage:notPass>
     			<s:set var="hasDeleteBindWxIDPermission">no</s:set>
     		</manage:notPass>
         </manage:permission>
		
		<s:form id="bindListForm" method="post">
			<s:hidden id="dealerOid" name="dealerOid"/>
			<s:hidden id="payNoticeBindWeixinOid" name="payNoticeBindWeixinOid"/>
	    	<div class="bgtable">
	            <ul class="bg_all">
	                <li class="bg_table bg_table1">
	                    <table class="bg_odd">
	                        <thead>
	                            <tr>
                                 	<th>序号</th>
	                                <th>绑定微信</th>
	                                <th>收款通知开关</th>
	                                <th>操作</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="payNoticeBindWeixinVoList != null && payNoticeBindWeixinVoList.size() > 0">
			  					<s:iterator value="payNoticeBindWeixinVoList" var="payNoticeBindWeixinVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:hidden name="payNoticeBindWeixinVoList[%{#rowStatus.index}].iwoid" value="%{#payNoticeBindWeixinVo.iwoid}"/>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#payNoticeBindWeixinVo.nickname" />">
						  				<s:property value="#payNoticeBindWeixinVo.nickname" />
						  			</td>
	                                <td>
	                                	<%-- <s:select list="#{1:'开启',2:'关闭' }" listKey="key" listValue="value" name="payNoticeBindWeixinVoList[%{#rowStatus.index}].state" value="#payNoticeBindWeixinVo.state" cssClass="bind_state" headerKey="" headerValue="请选择"/>
										<span class="tj_bt">*</span>	           --%>   
										<s:if test="#payNoticeBindWeixinVo.state == 1">
							  				开启
							  			</s:if>
							  			<s:else>
							  				关闭
							  			</s:else>
	                                </td>
						  			<td title="操作">
	  									<%-- <s:if test="#hasDeleteBindWxIDPermission eq 'yes'"></s:if> --%>
  										<a href="javascript:void(0);" onclick="toDeleteBind('<s:property value="#payNoticeBindWeixinVo.iwoid" />')">删除</a>
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="4">无绑定记录！</td></tr>
					  		</s:else>
	                    	</tbody>
	               		</table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="refreshBind();return false;">刷新</a>
	                    <a href="javascript:void(0);" onclick="history.back();">返回</a>
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
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<script type="text/javascript">
		function toDeleteBind(iwoid){
			$("#payNoticeBindWeixinOid").val(iwoid);
			invokeAction('deleteBindWxID');
		}
		
		function refreshBind() {
			invokeAction('goToBindWxID');
		}
		
	</script>
</body>
</html>