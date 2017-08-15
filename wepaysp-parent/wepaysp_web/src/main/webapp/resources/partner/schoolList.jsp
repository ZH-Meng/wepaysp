<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>学校信息管理</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：代理商管理&gt;学校信息管理</div>
		<s:form method="post">
			<s:hidden id="iwoid" name="schoolVO.iwoid"/>
			<s:hidden id="schoolOid" name="schoolOid"/>
			<s:hidden id="coreDataFlag" name="schoolVO.coreDataFlag"/>
			<s:hidden id="partnerOid" name="partnerOid"/>
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>

									<th>学校名称</th>
									<td><s:textfield name="schoolVO.shcoolName" id="shcoolName" maxlength="50"/></td>
									<th>所属业务员</th>
									<td><s:textfield name="schoolVO.partnerEmployeeName" id="partnerEmployeeName" maxlength="50"/></td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<manage:permission validateUrl="/resources/partner/schoolmanage!goToCreateSchool.action">
			        		<manage:pass>
						<a href="javascript:void(0);" onclick="toCreateSchool()">添加学校</a>
			        		</manage:pass>
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
	                                <th>学校名称</th>
	                                <th>学校类型</th>
	                                <th>登录名</th>
	                                <th>联系人</th>
	                                <th>联系电话</th>
	                                
	                                <th>省</th>
	                                <th>地市</th>
	                                <th>区县</th>	                                 
	                               	<th>代理商</th>
	               
	                                <th>所属业务员</th> 
	                                <!-- <th class="twenty">操作</th> -->
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="schoolVoList != null && schoolVoList.size() > 0">
			  					<s:iterator value="schoolVoList" var="schoolVo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:property value="#schoolVo.shcoolName" />">
						  				<s:property value="#schoolVo.shcoolName" />
						  			</td> 
						  			<s:if test="#schoolVo.schoolType == 1">
						  				<s:set var="schoolTypeStr">托儿所</s:set>
						  			</s:if>
						  			<s:elseif test="#schoolVo.schoolType == 2">
						  				<s:set var="schoolTypeStr">幼儿园</s:set>
						  			</s:elseif>
						  			<s:elseif test="#schoolVo.schoolType == 3">
						  				<s:set var="schoolTypeStr">小学</s:set>
									</s:elseif>
									<s:elseif test="#schoolVo.schoolType == 4">
						  				<s:set var="schoolTypeStr">初中</s:set>
									</s:elseif>
									<s:elseif test="#schoolVo.schoolType == 5">
						  				<s:set var="schoolTypeStr">高中</s:set>
									</s:elseif>
									<td title="<s:property value="#schoolTypeStr" />">
						  				<s:property value="#schoolTypeStr" />
						  			</td>
						  			<td title="<s:property value="#schoolVo.loginId" />">
						  				<s:property value="#schoolVo.loginId" />
						  			</td>
						  			<td title="<s:property value="#schoolVo.techSupportPerson" />">
						  				<s:property value="#schoolVo.techSupportPerson" />
						  			</td>
						  			<td title="<s:property value="#schoolVo.techSupportPhone" />">
						  				<s:property value="#schoolVo.techSupportPhone" />
						  			</td>
						  			<td title="<s:property value="#schoolVo.provinceName" />">
						  				<s:property value="#schoolVo.provinceName" />
						  			</td>
						  			<td title="<s:property value="#schoolVo.cityName" />">
						  				<s:property value="#schoolVo.cityName" />
						  			</td>
						  			<td title="<s:property value="#schoolVo.districtName" />">
						  				<s:property value="#schoolVo.districtName" />
						  			</td>
						  			

						  		    <td title="<s:property value="#schoolVo.partnerCompany" />">
						  				<s:property value="#schoolVo.partnerCompany" />
						  			</td>
						  			<td title="<s:property value="#schoolVo.partnerEmployeeName" />">
						  				<s:property value="#schoolVo.partnerEmployeeName" />
						  			</td> 
						  			<!-- <td title="操作"> 
						  					<a href="javascript:void(0);" onclick="toUpdateDealer('<s:property value="#schoolVo.iwoid" />')">修改</a>
						  			</td>-->
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="11">无符合条件的查询结果！</td></tr>
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
		function toCreateSchool(){
			invokeAction('goToCreateSchool');
		}
		function toUpdateDealer(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToUpdateDealer');
		}
		function toUpdateDealerCore(iwoid){
			$("#iwoid").val(iwoid);
			invokeAction('goToUpdateDealerCore');
		}
		function downloadPayQRCode(iwoid){
			$("#schoolOid").val(iwoid);
			invokeAction('downloadPayQRCode');
		}
		
		function alipayManage(iwoid){
			$("#schoolOid").val(iwoid);
			invokeAction('goToAlipayManage');
		}
	</script>
	
</body>
</html>