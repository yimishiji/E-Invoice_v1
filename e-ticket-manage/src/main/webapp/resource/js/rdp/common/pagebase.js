var pageBase=function(){
	   function initPath(ctx,resourceUrl) {
		    this.ctx = ctx;
		    this.resourceUrl = resourceUrl;
		}
		return {
		    initPath : initPath
		};
}();

function base_ajax(a_data,a_url,a_async,a_type,a_callback){
	 var a_callback;
	 $.ajax({
			type : a_type,
			dataType : "json",
			async : a_async,
			data : a_data,                                        //json数据格式
			url : a_url,
			beforeSend : function(XMLHttpRequest) {
			},
			success : function(data,textStatus) {
					  a_callback(data);				
			},
			error : function(XMLHttpRequest){
			          alert("status:"+XMLHttpRequest.status+"  "+"readyState:"+XMLHttpRequest.readyState+"\n"+property.server_message);  //返回错误信息	  
			},
			complete : function(){
			}
	});	
}



