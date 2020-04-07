Array.prototype.max = function()
{
	var i, max = this[0];
	
	for( i = 1; i < this.length; i++ )
	{
		if( max < this[i] )
		{ 
			max = this[i];
		}
	}
	return max;
};

String.prototype.trim = function()
{
    return this.replace( /(^\s*)|(\s*$)/g, "" );
};

function isAlphaNumeric( strValue,boxObj,paramsObj)
{
	return checkExp( /^\w*$/gi, strValue );
}

function isDate( strValue,boxObj,paramsObj )
{
	if( isEmpty( strValue ) ) {return true;}

	if( !checkExp( /^\d{4}-[01]?\d-[0-3]?\d$/, strValue ) ) 
	{
		return false;
	}
	
	var arr = strValue.split( "-" );
	var year = arr[0];
	var month = arr[1];
	var day = arr[2];
	
	if(year<1900||year>2060)
	{
		return false;
	}

	if( !( ( 1<= month ) && ( 12 >= month ) && ( 31 >= day ) && ( 1 <= day ) ) )
	{
		return false;
	}
		
	if( !( ( year % 4 ) == 0 ) && ( month == 2) && ( day == 29 ) )
	{
		return false;
	}
	
	if( ( month <= 7 ) && ( ( month % 2 ) == 0 ) && ( day >= 31 ) )
	{
		return false;
	}
	
	if( ( month >= 8) && ( ( month % 2 ) == 1) && ( day >= 31 ) )
	{
		return false;
	}
	
	if( ( month == 2) && ( day >=30 ) )
	{
		return false;
	}
	
	return true;
}

function isShortDate( strValue,boxObj,paramsObj )
{
	var DATETIME = strValue;
	if( isEmpty( strValue ) ) return true;
	if( !checkExp(/^\d{4}-[01]?\d/g,DATETIME) )
	{
		return false;
	}

	var arr = DATETIME.split( "-" );
	var year = arr[0];
	var month = arr[1];
	if(year<1753)
	{
		return false;
	}

	if(arr.length==3)
	{
	   return false;
	}
	if( !((1<= month ) && ( 12 >= month )))
	{
		return false;				
	}
	
	return true;
}

function isEmail( strValue,boxObj ,paramsObj)
{
	if( isEmpty( strValue ) ) return true;

	var pattern = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
	return checkExp( pattern, strValue );
	
}

function isMultipleEmail( strValue,boxObj ,paramsObj)
{
	if( isEmpty( strValue ) ) return true;
	var pattern = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
	var emailArr = strValue.split(';');
	var changeFlag = true;
	for(var i=0; i < emailArr.length; i++){
		if(emailArr[i]){
			if(pattern.test(emailArr[i])){
				changeFlag = true;
			}else{
				changeFlag = false;
				break;
			}
		}

	}
	return changeFlag ? true : false;
}



function isNumeric( strValue,boxObj,paramsObj )
{
	if( null == strValue || isEmpty( strValue ) ) return true;
	if( !checkExp( /^[+-]?\d+(\.\d+)?$/g, strValue ))
	{
		return false;
	}
	return true;
}

function isMoney( strValue,boxObj,paramsObj )
{
	if( isEmpty( strValue ) ) return true;
	
	return checkExp( /^[+-]?\d+(,\d{3})*(\.\d+)?$/g, strValue );
}

function isPhone( strValue,boxObj )
{
	if( isEmpty( strValue ) ) return true;
	
	return checkExp( /(^\(\d{3,5}\)\d{6,8}(-\d{2,8})?$)|(^\d+-\d+$)|(^(130|131|135|136|137|138|139)\d{8}$)/g, strValue );
}

function isPostalCode( strValue,boxObj,paramsObj )
{
	if( isEmpty( strValue ) ) return true;
	if(!checkExp( /(^$)|(^\d{6}$)/gi, strValue ))
	{
		return false;
	}
	return true;
}

