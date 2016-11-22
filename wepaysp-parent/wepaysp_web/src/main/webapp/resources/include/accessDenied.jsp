<%--
/*
* 
* 创建者：杨帆
* 创建日期：2015年7月10日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link href="<%=request.getContextPath()%>/css/zxstyle.css" rel="stylesheet" />
	<title>中彩宝后台管理系统</title>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript">
		$(function(){
			var d=$(window),
				w=d.width(),
				h=d.height(),
				p=$(".welimg"),
				pw=p.width(),
				ph=p.height();
			if(h>(ph+28+44+4)){
				p.height((h-28-4-44-10)+'px')
				p.find("img").css("margin-top",(p.height()-440)/2)
			}
		})	
	</script>	
</head>
<body class="bgbj">
	<div class="right">
	    <div class="welcome">
	    	<p class="welimg"><img src="<%=request.getContextPath()%>/images/forbid.gif" class="img" /><span class="clock_txt">您无权访问此功能，请<a href="javascript:history.go(-1);">返回上一步</a></span></p>
	    </div>
	    <div class="welbottom">版权所有：Copyright &copy; 淄博微盘信息科技有限公司</div>
	</div>
	<c:out value="${requestScope.messageBean.alertMessage }"  escapeXml="false" />
</body>
</html>