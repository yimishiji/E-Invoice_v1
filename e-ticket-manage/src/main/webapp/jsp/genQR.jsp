<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.rongzer.efapiao.util.UrlGenerateUtil" %>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp"%>
<!DOCTYPE>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Generate QRCode</title>
		<script src="resources/js/jquery-1.7.2.min.js"></script>
		<script src="resources/js/jquery.qrcode.min.js"></script>
	</head>
	<body>
		<form action="genQR.jsp">
			webURL:<input type="text" name="webUrl" id="webUrl"/>
			<br/>
			json:<input type="text" name="json" id="json"/>
			<br/>
			appId:<input type="text" name="appId" id="appId"/>
			<br/>
			appSecret:<input type="text" name="appSecret" id="appSecret"/>
			<br/>
			<input type="submit" value="生成">
		</form>
		<%
			String webUrl = request.getParameter("webUrl");
			String json = request.getParameter("json");
			String appId = request.getParameter("appId");
			String appSecret = request.getParameter("appSecret");
			String url = UrlGenerateUtil.generateUrlByDeflater(webUrl, json, appId, appSecret);
		%>
		<div>
			url:<%=url %>
		</div>
		<div id="qrOutput">
		</div>
		<%
			if(null!=url && !url.equals(""))
			{
		%>
			<script type="text/javascript">
				jQuery("#qrOutput").qrcode("<%=url %>");
			</script>
		<%
			}
		%>
	</body>
</html>