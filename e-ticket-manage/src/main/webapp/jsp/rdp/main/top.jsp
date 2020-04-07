<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=9"></meta>
<title></title>
<%@ include file="/jsp/rdp/commons/basic_lib.jsp"%>
<%@ include file="/jsp/rdp/commons/dialog_libs.jsp"%>
<script type="text/javascript" src="${js}/rdp/main/main.js"></script>

<link href="${ctx}/resource/css/system/main/skin/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/resource/css/system/${skinid}/skin/style.css" rel="stylesheet" type="text/css" id="skin"/>

<script type="text/javascript">
	var ctxp = "${ctx}";
	
	function toFullScreen()
	{
		if(window.parent.sss.rows=="126,*"){
			window.parent.sss.rows="65,*";
			$("#fullSrceen").removeClass("icon_fullscreen");
			$("#fullSrceen").addClass("icon_actualscreen");
			$("#fullSrceen").text("退出全屏");
		}
		else{
			window.parent.sss.rows="126,*";
			$("#fullSrceen").removeClass("icon_actualscreen");
			$("#fullSrceen").addClass("icon_fullscreen");
			$("#fullSrceen").text("开启全屏");
		}
	} 
</script>
<style type="text/css">
body{
  font-size:12px;
  line-height:180%;
  font-family:"宋体";
}
</style>
</head>

<body>
	<div id="mainFrame">
		<!--头部与导航start-->
		<div id="hbox">
			<div id="bs_bannercenter">
				<div id="bs_bannerright">
					<div id="bs_bannerleft"></div>
				</div>
			</div>
			<div id="bs_navcenter">
				<div id="bs_navleft">
					<div id="bs_navright">
						<div class="bs_nav">

							<div class="float_left padding_top2 padding_left5">
								尊敬的
								<c:out value="${LoginUser.loginUserName }"></c:out>
								，欢迎登录系统！【今天是 <span id="now_time"></span>】
							</div>
							<div class="float_left" style="padding: 2px 0 0 20px;"
								id="positionContent"></div>
							<div class="float_right padding_top2 padding_right5">
								<a href="javascript:;" onclick="backMenuIndex('mainframe');return false;"><span
									class="icon_home hand">我的工作台</span></a> <span
									class="icon_fullscreen hand" id="fullSrceen" hideNav="true" onclick="toFullScreen()" >开启全屏</span>
								<a href="javascript:void(0);" id="modifyPwd"><span
									class="icon_key">修改密码</span></a> <span class="icon_exit"
									id="exitHandler" style="cursor: pointer;">退出系统</span>
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>
			</div>

			<div class="box_tool_min">
				<div class="center">
					<div class="left">
						<div class="right">

							<div class="padding_top5 padding_left10">
								<c:forEach items="${TopMenu}" var="top" varStatus="stat">
									<a href="${ctx}/${top.REQUEST_URL}" target="mainFrame"><span
										class="${top.CLASS_NAME }">${top.FUNC_NAME}</span></a>
									<c:if test="${!stat.last}">
										<div class="box_tool_line"></div>
									</c:if>
								</c:forEach>

								<div class="clear"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="clear"></div>
			</div>

		</div>
	</div>
		<!--头部与导航end-->
</body>
</html>
