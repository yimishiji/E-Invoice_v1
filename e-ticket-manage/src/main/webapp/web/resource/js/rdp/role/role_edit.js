/**
 * 加载函数
 */
function load(){
	//获得角色Id
	var roleId = $("#roleId").val();
	//调用后台获得角色的权限
	$.ajax({
		async : "false",
		dataType:"json",
		data : {cid:"M0000",id:roleId},                                     
		url : ctx+"/role/getChangePermissionList.htm",
		success : function(data,textStatus) {
			createTable(data);
		}
	});	
}
/**
 * 创建权限table
 * @param data
 */
function createTable(data) {
	var html = "";
	html = html + "<table id='showRoleCheckBox' class='cls-data-table-detail' style=\"table-layout: fixed;margin-top:10px;\"";
	html = html + "width=\"100%\" cellspacing='0' cellpadding='0'>";
	html = html + "";
	html = html + "<tr>";
	html = html + "<td class='cls-data-th-detail' style='width:15%;text-align:center'>模块名称</td>";
	html = html + "<td class='cls-data-th-detail' style='width:40%;text-align:center'>功能名称</td>";
	html = html + "<td class='cls-data-th-detail' style='text-align:center'>操作权限</td>";
	html = html + "</tr>";
	//获得模块集合
	var modelNameList = data.parentList;
	//获得功能名称
	var menuFunctionMap = data.menuFunctionMap;
	//获得操作权限
	var resouceMap = data.resouceMap;
	//获得角色的操作权限
	var permissionMap = data.permissonsMap;
	//创建html
	//循环开始
	for(var i = 0;i < modelNameList.length; i++) {
		//模块名称
		var parentName = modelNameList[i].funcName;
		//获得模块的Id
		var modelId = modelNameList[i].funcId;
		//获得功能对象集合
		var functionList = menuFunctionMap[modelId];
		// 模块名称是否选中
		html = html + "<tr width='100%'>";
		if(null != functionList && 0 != functionList.length) {
			// 从map中获得是否有值
			if(null != permissionMap && null != permissionMap[modelId]) {
				html = html + "<td class='cls-data-th-detail' style='text-align:left' rowspan='"+functionList.length+"'>" +
				"<input checked='true' type='checkbox' value='"+modelId+"' id='model"+modelId+"' onclick=\"doChangFunAndResouce('"+modelId+"')\"/>" + 
				"<lable onclick=\"doChangLabelFunAndResouce('"+modelId+"')\">" + parentName + "</lable></td>";
			} else {
				html = html + "<td class='cls-data-th-detail' style='text-align:left'  rowspan='"+functionList.length+"'>" +
				"<input  type='checkbox' value='"+modelId+"' id='model"+modelId+"' onclick=\"doChangFunAndResouce('"+modelId+"')\"/>" + 
				"<lable onclick=\"doChangLabelFunAndResouce('"+modelId+"')\">" + parentName + "</lable></td>";
			}
			
		} else {
			// 从map中获得是否有值
			if(null != permissionMap && null != permissionMap[modelId]) {
				html = html +  "<td class='cls-data-th-detail' style='text-align:left'>" + 
				"<input checked='true' type='checkbox' value='"+modelId+"' id='model"+modelId+"' onclick=\"doChangFunAndResouce('"+modelId+"')\"/>" + 
				"<lable onclick=\"doChangLabelFunAndResouce('"+modelId+"')\">" +parentName + "</lable/></td>";
			} else {
				html = html +  "<td class='cls-data-th-detail' style='text-align:left'>" + 
				"<input  type='checkbox' value='"+modelId+"' id='model"+modelId+"' onclick=\"doChangFunAndResouce('"+modelId+"')\"/>" + 
				"<lable onclick=\"doChangLabelFunAndResouce('"+modelId+"')\">" +parentName + "</lable/></td>";
			}
			
		}
		//循环功能
		if(null != functionList) {
			//循环获得功能名称
			for(var j = 0; j < functionList.length; j++) {
				//获得功能名称
				var functionName = functionList[j].funcName;
				//获得功能Id
				var functionId = functionList[j].funcId;
				//获得资源集合
				var resouceList = resouceMap[functionId];
				//拼接功能 
				if(0 != j) {
					html = html + "<tr>";
				} 
				html = html + "<td class='cls-data-th-detail' style='background:#ffffff;text-align:left' >";
				//从map中获得是否有值
				if(null != permissionMap && null != permissionMap[functionId]) {
					html = html + "<input type=\"checkbox\" id=\"fun"+functionId+"\" checked='true'  name=\""+modelId+"\" onclick=\"doChangeResouce('"+functionId+"','"+modelId+"')\" value=\""+functionId+"\">";
				} else {
					html = html + "<input type=\"checkbox\" id=\"fun"+functionId+"\"  name=\""+modelId+"\" onclick=\"doChangeResouce('"+functionId+"','"+modelId+"')\" value=\""+functionId+"\">";
				}
				
				html = html + "<lable onclick=\"doChangeResouceByLabel('"+functionId+"','"+modelId+"')\">" + functionName + "</lable>";
				html = html + "</td>";
				//循环获得资源
				if(null != resouceList) {
					for(var k = 0; k < resouceList.length; k++) {
						//获得资源名称
						var resouceName = resouceList[k].resouceName;
						//获得资源Id
						var resouceId = resouceList[k].resouceId;
						//拼接资源名称
						if(k == 0) {
							html = html + "<td class='cls-data-th-detail' style='background:#ffffff;text-align:left'>";
						} 
						//从map中获得是否有值
						if(null != permissionMap && null != permissionMap[resouceId]) {
							html = html + "<input type=\"checkbox\" checked='true' id=\""+resouceId+"\" name=\""+functionId+"\" onclick=\"doChangeFunction('"+functionId+"','"+modelId+"')\" value=\""+resouceId+"\">";
						} else {
							html = html + "<input type=\"checkbox\" id=\""+resouceId+"\" name=\""+functionId+"\" onclick=\"doChangeFunction('"+functionId+"','"+modelId+"')\" value=\""+resouceId+"\">";
						}
						
						html = html + "<span  onclick=\"doChangeFunctionByLabel('"+resouceId+"','"+functionId+"','"+modelId+"')\">" + resouceName + "</span>";
						if(k == resouceList.length -1) {
							html = html + "</td>";
						}
					}
				} else {
					html = html + "<td class='cls-data-th-detail' style='background:#ffffff;text-align:left'></td>";
				}
				html = html + "</tr>";
			}
		}
	}
	html = html + "</table>";
	$("#showPermissionInfo").html(html);
}
/**
 * 点击模块名称时对功能名称和操作权限的控制
 * @param modelId
 */
