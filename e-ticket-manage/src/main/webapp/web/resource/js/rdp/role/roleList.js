$(function() {
	var w = $.dialog;
	$("#addRole").live("click", function() {
		$.dialog.openWin({
			URL : ctx + "/jsp/rdp/role/role.jsp",
			Width : 800,
			Height : 570
		});
	});
	//查询
	$("#QueryBtn").click(function(){
		refresh();
	});
	$("#delBatch").live("click", function() {
		var len = $.rzGrid.getCheckedHandler().length;
		msg = "确定要删除选中的数据吗？";
		if (0 == len) {
			w.winAlert("至少选择一条数据", w.closeWin());
		} else {
			var items = $.rzGrid.getCheckedHandler();
			w.winConfirmAjax({
				message : msg,
				URL : ctx + "/role/delOneOrMoreRoleInfo.htm",
				data : {
					ids : items.join(';')
				},
				success:function(dataResponse){
					$.dialog.winAlert(parseErrorCode(dataResponse.code,dataResponse.type,dataResponse.data), function(){
						refresh();
						$.dialog.closeWin();
					});
				}
			});
		}
	});

	// 删除单一
	$(".deleteOne").live("click", function() {
		var msg = "确定要删除选中的数据吗？";
		var items = new Array();
		var id = $(this).attr("fid");
		items.push(id);
		w.winConfirmAjax({
			message : msg,
			URL : ctx + "/role/delOneOrMoreRoleInfo.htm",
			data : {
				ids : items.join(';')
			},
			success:function(dataResponse){
				$.dialog.winAlert(parseErrorCode(dataResponse.code,dataResponse.type,dataResponse.data), function(){
					refresh();
					$.dialog.closeWin();
				});
			}
		});
	});
	$(".modifyOne").live("click", function() {
		// 修改单一
		var id = $(this).attr("fid");
		w.openWin({
			URL : ctx + "/role/toModifyPage.htm?id=" + id,
			Width : 800,
			Height : 570,
			success:function(dataResponse){
				$.dialog.winAlert(parseErrorCode(dataResponse.code,dataResponse.type,dataResponse.data), function(){
					refresh();
					$.dialog.closeWin();
				});
			}
		});
	});
	
	$.rzGrid.initGrid({
		tableName:"dataBasic",
		fileds:["roleId","roleName"],
		columns: [
	      {display: '操作', isAllowHide: false, align: 'center', width: "10%",
	          render: function (rowdata, rowindex, value, column){
	          return  $("#renderHtml").html().replace(new RegExp("\\{0\\}","g"), rowdata.roleId);
	          }
	      },
	      { display: '角色名称', name: 'roleName',  align: 'center' , width: "25%"},
	      { display: '角色描述', name: 'roleDesc',  align: 'center', width: "25%" }
	   ], 
	   data:[]
	}); 
	refresh();
});
//刷新
function refresh(){
	$.rzGrid.updateGridByUrl({
		url:ctx+"/role/getJsonRoleInfo.htm",
		params:{"sortName":$("#sort").val(),
	    "userName":$("#userName").val(),
	    "email":$("#email").val(),
	    "pageIndex":$("#pageIndex").val(),
	    "pageSize":$("#pageSize").val()
	   }
	});
};


