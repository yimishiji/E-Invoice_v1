<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>
<%@ page import="com.rongzer.rdp.common.util.*"%>  
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%@ include file="/jsp/rdp/commons/index_libs.jsp" %>
<%@ include file="/jsp/rdp/commons/ztree_libs.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/libs/js/nav/treeAccordion_normal.js"></script>
<%
	String strFuncId = StringUtil.getParameter(request,"funcid");
%>
<script type="text/javascript">
	$(function(){
		var funcid = "<%=strFuncId%>";
		var menuurl =  ctx+"/common/getMenuTreeById.htm";

		$.menu.showMenu({
			url:menuurl,
			data:{"id":funcid},
			divName:"treeDemo"
		});
	});
	var fixedObj=60;
	function customHeightSet(contentHeight){
		$("#scrollContent").height(contentHeight);
	}
</script>

</head>

<body leftFrame="true">

<div id="scrollContent" style="overflow-x:hidden;">
	<div>
		<ul id="treeDemo" class="ztree ztree_accordition"></ul>
	</div>
</div>	

			
</body>
</html>