function doChangFunAndResouce(modelId) {
	
	// 获得模块名称的check选项
	var objCheck = document.getElementById("model"+modelId).checked;
	// 循环模块名称
	var modelArray = document.getElementsByName(modelId);
	// 循环功能名称
	for(var i = 0; i < modelArray.length; i++) {
		modelArray[i].checked = objCheck;
		// 获得功能名称Id
		var functionId = modelArray[i].value;
		//获得操作权限集合
		var resouceCheckbox = document.getElementsByName(functionId);
		//循环操作权限
		for(var j = 0; j < resouceCheckbox.length; j++) {
			resouceCheckbox[j].checked = objCheck;
		} 
	}
}
/**
 * 点击模块名称文字时对功能名称和操作权限的控制
 * @param modelId
 */
function doChangLabelFunAndResouce(modelId) {
	
	// 获得模块名称的check选项
	var objCheck = document.getElementById("model"+modelId).checked;
	if(objCheck) {
		objCheck = false;
	} else {
		objCheck = true;
	}
	 document.getElementById("model"+modelId).checked = objCheck;
	// 循环模块名称
	var modelArray = document.getElementsByName(modelId);
	// 循环功能名称
	for(var i = 0; i < modelArray.length; i++) {
		modelArray[i].checked = objCheck;
		// 获得功能名称Id
		var functionId = modelArray[i].value;
		//获得操作权限集合
		var resouceCheckbox = document.getElementsByName(functionId);
		//循环操作权限
		for(var j = 0; j < resouceCheckbox.length; j++) {
			resouceCheckbox[j].checked = objCheck;
		} 
	}
}
/**
 * 实现点击文字时对资源checkbox的操作
 * @param functionId
 * 			功能Id
 */
function doChangeResouceByLabel(functionId,modelId) {
	//获得功能对象的check选项
	var objCheck = document.getElementById("fun" + functionId).checked;
	if(objCheck) {
		document.getElementById("fun" + functionId).checked = false;
	}else {
		document.getElementById("fun" + functionId).checked = true;
	}
	//对资源操作
	doChangeResouce(functionId,modelId);
}
/**
 * 实现点击文字时对功能名称checkbox的操作
 * @param resouceId
 * 			资源Id
 * @param functionId
 * 			功能
 */
