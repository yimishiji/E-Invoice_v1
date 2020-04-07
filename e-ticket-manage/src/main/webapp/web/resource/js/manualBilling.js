var data={};
var buyer={};
var sales={};
var check={};
var invoiceInfo={};

function addpro(){
	var length = $(".tqm li").length;
	if(length>=5){
		alert("最多只能输入5个提取码");
		return;
	}

	var lastChild = $("#tqm li:last-child").find("input");
	//判断是否输入值 或者是否获取交易数据
	if(lastChild.prop('disabled')){
		$(".tqm").append("<li><input class='fr pn' onkeyup='isPhone();'/></li>");
	}else{
		if(lastChild.val()==""){
			alert("请先输入一个提取码");
		}else{
			alert("请获取当前提取码的交易信息");
		}
	}

	function verifyPhone(){
		alert(1111)
		var phoneNumber = $(".vf").val();
		phoneNumber = phoneNumber.replace(/\s+/g,"");
		$(".vf").val(phoneNumber);
	}
	
}

function isPhone(){
	var phoneNumber = $(".pn").val();
	phoneNumber = phoneNumber.replace(/\s+/g,"");
	$(".pn").val(phoneNumber);
}

function delpro(){
	var lastChild = $("#tqm li:last-child").find("input");
	//遍历已经输入的提取码 判断是否有相同
	var pickupCode = lastChild.val();
	console.log(pickupCode)
	if(pickupCode!=null&&pickupCode!=""){
		var orderId = $("#orderId").val();
		$.ajax({
			type : "post",
			datatype : "json",
			data:{pickupCode:pickupCode,orderId:orderId,isAdd:1},
			url :ctx+"/manualInvoice/getBillingInfo.htm",
			async : true,
			success : function(data) {
				data = $.parseJSON(data);
				if(data.suc){
					if(data.data!=null){
						$("#salesId").val(data.data.TAXPAYER_ID);
						$("#salesnum").val(data.data.TAXPAYER_IDENTIFY_NO);// 纳税人识别号
						$("#salesname").val(data.data.TAXPAYER_NAME_CN);//纳税人名称
						$("#salesaddress").val(data.data.TAXPAYER_ADDRESS+" "+data.data.TAXPAYER_PHONE);// 纳税人地址 纳税人电话
						$("#salesbank").val(data.data.TAXPAYER_BANK+" "+data.data.TAXPAYER_ACCOUNT);// // 纳税人帐号 + 纳税人开户行
						$("#drawer").val(data.data.ISSUE);
						$("#check").val(data.data.FHR);
						$("#payee").val(data.data.SKY);
						$(".product li").remove();
						for(var i=0;i<data.data.transList.length;i++){
						   var tran=data.data.transList[i];
                           var number=i+1;
                           $("#pickupcpode").append('<li class="pro_'+number+'"><input value="'+tran.pickupcpode+'" readonly="readonly" style="border-style: solid; border-width: 0"/></li>');
                           $("#transactionnum").append('<li class="pro_'+number+'"><input value="'+tran.transactionnum+'" readonly="readonly" style="border-style: solid; border-width: 0;text-align:center;"/></li>');
                           $("#amount").append('<li class="pro_'+number+'"><input value="'+tran.amount+'" readonly="readonly" style="border-style: solid; border-width: 0;text-align:center;"/></li>');
						}
						$("#total_amout")[0].innerHTML= data.data.total_amout;
					}else{
						$("#salesnum").val("");// 纳税人识别号
						$("#salesname").val("");//纳税人名称
						$("#salesaddress").val("");// 纳税人地址 纳税人电话
						$("#salesbank").val("");// // 纳税人帐号 + 纳税人开户行
						$("#drawer").val("");// // 纳税人帐号 + 纳税人开户行
						$(".product li").remove();
						$("#total_amout_no_tax")[0].innerHTML= ""; 
						$("#total_amount_upcase")[0].innerHTML= "";  
						$("#total_amount_lowcase")[0].innerHTML= ""; 
					}
					if($("#tqm li").find("input").length!=1) {
						$("#tqm li:last-child").find("input").attr("disabled", "disabled");
					}
					alert("删除成功");
				}
			},
			error : function(){
				
			}
		})
	}
	

	if($(".tqm li").length>1){
		$(".tqm li:last-child").remove();
	}else{
		$(".tqm li:last-child").find("input").val("");
		$("#tqm li:last-child").find("input").removeAttr("disabled");
	}
}

