<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>内容管理系统</title>
<%@ include file="/jsp/rdp/commons/index_libs.jsp"%>
<%@ include file="/jsp/rdp/commons/ztree_libs.jsp"%>
<script type="text/javascript" src="${js}/rdp/role/role.js"></script>
</head>
<body>
<form method="post" action="${ctx}/role/addOneRoleInfo.htm" id="edit_form">
<input type="hidden" name="roleId" id="roleId" value="${roleInfo.roleId }"/>
<div style="height:515px;overflow:auto;">
<div class="height_5"></div>
	<table class="tableStyle" formMode="line">
		<tr><th colspan="2">填写表单</th></tr>
		<tr>
		    <td width="30%">角色名称：</td><td width="70%"><input type="text" name="roleName" value="${roleInfo.roleName }"/><span class="star">*</span></td>
		</tr>
    <tr>
	    <td>角色描述：</td>
	    <td><textarea  style="width:350px;height:50px;" name="roleDesc" ><c:out value="${roleInfo.roleDesc }"/></textarea></td>
    </tr>
    <tr>
	    <td>功能选择：</td>
	    <td>
	    	<input type="hidden" name="permissons" id="permissons" value="${roleInfo.permissons }"/>
	    	<ul id="tree-1" class="ztree"></ul>
	    </td>
    </tr>	
	</table>
</div>
<div class="height_15"></div>
	<div>
		<table width="100%">
			<tr>
				<td class="ali03">
					<button id="add_role">保存</button> <button id = "closeWin">关闭</button>
				</td>
			</tr>
		</table>
	</div>
</form>
</body>
</html>