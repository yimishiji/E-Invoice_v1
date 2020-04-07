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


</head>
<body>
<div class="wrapper-main">
    <div class="wrapper-content">
        <div class="row">
            <div class="col-sm-12">
                <div class="row">
                    <div class="col-sm-12">
                        <div class="ibox">
                            <div class="ibox-title">
                                <input type="button" id="addRow" value="添加行" class="cls-button">
                                <input type="button" id="scan" value="扫码枪扫描" class="cls-button">
                                <input type="button" id="invoiceCheck" value="发票查验" class="cls-button">
                                <input type="button" id="resetting" value="重置" class="cls-button">
                            </div>
                            <div class="ibox-content">
                                <table class="table table-condensed" id="optionContainer">
                                    <thead>
                                    <tr>
                                        <th class="text-center">发票代码</th>
                                        <th class="text-center">发票号码</th>
                                        <th class="text-center">校验码</th>
                                        <th class="text-center">操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>
                                            <input type="text" name="invoiceCode"
                                                   onblur="this.value=this.value.replace(/[^0-9]/g,'')"
                                                   class="form-control" data-toggle="tooltip" data-placement="top"
                                                   title="" placeholder="请输入10位或者12位发票代码" maxlength="12"
                                                   data-original-title="请输入10位或者12位发票代码">
                                        </td>
                                        <td>
                                            <input type="text" name="invoiceNumber"
                                                   onblur="this.value=this.value.replace(/[^0-9]/g,'')"
                                                   class="form-control" data-toggle="tooltip" data-placement="top"
                                                   title="" placeholder="请输入8位发票号码" maxlength="8"
                                                   data-original-title="请输入8位发票号码">
                                        </td>
                                        <td>
                                            <input type="text" name="invoiceCheckCode"
                                                   onblur="this.value=this.value.replace(/[^0-9a-zA-Z]/g,'')"
                                                   class="form-control" data-toggle="tooltip" data-placement="top"
                                                   title=""  placeholder="请正确输入校验码" data-original-title="请正确输入校验码">
                                        </td>
                                        <td style="font-size: 13pt"></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <div class="wrapper-content">
        <div class="row">
            <div class="col-sm-12">
                <div class="ibox">
                    <div class="ibox-title">

                    </div>
                    <div class="ibox-content">
                        <table class="table table-condensed" id="register_invoice_table">
                            <thead>
                            <tr>
                                <th class="text-center">发票代码</th>
                                <th class="text-center">发票号码</th>
                                <th class="text-center">开票日期</th>
                                <th class="text-center">开票金额(元)</th>
                                <th class="text-center">收款方名称</th>
                                <th class="text-center">状态</th>
                                <%--<th class="text-center">操作</th>--%>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>