//提交开票
function  submitinvoice(){
		$("#loading").show();
	 	//名称:  邮箱: 手机号:  纳税人识别号:  地址、电话:  开户行及账号:
		var buyername=$("#buyername").val();
		var buyermail=$("#buyermail").val();
		var buyerphone=$("#buyerphone").val();
		var buyernum=$("#buyernum").val();
		var buyeraddress=$("#buyeraddress").val();
		var buyerbank=$("#buyerbank").val();
		 //名称:  纳税人识别号:  地址、电话: 开户行及账号:
		var salesname=$("#salesname").val();
		var salesnum=$("#salesnum").val();
	    var salesId=$("#salesId").val();//纳税人Id
		var salesaddress=$("#salesaddress").val();
		var salesbank=$("#salesbank").val();
		var remark=$("#remark").val();//备注
		//收款人: 复核: 开票人:
		var payee=$("#payee").val();
		var check=$("#check").val();
		var drawer=$("#drawer").val();

	    //开票类型
	    var invoicetype = $("#invoicetype").val();

		var isinput=true;
		if(!isinput){
			$("#loading").hide();
			alert("请输入提取码");
			return;
		}
		if($.trim(buyername)==""){
			$("#loading").hide();
			alert("请输入购买方名称");
			$("#buyername").focus();
			return;
		}
		if($.trim(buyermail)==""){
			$("#loading").hide();
			alert("请输入购买方邮箱");
			$("#buyermail").focus();
			return;
		}

		if(!(/^1[34578]\d{9}$/.test(buyerphone))){
			$("#loading").hide();
			alert("购买方手机号错误");
			$("#buyerphone").focus();
			return;
		}
		if(!buyermail.match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/)){
			$("#loading").hide();
			alert("购买方邮箱错误");
			$("#buyermail").focus();
			return;
		}
		
		var lastChild = $("#tqm li:last-child").find("input");
		//判断是否输入值 或者是否获取交易数据
		if(!lastChild.prop('disabled')){
			$("#loading").hide();
			alert("请获取交易数据");
			return;
		}

		if($.trim(salesname)==""){
			$("#loading").hide();
			alert("销售方名称错误");
			$("#salesname").focus();
			return;
		}
		if($.trim(salesnum)==""){
			$("#loading").hide();
			alert("销售方纳税人识别号错误");
			$("#salesnum").focus();
			return;
		}
		if($.trim(salesaddress)==""){
			$("#loading").hide();
			alert("销售方地址、电话错误");
			$("#salesaddress").focus();
			return;
		}
		if($.trim(salesbank)==""){
			$("#loading").hide();
			alert("销售方开户银行错误");
			$("#salesbank").focus();
			return;
		}
		var num=$(".product li:last-child").attr("class");
		if(num==null){
			$("#loading").hide();
			alert("商品信息错误");
			return;
		}
		num=num.split("_")[1];
		if (num<1){
			$("#loading").hide();
			alert("商品信息错误");
			return;
		}else{
			num++;
		}
		var orderId = $("#orderId").val();
		data.orderId = orderId;
		var codelist = new Array();
		$.each($("#tqm li"),function(){
			var inputval=$(this).find("input").val();
			if($.trim(inputval)==""||inputval==null){
				isinput=false;
				$("#loading").hide();
				return;
			}else{
				codelist[codelist.length]=inputval;
			}
		});
		// buyername  buyermail  buyerphone buyernum  buyeraddress buyerbank 购买方信息
		data.buyer={buyername:buyername,buyermail:buyermail,buyerphone:buyerphone,buyernum:buyernum,buyeraddress:buyeraddress,buyerbank:buyerbank,invoicetype:invoicetype};
		//salesname salesnum salesaddress salesbank remark 销售方信息
		data.sales={salesId:salesId,salesname:salesname,salesnum:salesnum,salesaddress:salesaddress,salesbank:salesbank,remark:remark};
		//payee check drawer 开票人信息
		data.check={payee:payee,check:check,drawer:drawer};
		//拼多个提取码
		data.codelist={codelist:codelist};
		$.ajax({
			type : "post",
			datatype : "json",
			data:{data:JSON.stringify(data)},
			url :ctx+"/manualInvoice/submitManualBilling.htm",
			async : true,
			success : function(data) {
				$("#loading").hide();
				alert("提交成功!");
				aoRefresh();
			},
			error : function(){
				$("#loading").hide();
			}
		})
	}

