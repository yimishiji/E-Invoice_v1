<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>        
<%@ page import="com.rongzer.rdp.common.util.*"%>    
<%@ taglib uri="wabacus" prefix="wx"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>  
<%
    String id = StringUtil.getParameter(request,"id");
    String inputBoxId = StringUtil.getParameter(request,"INPUTBOXID");
    String pageId = StringUtil.getParameter(request,"SRC_PAGEID");
    String reportId = StringUtil.getParameter(request,"SRC_REPORTID");
    String codecol = StringUtil.getParameter(request,"codecol");
    String pid = StringUtil.getParameter(request,"pid");
    String type = StringUtil.getParameter(request,"type");
    String chkboxType = StringUtil.getParameter(request,"chkboxType");
    
    int nRow = -1;
    String inputBoxLeft = pageId+"_guid_"+reportId+"_wxcol_";
    inputBoxId = StringUtil.safeReplace(inputBoxId,inputBoxLeft,"");
    
    int nIndex = inputBoxId.indexOf("__");
    
    if (nIndex>0)
    {
        nRow = StringUtil.toInt(inputBoxId.substring(nIndex + 2,inputBoxId.length()),-1);
        inputBoxId = inputBoxId.substring(0,nIndex);
    }
    if (StringUtil.isEmpty(codecol))
    {
        codecol = inputBoxId;
    }
    
    if (StringUtil.isEmpty(type))
    {
        type="checkbox";
    }
    
    if (StringUtil.isEmpty(chkboxType))
    {
    	chkboxType="{ 'Y' : '', 'N' : '' }";
    }


    String strURLParam = "/rdp/rdpLeftTreeLoad.htm?id="+id;
    Enumeration rnames=request.getParameterNames();

    for (Enumeration e = rnames ; e.hasMoreElements() ;) 
    {
        String thisName=e.nextElement().toString();
        String thisValue=request.getParameter(thisName);
        if (!"id".equals(thisName))
        {
            strURLParam += "&"+StringUtil.delHTMLTag(thisName)+"="+StringUtil.delHTMLTag(thisValue);
        }
    }
 %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/libs/js/jquery-1.9.1.min.js"></script>
<%@ include file="/jsp/rdp/commons/ztree_libs.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/libs/js/tree/zTree_v3/js/jquery.ztree.excheck-3.5.min.js"></script>
<script type="text/javascript">
    var ctx="${ctx}";
    var pageId = "<%=pageId%>";
    var reportId = "<%=reportId%>";
    var inputBoxId = "<%=inputBoxId%>";
    var codecol = "<%=codecol%>";
    var id = "<%=id%>";
    var nRow = <%=nRow%>;
        
	$(function(){
		//查询己选中的值
		var value = getFormValue(codecol);
		var menuurl =  ctx+"<%=strURLParam%>"+"&value="+value;
		var setting = {
            check: {  
                enable: true,  
                chkStyle: "<%=type%>",  
                chkboxType : <%=chkboxType%>,
                radioType : "all"
            },  
			data: {
				simpleData: {
				    idKey:"id",
					pIdKey: "pid",
					enable: true
				}
			},
			async: {
				enable: true,
				url: menuurl
			}	
		};

		$.fn.zTree.init($("#treeMenu"), setting);

	});
	
	function getFormValue(colName)
	{
	   var tValue = null;
	   if (nRow >=0)
	   {
	       var trId = pageId+"_guid_"+reportId+"_tr_"+nRow;
           var trObj = artDialog.open.origin.document.getElementById(trId);
           tValue = artDialog.open.origin.wx_getListReportColValuesInRow(trObj,null);	       
	   }else
	   {
	   
	       tValue = artDialog.open.origin.getEditableReportColValues(pageId,reportId,null,null);
	   }

       var rValue = tValue[colName].value;	   
	   return rValue;
	}

    function setFromValue(trValue)
    {
       if (nRow >=0)
       {
           var trId = pageId+"_guid_"+reportId+"_tr_"+nRow;
           var trObj = artDialog.open.origin.document.getElementById(trId);
           artDialog.open.origin.setEditableListReportColValueInRow(pageId,reportId,trObj,trValue);
       }else
       {
           artDialog.open.origin.setEditableReportColValue(pageId,reportId,trValue,null)
       }
    }
    
    function doSelect()
    {
         var treeObj = $.fn.zTree.getZTreeObj("treeMenu");
         var checkedNodes = treeObj.getCheckedNodes(); 
         var ids = "";
         var names = "";
         for (var i in checkedNodes)
         {
            if(checkedNodes[i].show == "0"){
            	//什么都不做
            }else{
	           	if (ids.length>0)
	            {
	                ids +=",";
	                names += ",";
	            }
	            ids += checkedNodes[i].id;
                names += checkedNodes[i].name;
            }
         }
         var jValue={};
         jValue[inputBoxId] = names;
         jValue[codecol] = ids;
         setFromValue(jValue);
         artDialog.open.origin.closePopupWin();
    }
</script>

</head>

<body leftFrame="true">
    <wx:popuppage></wx:popuppage>

<div style="overflow-x:hidden;">
    <div style="text-align: right"> 
         <input type="button" value="确定" onclick="doSelect()" />
    </div>
    <div  id="scrollContent" style="text-align: center">
        <ul id="treeMenu" class="ztree"></ul>
    </div>
    
</div>  
				
</body>
</html>