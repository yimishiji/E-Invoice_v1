$(function(){

	
	//退出系统
	$("#exitHandler").click(function(){
		if(confirm("确定要退出系统吗")){
//			function(){
				$.ajax({
					url:ctxp+"/logout.htm",
					type:"post",
					success:function(){
						top.window.location.href = ctxp+"/index.htm";
					}
				});			
		}
	});
	
	//修改密码
	$("#modifyPwd").click(function(){
		$(self.parent.window.document).find("#mainframe").attr("src" ,ctxp+"/main/updatePwd.htm");
	});
});

function bookmarksite(title, url){
    if (window.sidebar) // firefox
        window.sidebar.addPanel(title, url, "");
    else 
        if (window.opera && window.print) { // opera
            var elem = document.createElement('a');
            elem.setAttribute('href', url);
            elem.setAttribute('title', title);
            elem.setAttribute('rel', 'sidebar');
            elem.click();
        }
        else 
            if (document.all)// ie
                window.external.AddFavorite(url, title);
}




function backMenuIndex(frameId){
	var frame = $("#"+frameId);
	if(frame){
		$(window.parent.document).find("#"+frameId).attr("src","ShowReport.wx?PAGEID=taskQuery&r="+Math.random()); 
	}else{
		window.open("ShowReport.wx?PAGEID=taskQuery&r="+Math.random(),"_blank");
	}
}

//验证两次输入的新密码是否一样
function updatePwds(doc){

	var index = doc.id.lastIndexOf('wxcol_');
	var id = doc.id.substring(0,index)+"wxcol_";
	
	var pwds = document.getElementById(id+"password");
	var pwd2 = document.getElementById(id+"restPassword");
	
	if(pwds.value != pwd2.value){
		wx_warn("两次输入的新密码不一样!");
		return;
	}
	
	
}