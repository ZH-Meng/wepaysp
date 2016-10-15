function iniNav(datas,path,target){
		function getRealRoot(data){
			var rootStr=''
			for(var i in data){
				if(data[i]['fatherId']===null){
					rootStr=data[i].nodeId
				}
			}

			return rootStr;
		}
		function getRoot(data){
			var arr=[],
				oData=data.concat(),
				rootId=getRealRoot(data)
			for(var i in data){
				if(data[i]['fatherId']===rootId){
					//���������ӽڵ�
					data[i].child=[]
					arr.push(data[i])
				}
			}
			data=oData
			return arr;
		}

		function createTree(r,data){
//			for(var i=0,l=data.length;i<l;i++){
//				for(var j=0,ll=r.length;j<ll;j++){
//					if(data[i].fatherId===r[j].nodeId){
//						data[i].child=[]
//						r[j].child.push(data[i])
//					}
//					if(j==(ll-1)&&r[j].child.length>0){
//						r[j].child.sort(sortNumber)
//						createTree(r[j].child,data)
//
//					}
//				}
//			}

			for(var i=0,l=r.length;i<l;i++){
				for(var j=0,ll=data.length;j<ll;j++){
					if(r[i].nodeId===data[j].fatherId){
						data[j].child=[]
						r[i].child.push(data[j])
					}
					if(j==(ll-1)&&r[i].child.length>0){
						r[i].child.sort(sortNumber)
						createTree(r[i].child,data)

					}
				}
			}

//			console.log(r)
			return r;
		}
		function sortNumber(a,b)
		{
			return a['nodeOrder'] - b['nodeOrder']
		}
		if(!datas){
			alert("�˵����ݲ����ڣ�")
			return false;
		}
		var data=datas.userNode

		if(data.length>0){
			var nData=getRoot(data)
			nData.sort(sortNumber)

			createTree(nData,data)
//			console.log(nData)
		}
		var path=path?path:''
		var arr=['<ul class="topmenu">']
		for(var i =0,l=nData.length;i<l;i++){
			arr.push('<li d-data="1"><h1><a class="top_menuico'+nData[i].nodeOrder+'" href="javascript:void(0);">'+nData[i].nodeName+'</a></h1>')
			if(nData[i].child.length>0){
				arr.push('<ul class="top_menulist">')
				for(var j=0,jl=nData[i].child.length;j<jl;j++){
					var jx=nData[i].child[j];
					var tar = jx.nodeUrl===null? "": target;
					var jhref=jx.nodeUrl===null?'javascript:void(0);':(path)+jx.nodeUrl

					arr.push('<li d-data="2"><h2><a target="'+tar+'" href="'+jhref+'">'+jx.nodeName+'</a></h2>')
						if(jx.child.length>0){
							arr.push('<ul class="top_menuconlist menuclose">')
							for(var k=0,kl=jx.child.length;k<kl;k++){
								var kx=jx.child[k]
								var tar = kx.nodeUrl===null? "": target;
								var khref=kx.nodeUrl===null?'javascript:void(0);':(path)+kx.nodeUrl
								arr.push('<li d-data="3"><h3><a target="'+tar+'" href="'+khref+'">'+kx.nodeName+'</a></h3>')
								//�ļ�
								if(kx.child.length>0){
									arr.push('<ul class="top_menuconlist2 menuclose">')
									for(var f=0,fl=kx.child.length;f<fl;f++){
										var fx=kx.child[f]
										var tar = fx.nodeUrl===null? "": target;
										var fhref=fx.nodeUrl===null?'javascript:void(0);':(path)+fx.nodeUrl
										arr.push('<li d-data="4"><h4><a target="'+tar+'" href="'+fhref+'">'+fx.nodeName+'</a></h4>');
										//�弶
										if(fx.child.length>0){
											arr.push('<ul class="top_menuconlist3 menuclose">')
											for(var g=0,gl=fx.child.length;g<gl;g++){
												var gx=fx.child[g]
												var tar = gx.nodeUrl===null? "": target;
												var ghref=gx.nodeUrl===null?'javascript:void(0);':(path)+gx.nodeUrl
												arr.push('<li d-data="5"><a target="'+tar+'" href="'+ghref+'">'+gx.nodeName+'</a>');
												arr.push('</li>')
											}




											arr.push('</ul>')

										}


										arr.push('</li>')
									}




									arr.push('</ul>')

								}
								arr.push('</li>')

							}
							arr.push('</ul>')
						}

					arr.push('</li>')
				}
				arr.push('</ul>')
			}
			arr.push('</li>')

		}

		arr.push('</ul>')
		$(".leftmenuone").html(arr.join(''))
		$(".leftmenuone a").bind("click",function(){
			var t=$(this),
				//�����ڵ��Ƿ� �Ѿ�չ��
				href=t.attr("href");
			if(href===null){
				return false;
			}
			//һ���˵����ɵ���
			if(t.parent().parent().attr("d-data") == 1){
				return;
			}

			var otherMenu = t.parents("[d-data=1]").siblings();
			if(t.parent().hasClass("h2active")){
				$(".leftmenuone h1").removeClass("h1active")
				$(".leftmenuone h2").removeClass("h2active")
				$(".leftmenuone h3").removeClass("h3active")
				$(".leftmenuone h4").removeClass("h4active")
				$(".leftmenuone .h5active").removeClass("h5active");
				$(".top_menuconlist").each(function(){
					$(this).addClass("menuclose");
				});
				t.parents(".top_menuconlist").each(function(){
					$(this).removeClass("menuclose");
				});
				return;
			}
			otherMenu.each(function(){
				$(".leftmenuone h1").removeClass("h1active")
				$(".leftmenuone h2").removeClass("h2active")
				$(".leftmenuone h3").removeClass("h3active")
				$(".leftmenuone h4").removeClass("h4active")
				$(".leftmenuone .h5active").removeClass("h5active");
			});
			$(".top_menuconlist").each(function(){
				$(this).addClass("menuclose");
			});
			t.parents(".top_menuconlist").each(function(){
				$(this).removeClass("menuclose");
			});

			if(t.parent()[0].tagName.toLowerCase() === 'li'){
				var num=t.parent().attr("d-data")
				t.parent().addClass("h"+num+"active")
				t=t.parent()
			}
			var nn=t.parent()[0].tagName.toLowerCase();
			//��ʾ����

			t.parent().addClass(nn+"active")
			var x=t.parent().next("ul")
			if(x.hasClass("menuclose")){
				x.removeClass("menuclose")
			}else{
				if(!/h1/.test(nn)){
					x.addClass("menuclose")
				}

			}

			t.parents("ul").each(function(){
				var tt=$(this),
					n;
					if(tt.prev().length>0){
						n=tt.prev()[0].tagName.toLowerCase()
						tt.prev().addClass(n+"active")
					}

			})
			//console.log("d")
//			t.parents('li').each(function(){
//				var tt=$(this),
//					num=tt.attr("d-data")
//					tt.addClass("h"+num+"active")
//			})
			//return false;
		})
	}
