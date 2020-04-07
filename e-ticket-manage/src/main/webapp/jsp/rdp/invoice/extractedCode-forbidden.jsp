<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@ include file="/jsp/rdp/commons/tag_libs.jsp" %>
<!DOCTYPE>
<html>
<head>
	<title>发票查验</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="${ctx}/web/resource/css/bootstrap.min.css?v=3.3.5">
	<link rel="stylesheet" type="text/css" href="${ctx}/webresources/skin/blue/wabacus_system.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/webresources/skin/blue/wabacus_container.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/web/resource/js/plugins/icheck/skins/all.css?v=1.0.2">
	<link rel="stylesheet" type="text/css" href="${ctx}/web/resource/js/plugins/bootstrap-fileinput/css/fileinput.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/web/resource/js/plugins/chosen/chosen.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/webresources/skin/blue/artDialog/artDialog.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/webresources/skin/colselected_tree.css">
	<script language="javascript" src="${ctx}/webresources/script/wabacus_systemhead.js"></script>
	<link rel="stylesheet" href="${ctx}/web/resource/js/plugins/layer/skin/layer.css" id="layui_layer_skinlayercss">
	<script type="text/javascript" src="${ctx}/webresources/component/jquery-1.9.1.min.js"></script>

	<script type="text/javascript" src="${ctx}/web/resource/js/bootstrap.min.js?v=3.3.5"></script>
	<script type="text/javascript" src="${ctx}/web/resource/js/plugins/icheck/icheck.min.js?v=1.0.2"></script>
	<script type="text/javascript" src="${ctx}/web/resource/js/plugins/bootstrap-fileinput/js/fileinput.js"></script>
	<script type="text/javascript" src="${ctx}/web/resource/js/plugins/layer/layer.js"></script>
	<script type="text/javascript" src="${ctx}/web/resource/js/plugins/chosen/chosen.jquery.js"></script>
	<script type="text/javascript" src="${ctx}/web/resource/js/plugins/jquery.ellipsis.js"></script>
	<script type="text/javascript" src="${ctx}/web/resource/js/plugins/rongzer.init.js"></script>
	<script type="text/javascript" src="${ctx}/webresources/script/wabacus_system.js"></script>
	<script type="text/javascript" src="${ctx}/webresources/script/wabacus_util.js"></script>
	<script type="text/javascript" src="${ctx}/webresources/script/wabacus_tools.js"></script>
	<script type="text/javascript" src="${ctx}/webresources/component/wabacus_component.js"></script>
	<script type="text/javascript" src="${ctx}/webresources/script//wabacus_api.js"></script>
	<script type="text/javascript" src="${ctx}/webresources/component/artDialog/artDialog.js"></script>
	<script type="text/javascript" src="${ctx}/webresources/component/artDialog/plugins/iframeTools.js"></script>
	<script type="text/javascript" src="${ctx}/webresources/script/validate.js"></script>
	<script type="text/javascript" src="${ctx}/webresources/script/wabacus_editsystem.js"></script>
	<style>
		.cls-button{/*按钮样式*/
			border: 1px solid #5FA8DE;
			border-radius: 3px;
			box-shadow: 1px 2px 2px rgba(0, 0, 0, 0.1);
			color: #fff;
			height: 27px;
			min-width: 80px;
			text-align:center;
			font-weight:bold;
			float:right;
			margin-right:2px;
		}
	</style>
