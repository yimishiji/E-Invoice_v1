$(function() {
//	var win = $.dialog;
	$("#closeWin").click(function() {
//		win.closeWin();
		window.close();
	});

	$("#save1").click(function() {
		var op = $("#oldPwd").val(),
			np = $("#newPwd").val(),
			rnp = $("#reNewPwd").val();
//		if(null == op || "" == op){
//			
//		}
//		$("#submitForm").sumbit();
		$.ajax({
			type : "post",
			url : ctx + "/common/updatepwd.htm",
			async : false,
			dataType : "text",
			data : {
				oldPwd : op ,
				newPwd : np,
				reNewPwd:rnp
			},
			success : function(responsedata) {
				var msg = "";
				switch (Number(responsedata)) {
					case 0:
						msg = "修改成功";
						break;
					case 1:
						msg = "修改失败";
						break;
					case 2:
						msg = "旧密码不能为空";
						break;
					case 3:
						msg = "新密码不能为空";
						break;
					case 4:
						msg = "确认密码不能为空";
						break;
					case 5:
						msg = "旧密码与原密码不一致";
						break;
					case 6:
						msg = "新密码与确定新密码不一致";
						break;
				}
				alert(msg);
				$("#oldPwd").val(""),
				$("#newPwd").val(""),
				$("#reNewPwd").val("");
			}
		});
		
		return false;

	});
});