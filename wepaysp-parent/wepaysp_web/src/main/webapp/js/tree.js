/*
--------------------------------------------------------------------
        文件名称：   tree.js
        开发人员：   郑万俊
        描述： 树形菜单
        版本： 1.0
----------------------------------------------------------------------
*/
var stc = stc || {};
var rh = "";
stc = (function (stc,$){
    var _private = stc._private = stc._private || {};
	function Tree(){

	}
	var str ="<ul class='topmenu'>";
	Tree.prototype = {
		init:function(option){
			var defaultSet = {
				//数据
				data:obj,
				//target属性
				tar:"mainFrame",
				//请求前缀
				reqHead :'',
				//显示的节点
				pos:".leftmenuone",
				//显示到几级菜单
				limit:"2"
			}
			var cfg=$.extend(defaultSet,option);
			this.jumpTo = cfg.jumpTo;
			this.datas = cfg.data.userNode;
			this.tar = cfg.tar;
			this.reqHead = cfg.reqHead;
			rh = cfg.reqHead;
			if(!cfg.reqHead){
				this.reqHead="";
			}
			this.pos = cfg.pos;
			this.limit = cfg.limit;
			this.paintTree();
			this.bindClick();
		},
		//将数据填入
		paintTree:function(){
			var menuObj = this.getMenuObj();
			var target = this.tar;
			this.paint(menuObj,this.limit);
			str += "</ul>";
			var pos = this.pos;
			$(pos).html(str);
			$(".top_menuconlist").each(function(){
				var childrenLen = $(this).children().length;
				if(childrenLen < 1){
					$(this).remove();
				}
			});
			//默认入口
			this.defaultMenu();
		},
		defaultMenu:function(){
			var that = this;
			var jumpAdd = that.jumpTo;
			if(!jumpAdd) return;
			$(".h1active").removeClass("h1active");
			//打开默认
			var foldTar = $("[href$='"+that.jumpTo+"']");
			foldTar.parent().addClass("h2active");
			//关闭其他一级菜单
			foldTar.parents(".top_menulist").eq(0).siblings("ul").addClass("menuclose");
			//打开选中的一级菜单
			foldTar.parents(".top_menulist").eq(0).prev().find("h1").eq(0).addClass("h1active");
		},
		//将数据数组化
		getMenuObj:function(){
			var datas = this.datas, datasLen = datas.length,rootNode,nodeId,nodes;
			if(datasLen == 0) return;
			rootNode = this.getMenuByNodeId();
			//通过root节点找到以下的菜单
			var mainMenu = this.getMenuByNodeId(rootNode[0]["nodeId"]);
			var nodes = this.circle(mainMenu,datas);
			return nodes;
		},
		//遍历
		circle:function(nodes,data){
			for(var i=0,l=nodes.length;i<l;i++){
				for(var j=0,ll=data.length;j<ll;j++){
					if(nodes[i].nodeId===data[j].fatherId){
						data[j].child=[];
						if(!nodes[i].child) nodes[i].child=[];
						nodes[i].child.push(data[j]);
					}
					if(!nodes[i].child) continue;
					if(j==(ll-1)&&nodes[i].child.length>0){
						nodes[i].child.sort(this.sortNumber);
						this.circle(nodes[i].child,data);
					}
				}
			}
			return nodes;
		},
		//通过nodeId获得菜单
		getMenuByNodeId:function(nodeId){
			var datas = this.datas,
				menuArr = [];
			if(nodeId){
				for(var i in datas){
					if(datas[i]['fatherId'] == nodeId){
						menuArr.push(datas[i]);
					}
				}
				return menuArr;
			}else{
				for(var i in datas){
					if(datas[i]['fatherId'] === null){
						//Ö»ÒªÒ»¸ö¸ù½Úµã
						menuArr.push(datas[i]);
						return menuArr;
					}
				}
			}
		},
		//菜单排序
		sortNumber:function(a,b){
			return a['nodeOrder'] - b['nodeOrder']
		},
		paint:function(o,limit,h,target){
			var tar = this.tar ||target;
			target = tar;
			
			var level = h || 0;
			level ++;
			for(var i=0;i<o.length;i++){
				var reqHead = rh;
				var urlstr = "";
				var url = "";
				if(!o[i]["nodeUrl"] || o[i]["nodeUrl"] =="null"){
					 tar="";
					 url = "javascript:void(0);";
					 reqHead ="";
				}else{
					url = o[i]["nodeUrl"];
					tar=target;
				}
				if(level == 1){
					urlstr = "<li><h"+level+" class='h1active'><a  class='top_menuico3' target='"+tar+"' href='"+reqHead+url+"'>"+ o[i]["nodeName"] +"</a></h"+level+"></li><ul class='top_menulist'>";
				}else{
					urlstr = "<li><h"+level+"><a  target='"+tar+"' href='"+reqHead+url+"'>"+ o[i]["nodeName"] +"</a></h"+level+"></li><ul class='top_menuconlist menuclose'>";
				}
				str += urlstr;
				//Ö»ÏÔÊ¾µ½ 3¼¶²Ëµ¥
				if(level < limit && o[i]["child"] && o[i]["child"].length > 0){
				 	arguments.callee(o[i]["child"],limit,level,target);
				}
				str += "</ul></div>";
			}
		},
		bindClick:function(){
			var pos = this.pos;
			$(pos).on("click","a",function(){
				var that = $(this);
				var parent = that.parent();
				var href = that.attr("href");
				var flag = false;
				var parentTagName = parent.get(0).tagName.toLowerCase();
				//判断是否是合上的状态
				if(parent.parent().next().hasClass("menuclose")){
					flag = true;
				}
				//判断你是否是一级菜单
				if(parentTagName != "h1"){
					//隐藏 菜单及将标记去除
					$(".leftmenuone h2").removeClass("h2active")
					$(".leftmenuone h3").removeClass("h3active")
					$(".leftmenuone h4").removeClass("h4active")
					$(".leftmenuone .h5active").removeClass("h5active");
					$(".top_menuconlist").each(function(){
						$(this).addClass("menuclose");
					});
					that.parents(".top_menuconlist").each(function(){
						$(this).removeClass("menuclose");
					});
					if(flag){
						parent.addClass(parentTagName+"active");
						parent.parent().next().removeClass("menuclose");
					}
					if(parentTagName != "h2"){
						var num = parentTagName.substr(1,1);
						num = parseInt(num)-1;
						parent.parent().parent().prev().children().eq(0).addClass("h"+num+"active");
					}
				}else{
					if(parent.hasClass("h1active")){
						parent.removeClass("h1active");
						parent.parent().next().addClass("menuclose");
						parent.parent().next().find("ul").each(function(){
							$(this).addClass("menuclose");
						});
					}else{
						parent.parent().next().removeClass("menuclose");
						parent.addClass("h1active");
					}
				}
			});
		}
	}
	stc.tree = new Tree();
    return stc;
} (stc,$));
$(document).on("click",".top_menulist li",function(){
    $(".top_menulist .active").removeClass("active");
    $(this).addClass("active");
})
