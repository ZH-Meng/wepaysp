<%--
/*
* 
* 创建者：马宗旺
* 创建日期：2015年6月12日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>修改用户</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
</head>
<body class="bgbj"  onload="init();">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：系统用户管理&gt;用户权限管理&gt;修改用户</div>
		<form id="form" action="<%=request.getContextPath()%>/resources/usermanage/usermanage!updateUser.action" method="post">
			<input type="hidden" name="sysUserVo.iwoid" value='<s:property value="sysUserVo.iwoid"/>' />
			<div class="bgtj">
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>登录名</th>
									<td>
										<s:if test="sysUserVo.lastLoginTime == null">
											<s:textfield maxlength="20" name="sysUserVo.userId"></s:textfield><span class="tj_bt">*</span>
										</s:if>
										<s:else>
											<s:hidden name="sysUserVo.userId"></s:hidden>
											<s:property value="sysUserVo.userId" />
										</s:else>
									</td>
									<th>真实姓名</th>
									<td><s:textfield maxlength="20" name="sysUserVo.userName"></s:textfield>	<span class="tj_bt">*</span></td>
								</tr>
								<tr>
									<th>性别</th>
									<td>
										<select name="sysUserVo.gender">
											<option value="">请选择</option>
											<option value="0">男</option>
											<option value="1">女</option>
										</select>
									</td>
									<th>年龄</th>
									<td><s:textfield maxlength="2" name="sysUserVo.age"></s:textfield></td>
								</tr>
								<tr>
									<th>部门</th>
									<td><s:textfield maxlength="50" name="sysUserVo.department"></s:textfield></td>
									<th>职务</th>
									<td><s:textfield maxlength="50" name="sysUserVo.position"></s:textfield></td>
								</tr>
								<tr>
									<th>联系电话</th>
									<td><s:textfield maxlength="18" name="sysUserVo.lineTel"></s:textfield></td>
									<th>邮箱</th>
									<td><s:textfield maxlength="50" name="sysUserVo.email"></s:textfield></td>
								</tr>
								<tr>
									<th>角色</th>
									<td>
										<select name="roleOid">
											<option value="">请选择</option>
											<s:iterator value="roleList" var="role">
												<option value="<s:property value="#role.iwoid"/>"><s:property value="#role.roleName" /></option>
											</s:iterator>
										</select><span class="tj_bt">*</span>
									</td>
									<th>状态</th>
									<td>
										<select name="sysUserVo.state">
											<option value="0">正常</option>
											<option value="1">冻结</option>
											<option value="2">注销</option>
										</select>
									</td>
								</tr>
								<tr>
									<th>组织机构</th>
									<td>
										<select name="sysUserVo.dataPermisionProvince.iwoid" title="选择省份" id="provinceTag">
											<option value="">请选择</option>
											<option value="AllCountry">全国</option>
											<c:forEach var="bean" items="${sysProvinceList}" varStatus="status">
												<option value="${bean.iwoid }">${bean.provinceName }</option>
											</c:forEach>
										</select><span class="tj_bt">*</span>
										&nbsp;&nbsp;&nbsp;
										<s:if test="sysCityList.size > 0">
										<select name="sysUserVo.dataPermisionCity.iwoid" title="选择城市" id="cityTag" class="slcone">
										    <option value="">请选择</option>
										    <c:forEach var="bean" items="${sysCityList}" varStatus="status">
												<option value="${bean.iwoid }">${bean.cityName }</option>
											</c:forEach>
										</select>
										</s:if>
										<s:else>
											<select name="sysUserVo.dataPermisionCity.iwoid" title="选择城市" id="cityTag" class="slcone" style="display:none;">
										    <option value="">请选择</option>
										    <c:forEach var="bean" items="${sysCityList}" varStatus="status">
												<option value="${bean.iwoid }">${bean.cityName }</option>
											</c:forEach>
										</select>
										</s:else>
									</td>
									<th></th><td></td>
								</tr>
								<tr>
									<th>备注</th>
									<td><s:textarea cols="50" rows="4" name="sysUserVo.remark" /></td>
									<th></th>
									<td></td>
								</tr>
							</tbody>
						</table>
					</li>
				</ul>
			</div>
			<ul class="bg_tjtab mg-40">
				<li class="bg_button">
					<input class="btn-2" type="button" value="修改" onclick="submitForm()" />
					<input class="btn-2" type="button" value="返回列表" onclick="toUserList()" />
				</li>
			</ul>
		</form>
		<s:form method="post">
			<div style="display: none;">
				<s:include value="/resources/include/page.jsp"/>
			</div>
		</s:form>
	</div>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<script type="text/javascript">
		function submitForm(){
			if(validate()){
				if(confirm("确认修改？")){
					
					$("#form").submit();
				}
			}
		}
		function validate(){
			var lastLoginTime = '<s:property value="sysUserVo.lastLoginTime"/>';
			if(isBlank(lastLoginTime)){//最后登录时间为空，可以修改登录名
				var userIdInput = $("input[name='sysUserVo.userId']").val();
				if(isBlank(userIdInput)){
					alert('登录名不能为空！');
					$("input[name='sysUserVo.userId']").focus();
					return false;
				}
				if(!isUserId(userIdInput)){
					alert('登录名应为1-20位长度的字母或数字！');
					$("input[name='sysUserVo.userId']").focus();
					return false;
				}
			}
			var userNameInput = $("input[name='sysUserVo.userName']").val();
			var userAgeInput = $("input[name='sysUserVo.age']").val();
			var lineTelInput = $("input[name='sysUserVo.lineTel']").val();
			var emailInput = $("input[name='sysUserVo.email']").val();
			
			if(isBlank(userNameInput)){
				alert('真实姓名不能为空！');
				$("input[name='sysUserVo.userName']").focus();
				return false;
			}
			if(!isWord(userNameInput)){
				alert('真实姓名长度应为1-20位数字、字母或汉字！');
				$("input[name='sysUserVo.userName']").focus();
				return false;
			}
			if(!isBlank(userAgeInput)){
				if(userAgeInput == '0'){
					alert('年龄应为大于0的整数！');
					$("input[name='sysUserVo.age']").focus();
					return false;
				}
				if(!isPositiveInteger(userAgeInput)){
					alert('年龄应为大于0的整数！');
					$("input[name='sysUserVo.age']").focus();
					return false;
				}
			}
			var reg = /^[0-9](-|[0-9]){5,16}[0-9]$/;
			var lineTelRegExp = new RegExp(reg);
			if(!isBlank(lineTelInput) && !lineTelRegExp.test(lineTelInput)){
				alert("联系电话应为7-18位数字！");
				$("input[name='sysUserVo.lineTel']").focus();
				return false;
			}
			if(isBlank($("select[name='roleOid']").val())){
				alert("请选择角色！");
				$("select[name='roleOid']").focus();
				return false;
			}
			if(!isBlank(emailInput) && !isEmail(emailInput)){
				alert("联系邮箱地址不合法");
				$("input[name='sysUserVo.email']").focus();
				return false;
			}
			
			var remark = $("textarea[name='sysUserVo.remark']").val();
			if(remark.length>100){
				alert('备注长度不能大于100！');
				$("textarea[name='sysUserVo.remark']").focus();
				return false;
			}
			//组织机构必填
			if($("#provinceTag").val()==null || $("#provinceTag").val()==""){
				alert("请选组织机构！");
				return false;
			}
			return true;
		}
		$(function(){
			$("select[name='roleOid']").val('<s:property value="roleOid"/>');
			$("select[name='sysUserVo.gender']").val('<s:property value="sysUserVo.gender"/>');
			$("select[name='sysUserVo.state']").val('<s:property value="sysUserVo.state"/>');
		});
	
		function toUserList(){
			var formObj = document.forms[1];
			var actionURL = formObj.action;
			var lastPoint = actionURL.lastIndexOf(".");
			var lastLine = actionURL.lastIndexOf("/");

			if (actionURL.indexOf("!") > 0) {
				lastPoint = actionURL.lastIndexOf("!");
			}

			var prefix = actionURL.substring(lastLine + 1, lastPoint);

			formObj.action = prefix + "!goCurrent.action";
			formObj.submit();
		}
		function init(){
			
			var type="${sysUserVo.dataPermisionType}";
			if(type=="0"){
				$("#provinceTag :first-child").attr("selected",true);
			}else if(type=="1"){
				$("#provinceTag :nth-child(2)").attr("selected",true);
			}
			else if(type=="2"){
				var str="${sysUserVo.dataPermisionProvince.iwoid}";
				$("#provinceTag").children("[value='${sysUserVo.dataPermisionProvince.iwoid}']").attr("selected",true);
			}
			else if(type=="3"){
				var str="${sysUserVo.dataPermisionCity.sysProvince.iwoid}";
				var str2="${sysUserVo.dataPermisionCity.iwoid}";
				$("#provinceTag").children("[value='${sysUserVo.dataPermisionProvince.iwoid}']").attr("selected",true);
				$("#cityTag").children("[value='${sysUserVo.dataPermisionCity.iwoid}']").attr("selected",true);
			}
			
			
			
			$("#provinceTag").change(function(){
// 				alert($(this).children('option:selected').val());
				if($(this).children('option:selected').val()=="AllCountry"){//全国分支
					$("#cityTag :first-child").siblings().remove();
					$("#cityTag").hide();
				}else if($(this).children('option:selected').val()==""){//请选择分支
					$("#cityTag :first-child").siblings().remove();
					$("#cityTag").hide();
				}else{
					//选择具体的一个省份的情况
					$.ajax({     
			            //方法所在页面和方法名
                		async : false,
			            url: "<%=request.getContextPath()%>/resources/usermanage/usermanage!getSysCityListByAjax.action?provinceIwoid="
			            		+$(this).children('option:selected').val(),     
			            contentType: "application/json; charset=utf-8",     
			            dataType: "json",     
			            success: function(data) {     
			                //返回的数据用data.d获取内容 
			                $("#cityTag :first-child").siblings().remove();
			                $.each(data, function(i, json){
								$("#cityTag").append("<option value='"+json.iwoid+"'>"+json.cityName+"</option>");
			                });
			            },
			            error: function(err) {     
			                alert(err);     
			            }     
			        });//ajax
					$("#cityTag").show();
				}
				

			});//change
		}//init
	</script>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
</body>
</html>