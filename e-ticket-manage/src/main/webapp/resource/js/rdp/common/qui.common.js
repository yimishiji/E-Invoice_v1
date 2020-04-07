var fixObjHeight=172;
var pageIndex = 1 ;
var pageSize = 10 ;
//初始化函数 QUI自带
function initComplete(){
	$("#searchPanel").bind("stateChange",function(e,state){
		if(state=="hide"){
			fixObjHeight=126;
		}
		else if(state=="show"){
			fixObjHeight=172;
		}
		triggerCustomHeightSet();
	});
	
//	$("#pageContent").bind("pageChange",function(e,index){
//		pageIndex = index + 1;
//        $("#pageIndex").val(pageIndex);
//        $("#pageSize").val(Number($(this).attr("pageSize")));
//        $("#submit_form").submit();    
//    });
//	$("#pageContent").bind("sizeChange",function(e,num){
//        pageSize = num;
//        $("#pageSize").val(pageSize);
//        $("#submit_form").submit();        
//    });
	
//	$("#QueryBtn").click(function(){
//		$("#submit_form").submit();
//	});
	
	$("#startTime").focus(function(){
		var endtimeTf=$dp.$('endTime');
        WdatePicker({
            skin:"blue",onpicked:function(){endtimeTf.focus();},maxDate:'#F{$dp.$D(\'endTime\')}'
        });
	});
	$("#endTime").focus(function(){
		//这里设置了最大日期为当前日期，如果不需要则把maxDate:'%y-%M-%d'去掉
	    WdatePicker({skin:"blue",minDate:'#F{$dp.$D(\'startTime\')}'});
	});
}

//内容页面高度处理
function customHeightSet(contentHeight){
	$("#scrollContent").height(contentHeight-fixObjHeight);
}

/**
 * 设置页面高度
 * @param height
 */
function setFixObjHeight(h1,h2){
	$(function(){
		$("#searchPanel").bind("stateChange",function(e,state){
			if(state=="hide"){
				fixObjHeight=h1;
			}
			else if(state=="show"){
				fixObjHeight=h2?h1:h2;
			}
			triggerCustomHeightSet();
		});
		
	});
}
