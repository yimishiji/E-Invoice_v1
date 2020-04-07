<script>
    var handler = function () {
        event.preventDefault();
        event.stopPropagation();
    };
	$(".close").click(function(event) {
		$(".invoice_down,.down").hide();
		document.getElementsByTagName("body")[0].setAttribute("style","overflow:auto;height:100%;");
		document.body.removeEventListener('touchmove',handler,false);
        document.body.removeEventListener('wheel',handler,false);
	});
	$(".close1").click(function(event) {
		$(".invoice_down,.down").hide();
		document.getElementsByTagName("body")[0].setAttribute("style","overflow:auto;height:100%;");
		document.body.removeEventListener('touchmove',handler,false);
        document.body.removeEventListener('wheel',handler,false);
	});
	window.alert = function(str){
		$("#loading").hide();
		$(".last_p").html(str);
		$(".down,#commMagTab").show();
	}
</script>