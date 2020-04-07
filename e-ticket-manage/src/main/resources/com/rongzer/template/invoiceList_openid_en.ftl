<!doctype html>
<@navigate id="index/invoiceIndex_en" var="invoiceIndex_en"/>
<@navigate id="index/invoiceHistory_en" var="invoiceHistory_en"/>
<@navigate id="index/invoiceWrite_en" var="invoiceWrite_en" cacahetime="0"/>
<html>
     <head>
        <@template id="invoiceCommonHeadTop" />
        <title>Get E-Invoice</title>
    </head>
    <body class="lang_cn body_grey" onload="time()">
        <input type="text" id="isFresh" value="true" style="display:none">
        <@business id="billingService" var="result" method="getTradeInfosByOpenId" cachetime="0"/>
	    <div id="app">
	    	<div class="grey" >
		    	<div id="headr" >
		    	<div class="header_container">		    	   
			    	<div class="minh">Get E-Invoice</div>
		    	   <div id="yearday">
		    	   </div>
		    	   </div>			
				</div>
	    	    <!-- 主要区域 -->
	    	    <section id="main" class="main invoice ">
	    	    	<div>
	    	    		<#assign tradeinfoMap=result.tradeinfos>
                            <div class="content">
                                <div class="des" style="width:40%;">
                                  <a class="desc1">History</a>
                                  <a class="desc">Notification | </a>
                                </div>
							</div>
	    	    		<#if tradeinfoMap?size gt 0>	    	    		
	    	    		<div class="content">
                        <#list tradeinfoMap?keys as key>
		    	    					<div class="monthlist" id="monthlist">  
	    	    				       	 <div id="monthonly">${key?substring(5,7)} Month</div>
		    	    	 	            </div>
		    	    	  <#assign fapiaoInfos=tradeinfoMap["${key}"]>
		    	    	  <#list fapiaoInfos as fapiaoInfo>
			<div class="trans_list_container <#if fapiaoInfo.INVOICE_STATUS != 'E00501' || fapiaoInfo.ALLOWED_INVOICE != 'E01101' || fapiaoInfo.TAXPAYER_ID == ''|| fapiaoInfo.DEAL_AMOUNT?number lte 0 || fapiaoInfo.IS_FORBINDDEN == 'YES'> unable</#if>" id="checkbox"  data-code="${fapiaoInfo.TRANSACTION_NUMBER}" data-time="${fapiaoInfo.TRANSACTION_DATETIME?substring(0,4)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(5,7)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(8,10)}" data-dealamount="${fapiaoInfo.DEAL_AMOUNT?number}" data-isforbidden="${fapiaoInfo.IS_FORBINDDEN}" data-storenumber="${fapiaoInfo.STORE_NUMBER}" data-amount="${fapiaoInfo.TRANSACTION_AMOUNT?number}" data-card="${fapiaoInfo.isCard}" data-taxpayer-id="${fapiaoInfo.TAXPAYER_ID}" data-status="${fapiaoInfo.INVOICE_STATUS}" data-merge="${fapiaoInfo.IS_MERGE}" data-limit="${fapiaoInfo.INVOICE_LIMIT_AMOUNT}">
				<ul class="trans_list">
					
					<#if fapiaoInfo.INVOICE_STATUS == 'E00502'|| fapiaoInfo.INVOICE_STATUS == 'E00503'|| fapiaoInfo.INVOICE_STATUS == 'E00504'|| fapiaoInfo.DEAL_AMOUNT?number lte 0 ||fapiaoInfo.IS_FORBINDDEN == 'YES'>
					<li>
						<div class="checkbox trans_checkbox" style="display: none;">
							<input type="checkbox" id="checkbox3" />
							
						</div>
						<div class="trans_info">
							<div class="trans_info_content">
								<div class="content_line">
									<b class="trans_time color_grey">${fapiaoInfo.TRANSACTION_DATETIME?substring(0,4)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(5,7)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(8,10)} ${fapiaoInfo.TRANSACTION_DATETIME?substring(11,13)}:${fapiaoInfo.TRANSACTION_DATETIME?substring(14,16)}:${fapiaoInfo.TRANSACTION_DATETIME?substring(17,19)}</b>
									<b class="trans_amount_en_grey color_grey">￥${fapiaoInfo.TRANSACTION_AMOUNT?number}</b>
								</div>
								<div class="content_line">
									<b class="trans_order color_grey">${fapiaoInfo.TRANSACTION_NUMBER}</b>
								</div>
								<div class="content_line">
									<b class="trans_location color_grey">${fapiaoInfo.STORE_NAME_EN}</b>
									<#if fapiaoInfo.INVOICE_STATUS == 'E00502'>
				    	    			<a class="trans_status" id="trans_status">Invoice Being Issued</a>
				    	    		<#elseif fapiaoInfo.INVOICE_STATUS == 'E00503'>
				    	    			<a class="trans_status" id="trans_status">Invoice Downloading</a>
				    	    		<#elseif fapiaoInfo.INVOICE_STATUS == 'E00504'>
				    	    			<a class="trans_status" id="trans_status">Invoiced Failed</a>
				    	    		<#elseif fapiaoInfo.IS_FORBINDDEN == 'YES'>
				    	    			<a class="trans_status" id="trans_status">Invoice forbidden</a>
				    	    		<#elseif fapiaoInfo.DEAL_AMOUNT?number== 0>
				    	    			<a class="trans_status" id="trans_status">Amount Is 0</a>
				    	    		<#elseif fapiaoInfo.DEAL_AMOUNT?number lt 0>
				    	    			<a class="trans_status" id="trans_status">Amount Is negative</a>
							    	</#if>
									
								</div>
							</div>
						</div>
					</li>
					<#else>
					<li>
						<div class="checkbox trans_checkbox" >
							<#if fapiaoInfo.INVOICE_STATUS == 'E00501'>
			    	    	  <img src="<@resource path="images/icon_duoxuan_weixuanze.svg"/>">
			    	    	</#if>							
						</div>
						<div class="trans_info">
							<div class="trans_info_content">
								<div class="content_line">
									<b class="trans_time">${fapiaoInfo.TRANSACTION_DATETIME?substring(0,4)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(5,7)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(8,10)} ${fapiaoInfo.TRANSACTION_DATETIME?substring(11,13)}:${fapiaoInfo.TRANSACTION_DATETIME?substring(14,16)}:${fapiaoInfo.TRANSACTION_DATETIME?substring(17,19)}</b>
									<b class="trans_amount_en"><span style="color:#E14E2E">${fapiaoInfo.TRANSACTION_AMOUNT?number}</span></b>
								</div>
								<div class="content_line">
									<b class="trans_order">${fapiaoInfo.TRANSACTION_NUMBER}</b>
								</div>
								<div class="content_line">
									<b class="trans_location">${fapiaoInfo.STORE_NAME_EN}</b>
								</div>
							</div>
						</div>
					</li>
					</#if>
				</ul>
			</div>
			    </#list>
			</#list>
			<ul class="trans_list">
			  <li style="background-color:#F2F2F2;">
			 </li>
			</ul>
		</div>
		<footer class="footer">
			<div class="order_number_en">
				<span style="color:#E14E2E" id="list1">0</span>
			</div>
			<div class="footer_container_en">
				<a href="javascript:;" class="btn orange" id="next">Next</a>
			</div>
		</footer>
		<#else>
		  <div class="content">
	    	   <div class="box contentsu">
	    	    <div class="succ">
	    	    	<img class="trans_no_logo" src="<@resource path="images/icon_transno.svg"/>"> 
	    	    	<img class="trans_no_logo_pc" style="width:200px;height:200px;" src="<@resource path="images/icon_transno_pc.svg"/>">    	    			
	    	    </div>
	    	    </div>	    	    	
	    	    <div class="succ_te" style="color:#333333;margin-top: 0px;">No Transactions
	    	    </div>		
	    	  </div>
	    	 <div class="succ_logo">
	    	    <img src="<@resource path="images/logo.svg"/>">    	    			
	    	 </div>
	     </#if>	
	    </div>
	    </section>	    	    
	    <!-- load -->
	    	    <div id="loading" class="loadingContainer">
					<p class="loadingText"></p>
	    	    </div>
	    	    <div id="commMagTab" class="invoice_down tc">
	    	    	<p class="first_p"></p>
	    	    	<p class="last_p"></p>
	    	    	<p class="four_p" style=" margin-top: -10px;"></p>
	    	    	<div class="close" style="margin-left: 26%;margin-top: 10px;margin-bottom: 18px;position: static;">Got it</div>
	    	    </div>	   
	    <!-- load -->
		<div class="down"></div>
		<div class="tip" id="tip">
			<ul>
			</ul>
			<p class="three_p"></p>
		 </div>    	    
	    	</div>
	    </div>
    </body>
    <script type="text/javascript">   
    var taxpayerId = "";
    var storenumber = "";
    var isCard = "";
     var screen_height = $(document).height();
    $("#down").css("height",screen_height +"px");
    var selectCount = $(".trans_list_container.selected").length;
    if(selectCount!=0){
        document.getElementById("list1").innerHTML=selectCount;
    }
    var serialNo = getUrlParam("serialNo");
    
    var isFresh = $("#isFresh").val();
	if(isFresh == "false"){
		window.location.reload();
	}
	
	var handler = function () {
        event.preventDefault();
        event.stopPropagation();
    };
    
	$(".trans_list_container").each(function(){
		var transactionNumber = $(this).attr("data-code");
    	   if(serialNo==transactionNumber){
			  if($(this).hasClass("unable")){ 
			  	$(this).removeClass("selected");
			  }else{		 
		    	$(this).removeClass("unable");
				$(this).addClass("selected");
				$(this).find(".trans_checkbox img").attr("src","<@resource path="images/icon_yixuanze.svg"/>");	
		      }	      
			}
    	selectCount = $(".trans_list_container.selected").length;
        if(selectCount!=0){
            document.getElementById("list1").innerHTML=selectCount;
        }
    });
    		
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
    
    $(".desc").click(function(event){
           $("#loading").hide();
			$(".first_p").html("");
			$(".last_p").html("");
			$(".four_p").html("You can only receive an E-Invoice for transactions within 30 days. Please contact our staff for transactions over 30 days.");
			$(".down,#commMagTab").show();
			document.getElementsByTagName("body")[0].setAttribute("style","overflow:hidden;");
			document.body.addEventListener('touchmove',handler,false);
            document.body.addEventListener('wheel',handler,false);
    });
    
    $("#next").click(function(event) {
    	$("#isFresh").val("false");
        var selectCount = $(".trans_list_container.selected").length;
    	if(selectCount > 0){
    		var extrCodes = new Array();
    		var tradeType = "E01002";
    		if(storenumber == "IF10" ){
    			tradeType = "E01001";
    		}else if( storenumber == "IF11"){
    			tradeType = "E01003";
    		}
    		$(".trans_list_container.selected").each(function(){
    			extrCodes.push($(this).attr("data-code"));
    		});
    		if(extrCodes.length>0){
	    		var params = {};
				params.method = "mergeApply";
				params.EXTRCODES = JSON.stringify(extrCodes);
				params.tradeType = tradeType;
				params.isCard = isCard;
	    		businessExec("billingService",params,function(data){
	    			if(data.suc){
	    				var randomNum = Math.random();
						window.location.href = "${invoiceWrite_en.linkURL}?randomNum="+randomNum;
	    			}
	    		});
    		}
    	
    	}else{
    		$("#tip").show().delay(3000).fadeOut();
	    	$(".three_p").html("Please choose one item");
    	}
	});
	
	var openId = getUrlParam("openId");

    
    function getUrlParam(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null)
			return unescape(r[2]);
		return null;
	}
	
	
	$(".desc1").click(function(enent){
	window.location.href = "${invoiceHistory_en.linkURL}?openId="+openId;
	});
	
    $(".trans_list_container").click(function() {
		var limitAmount = $(this).attr("data-limit");
		var transDate = $(this).attr("data-time");
        var nowDate=new Date().toLocaleDateString().replace(/\//g,"-");
        var aDate, oDate1, oDate2, iDays;  
	    aDate = nowDate.split("-");  
	    oDate1 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]);  //转换为yyyy-MM-dd格式  
	    aDate = transDate.split("-");  
	    oDate2 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]);  
	    iDays = parseInt((oDate1 - oDate2) / 1000 / 60 / 60 / 24); //把相差的毫秒数转换为天数
        if(iDays>30){
           $(this).find(".trans_checkbox img").attr("src","<@resource path="images/icon_duoxuan_weixuanze.svg"/>");
           $(this).removeClass("selected");
           $(this).addClass("unable");
           $("#tip").show().delay(3000).fadeOut();
	       $(".three_p").html("Sorry, you can't get a fapiao for a bill over 30 days old.");						
        } 
		if(!$(this).hasClass("unable")){
			if($(this).hasClass("selected")){			
				$(this).removeClass("selected")
				$(this).find(".trans_checkbox img").attr("src","<@resource path="images/icon_duoxuan_weixuanze.svg"/>");
				if($(".trans_list_container.selected").length == 0){
					taxpayerId = "";
					storenumber = "";
					isCard = "";
					updateTrade(taxpayerId);
				}
			}else{
				//从未勾选过
				if("" == taxpayerId){
					//如果是第一次勾选,则给纳税人赋值,用来过滤是否可以合并开票
					taxpayerId = $(this).attr("data-taxpayer-id");
					storenumber = $(this).attr("data-storenumber");
					isCard = $(this).attr("data-card");
					updateTrade(taxpayerId,isCard);
					//添加勾选
					$(this).addClass("selected");
					$(this).find(".trans_checkbox img").attr("src","<@resource path="images/icon_yixuanze.svg"/>");
				}else{
					var amount = 0;	
					if($(".trans_list_container.selected").length >0){
						$(".trans_list_container.selected").each(function(){
			    			var curAmount = parseFloat($(this).attr("data-amount").replace(",",""));
						    amount = amount + curAmount;
			    		});
					}
					var transactionAmount = parseFloat($(this).attr("data-amount").replace(",",""));
					amount = amount + transactionAmount;
					if(amount>limitAmount){
						if(!$(this).hasClass("unable")){
				    		$(this).addClass("unable");
				    		$(this).find(".trans_checkbox img").attr("src","<@resource path="images/icon_duoxuan_weixuanze.svg"/>");
				    		$("#tip").show().delay(3000).fadeOut();
	    	                $(".three_p").html("An e-Fapiao cannot be issued for this many transactions");
						}
						$(this).removeClass("unable");
					}else{
					    //添加勾选
						$(this).addClass("selected");
						$(this).find(".trans_checkbox img").attr("src","<@resource path="images/icon_yixuanze.svg"/>");
					}
				}
			}
		selectCount = $(".trans_list_container.selected").length;
            if(selectCount!=0){
                document.getElementById("list1").innerHTML=selectCount;
            }
		}
	});
	
	//根据纳税人,更新所有的交易是否可以选择
	function updateTrade(taxpayerId,isCard){
		if("" == taxpayerId){
			//纳税人为空,所有未开票的交易都可以被选中
			$(".trans_list_container[data-status='E00501']").each(function(){
    			$(this).removeClass("unable");
    			$(this).find(".trans_checkbox img").attr("src","<@resource path="images/icon_duoxuan_weixuanze.svg"/>");
    			$(this).find(".error").remove();
    		});
		}else{
			//同一个纳税人的交易才可以被选中
			$(".trans_list_container[data-taxpayer-id!='"+taxpayerId+"']").each(function(){
				if(!$(this).hasClass("unable")){
	    			$(this).addClass("unable");
	    			$(this).find(".trans_checkbox img").attr("src","<@resource path="images/icon_duoxuan_weixuanze.svg"/>");
	    			$("#tip").show().delay(3000).fadeOut();
	    	        $(".three_p").html("You Cannot Issue One Invoice For Multiple Taxpayers");
								
				}
    		});

		}
	}
	
	
	
		
    </script>
    <@template id="commonBodyBottom" />
</html>