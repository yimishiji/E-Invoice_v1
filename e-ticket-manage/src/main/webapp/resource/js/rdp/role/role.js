$(function(){
	
	$("#closeWin").click(function(){
		$.dialog.closeWin();
	});
	
	$("#add_role").click(function(){
		$("#permissons").val($.mytree.getSelectValue());
		$.form.ajaxForm({
			form:$("#edit_form"),
			url:ctx +"/role/addOneRoleInfo.htm",
			success:function(data){
				var dataResponse = eval('('+data+')');
				$.dialog.winAlert(parseErrorCode(dataResponse.code,dataResponse.type,dataResponse.data), function(){
					top.frmright.window.refresh();
					$.dialog.closeWin();
				});
			}
		});
	});
	
	$.mytree.initTree({
		treeName:"tree-1",
		url:ctx+"/common/getMenuTreeBySysId.htm",
		data:{id:"M0000"},
		leftmenu:true
	});
});