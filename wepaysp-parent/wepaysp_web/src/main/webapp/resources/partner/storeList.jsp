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
		<div class="bgposition">您现在的位置：商户信息管理&gt;门店信息管理</div>
		<s:form method="post">
			<s:hidden id="iwoid" name="storeVO.iwoid"/>
			<s:hidden id="storeOid" name="storeOid"/>
			<s:hidden id="dealerOid" name="dealerOid"/>
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>联系电话</th>
									<td><s:textfield name="storeVO.storeTel" id="storeTel" maxlength="20"/></td>
									<th>门店名称</th>
									<td><s:textfield name="storeVO.storeName" id="storeName" maxlength="20"/></td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
			             <s:if test="dealerOid == null || dealerOid == '' ">
							<manage:permission validateUrl="/resources/partner/storemanage!goToCreateStore.action">
				        		<manage:pass>
									<a href="javascript:void(0);" onclick="toCreateStore()">添加门店</a>
				        		</manage:pass>
				            </manage:permission>
				            <manage:permission validateUrl="/resources/partner/storemanage!goToUpdateStore.action">
				        		<manage:pass>
				        			<s:set var="hasUpdatePermission">yes</s:set>
				        		</manage:pass>
				        		<manage:notPass>
				        			<s:set var="hasUpdatePermission">no</s:set>
				        		</manage:notPass>
				            </manage:permission>
			             </s:if>
			            <s:elseif test="dealerOid != null && dealerOid != '' ">
							<%-- 查看商户员工按钮权限 --%>
				            <manage:permission validateUrl="/resources/partner/dealeremployeemanage!listByStoreOid.action">
				        		<manage:pass>
				        			<s:set var="hasFindCashiersPermission">yes</s:set>
				        		</manage:pass>
				        		<manage:notPass>
				        			<s:set var="hasFindCashiersPermission">no</s:set>
				        		</manage:notPass>
				            </manage:permission>
							<s:set name="backFlag" value="true"/>
						</s:elseif>
						
     				    <manage:permission validateUrl="/resources/partner/storemanage!downloadPayQRCode.action">
			        		<manage:pass>
			        			<s:set var="hasDownQrCodePermission">yes</s:set>
			        		</manage:pass>
			        		<manage:notPass>
			        			<s:set var="hasDownQrCodePermission">no</s:set>
			        		</manage:notPass>
			            </manage:permission>
			            
						<a href="javascript:void(0);" onclick="invokeAction('list');">查询</a>
						<s:if test="backFlag">
							<a href="javascript:void(0);" onclick="history.back();">返回</a>
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
	                                <th style="width: 40px;">序号</th>
	                                <th>门店编号</th>
	                                <th>门店名称</th>
	                                <th>门店地址</th>
	                                <th>联系电话</th>
	                                <th>商户</th>
	                                <th>代理商</th>	                                
	                                <th>操作</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="storeVoList != null && storeVoList.size() > 0">
			  					<s:iterator value="storeVoList" var="storeVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#storeVo.storeId" />">
						  				<s:property value="#storeVo.storeId" />
						  			</td>
						  			<td title="<s:property value="#storeVo.storeName" />">
						  				<s:property value="#storeVo.storeName" />
						  			</td>
						  			<td title="<s:property value="#storeVo.storeAddress" />">
						  				<s:property value="#storeVo.storeAddress" />
						  			</td>
						  			<td title="<s:property value="#storeVo.storeTel" />">
						  				<s:property value="#storeVo.storeTel" />
						  			</td>
						  			<td title="<s:property value="#storeVo.dealerCompany" />">
						  				<s:property value="#storeVo.dealerCompany" />
						  			</td>
						  			<td title="<s:property value="#storeVo.partnerCompany" />">
						  				<s:property value="#storeVo.partnerCompany" />
						  			</td>
						  			<td title="操作">
	  									<s:if test="#hasUpdatePermission eq 'yes'">
						  					<a href="javascript:void(0);" onclick="toUpdateStore('<s:property value="#storeVo.iwoid" />')">修改</a>
						  				</s:if>
						  				<s:if test="#hasFindCashiersPermission eq 'yes'">
						  					<a href="<%=request.getContextPath()%>/resources/partner/dealeremployeemanage!listByStoreOid.action?storeOid=<s:property value="#storeVo.iwoid" />" >查看商户员工</a>
						  				</s:if>
						  				<s:if test="#hasDownQrCodePermission eq 'yes'">
						  					<a href="javascript:void(0);" onclick="downloadPayQRCode('<s:property value="#storeVo.iwoid" />')">下载二维码</a>
						  				</s:if>
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="8">无符合条件的查询结果！</td></tr>
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
	<script type="text/javascript">
		function toCreateStore(){
			invokeAction('goToCreateStore');
		}
		function toUpdateStore(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToUpdateStore');
		}
		function toUpdateStoreCore(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToUpdateStoreCore');
		}
		function downloadPayQRCode(iwoid){
			$("#storeOid").val(iwoid);
			invokeAction('downloadPayQRCode');
		}
	</script>
	
</body>
</html>