var menuurl =  ctx+"/common/getMenuTreeById.htm";
function initLeftNav(nav){
	var funcId = nav.attr("funcId");
	var topUrl = nav.attr("url");
	initLeftNavUtil(funcId,topUrl);
}


function initLeftNavUtil(funcId,url){
	if(url!=null&&url.indexOf("mainc")<0){
        var $jMenuTab = $('.J_menuTab');
        if(0 != $jMenuTab.size()){
            $($jMenuTab[0]).click();
        }
        $(".navbar-default").hide();
		$("#page-wrapper").css("margin-left","0px");
		$(".J_menuTabs").hide();
		$("#frmright").attr("src", url);
		$("#side-menu").hide();
		$("#page-wrapper .logo").show();
		$(".roll-nav").not('.J_tabExit').hide();
		return false;
	}else{
		var tmp = $("#frmright").attr("data-id");
		$("#frmright").attr("src", tmp);
	}
	$(".roll-nav").not('.J_tabExit').show();
	$("#side-menu").show();
	$("#page-wrapper .logo").hide();
	$(".navbar-default").show();
	var width = $(".navbar-default").width();
	$("#page-wrapper").css("margin-left",'');
	$(".J_menuTabs").show();


	var str = "";
	var forTree = function(o,j){
        if(j==null||j==""){
            j=0;
        }
        j++;
		for(var i=0;i<o.length;i++){
			var urlstr = "";
			try{
				var url =o[i]["url"];
				if(o[i]["nodes"] != null&&o[i]["nodes"].length>0){
					url = "";
				}
				var classType = "";
				if(o[i]["nodes"][0] != null){
					classType = "nav_title";
				}else{
					classType = "nav_item";
				}
				urlstr = "<li class='"+classType+"'><a href='"+url+"' class='J_menuItem nav_img "+o[i]["icon"]+"' target='frmright'><span class='nav-label'>"+ o[i]["name"] +"</span></a><ul class='nav nav-"+j+"-level'>";
				str += urlstr;
				if(o[i]["nodes"][0] != null){
					forTree(o[i]["nodes"],j);
				}
				str += "</ul></li>";
			}catch(e){}
		}
		return str;
	}

	$.ajaxSetup({
	    async : false
	});

	$.post(menuurl, {"id":funcId}, function(result){
		var myArray=new Array()
		myArray[0] = result;
		$("#side-menu").html(forTree(myArray));
		$("#side-menu").append("<span class='nav-bar'></span>");


		$("#side-menu li").each(function(index, element) {
			var $ul = $(this).find("ul:first");
			var ulContent = $ul.html();
	        if(ulContent!=null&&ulContent!=""){
	        	$(this).find("a:first").append($("<span class='fa arrow'></span>"));
				//$ul.css("padding-left","25px");
			}else{
				$ul.remove();
			}
	    });

		$("#side-menu .nav_item").click(function(){
			$("#side-menu .nav_item").removeClass("nav_active");
			$(this).addClass("nav_active");
		});

		$("#side-menu .nav_item").hover(function(){
			var top = $(this).offset().top-$("#side-menu").offset().top;
			$(".nav-bar").css("top",top);
			$(".nav-bar").css("height","40px");
			$(".nav-bar").css("width","5px");
			$(".nav-bar").css("opacity","1");

		},function(){
			$(".nav-bar").css("height","0");
			$(".nav-bar").css("opacity","0");

		});


		$("#side-menu").metisMenu();
		$("#side-menu li:first a:first").click();
		contabs.initDataId();
		$("#side-menu li:first").find("li:first a")[0].click();
	}, "json");

	$(".sidebar-collapse").slimScroll({
		height: "100%",
		railOpacity: .9,
		alwaysVisible: !1
	});
	return false;
};