</div>
<script>
    var ctx = "<%= request.getContextPath()%>";
    //扫码输入页面
    function openScanWindows() {
        layer.open({
            type: 1,
            title: '扫码枪扫描',
            area: ['600px', '250px'],
            shadeClose: false, //点击遮罩关闭
            content: '<div class="modal-body">' +
            '<input type="text" class="form-control" placeholder="" id="smContent" style="ime-mode:inactive;">' +
            '<p class="text-danger"></p>' +
            '<p class="text-danger">* 请将光标定位在该文框中并且确保当前输入法为英文</p>' +
            '<p class="text-danger">扫码过程中，请勿随意操作其他界面</p></div>' +
            '<div class="clearfix"></div>' +
            '<div class="text-center" style="margin: 10px 0px;">' +
            '<button type="button" class="btn btn-primary" onclick="getSmContent();">扫码完成，填充数据</button>' +
            '</div>',
            success: function (layero, index) {
                $("#smContent").val('');//清空扫码内容文本框
                $("#smContent").focus();  //定位光标
            }
        });
    }
    $(function () {
        $("#scan").click(function () {
            openScanWindows();
        });

        $("#addRow").click(function () {
            addRow();
        });

        $("#resetting").click(function () {
            resetting();
        });

        $("#invoiceCheck").click(function () {
            invoiceCheck();
        });

    });

    function invoiceCheck(){
        var invoices = [];
        var $trs = $("#optionContainer").find("tbody").find("tr");
        var errorInfo="";
        $.each($trs, function (i, m) {
            var obj = {};
            var invoiceCode = $(this).find("input[name='invoiceCode']").val();
            var invoiceNumber = $(this).find("input[name='invoiceNumber']").val();
            var invoiceCheckCode = $(this).find("input[name='invoiceCheckCode']").val();
            if(!invoiceCode || !invoiceNumber || !invoiceCheckCode){
                var line = i + 1;
                errorInfo += "第"+line+"行发票信息不完整\r\n";
            }
            var invoiceType = $(this).find("input[name='invoiceType']").val();
            var invoiceAmount = $(this).find("input[name='invoiceAmount']").val();
            var invoiceDate = $(this).find("input[name='invoiceDate']").val();
            var randomNumber = $(this).find("input[name='randomNumber']").val();
            obj.INVOICE_CODE = invoiceCode;
            obj.INVOICE_NUMBER = invoiceNumber;
            obj.INVOICE_CHECK_CODE = invoiceCheckCode;
            obj.INVOICE_TYPE = invoiceType;
            obj.INVOICE_AMOUNT = invoiceAmount;
            obj.INVOICE_DATE = invoiceDate;
            obj.RANDOM_NUMBER = randomNumber;
            invoices.push(obj);
        });
        if(errorInfo != ""){
            alert(errorInfo);
            return;
        }

        var params = {};
        params.invoices = invoices;

        $.ajax({
            type : "post",
            datatype : "json",
            data:{json:JSON.stringify(params)},
            url :ctx+"/invoiceCheck/getInvoices.htm",
            async : true,
            success : function(data) {
                $("#register_invoice_table tr:not(:first)").remove();
                data = $.parseJSON(data);
                //jQuery("#qrOutput").qrcode(data.url);
                if(data.RESULT_CODE == "10000"){
                    alert("发票入账成功");
                    var invoiceRegisterList = data.registerinvoicelist;
                    for(var i=0;i<invoiceRegisterList.length;i++){
                        var registerinvoice = invoiceRegisterList[i];
                        var rowdata = "<tr>" +
                                "<td>"+registerinvoice.INVOICE_CODE+"</td>" +
                                "<td>"+registerinvoice.INVOICE_NUMBER+"</td>" +
                                "<td>"+registerinvoice.INVOICE_DATE+"</td>" +
                                "<td>"+registerinvoice.INVOICE_AMOUNT+"</td>" +
                                "<td>"+registerinvoice.PURCHASER_NAME+"</td>" +
                                "<td>"+registerinvoice.INVOICE_STATUS_CN+"</td>" +
                                "</tr>";
                        $("#register_invoice_table").append(rowdata);
                    }
                }else if(data.RESULT_CODE == "10001"){//不是平台开具
                    alert(data.RESULT_MESSAGE);
                }else if(data.RESULT_CODE == "10002"){//说明发票之前查验过
                    var rowdata = "<tr>" +
                            "<td>"+data.registerinvoice.INVOICE_CODE+"</td>" +
                            "<td>"+data.registerinvoice.INVOICE_NUMBER+"</td>" +
                            "<td>"+data.registerinvoice.INVOICE_TIME+"</td>" +
                            "<td>"+data.registerinvoice.TOTAL_AMOUNT+"</td>" +
                            "<td>"+data.registerinvoice.PURCHASER_NAME+"</td>" +
                            "<td>"+data.registerinvoice.INVOICE_STATUS_CN+"</td>" +
                            "</tr>";
                    $("#register_invoice_table").append(rowdata);
                    alert(data.RESULT_MESSAGE);
                }
            },
            error : function(){

            }
        })


    }

    function getSmContent() {
        var smContent = $("#smContent").val();
        if (null == smContent || '' == smContent) {
            layer.alert('扫码枪获取信息为空，无法填充！');
            return;
        }
        //判断是否只包含字母 数字 英文逗号
        var checkContent = /^[A-Za-z0-9,\.]+$/;
        if (!checkContent.test(smContent)) {
            layer.alert('扫码枪获取信息格式不合法，无法填充！');
            $("#smContent").val('');//清空扫码内容文本框
            return;
        }
        //如果最后一位是逗号 去除
        var comma = /,$/;
        smContent = smContent.replace(comma, "");
        var invoiceCode, invoiceNumber, invoiceCheckCode;
        var arr = new Array();
        arr = smContent.split(",");
        var number = arr.length;
        //判断数组长度
        if (number != 8) {
            layer.alert('扫码枪获取信息格式不合法，无法填充！');
            $("#smContent").val('');//清空扫码内容文本框
            return;
        }
        invoiceCode = arr[2];
        invoiceNumber = arr[3];
        invoiceCheckCode = arr[6];
        if (typeof(invoiceCode) == 'undefined' || typeof(invoiceNumber) == 'undefined' || typeof(invoiceCheckCode) == 'undefined') {
            layer.alert('扫码枪获取信息格式不合法，无法填充！');
            $("#smContent").val('');//清空扫码内容文本框
            return;
        }
        var isExistRow;
        isExistRow = setSmData(invoiceCode, invoiceNumber, invoiceCheckCode);
        //如果不存在空数据行,自动添加行并填充数据
        if (!isExistRow) {
            addRow();
            setSmData(invoiceCode, invoiceNumber, invoiceCheckCode);
        }
    }
    //添加一行
    function addRow() {
        var $tr = $("#optionContainer").find("tbody").find("tr:first");
        var $tmp_tr = $tr.clone(true);
        $tmp_tr.find("input[name='invoiceCode']").val("");
        $tmp_tr.find("input[name='invoiceNumber']").val("");
        $tmp_tr.find("input[name='invoiceCheckCode']").val("");
        $("#optionContainer").find("tbody").append($tmp_tr);
        $tmp_tr.find("td:eq(3)").html("<a  href=\"javascript:void(0)\" onclick=\"deleteRow(this)\">删除</a>");
    }

    //填充扫码数据
    function setSmData(invoiceCode, invoiceNumber, invoiceCheckCode) {
        //填充数据
        var $trs = $("#optionContainer").find("tbody").find("tr");

        var isExistRow = false;
        $.each($trs, function (i, m) {
            var $invoiceCode = $(this).find("input[name='invoiceCode']");
            var $invoiceNumber = $(this).find("input[name='invoiceNumber']");
            var $invoiceCheckCode = $(this).find("input[name='invoiceCheckCode']");
            var temp_invoiceCode = $invoiceCode.val();
            var temp_invoiceNumber = $invoiceNumber.val();
            var temp_invoiceCheckCode = $invoiceCheckCode.val();
            if ((null == temp_invoiceCode || temp_invoiceCode == '')
                && (null == temp_invoiceNumber || '' == temp_invoiceNumber)
                && (null == temp_invoiceCheckCode || '' == temp_invoiceCheckCode)) {
                isExistRow = true;
                $invoiceCode.val(invoiceCode);
                $invoiceNumber.val(invoiceNumber);
                $invoiceCheckCode.val(invoiceCheckCode);
                $("#smContent").val('');//清空扫码内容文本框
                $("#smContent").focus();  //定位光标
                return false;
            }
        });
        return isExistRow;
    }
    //重置
    function resetting() {
        //删除除第一行以外所有行
        $("#optionContainer").find("tbody").find("tr:gt(0)").remove();
        //清空第一行数据
        var $tr = $("#optionContainer").find("tbody").find("tr:first");
        $tr.find("input[name='invoiceCode']").val("");
        $tr.find("input[name='invoiceNumber']").val("");
        $tr.find("input[name='invoiceCheckCode']").val("");
        $("#register_invoice_table tr:not(:first)").remove();

    }

    //删除行
    function deleteRow(currenttd){
        if($("#optionContainer tr").length==2){
            return;
        }
        //获取父节点tr
        var hang = $(currenttd.parentNode).parent().prevAll().length+1;
        //var lie = $(temp.parentNode).prevAll().length+1;
        $("#optionContainer tr:eq("+hang+")").remove();
    }


</script>
</body>
</html>