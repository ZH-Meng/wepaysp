String.prototype.trim = function(){
	return this.replace(/(^\s*)|(\s*$)/g, "");
}
// 正整数正则表达式(包括0)
var positiveIntegerPattern = /^[0-9]+$/;
//正整数正则表达式
var positiveIntegerPattern1 = /^[1-9][0-9]*$/;
var emailPattern = /^("(?:\\[ -~]|[ !#-\[\]-~])+"|[!#-'*+\-\/\d=?A-Z^_`a-z{|}~]+(?:\.[!#-'*+\-\/\d=?A-Z^_`a-z{|}~]+)*)@((?:[\dA-Za-z](?:[-\dA-Za-z]*[\dA-Za-z])?\.)+[A-Za-z][-A-Za-z]*[A-Za-z])$/;
var mobilePattern = /^1\d{10}$/;
var alphaNumericPattern = /^\w+$/;
var userIdPattern = /^[0-9|a-z|A-Z]{1,20}$/;
var alpNumWordPattern = /^[0-9|a-z|A-Z|\u4e00-\u9fa5]+$/;//字母数字或汉字
var alpWordPattern = /^[a-z|A-Z|\u4e00-\u9fa5]+$/;//字母或汉字
var lineTel = /^[0-9](-|[0-9]){5,16}[0-9]+$/;//联系人，7-18位数字或-
var lineTel5_18 = /^[0-9](-|[0-9]){3,16}[0-9]+$/;//5-18位数字或-
var zIntergerPattern = /^[0-9]*[1-9][0-9]*$/;
var bankCardNoPattern = /^\d{15,19}$/;  //银行卡有可能是16位也有可能是19位 工行的运通卡特别，15位
//如果你只要带空格的16位    /^\d{4}(?:\s\d{4}){3}$/
var idCardNoPattern = /^\d{17}(\d|x|X)$/; //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X  支持输入小写x   (^\d{15}$)|
var digital10Pattern = /^[0-9]{10}$/;//10位数字或-
var digital8Pattern = /^[0-9]{8}$/;//8位数字或-
var macPattern = /[A-Fa-f\d]{2}:[A-Fa-f\d]{2}:[A-Fa-f\d]{2}:[A-Fa-f\d]{2}:[A-Fa-f\d]{2}:[A-Fa-f\d]{2}/; 
var qqPattern = /^[1-9][0-9]{4,9}$/;
var digital6Pattern = /^\d{6}$/;//6位数字

var piRegExp = new RegExp(positiveIntegerPattern);
var piRegExp1 = new RegExp(positiveIntegerPattern1);
var emailRegExp = new RegExp(emailPattern);
var mobileRegExp = new RegExp(mobilePattern);
var userIdRegExp = new RegExp(userIdPattern);
var wordExp = new RegExp(alpNumWordPattern);
var alpWordExp = new RegExp(alpWordPattern);
var lineTelExp = new RegExp(lineTel);
var lineTelExp5_18 = new RegExp(lineTel5_18);
var zIntergerExp = new RegExp(zIntergerPattern);
var bankCardNoExp = new RegExp(bankCardNoPattern);
var idCardNoExp = new RegExp(idCardNoPattern);
var digital10Exp = new RegExp(digital10Pattern);
var digital8Exp = new RegExp(digital8Pattern);
var macExp = new RegExp(macPattern);
var qqExp = new RegExp(qqPattern);
var digital6Exp = new RegExp(digital6Pattern);

//MAC地址校验
function isMac(str){
	if(macExp.test(str)){
		return true;
	}
	return false;
}

//投注站编号8位数字
function isDigital8Exp(str){
	if(digital8Exp.test(str)){
		return true;
	}
	return false;
}

//设备编码10位数字
function isDigital10Exp(str){
	if(digital10Exp.test(str)){
		return true;
	}
	return false;
}

//银行卡号码校验，输入位数与格式
function isBankCardNo(str){
	if(bankCardNoExp.test(str)){
		return true;
	}
	return false;
}

//身份证号码校验，输入位数与格式
function isIdCardNo(str){
	if(idCardNoExp.test(str)){
		return true;
	}
	return false;
}

//判断字符串是否为null或者""
function isBlank(str){
	if(str == null || str.trim().length == 0){
		return true;
	} else {
		return false;
	}
}

// 判断字符串是否为数字或字母
function isAlphaNumeric(str) {
	if(alphaNumericPattern.test(str)){
		return true;
	}
	
	return false;
}


// 判断字符串是否正整数
function isPositiveInteger(str){
	if(piRegExp.test(str)){
		return true;
	}
	
	return false;
}

//判断字符串是否正整数
function isPositiveInteger1(str){
	if(piRegExp1.test(str)){
		return true;
	}
	
	return false;
}

// 判断字符串是否是邮件地址
function isEmail(str){
	if(emailRegExp.test(str)){
		return true;
	}
	
	return false;
}

function isMobile(str){
	if(mobileRegExp.test(str)){
		return true;
	}
	
	return false;
}

//判断用户登录名是否合法
function isUserId(str){
	if(userIdRegExp.test(str)){
		return true;
	}
	
	return false;
}

//判断只能是数字，字母或者汉字
function isWord(str){
	if(wordExp.test(str)){
		return true;
	}
	
	return false;
}
//判断只能是字母或者汉字
function isChinWord(str){
	if(alpWordExp.test(str)){
		return true;
	}
	return false;
}

//联系电话应为7-18位数字或-
function islineTel(str){
	if(lineTelExp.test(str)){
		return true;
	}
	
	return false;
}
//客服电话应为5-18位数字或-
function islineTel5_18(str){
	if(lineTelExp5_18.test(str)){
		return true;
	}
	
	return false;
}

function isQQ(str) {  
    if(qqExp.test(str)){
		return true;
	}
   return false;  
}  

//6位数字
function isDigital6Exp(str){
	if(digital6Exp.test(str)){
		return true;
	}
	return false;
}


var saveListBackgroundColor = null;

function high(obj) {
	saveListBackgroundColor = obj.style.backgroundColor ;
	
	if (obj.style != null) {
		obj.style.backgroundColor = 'rgb(207,229,251)';
	}
}

function low(obj) {
	if (obj.style != null) {
		obj.style.backgroundColor = saveListBackgroundColor;
	}
}

//判断两个日期字符串所表示的日期相隔天数
function DateDiff(endDate, startDate) {
	var startTime = new Date(Date.parse(startDate.replace(/-/g,   "/"))).getTime();      
    var endTime = new Date(Date.parse(endDate.replace(/-/g,   "/"))).getTime();
    var dates = (endTime - startTime)/(1000*60*60*24);      
	return dates;
}
//判断日期是否晚于当前日期（日期格式yyyy-MM-dd）
function isAfterCurrent(date){
	var today=new Date();
	date = new Date(Date.parse(date.replace("-","/")));
	return date>today;
}
function isZInterger(str){
	if(zIntergerExp.test(str)){
		return true;
	}
	return false;
}

//判断两个日期字符串所表示的日期相隔月份（日期格式yyyy-MM-dd）
function monthDiff(endDate, startDate){
	var m1 = parseInt(startDate.split("-")[1].replace(/^0+/, "")) + parseInt(startDate.split("-")[0]) * 12;
    var m2 = parseInt(endDate.split("-")[1].replace(/^0+/, "")) + parseInt(endDate.split("-")[0]) * 12;
    // day 相隔
    var dayDiff = parseInt(endDate.split("-")[2].replace(/^0+/, "")) - parseInt(startDate.split("-")[2].replace(/^0+/, ""));
    if (m2-m1 > 0) {
    	if (dayDiff <= -1) {
    		return m2-m1;
    	} else {
    		return m2-m1+1;
    	}
    } else {
    	return m2-m1;
    }
}

$.ajaxSetup({
	 type: 'POST',
	 complete: function(xhr,status) {
		 var sessionStatus = xhr.getResponseHeader('sessionstatus');
		 if(sessionStatus == 'timeout') {
			 window.parent.location.href = 'index.jsp';
		 }
	 }
});