<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=com.rongzer.rdp.common.service.RDPUtil.getSysConfig("rdp.system.title")%></title>
<link href="${ctx}/web/resource/css/manualBilling.css"
	rel="stylesheet" type="text/css" />
<script type="text/javascript"
	src="${ctx}/resource/libs/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${ctx}/web/resource/js/manualBilling.js"></script>
<script type="text/javascript" src="${ctx}/web/resource/js/json2.js"></script>
<script type="text/javascript" src="${ctx}/webresources/script/wabacus_api.js"></script>
<script type="text/javascript" src="${ctx}/webresources/component/artDialog/artDialog.js"></script>
<script type="text/javascript" src="${ctx}/webresources/component/artDialog/plugins/iframeTools.js"></script>
<link rel="stylesheet" href="${ctx}/web/resource/css/invoice.css" type="text/css" />

<script type="text/javascript">
var ctx = "<%= request.getContextPath()%>";
</script>
</head>
<body style="overflow-y:auto">
<div id="loading" class="loadingContainer">
   	<img class="loadingImg" src="${ctx}/resource/image/shinho_progress.png"/>
	<p class="loadingText">处理中，请耐心等待</p>
</div>
<input type="hidden" name="orderId" id="orderId" value="${orderId}">
	<div>
		<button id="delpro" class="invoice" style="min-width: 30px;" onclick="delpro();">-</button>
		<button id="addpro" class="invoice" style="min-width: 30px;" onclick="addpro();">+</button>
		<button class="cls-button2 invoice"  id="submitinvoice" onclick="submitinvoice();">提交开票</button>
		<button class="cls-button2 invoice"  id=getBillingInfo onclick="getBillingInfo();">获取开票信息</button>
	</div>
	<div style="padding-top: 50px;">
		<table width="90%;" class="oneTabal" style="table-layout: fixed;" >
			<tbody>
				<tr>
					<!-- 购买方 ｛名称 邮箱 手机号 纳税人识别号 地址、电话 开户行及账号｝ -->
					<td
						style="border: 1px solid #955319; text-align: center; width: 50px; word-wrap: break-word;">购买方</td>
					<td
						style="border: 1px solid #955319; border-left: 0px; text-align: center; width: 350px; word-wrap: break-word;">
						<ul style="list-style: none;">
							<li class="clearfix">
								<div class="fl">
									<font color="red">*</font>
									<p>名称</p>
									<font>:</font>
								</div>
								<input id="buyername" class="fr" />
							</li>
							<li class="clearfix">
								<div class="fl">
									<font color="red">*</font>
									<p>邮箱</p>
									<font>:</font>
								</div>
								<input class="fr" id="buyermail"/>
							</li>
							<li class="clearfix">
								<div class="fl">
<!-- 									<font color="red">*</font> -->
									<p>手机号</p>
									<font>:</font>
								</div>
								<input class="fr" id="buyerphone"/>
							</li>
							<li class="clearfix">
								<div class="fl">
<!-- 									<font color="red">*</font> -->
									<p>纳税人识别号</p>
									<font>:</font>
								</div>
								<input class="fr" id="buyernum"/>
							</li>
							<li class="clearfix">
								<div class="fl">
<!-- 									<font color="red">*</font> -->
									<p>地址、电话</p>
									<font>:</font>
								</div>
								<input class="fr" id="buyeraddress"/>
							</li>
							<li class="clearfix">
								<div class="fl">
<!-- 									<font color="red">*</font> -->
									<p>开户行及账号</p>
									<font>:</font>
								</div>
								<input class="fr" id="buyerbank"/>
							</li>
							<li class="clearfix">
								<div class="fl">
									<!-- 									<font color="red">*</font> -->
									<p>开票类型</p>
									<font>:</font>
								</div>
								<select class="fr" style="width: 68%;margin-right:2%;height: 25px" id="invoicetype" name="invoicetype">
									<c:forEach items="${invoice_types}" var="invoice_type">
										<c:if test="${'E00901' ne invoice_type.itemCode}">
											<option value="${invoice_type.itemCode}">${invoice_type.itemName}</option>
										</c:if>
									</c:forEach>
								</select>
							</li>
						</ul>
					</td>
					<!-- 提取码  -->
					<td style="border: 1px solid #955319; border-left: 0px; text-align: center; width: 50px; word-wrap: break-word;"><font color="red">*</font>提取码</td>
					<td class="extractionCode" valign="top" style="border: 1px solid #955319; border-left: 0px; text-align: center; width: 350px; word-wrap: break-word;">
					<ul class="tqm" style="list-style: none;" id="tqm">
							<li><input class="fr pn" onkeyup="isPhone();"/></li>
						</ul>
					</td>
				</tr>

			</tbody>
		</table>
		<!--商品详情  -->
		<table width="90%;"  class="twoTabal"  style="table-layout: fixed;">
			<tbody>
				<tr height="50px;" class="twoTabalfirstTr">
					<td style="border: 1px solid #955319; border-top: 0px;border-bottom:0px; text-align: center; width: 180px; word-wrap: break-word;">提取码</td>
					<td style="border: 1px solid #955319; border-top: 0px;border-bottom:0px; border-left: 0px; text-align: center; width: 100px; word-wrap: break-word;">交易流水号</td>
					<td style="border: 1px solid #955319; border-top: 0px;border-bottom:0px; border-left: 0px; text-align: center; width: 100px; word-wrap: break-word;">金额</td>
				</tr>
				<tr class="twoTabalTtr">
					<td style="border: 1px solid #955319; border-top: 0px;border-bottom:0px; text-align: center; width: 100px; word-wrap: break-word;" >
						<ul class="product" style="list-style: none;" id="pickupcpode">