</head>
<body>
<div class="wrapper-main">
	<div class="wrapper-content">
							<div class="ibox-title">
								<input type="button" id="btn_forbidden"  value="禁止开票" style="cursor: hand" class="cls-button"
								disabled="disabled">
							</div>
							<div class="ibox-content">
								<table class="table table-condensed" id="optionContainer">
									<tr>
										<td style="padding-top: 12px">提取码：</td>
										<td >
											<input type="text" name="extractedCode" id="extractedCode" style="width: 200px"
												   class="form-control" data-toggle="tooltip" data-placement="top"
											value="${EXTRACTED_CODE}">
										</td>
										<td align="left"  style="padding-top: 12px">
											<c:if test="${empty TRANSACTION_NUMBER}">
												<a href="javascript:void(0)" onclick="checkExtractedCodeCanForbidden()">校验</a>
											</c:if>
										</td>
										<td align="left"  style="padding-top: 12px;">
											<label><font  id="tip" color="red"></font></label>
										</td>
									</tr>
									<tr>
										<td  style="padding-top: 12px">交易流水号:</td>
										<td>
											<input type="text" name="transaction_number" id="transaction_number" readonly
												   class="form-control" data-toggle="tooltip" data-placement="top"
												   value="${TRANSACTION_NUMBER}">
										</td>
										<td  style="padding-top: 12px">交易时间:</td>
										<td>
											<input type="text" name="transaction_datetime" id="transaction_datetime" readonly
												   class="form-control" data-toggle="tooltip" data-placement="top"
												   value="${TRANSACTION_DATETIME}">
										</td>
									</tr>
									<tr>
										<td  style="padding-top: 12px">门店号:</td>
										<td>
											<input type="text" name="store_number" id="store_number" readonly
														 class="form-control" data-toggle="tooltip" data-placement="top"
												   value="${STORE_NUMBER}">
										</td>
										<td  style="padding-top: 12px">金额:</td>
										<td>
											<input type="text" name="transaction_amount" id="transaction_amount" readonly
														class="form-control" data-toggle="tooltip" data-placement="top"
												   value="${TRANSACTION_AMOUNT}">
										</td>
									</tr>
								</table>
							</div>
						</div>
	</div>
</div>
<script>
	var ctx = "<%= request.getContextPath()%>";
	$(function () {
		$("#btn_forbidden").click(function () {
			forbiddenExtractedCode();
		});
	});

	//禁止提取码开票
	function forbiddenExtractedCode(){
		var extractedCode = $("#extractedCode").val();
		$.ajax({
			type : "post",
			datatype : "json",
			data:{extractedCode:extractedCode},
			url :ctx+"/manualInvoice/forbiddenExtractedCode.htm",
			async : true,
			success : function(data) {
				data = $.parseJSON(data);
				if(data.suc){
					wx_alert("操作成功");
					aoRefresh();
				}else{
					if(data.msg){
						wx_alert(data.msg);
					}else {
						wx_alert("操作失败");
					}
				}
			}
		})
	}

	//校验提取码能否禁止开票
	function checkExtractedCodeCanForbidden(){
		$("#tip").html("");
		var extractedCode = $("#extractedCode").val();
		if(!extractedCode){
			$("#tip").html("提取码为空");
			return;
		}
		$.ajax({
			type : "post",
			datatype : "json",
			data:{extractedCode:extractedCode},
			url :ctx+"/manualInvoice/checkExtractedCodeCanForbidden.htm",
			async : true,
			success : function(data) {
				$("#transaction_number").val("");
				$("#transaction_datetime").val("");
				$("#store_number").val("");
				$("#transaction_amount").val("");
				data = $.parseJSON(data);
				if(data.suc){
					$("#transaction_number").val(data.data.TRANSACTION_NUMBER);
					$("#transaction_datetime").val(data.data.TRANSACTION_DATETIME);
					$("#store_number").val(data.data.STORE_NUMBER);
					$("#transaction_amount").val(data.data.TRANSACTION_AMOUNT);
					$("#tip").html("校验通过");
					$("#btn_forbidden").attr("disabled",false);
				}else{
					if(data.msg){
						$("#tip").html(data.msg);
					}else{
						$("#tip").html("没有获取到提取码对应的交易信息");
					}
					$("#btn_forbidden").attr("disabled",true);
				}
			}
		})
	}

	function aoRefresh(){
		setTimeout(
				function(){
					//刷新报表
					parent.refreshComponentDisplay("extractedForbidden","report1",false);
					art.dialog.close();
				},1*1000);

	}
</script>
</body>
</html>