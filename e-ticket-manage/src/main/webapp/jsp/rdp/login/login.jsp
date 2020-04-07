<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title><%=com.rongzer.rdp.common.service.RDPUtil.getSysConfig("rdp.system.title")%></title>
<link href="${ctx}/resource/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
<link href="${ctx}/resource/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
<link href="${ctx}/resource/css/animate.min.css" rel="stylesheet">
<link href="${ctx}/resource/css/style.css?v=4.0.0" rel="stylesheet">
<style type="text/css">

input {
    width: 400px;
    height: 42px;
    padding: 0 15px;
    background: #2d2d2d; /* browsers that don't support rgba */
    background: rgba(45,45,45,.15);
    -moz-border-radius: 6px;
    -webkit-border-radius: 6px;
    border-radius: 6px;
    border: 1px solid #3d3d3d; /* browsers that don't support rgba */
    border: 1px solid rgba(255,255,255,.15);
    -moz-box-shadow: 0 2px 3px 0 rgba(0,0,0,.1) inset;
    -webkit-box-shadow: 0 2px 3px 0 rgba(0,0,0,.1) inset;
    box-shadow: 0 2px 3px 0 rgba(0,0,0,.1) inset;
    font-family: 'PT Sans', Helvetica, Arial, sans-serif;
    font-size: 14px;
    color: #fff;
    text-shadow: 0 1px 2px rgba(0,0,0,.1);
    -o-transition: all .2s;
    -moz-transition: all .2s;
    -webkit-transition: all .2s;
    -ms-transition: all .2s;
}

input:-moz-placeholder { color: #fff; }
input:-ms-input-placeholder { color: #fff; }
input::-webkit-input-placeholder { color: #fff; }

input:focus {
    outline: none;
    -moz-box-shadow:
        0 2px 3px 0 rgba(0,0,0,.1) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
    -webkit-box-shadow:
        0 2px 3px 0 rgba(0,0,0,.1) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
    box-shadow:
        0 2px 3px 0 rgba(0,0,0,.1) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
}
button {
    cursor: pointer;
    width: 300px;
    height: 44px;
    margin-top: 25px;
    padding: 0;
    background: #60A7DF;
    -moz-border-radius: 6px;
    -webkit-border-radius: 6px;
    border-radius: 6px;
    border: 1px solid #ff730e;
    -moz-box-shadow:
        0 15px 30px 0 rgba(255,255,255,.25) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
    -webkit-box-shadow:
        0 15px 30px 0 rgba(255,255,255,.25) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
    box-shadow:
        0 15px 30px 0 rgba(255,255,255,.25) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
    font-family: 'PT Sans', Helvetica, Arial, sans-serif;
    font-size: 14px;
    font-weight: 700;
    color: #fff;
    -o-transition: all .2s;
    -moz-transition: all .2s;
    -webkit-transition: all .2s;
    -ms-transition: all .2s;
}

button:hover {
    -moz-box-shadow:
        0 15px 30px 0 rgba(255,255,255,.15) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
    -webkit-box-shadow:
        0 15px 30px 0 rgba(255,255,255,.15) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
    box-shadow:
        0 15px 30px 0 rgba(255,255,255,.15) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
        color: #fff;
}

button:active {
    -moz-box-shadow:
        0 15px 30px 0 rgba(255,255,255,.15) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
    -webkit-box-shadow:
        0 15px 30px 0 rgba(255,255,255,.15) inset,
        0 2px 7px 0 rgba(0,0,0,.2);
    box-shadow:        
        0 5px 8px 0 rgba(0,0,0,.1) inset,
        0 1px 4px 0 rgba(0,0,0,.1);

    border: 0px solid #ef4300;
    color: #fff;
}

</style>
</head>
<body>
	<div class="middle-box text-center animated fadeInDown">
        <div>
            <div>

                <h1 style="background:url(resource/image/Login_logo.png);width: 397px;height: 135px;"></h1>

            </div>
            <h3>欢迎使用<%=com.rongzer.rdp.common.service.RDPUtil.getSysConfig("rdp.system.title")%></h3>
            <form id="loginForm">
                <div class="form-group">
                    <input type="text" id="username" class="" placeholder="用户名" required="">
                </div>
                <div class="form-group">
                    <input type="password" id="password" class="" placeholder="密码" required="">
                </div>
                <button type="button" id="loginBtn" class="btn block full-width m-b">登 录</button>
			</form>

                <!-- <p class="text-muted text-center"> <a href="login.html#"><small>忘记密码了？</small></a> | <a href="register.html">注册一个新账号</a>
                </p> -->
        </div>
    </div>
	
	
	
<script type="text/javascript" src="${ctx}/resource/libs/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${ctx}/resource/js/plugins/supersized/supersized.3.2.7.min.js"></script>
<script type="text/javascript" src="${ctx}/resource/js/plugins/supersized/supersized-init.js"></script>
<script src="${ctx}/resource/js/plugins/layer/layer.js"></script>
<script type="text/javascript">
$(function() {
	$("#username").keydown(function(event) {
		if (event.keyCode == 13) {
			login();
		}
	});
	$("#password").keydown(function(event) {
		if (event.keyCode == 13) {
			login();
		}
	});
	
	$("#loginBtn").click(function(){
		login();
	});
	
	
	$('.page-container form .username, .page-container form .password')
			.keyup(function() {
				$(this).parent().find('.error').fadeOut('fast');
			});
			
if (top.location.href != self.location.href) {
    top.location.href=self.location.href;
  }	
$("#username").focus();
});


//登录
function login() {
	var errorMsg = "";
	var loginName = $("#username");
	var password = $("#password");
	//登录处理
	loginUtil($.trim(loginName.val()), password.val(), false);
}
function loginUtil(loginName, password, obj) {
	$.post("${ctx}/login.htm", {
		"username" : loginName,
		"password" : password,
		"relogin" : obj
	}, function(result) {
		result = $.parseJSON(eval('(' + result + ')'));
		if (result == null) {
			layer.alert("登录失败", {title: false,icon: 2,time: 3000});
			return false;
		}
		if (result.status == "true" || result.status == true) {
			window.location = "${ctx}/" + result.url;
		} else {
			if (result.code == "4") {
				layer.alert("当前用户已登录，是否重新登录？", {title: false,icon: 2,time: 3000});
			} else {
				var msg = result.code == "1" ? "用户名或密码不能为空"
						: result.code == "2" ? "没有相关用户" : "用户名或密码不正确";

				layer.alert(msg, {title: false,icon: 2,time: 3000});
			}

		}

	}, "text");
}
</script>
</body>
</html>