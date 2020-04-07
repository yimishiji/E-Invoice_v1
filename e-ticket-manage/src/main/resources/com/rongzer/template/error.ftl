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
<div class="mui-content index container" id="index">
    <!--main-->
    <div class="main font50">
        <div class="in_main">
            <!--fail-->
            <div class="invoiceFail tc">
                <img src="<@resource path="images/e-fapiao_img_03.png"/>">
                <div class="text font44">
                    <p class="">${request.errorMsg}</p>
                    <p class="">请稍后重试或联系客服 <span class="tel hot">025-52360632</span></p>
                </div>
            </div>
        </div>
    </div>
    <!-- load -->
    <div class="down" id="down"></div>
</div>
</body>
</html>