function isURL( strValue,boxObj ,paramsObj)
{
	if( isEmpty( strValue ) ) return true;
	
	
	  var pattern = /[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/;  
	//var pattern = /^(http|https|ftp):\/\/(\w+\.)+[a-z]{2,3}(\/\w+)*(\/\w+\.\w+)*(\?\w+=\w*(&\w+=\w*)*)*/gi;
	// var pattern = /^(http|https|ftp):(\/\/|\\\\)(\w+\.)+(net|com|cn|org|cc|tv|[0-9]{1,3})((\/|\\)[~]?(\w+(\.|\,)?\w\/)*([?]\w+[=])*\w+(\&\w+[=]\w+)*)*$/gi;
	// var pattern = ((http|https|ftp):(\/\/|\\\\)((\w)+[.]){1,}(net|com|cn|org|cc|tv|[0-9]{1,3})(((\/[\~]*|\\[\~]*)(\w)+)|[.](\w)+)*(((([?](\w)+){1}[=]*))*((\w)+){1}([\&](\w)+[\=](\w)+)*)*)/gi;
	
	return checkExp( pattern, strValue );
	
}
function trim(strValue)
{
	if(!strValue||strValue=='') return strValue;
	while(strValue.substring(0,1)==' ')
	{
		strValue=strValue.substring(1);
	}
	if(strValue=='') return strValue;
	while(strValue.substring(strValue.length-1,strValue.length)==' ')
	{
		strValue=strValue.substring(0,strValue.length-1);
	}
	return strValue;
}

function isNotEmpty( strValue,boxObj,paramsObj )
{
	try
	{
		if (boxObj.innerHTML.indexOf("none") < 0)
		{
			strValue=trim(strValue);
			if( !strValue||strValue == '' )
				return false;
			else
				return true;	
		}
	}catch(e){
		
	}

	return true;
}

function isEmpty( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue == "" )
		return true;
	else
		return false;
}

/**
 * 整型校验
 * @param strValue
 * @param boxObj
 * @param paramsObj
 */
function isInteger( strValue,boxObj,paramsObj ){
	if(null == strValue|| isEmpty( strValue ) ) return true;
	if( !checkExp( /^\d+$/g, strValue ))
	{
		return false;
	}
	return true;
}

/**
 * decimal(3,2)
 */
function isThreeBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,3-2,boxObj,paramsObj);
}

/**
 *   decimal(4,2)
 */
function isFourBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,4-2,boxObj,paramsObj);
}

/**
 *   decimal(5,2)
 */
function isFiveBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNegNumeric(strValue,0,5-2,boxObj,paramsObj);
}

/**
 *   decimal(6,2)
 */
function isSixBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,6-2,boxObj,paramsObj);
}
/**
 * 允许负数
 * decimal(6,2)
 */
function isSixBitsNegDecimal(strValue,boxObj,paramsObj){
	return isLimitNegNumeric(strValue,0,6-2,boxObj,paramsObj);
}

/**
 *   decimal(7,2)
 */
function isSevenBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,7-2,boxObj,paramsObj);
}

/**
 *   decimal(8,0)
 */
function isEightBitsTooDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,8-0,boxObj,paramsObj);
}
/**
 *   decimal(8,2)
 */
function isEightBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,8-2,boxObj,paramsObj);
}

/**
 *   decimal(9,2)
 */
function isNineBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,9-2,boxObj,paramsObj);
}
/**
 *   decimal(10,2)
 */
function isTenBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,10-2,boxObj,paramsObj);
}
/**
 *   decimal(12,2)
 */
function isTwelveBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,12-2,boxObj,paramsObj);
}

/**
 *   decimal(14,2)
 */
function isFourteenBitsDecimal(strValue,boxObj,paramsObj){
	return isLimitNumeric(strValue,0,14-2,boxObj,paramsObj);
}


/**
 * 带小数的数字共通
 * @param strValue
 * @param from
 * @param to
 * @param boxObj
 * @param paramsObj
 * @returns {Boolean}
 */
function isLimitNumeric( strValue,from,len,boxObj,paramsObj )
{
	if( null == strValue || isEmpty( strValue ) ) return true;
	strValue = strValue.replace(/,/g, "");
	if( !checkExp( /^[+-]?\d+(\.\d{0,2})?$/g, strValue ))
	{
		return false;
	}
	
	if(Number(strValue)< Number(from) || Number(strValue) >= sameNumPlus(len)){
		return false;
	}
	return true;
}

/**
 * 允许负数的数字共通
 * @param strValue
 * @param from
 * @param to
 * @param boxObj
 * @param paramsObj
 * @returns {Boolean}
 */
function isLimitNegNumeric( strValue,from,len,boxObj,paramsObj )
{
	if( null == strValue || isEmpty( strValue ) ) return true;
	if( !checkExp( /^[+-]?\d+(\.\d{0,2})?$/g, strValue ))
	{
		return false;
	}
	
	if(Math.abs(strValue) >= sameNumPlus(len)){
		return false;
	}
	return true;
}

/**
 * 同一个数相乘
 */