<!-- 							<li class="pro_1"><input /></li> -->
						</ul></td>
					<td style="border: 1px solid #955319; border-top: 0px;border-bottom:0px; border-left: 0px; text-align: center; width: 100px; word-wrap: break-word;">
						<ul class="product" style="list-style: none;" id="transactionnum">
<!-- 							<li class="pro_1"><input /></li> -->
						</ul></td>
					<td style="border: 1px solid #955319; border-top: 0px;border-bottom:0px; border-left: 0px; text-align: center; width: 100px; word-wrap: break-word;">
						<ul class="product" style="list-style: none;" id="amount">
<!-- 							<li class="pro_1"><input /></li> -->
						</ul></td>
				</tr>
				<tr class="allP" height="60px;">
					<td style="border: 1px solid #955319; padding-top:20px; border-top: 0px; text-align: center; width: 100px; word-wrap: break-word;">合计</td>
					<td style="border: 1px solid #955319; padding-top:20px; border-top: 0px; border-left: 0px; text-align: center; width: 100px; word-wrap: break-word;"></td>
					<td style="border: 1px solid #955319; padding-top:20px; border-top: 0px; border-left: 0px; text-align: center; width: 100px; word-wrap: break-word;" id="total_amout"></td>
				</tr>
			</tbody>
		</table>
		<table width="90%;" class="threeTabal"  style="table-layout: fixed;" >
			<tbody>
				<tr>
					<!-- 销售方 ｛名称  纳税人识别号 地址、电话 开户行及账号｝ -->
					<td
						style="border: 1px solid #955319; border-top: 0px; text-align: center; width: 50px; word-wrap: break-word;">销售方</td>
					<td
						style="border: 1px solid #955319; border-top: 0px; border-left: 0px; text-align: center; width: 350px; word-wrap: break-word;">
						<ul style="list-style: none;">
							<li class="clearfix">
								<div class="fl">
<!-- 									<font color="red">*</font> -->
									<p>名称</p>
									<font>:</font>
								</div>
								<input class="fr"  id="salesname" value="" readonly="readonly" style="border-style: solid; border-width: 0"/>
							</li>
							<li class="clearfix">
								<div class="fl">
<!-- 									<font color="red">*</font> -->
									<p>纳税人识别号</p>
									<font>:</font>
								</div>
							    <input class="fr"  id="salesnum" value="" readonly="readonly" style="border-style: solid; border-width: 0"/>
								<input type="hidden" id="salesId"/>
							</li>
							<li class="clearfix">
								<div class="fl">
<!-- 									<font color="red">*</font> -->
									<p>地址、电话</p>
									<font>:</font>
								</div>
								<input class="fr"  id="salesaddress" value="" readonly="readonly" style="border-style: solid; border-width: 0"/>
							</li>
							<li class="clearfix">
								<div class="fl">
<!-- 									<font color="red">*</font> -->
									<p>开户行及账号</p>
									<font>:</font>
								</div>
								<input class="fr"  id="salesbank" value="" readonly="readonly" style="border-style: solid; border-width: 0"/>
							</li>
						</ul>
					</td>
					<!-- 备注  -->
					<td
						style="border: 1px solid #955319; border-top: 0px; border-left: 0px; text-align: center; width: 50px; word-wrap: break-word;">备注(可填)</td>
					<td style="border: 1px solid #955319; border-top: 0px; border-left: 0px; text-align: center; width: 350px; word-wrap: break-word;">
						<!-- <input type="text" id="remark" /> -->
						<textarea name="" id="remark"></textarea>
					</td>
				</tr>

			</tbody>
		</table>
		<!-- 底部 -->
		<table width="90%;" class="fourTabal" style="table-layout: fixed;" >
			<tbody>
				<tr>
					<td
						style="border: 0px solid #955319; border-top: 0px; text-align: center; width: 200px; word-wrap: break-word;">收款人:<input id="payee" value="" readonly="readonly" style="border-style: solid; border-width: 0"/></td>
					<td
						style="border: 0px solid #955319; border-top: 0px; border-left: 0px; text-align: center; width: 200px; word-wrap: break-word;">复核:<input id="check" value="" readonly="readonly" style="border-style: solid; border-width: 0"/></td>
					<td
						style="border: 0px solid #955319; border-top: 0px; border-left: 0px; text-align: center; width: 200px; word-wrap: break-word;">开票人:<input id="drawer" value="" readonly="readonly" style="border-style: solid; border-width: 0"/></td>
					<td
						style="border: 0px solid #955319; border-top: 0px; border-left: 0px; text-align: center; width: 200px; word-wrap: break-word;">销售方(章)</td>
				</tr>

			</tbody>
		</table>
	</div>
</body>
</html>