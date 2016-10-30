<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>代理商员工管理</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：代理商员工管理&gt;员工管理</div>
		<s:form method="post">
			<s:hidden id="iwoid" name="partnerEmployeeVO.iwoid"/>
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>姓名</th>
									<td><s:textfield name="partnerEmployeeVO.employeeName" id="employeeName" maxlength="20"/></td>
									<th>手机号码</th>
									<td><s:textfield name="partnerEmployeeVO.moblieNumber" id="moblieNumber" maxlength="20"/></td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<manage:permission validateUrl="/resources/partner/partneremployeemanage!goToCreatePartnerEmployee.action">
			        		<manage:pass>
								<a href="javascript:void(0);" onclick="toCreatePartnerEmployee()">添加员工</a>
			        		</manage:pass>
			            </manage:permission>
			            <manage:permission validateUrl="/resources/partner/partneremployeemanage!goToUpdatePartnerEmployee.action">
			        		<manage:pass>
			        			<s:set var="hasUpdatePermission">yes</s:set>
			        		</manage:pass>
			        		<manage:notPass>
			        			<s:set var="hasUpdatePermission">no</s:set>
			        		</manage:notPass>
			            </manage:permission>
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
	                                <th>序号</th>
	                                <th>员工编号</th>
	                                <th>登录名</th>
	                                <th>姓名</th>
	                                <th>手机号码</th>
	                                <th>状态</th>
	                                <th>操作</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="partnerEmployeeVoList != null && partnerEmployeeVoList.size() > 0">
			  					<s:iterator value="partnerEmployeeVoList" var="partnerEmployeeVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#partnerEmployeeVo.partnerEmployeeId" />">
						  				<s:property value="#partnerEmployeeVo.partnerEmployeeId" />
						  			</td>
						  			<td title="<s:property value="#partnerEmployeeVo.loginId" />">
						  				<s:property value="#partnerEmployeeVo.loginId" />
						  			</td>
						  			<td title="<s:property value="#partnerEmployeeVo.employeeName" />">
						  				<s:property value="#partnerEmployeeVo.employeeName" />
						  			</td>
						  			<td title="<s:property value="#partnerEmployeeVo.moblieNumber" />">
						  				<s:property value="#partnerEmployeeVo.moblieNumber" />
						  			</td>
						  			<s:if test="#partnerEmployeeVo.state == 1">
						  				<s:set var="stateStr">未使用</s:set>
						  			</s:if>
						  			<s:elseif test="#partnerEmployeeVo.state == 2">
						  				<s:set var="stateStr">使用中</s:set>
						  			</s:elseif>
						  			<s:elseif test="#partnerEmployeeVo.state == 3">
						  				<s:set var="stateStr">冻结</s:set>
									</s:elseif>
						  			<td title="<s:property value="#stateStr" />">
						  				<s:property value="#stateStr" />
						  			</td>
						  			<td title="修改">
	  									<s:if test="#hasUpdatePermission eq 'yes'">
						  					<a href="javascript:void(0);" onclick="toUpdatePartnerEmployee('<s:property value="#partnerEmployeeVo.iwoid" />')">修改</a>
						  				</s:if>
						  				<s:else><strong>修改</strong></s:else>
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="7">无符合条件的查询结果！</td></tr>
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
		function toCreatePartnerEmployee(){
			invokeAction('goToCreatePartnerEmployee');
		}
		function toUpdatePartnerEmployee(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToUpdatePartnerEmployee');
		}
	</script>
	
</body>
</html>