<%@ page import="com.rongzer.rdp.memcached.CacheClient" %>
<%@ page import="com.rongzer.rdp.common.context.RDPContext" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<%
	String strFlag = request.getParameter("flag");
    String strReturn ="";
    String strq = request.getParameter("q");
    String strfq = request.getParameter("fq");
    String strfl = request.getParameter("fl");
    String sort = request.getParameter("sort");

    if (strq == null)
    {
    	strq = "";
    }
    if (strfq == null)
    {
    	strfq = "";
    }
    if (strfl == null)
    {
    	strfl = "";
    }    
    if (sort == null)
    {
    	sort = "";
    }    
    
    if ("test1".equals(strFlag))
    {

        strReturn = com.rongzer.solr.service.SolrTestService.query1();
    }
    
    
    if ("query".equals(strFlag))
    {

        strReturn = com.rongzer.solr.service.SolrTestService.query(strq,strfq,strfl,sort);
    }
    
    if ("clearAllIndex".equals(strFlag))
    {
        strReturn = com.rongzer.solr.service.SolrTestService.clearAllIndex();
    }

    if ("clearMemCache".equals(strFlag))
    {
        CacheClient cacheClient1 = (CacheClient)RDPContext.getContext().getBean("cacheClient");
        cacheClient1.flushAll();
        strReturn = "清除成功";
    }
    
    if ("clearAllCache".equals(strFlag))
    {
        strReturn = com.rongzer.solr.service.SolrTestService.clearAllCache();
    }

    if ("indexArticle".equals(strFlag))
    {
        strReturn = com.rongzer.solr.service.SolrTestService.indexArticle();
    }

    if ("indexGoods".equals(strFlag))
    {
        strReturn = com.rongzer.solr.service.SolrTestService.indexGoods();
    }
   
    if ("indexStore".equals(strFlag))
    {
        strReturn = com.rongzer.solr.service.SolrTestService.indexSolr("StoreSolrService");
    }

    if ("indexAppraise".equals(strFlag)) 
    {
        strReturn = com.rongzer.solr.service.SolrTestService.indexSolr("AppraiseSolrService");
    }
 
     if ("indexAreaStock".equals(strFlag)) 
    {
        strReturn = com.rongzer.solr.service.SolrTestService.indexSolr("AreaStockSolrService");
    }
            
    if ("getAnalysis".equals(strFlag))
    {
        strReturn = com.rongzer.solr.service.SolrTestService.getAnalysis(strq);
    }
    
    if ("testRule".equals(strFlag))
    {
        com.rongzer.rdp.drools.engine.RDPRuleTest rdpRuleTest = new com.rongzer.rdp.drools.engine.RDPRuleTest();
        
        strReturn = rdpRuleTest.execute();
    }
    
    
    String strSeq = "";
    if ("testSeq".equals(strFlag))
    {
        strSeq = com.rongzer.rdp.common.service.RDPUtil.getSeqVal("EC_ORDERNO");        
    }
%>
<script>
    function submitForm()
    {
        document.all.form1.submit();
    }
    
    function query()
    {
       document.all.flag.value="query";
       submitForm();
    }

    function clearMemCache()
    {
        document.all.flag.value="clearMemCache";
        submitForm();
    }
    
    function clearAllCache()
    {
       document.all.flag.value="clearAllCache";
       submitForm();
    }  
    
    function clearAllIndex()
    {
       document.all.flag.value="clearAllIndex";
       submitForm();
    }   
    
    function indexArticle()
    {
       document.all.flag.value="indexArticle";
       submitForm();
    }  
    
    function indexGoods()
    {
       document.all.flag.value="indexGoods";
       submitForm();
    } 
    
    function indexStore()
    {
       document.all.flag.value="indexStore";
       submitForm();
    }  

    function indexAppraise()
    {
       document.all.flag.value="indexAppraise";
       submitForm();
    }  

    function indexAreaStock()
    {
       document.all.flag.value="indexAreaStock";
       submitForm();
    }  
            
    function getAnalysis()
    {
       document.all.flag.value="getAnalysis";
       submitForm();
    } 
    
    function testRule()
    {
       document.all.flag.value="testRule";
       submitForm();
    } 
    
    function test1()
    {
       document.all.flag.value="test1";
       submitForm();
    } 
    
    function testSeq()
    {
       document.all.flag.value="testSeq";
       submitForm();
    } 
  </script>
</head>

<body>
<form id="form1" name="form1" action="test.jsp" method="post" width="100%">
<input type="hidden" name="flag" id="flag">
    过滤(fq)：<input id="fq" name="fq" type="text" value='<%=strfq%>' style="width:80%"><br>
   输出(fl)：<input id="fl" name="fl" type="text" value='<%=strfl%>'  style="width:80%"><br>
  查询(qstr)：<input id="q" name="q" type="text" value='<%=strq%>'  style="width:80%"><br>
    顺序(sort)：<input id="sort" name="sort" type="text" value='<%=sort%>'  style="width:80%"><br>
	<input type="button" value="搜索" onclick="javascript:query();"> 
	<br>
    <input type="button" value="清空memcached缓存" onclick="javascript:clearMemCache();">
    <input type="button" value="清空Solr缓存与memcached" onclick="javascript:clearAllCache();">
    <input type="button" value="清除Solr索引" onclick="javascript:clearAllIndex();">
    <input type="button" value="更新文章索引" onclick="javascript:indexArticle();">
    <input type="button" value="更新商品索引" onclick="javascript:indexGoods();">
    <input type="button" value="更新店铺索引" onclick="javascript:indexStore();">
    <input type="button" value="更新评价索引" onclick="javascript:indexAppraise();">
    <input type="button" value="更新区域库存索引" onclick="javascript:indexAreaStock();">
    
    <input type="button" value="测试分词" onclick="javascript:getAnalysis();">

    <input type="button" value="测试规则" onclick="javascript:testRule();">
    <input type="button" value="其它测试" onclick="javascript:test1();">
    
    
    <input type="button" value="测试序号" onclick="javascript:testSeq();"><%=strSeq%>
    

</form>
<%=strReturn%>
</body>
</html> 