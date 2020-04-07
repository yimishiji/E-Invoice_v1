<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>    
<%@ page import="com.rongzer.rdp.common.util.*"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>
<!DOCTYPE html>
<html style="height: 100%;">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=9" ></meta>
<title></title>
<%@ include file="/jsp/rdp/commons/basic_lib.jsp" %>
<%@ include file="/jsp/rdp/commons/dialog_libs.jsp" %>
<link href="${ctx}/resource/css/style.css?v=4.0.0" rel="stylesheet">
<link href="${ctx}/resource/css/animate.min.css" rel="stylesheet">
<link href="${ctx}/resource/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
<script type="text/javascript">	var ctxp = "${ctx}";</script>
<title>内容区域</title>

<%
    String strURLParam = "";
	String id=StringUtil.getParameter(request,"id");
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

<script type="text/javascript">


    function refreshZTree()
    {
        tfrmleft.refreshZTree();
    }   
    $(function(){

    	$(".shrink").click(function(){
    		//判断bs_left宽度
    		if($("#bs_left").width()==224){
    			$("#bs_left").css({
			          "width":"0px"
			          });
				$("#chevron").addClass("fa-chevron-right") .removeClass("fa-chevron-left");
    		}else{
    			$("#bs_left").css({
			          "width":"224px"
			          });
				$("#chevron").addClass("fa-chevron-left") .removeClass("fa-chevron-right");
    			
    		}
    		
    		
    	});
    });
</script>
</head>
<body> 
<div style="height: 100%;display: table;table-layout: fixed;width: 100%;overflow: hidden;">
<div id="bs_left" class="" style="transition:All 0.5s;width:224px;height: 100%;display: table-cell;vertical-align: top;float: none;background-color: #fff;border-right: solid 1px #e5e5e5;">
<IFRAME height="100%" width="100%"  frameBorder=0 id=tfrmleft name=tfrmleft src="${ctx}/rdpTreeLeft.htm?id=<%=id%><%=strURLParam%>"  allowTransparency="true"></IFRAME>
</div>

<div id="bs_right" style="height: 100%;display: table-cell;vertical-align: top;float: none;width: 100%;">
<div class="shrink">
	<i id="chevron" class="fa fa-chevron-left" style="margin-top: 24px;color: #fff;margin-left: 3px;transition:All 0.5s"></i>
</div>
<IFRAME height="100%" width="100%" frameBorder=0 id="tfrmright<%=id%>" name="tfrmright<%=id%>" src=""  allowTransparency="true"></IFRAME>
</div>
</div>				
</body> 
</html>
