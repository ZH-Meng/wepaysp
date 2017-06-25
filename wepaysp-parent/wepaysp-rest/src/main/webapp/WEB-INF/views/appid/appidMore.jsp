<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${not empty collectionNoticeState }">
	<input hidden="hidden" id="collnotice-state" type="radio" value="" checked="checked" />
	<div class="weui-cells weui-cells_form">
		<div class="weui-cell weui-cell_switch">
			<div class="weui-cell__bd">接收收款通知</div>
			<div class="weui-cell__ft">
				<input class="weui-switch" type="checkbox" id="collnotice-switch">
			</div>
		</div>
	</div>
	<script>
	var switching = false;
	$(function() {
		var collNoticeState = "${collectionNoticeState}";
		if (collNoticeState == "on") {
			$("#collnotice-switch").attr("checked", "checked");
			$("#collnotice-state").val("on");
		} else {
			$("#collnotice-state").val("off");
		}
	});
	
	$("#collnotice-switch").on("click", function() {
		if ($("#collnotice-state").val() == "off") {
			if (collnoticeSwitch("on")) {
				$("#collnotice-state").val("on");
			}
		} else {
			if (collnoticeSwitch("off")) {
				$("#collnotice-state").val("off");
			}
		}
	});
	
	function collnoticeSwitch(state) {
		var result = false;
		var oper = state == "on" ? "open" : "close";
		$.ajax({  
	          type : "get",  
	          url : "<%=request.getContextPath()%>/appid/more/collectionNotice/" + oper,  
	          data : { bindOid:"${bindCashierOid}"},
	          async : false,  
	          dataType : "json",
	          success : function(data){
	        	result = true;
	          },
	          error : function() {
       		  	alert("操作失败");
			  	$("#tab-more").load("<%=request.getContextPath()%>/appid/more/index",{openid:"${openid}"},function(response,status,xhr){
		   			if (status == 'success') {
		   				initMoreFlag = false;
		   			}
				 });
	          }
          });
		return result;
	}
</script>
</c:if>