function initECLeftNavUtil(funcId,webId,webType){


	$(".roll-nav").not('.J_tabExit').show();
	$("#side-menu").show();
	$("#page-wrapper .logo").hide();
	$(".navbar-default").show();
	var width = $(".navbar-default").width();
	$("#page-wrapper").css("margin-left",'');
	$(".J_menuTabs").show();


	var str = "";
	var forTree = function(o,j){
        if(j==null||j==""){
            j=0;
        }
        j++;
		for(var i=0;i<o.length;i++){
			var urlstr = "";
			try{
				var url =o[i]["url"];
				if(o[i]["nodes"] != null&&o[i]["nodes"].length>0){
					url = "";
				}
				url = url+"&WEB_ID="+webId;
				urlstr = "<li class=''><a href='"+url+"' class='J_menuItem nav_img "+o[i]["icon"]+"' target='frmright'><span class='nav-label'>"+ o[i]["name"] +"</span></a><ul class='nav nav-"+j+"-level'>";
				str += urlstr;
				if(o[i]["nodes"] != null){
					forTree(o[i]["nodes"],j);
				}
				str += "</ul></li>";
			}catch(e){}
		}
		return str;
	}

	$.ajaxSetup({
	    async : false
	});



    var excludeId = "";
    if ("ECW101"==webType)
    {
        excludeId="M1110,M1111,M1112,M1113,M1120";
    }else if ("ECW102"==webType)
    {
        excludeId="M1110,M1111,M1112,M1113";
    }else if ("ECW103"==webType)
    {
        excludeId="M1106,M1120";
    }

	$.post(menuurl, {"id":funcId,"excludeId":excludeId}, function(result){
		var myArray=new Array()
		myArray[0] = result;
		$("#side-menu").html(forTree(myArray));
		$("#side-menu").append("<span class='nav-bar'></span>");
		$("#side-menu li").each(function(index, element) {
			var $ul = $(this).find("ul:first");
			var ulContent = $ul.html();
	        if(ulContent!=null&&ulContent!=""){
	        	$(this).find("a:first").append($("<span class='fa arrow'></span>"));
	        	$(this).find("a:first").attr("href","");
				//$ul.css("padding-left","25px");
			}else{
				$ul.remove();
			}
	    });

		$("#side-menu .nav_item").click(function(){
			$("#side-menu .nav_item").removeClass("nav_active");
			$(this).addClass("nav_active");
		});

		$("#side-menu .nav_item").hover(function(){
			var top = $(this).offset().top-$("#side-menu").offset().top;
			$(".nav-bar").css("top",top);
			$(".nav-bar").css("height","40px");
			$(".nav-bar").css("width","5px");
			$(".nav-bar").css("opacity","1");

		},function(){
			$(".nav-bar").css("height","0");
			$(".nav-bar").css("opacity","0");

		});


		$("#side-menu").metisMenu();
		$("#side-menu li:first a:first").click();
		contabs.initDataId();
		$("#side-menu li:first").find("li:first a")[0].click();
	}, "json");

	$(".sidebar-collapse").slimScroll({
		height: "100%",
		railOpacity: .9,
		alwaysVisible: !1
	});
	return false;
};

function initOperateLeftNavUtil(funcId,storeId){
	$(".roll-nav").not('.J_tabExit').show();
	$("#side-menu").show();
	$("#page-wrapper .logo").hide();
	$(".navbar-default").show();
	var width = $(".navbar-default").width();
	$("#page-wrapper").css("margin-left",'');
	$(".J_menuTabs").show();


	var str = "";
	var forTree = function(o,j){
        if(j==null||j==""){
            j=0;
        }
        j++;
		for(var i=0;i<o.length;i++){
			var urlstr = "";
			try{
				var url =o[i]["url"];
				if(o[i]["nodes"] != null&&o[i]["nodes"].length>0){
					url = "";
				}
				url = url+"&STORE_ID="+storeId;
				urlstr = "<li class=''><a href='"+url+"' class='J_menuItem nav_img "+o[i]["icon"]+"' target='frmright'><span class='nav-label'>"+ o[i]["name"] +"</span></a><ul class='nav nav-"+j+"-level'>";
				str += urlstr;
				if(o[i]["nodes"] != null){
					forTree(o[i]["nodes"],j);
				}
				str += "</ul></li>";
			}catch(e){}
		}
		return str;
	}

	$.ajaxSetup({
	    async : false
	});

	$.post(menuurl, {"id":funcId}, function(result){
		var myArray=new Array()
		myArray[0] = result;
		$("#side-menu").html(forTree(myArray));
		$("#side-menu").append("<span class='nav-bar'></span>");
		$("#side-menu li").each(function(index, element) {
			var $ul = $(this).find("ul:first");
			var ulContent = $ul.html();
	        if(ulContent!=null&&ulContent!=""){
	        	$(this).find("a:first").append($("<span class='fa arrow'></span>"));
	        	$(this).find("a:first").attr("href","");
			}else{
				$ul.remove();
			}
	    });

		$("#side-menu .nav_item").click(function(){
			$("#side-menu .nav_item").removeClass("nav_active");
			$(this).addClass("nav_active");
		});

		$("#side-menu .nav_item").hover(function(){
			var top = $(this).offset().top-$("#side-menu").offset().top;
			$(".nav-bar").css("top",top);
			$(".nav-bar").css("height","40px");
			$(".nav-bar").css("width","5px");
			$(".nav-bar").css("opacity","1");

		},function(){
			$(".nav-bar").css("height","0");
			$(".nav-bar").css("opacity","0");

		});


		$("#side-menu").metisMenu();
		$("#side-menu li:first a:first").click();
		contabs.initDataId();
		$("#side-menu li:first").find("li:first a")[0].click();
	}, "json");

	$(".sidebar-collapse").slimScroll({
		height: "100%",
		railOpacity: .9,
		alwaysVisible: !1
	});
	return false;
};

