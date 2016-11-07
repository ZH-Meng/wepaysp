<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
			
<script type="text/javascript">
	function invokeAction(methodName){
		var formObj = document.forms[0];
		var actionURL = formObj.action;
		var lastPoint = actionURL.lastIndexOf(".");
                 var lastLine = actionURL.lastIndexOf("/");
                 
                 if(actionURL.indexOf("!") > 0){
                 	lastPoint = actionURL.lastIndexOf("!");
                 }
                 
                 var prefix = actionURL.substring(lastLine + 1, lastPoint);
		
		formObj.action = prefix + "!" + methodName + ".action";
		formObj.submit();
	}
	/**
	 *
	 * 弹出窗口显示数据
	 */
	function invokeAction2(methodName){
		var iwoid=$("input[name='iwoid']").val();
		var formObj = document.forms[0];
		var actionURL = formObj.action;
		var lastPoint = actionURL.lastIndexOf(".");
                 var lastLine = actionURL.lastIndexOf("/");
                 
                 if(actionURL.indexOf("!") > 0){
                 	lastPoint = actionURL.lastIndexOf("!");
                 }
                 var prefix = actionURL.substring(lastLine + 1, lastPoint);
		window.open((prefix + "!" + methodName + ".action?"+"iwoid="+iwoid),  'newwindow', 
				'height=300, width=1100, top=300, left=200, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
	}
</script>
			