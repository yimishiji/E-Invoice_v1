<!doctype html>
<@navigate id="index/invoiceIndex_en" var="invoiceIndex_en"/>
<@navigate id="index/invoiceEnd_en" var="invoiceEnd_en"/>
<@navigate id="index/invoiceWrite_en" var="invoiceWrite_en" cacahetime="0"/>
<html>
     <head>
        <@template id="invoiceCommonHeadTop" />
        <title>History</title>
    </head>
    <body class="lang_cn body_grey" >
    <@business id="billingService" var="result" method="getInvoiceInfosByOpenId" cachetime="0"/>
       <@template id="commonBodyTop" />
	    <div id="app">
	    	<div class="grey" >
		    	<div id="headr" >
		    	<div class="header_container">
		    	  <div class="minh">History</div>		
				</div>
				</div>
	    	    <!-- 主要区域 -->
	    	    <section id="main" class="main invoice ">
	    	    	<div>
	    	    		<div class="content" >
	    	    			<#assign tradeinfoMap=result.tradeinfos>
	    	    				<#if tradeinfoMap?size gt 0>
	    	    				<#list tradeinfoMap as invoiceInfo>	 	 	            
											<div class="trans_list_container" style="margin-top:10px;margin-bottom: 10px;" data-code="${invoiceInfo.ORDER_ID}" data-time="${invoiceInfo.BILLING_DATE}" data-amount="${invoiceInfo.TOTAL_AMOUNT}">
			    	    						<ul class="trans_list_his">
					                               <li>
					                              <div class="trans_info_his">
													<div class="trans_info_content">
														<div class="content_line">
															<b class="trans_time">${invoiceInfo.BILLING_DATE}</b>
															<img id="trans_his_img" class="trans_his_img" src="<@resource path="images/icon_arrow.svg"/>">
															<b class="trans_his_yikaipiao" style="margin-left: 5%;">Invoice Issued</b>
														</div>
														<div class="content_line">
															<#if invoiceInfo.recordFlag ==  0>
                                                    			<b class="trans_order">${invoiceInfo.TRANSACTION_NUMBER}</b>
															<#elseif invoiceInfo.recordFlag ==  1>
                                                    			<b class="trans_order">Multiple Orders</b>
															</#if>
														</div>
														<div class="content_line_his">
															<b class="trans_his_amount">￥<span style="color:#E14E2E">${invoiceInfo.TOTAL_AMOUNT}</span></b>
														</div>
													</div>
												</div>
											</li>
											</ul>
			    	    					</div>
	    	    						</#list>
			    	    			<#else>
			    	    			   <div class="history_none">No Invoice History</div>	     	    	
	    	    				</#if>
	    	    			</div>
	    	    			</div>				    	
	    	    </section>	    	    
	    	    <@template id="commonLoadAlert" />	    	    
	    	</div>
	    </div>
    </body>
    <script type="text/javascript">
	   var openId = getUrlParam("openId");   
	    function getUrlParam(name) {
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
			var r = window.location.search.substr(1).match(reg);
			if (r != null)
				return unescape(r[2]);
			return null;
		}
	    $(".trans_list_container").click(function(event) {
		    var params = {};
		    var ORDER_ID = $(this).attr("data-code");
			params.ORDER_ID = ORDER_ID;
			params.method = "addKeyToCaChe";
			businessExec(
				"billingService",
				params,
				function(data){
					if(data.suc){
						window.location.href= "${invoiceEnd_en.linkURL}";
					}
				});
	   });
    </script>
    <@template id="commonBodyBottom" />
</html>