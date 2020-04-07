<!doctype html>
<@navigate id="index/invoiceTradeList" var="tradeList"/>
<@navigate id="index/invoiceEnd" var="end"/>
<html>
    <head>
        <@template id="invoiceCommonHeadTop" />
         <title>悦衡食集电子发票开具</title>
    </head>
    <body class="lang_cn" onload="time()">
        <@template id="commonBodyTop" />
	    <div id="app">
	    	<div>    	
		    	<div id="headr" >
		    	<div class="header_container">
		    	   <div class="minh">悦衡食集电子发票开具</div>
		    	   <div class="maxh">悦衡食集电子发票开具</div>
		    	   <div id="yearday"></div>
		    	   </div>	
				</div>
	    	    <!-- 主要区域 -->
	    	        <!-- 中英文切换按钮 -->
    	    		<div class="togBtn">
    	    			<a href="../invoiceIndex/index.htm?" class="now">中文&nbsp;</a>/
    	    			<a href="../invoiceIndex_en/index.htm?" class="">English</a>
	    			</div>
	    	    <section id="main" class="main invoice invoiceIndex">
	    	    	<div>
	    	    		<div class="content">
			<div class="input_container">
				<div class="input_box">
					<div id="hot1" class="hint">
						发票提取码
					</div>
					<input id="PICKUPCODE" type="text" class="input_text" placeholder="请输入发票提取码" onkeyup="isPickup()"/>
				</div>
				<div class="input_box">
					<div id="hot2" class="hint">
						验证码
					</div>
					<input id="IDENTIFYCODE" type="text" class="input_text" placeholder="请输入右侧验证码"  name="" autocomplete="no" maxlength="4" value="" onkeyup="isCode()"/>
					<b class="line" id="code_rt"></b>
					<img class="ver_code" src="">
    	    			<a href="javascript:;">
						   <img src="<@resource path="images/icon_refresh.svg"/>" class="refresh" />
					    </a>
				</div>
				<a href="javascript:;" class="confirm_invoice" id="queren" style="cursor:pointer;">确认开票</a>
			</div>
		</div>	    	    		
	    	    	</div>
	    	    </section>
	    	    <!-- load -->
				 
				 <div class="tip">
				    <p class="three_p"></p>
				  </div>
				<@template id="commonLoadAlert" />
	    	</div>
	    </div>
    </body>
    <script type="text/javascript">
    	var num = 0;
		var interval;
		var isAddMerge = getUrlParam("isAddMerge");
		 function time(){
		 var Week = ['日','一','二','三','四','五','六'];  
	      var now= new Date();	
		  var year=now.getFullYear();	
		  var month=now.getMonth();	
		  var date=now.getDate();
		  var week=Week[now.getDay()];	
		  document.getElementById("yearday").innerHTML=year+"年"+(month+1)+"月"+date+"日"+"  星期"+week;
    }
    
    function isPickup() { 
      var PICKUPCODE = $("#PICKUPCODE").val();
      var IDENTIFYCODE = $("#IDENTIFYCODE").val();
        PICKUPCODE = PICKUPCODE.replace(/\s/g,'');
        $("#PICKUPCODE").val(PICKUPCODE);
      if(PICKUPCODE!=""&&IDENTIFYCODE!=""){
			  document.getElementById("queren").style.backgroundColor="#E14E2E";
			}else{
			  document.getElementById("queren").style.backgroundColor="#DDDDDD";
			}
    }
    
    function isCode() { 
       var PICKUPCODE = $("#PICKUPCODE").val();
	   var IDENTIFYCODE = $("#IDENTIFYCODE").val();
	   if(PICKUPCODE!=""&&IDENTIFYCODE!=""){
			document.getElementById("queren").style.backgroundColor="#E14E2E";
			}else{
			 document.getElementById("queren").style.backgroundColor="#DDDDDD";
		}
    }
    $("#PICKUPCODE").focus(function() {
			$("#hot1").fadeIn();
			$(".tip").hide();
			
		});
		$("#IDENTIFYCODE").focus(function() {
			$("#hot2").fadeIn();
			$(".tip").hide();
			
		});
    
    	//刷新验证码
    	function getVerificationCode(){
			var randomNum = Math.random();
			$('.ver_code').attr('src','validatecodecontroller/getRandcode.htm?randomNum='+randomNum);
		}
		//	获取验证码
        getVerificationCode()
		//刷新验证码点击事件
    	$(".refresh").click(function(event) {
    		getVerificationCode();
    	});
    	
    	//开票点击事件
    	$("#queren").click(function(event) {
    		$(".down_rgb,.l-wrapper").show();
    	    document.getElementById("queren").style.backgroundColor="#DDDDDD";
    		var PICKUPCODE = $("#PICKUPCODE").val().replace(/\s+/g,"");
    		var IDENTIFYCODE = $("#IDENTIFYCODE").val();
    		if(PICKUPCODE!=""&&IDENTIFYCODE!="")
    		  document.getElementById("queren").style.backgroundColor="#E14E2E";
    		if(PICKUPCODE == ""){
    			var data= {
    				msg:"pickupCodeEmpty"
    			}
    			pageIntent(data);
    			return;
    		}
    		if(PICKUPCODE.length !=14 && PICKUPCODE.length !=28){
    			var data= {
    				msg:"pickupCodeError"
    			}
    			pageIntent(data);
    			return;
    		}
    		
    		if(IDENTIFYCODE == ""){
    			var data= {
    				msg:"identifyCodeEmpty"
    			}
    			pageIntent(data);
    			return;
    		}
			var params = {};
			params.method = "billing";
			params.PICKUPCODE = PICKUPCODE;
			params.IDENTIFYCODE = IDENTIFYCODE;
			businessExec(
				"billingService",
				params,
				function(data){
					getVerificationCode();
					if(data.suc){
						timer(PICKUPCODE);
					}else{
						pageIntent(data);
					}
				});
			
			
    	});
    	
    	//根据提取码获取交易信息
    	function timer(pickupCode) {
			//判断计数 超过上限停止心跳
			var params = {};
			if(num<5){
	    		params.method = "posDatatimer";
	    		params.PICKUPCODE = pickupCode;
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
							if(data.suc){
								pageIntent(data);
							}else{
								//需要计数
								num = num+1;
								if(num >= 5){
									timer(pickupCode);
								}else{
									setTimeout("timer('"+pickupCode+"')",1000)
								}
							}
						}
					},
					error : function(XMLHttpRequest, textStatus, errorThrown){
						//需要计数
						num = num+1;
						if(num >= 5){
							timer(pickupCode);
						}else{
							setTimeout("timer('"+pickupCode+"')",1000)
						}
					}
				});
			}else{
				var data ={};
				data.msg="noPosData";
				pageIntent(data);
			}
		}
		
	
    	function pageIntent(returnMap){
			num = 0;
			$(".first_p").html("");
			$(".last_p").html("");
			if(returnMap.msg=="noPosData"){
			    $(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("交易数据未上传");				
			}else if(returnMap.msg=="pickupCodeEmpty"){
				 $(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("未输入提取码");
			}else if(returnMap.msg=="identifyCodeEmpty"){
				$(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("未输入验证码");
			}else if(returnMap.msg=="errCode"){
				$(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("验证码输入错误");
			}else if(returnMap.msg=="invoiceError"){
				$(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("税局系统异常，请稍后再试");
			}else if(returnMap.msg=="invoiceIng"){
				$(".down_rgb,.l-wrapper").hide();
				$("#alreadyRequested").show();
			}else if(returnMap.msg=="noInvoiceToApply"){
				$(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("根据相关法规，该类交易不能开具发票！");
			}else if(returnMap.msg=="pickupCodeError"){
				$(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("提取码输入错误");
			}else if(returnMap.msg=="systemError"){
				$(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("系统异常！");
			}else if(returnMap.msg=="invoiceSucess"){
				$(".down_rgb,.l-wrapper").hide();
				window.location.href= "${end.linkURL}";
			}else if(returnMap.msg=="transactionSucess"){
				$(".down_rgb,.l-wrapper").hide();
				window.location.href = "${tradeList.linkURL}";
			}else if(returnMap.msg=="outOfDate"){
				$(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("抱歉，订单超过30天，不支持开票");
			}else if(returnMap.msg=="invoiceManualProcessing"){
				$(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("交易在手工开票中，如需发票请联系客服！");
			}else if(returnMap.msg=="timeTooEarly"){
				$(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("您的交易数据还没有上传");
			}else if(returnMap.msg=="pickupCodeForbidden"){
                $(".down_rgb,.l-wrapper").hide();
				$(".tip").show().delay(3000).fadeOut();
	    		$(".three_p").html("提取码被禁用");
            }
             
		}
	
	
		function getUrlParam(name) {
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
			var r = window.location.search.substr(1).match(reg);
			if (r != null)
				return unescape(r[2]);
			return null;
		}
    </script>
    <@template id="commonBodyBottom" />
</html>