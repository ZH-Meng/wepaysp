/*
--------------------------------------------------------------------
        文件名称：   mask.js
        开发人员：   郑万俊
        描述： 遮罩层
        版本： 1.0
----------------------------------------------------------------------
*/
var stc = stc || {};
stc = (function (stc,$){
    var _private = stc._private = stc._private || {};
    _private.maskBlock = "<div class='blockmask'"
						+"	style='position: fixed; left: 0px; top: 0px; width: 100%; height: 100%; "
						+"  overflow: hidden; z-index: 2000; background-color:white; background: #fff !important;filter: alpha(opacity=80);-moz-opacity:0.8;-khtml-opacity: 0.3;opacity: 0.8;'> "
						+"  <div style='position: fixed;width: 125px;height: 32px;left: 50%;top: 50%;'> "
						+"		<img style='display:inline-block;*display:inline;zoom:1' src='../../../images/wait32.gif'/><div style='display:inline-block;*display:inline;zoom:1;vertical-align: super;' >处理中</div>"
						+"  </div> "
						+"</div> ";
	stc.mask = {};
    stc.mask.open = function(infoMsg,fun){
//    	$(window.top.frames["leftFrame"].document.body).append("<div class='blockmask' style='position: fixed; left: 0px; top: 0px; width: 100%; height: 100%;overflow: hidden; z-index: 2000; background-color:white; background: #fff !important;filter: alpha(opacity=80);-moz-opacity:0.8;-khtml-opacity: 0.3;opacity: 0.8;'></div>");
//    	$(window.top.frames["topFrame"].document.body).append("<div class='blockmask' style='position: fixed; left: 0px; top: 0px; width: 100%; height: 100%;overflow: hidden; z-index: 2000; background-color:white; background: #fff !important;filter: alpha(opacity=80);-moz-opacity:0.8;-khtml-opacity: 0.3;opacity: 0.8;'></div>");
    	$("body").append(_private.maskBlock);
	}
	stc.mask.close = function(infoMsg,fun){
//		$(window.top.frames["leftFrame"].document.body).find(".blockmask").eq(0).remove();
//    	$(window.top.frames["topFrame"].document.body).find(".blockmask").eq(0).remove();
    	$(".blockmask").remove();
	}
    return stc;
} (stc,$));

