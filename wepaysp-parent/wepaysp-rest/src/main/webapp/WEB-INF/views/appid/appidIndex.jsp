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
	body,html{
		height: 100%;
	}
	.collection-money{color: #7D9EC0}
	.collection-time{color: #838B8B}
</style>
</head>
<body ontouchstart>
	<div class="weui-tab">
		<div class="weui-tab__bd">
			<div id="tab-collection-list" class="weui-tab__bd-item ">
				<c:if test="${not empty storeList }">
					<div class="weui-cell weui-cell_select">
						<div class="weui-cell__bd">
							<select class="weui-select" name="queryStoreOid" id="collection-query-store">
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
				<div style="padding-top: 50px;"></div>
			</div>
			<div id="tab-stat-list" class="weui-tab__bd-item">
				<c:if test="${not empty storeList }">
					<div class="weui-cell weui-cell_select">
						<div class="weui-cell__bd">
							<select class="weui-select" name="queryStoreOid" id="stat-query-store">
								<option value="">全部门店</option>
								<c:forEach items="${storeList}" var="store">
									<option value="${store.iwoid }">${store.storeName }</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</c:if>
				<div class="weui-cells" id="stat-list">
				<div id="stat-loading">
					<div class="weui-loadmore">
				        <i class="weui-loading"></i>
				        <span class="weui-loadmore__tips">正在加载</span>
				      </div>
				</div>
				<div id="stat-empty">
					<div class="weui-loadmore weui-loadmore_line">
				        <span class="weui-loadmore__tips">暂无数据</span>
				    </div>
				</div>
			</div>
			<div id="tab-more" class="weui-tab__bd-item">
				 <div class="weui-cell weui-cell_switch">
			        <div class="weui-cell__bd">接收收款通知</div>
			        <div class="weui-cell__ft">
			          <label for="switchCP" class="weui-switch-cp">
			            <input id="switchCP" class="weui-switch-cp__input" type="checkbox" checked="checked">
			            <div class="weui-switch-cp__box"></div>
			          </label>
			        </div>
			      </div>
			</div>			
		</div>

		<div class="weui-tabbar">
			<a href="#tab-collection-list" class="weui-tabbar__item " id="bar-collection-list">
				<div class="weui-tabbar__icon">
					<img src="<%=request.getContextPath()%>/resources/images/icon_nav_cell.png" alt="">
				</div>
				<p class="weui-tabbar__label">收款列表</p>
			</a>
			 <a href="#tab-stat-list" class="weui-tabbar__item" id="bar-stat-list">
				<div class="weui-tabbar__icon">
					<img src="<%=request.getContextPath()%>/resources/images/icon_nav_calendar.png" alt="">					
				</div>
				<p class="weui-tabbar__label">收款汇总</p>
			</a> 
			<a href="#tab3" class="weui-tabbar__item" id="bar-more">
				<div class="weui-tabbar__icon">
					<img src="<%=request.getContextPath()%>/resources/images/icon_nav_actionSheet.png" alt="">
				</div>
				<p class="weui-tabbar__label">更多</p>
			</a> 
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
	  var statFlag = true;
	  $(function(){
	    FastClick.attach(document.body);
	    $("#tab-collection-list").infinite();
	    $("#query-date").val(queryDate);
	    
	    var func = "${function}";
	    $("#bar-"+func).addClass("weui-bar__item--on");
		$("#tab-"+func).addClass("weui-tab__bd-item--active");
	    if (func == "collection-list") {
	    	activeCollection(true);
	    } else if (func == "stat-list") {
	    	activeStat(true);
		} else if (func == "more") {
	    	activeMore(true);
	    }
	  });
	  
	  function activeCollection(init) {
		  $(document).attr("title","收款列表");
		  if (init == true) {
			initCollection();
			loadCollectionData(queryDate);
		  }
	  }
	  function activeStat(init) {
		  $(document).attr("title","收款汇总");
		  if (init == true || statFlag) {
			initStatLoad();
			loadStatData();
		  }
	  }
	  function activeMore(init) {
		  $(document).attr("title","更多");
		  if (init == true) {
		  }
	  }
	  
	  function initStatLoad(){
		  statFlag = true;
		  $("#stat-list").html("");
		  $("#stat-empty").hide();
	  }
	  
	  $(".weui-tabbar__item").on("click",function(){
		  var id =$(this).attr("id");
		  if (id=="bar-collection-list") {
			  activeCollection();
		  } else if (id=="bar-stat-list") {
			  activeStat();
		  } else if (id=="bar-stat-list") {
			  activeMore();
		  }		  
	  });
	  
	  $("#stat-query-store").on("change",function(){
		initStatLoad();
		loadStatData($("#stat-query-store").val());
	  });
	  
	  $("#query-date").on("change",function(){
		queryDate = $("#query-date").val();
		if (queryDate == '') {
			queryDate = "${today}";
			$("#query-date").val(queryDate);
		}
	    initCollection();
	    loadCollectionData(queryDate);
	  });
	  
	  $("#collection-query-store").on("change",function(){
	    initCollection();
	    loadCollectionData(queryDate, $("#collection-query-store").val());
	  });
	  
	  function initCollection(){
		  curPage = 0;
		  loading = false;
		  $("#collection-list").html("");
		  $("#collection-empty").hide();
		  $("#total").html("");
		  $("#tab-collection-list").infinite().on("infinite", function() {
		    if(loading) return;
		    loadCollectionData(queryDate, $("#collection-query-store").val());
		  });
	  }
	  
	  function loadCollectionData(date,storeOid){
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
	   					$("#tab-collection-list").destroyInfinite();
	   					$("#collection-empty").show();
	   				} else {
	   					if (dataSize < pageRow) {
	   						$("#tab-collection-list").destroyInfinite();
		   				}
	   					for(var i=0; i<data.payList.length; i++) {
				    		addCollectionCell(data.payList[i]);
				    	}
	   					loading = false;//加载成功
	   				}
	   			}
		    },"json");
	  }
	  
	  function addCollectionCell(item){
		  var cell = '<div class="weui-cell"><div class="weui-cell__bd collection-time">index&nbsp;&nbsp;transTime</div><div class="weui-cell__ft collection-money">collectionMoney</div></div>';
		  $("#collection-list").append(cell.replace("index", item.index).replace("transTime", item.transTime).replace("collectionMoney", item.collectionMoney));
	  }
	  
	  function loadStatData(storeOid){
		  $("#stat-loading").show();
		  $.get("<%=request.getContextPath()%>/appid/collection/statList/", 
		    		{ openid:"${openid}", dealerOid:"${dealerOid}", storeOid:"${storeOid}", dealerEmployeeOid:"${dealerEmployeeOid}", queryStoreOid:storeOid },
		    function(data,status,xhr){
	   			if (status == 'success'&& undefined != data) {
			    	$("#stat-loading").hide();
			    	statFlag = false;
	   				if (data.length == 0){
	   					$("#stat-empty").show();
	   				} else {
	   					for(var i=0; i<data.length; i++) {
				    		addStatCell(data[i]);
				    	}
	   				}
	   			}
		    },"json");
	  }
	  
	  function addStatCell(item){
		  var cell = '<div class="weui-cell"><div class="weui-cell__bd collection-time">statTime</div><div class="weui-cell__ft collection-money">总笔数:totalAmount&nbsp;&nbsp;总金额:totalMoney</div></div>';
		  $("#stat-list").append(cell.replace("statTime", item.statTime).replace("totalAmount", item.totalAmount).replace("totalMoney", item.totalMoney));
	  }
	</script>
</body>
</html>