function sameNumPlus(len){
	if(len == 0 ){
		return 0;
	}
	var sum = 1;
	for(var i = 0 ; i < len ; i++){
		sum *= 10;
	}
	return sum;
}
function textareaLength3(strValue,boxObj,paramsObj){
	return maxLength(strValue,3,boxObj,paramsObj);
}
function textareaLength6(strValue,boxObj,paramsObj){
	return maxLength(strValue,6,boxObj,paramsObj);
}
function textareaLength8(strValue,boxObj,paramsObj){
	return maxLength(strValue,8,boxObj,paramsObj);
}
function textareaLength9(strValue,boxObj,paramsObj){
	return maxLength(strValue,9,boxObj,paramsObj);
}
function textareaLength10(strValue,boxObj,paramsObj){
	return maxLength(strValue,10,boxObj,paramsObj);
}
function textareaLength11(strValue,boxObj,paramsObj){
	return maxLength(strValue,11,boxObj,paramsObj);
}
function textareaLength15(strValue,boxObj,paramsObj){
	return maxLength(strValue,15,boxObj,paramsObj);
}
function textareaLength16(strValue,boxObj,paramsObj){
	return maxLength(strValue,16,boxObj,paramsObj);
}
function textareaLength(strValue,boxObj,paramsObj){
	return maxLength(strValue,32,boxObj,paramsObj);
}
function textareaLength31(strValue,boxObj,paramsObj){
	return maxLength(strValue,31,boxObj,paramsObj);
}
function textareaLength32(strValue,boxObj,paramsObj){
	return maxLength(strValue,32,boxObj,paramsObj);
}
function textareaLength63(strValue,boxObj,paramsObj){
	return maxLength(strValue,63,boxObj,paramsObj);
}
function textareaLength64(strValue,boxObj,paramsObj){
	return maxLength(strValue,64,boxObj,paramsObj);
}
function textareaLength128(strValue,boxObj,paramsObj){
	return maxLength(strValue,128,boxObj,paramsObj);
}
function textareaLength255(strValue,boxObj,paramsObj){
	return maxLength(strValue,255,boxObj,paramsObj);
}
function textareaLength256(strValue,boxObj,paramsObj){
	return maxLength(strValue,256,boxObj,paramsObj);
}
function textareaLength512(strValue,boxObj,paramsObj){
	return maxLength(strValue,512,boxObj,paramsObj);
}
function textareaLength1023(strValue,boxObj,paramsObj){
	return maxLength(strValue,1023,boxObj,paramsObj);
}
function textareaLength1024(strValue,boxObj,paramsObj){
	return maxLength(strValue,1024,boxObj,paramsObj);
}
function textareaLength2048(strValue,boxObj,paramsObj){
	return maxLength(strValue,2048,boxObj,paramsObj);
}
function textareaLength4096(strValue,boxObj,paramsObj){
	return maxLength(strValue,4096,boxObj,paramsObj);
}
function textareaLength65535(strValue,boxObj,paramsObj){
	return maxLength(strValue,65535,boxObj,paramsObj);
}
function textareaMinLength8(strValue,boxObj,paramsObj){
	return minLength(strValue,8,boxObj,paramsObj);
}
function textLength(strValue,boxObj,paramsObj){
	return maxLength(strValue,paramsObj.textLength_methodparams.zhlen,boxObj,paramsObj);
}

function maxLength(strValue,len,boxObj,paramsObj){
	if( null == strValue || isEmpty( strValue ) ) return true;
	return strValue.replace(/[^\x00-\xff]/gi, "--").length<=len;
}

function minLength(strValue,len,boxObj,paramsObj){
	if( null == strValue || isEmpty( strValue ) ) return true;
	return strValue.replace(/[^\x00-\xff]/gi, "--").length>=len;
}

/**
 * 最小值是0 前提是已经加入 isNumberic校验
 * @param strValue
 * @param boxObj
 * @param paramsObj
 */
function isNumMin(strValue,boxObj,paramsObj){
	if( null == strValue || isEmpty( strValue ) ) return true;
	if(Number(strValue)< 0){
		return false;
	}
	return true;
}


/**
 * 验证常规字符
 * （只能输入大小写英文字母、数字、下划线）
 *
 * @parameter string str 字符串
 * @return boolean
 */
function isSimpleStr(str){
 if (str.length <1)
 {
	 return true;
 }
 return str.match(/^[a-zA-Z]\w+$/);
}

/**
 * 验证英文
 * （只能输入大小写英文字母）
 *
 * @parameter string str 字符串
 * @return boolean
 */
function isEn(strValue,boxObj,paramsObj )
{
	if( null == strValue || isEmpty( strValue ) ) return true;
	if( !checkExp(/^[a-zA-Z]+$/, strValue ))
	{
		return false;
	}
	return true;
}


function isMobilePhone(strValue,boxObj,paramsObj )
{
  if(null==strValue||""==strValue){
  return true;
  }
	var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
	return myreg.test(strValue);
}