function initECALeftNavUtil(funcId,webId,webType,checkFlag){
	$(".roll-nav").not('.J_tabExit').show();
	$("#side-menu").show();
	$("#page-wrapper .logo").hide();
	$(".navbar-default").show();
	var width = $(".navbar-default").width();
	$("#page-wrapper").css("margin-left",'');
	$(".J_menuTabs").show();


	var str = "";
	var forTree = function(o,j){
		if(j==null||j==""){
			j=0;
		}
        j++;
		for(var i=0;i<o.length;i++){
			var urlstr = "";
			try{
				var url =o[i]["url"];
				if(o[i]["nodes"] != null&&o[i]["nodes"].length>0){
					url = "";
				}
				url = url+"&FK_TEMP_ID="+webId;
				urlstr = "<li class=''><a href='"+url+"' class='J_menuItem nav_img "+o[i]["icon"]+"' target='frmright'><span class='nav-label'>"+ o[i]["name"] +"</span></a><ul class='nav nav-"+j+"-level'>";
				str += urlstr;
				if(o[i]["nodes"] != null){
					forTree(o[i]["nodes"],j);
				}
				str += "</ul></li>";
			}catch(e){}
		}
		return str;
	}

	$.ajaxSetup({
	    async : false
	});



    var excludeId = "";
    if ("ECW101"==webType)
    {
        excludeId="M1110,M1111,M1112,M1113,M1120";
    }else if ("ECW102"==webType)
    {
        excludeId="M1110,M1111,M1112,M1113";
    }else if ("ECW103"==webType)
    {
        excludeId="M1106,M1120";
    }

    var params = {
        "id":funcId,
		"excludeId":excludeId
	}
	if(checkFlag){
        params.checkFlag = checkFlag;
	}
	$.post(menuurl, params, function(result){
		var myArray=new Array()
		//myArray[0] = result;
		var obj = new Object();
		obj.url = "rdp/rdpMain.htm?id=SC1";
		obj.name = "销售展示分类";
		myArray[0] = obj;
		//$("#side-menu").html(forTree(myArray));
		$("#side-menu").html($("#side-menu").html());
		//$("#side-menu").append("<span class='nav-bar'></span>");
		/*
		$("#side-menu li").each(function(index, element) {
			var $ul = $(this).find("ul:first");
			var ulContent = $ul.html();
	        if(ulContent!=null&&ulContent!=""){
	        	$(this).find("a:first").append($("<span class='fa arrow'></span>"));
	        	$(this).find("a:first").attr("href","");
				//$ul.css("padding-left","25px");
			}else{
				$ul.remove();
			}
	    });
		*/
		$("#side-menu .nav_item").click(function(){
			$("#side-menu .nav_item").removeClass("nav_active");
			$(this).addClass("nav_active");
		});

		$("#side-menu .nav_item").hover(function(){
			var top = $(this).offset().top-$("#side-menu").offset().top;
			$(".nav-bar").css("top",top);
			$(".nav-bar").css("height","40px");
			$(".nav-bar").css("width","5px");
			$(".nav-bar").css("opacity","1");

		},function(){
			$(".nav-bar").css("height","0");
			$(".nav-bar").css("opacity","0");

		});


		$("#side-menu").metisMenu();
		//$("#side-menu li:first a:first").click();
		contabs.initDataId();
		//$("#side-menu li:first").find("li:first a")[0].click();
		window.open("rdp/rdpMain.htm?id=SC1&FK_TEMP_ID="+webId);
	}, "json");

	$(".sidebar-collapse").slimScroll({
		height: "100%",
		railOpacity: .9,
		alwaysVisible: !1
	});
	return false;
};

