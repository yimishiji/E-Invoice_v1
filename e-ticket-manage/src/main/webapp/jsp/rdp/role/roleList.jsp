<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>内容管理系统</title>
<%@ include file="/jsp/rdp/commons/index_libs.jsp"%>
<%@ include file="/jsp/rdp/commons/page_libs.jsp"%>
<script type="text/javascript" src="${ctx}/resource/libs/js/table/quiGrid.js"></script>
<script type="text/javascript" src="${ctx}/resource/js/rdp/common/rongzer.grid.js"></script>
<script type="text/javascript" src="${js}/rdp/role/roleList.js"></script>
</head>
<body>
<input type="hidden" value="" name="sort" id="sort" /> 
<input type="hidden" value="${page.OPER_DATA_PAGEPARAM_KEY.pageIndex}" name="pageIndex" id="pageIndex" /> 
<input type="hidden" value="${page.OPER_DATA_PAGEPARAM_KEY.pageSize}" name="pageSize" id="pageSize" />
<ps:enable resourceNo="R3101">
<div class="box2" panelTitle="查询" id="searchPanel">
		<table class="tableStyle" formMode="line" >
			<tr >
				<td width="15%">角色名称：</td>
				<td width="18%">
				    <select prompt="全部" name="roleName" data='${roles}' selectedValue="${roleName }"></select>   
			    </td>	
				<td width="15%">角色描述：</td>
				<td width="18%">
				    <input type="text" name="roleDesc" value="${roleDesc}"/>                    				
			    </td>
				<td width="34%"><button><span class="icon_find" id="QueryBtn">查  询</span></button></td>
			</tr>
			
		</table>
</div>
</ps:enable>
<div>
<ps:enable resourceNo="R3102">
	<div class="float_left padding5">
		<button type="button" id="addRole">增加新角色</button>
	</div>
</ps:enable>
<ps:enable resourceNo="R3105">
	<div class="float_left padding5">
		<button type="button" id="delBatch" >批量删除</button>
	</div>
</ps:enable>
	<div class="clear"></div>
</div>

<div style="display:none;" id="renderHtml">
	<ps:enable resourceNo="R3103">
	<a href="javascript:void(0);" class="modifyOne" fid="{0}">编辑</a> 
	</ps:enable> &nbsp;
	<ps:enable resourceNo="R3104">
	<a href="javascript:void(0);" class="deleteOne" fid="{0}">删除</a>
	</ps:enable>
</div>

<div class="padding_right5">
<div id="dataBasic"></div>
</div>
<div style="height: 35px;" id="pageContent">
</div>
</body>
</html>