package com.rongzer.efapiao.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.dao.TaskServiceMapper;
import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.RDPBaseService;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.HttpUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import com.rongzer.rdp.common.util.StringUtil;
import com.rongzer.rdp.memcached.CacheClient;
import com.rongzer.rdp.memcached.MemcachedException;

/**
 * 商品分类和商品信息同步
 * @author qrl
 *
 */
@Service("goodsGroupSynchService")
public class GoodsGroupSynchService implements RDPBaseService{
	@Autowired
	private TaskServiceMapper taskServiceMapper;
	@Override
	public String execute(String arg0) {
		StringBuffer result = new StringBuffer();
		{
			//获取商品分类
			Map<String, Object> groupInfoOld = getGroupInfo();
			//获取接口门店
			List<Map<String,Object>> groupList = getGroupList();
			//处理同步信息
			Map<String,Object> storeResponse = addGroupInfo(groupInfoOld,groupList);
			String resultCode = StringUtil.toStringWithEmpty(storeResponse.get("resultCode"));
			if("success".equals(resultCode)){
				result.append("group infomation access succeed."+storeResponse.get("resultDesc"));
			}else{
				result.append("group infomation access failed.").append(storeResponse.get("resultDesc"));
			}
			updateGroupInfo();
		}
		{
			//获取商品明细
			Map<String, Object> goodsInfoOld = getGoodsInfo();
			//获取接口商品明细
			List<Map<String,Object>> goodsList = getGoodsList();
			//处理同步信息
			Map<String,Object> storeResponse = addGoodsInfo(goodsInfoOld,goodsList);
			String resultCode = StringUtil.toStringWithEmpty(storeResponse.get("resultCode"));
			if("success".equals(resultCode)){
				result.append("goods infomation access succeed."+storeResponse.get("resultDesc"));
			}else{
				result.append("goods infomation access failed.").append(storeResponse.get("resultDesc"));
			}
			updateGoodsInfo();
		}
		return result.toString();
	}

	/**
	 *
	 * @param goodsInfoOld
	 * @param goodsList
	 * @return
	 */
	private Map<String, Object> addGoodsInfo(Map<String, Object> goodsInfoOld,
											 List<Map<String, Object>> goodsList) {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		String resultCode = "success";
		String resultDesc = "";
		try{
			if(CollectionUtil.isNotEmpty(goodsList)){
				List<Map<String,Object>> goodsAddList = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> goodsUpdateList = new ArrayList<Map<String,Object>>();
				//循环遍历门店list，有放入更新列表，无放入新增列表
				String goodsCode = "";
				String groupCode = "";
				Map<String, Object> mapTemp = new HashMap<String,Object>();
				for (int i = 0; i < goodsList.size(); i++) {
					mapTemp = goodsList.get(i);
					goodsCode = StringUtil.toStringWithEmpty(mapTemp.get("code"));//商品编码
					groupCode = StringUtil.toStringWithEmpty(mapTemp.get("parentCode"));//分类编码
					mapTemp.put("NOWTIME", StringUtil.getNowTime());
					mapTemp.put("USER", "SYSADMIN");
					if(goodsInfoOld.containsKey(goodsCode)){
						if(!groupCode.equals(goodsInfoOld.get(goodsCode))){//分类发生调整
							goodsUpdateList.add(mapTemp);
						}
					}else{
						mapTemp.put("GOODS_ID", StringUtil.getUuid32());
						mapTemp.put("IS_USED", "D00002");
						goodsAddList.add(mapTemp);
						goodsUpdateList.add(mapTemp);
					}
				}
				if(CollectionUtil.isNotEmpty(goodsAddList)){
					batchCommit("com.rongzer.efapiao.dao.TaskServiceMapper.insertGoodsInfo",5,goodsAddList);
				}
				if(CollectionUtil.isNotEmpty(goodsUpdateList)){
					batchCommit("com.rongzer.efapiao.dao.TaskServiceMapper.updateGoodsInfo",5,goodsUpdateList);
				}
				resultDesc = "add goods :" + goodsAddList.size() + ";update goods :"+ (goodsUpdateList.size()-goodsAddList.size());
			}else{
				resultCode = "fail";
				resultDesc = "no goods data!";
			}
		}catch(Exception e){
			resultCode = "fail";
			e.printStackTrace();
			resultDesc = "System error.Please contact IT supporter!";
		}
		responseMap.put("resultCode", resultCode);
		responseMap.put("resultDesc", resultDesc);
		return responseMap;
	}

