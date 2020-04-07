<!doctype html>
<@navigate id="index/invoiceTradeList_en" var="tradeList_en"/>
<@navigate id="index/invoiceEnd_en" var="end_en"/>
<html>
    <head>
        <@template id="invoiceCommonHeadTop" />
         <title>E-Invoice</title>
    </head>
    <body class="lang_cn" onload="time()">
        <@template id="commonBodyTop" />
	    <div id="app">
	    	<div class="" >
		    	<div id="headr" >
		    	<div class="header_container">
		    	   <div class="minh">E-Invoice</div>
		    	   <div class="maxh">HG E-Invoice</div>	
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
	    	    	       <div class="fail_te">Sorry, your e-invoice could not be issued.</div>
	    	    			<div class="fail_te1">Please try again later or contact the shop staff<span class="tel" style="font-size:15px;color:#E14E2E"></span></div>	
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
		  var Week = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];  
	      var now= new Date();	
		  var year=now.getFullYear();
		  var Month=['January','February','March','April','May','June','July','August','September','October','November','December'];	
		  var month=Month[now.getMonth()];	
		  var date=now.getDate();
		  var week=Week[now.getDay()];	
		  document.getElementById("yearday").innerHTML=month+"&nbsp;"+date+",&nbsp;&nbsp;"+year+"&nbsp;&nbsp;&nbsp;"+week;
       }
        </script>
    <@template id="commonBodyBottom" />
    
</html>