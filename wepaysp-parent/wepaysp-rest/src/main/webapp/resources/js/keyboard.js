(function(exports){
	var amountText='';
	
	var KeyBoard = function(input, options){
		var body = document.getElementsByTagName('body')[0];
		var DIV_ID = options && options.divId || '__w_l_h_v_c_z_e_r_o_divid';
		
		if(document.getElementById(DIV_ID)){
			body.removeChild(document.getElementById(DIV_ID));
		}
		
		this.input = input;
		this.el = document.createElement('div');
		
		var self = this;
		var zIndex = options && options.zIndex || 1000;
		var width = options && options.width || '100%';
		var height = options && options.height || '206px';
		var fontSize = options && options.fontSize || '15px';
		var backgroundColor = options && options.backgroundColor || '#fff';
		var TABLE_ID = options && options.table_id || 'table_0909099';
		var mobile = typeof orientation !== 'undefined';
		
		this.el.id = DIV_ID;
		this.el.style.position = 'fixed';
		this.el.style.left = 0;
		this.el.style.right = 0;
		this.el.style.bottom = 0;
		this.el.style.zIndex = zIndex;
		this.el.style.width = width;
		this.el.style.height = height;
		this.el.style.backgroundColor = backgroundColor;
		
		//样式
		var cssStr = '<style type="text/css">';
		cssStr += '#' + TABLE_ID + '{-moz-user-select:none;-webkit-user-select:none;text-align:center;width:100%;height:206px;border-top:1px solid #eee;background-color:#FFF;}';
		cssStr += '#' + TABLE_ID + ' td{font-size: 22px;width:33%;border:1px solid #eee;border-right:0;border-top:0;}';
		if(!mobile){
			cssStr += '#' + TABLE_ID + ' td:hover{background-color:#1FB9FF;color:#FFF;}';
		}
		cssStr += '</style>';
		
		//table
		var tableStr = '<table id="' + TABLE_ID + '" border="0" cellspacing="0" cellpadding="0">';
			tableStr += '<tr><td>1</td><td>2</td><td>3</td></tr>';
			tableStr += '<tr><td>4</td><td>5</td><td>6</td></tr>';
			tableStr += '<tr><td>7</td><td>8</td><td>9</td></tr>';
			tableStr += '<tr><td style="background-color:#eee;">.</td><td>0</td>';
			tableStr += '<td class="wen" style="background-color:#eee;"><i style="font-size: 26px;" class="icon iconfont icon-shurushanchu"></i></td></tr>';
			tableStr += '</table>';
		this.el.innerHTML = cssStr + tableStr;
		
		function addEvent1(e){
			var ev = e || window.event;
			var clickEl = ev.element || ev.target;
			var value = clickEl.textContent || clickEl.innerText;
			
			if(amountText.toString().length < 10) {
				// 输入01234567890
				if(clickEl.tagName.toLocaleLowerCase() === 'td' && value !== "×" && value !== ".") {

					if(amountText == "0" && value == "0") {// 已经输入了一个0，再输入一个0
						$("#ok-btn").attr("disabled", true);
					} else if(amountText.indexOf(".") >= 0) {// 包含小数点
						if(amountText.toString().split(".")[1].length < 2) {// 小数位数不超过2
							if(self.input) {
								amountText += value;
							}
						}
					} else {
						if(self.input) {
							if (amountText == "0") {
								amountText = value;
							} else {
								amountText += value;
							}
						}
					}
				}
				// 输入.
				else if(clickEl.tagName.toLocaleLowerCase() === 'td' && value === ".") {
					if(amountText.indexOf(".") == -1 && amountText !='') {// 未包含小数点
						if(self.input) {
							amountText += value;
						}
					} 
				}
			}
			
			// 点击“×”
			if(clickEl.tagName.toLocaleLowerCase() === 'td' && (clickEl.className === "wen") || (clickEl.className === "icon iconfont icon-shurushanchu")) {
				var num = amountText;
				if(num) {
					var newNum = num.substr(0, num.length - 1);
					amountText = newNum;
				}
			}
			// 点击“完成”
			else if(clickEl.tagName.toLocaleLowerCase() === 'div' && value === "完成") {
				body.removeChild(self.el);
			} 

		}
		
		function addEvent2(e){
			self.input.value=amountText;
			
			// 判断输入框是否为空或者金额为 0
			if(self.input.value == "") {
				$("#ok-btn").attr("disabled", true);
			} else if (parseFloat(self.input.value) == "0") {
				$("#ok-btn").attr("disabled", true);
			} else {
				$("#ok-btn").attr("disabled", false);
			}
		}
		
		if(mobile) {
			this.el.ontouchstart = addEvent1;
			this.el.ontouchend = addEvent2;
		} else {
			this.el.onmousedown = addEvent1;
			this.el.onmouseup = addEvent2;
		}
		body.appendChild(this.el);
	}
	
	exports.KeyBoard = KeyBoard;

})(window);
