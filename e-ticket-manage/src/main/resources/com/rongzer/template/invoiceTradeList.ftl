<!doctype html>
<@navigate id="index/invoiceIndex" var="invoiceIndex"/>
<@navigate id="index/invoiceWrite" var="invoiceWrite" cacahetime="0"/>
<html>
<head>
<@template id="invoiceCommonHeadTop" />
    <title>交易信息</title>
</head>
<body class="lang_cn" onload="time()">
<input type="text" id="isFresh" value="true" style="display:none">
<@business id="billingService" var="result" method="getTradeInfos" cachetime="0"/>
<div id="app">
    <div class="grey" >
        <div id="headr" >
            <div class="header_container">
                <div class="maxh" id="maxh">悦衡食集电子发票开具</div>
			<@business id="billingService" var="result" method="getTradeInfos" cachetime="0"/>
                <div class="minh">交易信息</div>
                <div id="yearday">
                </div>
            </div>
        </div>
        <!-- 主要区域 -->
        <section id="main" class="main invoice ">
            <div>
			<#assign fapiaoInfos=result.tradeinfos>
                <div class="content content_top">
				<#if fapiaoInfos?size gt 0>
					<#list fapiaoInfos as fapiaoInfo>
                        <div class="trans_list_container <#if fapiaoInfo.INVOICE_STATUS != 'E00501' || fapiaoInfo.ALLOWED_INVOICE != 'E01101' || fapiaoInfo.TAXPAYER_ID == '' ||fapiaoInfo.DEAL_AMOUNT?number lte 0 ||fapiaoInfo.IS_FORBINDDEN == 'YES'> unable</#if>" id="checkbox"  data-code="${fapiaoInfo.TRANSACTION_NUMBER}" data-storenumber="${fapiaoInfo.STORE_NUMBER}" data-card="${fapiaoInfo.isCard}" data-dealamount="${fapiaoInfo.DEAL_AMOUNT?number}" data-isforbidden="${fapiaoInfo.IS_FORBINDDEN}" data-taxpayer-id="${fapiaoInfo.TAXPAYER_ID}" data-status="${fapiaoInfo.INVOICE_STATUS}" data-merge="${fapiaoInfo.IS_MERGE}" data-amount="${fapiaoInfo.TRANSACTION_AMOUNT?number}" data-limit="${fapiaoInfo.INVOICE_LIMIT_AMOUNT}">
                            <ul class="trans_list trade_listContent">

								<#if fapiaoInfo.INVOICE_STATUS == 'E00502'|| fapiaoInfo.INVOICE_STATUS == 'E00503'|| fapiaoInfo.INVOICE_STATUS == 'E00504'|| fapiaoInfo.INVOICE_STATUS == 'E00505'||fapiaoInfo.DEAL_AMOUNT?number lte 0 ||fapiaoInfo.IS_FORBINDDEN == 'YES'>
                                    <li>
                                        <div class="checkbox trans_checkbox" style="display: none;">
                                            <input type="checkbox" id="checkbox3" />

                                        </div>
                                        <div class="trans_info">
                                            <div class="trans_info_content trade_info_Content">
                                                <div class="content_line trade_listContent_line">
                                                    <b class="trans_time color_grey">${fapiaoInfo.TRANSACTION_DATETIME?substring(0,4)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(5,7)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(8,10)} ${fapiaoInfo.TRANSACTION_DATETIME?substring(11,13)}:${fapiaoInfo.TRANSACTION_DATETIME?substring(14,16)}:${fapiaoInfo.TRANSACTION_DATETIME?substring(17,19)}</b>
                                                    <b class="trans_amount_grey color_grey">${fapiaoInfo.TRANSACTION_AMOUNT?number}元</b>
                                                </div>
                                                <div class="content_line trade_listContent_line">
                                                    <b class="trans_location color_grey">${fapiaoInfo.STORE_NAME_CN}</b>
													<#if fapiaoInfo.INVOICE_STATUS == 'E00502'>
                                                        <a class="trans_status" id="trans_status">开票中</a>
													<#elseif fapiaoInfo.INVOICE_STATUS == 'E00503'>
                                                        <a class="trans_status" id="trans_status">正在下载</a>
													<#elseif fapiaoInfo.INVOICE_STATUS == 'E00504'>
                                                        <a class="trans_status" id="trans_status">开票失败</a>
													<#elseif fapiaoInfo.INVOICE_STATUS == 'E00505'>
                                                        <a class="trans_status" id="trans_status">已开票</a>
													<#elseif fapiaoInfo.IS_FORBINDDEN == 'YES'>
                                                        <a class="trans_status" id="trans_status">不可开票</a>
													<#elseif fapiaoInfo.DEAL_AMOUNT?number== 0>
                                                        <a class="trans_status" id="trans_status">可开票金额为0</a>
													<#elseif fapiaoInfo.DEAL_AMOUNT?number lt 0>
                                                        <a class="trans_status" id="trans_status">可开金额为负</a>
													</#if>

                                                </div>
                                            </div>
                                        </div>
                                    </li>
								<#else>
                                    <li>
                                        <div class="checkbox trans_checkbox" >
                                            <img src="<@resource path="images/icon_duoxuan_weixuanze.svg"/>">

                                        </div>
                                        <div class="trans_info">
                                            <div class="trans_info_content trade_info_Content">
                                                <div class="content_line trade_listContent_line">
                                                    <b class="trans_time">${fapiaoInfo.TRANSACTION_DATETIME?substring(0,4)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(5,7)}-${fapiaoInfo.TRANSACTION_DATETIME?substring(8,10)} ${fapiaoInfo.TRANSACTION_DATETIME?substring(11,13)}:${fapiaoInfo.TRANSACTION_DATETIME?substring(14,16)}:${fapiaoInfo.TRANSACTION_DATETIME?substring(17,19)}</b>
                                                    <b class="trans_amount"><span style="color:#E14E2E">${fapiaoInfo.TRANSACTION_AMOUNT?number}</span></b>
                                                </div>
                                                <div class="content_line trade_listContent_line">
                                                    <b class="trans_location">${fapiaoInfo.STORE_NAME_CN}</b>
                                                </div>
                                            </div>
                                        </div>
                                    </li>
								</#if>
                            </ul>
                        </div>
					</#list>
				</#if>
                </div>
                <footer class="footer footer_pos">
                    <div class="order_number">
                        <span style="color:#E14E2E" id="list1">0</span>
                    </div>
                    <div class="footer_container">
                        <a href="javascript:;" class="btn orange" id="next">下一步</a>
                        <a href="javascript:;" class="btn" id="add">添加交易</a>
                    </div>
                </footer>

            </div>
        </section>
        <!-- load -->
        <div class="down"></div>
        <div class="tip" id="tip">
            <ul>
            </ul>
            <p class="three_p"></p>
        </div>
	<@template id="commonLoadAlert" />

    </div>
