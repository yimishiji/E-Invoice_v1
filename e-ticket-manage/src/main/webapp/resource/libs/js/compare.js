/**
 * @date 2016-04-18
 * @author d.dw
 */
$(function(){
	//compare.init();
})

var compare = function(){
	
	function init(){
		var tds = $("td[showold='true']");
		for(var i=0;i<tds.length;i++){
			var tdtitle = $(tds[i]).attr("title");
			var divid = "WX_CONTENT_"+$(tds[i]).attr("reportid");
			var titlespan = $("<span class='spanTipContainer'>"+tdtitle+"</span>");
			var spanid = "id_" + parseInt((Math.random() * 100000))+ parseInt((Math.random() * 100000));
			titlespan.attr("id",spanid);
			$(tds[i]).attr("spanid",spanid);
			titlespan.css("display","block");
			titlespan.css("position","absolute");
			titlespan.css("left",$(tds[i]).position().left);
			var tdtop = $(tds[i]).position().top + $(tds[i]).height();
			titlespan.css("top",tdtop);
			$("#"+divid).append(titlespan);
			$(tds[i]).unbind("mouseover").bind("mouseover",function(){
				var titlespanid =  $(this).attr("spanid");
				var titlespan = $(".spanTipContainer[id='"+titlespanid+"']");
				titlespan.css("display","block");
				titlespan.css("left",$(this).position().left);
				var tdtop = $(this).position().top + $(this).height();
				titlespan.css("top",tdtop);
			})
			$(tds[i]).unbind("mouseout").bind("mouseout",function(){
				var titlespanid =  $(this).attr("spanid");
				var titlespan = $(".spanTipContainer[id='"+titlespanid+"']");
				titlespan.css("display","none");
				titlespan.css("left",$(this).position().left);
				var tdtop = $(this).position().top + $(this).height();
				titlespan.css("top",tdtop);
			})
		}
	}
	
	
	return{
		init : function(){setTimeout(init(),3000)}
	}
}();

$(window).resize(function(){
	compare.init();
})