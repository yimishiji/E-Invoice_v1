<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>      
<%@ page import="com.rongzer.rdp.common.util.*"%>  
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>  
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/libs/js/jquery-1.9.1.min.js"></script>
<%@ include file="/jsp/rdp/commons/ztree_libs.jsp" %>
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
var key;
var hisOpen = [];
var treeIdList = new Object();
	$(function(){
		var ctx="${ctx}";
		var id = "<%=id%>";
		var menuurl =  ctx+"/rdp/rdpLeftTreeLoad.htm?id="+id+"<%=strURLParam%>";
		
		var setting = {
			data: {
				simpleData: {
					pIdKey: "pid",
					enable: true,
					rootPId: "root"
				}
			},
			async: {
				enable: true,
				url: menuurl
			},
			callback:{
				onAsyncSuccess:function(){
				    var zTree = $.fn.zTree.getZTreeObj("treeMenu");

                    var rootNode=zTree.getNodes()[0]; 
                    //展开根结点
                    if (rootNode != null)
                    {
                       zTree.expandNode(rootNode,true);
                    }				    
				    
				    for(var i = 0;i<hisOpen.length;i++){
				        var node = zTree.getNodeByParam("id",hisOpen[i]);
				        if (node != null)
				        {
				           zTree.expandNode(node,true);
				        }
                    }
                    
                    var rightURL = "";
                    if (parent.document.getElementById("tfrmright"+id)!= null)
                    {
                        rightURL = parent.document.getElementById("tfrmright"+id).src;
                    }
                    
                    if (rightURL.indexOf("rdpMain.htm")>0 || rightURL.indexOf("/rdp/")>=0)
                    {
                        var nodes = zTree.getNodes(); 
                        var childNodes = zTree.transformToArray(nodes); //getNodes()方法得到的只是父级节点，想要得到全部的，得转换为数组进行遍历
                        for(var j=0;j<childNodes.length;j++){
                            var strURL =  childNodes[j].url;
                            if(strURL && strURL != null && strURL.length>0)
                            {
                                if (parent.document.getElementById("tfrmright"+id) != null)
                                {
                                    parent.document.getElementById("tfrmright"+id).src = ctx+"/"+strURL;                                
                                }
                                break;                                    
                            }
                        }
                    }

        
				}
			}
			
		};

		$.fn.zTree.init($("#treeMenu"), setting);
	});
	

	//刷新树
    function refreshZTree()
    {
        var zTree = $.fn.zTree.getZTreeObj("treeMenu");
        
        var nodes = zTree.getNodes();
        var childNodes = zTree.transformToArray(nodes); //getNodes()方法得到的只是父级节点，想要得到全部的，得转换为数组进行遍历
        
        for(var i=0;i<childNodes.length;i++){
            if(childNodes[i].open)
            {
                hisOpen.push(childNodes[i].id);
            }
        } 
                
        zTree.reAsyncChildNodes(null, "refresh",true);
    }
</script>

</head>

<body leftFrame="true" style="margin: 0px">
<div id="scrollContent" style="overflow-x:hidden;" >
	<div style="text-align: center">
		<ul id="treeMenu" class="ztree ztree_accordition"></ul>
	</div>
</div>	

			
</body>
</html>