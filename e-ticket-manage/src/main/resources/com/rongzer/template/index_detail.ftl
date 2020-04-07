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
    <div class="mui-content main">
        <h2 class="tc font44">填写发票信息</h2>
        <div class="in_main">
            <!-- 展示 -->
            <ul class="invoiceInfo font38">
                <li class="clearfix">
                    <p class="fl">发票金额</p>
                    <p class="fr color_83">￥70</p>
                </li>
                <li class="clearfix">
                    <p class="fl">交易时间</p>
                    <p class="fr color_83">2016年10月26日 10:37</p>
                </li>
            </ul>
            <form class="form" >
                <label class="hot font44">*号为必填</label>
                <div class="item first form_required">
                    <input type="text" placeholder="请输入发票抬头（个人/企业名称）" name="" autocomplete="no" maxlength="" >
                </div>
                <div class="item sec_item form_required last_item">
                    <input type="text" placeholder="请输入常用邮箱" name="" autocomplete="no" maxlength="">
                </div>
                <div class="form_add" id="form_add">
                    <div class="item">
                        <input type="text" placeholder="请输入您的手机号" name="" autocomplete="no" maxlength="">
                    </div>
                    <div class="item">
                        <input type="text" placeholder="请输入纳税人识别号" name="" autocomplete="no" maxlength="">
                    </div>
                    <div class="item">
                        <input type="text" placeholder="请输入地址" name="" autocomplete="no" maxlength="">
                    </div>
                    <div class="item">
                        <input type="text" placeholder="请输入电话" name="" autocomplete="no" maxlength="">
                    </div>
                    <div class="item">
                        <input type="text" placeholder="请输入开户行" name="" autocomplete="no" maxlength="">
                    </div>
                    <div class="item last_item">
                        <input type="text" placeholder="请输入开户行账号" name="" autocomplete="no" maxlength="">
                    </div>
                </div>
                <div class="add_form hot font44 tc">
                    <span class="add_form_text" id="add_form_text">展开纳税人信息</span>
                    <span class="add_form_icon"></span>
                </div>

                <div class="button tc button_confirm full-width">确认提交</div>
            </form>
        </div>
    </div>
</div>
</body>
<script>
    var down=document.getElementById('down');
    var add_form=document.getElementsByClassName('add_form')[0];
    var form_add=document.getElementById('form_add');
    var sec_item=document.getElementsByClassName('sec_item');
    var add_form_text=document.getElementById('add_form_text');
    var add_form_icon=document.getElementsByClassName('add_form_icon');
    var close=document.getElementsByClassName('close');
    var button_confirm=document.getElementsByClassName('button_confirm')[0];
    var confirm=document.getElementsByClassName('confirm')[0];
    var confirm_two=document.getElementsByClassName('confirm_two')[0];
    form_add.style.display='none';

    add_form.addEventListener('tap',function(){
        if (form_add.style.display=='none') {
            for (var i=0; i<sec_item.length; i++) {
                sec_item[i].classList.remove('last_item');
            }
            add_form_text.innerText='收起纳税人信息';
            for (var i = 0; i < add_form_icon.length; i++) {
                add_form_icon[i].classList.add('turn');
            }
            form_add.style.display='block';
        } else{
            add_form_text.innerText='展开纳税人信息';
            form_add.style.display='none';
            for (var i = 0; i < add_form_icon.length; i++) {
                add_form_icon[i].classList.remove('turn');
            }
            for (var i=0; i<sec_item.length; i++) {
                sec_item[i].classList.add('last_item');
            }
        }
    })

    function downHide(){
        down.style.display='none';
    }
    function downShow(){
        down.style.display='block';
    }
    //button_confirm
    button_confirm.addEventListener('tap',function(){
        downShow();
        confirm.style.display='block';
    })

    //close
    for (var i=0; i<close.length; i++) {
        close[i].addEventListener('tap',function(){
            downHide()
            confirm_two.style.display='none';
        })
    }
    down.addEventListener('tap',function(){
        confirm_two.style.display='none';
        downHide();
        confirm.style.display='none';
    })
</script>
</html>