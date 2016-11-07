<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title></title>
	<link href="<%=request.getContextPath()%>/css/zxstyle.css" rel="stylesheet" />
</head>
<<body class="bgbj">
<div class="right">
    <div class="welcome clock">
    	<p class="welimg">
    		<img src="<%=request.getContextPath()%>/images/forbid.gif" class="img" /><span class="clock_txt">系统错误！</span>
    	</p>
    </div>
</div> 
<s:property value="#request.messageBean.alertMessage" escape="false" />
</body>
</html>