</div>
</body>
<script type="text/javascript">
    var taxpayerId = "";
    var storenumber = "";
    var isCard = "";
    var selectCount = $(".trans_list_container.selected").length;
    document.getElementById("list1").innerHTML=selectCount;
    function time(){
        var Week = ['日','一','二','三','四','五','六'];
        var now= new Date();
        var year=now.getFullYear();
        var month=now.getMonth();
        var date=now.getDate();
        var week=Week[now.getDay()];
        document.getElementById("yearday").innerHTML=year+"年"+(month+1)+"月"+date+"日"+"  星期"+week;
    }

    var isFresh = $("#isFresh").val();
    if(isFresh == "false"){
        window.location.reload();
    }

    $("#next").click(function(event) {
        $("#isFresh").val("false");
        var list_select = $(".trans_list_container.selected");
        var selectCount = list_select.length;
        if(selectCount > 0){
            var extrCodes = new Array();
            list_select.each(function(){
                extrCodes.push($(this).attr("data-code"));
            });
            if(extrCodes.length>0){
                var params = {};
                params.method = "mergeApply";
                params.EXTRCODES = JSON.stringify(extrCodes);
                params.isCard = isCard;
                businessExec("billingService",params,function(data){
                    if(data.suc){
                        var randomNum = Math.random();
                        window.location.href = "${invoiceWrite.linkURL}?randomNum="+randomNum;
                    }
                });
            }
        }else{
            $("#tip").show().delay(3000).fadeOut();
            $(".three_p").html("请选择一条记录");

        }
    });
    $("#add").click(function(event) {
        window.location.href = "${invoiceIndex.linkURL}"+"?isAddMerge=yes";
    });
    $(".trans_list_container").click(function() {
        var limitAmount = $(this).attr("data-limit");

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
                            $(".three_p").html("勾选的交易金额超过开票限制金额");
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
            document.getElementById("list1").innerHTML=selectCount;
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
                    $(".three_p").html("选择了不同的纳税人，不允许合并开票");
                }
            });

        }
    }
</script>
<@template id="commonBodyBottom" />
</html>