Array.prototype.max = function()
{
	var i, max = this[0];
	
	for( i = 1; i < this.length; i++ )
	{
		if( max < this[i] )
		max = this[i];
	}
	
	return max;
}

// 为 String 类增加一个 trim 方法
String.prototype.trim = function()
{
    // 用正则表达式将前后空格用空字符串替代。
    return this.replace( /(^\s*)|(\s*$)/g, "" );
}

// 验证是否 字母数字
function isAlphaNumeric( strValue,boxObj)
{
	// 只能是 A-Z a-z 0-9 之间的字母数字 或者为空
	return checkExp( /^\w*$/gi, strValue );
}

// 验证是否 日期
function isDate( strValue,boxObj )
{
	// 日期格式必须是 2001-10-1/2001-1-10 或者为空
	if( isEmpty( strValue ) ) return true;

	if( !checkExp( /^\d{4}-[01]?\d-[0-3]?\d$/, strValue ) ) 
	{
		return false;
	}
	// 或者 /^\d{4}-[1-12]-[1-31]\d$/
	
	var arr = strValue.split( "-" );
	var year = arr[0];
	var month = arr[1];
	var day = arr[2];
	
	if(year<1900||year>2060)
	{
		return false;
	}

	// 1 <= 月份 <= 12，1 <= 日期 <= 31
	if( !( ( 1<= month ) && ( 12 >= month ) && ( 31 >= day ) && ( 1 <= day ) ) )
	{
		return false;
	}
		
	// 润年检查
	if( !( ( year % 4 ) == 0 ) && ( month == 2) && ( day == 29 ) )
	{
		return false;
	}
	
	// 7月以前的双月每月不超过30天
	if( ( month <= 7 ) && ( ( month % 2 ) == 0 ) && ( day >= 31 ) )
	{
		return false;
	}
	
	// 8月以后的单月每月不超过30天
	if( ( month >= 8) && ( ( month % 2 ) == 1) && ( day >= 31 ) )
	{
		return false;
	}
	
	// 2月最多29天
	if( ( month == 2) && ( day >=30 ) )
	{
		return false;
	}
	
	return true;
}

// 验证是否 日期
function isShortDate( strValue,boxObj )
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

// 验证是否 Email
function isEmail( strValue,boxObj )
{
	// Email 必须是 x@a.b.c.d 等格式 或者为空
	if( isEmpty( strValue ) ) return true;
	
	//return checkExp( /^\w+@(\w+\.)+\w+$/gi, strValue );	//2001.12.24测试出错 检查 jxj-xxx@114online.com时不能通过
	//Modify By Tianjincat 2001.12.24
	var pattern = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
	return checkExp( pattern, strValue );
	
}

// 验证是否 数字
function isNumeric( strValue,boxObj )
{
	// 数字必须是 0123456789 或者为空
	if( isEmpty( strValue ) ) return true;
	if( !checkExp( /^[+-]?\d+(\.\d+)?$/g, strValue ))
	{
		return false;
	}
	return true;
}

// 验证是否 货币
function isMoney( strValue,boxObj )
{
	// 货币必须是 -12,345,678.9 等格式 或者为空
	if( isEmpty( strValue ) ) return true;
	
	return checkExp( /^[+-]?\d+(,\d{3})*(\.\d+)?$/g, strValue );
}

// 验证是否 电话
function isPhone( strValue,boxObj )
{
	// 普通电话	(0755)4477377-3301/(86755)6645798-665
	// Call 机	95952-351
	// 手机		130/131/135/136/137/138/13912345678
	// 或者为空
	if( isEmpty( strValue ) ) return true;
	
	return checkExp( /(^\(\d{3,5}\)\d{6,8}(-\d{2,8})?$)|(^\d+-\d+$)|(^(130|131|135|136|137|138|139)\d{8}$)/g, strValue );
}

// 验证是否 邮政编码
function isPostalCode( strValue,boxObj )
{
	if( isEmpty( strValue ) ) return true;
	// 邮政编码必须是6位数字
	if(!checkExp( /(^$)|(^\d{6}$)/gi, strValue ))
	{
		return false;
	}
	return true;
}

// 验证是否 URL
function isURL( strValue,boxObj )
{
	// http://www.yysoft.com/ssj/default.asp?Type=1&ArticleID=789
	if( isEmpty( strValue ) ) return true;
	
	var pattern = /^(http|https|ftp):\/\/(\w+\.)+[a-z]{2,3}(\/\w+)*(\/\w+\.\w+)*(\?\w+=\w*(&\w+=\w*)*)*/gi;
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

function isNotEmpty( strValue,boxObj )
{
	strValue=trim(strValue);
	if( !strValue||strValue == '' )
		return false;
	else
		return true;
}

function isEmpty( strValue,boxObj )
{
	strValue=trim(strValue);
	if( strValue == "" )
		return true;
	else
		return false;
}
