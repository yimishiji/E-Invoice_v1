/**
 * array list
 * 1、页面以 ajax方式得到json数组
 * 2、将json 数组存入到 arr 
 * @author wuyanxi 
 * @param $
 */
(function( $ ){
	var arrayList = {
		idPrefix:"operId",
		index : 0,
		arr:[],
		removeArr:[],
		htmlTemplate:"",
		params:["id"],
		//增加list增加的元素 直接增加到最后 
		add:function(list){
			this.arr.push(list);	
		},
		//已经存在
		isHaving:function(list){
			var isAdd = false;
			$.each(this.arr,function(i,node){
				if( node[$.arrayList.params[0]]== list[$.arrayList.params[0]] ){
					alert("当前id已经存在");
					isAdd = true;
					return;
				}
			});
			return isAdd;
		},
		//移除
		removeOper:function( id ){
			for(var i = 0 ,s = this.arr.length ; i < s ; i++){
				if(id == this.arr[i][this.params[0]]){
					this.arr.splice(i, 1);
					break;
				}
			}
		},
		//上移操作
		prevMove : function( id ){
			for(var i = 0 ,s = this.arr.length ; i < s ; i++){
				if(id == this.arr[i][this.params[0]]){
					var temp = this.arr[i-1];
					this.arr[i-1] = this.arr[i];
					this.arr[i] = temp;
					break;
				}
			}
			
		},
		//下移操作
		nextMove : function(id){
			for(var i = 0 ,s = this.arr.length ; i < s ; i++){
				if(id == this.arr[i][this.params[0]]){
				   var temp = this.arr[i+1];
				   this.arr[i+1] = this.arr[i];
				   this.arr[i] = temp;
				   return;
					break;
				}
			}
		},
		//置顶
		toTop : function( id ){
			
		},
		//置底
		toBottom:function(){
			
		},
		//得到当前的数组
		getCurrArr:function(){
			return this.arr;
		},
		getCurrMoveArr:function(){
			return this.removeArr;
		},
		setHtmlTemplate:function(html){
			this.htmlTemplate = html;
		},
		//设置arrList
		setArrayList:function(list){
			this.arr = list;
		},
		//设置参数集
		setParamsMap:function(params){
			this.params = params;
		},
		//设置模板的参数
		parseTemplate:function(params){
			var temp = $.arrayList.htmlTemplate;
			$.each(params,function(i){
				(function(i,o){
					temp = temp.replace(new RegExp("\\{"+i+"\\}","g"), o);
				})(i,params[i]);
			});
			return temp;
		},
		//显示table表格数据
		showTableData:function(params,tableId){
			var options = [];
			for(var i = 0 , s = params.length; i < this.arr.length; i++){
				for(var j = 0 ; j < s ; j++){
					options[j] = this.arr[i][params[j]];					
				}
				$("#"+tableId).append($.arrayList.parseTemplate(options));		
			}
		},
		//初始化table数据
		initArrayList:function(options){
			this.setHtmlTemplate(options.html);
			this.setArrayList(options.list);
			this.setParamsMap(options.params);
			this.showTableData(options.params,options.tableId);
		},
		//上移表格
		prevTable:function(obj){
			var fid = $(obj).attr("fid");
			var onthis = $(obj).parent().parent();  //得到当前行
			var getUp=onthis.prev();  
			if ($(getUp).has("td").size()==0)  
			   {  
			       alert("顶级元素不能上移");  
			       return;  
			   }  
			$.arrayList.prevMove(fid);
		    $(onthis).after(getUp);  
		},
		nextTable:function(obj){
			var fid = $(obj).attr("fid");
			var onthis = $(obj).parent().parent();  //得到当前行
			var getdown=onthis.next();
			if ($(getdown).has("td").size()==0)  
			{  
				alert("底部元素不能下移");  
				return;  
			}  
			$.arrayList.nextMove(fid);
			$(onthis).before(getdown);
		},
		removeTable:function(obj){
			var fid = $(obj).attr("fid");
			$.arrayList.removeOper(fid);
			var onthis = $(obj).parent().parent();  //得到当前行
			$(onthis).remove();
			if(!/^operId/.test(fid))
			$.arrayList.removeArr.push(fid);
		},
		addTable:function(tableId,contion){
			this.index++;
			var temp = this.parseTemplate(contion);
			var li = {};
			for(var i = 0 ; i < this.params.length ; i++){
				li[this.params[i]] = contion[i];
			}
			if(!$.arrayList.isHaving(li))
			{
				$.arrayList.add(li);
				$("#"+tableId).append(temp);
			}
		},
		createId:function(){
			return this.idPrefix + this.index;
		}
		
	};
	$.arrayList = arrayList;
})( jQuery );