function doChangeFunctionByLabel(resouceId,functionId,modelId) {
	//获得功能对象的check选项
	var objCheck = document.getElementById(resouceId).checked;
	if(objCheck) {
		document.getElementById(resouceId).checked = false;
	}else {
		document.getElementById(resouceId).checked = true;
	}
	doChangeFunction(functionId,modelId);
	
}
/**
 * 点击功能名称,操作权限选中
 * @param functionId
 */
function doChangeResouce(functionId,modelId){
	//获得功能对象的check选项
	var objCheck = document.getElementById("fun" + functionId).checked;
	//获得操作权限集合
	var resouceCheckbox = document.getElementsByName(functionId);
	//循环操作权限
	for(var i = 0; i < resouceCheckbox.length; i++) {
		resouceCheckbox[i].checked = objCheck;
	} 
	// 通过功能名称确定模块名称是否选中
	doChangeModelByFunResouce(modelId);
}
/**
 * 点击操作权限时,对功能名称的处理
 * @param functionId
 */
function doChangeFunction(functionId,modelId){
	//获得操作权限集合
	var resouceCheckbox = document.getElementsByName(functionId);
	//用来判断功能名称是否选中
	var isfunChecked = false;
	//循环操作权限
	for(var i = 0; i < resouceCheckbox.length; i++) {
		//获得操作权限的check属性
		var checkStyle = resouceCheckbox[i].checked;
		//如果有一个选中的则对功能名称进行操作
		if(checkStyle) {
			isfunChecked = true;
			break;
		} 
	} 
	
	if (isfunChecked)
	{
		document.getElementById("fun" + functionId).checked = isfunChecked;
		// 通过功能名称确定模块名称是否选中
		doChangeModelByFunResouce(modelId);
	}
	
	// 通过功能名称确定模块名称是否选中
	doChangeModelByFunResouce(modelId);
}
/**
 * 通过功能名称确定模块名称是否选中
 * @param modelId
 */
function doChangeModelByFunResouce(modelId) {
	// 用来判断功能名称是否选中
	var isfunChecked = false;
	// 获得该模块下的所有功能
	var modelArray = document.getElementsByName(modelId);
	// 循环数组
	for(var i = 0; i < modelArray.length; i++) {
		//获得功能名称的check属性
		var checkStyle = modelArray[i].checked;
		//如果有一个选中的则对功能名称进行操作
		if(checkStyle) {
			isfunChecked = true;
			break;
		} 
	}
	// 设定模块名称的值
	document.getElementById("model" + modelId).checked = isfunChecked;
}
/**
 * 保存角色的权限
 * @param paramsObj
 */
function doSaveRolePermission(paramsObj){
	var roleId = $("#roleId").val();
	var roleName = "";
	if(null == roleId || "" == roleId) {
		var reportguid=getComponentGuidById(paramsObj.pageid,paramsObj.reportid);
		//得到已保存的数据
		var dataObjArr = WX_ALL_SAVEING_DATA[reportguid];
		var dataObjTmp =dataObjArr[0];
		roleName = dataObjTmp["role_name"];
	}
	//添加权限
	addRolePermis(roleName);
}
/**
 * 添加权限
 */
function addRolePermis(roleName){
	var permi = "";
	//获得table下的所有checkbox
	var allCheckbox = document.getElementById("showRoleCheckBox").getElementsByTagName("input");  
	//循环所有checkbox
	for(var i = 0; i < allCheckbox.length; i++) {
		//判断是否选中
		if(allCheckbox[i].checked) {
			//如果选中则获得值
			var checkboxValue = allCheckbox[i].value;
			//拼接值
			if(i == 0) {
				permi = permi + checkboxValue;
			} else {
				permi = permi + ";" + checkboxValue;
			}
		}
	}
	//获得角色Id
	var roleId = $("#roleId").val();
	//获得权限
	$.ajax({
		dataType : "text",
		async : "false",
		type : "post",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8", 
		data : {roleId:roleId,permissons:permi,roleName:roleName},                                     
		url : ctx+"/role/modifyRoleInfo.htm",
		success : function(data,textStatus) {
					
		}
	});
}
