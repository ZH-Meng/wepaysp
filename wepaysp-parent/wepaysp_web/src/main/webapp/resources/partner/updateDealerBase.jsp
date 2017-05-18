<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>商户信息维护</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<style>
		.bg_tjall th {width: 45%;}
		.qrCode_wrapper{min-height:220px;text-align:center;}
		.qrCode_block{float:left; padding:5px 0px;width:30%;}
		.qrCode_remark_block{text-align:left;padding-top:10px;line-height: 30px;color:#999999;}
		.qrCode_title{display:block;font-weight:bold;}
	</style>
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：商户信息管理&gt;商户信息维护</div>
	    <div class="bgtj">
	    	<form action="<%=request.getContextPath()%>/resources/partner/dealermanage!updateDealerBase.action" method="post" id="dealerForm">
	    		<s:hidden id="iwoid" name="dealerVO.iwoid"/>
	            <ul class="bg_tjtab">
	                <li class="bg_tjall">
	                	<table>
	                    	<tbody>
	                            <tr>
	                            	<th>商户名称</th>
	                                <td><s:textfield id="company" name="dealerVO.company" readonly="true"/><span class="tj_bt">*</span>此项不可修改</td>
	                            </tr>
	                            <tr>
	                            	<th>手机号码</th>
	                                <td><s:textfield id="moblieNumber" maxlength="32" name="dealerVO.moblieNumber" /><span class="tj_bt">*</span></td>
	                            </tr>
	                             <tr>
	                            	<th>常用QQ号码</th>
	                                <td><s:textfield id="qqNumber" maxlength="16" name="dealerVO.qqNumber" /><span class="tj_bt">*</span></td>
	                            </tr>
	                           	<tr>
	                            	<th>常用email</th>
	                                <td><s:textfield id="email" maxlength="100" name="dealerVO.email" /><span class="tj_bt">*</span></td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </li>
	                <li class="bg_button">
	                    <a href="javascript:void(0);" onclick="updateDealerBase();return false;">保存</a>
	                    <a href="javascript:void(0);" id="bind-wx-btn">绑定微信</a>
	                </li>
	            </ul>
	        </form>
	        <div id="bind-wx" style="display: none;">
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
	        <s:form method="post">
				<div style="display: none;">
					<s:include value="/resources/include/page.jsp"/>
				</div>
			</s:form>
	    </div>
	</div>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#moblieNumber").focus();
		});	
		
		function updateDealerBase() {
			var moblieNumber = $("#moblieNumber").val();
			var qqNumber = $("#qqNumber").val();
			var email = $("#email").val();
			
			if (isBlank(moblieNumber)) {
				alert("手机号码不能为空！");
				$("#moblieNumber").focus();
				return false;
			} else if (!isMobile(moblieNumber)) {
				alert("手机号码格式不正确！");
				$("#moblieNumber").focus();
				return false;
			} else if (isBlank(qqNumber)) {
				alert("QQ号码不能为空！");
				$("#qqNumber").focus();
				return false;
			} else if (!isQQ(qqNumber)) {
				alert("QQ号码格式不正确！");
				$("#qqNumber").focus();
				return false;
			} else if (isBlank(email)) {
				alert("邮箱不能为空！");
				$("#email").focus();
				return false;
			} else if (!isEmail(email)) {
				alert("邮箱格式不正确！");
				$("#email").focus();
				return false;
			}
			
			if (!window.confirm("确认修改？")) {
				return false;
			}
			$("#dealerForm").submit();
		}
		

		$("#bind-wx-btn").toggle(function() {
			$("#bind-wx").show();
		}, function() {
			$("#bind-wx").hide();
		});
	</script>	
	<s:property value="#request.messageBean.alertMessage" escape="false" />
</body>
</html>