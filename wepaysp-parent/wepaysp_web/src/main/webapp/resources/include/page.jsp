<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
			<input type="hidden" name="rowCount" value="<s:property value="rowCount" />" />
			<input type="hidden" name="pageRows" value="<s:property value="pageRows" />" />
			<input type="hidden" name="pageCount" value="<s:property value="pageCount" />" />
			<input type="hidden" name="currPage" value="<s:property value="currPage" />" />
			
			<div class="bg_page">
				<s:if test="pageCount > 0">
            	<p>当前<span><s:property value="currPage"/></span>/<span><s:property value="pageCount"/></span>页</p>
            	</s:if>
	            <p>
	            	<s:if test="disabledFirst == null || disabledFirst == 'true'">首页</s:if>
					<s:else><a href="#" onclick="invokeAction('goFirst')">首页</a></s:else>
	            </p>
	            <p>
	            	<s:if test="disabledBack == null || disabledBack == 'true'">上一页</s:if>
					<s:else><a href="#" onclick="invokeAction('goBack')">上一页</a></s:else>
	            </p>
	            <p>
	            	<s:if test="disabledNext == null || disabledNext == 'true'">下一页</s:if>
					<s:else><a href="#" onclick="invokeAction('goNext')">下一页</a></s:else>
	            </p>
	            <p>
	            	<s:if test="disabledLast == null || disabledLast == 'true'">末页</s:if>
					<s:else><a href="#" onclick="invokeAction('goLast')">末页</a></s:else>
	            </p>
	            <p>
	            	<span style="vertical-align:top;">跳转至</span>
	                <input class="page_num" type="text" name="goPage" value="<s:property value="goPage"/>" size="8" maxlength="8" />
	                <span style="vertical-align:top;">页&gt;</span>
	                <a href="#" onclick="invokeAction('reGoPage')">GO</a>
	           </p>
            </div>
			
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
			