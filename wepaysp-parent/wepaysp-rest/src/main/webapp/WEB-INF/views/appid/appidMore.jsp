<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
		if (switching) {
			return;
		}
		if ($("#collnotice-state").val() == "off") {
			console.log("off");
			switching = true;
			if (collnoticeSwitch("on")) {
				console.log("on");
				$("#collnotice-state").val("on");
			}
		} else {
			console.log("on");
			switching = true;
			if (collnoticeSwitch("off")) {
				console.log("off");
				$("#collnotice-state").val("off");
			}
		}
	});
	
	function collnoticeSwitch(state) {
		console.log(state);
		var oper = state == "on" ? "open" : "close";
		$.get("<%=request.getContextPath()%>/appid/more/collectionNotice/" + oper, { bindOid:"${bindOid}"},
	    function(data,status,xhr){
			if (status == 'success'&& undefined != data) {
				console.log(data);
				switching = false;
				return true;
			}
	    },"json");
		return false;
	}
</script>