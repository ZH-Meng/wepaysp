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
	<style>
		.layui-form-label{width: 100px;}
		.layui-input-block{margin-left: 130px;}
		.layui-form-item{margin-bottom: 5px;}
		.layui-input-block input.Wdate{
			width: 185px;
		    height: 22px;
		    line-height: 22px;
		    font-size: 12px;
		    border: 1px solid #c7c7c7;
		    background: #fff;
		}
	</style>
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
											onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})"/>
									</td>
									<th>收费备注</th>
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
	                                <th>收费备注</th>
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
						  			<s:if test="#vo.sendTime == null">
										<td></td>	
						  			</s:if>
						  			<s:else>
							  			<td title="<s:date name="#vo.sendTime" format="yyyy-MM-dd HH:mm:ss" />">
							  				<s:date name="#vo.sendTime" format="yyyy-MM-dd HH:mm:ss" />
							  			</td>
						  			</s:else>
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
	    
    	<div id="uploadBillDialog" style="display: none;">
		     <form id="new-bill-form" class="layui-form" style="padding: 5px 10px 0px 0px;">
			    <input type="hidden" name="saveName" id="excel-save-name"/>
		     	<div class="layui-form-item">
		     		<label class="layui-form-label" for="billFile">缴费账单Excel：<span class="tj_bt">*</span></label>
				    <div class="layui-input-block">
				       <input type="file" name="billFile" lay-type="file" class="layui-upload-file" class="layui-input"/>
				       <span id="excel-display-name"></span>
				    </div>
			    </div>
			    <div class="layui-form-item">
				    <label class="layui-form-label" for="endTime">账单截止日期：</label>
				    <div class="layui-input-block">
				      <input type="text" name="endTime" id="endTime" class="Wdate layui-input" readonly="readonly" value="<s:property value="endTime"/>"
											onfocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM-dd',minDate:'%y-%M-%d'})"/>
				    </div>
			    </div>
			    <div class="layui-form-item">
				    <label class="layui-form-label" for="billFile">收费备注：<span class="tj_bt">*</span></label>
				    <div class="layui-input-block">
				      <input id="bill-name" type="text" name="billName" lay-verify="title" autocomplete="off" placeholder="" class="layui-input"/>
				    </div>
				</div>
	        </form>
    	</div>
	</div>
	<s:property value="#request.messageBean.alertMessage" escape="false" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/check.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/tools/datePicker/WdatePicker.js"></script>
	<script src="<%=request.getContextPath()%>/layui/layui.js" charset="utf-8"></script>
	
	<script type="text/javascript">
		var layer;
		layui.use(['layer', 'upload'],
		function() {
		    layer = layui.layer;
	
		    layui.upload({
		        url: '<%=request.getContextPath()%>/resources/edu/totalbillmanage!uploadExcel.action',
		        before: function(input) {
		            $("#excel-save-name").val("");
		            $("#excel-display-name").text("");
		        },
		        success: function(res) {
		            if (res.code == 'success') {
		                $("#excel-save-name").val(res.saveName);
		                $("#excel-display-name").text(res.displayName);
		            } else {
		            	layer.alert(res.msg);
		            }
		        }
		    });
		});
	
		var uploading = false;
	
		$('#sendBIll-btn').on('click',
		function() {
		    var type = 'auto';
	
		    layer.open({
		        type: 1,
		        offset: type //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
		        ,
		        id: 'LAY_demo' + type //防止重复弹出
		        ,
		        area: ['400px', '300px'],
		        title: '发送缴费账单',
		        content: $("#uploadBillDialog"),
		        btn: ['发送', '关闭'],
		        btnAlign: 'c' //按钮居中
		        ,
		        yes: function(index, layero) {
		        	if (uploading) return;
		            uploading = true;
		            if (isBlank($("#excel-save-name").val())) {
		                layer.alert("缴费账单excel还未上传！");
		            } else if (isBlank($("#bill-name").val())) {
		                layer.alert("收费备注不能为空！");
		                $("#bill-name").focus();
		            } else {
		                $.ajax({
		                    url: '<%=request.getContextPath()%>/resources/edu/totalbillmanage!newBill.action',
		                    data: $("#new-bill-form").serialize(),
		                    type: "POST",
		                    async: false,
		                    success: function(data) {
		                        if (data.msg != undefined) {
		                        	if (data.code != 'success') layer.alert(data.msg);
		                        }  else 
		                        	alert("上传失败！");
		                        if (data.code == 'success') {
		                        	alert("上传成功！");
		                            layer.closeAll();
		                            invokeAction('list');
		                        }
		                    },
		                    error: function() {
		                        alert("上传失败！");
		                    }
		                });
		            }
		            uploading = false;
		            return false; // 开启该代码可禁止点击该按钮关闭
		        },
		        btn2: function(index, layero) {
		        	 layer.closeAll();
		        },
		        shade: 0.1
		    });
		});
	</script>
	
</body>
</html>