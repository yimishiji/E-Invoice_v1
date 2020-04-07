<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>  
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%@ include file="/jsp/rdp/commons/index_libs.jsp" %>

<!--修正IE6不支持PNG图start-->
<style>
img {
	behavior:url("${ctx}/resource/libs/js/method/pngFix/pngbehavior.htc");
}
</style>
<!--修正IE6不支持PNG图end-->

<!--动画方式入场效果start-->
<script type="text/javascript" src="${ctx }/resource/libs/js/pic/jomino.js"></script>
<script>
	 $(function(){
		$(".navIcon").jomino();
	});
	function customHeightSet(contentHeight){
		$("#scrollContent").height(contentHeight);
	}
</script>
<!--动画方式入场效果end-->
</head>
<body>

<div class="box2" panelTitle="管理中心" showStatus="false">
	<div>
		<table class="tableStyle" >
			<tr class="ali01">
				<th colspan="4" ><B>待发布信息</B></th>
			</tr>
			<tr id="node-1">
				<td class="ali03" width="20%"><a href="#" >待发布商品分类：</a></td>
				<td class="ali01" width="30%"><span class="star">2</span></td>
				<td class="ali03" width="20%"><a href="#" >待发布导航条：</a></td>
				<td class="ali01" width="30%"><span class="star">2</span></td>

			</tr>
			<tr id="node-1">
				<td class="ali03"><a href="#" >待发布商品：</a></td>
				<td class="ali01"><span class="star">2</span></td>
				<td class="ali03"><a href="#" >待发布广告：</a></td>
				<td class="ali01"><span class="star">2</span></td>

			</tr>	

			<tr id="node-1">
				<td class="ali03"><a href="#" >待发布促销：</a></td>
				<td class="ali01"><span class="star">2</span></td>
				<td class="ali03"><a href="#" >待发布模板：</a></td>
				<td class="ali01"><span class="star">2</span></td>

			</tr>	

			<tr id="node-1">
				<td class="ali03"><a href="#" >待发布汽车车型：</a></td>
				<td class="ali01"><span class="star">2</span></td>
				<td class="ali03"><a href="#" >待发布常见问题：</a></td>
				<td class="ali01"><span class="star">2</span></td>

			</tr>	

			<tr id="node-1">
				<td class="ali03"><a href="#" >待发布商品价格：</a></td>
				<td class="ali01"><span class="star">2</span></td>
				<td class="ali03"><a href="#" >待发布商品库存：</a></td>
				<td class="ali01"><span class="star">2</span></td>

			</tr>

			<tr id="node-1">
				<td class="ali03"><a href="#" >待发布零售商服务：</a></td>
				<td class="ali01"><span class="star">2</span></td>
				<td class="ali03"><a href="#" >&nbsp;</td>
				<td class="ali01">&nbsp;</td>

			</tr>			

			<tr class="ali01">
				<th colspan="4" ><B>订单信息</B></th>
			</tr>
			<tr id="node-1">
				<td class="ali03" width="20%"><a href="#" >零售商未确认订单:</a></td>
				<td class="ali01" width="30%"><span class="star">2</span></td>
				<td class="ali03" width="20%"><a href="#" >零售商拒接订单:</a></td>
				<td class="ali01" width="30%"><span class="star">2</span></td>

			</tr>
			<tr id="node-1">
				<td class="ali03"><a href="#" >超期未支付订单:</a></td>
				<td class="ali01"><span class="star">2</span></td>
				<td class="ali03"><a href="#" >超期未安装订单:</a></td>
				<td class="ali01"><span class="star">2</span></td>

			</tr>	

			<tr id="node-1">
				<td class="ali03"><a href="#" >退款申请:</a></td>
				<td class="ali01"><span class="star">2</span></td>
				<td class="ali03"><a href="#" >用户投诉：</a></td>
				<td class="ali01"><span class="star">2</span></td>

			</tr>	

			<tr id="node-1">
				<td class="ali03"><a href="#" >询价单：</a></td>
				<td class="ali01"><span class="star">2</span></td>
				<td class="ali03"><a href="#" >&nbsp;</a></td>
				<td class="ali01">&nbsp;</td>

			</tr>	

			<tr class="ali01">
				<th colspan="4" ><B>商品信息</B></th>
			</tr>
			<tr id="node-1">
				<td class="ali03" width="20%"><a href="#" >商品总数:</a></td>
				<td class="ali01" width="30%"><span class="star">102</span></td>
				<td class="ali03" width="20%"><a href="#" >库存警告商品数:</a></td>
				<td class="ali01" width="30%"><span class="star">2</span></td>

			</tr>
			<tr class="ali01">
				<th colspan="4" ><B>用户信息</B></th>
			</tr>
			<tr id="node-1">
				<td class="ali03" width="20%"><a href="#" >待审核的用户:</a></td>
				<td class="ali01" width="30%"><span class="star">2</span></td>
				<td class="ali03" width="20%"><a href="#" >待处理的用户留言:</a></td>
				<td class="ali01" width="30%"><span class="star">2</span></td>

			</tr>
			<tr id="node-1">
				<td class="ali03" width="20%"><a href="#" >待处理的用户投诉:</a></td>
				<td class="ali01" width="30%"><span class="star">20</span></td>
				<td class="ali03" width="20%"></td>
				<td class="ali01" width="30%"></td>

			</tr>
		</table>
	</div>	
</div>


</body>
</html>