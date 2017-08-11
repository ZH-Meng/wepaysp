<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="manage" uri="/permission-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>缴费账单管理</title>
	<link href="<%=request.getContextPath()%>/css/zxbgstyle.css" rel="stylesheet" />
	<link href="<%=request.getContextPath()%>/layui/css/layui.css" rel="stylesheet" />
</head>
<body class="bgbj">
	<div class="rightbg">
		<div class="bgposition">您现在的位置：缴费账单管理</div>
		<s:form method="post">
			<div class="bgtj">
				<ul class="tj_title">
					<li>查询条件</li>
				</ul>
				<ul class="bg_tjtab">
					<li class="bg_tjall">
						<table>
							<tbody>
								<tr>
									<th>账单时间范围</th>
									<td>
										<input type="text" name="beginTime" id="beginTime" class="Wdate" readonly="readonly" value="<s:property value="beginTime"/>"
											onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})"/>
										至	
										<input type="text" name="endTime" id="endTime" class="Wdate" readonly="readonly" value="<s:property value="endTime"/>"
											onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d+1}'})"/>
									</td>
									<th>账单名称</th>
									<td><s:textfield name="billName" id="billName" maxlength="20"/></td>
								</tr>
							</tbody>
						</table>
					</li>
					<li class="bg_button">
						<a href="javascript:void(0);" onclick="invokeAction('downloadBillTemplate');">下载账单模版</a>
						<a href="javascript:void(0);" id="sendBIll-btn">发送账单</a>
						<a href="javascript:void(0);" onclick="invokeAction('list');">查询</a>
					</li>
				</ul>
			</div>
	    	<div class="bgtable">
	            <ul class="bg_all">
	                <li class="bg_table bg_table1">
	                    <table class="bg_odd">
	                        <thead>
	                            <tr>
	                                <th style="width: 100px;">序号</th>
	                                <th>账单发送时间</th>
	                                <th>账单名称</th>
	                                <th class="twenty">操作</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        <s:if test="alipayEduTotalBillVOList != null && alipayEduTotalBillVOList.size() > 0">
			  					<s:iterator value="alipayEduTotalBillVOList" var="vo" status="rowStatus">
						  		<tr>
						  			<td>
						  				<s:property value="pageRows*(currPage-1) + #rowStatus.index + 1" />
						  			</td>
						  			<td title="<s:date name="#vo.sendTime" format="yyyy-MM-dd HH:mm:ss" />">
						  				<s:date name="#vo.sendTime" format="yyyy-MM-dd HH:mm:ss" />
						  			</td>
						  			<td title="<s:property value="#vo.billName" />">
						  				<s:property value="#vo.billName" />
						  			</td>
						  			<td title="操作">
					  					<a href="<%=request.getContextPath()%>/resources/edu/billdetail!list.action?totalBillOid=<s:property value="#vo.iwoid" />" >查看</a>
					  					<a href="<%=request.getContextPath()%>/resources/edu/billdetail!downloadBill.action?totalBillOid=<s:property value="#vo.iwoid" />" >下载账单</a>
						  			</td>
						  		</tr>
						  		</s:iterator>
			  				</s:if>
					  		<s:else>
					  			<tr><td colspan="4">无符合条件的查询结果！</td></tr>
					  		</s:else>
	                    	</tbody>
	               		</table>
	                </li>
	            </ul>
				<ul>
	            	<li class="t-center">
	                	<s:include value="/resources/include/page.jsp"></s:include>
	                </li>
	            </ul>
	    	</div>
	    </s:form>
	    <!-- TODO 改为弹出层 -->
    	<div id="uploadBillDialog" style="display: none;">
		     <form action="<%=request.getContextPath()%>/resources/edu/totalbillmanage!uploadBill.action"  enctype="multipart/form-data" method="post">
	            <label for="billFile">缴费账单Excel：</label><input type="file" name="billFile" />
	            <label for="endTime">账单截止日期：</label><input type="text" name="endTime" id="endTime" class="Wdate" readonly="readonly" value="<s:property value="endTime"/>"
											onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',minDate:'%y-%M-%d'})"/>
	            <label for="收费名称"></label><input type="text" name="billName"/>
	        </form>
    	</div>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script src="<%=request.getContextPath()%>/layui/layui.js" charset="utf-8"></script>
	
	<script type="text/javascript">
		/* $("#sendBIll-btn").toggle(function() {
			$("#uploadBillDialog").show();
		}, function() {
			$("#uploadBillDialog").hide();
		}); */
		
		function showUploadBillDialog() {
			$("#dealerOid").val(iwoid);
			invokeAction('goToAlipayManage');
		}
		
		layui.use('layer', function(){
			  var layer = layui.layer;
		});
		
		
		$('#sendBIll-btn').on('click', function(){
		  var type ='auto', text = $(this).text();
			  
			layer.open({
		        type: 1
		        ,offset: type //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
		        ,id: 'LAY_demo'+type //防止重复弹出
		        ,content: $("#uploadBillDialog")
		        ,btn: ['取消','确认']
		        ,btnAlign: 'c' //按钮居中
	        	,yes: function(index, layero){
	        	    //按钮【按钮一】的回调
	        	    return false;
	        	  }
	        	  ,btn2: function(index, layero){
	        	    //按钮【按钮二】的回调
	        	    
	        	    //return false 开启该代码可禁止点击该按钮关闭
	        		  return false;
	        	  }
		        ,shade: 0.1
		        ,yes: function(){
		          layer.closeAll();
		        }
		      });
		  });
	</script>
	
	
</body>
</html>