<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<s:if test="resetFlag == 'yes'">
		<s:set name="title">重置退款权限密码</s:set>
		<s:set name="navTag">重置退款权限密码</s:set>
	</s:if>
	<s:else>
		<s:set name="title">商户员工管理</s:set>
		<s:set name="navTag">商户员工管理&gt;员工管理</s:set>
	</s:else>
	<title>${title }</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：${navTag }</div>
		<s:form method="post">
			<s:hidden id="iwoid" name="dealerEmployeeVO.iwoid"/>
			<s:hidden id="dealerEmployeeOid" name="dealerEmployeeOid"/>
			<s:hidden id="resetFlag" name="resetFlag"/>
			<s:hidden id="storeOid" name="storeOid"/>
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
									<td><s:textfield name="dealerEmployeeVO.employeeName" id="employeeName" maxlength="20"/></td>
									<th>手机号码</th>
									<td><s:textfield name="dealerEmployeeVO.moblieNumber" id="moblieNumber" maxlength="20"/></td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<s:if test="resetFlag != 'yes'">
							<s:if test="storeOid == null || storeOid == '' ">
								<manage:permission validateUrl="/resources/partner/dealeremployeemanage!goToCreateDealerEmployee.action">
					        		<manage:pass>
										<a href="javascript:void(0);" onclick="toCreateDealerEmployee()">添加员工</a>
					        		</manage:pass>
					            </manage:permission>
					            <manage:permission validateUrl="/resources/partner/dealeremployeemanage!goToUpdateDealerEmployee.action">
					        		<manage:pass>
					        			<s:set var="hasUpdatePermission">yes</s:set>
					        		</manage:pass>
					        		<manage:notPass>
					        			<s:set var="hasUpdatePermission">no</s:set>
					        		</manage:notPass>
					            </manage:permission>
							</s:if>
							<s:else>
								<s:set name="backFlag" value="true"/>
							</s:else>
				            <manage:permission validateUrl="/resources/partner/dealeremployeemanage!downloadPayQRCode.action">
				        		<manage:pass>
				        			<s:set var="hasDownQrCodePermission">yes</s:set>
				        		</manage:pass>
				        		<manage:notPass>
				        			<s:set var="hasDownQrCodePermission">no</s:set>
				        		</manage:notPass>
				            </manage:permission>
						</s:if>
						<s:else>
				            <manage:permission validateUrl="/resources/partner/dealeremployeemanage!goToResetRefundPwd.action">
				        		<manage:pass>
				        			<s:set var="hasResetPermission">yes</s:set>
				        		</manage:pass>
				        		<manage:notPass>
				        			<s:set var="hasResetPermission">no</s:set>
				        		</manage:notPass>
				            </manage:permission>
						</s:else>
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
	                                <th class="six">序号</th>
	                                <th>门店编号</th>
	                                <th>员工编号</th>
	                                <th>登录名</th>
	                                <th>姓名</th>
	                                <th>员工级别</th>
	                                <th>手机号码</th>
	                                <th>状态</th>
	                                <th>操作</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="dealerEmployeeVoList != null && dealerEmployeeVoList.size() > 0">
			  					<s:iterator value="dealerEmployeeVoList" var="dealerEmployeeVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#dealerEmployeeVo.storeId" />">
						  				<s:property value="#dealerEmployeeVo.storeId" />
						  			</td>
						  			<td title="<s:property value="#dealerEmployeeVo.dealerEmployeeId" />">
						  				<s:property value="#dealerEmployeeVo.dealerEmployeeId" />
						  			</td>
						  			<td title="<s:property value="#dealerEmployeeVo.loginId" />">
						  				<s:property value="#dealerEmployeeVo.loginId" />
						  			</td>
						  			<td title="<s:property value="#dealerEmployeeVo.employeeName" />">
						  				<s:property value="#dealerEmployeeVo.employeeName" />
						  			</td>
						  			<s:if test="#dealerEmployeeVo.employeeType == 1">
						  				<s:set var="employeeTypeStr">收银员</s:set>
						  			</s:if>
						  			<s:elseif test="#dealerEmployeeVo.employeeType == 2">
						  				<s:set var="employeeTypeStr">店长</s:set>
						  			</s:elseif>
						  			<s:else>
						  				<s:set var="employeeTypeStr">收银员</s:set>
						  			</s:else>
						  			<td title="<s:property value="employeeTypeStr" />">
						  				<s:property value="#employeeTypeStr" />
						  			</td>
						  			<td title="<s:property value="#dealerEmployeeVo.moblieNumber" />">
						  				<s:property value="#dealerEmployeeVo.moblieNumber" />
						  			</td>
						  			<s:if test="#dealerEmployeeVo.state == 1">
						  				<s:set var="stateStr">未使用</s:set>
						  			</s:if>
						  			<s:elseif test="#dealerEmployeeVo.state == 2">
						  				<s:set var="stateStr">使用中</s:set>
						  			</s:elseif>
						  			<s:elseif test="#dealerEmployeeVo.state == 3">
						  				<s:set var="stateStr">冻结</s:set>
									</s:elseif>
						  			<td title="<s:property value="#stateStr" />">
						  				<s:property value="#stateStr" />
						  			</td>
						  			<td title="操作">
	  									<s:if test="#hasUpdatePermission eq 'yes'">
						  					<a href="javascript:void(0);" onclick="toUpdateDealerEmployee('<s:property value="#dealerEmployeeVo.iwoid" />')">修改</a>
						  				</s:if>
						  				<s:if test="#hasResetPermission eq 'yes'">
						  					<a href="javascript:void(0);" onclick="goToResetRefundPwd('<s:property value="#dealerEmployeeVo.iwoid" />')">重置退款权限密码</a>
						  				</s:if>
						  				<s:if test="#hasDownQrCodePermission eq 'yes'">
						  					<a href="javascript:void(0);" onclick="downloadPayQRCode('<s:property value="#dealerEmployeeVo.iwoid" />')">下载二维码</a>
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
		function toCreateDealerEmployee(){
			invokeAction('goToCreateDealerEmployee');
		}
		function toUpdateDealerEmployee(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToUpdateDealerEmployee');
		}
		function goToResetRefundPwd(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToResetRefundPwd');
		}
		function downloadPayQRCode(iwoid){
			$("#dealerEmployeeOid").val(iwoid);
			invokeAction('downloadPayQRCode');
		}
	</script>
	
</body>
</html>