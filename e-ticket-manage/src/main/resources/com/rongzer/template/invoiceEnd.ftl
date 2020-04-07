<!doctype html>
<html>
    <head>
        <@template id="invoiceCommonHeadTop" />
        <title>发票详情</title>
    </head>
    <body class="lang_cn body_grey" onload="time()">
        <@template id="commonBodyTop" />
	    <div id="app">
	    	<div class="grey" >
		    	<div id="headr" >
		    	<div class="header_container">
		    	   <div class="minh">发票详情</div>
		    	   <div class="maxh">悦衡食集电子发票开具</div>
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
								已开票
							</div>
							<div class="invoice_end_container invoice_padding">
								<div class="invoice_end_line">
									<div class="label_end_title">订单号</div>
									<div class="label_end_value">
										<#list INVOICEOBJ.TRANSACTION_NUMBER as orderNum>
                                			<p>${orderNum}</p>
                            			</#list>
									</div>
								</div>
								<div class="invoice_end_line">
									<div class="label_end_title">开票时间</div>
									<div class="label_end_value">
										${INVOICEOBJ.BILLING_DATE}
									</div>
								</div>
							</div>
							<div class="invoice_end_container invoice_padding">
								<div class="invoice_end_line no_content">
									<div class="label_end_title">收件信息</div>
								</div>
								<div class="invoice_end_line">
									<div class="label_sub_title">电子邮件：</div>
									<div class="label_tip">
										${INVOICEOBJ.EMAIL}
									</div>
								</div>
							</div>
							<#list invoiceList as invoice>	
								<div class="invoice_end_container">
									<div class="invoice_end_line no_content">
										<div class="label_end_title">发票信息</div>
									</div>
									<div class="invoice_end_line">
									 <div class="invoice_end_line_min">
										<div class="label_sub_title">发票抬头：</div>
										<div class="label_tip">
											${INVOICEOBJ.GHFMC}
										</div>
									 </div>
									  <div class="invoice_end_line_min">
										<div class="label_sub_title">手机号：</div>
										<div class="label_tip">
											${INVOICEOBJ.GHFDH}
										</div>
									   </div>
									 <div class="invoice_end_line_min">
										<div class="label_sub_title">销方税号：</div>
										<div class="label_tip">
											${INVOICEOBJ.NSRSBH}
										</div>
									  </div>
									   <div class="invoice_end_line_min">
										<div class="label_sub_title">开户行：</div>
										<div class="label_tip">
											${INVOICEOBJ.BANK}
										</div>
										</div>
									  <div class="invoice_end_line_min">
										<div class="label_sub_title">银行账号：</div>
										<div class="label_tip">
											${INVOICEOBJ.ACCOUNT}
										</div>
									  </div>
									  <div class="invoice_end_line_min">
										<div class="label_sub_title">地址：</div>
										<div class="label_tip">
											${INVOICEOBJ.XHF_DZ}
										</div>
									  </div>
									  <div class="invoice_end_line_min">
										<div class="label_sub_title">电话：</div>
										<div class="label_tip">
											${INVOICEOBJ.XHF_DH}
										</div>
									   </div>
									   <div class="invoice_end_line_min">
										<div class="label_sub_title">金额：</div>
										<div class="label_tip">
											${invoice.TOTAL_AMOUNT_WITHOUT_TAX}
										</div>
									   </div>
									   <div class="invoice_end_line_min">
										<div class="label_sub_title">税额：</div>
										<div class="label_tip">
											${invoice.TOTAL_TAX_AMOUNT}
										</div>
										</div>
										<div class="invoice_end_line_min">
										<div class="label_sub_title">合计：</div>
										<div class="label_tip">
											${invoice.TOTAL_AMOUNT}
										</div>
										</div>
										<div class="invoice_end_line_min">
										<div class="label_sub_title">发票代码：</div>
										<div class="label_tip">
											${invoice.INVOICE_CODE}
										</div>
										</div>
										<div class="invoice_end_line_min">
										<div class="label_sub_title">发票号码：</div>
										<div class="label_tip">
											${invoice.INVOICE_NUMBER}
										</div>
										</div>
									</div>
								</div>
								<div class="invoice_weixin_container" style="display:none;">
							      <div class="weixin" >添加到微信卡包</div>
								</div>
								<div class="clear invoice_end_btn_container" >
									<div class="invoice_end_btn sendEmail"  id="sendEmail" data-invoiceid="${invoice.INVOICE_ID}">
										<img src="<@resource path="images/icon_weixuanze.svg"/>">
									    	  发送到邮箱
									</div>
									<div class="invoice_end_btn download"  id="download" data-href="${invoice.INVOICE_URL}">
										<img src="<@resource path="images/icon_weixuanze.svg"/>">
											下载电子发票
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
	    	    	<div class="close1" style="margin-left: 26%;margin-top: 10px;margin-bottom: 18px;position: static;">我知道了</div>
	    	    </div>
	    	    </div>
	    	    <!-- send -->
			   <div class="invoice_down invoice_down1 invoice_down_top tc">
			   <div class="send_margin">
			    	<p class="first_p">我的邮箱</p>			    	
			    	<div>
			    		<input id="newEmail" class="newEmail"  placeholder="请输入我的常用邮箱" value=""/>
			    	</div>
			    	<div class="btn_end">
			    	  <div id="quxiao" class="quxiao">取消</div>
				      <div id="send" class="sendqueren">确认发送</div>
				     </div>
			    	<br/>
			    	</div>
			    </div>
			    
			    <div class="invoice_down invoice_down2 invoice_down_top tc">
			    <div class="send_margin">			    	
			    	<p class="last_p" id="newEmailInputTitle">您的电子发票已经发送到</p>
			    	<p class="last_p" style="color:#E14E2E;margin-bottom: 20px;" id="newEmailInput"></p>
			    	<div class="close1" style="margin-left: 26%;margin-top: 10px;margin-bottom: 18px;position: static;">我知道了</div>
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
		 var Week = ['日','一','二','三','四','五','六'];  
	      var now= new Date();	
		  var year=now.getFullYear();	
		  var month=now.getMonth();	
		  var date=now.getDate();
		  var week=Week[now.getDay()];	
		  document.getElementById("yearday").innerHTML=year+"年"+(month+1)+"月"+date+"日"+"  星期"+week;

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
    			alert("邮箱不能为空");
    			return;
    		}
    		var mailFilter  = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    		if (!mailFilter.test(newEmail)){
    			$(".invoice_down1").hide();
				alert("邮箱格式不正确！");
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
							$("#newEmailInputTitle").text("您的电子发票已经发送到");
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
    		$(this).html("已添加到微信卡包");
    	});
    	
    	
    </script>
    <@template id="commonBodyBottom" />
</html>