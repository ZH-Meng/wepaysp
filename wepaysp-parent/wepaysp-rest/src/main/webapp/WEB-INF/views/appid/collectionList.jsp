<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">

<title>收款列表</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/weui.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/jquery-weui.css">
<style type="text/css">
	.collection-money{color: #7D9EC0}
	.collection-time{color: #838B8B}
</style>
</head>
<body ontouchstart>
	<div class="bd">
		<div class="page__bd">
			<c:if test="${not empty storeList }">
				<div class="weui-cell weui-cell_select">
					<div class="weui-cell__bd">
						<select class="weui-select" name="queryStoreOid" id="query-store">
							<option value="">全部门店</option>
							<c:forEach items="${storeList}" var="store">
								<option value="${store.iwoid }">${store.storeName }</option>
							</c:forEach>
						</select>
					</div>
				</div>
			</c:if>
			<div class="weui-cell">
				<div class="weui-cell__ft ">
					<input class="weui-input" id="query-date" type="date" value="" max="${queryDate }">
				</div>
				<label for="" class="weui-cell__ft" id="total"></label>
				<!-- 
				<label for="" class="weui-label" id="total-amount"></label>
				<label for="" class="weui-label" id="total-money"></label>
				 -->
			</div>
			<div class="weui-cells" id="collection-list">
			</div>
			<div id="collection-loading">
				<div class="weui-loadmore">
			        <i class="weui-loading"></i>
			        <span class="weui-loadmore__tips">正在加载</span>
			      </div>
			</div>
			<div id="collection-empty">
				<div class="weui-loadmore weui-loadmore_line">
			        <span class="weui-loadmore__tips">暂无数据</span>
			    </div>
			</div>
		</div>
	</div>
	<script src="<%=request.getContextPath()%>/resources/js/jquery-2.1.4.js"></script>
	<script src="<%=request.getContextPath()%>/resources/js/fastclick.js"></script>
	<script src="<%=request.getContextPath()%>/resources/js/jquery-weui.js"></script>
	<script>
	  var curPage = 0;
	  var pageRow = 12;
	  var loading = false;  //状态标记
	  var queryDate = "${today}";
	  $(function(){
	    FastClick.attach(document.body);
	    $(document.body).infinite();
	    $("#query-date").val(queryDate);
	    initLoad();
	    loadData(queryDate);
	  });
	  
	  $("#query-date").on("change",function(){
		queryDate = $("#query-date").val();
		if (queryDate == '') {
			queryDate = "${today}";
			$("#query-date").val(queryDate);
		}
	    initLoad();
	    loadData(queryDate);
	  });
	  
	  $("#query-store").on("change",function(){
	    initLoad();
	    loadData(queryDate, $("#query-store").val());
	  });
	  
	  function initLoad(){
		  curPage = 0;
		  loading = false;
		  $("#collection-list").html("");
		  $("#collection-empty").hide();
		  $("#total").html("");
		  $(document.body).infinite().on("infinite", function() {
		    if(loading) return;
		    loadData(queryDate);
		  });
	  }
	  
	  function loadData(date,storeOid){
		  loading = true;
		  $("#collection-loading").show();
		  $.get("<%=request.getContextPath()%>/appid/collection/list/" + ++curPage, 
		    		{ openid:"${openid}", dealerOid:"${dealerOid}", storeOid:"${storeOid}", dealerEmployeeOid:"${dealerEmployeeOid}", pageSize:pageRow,queryDate:date,queryStoreOid:storeOid },
		    function(data,status,xhr){
	   			if (status == 'success'&& undefined != data && undefined != data.payList) {
	   				var dataSize = data.payList.length;
			    	$("#collection-loading").hide();
			    	if (curPage == 1){
				    	$("#total").text("笔数:"+data.total[1]+"，金额:"+data.total[0]);
				    	//$("#total-money").text("金额:"+data.total[0]);
			    	}
	   				if (curPage == 1 && dataSize == 0){
	   					$(document.body).destroyInfinite();
	   					$("#collection-empty").show();
	   				} else {
	   					if (dataSize < pageRow) {
	   						$(document.body).destroyInfinite();
		   				}
	   					for(var i=0; i<data.payList.length; i++) {
				    		addCell(data.payList[i]);
				    	}
	   					loading = false;//加载成功
	   				}
	   			}
		    },"json");
	  }
	  
	  function addCell(item){
		  var cell = '<div class="weui-cell"><div class="weui-cell__bd collection-time">transTime</div><div class="weui-cell__ft collection-money">collectionMoney</div></div>';
		  $("#collection-list").append(cell.replace("transTime", item.transTime).replace("collectionMoney", item.collectionMoney));
	  }
	</script>
</body>
</html>