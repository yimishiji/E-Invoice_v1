<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>首页</title>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <script type="text/javascript" src="<@resource path="js/rem.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<@resource path="plugins/mui/css/mui.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<@resource path="css/base.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<@resource path="css/style.css"/>"/>
    <script type="text/javascript" src="<@resource path="plugins/mui/js/mui.js"/>"></script>
    <script type="text/javascript" src="<@resource path="js/mon.js"/>"></script>
</head>
<body>
<header class="mui-bar mui-bar-nav nav">
    <h1 class="mui-title font48">E-FAPIAO</h1>
    <a class="noticeIcon" id="noticeIcon"></a>
    <div class="noticeCon" id="noticeCon">
        <i></i>
        <header class="noticeConHd tc font44">关于变更电子发票的公告</header>
        <div class="noticeMain font36">
            <p class="conHd">尊敬的顾客：</p>
            <p class="paragraph">根据《关于推行通过增值税电子发票系统开具的增值税电子普通发票有关问题的公告》
                （国家税务总局公告2015年第84号），我司已逐步推行增值税电子普通发票。如顾客需要纸质发票，可以自行打印增值税电子普通发票的版式文件，
                其法律效力、基本用途、基本使用规定等与税务机关监制的增值税普通发票相同。</p>
            <p class="paragraph">您可通过扫描收银条下方的二维码或输入电子发票提取码的方式开具增值税电子普通发票。如您对电子发票的开具有任何疑问，请致电收银条上的电子发票服务热线垂询。</p>
            <p class="conFt">2016年12月12日</p>
        </div>
    </div>
</header>
<script>
    var url = "";
    <@navigate id="index/index_detail" var="index_detail" />
    <@navigate id="index/error" var="error"/>
    <@navigate id="index/index_input" var="index_input" />

    <@business id="requestService" var="order" method="dealRequest" ORDER_ID="${request.detail_ORDER_ID}" />
    <#if order.STATUS=="E01101">
    url = "${index_detail.linkURL}";
    <#elseif order.STATUS=="E01102">
    url = "${error.linkURL}?errorMsg=${order.ERROR_MSG}";
    <#else>
    url = "${index_input.linkURL}";
    </#if>
    mui.init({
        subpages: [{
            url: url,//子页面HTML地址，支持本地地址和网络地址
            id: 'index',//子页面标志
            styles: {
                top: '1.6rem',//子页面顶部位置
                bottom: '0',//子页面底部位置
                //width:'100%',//子页面宽度，默认为100%
                //height:'100%',//子页面高度，默认为100%
            }
        }]
    });
</script>
</body>
</html>