	/**
	 * 接口中的商品明细
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getGoodsList() {
		List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
		try {
			//TODO
			String goodsurl = RDPUtil.getSysConfig("efapiao.shinho.goodsInfo");
			String goodsstr = HttpUtil.readContentFromGet(goodsurl);
			Map<String, String> returnmap = JSONUtil.json2Map(goodsstr);
			if(CollectionUtil.isNotEmpty(returnmap)&&"true".equals(returnmap.get("success"))){
				goodsList = JSONUtil.getJSONArrayFromStr(returnmap.get("data"));
				String regexp = "\'";
				for (Map<String, Object> map :goodsList) {
//					String parentNameEN=StringUtil.safeToString(map.get("parentNameEN"));
//					String parentNameCN=StringUtil.safeToString(map.get("parentNameCN"));
//					map.put("parentName_EN", parentNameEN.replaceAll(regexp, "\''"));
//					map.put("parentName_CN", parentNameCN.replaceAll(regexp, "\''"));
					String nameEN=StringUtil.safeToString(map.get("nameEN"));
					String nameCN=StringUtil.safeToString(map.get("nameCN"));
					map.put("name_EN", nameEN.replaceAll(regexp, "\''"));
					map.put("nameCN", nameCN.replaceAll(regexp, "\''"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goodsList;
	}

	/**
	 * 获取系统中的商品信息
	 * @return GOODS_CODE/GROUP_CODE
	 */
	private Map<String, Object> getGoodsInfo() {
		Map<String, Object> goodInfoMap = new HashMap<String,Object>();
		try {
			// 从数据库重新获取
			List<Map<String, String>> goodsList = taskServiceMapper.getGoodsInfo();
			for (Map<String, String> map : goodsList) {
				goodInfoMap.put(map.get("GOODS_CODE"), map.get("GROUP_CODE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goodInfoMap;
	}

	private Map<String, Object> addGroupInfo(Map<String, Object> groupInfoOld,
											 List<Map<String, Object>> groupList) {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		String resultCode = "success";
		String resultDesc = "";
		try{
			if(CollectionUtil.isNotEmpty(groupList)){
				List<Map<String,Object>> groupAddList = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> groupUpdateList = new ArrayList<Map<String,Object>>();
				//循环遍历商品分类list，有放入更新列表，无放入新增列表
				String groupKey = "";
				String parentCode = "";
				Map<String, Object> mapTemp = new HashMap<String,Object>();
				for (int i = 0; i < groupList.size(); i++) {
					mapTemp = groupList.get(i);
					groupKey = StringUtil.toStringWithEmpty(mapTemp.get("code"));
					parentCode = StringUtil.toStringWithEmpty(mapTemp.get("parentCode"));
					mapTemp.put("NOWTIME", StringUtil.getNowTime());
					mapTemp.put("USER", "SYSADMIN");
					if(groupInfoOld.containsKey(groupKey)){
						if(!parentCode.equals(groupInfoOld.get(groupKey))){
							mapTemp.put("parentCode",parentCode);//parent group code
							groupUpdateList.add(mapTemp);
						}
					}else{
						mapTemp.put("GROUP_ID", StringUtil.getUuid32());
						mapTemp.put("IS_USED", "D00002");
						mapTemp.put("parentCode",parentCode);//parent group code
						groupAddList.add(mapTemp);
						groupUpdateList.add(mapTemp);
					}
				}
				if(CollectionUtil.isNotEmpty(groupAddList)){
					batchCommit("com.rongzer.efapiao.dao.TaskServiceMapper.insertGroupInfo",5,groupAddList);
				}
				if(CollectionUtil.isNotEmpty(groupUpdateList)){
					batchCommit("com.rongzer.efapiao.dao.TaskServiceMapper.updateGroupInfo",5,groupUpdateList);
				}
				resultDesc = "add group :" + groupAddList.size() + ";update group :"+ (groupUpdateList.size()-groupAddList.size());
			}else{
				resultCode = "fail";
				resultDesc = "no group data!";
			}
		}catch(Exception e){
			resultCode = "fail";
			e.printStackTrace();
			resultDesc = "System error.Please contact IT supporter!";
		}
		responseMap.put("resultCode", resultCode);
		responseMap.put("resultDesc", resultDesc);
		return responseMap;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getGroupList() {
		List<Map<String,Object>> groupList = new ArrayList<Map<String,Object>>();
		try {
			String groupturl = RDPUtil.getSysConfig("efapiao.shinho.categoryInfo");
			String groupstr = HttpUtil.readContentFromGet(groupturl);
			Map<String, String> returnmap = JSONUtil.json2Map(groupstr);
			if(CollectionUtil.isNotEmpty(returnmap)&&"true".equals(returnmap.get("success"))){
				groupList = JSONUtil.getJSONArrayFromStr(returnmap.get("data"));
				for (Map<String, Object> map :groupList) {
					String regexp = "\'";
					String nameEN=StringUtil.safeToString(map.get("nameEN"));
					String name_EN;
					name_EN = nameEN.replaceAll(regexp, "\''");
					map.put("name_EN", name_EN);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupList;
	}

	private Map<String, Object> getGroupInfo() {
		Map<String, Object> groupInfoMap = new HashMap<String,Object>();
		try {
			// 从数据库重新获取
			List<Map<String, String>> groupList = taskServiceMapper.getGroupInfo();
			for (Map<String, String> map : groupList) {
				groupInfoMap.put(map.get("GROUP_CODE"), map.get("PARENT_GROUP_CODE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoMap;
	}

	/**
	 * 获取商品信息
	 * @param paramMap
	 * @return
	 */
	public void updateGoodsInfo() {
		//获取货品信息
		try{
			CacheClient cacheClient = (CacheClient) RDPContext.getContext()
					.getBean("cacheClient");
			Map<String, Object> goodsMap = new HashMap<String, Object>();
			List<Map<String, String>> paymentList = taskServiceMapper
					.getGoodsInfo();
			goodsMap = new HashMap<String,Object>();
			for (Map<String, String> payment : paymentList) {
				String goods_id = (String) payment.get("GOODS_CODE");
				if (!goodsMap.containsKey(goods_id)) {
					goodsMap.put(goods_id, payment);
				} else {
					// paymentCode 异常需要处理 TODO
				}
			}
			if (CollectionUtil.isNotEmpty(goodsMap)) {
				cacheClient.save(EfapiaoConstant.CacheKey.GOODS_INFO, goodsMap);
			}
		}catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 获取商品分类
	 * @param paramMap
	 * @return
	 */
	public void updateGroupInfo() {
		//获取分组和开票内容之间的关系
		try{
			CacheClient cacheClient = (CacheClient) RDPContext.getContext()
					.getBean("cacheClient");
			Map<String, Object> groupMap = new HashMap<String, Object>();
			List<Map<String, String>> groupList = taskServiceMapper
					.getGroupInfo();
			groupMap = new HashMap<String, Object>();
			groupMap = new HashMap<String, Object>();
			for (Map<String,String> groupInfo : groupList) {
				String groupId = (String) groupInfo.get("GROUP_ID");
				if (!groupMap.containsKey(groupId)) {
					groupMap.put(groupId, groupInfo);
				} else {
					//storeInfoCode 异常需要处理 TODO
				}
			}
			if (CollectionUtil.isNotEmpty(groupMap)) {
				cacheClient.save(EfapiaoConstant.CacheKey.GROUP_INFO, groupMap);
			}
		}catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * xuk 批量提交数据
	 *
	 * @param mybatisSQLId
	 *            SQL语句在Mapper XML文件中的ID
	 * @param commitCountEveryTime
	 *            每次提交的记录数
	 * @param list
	 *            要提交的数据列表
	 * @param logger
	 *            日志记录器
	 * @throws Exception
	 */
	private <T> void batchCommit(String mybatisSQLId,
								 int commitCountEveryTime, List<Map<String, Object>> list)  {
		SqlSession session = null;
		try {
			DefaultSqlSessionFactory sqlSessionFactory = (DefaultSqlSessionFactory) RDPContext.getContext().getBean("sqlSessionFactory");
			session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
			int commitCount = (int) Math.ceil(list.size() / (double) commitCountEveryTime);
			List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>(commitCountEveryTime);
			int start, stop;
			//Long startTime = System.currentTimeMillis();
			for (int i = 0; i < commitCount; i++) {
				tempList.clear();
				start = i * commitCountEveryTime;
				stop = Math.min(i * commitCountEveryTime + commitCountEveryTime
						- 1, list.size() - 1);
				for (int j = start; j <= stop; j++) {
					tempList.add(list.get(j));
				}
				session.insert(mybatisSQLId, tempList);
				session.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}

		}
	}
}
