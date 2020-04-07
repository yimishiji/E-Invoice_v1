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
        <h2 class="tc font44">电子发票申请</h2>
        <div class="in_main">
            <div class="item">
                <input type="" name="" id="" value="" class="font44" placeholder="请输入发票提取码" />
            </div>
            <div class="item code clearfix">
                <input type="" name="" id="" value="" class="font44 fl" placeholder="请输入右侧验证码" />
                <div class="code_rt fr">
                    <img class="code_img" src="../resource/images/code.jpg">
                    <span class="code_refresh"></span>
                </div>
            </div>
            <div id="billing" class=" font44 tc button">开 票</div>
        </div>
    </div>
    <!-- load -->
    <div class="down" id="down"></div>
    <div class="invoice_down tc bra5">
        <img class="invoice_img" src="../resource/images/e-fapiao_img_01.png">
        <i class="close"></i>
        <p class="first_p font44">您的交易数据还没有上传</p>
        <p class="last_p font44">请稍后再试!</p>
    </div>
</div>
</body>
<script>
    var indexBtn=document.getElementById('billing');
    var down=document.getElementById('down');
    var invoice_down=document.getElementsByClassName('invoice_down');
    var close=document.getElementsByClassName('close');
    indexBtn.addEventListener('tap',function(){
        down.style.display='block';
        for (var i=0;i < invoice_down.length; i++) {
            invoice_down[i].style.display='block';
        }
    })

    down.addEventListener('tap',function(){
        down.style.display='none';
        for (var i=0;i < invoice_down.length; i++) {
            invoice_down[i].style.display='none';
        }
    });
    for (var i=0; i<close.length; i++) {
        close[i].addEventListener('tap',function(){
            down.style.display='none';
            for (var i=0;i < invoice_down.length; i++) {
                invoice_down[i].style.display='none';
            }
        })
    }

</script>
</html>
