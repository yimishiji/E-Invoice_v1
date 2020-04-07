<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.rongzer.rdp.common.util.*"%>  


<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%@ include file="/jsp/rdp/commons/index_libs.jsp" %>
<%@ include file="/jsp/rdp/commons/ztree_libs.jsp" %>
<%
    String strFuncId = StringUtil.getParameter(request,"funcid");
    String strWebId = StringUtil.getParameter(request,"WEB_ID");
    String strWebType = StringUtil.getParameter(request,"WEB_TYPE");
    String excludeId = "";
    if ("ECW101".equals(strWebType))
    {
        excludeId=",\"excludeId\":\"M1110,M1111,M1112,M1113,M1120\"";
    }else if ("ECW102".equals(strWebType))
    {
        excludeId=",\"excludeId\":\"M1110,M1111,M1112,M1113\"";
    }else if ("ECW103".equals(strWebType))
    {
        excludeId=",\"excludeId\":\"M1106,M1120\"";        
    }
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/libs/js/nav/treeAccordion_normal.js"></script>
<script type="text/javascript">
$(function(){
	var funcid = "<%=strFuncId%>";
	var menuurl =  ctx+"/common/getMenuTreeById.htm";
	
	var treeIdList = new Object();

	$.menu.showMenu({
		url:menuurl,
		data:{"id":funcid<%=excludeId%>},
		divName:"cmsMenuTree",
		callBack:function(){
			$("#cmsMenuTree a").click(function(){
				var url = "";
				if(treeIdList[$(this).attr("id")]==null){
					treeIdList[$(this).attr("id")] = $(this).attr("href");
				}
				
				url = treeIdList[$(this).attr("id")];
				if(url && url != null && url.length>0)
				{
				    url = url+"&WEB_ID=<%=strWebId%>";
				
		           $(this).attr("href",url);
		        }
			});
		}
	});
	
	
});
var fixedObj=60;
function customHeightSet(contentHeight){
	$("#scrollContent").height(contentHeight);
}

function load()
{
    if (parent.document.getElementById("frmright") != null)
    {
        parent.document.getElementById("frmright").src = "../rdp/rdpMain.htm?id=NAV1&WEB_ID=<%=strWebId%>";
    }
}
</script>

</head>

<body leftFrame="true" onload="load();">
<div style="padding-left: 10px;padding-top: 5px">
	<div id="scrollContent" style="overflow-x:hidden;float: left;">
		<div >
			<ul id="cmsMenuTree" class="ztree"></ul>
		</div>
	</div>	
</div>
</body>
</html>