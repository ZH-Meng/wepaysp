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
		<div class="bgposition">您现在的位置：代理商管理&gt;子代理商列表</div>
	    <div class="bgtable">
	        <s:form method="post">
	        	<s:hidden id="parentPartnerOid" name="partnerVO.parentPartnerOid"/>
	        	<s:hidden id="iwoid" name="partnerVO.iwoid"/>
	        	<manage:permission validateUrl="/resources/partner/partnermanage!goToCreatePartner.action">
	        		<manage:pass>
	        			<ul class="title_button">
			                <li><a href="javascript:void(0);" onclick="toCreatePartner()">创建子代理商</a></li>
			            </ul>
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
	            <ul class="bg_all">
	                <li class="bg_table bg_table1">
	                    <table class="bg_odd">
	                        <thead>
	                            <tr>
	                                <th>序号</th>
	                                <th>登录名</th>
	                                <th>上级代理商</th>
	                                <th>级别类型</th>
	                                <th>联系人</th>
	                                <th>公司名称</th>
	                                <th>地址</th>
	                                <th>固定电话</th>
	                                <th>手机</th>
	                                <th>使用时间</th>
	                                <th>状态</th>
	                                <th>余额</th>
	                                <th>操作</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="partnerVoList != null && partnerVoList.size() > 0">
			  					<s:iterator value="partnerVoList" var="partnerVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.loginId" />">
						  				<s:property value="#partnerVo.loginId" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.parentCompany" />">
						  				<s:property value="#partnerVo.parentCompany" />
						  			</td>
						  			<s:if test="#partnerVo.level == 0">
						  				<s:set var="partnerLevelStr">服務商</s:set>
						  			</s:if>
						  			<s:elseif test="#partnerVo.level == 1">
						  				<s:set var="partnerLevelStr">一級</s:set>
						  			</s:elseif>
						  			<s:elseif test="#partnerVo.level == 2">
						  				<s:set var="partnerLevelStr">二級</s:set>
						  			</s:elseif>
						  			<td title="<s:property value="#partnerLevelStr" />">
						  				<s:property value="#partnerLevelStr" />
						  			</td>
						  			<td style="text-align: left" title="<s:property value="#partnerVo.contactor" />">
						  				<s:property value="#partnerVo.contactor" />
						  			</td>
						  			<td style="text-align: left" title="<s:property value="#partnerVo.company" />">
						  				<s:property value="#partnerVo.company" />
						  			</td>
						  			<td style="text-align: left" title="<s:property value="#partnerVo.address" />">
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
						  				-
						  				<s:date name="#partnerVo.contractEnd" format="yyyy-MM-dd" />
						  			</td>
						  			<s:if test="#partnerVo.state == 1">
						  				<s:set var="stateStr">未使用</s:set>
						  			</s:if>
						  			<s:elseif test="#partnerVo.state == 1">
						  				<s:set var="stateStr">使用中</s:set>
						  			</s:elseif>
						  			<s:elseif test="#partnerVo.state == 2">
						  				<s:set var="stateStr">冻结</s:set>
									</s:elseif>
						  			<td title="<s:property value="#stateStr" />">
						  				<s:property value="#stateStr" />
						  			</td>
						  			<td title="<s:property value="#partnerVo.balance" />">
						  				<s:property value="#partnerVo.balance" />
						  			</td>
						  			<td title="修改">
						  				<s:if test="#hasUpdatePermission eq 'yes' && #partnerVo.state != 2">
						  					<a href="javascript:void(0);" onclick="toUpdatePartner('<s:property value="#partnerVo.iwoid" />')">修改</a>
						  				</s:if>
						  				<s:else><strong>修改</strong></s:else>
						  				<s:if test="#partnerVo.level < 2">
						  					<a href="javascript:void(0);" onclick="findChildPartners('<s:property value="#partnerVo.iwoid" />')">子代理商</a>
						  				</s:if>
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="13">无符合条件的查询结果！</td></tr>
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
	        </s:form>
	    </div>
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
	</script>
	
</body>
</html>