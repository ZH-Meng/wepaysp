<%--
/*
* 
* 创建者：杨帆
* 创建日期：2015年6月9日
*
* 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
* 保留所有权利。
*/
--%>
<%@page import="com.zbsp.wepaysp.manage.web.util.DateUtil"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
// 	Date date=new Date();
// 	DateUtil.getDate(new Date(), "yyyy年MM月dd HH：mm:ss");
%>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link href="<%=request.getContextPath()%>/css/zxstyle.css" rel="stylesheet" />
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<title>中彩宝后台管理系统</title>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
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
<div class="rightbg">
	<div class="bgposition">您现在的位置：管理统计</div>
    <div class="bgtj clear">
    	<div class="gltj">
        	<h1>管理统计</h1>
            <ul class="gltjlist clear">
            	<li class="gltjtx"><img src="<%=request.getContextPath()%>/images/wetximg.png" alt="头像" /></li>
                <li class="gltjtxt">
                	<dl>
                    	<dt>您好，管理员</dt>
                        <dd>当前登录用户：<span><s:property value="manageUser.userId"/></span></dd>
                        <dd>上次登录时间：
                        	<span>
                     			<s:date name="manageUser.lastLoginTime" format="yyyy-MM-dd HH:mm:ss" />
                     		</span>
                     	</dd>
                        <dd>上次登录地点：<span><s:property value="manageUser.lastLoginIp"/></span></dd>
                        <dd>总登录次数：<span><s:property value="indexStatDataVo.loginCount"/></span></dd>
                    </dl>
                </li>
            </ul>
        </div>
        <div class="yygk">
        	<h1>产品运营概况</h1>
            <ul class="yygklist">
            	<li>
                	<table>
                    	<tbody>
                        	<tr>
                            	<td>数据截止时间</td>
                                <td>
                                	<jsp:useBean id="now" class="java.util.Date"/>
                                	<fmt:formatDate value="${now}" pattern="yyyy年MM月dd日 00:00:00"/>
                                </td>
                            </tr>
                            <tr>
                            	<td>用户总数</td>
                                <td><span><fmt:formatNumber value="${indexStatDataVo.cardCount}" pattern="###,###,###,###"/></span>个</td>
                            </tr>
                            <tr>
                            	<td>积分产生总数</td>
                                <td><span><fmt:formatNumber value="${indexStatDataVo.pointIncomeCount}" pattern="###,###,###,###"/></span>分</td>
                            </tr>
                            <tr>
                            	<td>奖金派发总数</td>
                                <td><span><fmt:formatNumber value="${indexStatDataVo.prizeSendAmount/100}" pattern="###,###,###,###"/></span>元</td>
                            </tr>
                        </tbody>
                    </table>
                </li>
            </ul>
        </div>
    </div>
</div> 

</body>
</html>