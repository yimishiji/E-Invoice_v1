package com.rongzer.efapiao.common;

import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.QueryDataService;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 对下拉框通过 valuestyelproperty="DICT" 解析的扩展
 * @why 如果通过配制表selectbox不能通过dict 解析
 * @author wuyanxi
 *
 */
public class SelectBoxExt {
	
	/**
	 * 通过valuestyleproperty ="DICT"不能解析 
	 * @return
	 */
	public static String getSelectBoxValueName(String bizId,String code){
		String strSql = "SELECT EXEC_SQL from R_BASE_SELECTBOX where biz_id = '"+bizId+"'";
		Map<String, String> resSqlMap = ((QueryDataService) RDPContext.getContext().getBean("QueryDataService")).queryRowDataBySql(strSql);
		String resSql = resSqlMap.get("EXEC_SQL");
		List<Map<String, String>> resSqlList = ((QueryDataService) RDPContext.getContext().getBean("QueryDataService")).queryDataBySql(resSql);
		
		if(resSqlList.size() > 0 ){
			for (Iterator iterator = resSqlList.iterator(); iterator.hasNext();) {
				Map<String, String> map = (Map<String, String>) iterator.next();
				String key = map.get(bizId);
				if(key.equals(code)){
					return  map.get("label");
				}
			}
		}
		return null != code ? code : "";
	}
	/**
	 * 将毫秒数转换为时间格式
	 * @param longer
	 * @return
	 */
	public static String getDateFormatByLong(String longer){
		if(null != longer && !"".equals(longer)){
			SimpleDateFormat sf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
			return sf.format(Long.valueOf(longer)*1000);
		}
		return longer;
	}
	
	public static void main(String[] args) {
		System.out.println(getTimeMillis());
		long time = Calendar.getInstance().getTimeInMillis();
		System.out.println(time);
	}
	
	/**
	 * 取得当前时间的秒数
	 * @return
	 */
	private static long getTimeMillis(){
		Calendar calendar = Calendar.getInstance();
		TimeZone tz = TimeZone.getTimeZone("GMT");        
		calendar.setTimeZone(tz); 
		return calendar.getTimeInMillis();
	}
}
