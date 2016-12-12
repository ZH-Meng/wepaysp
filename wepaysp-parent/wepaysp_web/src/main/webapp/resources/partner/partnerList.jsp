<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>子代理商管理</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：代理商管理&gt;子代理商管理</div>
		<s:form method="post">
			<s:hidden id="parentPartnerOid" name="partnerVO.parentPartnerOid"/>
	        <s:hidden id="iwoid" name="partnerVO.iwoid"/>
	        <s:hidden id="returnParentPartnerOid" name="returnParentPartnerOid"/>
	        <s:hidden id="doReturnParent" name="doReturnParent"/>
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>状态</th>
									<td>
										<s:select list="#{1:'未使用',2:'使用中',3:'冻结'}" listKey="key" listValue="value" name="partnerVO.state"  id="state" headerKey="" headerValue="全部"/>
									</td>
									<th>联系人</th>
									<td><s:textfield name="partnerVO.contactor" id="contactor" maxlength="20"/></td>
									<th>登录名</th>
									<td><s:textfield name="partnerVO.loginId" id="loginId" maxlength="20"/></td>
									<th>代理商</th>
									<td><s:textfield name="partnerVO.company" id="parentCompany" maxlength="20" /></td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<manage:permission validateUrl="/resources/partner/partnermanage!goToCreatePartner.action">
			        		<manage:pass>
			        			<s:if test="isChildPage==1">
									<a href="javascript:void(0);" onclick="toCreatePartner()">添加子代理商</a>
			        			</s:if>
			        		</manage:pass>
			            </manage:permission>
			            <manage:permission validateUrl="/resources/partner/partnermanage!goToUpdatePartner.action">
			        		<manage:pass>
			        			<s:set var="hasUpdatePermission">yes</s:set>
			        		</manage:pass>
			        		<manage:notPass>
			        			<s:set var="hasUpdatePermission">no</s:set>
			        		</manage:notPass>
			            </manage:permission>
			            <%-- 查看商户按钮权限 --%>
			            <manage:permission validateUrl="/resources/partner/dealermanage!listByPartnerOid.action">
			        		<manage:pass>
			        			<s:set var="hasFindDealersPermission">yes</s:set>
			        		</manage:pass>
			        		<manage:notPass>
			        			<s:set var="hasFindDealersPermission">no</s:set>
			        		</manage:notPass>
			            </manage:permission>
			            
						<a href="javascript:void(0);" onclick="invokeAction('list');">查询</a>						
						
						<s:if test="returnParentFlag == 'on'">
							<div class="bg_page bg_page1">
								<p class="bg_pagebutton">
									<a class="sjbtn" href="javascript:void(0);" onclick="returnParent();">返回上级</a>
								</p>
							</div>
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
	                                <th style="width: 120px;">代理商编号</th>
	                                <th>登录名</th>
	                                <!-- <th>父代理商</th> -->
	                                <th class="five">级别</th>
	                                <th>联系人</th>
	                                <th>公司名称</th>
	                                <th>地址</th>
	                                <th>固定电话</th>
	                                <th>手机</th>
	                                <th>使用时间</th>
	                                <th>状态</th>
	                                <!-- <th>余额</th> -->
	                                <th class="twenty">操作</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="partnerVoList != null && partnerVoList.size() > 0">
			  					<s:iterator value="partnerVoList" var="partnerVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.partnerId" />">
						  				<s:property value="#partnerVo.partnerId" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.loginId" />">
						  				<s:property value="#partnerVo.loginId" />
						  			</td>
						  			<%-- <td title="<s:property value="#partnerVo.parentCompany" />">
						  				<s:property value="#partnerVo.parentCompany" />
						  			</td> --%>
						  			<s:if test="#partnerVo.level == 1">
						  				<s:set var="partnerLevelStr">一级</s:set>
						  			</s:if>
						  			<s:elseif test="#partnerVo.level == 2">
						  				<s:set var="partnerLevelStr">二级</s:set>
						  			</s:elseif>
						  			<s:elseif test="#partnerVo.level == 3">
						  				<s:set var="partnerLevelStr">三级</s:set>
						  			</s:elseif>
						  			<td title="<s:property value="#partnerLevelStr" />">
						  				<s:property value="#partnerLevelStr" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.contactor" />">
						  				<s:property value="#partnerVo.contactor" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.company" />">
						  				<s:property value="#partnerVo.company" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.address" />">
						  				<s:property value="#partnerVo.address" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.telephone" />">
						  				<s:property value="#partnerVo.telephone" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.moblieNumber" />">
						  				<s:property value="#partnerVo.moblieNumber" />
						  			</td>
						  			
						  			<td title="<s:date name="#partnerVo.contractBegin" format="yyyy-MM-dd" />-<s:date name="#partnerVo.contractEnd" format="yyyy-MM-dd" />">
						  				<s:date name="#partnerVo.contractBegin" format="yyyy-MM-dd" />
						  				至
						  				<s:date name="#partnerVo.contractEnd" format="yyyy-MM-dd" />
						  			</td>
						  			<s:if test="#partnerVo.state == 1">
						  				<s:set var="stateStr">未使用</s:set>
						  			</s:if>
						  			<s:elseif test="#partnerVo.state == 2">
						  				<s:set var="stateStr">使用中</s:set>
						  			</s:elseif>
						  			<s:elseif test="#partnerVo.state == 3">
						  				<s:set var="stateStr">冻结</s:set>
									</s:elseif>
						  			<td title="<s:property value="#stateStr" />">
						  				<s:property value="#stateStr" />
						  			</td>
						  			<%-- <td title="<s:property value="#partnerVo.balance" />">
						  				<s:property value="#partnerVo.balance" />
						  			</td> --%>
						  			<td title="修改">
						  				<s:if test="#hasUpdatePermission eq 'yes' && #partnerVo.state != 3 && isChildPage==1">
						  					<a href="javascript:void(0);" onclick="toUpdatePartner('<s:property value="#partnerVo.iwoid" />')">修改</a>
						  				</s:if>
						  				<s:if test="#partnerVo.level < 3">
						  					<a href="javascript:void(0);" onclick="findChildPartners('<s:property value="#partnerVo.iwoid" />')">查看下级代理商</a>
						  				</s:if>
										<s:if test="#hasFindDealersPermission eq 'yes'">
							  				<a href="<%=request.getContextPath()%>/resources/partner/dealermanage!listByPartnerOid.action?partnerOid=<s:property value="#partnerVo.iwoid" />" >查看商户</a>
										</s:if>
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
		function toCreatePartner(){
			invokeAction('goToCreatePartner');
		}
		function toUpdatePartner(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToUpdatePartner');
		}
		function findChildPartners(iwoid){
			$("#parentPartnerOid").val(iwoid);
			invokeAction('list');
		}
		function returnParent(){
			$("#doReturnParent").val("yes");
			invokeAction('list');
		}
	</script>
	
</body>
</html>