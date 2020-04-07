<!doctype html>
<html>
    <head>
        <@template id="invoiceCommonHeadTop" />
        <title>E-Invoice Details</title>
    </head>
    <body class="lang_cn body_grey" onload="time()">
        <@template id="commonBodyTop" />
	    <div id="app">
	    	<div class="grey" >
		    	<div id="headr" >
		    	<div class="header_container">
		    	   <div class="minh">E-Invoice Details</div>
		    	   <div class="maxh">HG E-Invoice</div>
		    	   <div id="yearday">
		    	   </div>
		    	   </div>				
				</div>
	    	    <!-- 主要区域 -->
	    	    <section id="main" class="main invoice invoiceEnd">
	    	    	<div>
	    	    		<@business id="billingService" var="result" method="getInvoiceInfo" cachetime="0"/>
	    	    		<#assign INVOICEOBJ=result.INVOICEOBJ>
	    	    		<#assign invoiceList=INVOICEOBJ.invoiceList>
	    	    		<div class="content">
							<div class="invoice_status invoice_padding">
								Invoice Issued
							</div>
							<div class="invoice_end_container invoice_padding">
								<div class="invoice_end_line">
									<div class="label_end_title">Order Number</div>
									<div class="label_end_value">
										<#list INVOICEOBJ.TRANSACTION_NUMBER as orderNum>
                                			<p>${orderNum}</p>
                            			</#list>
									</div>
								</div>
								<div class="invoice_end_line">
									<div class="label_end_title label_en_title">Invoice Time</div>
									<div class="label_end_value label_en_tip">
										${INVOICEOBJ.BILLING_DATE}
									</div>
								</div>
							</div>
							<div class="invoice_end_container invoice_padding">
								<div class="invoice_end_line no_content">
									<div class="label_end_title" style="width:100%;">Delivery Information</div>
								</div>
								<div class="invoice_end_line">
									<div class="label_sub_title label_en_title">Email：</div>
									<div class="label_tip label_en_tip">
										${INVOICEOBJ.EMAIL}
									</div>
								</div>
							</div>
							<#list invoiceList as invoice>	
							<div class="invoice_end_container">
								<div class="invoice_end_line no_content">
									<div class="label_end_title" style="width:100%;">E-Invoice Details</div>
								</div>
								<div class="invoice_end_line">
								<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Name：</div>
									<div class="label_tip label_en_tip">
										${INVOICEOBJ.GHFMC}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Mobile：</div>
									<div class="label_tip label_en_tip">
										${INVOICEOBJ.GHFDH}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Payee No.：</div>
									<div class="label_tip label_en_tip">
										${INVOICEOBJ.NSRSBH}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Bank Name：</div>
									<div class="label_tip label_en_tip">
										${INVOICEOBJ.BANK}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Bank Account：</div>
									<div class="label_tip label_en_tip">
										${INVOICEOBJ.ACCOUNT}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Address：</div>
									<div class="label_tip label_en_tip">
										${INVOICEOBJ.XHF_DZ}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Tel：</div>
									<div class="label_tip label_en_tip">
										${INVOICEOBJ.XHF_DH}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Amount：</div>
									<div class="label_tip label_en_tip">
										${invoice.TOTAL_AMOUNT_WITHOUT_TAX}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Tax Rate：</div>
									<div class="label_tip label_en_tip">
										${invoice.TOTAL_TAX_AMOUNT}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Total：</div>
									<div class="label_tip label_en_tip">
										${invoice.TOTAL_AMOUNT}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Invoice Code：</div>
									<div class="label_tip label_en_tip">
										${invoice.INVOICE_CODE}
									</div>
									</div>
									<div class="invoice_end_line_min">
									<div class="label_sub_title label_en_title">Invoice ID：</div>
									<div class="label_tip label_en_tip">
										${invoice.INVOICE_NUMBER}
									</div>
									</div>
								</div>
							</div>
							<div class="invoice_weixin_container"  style="display:none;">
							   <div class="weixin" id="weixin">Add to the WeChat Package</div>
		    	    		</div>
							<div class="clear invoice_end_btn_container">
									<div class="invoice_end_btn sendEmail" id="sendEmail" data-invoiceid="${invoice.INVOICE_ID}">
									<img src="<@resource path="images/icon_weixuanze.svg"/>">
									     Send
									</div>
									<div class="invoice_end_btn download" id="download" data-href="${invoice.INVOICE_URL}">
									<img src="<@resource path="images/icon_weixuanze.svg"/>">
										Download
									</div>
								</div>
							</div>
							</#list>
						</div>
	    	    		
	    	    	</div>
	    	    </section>

			      <!-- load -->
	    	  
	    	    <div class="down" id="down"></div>
	    	    <div id="commMagTab" class="invoice_down invoice_down_top tc">
	    	    <div class="send_margin">
	    	    	<p class="first_p" ></p>
	    	    	<p class="last_p" ></p>
	    	    	<p class="four_p" ></p>
	    	    	<div class="close1" style="margin-left: 26%;margin-top: 10px;margin-bottom: 18px;position: static;">Got it</div>
	    	    </div>
	    	    </div>
	    	    <!-- send -->
			   <div class="invoice_down invoice_down1 invoice_down_top tc">
			    <div class="send_margin">
			    	<p class="first_p">My Email</p>			    	
			    	<div>
			    		<input id="newEmail" class="newEmail"  placeholder="Enter My Email" value=""/>
			    	</div>	
			    	<div class="btn_end">
			    	  <div id="quxiao" class="quxiao">Cancel</div>				   	    	
				      <div id="send" class="sendqueren">Confirm</div>
				    </div>	
			    	<br/>
			     </div>
			    </div>
			    
			    <div class="invoice_down invoice_down2 invoice_down_top tc">
			     <div class="send_margin">
			    	
			    	<p class="last_p" id="newEmailInputTitle">Your Invoice sended to</p>
			    	<p class="last_p" style="color:#E14E2E;" id="newEmailInput"></p>
			    	<div class="close" style="margin-left: 26%;margin-top: 10px;margin-bottom: 18px;position: static;">Got it</div>
			     </div>
			    </div>
	    	</div>
	    </div>
    </body>
    <script type="text/javascript">
    	var invoiceId = "";    
    	 var screen_height = $(document).height();
    	 $("#down").css("height",screen_height +"px");
    	 
    	 function time(){
		  var Week = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];  
	      var now= new Date();	
		  var year=now.getFullYear();
		  var Month=['January','February','March','April','May','June','July','August','September','October','November','December'];	
		  var month=Month[now.getMonth()];	
		  var date=now.getDate();
		  var week=Week[now.getDay()];	
		  document.getElementById("yearday").innerHTML=month+" "+date+",  "+year+"   "+week;
       }
    
       var handler = function () {
        event.preventDefault();
        event.stopPropagation();
    };
    
      $(".quxiao").click(function(event) {
			$(".invoice_down1,.down").hide();
			document.body.removeEventListener('touchmove',handler,false);
            document.body.removeEventListener('wheel',handler,false);
		});
    	$("#send").click(function(event){
    	
    		var newEmail = $("#newEmail").val();
    		if(newEmail == null || newEmail == ""){
    			$(".invoice_down1").hide();
    			alert("Please Enter A Billing Address");
    			return;
    		}
    		var mailFilter  = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    		if (!mailFilter.test(newEmail)){
    			$(".invoice_down1").hide();
				alert("Billing Address Is Incorrect");
				return;
			}
    		var params = {};
    		params.method = "reSendEmail";
    		params.invoiceId = invoiceId;
    		params.newEmail = newEmail;
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
					if (rspData != null && rspData.RESULT != null)
					{
						var data = rspData.DATA;
						if(data.SUC){
							$("#newEmailInputTitle").text("Your Email Has Been Sended");
							$("#newEmailInput").text(newEmail);
				    		$(".invoice_down1,.down").hide();
				    		$(".invoice_down2,.down").show();
				    		document.getElementsByTagName("body")[0].setAttribute("style","overflow:hidden;");
						    document.body.addEventListener('touchmove',handler,false);
                            document.body.addEventListener('wheel',handler,false);
						}else{
							alert(data.MSG);
						}
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown){
					alert("系统异常,请稍后重试");
				}
			});
			
    	});
    	
    	 $(".sendEmail").click(function(event){
        $(".sendEmail").each(function(){
        	$(this).find("img").attr("src","<@resource path="images/icon_weixuanze.svg"/>");
        	$(this).css("color","#333333");
    	    $(this).css("borderColor","#E6E6E6");
        });
    	$(this).find("img").attr("src","<@resource path="images/icon_yixuanze.svg"/>");	  
	    $(this).css("color","#E14E2E");
    	$(this).css("borderColor","#E14E2E"); 
    	
    	var objdown = $(".download");
    	$(objdown).find("img").attr("src","<@resource path="images/icon_weixuanze.svg"/>");
    	$(objdown).css("color","#333333");
    	$(objdown).css("borderColor","#E6E6E6");
    	invoiceId = $(this).attr("data-invoiceid");
    	$(".invoice_down2,.down").hide();
    	$(".invoice_down1,.down").show();
    	document.getElementsByTagName("body")[0].setAttribute("style","overflow:hidden;");
    	document.body.addEventListener('touchmove',handler,false);
        document.body.addEventListener('wheel',handler,false);
    	});
    	
    	$(".download").click(function(event){
    	$(".download").each(function(){
        	$(this).find("img").attr("src","<@resource path="images/icon_weixuanze.svg"/>");
        	$(this).css("color","#333333");
    	    $(this).css("borderColor","#E6E6E6");
        });
    	 $(this).find("img").attr("src","<@resource path="images/icon_yixuanze.svg"/>");
	     $(this).css("color","#E14E2E");
    	 $(this).css("borderColor","#E14E2E"); 
    	
    	var objsend = $(".sendEmail");
    	$(objsend).find("img").attr("src","<@resource path="images/icon_weixuanze.svg"/>");
    	$(objsend).css("color","#333333");
    	$(objsend).css("borderColor","#E6E6E6");
    	window.location.href = $(this).attr("data-href");
    	});
    	
    	$(".weixin").click(function(event){
    		$(this).css("color","#CCCCCC");
    		$(this).html("Already Add to the WeChat package");
    	});   	
    </script>
    <@template id="commonBodyBottom" />
</html>