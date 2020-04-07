<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>
<%@ page import="com.rongzer.rdp.common.util.*"%>  
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=9" ></meta>
<title></title>
<%@ include file="/jsp/rdp/commons/basic_lib.jsp" %>
<%@ include file="/jsp/rdp/commons/dialog_libs.jsp" %>
<link href="${ctx}/resource/css/system/main/skin/style.css" rel="stylesheet" type="text/css"/>
<link href="${ctx}/resource/css/system/${skinid}/skin/style.css" rel="stylesheet" type="text/css" id="skin"/>
<script type="text/javascript">	var ctxp = "${ctx}";</script>
<title>内容区域</title>

<%
	String strFuncId = StringUtil.getParameter(request,"funcid");
%>	
</head>
<body> 

 <div id="mainLayout" leftWidth="170">
    <div position="left">
			<div id="lbox">
				<div id="lbox_topcenter">
				<div id="lbox_topleft">
				<div id="lbox_topright">
				</div>
				</div>
				</div>
				<div id="lbox_middlecenter">
				<div id="lbox_middleleft">
				<div id="lbox_middleright">
						<div id="bs_left" style="width:100%;">
						<IFRAME height="100%" width="100%"  frameBorder=0 id=frmleft name=frmleft src="${ctx}/jsp/rdp/main/left.jsp?funcid=<%=strFuncId%>"  allowTransparency="true"></IFRAME>
						</div>
						<!--更改左侧栏的宽度需要修改id="bs_left"的样式-->
				</div>
				</div>
				</div>
				<div id="lbox_bottomcenter">
				<div id="lbox_bottomleft">
				<div id="lbox_bottomright">
					<div class="lbox_foot"></div>
				</div>
				</div>
				</div>
			</div>
    </div>
    <div position="center">
   		<div class="ali01 ver01"  width="100%">
			<div id="rbox">
				<div id="rbox_topcenter">
				<div id="rbox_topleft">
				<div id="rbox_topright">
				</div>
				</div>
				</div>
				<div id="rbox_middlecenter">
				<div id="rbox_middleleft">
				<div id="rbox_middleright">
					<div id="bs_right">
					       <IFRAME height="100%" width="100%" frameBorder=0 id="frmright" name=frmright src=""  allowTransparency="true"></IFRAME>
					</div>
				</div>
				</div>
				</div>
				<div id="rbox_bottomcenter" >
				<div id="rbox_bottomleft">
				<div id="rbox_bottomright">

				</div>
				</div>
				</div>
			</div>
		</div>
    </div>
</div> 
</body> 
</html>
