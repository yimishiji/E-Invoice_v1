<!doctype html>
<@navigate id="index/invoiceTradeList" var="tradeList"/>
<@navigate id="index/invoiceEnd" var="end"/>
<html>
    <head>
        <@template id="invoiceCommonHeadTop" />
        <title>发票开具</title>
    </head>
    <body class="lang_cn" onload="time()">
        <@template id="commonBodyTop" />
	    <div id="app">
	    	<div class="" >
		    	<div id="headr" >
		    	<div class="header_container">
		    	   <div class="minh">发票开具</div>
		    	   <div class="maxh">悦衡食集电子发票开具</div>	
		    	   <div id="yearday">
		    	   </div>
		    	   </div>			
				</div>
	    	    <!-- 主要区域 -->
	    	    <section id="main" class="main invoice invoiceFail">
	    	    <div>
	    	    		<div class="content" >
	    	    			<div class="box contentsu">
	    	    			<div class="fail">
	    	    			<img style="width:200px;height:200px;" src="<@resource path="images/icon_fail.svg"/>">	    	    				
	    	    			</div>
	    	    	       </div>	
	    	    	       <div class="fail_te">很抱歉，发票失败</div>
	    	    			<div class="fail_te1">请稍后重试或联系门店工作人员<span class="tel" style="font-size:15px;color:#E14E2E"></span></div>	
	    	    	</div>
	    	    	<div class="succ_logo">
	    	    			<img src="<@resource path="images/logo.svg"/>">    	    			
	    	    	</div>	
	    	    </div>		
	    	     </section>
	    	    <@template id="commonLoadAlert" />
	    	</div>
	    </div>
    </body>
    <script type="text/javascript">
     function time(){
		 var Week = ['日','一','二','三','四','五','六'];  
	      var now= new Date();	
		  var year=now.getFullYear();	
		  var month=now.getMonth();	
		  var date=now.getDate();
		  var week=Week[now.getDay()];	
		  document.getElementById("yearday").innerHTML=year+"年"+(month+1)+"月"+date+"日"+"  星期"+week;
    }
        </script>
    <@template id="commonBodyBottom" />
    
</html>