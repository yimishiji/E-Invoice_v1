/**
 * core. grid
 */

(function($,rz){
	rz.rzGrid = {
		grid:null,
		tableName:"",
		//字段对应实体名 第一个为ID 第2个为NAME
		fileds:["ID","Name"],
		//用来与前面可变操作对应的
		column:[""],
		data:[],
		setting:{},
		//选中的数组
		checkedArray:[],
		//选中的文本
		checkedTextArray :[],
		//设置TableName
		setTableName:function(name){
			this.tableName = name;
		},
		//设置数据
		setData:function(data){
			this.data = data;
		},
		//设置初始化参数
		setSetting:function(options){
			this.setting = options;
		},
		//设置字段参数
		setFileds:function(options){
			if(!!options.fileds) this.fileds = options.fileds;
		},
		//解析模板 并判断数据的值来实现权限控制
		parseTemplate:function(rowdata,html,param){
			var temp = html.replace(new RegExp("\\{0\\}","g"), param[0]);
        	  var $div = $("<div id='temp'></div>");
        	  	  $div.append(temp);
        	  var as = $div.find("a[showFlag]");
        	  (function(_as){
        		  $(_as).each(function(i,n){
        			  var flag = $(this).attr("showFlag");
        			  if(param[1] != flag){
        				  $div.find("a[showFlag="+flag+"]").remove();
        			  }        		   
        		  });
        	  })(as);
			return $div.html();
		},
		//更新数据表格数据
		updateGrid:function(data){
			if(!!!this.data) this.setData(options.data);
			 var gridData={"form.paginate.pageNo":1,"form.paginate.totalRows":6,"rows":result};
             rz.rzGrid.grid.loadData(gridData);    
		},
		//通过读取后台得到数据
		updateGridByUrl:function(opt){
			$.post(opt.url,
		           opt.params,
		           function(result){
		           var gridData={"pager.pageNo":result.page.OPER_DATA_PAGEPARAM_KEY.pageIndex,"pager.totalRows":result.page.OPER_DATA_PAGEPARAM_KEY.totalCount,"rows":result.infoList};
		           //将分页信息重新保存
		           $("#pageIndex").val(gridData["pager.pageNo"]);
		           $("#pageSize").val(result.page.OPER_DATA_PAGEPARAM_KEY.pageSize);
		           rz.rzGrid.grid.loadData(gridData); 
		           var $pager=$('<div class="pageArrow" showSelect="true" showNumber="true" inputPosition="right"></div>');
                   $pager.attr("total",gridData["pager.totalRows"]);
                   var $pagerCon = $('<div class="float_left padding5">' + "共有" + gridData["pager.totalRows"] + "条记录" + '</div><div class="float_right padding5"></div><div class="clear"></div>');
                   $pagerCon.eq(1).append($pager);
                   $("#pageContent").empty();
                   $("#pageContent").append($pagerCon);
                   $pager.render();
                   $pager.bind("pageChange",function(e,index){
                       var g_pageNo = index + 1;
                       var par = opt.params;
                       par.pageIndex = g_pageNo;
                       rz.rzGrid.updateByPage(opt.url,par);
                   });
                   $pager.bind("sizeChange",function(e,num){
                       var g_pageSize = num;
                       var par = opt.params;
                       par.pageSize = g_pageSize;
                       rz.rzGrid.updateByPage(opt.url,par);
                   });
		    },"json");
		},
		//分页
		updateByPage:function(url,params){
	        $.post(url,
            params,
            function(result){
            	var gridData={"pager.pageNo":result.page.OPER_DATA_PAGEPARAM_KEY.pageIndex,"pager.totalRows":result.page.OPER_DATA_PAGEPARAM_KEY.totalCount,"rows":result.infoList};
                //刷新表格
                rz.rzGrid.grid.loadData(gridData);
            },"json");

	    },
		//初始化表格
		initGrid:function(options){
			this.setTableName(options.tableName);
			if(!!options.data) this.setData(options.data);
			this.setFileds(options);
			this.initTableGrid(options);
//			return this.grid;
		},
		//初始化表格
		initTableGrid:function(options){
			var params = {};
			if(!!options.pageSize)params["pageSize"] = options.pageSize;
			else params["pageSize"] = 10;
			if(options.showPageInfo && options.showPageInfo != undefined) params["showPageInfo"] = options.showPageInfo;
			else params["showPageInfo"] = false;
			if(options.rownumbers && options.rownumbers!= undefined) params["rownumbers"] = options.rownumbers;
			else params["rownumbers"] = false;
			if(!options.checkbox && options.checkbox!= undefined)params["checkbox"] = options.checkbox;
			else params["checkbox"] = true;
			if(!!options.height) params["height"] = options.height;
			else params["height"] = "95%";
			if(!!options.width)params["width"] = options.width;
			else params["width"] = "100%";
			//如果没有checkbox
			if(params["checkbox"]){
				if(!!options.isChecked)params["isChecked"] = options.isChecked;
				else params["isChecked"] = this.checkedHandler;
				if(!!options.onCheckRow)params["onCheckRow"] = options.onCheckRow;
				else params["onCheckRow"] = this.checkRowHandler;
				if(!!options.onCheckAllRow)params["onCheckAllRow"] = options.onCheckAllRow;
				else params["onCheckAllRow"] = this.checkAllRowHandler;
			}
			if(!!options.columns)params["columns"] = options.columns;
			if(!!options.toolbar)params["toolbar"] = options.toolbar;
			if(!!options.url)params["url"] = options.url;
			if(!!options.url)params["usePager"] = options.usePager;
			else params["usePager"]=false;
			if(!!!this.data){
				params["data"] = this.data;
			}
			if(!!options.rowAttrRender){
				options["rowAttrRender"] = options.rowAttrRender;
			}
			this.setSetting(params);
			this.grid = $("#"+options.tableName).quiGrid(params);
			
		},
		//checked 初始化
		checkedHandler:function(rowdata){
			if (rz.rzGrid.findCheckedArray(rowdata[rz.rzGrid.fileds[0]]) == -1)
				return false;
			return true;
		},
		//得到选中的值
		findCheckedArray: function(id)
		{
			for (var i = 0; i < rz.rzGrid.checkedArray.length; i++)
			{
				if (this.checkedArray[i] == id)
					return i;
			}
			return -1;
		},
		//增加选中的值
		addCheckedArray:function(id, name)
		{
			if (rz.rzGrid.findCheckedArray(id) == -1) {
				rz.rzGrid.checkedArray.push(id);
				rz.rzGrid.checkedTextArray.push(name);
			}
		},
		//移除选中的值
		removeCheckedArray:function(id)
		{
			var _this = rz.rzGrid;
			var i = _this.findCheckedArray(id);
			if (i == -1)
				return;
			_this.checkedArray.splice(i, 1);
			_this.checkedTextArray.splice(i, 1);
		},
		checkRowHandler:function(checked, data)
		{
			var _this = rz.rzGrid;
			if (checked)
				_this.addCheckedArray(data[_this.fileds[0]], data[_this.fileds[1]]);
			else
				_this.removeCheckedArray(data[_this.fileds[0]], data[_this.fileds[1]]);
		},
		checkAllRowHandler:function(checked)
		{
			var _this = rz.rzGrid;
			for ( var rowid in this.records)
			{
				if (checked)
					_this.addCheckedArray(this.records[rowid][_this.fileds[0]],
							this.records[rowid][_this.fileds[1]]);
				else
					this.removeCheckedArray(_this.records[rowid][_this.fileds[0]],
							this.records[rowid][_this.fileds[1]]);

			}

		},
		//得到选中的值
		getCheckedHandler:function()
		{
			var _this = rz.rzGrid;
			return _this.checkedArray;
		}
		
		
	};
	
	$.rzGrid = rz.rzGrid;
})(jQuery,com.rongzer);