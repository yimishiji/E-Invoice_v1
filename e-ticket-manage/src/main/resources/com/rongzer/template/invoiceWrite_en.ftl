<@navigate id="index/invoiceIndex_en" var="invoiceIndex_en"/>
<@navigate id="index/invoiceFail_en" var="invoiceFail_en"/>
<@navigate id="index/invoiceSuccess_en" var="invoiceSuccess_en"/>
<@business id="billingService" var="fapiaoInfo" method="getMergeInfo" cachetime="0"/>
<#if fapiaoInfo.SUC == true>
	<!doctype html>
	<@navigate id="index/invoiceEnd_en" var="invoiceEnd_en"/>
	<@navigate id="index/invoiceTradeList_en" var="tradeList_en"/>
	<html>
	    <head>
	        <@template id="invoiceCommonHeadTop" />
	        <title>E-Invoice</title>
	    </head>
	    <body class="lang_cn body_grey" onload="time()">
	        <@template id="commonBodyTop" />
		    <div id="app">
		    	<div class="grey" >
		    	<div id="headr" >
		    	<div class="header_container">
		    	   <div class="minh">E-Invoice</div>
		    	   <div class="maxh">HG E-Invoice</div>
		    	   <div id="yearday">
		    	   </div>
		    	   </div>				
				</div>
				<!-- 主要区域 -->
	    	    <section id="main" class="main invoiceWrite">
		    	    	<div>
		    	    		<#assign orderMap=fapiaoInfo.orderMap>
		    	    		<#assign tradetype=orderMap.INVOICE_TRADE_TYPE>
		    	    		<#assign isCard=orderMap.ISCARD>
		    	    		<#assign invoice_detail_type=orderMap.INVOICE_DETAIL_TYPE>
		    	    		<#assign invoice_write_type=orderMap.INVOICE_WRITE_TYPE>
							<input type="hidden" id="invoiceStatus" value="${orderMap.INVOICE_STATUS}">
							<input type="hidden" id="orderReadOnly" value="${fapiaoInfo.READONLY}">
		    	    		
		    	    		<div class="content">
			<div class="invoice_write_container">
				<div class="invoice_amount">
					<b class="label_title">Amount</b>
					<b class="label_value_en_i" id="kkpje" style="width:60%;"><i>${orderMap.TOTAL_AMOUNT}</i></b>
				</div>
				<div class="invoice_type_selecter_container">
					<div class="label_title">Type</div>
					<div id="invoiceType1" class="invoiceType invoiceType1 <#if invoice_detail_type == 'E00903'>invoiceTypeSelected</#if> " data-invoice="E00903">
			    	   <#if invoice_detail_type == 'E00903'>
				    	   <img class="icon_detail" src="<@resource path="images/icon_details01.svg"/>">
			    	   <#else>
				    	   <img class="icon_detail" src="<@resource path="images/icon_details02.svg"/>">
			    	   </#if>
			    	       Details
			    <!--	  <img id="jiaobiao" class="jiaobiao1" style="width:15px;height:14px" src="<@resource path="images/icon_jiaoweiji.svg"/>">  -->
			    	</div>
					<div id="invoiceType2" class="invoiceType invoiceType2_en <#if invoice_detail_type == 'E00902'>invoiceTypeSelected</#if> " data-invoice="E00902">
			    	    <#if invoice_detail_type == 'E00902'>
				    	  <img class="icon_detail" src="<@resource path="images/icon_classification02.svg"/>">
			    	    <#else>
				    	   <img class="icon_detail" src="<@resource path="images/icon_classification01.svg"/>">
			    	    </#if>
			    	    	Summary
			    <!--	   <img id="jiaobiao" class="jiaobiao1" style="width:15px;height:14px" src="<@resource path="images/icon_jiaoji.svg"/>">  -->
			    	</div>
				</div>
				<div class="invoice_detail_container">
					<div class="invoice_detail_line_none">
						<div class="label_title">Type</div> 
						<div id="invoiceTypeCompany" class="invoiceTypeIT invoiceType4 invoiceTypeC<#if invoice_write_type == 'E00802'>selected</#if>" data-invoice="E00802">	
				    	    <img class="imgnone invoiceTypeIT" id="imgnone"  src="<@resource path="images/icon_weixuanze.svg"/>">
				    	    <div class="blank40" id="blank40">Company</div>
				     <!--	    <img id="jiaobiao" class="jiaobiao2" style="width:15px;height:14px" src="<@resource path="images/icon_jiaoweiji.svg"/>">  -->
				    	 </div>
						<div id="invoiceTypePerson" class="invoiceTypeIT invoiceType4 invoiceTypeP <#if invoice_write_type == 'E00801'>selected</#if> "  data-invoice="E00801">			    	    						
				    	    	<img class="imgnone invoiceTypeIT blankIT" id="imgnone"  src="<@resource path="images/icon_yixuanze.svg"/>">
				    	    	<div class="blank42" id ="blank42">Personal</div>
				     <!--	    	<img id="jiaobiao" class="jiaobiao2" style="width:15px;height:14px" src="<@resource path="images/icon_jiaoji.svg"/>">  -->
						</div>
						</div>
				
					<div id="company" style="display: none;"<#if invoice_write_type == 'E00802'>show</#if>">
					    <div class="invoice_detail_line bitian" style="font-size:1em;color:#333333;background-color:#f2f2f2;padding-left:4.5%;width:100%;height:30px;line-height:30px;margin-left: 0;border-bottom: 1px solid #ffffff;">						
								The following is required							
						</div>
						<div class="invoice_detail_line" id="p_title2">
					 <!--		<div class="label_title_en">Name of Company</div>  -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Name of Company (Required)" class="input_text" id="c_title" autocomplete="no" maxlength="" onkeyup="isConfirmC(this)"/>
							</div>
						</div>
						<div class="invoice_detail_line" id="c_no2">
					 <!--		<div class="label_title_en">Taxpayer No</div>   -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Taxpayer No. (Required)" class="input_text" id="c_no" autocomplete="no" maxlength="" onkeyup="isConfirmC(this)"/>
							</div>
						</div>
						<div class="invoice_detail_line" id="c_pho2">
					 <!--		<div class="label_title_en">Mobile Number</div> -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Mobile Number (Required)" class="input_text" id="c_pho" autocomplete="no" maxlength="" onkeyup="isConfirmC(this)"/>
							</div>
						</div>
						<div class="invoice_detail_line" id="c_mail2">
					 <!--		<div class="label_title_en">Email Address</div>   -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Email Address (Required)" class="input_text" id="c_mail" autocomplete="no" maxlength="" onkeyup="isConfirmC(this)"/>
							</div>
						</div>
						<div class="invoice_detail_line bitian" style="font-size:1em;color:#333333;background-color:#f2f2f2;padding-left:4.5%;width:100%;height:30px;line-height:30px;margin-left: 0;border-bottom: 1px solid #ffffff;">						
								The following is optional							
						</div>
						<div class="invoice_detail_line" id="c_bank2">
					 <!--		<div class="label_title_en">Bank Name</div>  -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Bank Name" class="input_text" id="c_bank" autocomplete="no" maxlength=""/>
							</div>
						</div>
						<div class="invoice_detail_line" id="c_bankNo2">
					<!--	<div class="label_title_en">Bank Account</div>  -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Bank Account" class="input_text" id="c_bankNo" autocomplete="no" maxlength=""/>
							</div>
						</div>
						<div class="invoice_detail_line" id="c_address2">
					 <!--		<div class="label_title_en">Address</div>   -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Address" class="input_text" id="c_address" autocomplete="no" maxlength=""/>
							</div>
						</div>
						<div class="invoice_detail_line" id="c_phone2">
					 <!--		<div class="label_title_en">Tel</div>  -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Tel" class="input_text" id="c_phone" autocomplete="no" maxlength=""/>
							</div>
						</div>
					</div>
					<div id="personal" <#if invoice_write_type == 'E00801'>show</#if>">
						<div class="invoice_detail_line" id="p_title1">
					 <!--		<div class="label_title_en">Personal/Government Name</div>   -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Personal Name(Required)" class="input_text" id="p_title" autocomplete="no" maxlength="" onkeyup="isConfirmP(this)"/>
							</div>
						</div>
						<div class="invoice_detail_line" id="p_pho1">
					 <!--		<div class="label_title_en">Mobile Number</div>  -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Mobile Number (Required)" class="input_text" id="p_pho" autocomplete="no" maxlength="" onkeyup="isConfirmP(this)"/>
							</div>
						</div>
						<div class="invoice_detail_line" id="p_mail1">
					 <!--		<div class="label_title_en">Email Address</div>  -->
							<div class="label_value">
								<input type="text" placeholder="Please Enter Email Address (Required)" class="input_text" id="p_mail" autocomplete="no" maxlength="" onkeyup="isConfirmP(this)"/>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="invoice_submit_big">
		  <div class="invoice_submit" id="button_confirm" style="cursor:pointer;">Confirm</div>		    	    		
		</div>
	  </div>
		</section>
		<!-- load -->
		<div class="down"></div>
		<div class="tip" id="tip">
			<ul>
			</ul>
			<p class="three_p"></p>
		 </div>
		<div class="invoice_down tc confirm" style="">
			   <ul class="ul_li">
			    </ul>
			<div class="all_btn_en">
			    	 <div class="button btn_return fl ">Cancel</div>
			    	  <div class="button btn_confirm fr">Confirm</div>
			</div>
     </div>

					<@template id="commonLoadAlert" />
		    	    <@template id="invoiceLoadAlert" />
		    	</div>
		    </div>
	    </body>
	    <script type="text/javascript">
	    var handler = function () {
		        event.preventDefault();
		        event.stopPropagation();
            };
            
	    function time(){
		  var Week = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];  
	      var now= new Date();	
		  var year=now.getFullYear();
		  var Month=['January','February','March','April','May','June','July','August','September','October','November','December'];	
		  var month=Month[now.getMonth()];	
		  var date=now.getDate();
		  var week=Week[now.getDay()];	
		  document.getElementById("yearday").innerHTML=month+"&nbsp;"+date+",&nbsp;&nbsp;"+year+"&nbsp;&nbsp;&nbsp;"+week;
       }
    
    $("input").blur(function(){
        document.getElementById("button_confirm").style.background="#cccccc";
        var invoiceType = $(".invoiceType4.selected").attr("data-invoice");
	    $(".confirm ul li").remove();
	    if("E00801" == invoiceType){
				var c_title= $("#p_title").val();
				var p_pho= $("#p_pho").val();
				var mailAccount= $("#p_mail").val();
				if(c_title!=""&&p_pho != null && p_pho !="" &&mailAccount!="")
			    document.getElementById("button_confirm").style.background="#E14E2E";
	    	}else{
	    	   var c_title= $("#c_title").val();
			   var p_pho= $("#c_pho").val();
				var mailAccount= $("#c_mail").val();
				var c_no= $("#c_no").val();
				if(c_title!=""&&p_pho != null && p_pho !="" &&mailAccount!=""&&c_no!="")
			    document.getElementById("button_confirm").style.background="#E14E2E";
	    	}
    });
			//订单参数
			var params = {};
	    	$(".invoiceType1").click(function(event) {
	    		if(!$(this).hasClass("invoiceTypeSelected")){
	    			$(".invoiceType1,.invoiceType2_en,.invoiceType3").removeClass("invoiceTypeSelected");
	    			document.getElementById("invoiceType1").style.borderColor="#E14E2E";
	    			document.getElementById("invoiceType1").style.color="#E14E2E";
	    			document.getElementById("invoiceType2").style.borderColor="#CCCCCC";
	    			document.getElementById("invoiceType2").style.color="#CCCCCC";
	    			$(".invoiceType1 .icon_detail").attr("src","<@resource path="images/icon_details01.svg"/>");
	    			$(".invoiceType1 .jiaobiao1").attr("src","<@resource path="images/icon_jiaoji.svg"/>");
	    			$(".invoiceType2_en .icon_detail").attr("src","<@resource path="images/icon_classification01.svg"/>");
	    			$(".invoiceType2_en .jiaobiao1").attr("src","<@resource path="images/icon_jiaoweiji.svg"/>");
	    			$(".invoiceType3 img").attr("src","<@resource path="images/content_icon06.png"/>");
	    			$(this).addClass("invoiceTypeSelected");
	    		}
	    	});
	    	$(".invoiceType2_en").click(function(event) {
	    		if(!$(this).hasClass("invoiceTypeSelected")){
	    			$(".invoiceType1,.invoiceType2_en,.invoiceType3").removeClass("invoiceTypeSelected");
	    			document.getElementById("invoiceType2").style.borderColor="#E14E2E";
	    			document.getElementById("invoiceType2").style.color="#E14E2E";
	    			document.getElementById("invoiceType1").style.borderColor="#CCCCCC";
	    			document.getElementById("invoiceType1").style.color="#CCCCCC";
	    			$(".invoiceType1 .icon_detail").attr("src","<@resource path="images/icon_details02.svg"/>");
	    			$(".invoiceType1 .jiaobiao1").attr("src","<@resource path="images/icon_jiaoweiji.svg"/>");
	    			$(".invoiceType2_en .icon_detail").attr("src","<@resource path="images/icon_classification02.svg"/>");
	    			$(".invoiceType2_en .jiaobiao1").attr("src","<@resource path="images/icon_jiaoji.svg"/>");
	    			$(".invoiceType3 img").attr("src","<@resource path="images/content_icon06.png"/>");
	    			$(this).addClass("invoiceTypeSelected");
	    		}
	    	});	    
	    	
	    	$("#invoiceTypePerson").click(function(event) {
	    	    $(".invoiceTypeP .imgnone").attr("src","<@resource path="images/icon_yixuanze.svg"/>");
	    	    $(".invoiceTypeC .imgnone").attr("src","<@resource path="images/icon_weixuanze.svg"/>");
	    	    $(".invoiceTypeP .jiaobiao2").attr("src","<@resource path="images/icon_jiaoji.svg"/>");
	    	    $(".invoiceTypeC .jiaobiao2").attr("src","<@resource path="images/icon_jiaoweiji.svg"/>");
	    	    document.getElementById("invoiceTypePerson").style.borderColor="#E14E2E";
	    	    document.getElementById("invoiceTypePerson").style.color="#E14E2E";
	    		document.getElementById("invoiceTypeCompany").style.borderColor="#CCCCCC";
	    		document.getElementById("invoiceTypeCompany").style.color="#CCCCCC";
	    		$("#invoiceTypePerson").addClass("selected");
	    		$("#invoiceTypeCompany").removeClass("selected");
	    		$("#company").hide();
	    		$("#personal").show();
	    		var personInfoStr = localStorage.getItem("personInfo");
		    	if(personInfoStr){
		    		personInfo = JSON.parse(personInfoStr);
		    		$("#p_title").val(personInfo.INVOICETITLE);
					$("#p_pho").val(personInfo.INVOICEPHONE);
					$("#p_mail").val(personInfo.MAILACCOUNT);
					document.getElementById("button_confirm").style.backgroundColor="#E14E2E";
		    	}else{
		    		document.getElementById("button_confirm").style.backgroundColor="#CCCCCC";
		    	}
	    	});
	    	
	    	$("#invoiceTypeCompany").click(function(event) {
	    	var screen_height = $(document).height();
	    	 $(".invoiceTypeC img").attr("src","<@resource path="images/icon_yixuanze.svg"/>");
	    	 $(".invoiceTypeP img").attr("src","<@resource path="images/icon_weixuanze.svg"/>");
	    	 $(".invoiceTypeC .jiaobiao2").attr("src","<@resource path="images/icon_jiaoji.svg"/>");
	    	 $(".invoiceTypeP .jiaobiao2").attr("src","<@resource path="images/icon_jiaoweiji.svg"/>");
	    	 document.getElementById("invoiceTypeCompany").style.borderColor="#E14E2E";
	    	    document.getElementById("invoiceTypeCompany").style.color="#E14E2E";
	    		document.getElementById("invoiceTypePerson").style.borderColor="#CCCCCC";
	    		document.getElementById("invoiceTypePerson").style.color="#CCCCCC";
	    		$("#invoiceTypePerson").removeClass("selected");
	    		$("#invoiceTypeCompany").addClass("selected");
	    		$("#company").show();
	    		$("#personal").hide();
	    		var complanyInfoStr = localStorage.getItem("complanyInfo");
		    	if(complanyInfoStr){
		    		complanyInfo = JSON.parse(complanyInfoStr);
		    		$("#c_title").val(complanyInfo.INVOICETITLE);
		    		$("#c_no").val(complanyInfo.TAXPAYERNUMBER);
					$("#c_pho").val(complanyInfo.INVOICEPHONE);
					$("#c_mail").val(complanyInfo.MAILACCOUNT);
		    		$("#c_bank").val(complanyInfo.DEPOSITBANK);
					$("#c_bankNo").val(complanyInfo.BANKACCOUNT);
					$("#c_address").val(complanyInfo.ADDRESS);
					$("#c_phone").val(complanyInfo.TELEPHONENUMBER);
					document.getElementById("button_confirm").style.backgroundColor="#E14E2E";
		    	}else{
		    		document.getElementById("button_confirm").style.backgroundColor="#CCCCCC";
		    	}
	    	});
	
	    	$("#button_confirm").click(function(event) {
	    		if($("#kkpje").text() == "￥0"){
	    		$("#tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("You Cannot Issue Invoices With a Value of 0");
				}else{
				 var screen_height = $(document).height();
				 $("#down").css("height",screen_height +"px");
				 $("#down_rgb").css("height",screen_height +"px");
		         invoiceConfirm();
				}
	    	});
	    	
	    	function isConfirmP(Obj) { 
		       var p_title= $("#p_title").val();
			   var p_pho= $("#p_pho").val();
			   var mailAccount= $("#p_mail").val();
			   var curValue = $(Obj).val();
			   if(curValue!=""){
				var borderParent = $(Obj).parent().parent();
				borderParent.css("border-color","#CCC");
			   }
			   if(p_title!=""&&p_pho!=""&&mailAccount!=""){
					document.getElementById("button_confirm").style.backgroundColor="#E14E2E";
					}else{
					 document.getElementById("button_confirm").style.backgroundColor="#CCCCCC";
				}
	   		}
		    
		    
		    function isConfirmC(Obj) { 
		       var c_title= $("#c_title").val();
			   var mailAccount= $("#c_mail").val();
			   var taxpayerNumber= $("#c_no").val();
			   var c_pho= $("#c_pho").val();
			   var curValue = $(Obj).val();
			   if(curValue!=""){
					var borderParent = $(Obj).parent().parent();
					borderParent.css("border-color","#CCC");
			   }
			   if(c_title!=""&&mailAccount!=""&&taxpayerNumber!=""&&c_pho!=""){
					document.getElementById("button_confirm").style.backgroundColor="#E14E2E";
					}else{
					 document.getElementById("button_confirm").style.backgroundColor="#CCCCCC";
				}
		    }
		    
		    function byteLength(str) {
			 	var byteLen = 0, len = str.length;
			 	if( !str ) return 0;
			 	for( var i=0; i<len; i++ )
			 	 byteLen += str.charCodeAt(i) > 255 ? 2 : 1;
			 	return byteLen;
			}
			
	    	function invoiceConfirm(){
	    	
	    		var invoiceType = $(".invoiceType4.selected").attr("data-invoice");
	    		$(".confirm ul li").remove();
	    		if("E00801" == invoiceType){
	    		  
					params.INVOICE_WRITE_TYPE = "E00801";
	    			var p_title= $("#p_title").val();
	    			if(p_title==""){
						document.getElementById("p_title1").style.borderColor="#E14E2E";
						$("#tip").show().delay(3000).fadeOut();
					    $(".three_p").html("Please Fill In The Invoice Title");
						return;
					}else{
					    document.getElementById("p_title1").style.borderColor="#CCCCCC";
					}
					if(byteLength(p_title)> 100 ){
						$("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("The Length of Personal/Government Name Cannot Exceed 100 Characters");
						return;
					}
					
					var phoneFilter  = /^[1][3-8]+\d{9}$/;
 		            var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
					var p_pho= $("#p_pho").val();
					if(p_pho==""){
					    document.getElementById("p_pho1").style.borderColor="#E14E2E";
					    $("#tip").show().delay(3000).fadeOut();
					    $(".three_p").html("Please Fill In The Phone");
						return;
					}else{
					   document.getElementById("p_pho1").style.borderColor="#CCCCCC";
					}
					if(p_pho != null && p_pho !="" && byteLength(p_pho) > 20  ){
					        $("#tip").show().delay(3000).fadeOut();
							$(".three_p").html("The Length of Phone Cannot Exceed 20 Characters");
							return;
					}
					if(!phoneFilter.test(p_pho) ){
					        $("#tip").show().delay(3000).fadeOut();
							$(".three_p").html("The format of the phone number is not correct");
							return;
					}	
						
			 		var mailFilter  = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	    			var mailAccount= $("#p_mail").val();
					if(mailAccount==""){
					    document.getElementById("p_mail1").style.borderColor="#E14E2E";
				    	$("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Please Enter A Billing Address");	
						return;
					}else{
					    document.getElementById("p_mail1").style.borderColor="#CCCCCC";
					}
					if (!mailFilter.test(mailAccount)){
					    $("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Billing Address Is Incorrect");
						return;
					}
					if(byteLength(mailAccount) > 50 ){
					    $("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Billing Address Cannot Exceed 50 Characters");
						return;
					}
					 
					var title=document.getElementById("blank42").innerHTML;
					document.getElementById("p_title1").style.borderColor="#CCCCCC";
					document.getElementById("p_pho1").style.borderColor="#CCCCCC";
					document.getElementById("p_mail1").style.borderColor="#CCCCCC";
					$(".confirm ul").append('<li class=clearfix><p class="fl flr">Type</p><p class="fr color_84 frr">'+title+'</p>');
					$(".confirm ul").append('<li class=clearfix><p class="fl flr">Name</p><p class="fr color_84 frr">'+p_title+'</p>');
					$(".confirm ul").append('<li class=clearfix><p class="fl flr">Mobile Number</p><p class="fr color_84 frr">'+p_pho+'</p>');
					$(".confirm ul").append('<li class=clearfix><p class="fl flr">Email Address</p><p class="fr color_84 frr">'+mailAccount+'</p>');
	    		
	    		     params.method = "invoiceConfirm";
	    		     params.INVOICE_WRITE_TYPE = "E00801";
	    		     params.INVOICETITLE = p_title;
	    		     params.INVOICEPHONE = p_pho;
	    		     params.MAILACCOUNT = mailAccount;
	    		     //保存开票信息
		             localStorage.setItem("personInfo",JSON.stringify(params));
	    		}else{
	    			params.INVOICE_WRITE_TYPE = "E00802";
			 		var mailFilter  = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
			 		var c_title= $("#c_title").val();
					var mailAccount= $("#c_mail").val();
					var taxpayerNumber= $("#c_no").val();
					var c_pho= $("#c_pho").val();
					if(c_title==""){
						document.getElementById("p_title2").style.borderColor="#E14E2E";	
						$("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Please Enter a Company Name");
						return;
					}else{
					   document.getElementById("p_title2").style.borderColor="#CCCCCC";
					}
					if(byteLength(c_title) > 100 ){	
					    $("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Company Name Cannot Exceed 200 Characters");
						return;
					}
					
					if(taxpayerNumber==""){
						document.getElementById("c_no2").style.borderColor="#E14E2E";
						$("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Please Fill In the Taxpayer Information");					
						return;
					}else{
					    document.getElementById("c_no2").style.borderColor="#CCCCCC";
					}
					if( ( taxpayerNumber.length <18 || taxpayerNumber.length>20 ) && taxpayerNumber.length!= 15 ){
						document.getElementById("c_no2").style.borderColor="#E14E2E";
						$("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Taxpayer Number Is Incorrect, Should be 15 Characters");
						return;
					}
					
					var phoneFilter  = /^[1][3-8]+\d{9}$/;
 		            var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
					if(c_pho==""){
						document.getElementById("c_pho2").style.borderColor="#E14E2E";
						 $("#tip").show().delay(3000).fadeOut();
					    $(".three_p").html("Please Fill In The Phone");
						return;
					}else{
					    document.getElementById("c_pho2").style.borderColor="#CCCCCC";
					}
					if(c_pho != null && c_pho !="" && byteLength(c_pho) > 20 ){
					        $("#tip").show().delay(3000).fadeOut();
							$(".three_p").html("The Length of Phone Cannot Exceed 20 Characters");
							return;
					}
					if(!phoneFilter.test(c_pho) ){
					        $("#tip").show().delay(3000).fadeOut();
							$(".three_p").html("The format of the phone number is not correct");
							return;
					}
						
					if(mailAccount==""){
						document.getElementById("c_mail2").style.borderColor="#E14E2E";
						$("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Please Enter A Billing Address");
						return;
					}else{
					    document.getElementById("c_mail2").style.borderColor="#CCCCCC";
					}
					if (!mailFilter.test(mailAccount)){
					    $("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Billing Address Is Incorrect");
						return;
					}
					if(byteLength(mailAccount) > 50 ){
					    $("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("Billing Address Cannot Exceed 50 Characters");
						return;
					}
							
					var address= $("#c_address").val();
					if(address != null && address !="" && byteLength(address) > 80  ){
					       $("#tip").show().delay(3000).fadeOut();
							$(".three_p").html("Address Cannot Exceed 50 Characters");
							return;
					}
					var telephoneNumber= $("#c_phone").val();
					if(telephoneNumber != null && telephoneNumber !="" && byteLength(telephoneNumber) > 20 ){
					        $("#tip").show().delay(3000).fadeOut();
							$(".three_p").html("Tel Cannot Exceed 50 Characters");
							return;
					}
					if(telephoneNumber != null && telephoneNumber !="" &&!(phoneFilter.test(telephoneNumber)||isPhone.test(telephoneNumber))){
				       $("#tip").show().delay(3000).fadeOut();
						$(".three_p").html("The format of the tel number is not correct");
						return;
				   }
					var depositBank= $("#c_bank").val();
					var bankAccount= $("#c_bankNo").val();
					if(depositBank != null && depositBank !="" && bankAccount!=null && bankAccount !="" && byteLength(depositBank)+byteLength(bankAccount) > 100  ){
					        $("#tip").show().delay(3000).fadeOut();
							$(".three_p").html("Bank Account & Name Cannot Exceed 100 Characters");			
                            return;
					}
					var title=document.getElementById("blank40").innerHTML;
					$(".confirm ul").append('<li class=clearfix><p class="fl flr">Type</p><p class="fr color_84 frr">'+title+'</p>');
	    			$(".confirm ul").append('<li class=clearfix><p class="fl flr">Name</p><p class="fr color_84 frr">'+c_title+'</p>');					
					$(".confirm ul").append('<li class=clearfix><p class="fl flr">Taxpayer No.</p><p class="fr color_84 frr">'+taxpayerNumber+'</p>');
					$(".confirm ul").append('<li class=clearfix><p class="fl flr">Mobile Number</p><p class="fr color_84 frr">'+c_pho+'</p>');
					$(".confirm ul").append('<li class=clearfix><p class="fl flr">Email Address</p><p class="fr color_84 frr">'+mailAccount+'</p>');
	    		    
	    		     params.method = "invoiceConfirm";
	    		     params.INVOICE_WRITE_TYPE = "E00802";
	    		     params.INVOICETITLE = c_title;
	    		     params.INVOICEPHONE = c_pho;
	    		     params.TAXPAYERNUMBER = taxpayerNumber;
	    		     params.MAILACCOUNT = mailAccount;
	    		     params.ADDRESS = address;
	    		     params.TELEPHONENUMBER = telephoneNumber;
	    		     params.DEPOSITBANK = depositBank;
					 params.BANKACCOUNT = bankAccount;
	    		     //保存开票信息
		             localStorage.setItem("complanyInfo",JSON.stringify(params));
	    		}				
				$(".down,.confirm").show();
				document.getElementsByTagName("body")[0].setAttribute("style","overflow:hidden;");				
		        document.body.addEventListener('touchmove',handler,false);
                document.body.addEventListener('wheel',handler,false);
		     }
		    $(".btn_return").click(function(event) {
				$(".down,.confirm").hide();
				document.getElementsByTagName("body")[0].setAttribute("style","overflow:auto;height:100%;");
				document.body.removeEventListener('touchmove',handler,false);
               document.body.removeEventListener('wheel',handler,false);
			});
			
			$(".btn_confirm").click(function(event) {
				$(".down,.confirm").hide();
				$(".down_rgb,.l-wrapper").show();
				document.getElementsByTagName("body")[0].setAttribute("style","overflow:auto;height:100%;");
				document.body.removeEventListener('touchmove',handler,false);
               document.body.removeEventListener('wheel',handler,false);
				if($("#kkpje").text() == "￥0"){
				$("#tip").show().delay(3000).fadeOut();
					$(".three_p").html("You Cannot Issue Invoices With a Value of 0");
				}else{
					params.method = "dealOrder";
		    		params.INVOICE_DETAIL_TYPE = $(".invoiceTypeSelected").attr("data-invoice");
		    		params.INVOICE_WRITE_TYPE = $(".invoiceType4.selected").attr("data-invoice");
					businessExec("billingService",params,function(data){
						if(data.SUC == true){
							timer(data.ORDER_ID);
						}else{
						$("#tip").show().delay(3000).fadeOut();
							$(".three_p").html("You Cannot Issue Invoices With a Value of 0");
						}
					})
				}
				
	    		
			});
			
			
			var num = 0;
			function timer(orderId) {
		    	console.log(num);
		    	if(num < 10){
		    		var params = {};
		    		params.method = "getOrderStatusById";
	    			params.orderId = orderId;
		    		var bisinessNo = "billingService";
		    		generatSign(bisinessNo,params);
					var url = getRdpPostURL(bisinessNo+"/biz-service.htm");
		    		$.ajax({
						type : "post",
						datatype : "json",
						data:params,
						url :url,
						async : false,
						success : function(responsedata) {
							var rspData = $.parseJSON(responsedata);
							if (rspData == null)
							{
								alert("业务调用失败！");
								return;
							}
			
							if (rspData.RESULT == "-1")
							{
								alert("业务调用失败！");
								return;
							}else if (rspData.RESULT == "2")
							{
								alert("站点不存在！");
								return;
							}else if (rspData.RESULT == "3")
							{
								alert("业务号不存在！");
								return;
							}else if (rspData.RESULT == "4")
							{
								alert("业务服务不存在！");
								return;
							}else if (rspData.RESULT == "5")
							{
								alert(rspData.EXCEPTION);
								return;
							}else if (rspData.RESULT == "1001")
							{
								if(rspData.EXCEPTION=="Incorrent Password"){
									alert("密码输入错误");
									return;
								}else{
									alert(rspData.EXCEPTION);
									return;
								}
							}
							if (rspData.RESULT != null)
							{
								if(params != null && params.method != "setSessionValues" && params.method != "getJSTicket")
								{
									clearHistory();
								}
								var data = rspData.DATA;
								if(data.SUC){
									location.replace("${invoiceSuccess_en.linkURL}"); 
									event.returnValue=false;
									//window.location.href = "${invoiceSuccess_en.linkURL}";
								}else{
									//需要计数
									num = num+1;
									if(num >= 10){
										timer(orderId);
									}else{
										setTimeout("timer('"+orderId+"')",5000)
									}
								}
							}
						},
						error : function(XMLHttpRequest, textStatus, errorThrown){
							//需要计数
							num = num+1;
							if(num >= 10){
								timer(orderId);
							}else{
								setTimeout("timer('"+orderId+"')",5000)
							}
						}
					});
		    	}else{
                    num = 0;
                    location.replace("${invoiceFail_en.linkURL}"); 
					event.returnValue=false;
                    //window.location.href = "${invoiceFail_en.linkURL}";
		    	}
			}
			
		var num = 0 ;
		var interval ;
		var isConfirm = true;
		//初始化方法
		$(function(){
	    	//之前未开票，本次第一次开票，获取localStoge中的数据，作为本次的开票信息
	    	var personInfoStr = localStorage.getItem("personInfo");
	    	if(personInfoStr){
	    		personInfo = JSON.parse(personInfoStr);
	    		$("#p_title").val(personInfo.INVOICETITLE);
				$("#p_pho").val(personInfo.INVOICEPHONE);
				$("#p_mail").val(personInfo.MAILACCOUNT);
				document.getElementById("button_confirm").style.backgroundColor="#E14E2E";
	    	}
		});
	    </script>		
	    <@template id="commonBodyBottom" />
	</html>
<#else>
	<script>
		window.location.href = "${invoiceIndex_en.linkURL}";
	</script>
</#if>