function initShowMenuLeftNavUtil(funcId,webId,webType,channelName,storeId,channelId,checkFlag){
	$(".roll-nav").not('.J_tabExit').show();
	$("#side-menu").show();
	$("#page-wrapper .logo").hide();
	$(".navbar-default").show();
	var width = $(".navbar-default").width();
	$("#page-wrapper").css("margin-left",'');
	$(".J_menuTabs").show();


	var str = "";
	var forTree = function(o,j){
        if(j==null||j==""){
            j=0;
        }
        j++;
		for(var i=0;i<o.length;i++){
			var urlstr = "";
			try{
				var url =o[i]["url"];
				if(o[i]["nodes"] != null&&o[i]["nodes"].length>0){
					url = "";
				}
				url = url+"&FK_TEMP_ID="+webId+"&FK_STORE_ID="+storeId+"&FK_CHANNEL_ID="+channelId;
				urlstr = "<li class=''><a href='"+url+"' class='J_menuItem nav_img "+o[i]["icon"]+"' target='frmright'><span class='nav-label'>"+ o[i]["name"] +"</span></a><ul class='nav nav-"+j+"-level'>";
				str += urlstr;
				if(o[i]["nodes"] != null){
					forTree(o[i]["nodes"],j);
				}
				str += "</ul></li>";
			}catch(e){}
		}
		return str;
	}

	$.ajaxSetup({
	    async : false
	});



    var excludeId = "";
    if ("ECW101"==webType)
    {
        excludeId="M1110,M1111,M1112,M1113,M1120";
    }else if ("ECW102"==webType)
    {
        excludeId="M1110,M1111,M1112,M1113";
    }else if ("ECW103"==webType)
    {
        excludeId="M1106,M1120";
    }


    var ajaxParams = {"id":funcId,"excludeId":excludeId};
	if(checkFlag){
        ajaxParams.checkFlag = checkFlag;
	}
	$.post(menuurl, ajaxParams, function(result){
		var myArray=new Array()
		//myArray[0] = result;
		var obj = new Object();
		obj.url = "rdp/rdpMain.htm?id=SC2";
		obj.name = channelName;
		myArray[0] = obj;
		//$("#side-menu").html(forTree(myArray));
		$("#side-menu").html($("#side-menu").html());
		/*
		$("#side-menu").append("<span class='nav-bar'></span>");
		$("#side-menu li").each(function(index, element) {
			var $ul = $(this).find("ul:first");
			var ulContent = $ul.html();
	        if(ulContent!=null&&ulContent!=""){
	        	$(this).find("a:first").append($("<span class='fa arrow'></span>"));
	        	$(this).find("a:first").attr("href","");
				//$ul.css("padding-left","25px");
			}else{
				$ul.remove();
			}
	    });
		*/
		$("#side-menu .nav_item").click(function(){
			$("#side-menu .nav_item").removeClass("nav_active");
			$(this).addClass("nav_active");
		});

		$("#side-menu .nav_item").hover(function(){
			var top = $(this).offset().top-$("#side-menu").offset().top;
			$(".nav-bar").css("top",top);
			$(".nav-bar").css("height","40px");
			$(".nav-bar").css("width","5px");
			$(".nav-bar").css("opacity","1");

		},function(){
			$(".nav-bar").css("height","0");
			$(".nav-bar").css("opacity","0");

		});


		$("#side-menu").metisMenu();
		//$("#side-menu li:first a:first").click();
		contabs.initDataId();
		//$("#side-menu li:first").find("li:first a")[0].click();
		window.open("rdp/rdpMain.htm?id=SC2&FK_TEMP_ID="+webId+"&FK_STORE_ID="+storeId+"&FK_CHANNEL_ID="+channelId);
	}, "json");

	$(".sidebar-collapse").slimScroll({
		height: "100%",
		railOpacity: .9,
		alwaysVisible: !1
	});
	return false;
};

function navbarTopContentWidth(){
	//获取一级导航的宽度
	var logoWidth = $(".logo").width();
	var navbarRightWidth = $(".navbar-right").width();
	var bodyWidth = $(document.body).width();
	var navbarTopContentWidth = bodyWidth-navbarRightWidth-logoWidth
	$(".navbar-top-content").css("width",navbarTopContentWidth);

}


$(window).resize(function(){
	navbarTopContentWidth();
});


$(function(){
	navbarTopContentWidth();

	initLeftNav($(".navbar-header").find("a[name=topBar]:first"));
	$(".navbar-header").find("a[name=topBar]").on("click",function(){
		$(".navbar-header").find("a[name=topBar]").removeClass("active");
		$(this).addClass("active");
		//$(".J_tabCloseAll").click();
		initLeftNav($(this));

	});
	//退出系统
	$("#exitHandler").click(function(){
		layer.confirm('确定要退出系统吗', {
			icon: 3, title:false
			}, function(){
				$.ajax({
					url:ctx+"/logout.htm",
					type:"post",
					success:function(){
						top.window.location.href = ctx+"/index.htm";
					}
				});
			}, function(){

			});
	});


	$(".fa-align-justify").click(function(){
		if($("body").hasClass("mini-navbar")){
			$("body").removeClass("mini-navbar");
		}else{
			$("body").addClass("mini-navbar");
		}

	});


});