function aoRefresh(){		
	setTimeout(
		function(){
			//刷新报表
			parent.refreshComponentDisplay("invoiceIssue","report1",false);
			art.dialog.close();
		},1*1000);
		
	 }


	 //获取提取码对应的交易信息
	function getBillingInfo(){
		$("#loading").show();
		var lastChild = $("#tqm li:last-child").find("input");
		if(lastChild.prop('disabled')){
			alert("请输入提取码");
			$("#loading").hide();
			return;
		}
		//遍历已经输入的提取码 判断是否有相同
		var pickupCode = lastChild.val();
		var len = $("#tqm li").length;
		var errorFlag = true;
		$.each($("#tqm li"),function(i,m){
			if(i != len-1){
				if($(this).find("input").val()==pickupCode){
					errorFlag = false;
					alert("请勿输入相同的提取码");
				}
			}
		});
		if(pickupCode==""||typeof(pickupCode) == "undefined"){
			alert("请输入提取码");
			errorFlag = false;
		}
		if(!errorFlag){
			$("#loading").hide();
			return;
		}
		var orderId = $("#orderId").val();
		$.ajax({
			type : "post",
			datatype : "json",
			data:{pickupCode:pickupCode,orderId:orderId,isAdd:0},
			url :ctx+"/manualInvoice/getBillingInfo.htm",
			async : true,
			success : function(data) {
				data = $.parseJSON(data);
				if(data.suc){
					$("#salesId").val(data.data.TAXPAYER_ID);
					$("#salesnum").val(data.data.TAXPAYER_IDENTIFY_NO);// 纳税人识别号
					$("#salesname").val(data.data.TAXPAYER_NAME_CN);//纳税人名称
					$("#salesaddress").val(data.data.TAXPAYER_ADDRESS+" "+data.data.TAXPAYER_PHONE);// 纳税人地址 纳税人电话
					$("#salesbank").val(data.data.TAXPAYER_BANK+" "+data.data.TAXPAYER_ACCOUNT);// // 纳税人帐号 + 纳税人开户行
					$("#drawer").val(data.data.ISSUE);
					$("#check").val(data.data.FHR);
					$("#payee").val(data.data.SKY);
					$(".product li").remove();
					for(var i=0;i<data.data.transList.length;i++){
						var tran=data.data.transList[i];
						var number=i+1;
						$("#pickupcpode").append('<li class="pro_'+number+'"><input value="'+tran.pickupcpode+'" readonly="readonly" style="border-style: solid; border-width: 0"/></li>');
						$("#transactionnum").append('<li class="pro_'+number+'"><input value="'+tran.transactionnum+'" readonly="readonly" style="border-style: solid; border-width: 0;text-align:center;"/></li>');
						$("#amount").append('<li class="pro_'+number+'"><input value="'+tran.amount+'" readonly="readonly" style="border-style: solid; border-width: 0;text-align:center;"/></li>');
					}
					$("#total_amout")[0].innerHTML= data.data.total_amout;
						   
					$("#tqm li:last-child").find("input").attr("disabled","disabled");
					alert("获取成功");
					$("#loading").hide();
				}else{
					pageIntent(data);
					$("#loading").hide();
				}
			},
			error : function(){
				$("#loading").hide();
			}
		})
	}
	
	function pageIntent(returnMap){
		if(returnMap.msg=="errorInvoiceStatus"){
			alert("当前提取码已开发票");
		}else if(returnMap.msg=="noPosData"){
			alert("未获取到交易数据");
		}else if(returnMap.msg=="pickCodeEmpty"){
			alert("提取码错误");
		}else if(returnMap.msg=="errorPosData"){
			alert("当前提取码不可开票");
		}else if(returnMap.msg=="inequalityNsrsbh"){
			alert("当前交易数据不在同一个纳税识别号下");
		}else{
			alert(returnMap.msg);
		}
	}