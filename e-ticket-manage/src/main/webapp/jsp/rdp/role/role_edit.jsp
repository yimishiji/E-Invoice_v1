<%@page import="com.rongzer.rdp.web.domain.system.MenuFunctionEntity"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>内容管理系统</title>
<%
	String roleId = String.valueOf(request.getAttribute("roleId"));
%>
<!--框架必需start-->
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/libs/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/libs/js/language/cn.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/libs/js/framework.js"></script>
<!--框架必需end-->
<!-- 异步form表单提交 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/libs/js/form/form.js"></script>
<!-- 通用常量 -->
<script type="text/javascript"> var ctx = "${ctx}";</script>
<!-- 语言包 -->
<script type="text/javascript" src="${js}/rdp/common/rongzer.messeage.cn.js"></script>
<!-- 权限编辑js导入 -->
<script type="text/javascript" src="${js}/rdp/role/role_edit.js"></script>
</head>
<body onload="load()">
	<form method="post" action="${ctx}/role/modifyRoleInfo.htm"
		id="edit_form">
		<input type="hidden" name="roleId" id="roleId" value="<%=roleId%>" />
		
		<div id="showPermissionInfo">
		</div>
	</form>
</body>
</html>