//初始化fileinput
var FileInput = function () {
    var oFile = new Object();

    //初始化fileinput控件（第一次初始化）
    oFile.Init = function(ctrlName, uploadUrl) {
    var control = $('#' + ctrlName);

    //初始化上传控件的样式
    control.fileinput({
        language: 'zh', //设置语言
        uploadUrl: uploadUrl, //上传的地址
        allowedFileExtensions: ['jpg', 'gif', 'png'],//接收的文件后缀
        showUpload: true, //是否显示上传按钮
        showCaption: false,//是否显示标题
        browseClass: "btn btn-primary", //按钮样式     
        //dropZoneEnabled: false,//是否显示拖拽区域
        //minImageWidth: 50, //图片的最小宽度
        //minImageHeight: 50,//图片的最小高度
        //maxImageWidth: 50,//图片的最大宽度
        //maxImageHeight: 50,//图片的最大高度
        //maxFileSize: 0,//单位为kb，如果为0表示不限制文件大小
        //minFileCount: 0,
        maxFileCount: 10, //表示允许同时上传的最大文件个数
        enctype: 'multipart/form-data',
        validateInitialCount:true,
        previewFileIcon: "<i class='glyphicon glyphicon-king'></i>",
        msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
    });

    //导入文件上传完成之后的事件
    $("#txt_file").on("fileuploaded", function (event, data, previewId, index) {
        $("#myModal").modal("hide");
        var data = data.response.lstOrderImport;
        if (data == undefined) {
            toastr.error('文件格式类型不正确');
            return;
        }
        //1.初始化表格
        var oTable = new TableInit();
        oTable.Init(data);
        $("#div_startimport").show();
    });
}
    return oFile;
};


var RongzerInit = function(){
	function init(){
		//添加tip描述属性 ========Start
		$(".cls-data-content-list").each(function(){
			if(!$(this).find(".fresco")[0]){
				$(this).attr("data-toggle","tooltip");
			}
			
		});
		//赋值title
		$(".cls-data-content-list").each(function(){
			$(this).attr("title",$(this).text());
		});
		$("[data-toggle='tooltip']").tooltip();
		//添加tip描述属性 ========End
		
		
		//添加图片鼠标移上显示缩略图效果========Start
		$(".fresco").each(function(){
			var $parentDiv = $(this).closest(".cls-data-content-list");
			$parentDiv.tooltip({html : true });
			$parentDiv.attr("data-original-title",$(this).attr("data-title"));
			
		});
		//添加图片鼠标移上显示缩略图效果========End

		
		//文字超长自动截取========Start
		//$(".cls-data-td-list .cls-data-content-list").wordLimit();
		//文字超长自动截取========End
		
		
		
		
		
	    //初始化fileinput
	    var oFileInput = new FileInput();
	    oFileInput.Init("txt_file", "ShowReport.wx");

	    
	    
	    
	    //选项框效果
	    $("input[type='checkbox']").on('ifClicked', function(event){ //ifCreated 事件应该在插件初始化之前绑定 
/*			  var name = $(this).attr("name");
			  //修改当前checkbox选项框checked状态
			  if($(this).is(':checked')){
				  $(this).prop("checked",false);
			  }else{
				  $(this).prop("checked",true);
			  }*/
			  
			  //var _arr_events= $._data($(this)[0], 'events').click;
			  $(this).trigger("click");
			  
			  
/*			  if(name!=null){
				  if(name.lastIndexOf("col")>0){
					  doSelectedDataRowChkRadio(this);
				  }else{
					  //全选按钮
					  doSelectedAllDataRowChkRadio(this);
				  }
			  }*/
			});
	    
	    
	    //选项框效果
	    $("input[type='radio']").on('ifClicked', function(event){ //ifCreated 事件应该在插件初始化之前绑定 
	    	$(this).trigger("click");
	    	/*doSelectedDataRowChkRadio(this);*/
			});

		  $('input').on('ifDisabled', function(event){
			  $(this).iCheck('disable');
			});
		  
		  $('input').on('ifEnabled', function(event){
			  $(this).iCheck('enable');
			});
	  $('input').iCheck({
	    checkboxClass: 'icheckbox_square-blue',
	    radioClass: 'iradio_square-blue',
	    increaseArea: '20%' // optional
	  });
	    
	  
/*	  //选择框效果
	  $(".cls-selectbox-normal").chosen();
	  $(".cls-data-td-editdetail").find(".cls-selectbox-normal").chosen().change(function(){
		  var $table = $(this).closest("table");
		  if($table[0]){
			  var tableId = $table.attr("id").replace("_data","");
			  wx_onblurValidate(tableId,this,false,false,null);
			  addInputboxDataForSaving(tableId,this)
		  }
		 });*/

	}
	
	
	
	return {
		init:function() {init();}
	};
}

$(function () {
	var rongzerInit = new RongzerInit();
	rongzerInit.init();
});

