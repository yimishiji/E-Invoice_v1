var outerLayout;
$(document).ready( function() {
	// create the OUTER LAYOUT
	outerLayout = $("body").layout(layoutSettings_Outer);

	// BIND events to hard-coded buttons in the NORTH toolbar
	

	var westSelector = "body > .ui-layout-west";
	var eastSelector = "body > .ui-layout-east";

	 // CREATE SPANs for pin-buttons - using a generic class as identifiers
	$("<span></span>").addClass("pin-button").prependTo(westSelector);
	$("<span></span>").addClass("pin-button").prependTo(eastSelector);
	// BIND events to pin-buttons to make them functional
	outerLayout.addPinBtn(westSelector +" .pin-button", "west");
	outerLayout.addPinBtn(eastSelector +" .pin-button", "east");

	 // CREATE SPANs for close-buttons - using unique IDs as identifiers
	$("<span></span>").attr("id", "west-closer").prependTo(westSelector);
	$("<span></span>").attr("id", "east-closer").prependTo(eastSelector);
	// BIND layout events to close-buttons to make them functional
	//outerLayout.addCloseBtn("#west-closer", "west");
	//outerLayout.addCloseBtn("#east-closer", "east");

});
var layoutSettings_Outer = {
	name: "outerLayout" // NO FUNCTIONAL USE, but could be used by custom code to 'identify' a layout
	// options.defaults apply to ALL PANES - but overridden by pane-specific settings
,	defaults: {
		size:					"auto"
	,	minSize:				50
	,	paneClass:				"pane" 		// default = 'ui-layout-pane'
	,	resizerClass:			"resizer"	// default = 'ui-layout-resizer'
	,	togglerClass:			"toggler"	// default = 'ui-layout-toggler'
	,	buttonClass:			"button"	// default = 'ui-layout-button'
	,	contentSelector:		".content"	// inner div to auto-size so only it scrolls, not the entire pane!
	,	contentIgnoreSelector:	"span"		// 'paneSelector' for content to 'ignore' when measuring room for content
	,	togglerLength_open:		35			// WIDTH of toggler on north/south edges - HEIGHT on east/west edges
	,	togglerLength_closed:	35			// "100%" OR -1 = full height
	,	hideTogglerOnSlide:		true		// hide the toggler when pane is 'slid open'
	,	togglerTip_open:		"收起面板"
	,	togglerTip_closed:		"展开面板"
	,	resizerTip:				"拖动面板"
	,	sliderTip:				"展开面板"
	//	effect defaults - overridden on some panes
	,	fxName:					"slide"		// none, slide, drop, scale
	,	fxSpeed_open:			750
	,	fxSpeed_close:			1500
	,	fxSettings_open:		{easing: "easeInQuint"}
	,	fxSettings_close:		{easing: "easeOutQuint"}
}
,	north: {
	    minSize:				28
	,   size:					200
	,	spacing_closed:			5			// HIDE resizer & toggler when 'closed'
	,	resizable: 				true
	,	slidable:				true		// REFERENCE - cannot slide if spacing_closed = 0
	,	initClosed:				false
	}
,	south: {
	    minSize:				28
	,   size:					200
	,	spacing_closed:			5			// HIDE resizer & toggler when 'closed'
	,	resizable: 				true
	,	slidable:				true		// REFERENCE - cannot slide if spacing_closed = 0
	,	initClosed:				false
	}
,	west: {
		size:					120
	,	spacing_closed:			21			// wider space when closed
	,	togglerLength_closed:	21			// make toggler 'square' - 21x21
	,	togglerAlign_closed:	"top"		// align to top of resizer
	,	togglerLength_open:		0			// NONE - using custom togglers INSIDE west-pane
	,	togglerTip_open:		"收起主菜单"
	,	togglerTip_closed:		"展开主菜单"
	,	resizerTip_open:		"拖动面板"
	,	slideTrigger_open:		"click" 	// default
	,	resizable: 				true
	,	slidable:				false		// REFERENCE - cannot slide if spacing_closed = 0
	,	initClosed:				false
	//	add 'bounce' option to default 'slide' effect
	,	fxSettings_open:		{easing: "easeOutBounce"}
	}
,	east: {
		size:					200
	,	resizable: 				true
	,	spacing_closed:			21			// wider space when closed
	,	togglerLength_closed:	21			// make toggler 'square' - 21x21
	,	togglerAlign_closed:	"top"		// align to top of resizer
	,	togglerLength_open:		0 			// NONE - using custom togglers INSIDE east-pane
	,	togglerTip_open:		"关起工具箱"
	,	togglerTip_closed:		"打开工具箱"
	,	resizerTip_open:		"拖动面板"
	,	slideTrigger_open:		"click"
	,	initClosed:				true
	//	override default effect, speed, and settings
	,	fxName:					"drop"
	,	fxSpeed:				"normal"
	,	fxSettings:				{easing: ""} // nullify default easing
	}
,	center: {
		minWidth:				200
	,	minHeight:				200
	}
};