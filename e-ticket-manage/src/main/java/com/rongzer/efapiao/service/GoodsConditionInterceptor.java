package com.rongzer.efapiao.service;

import com.rongzer.rdp.common.util.StringUtil;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;

/**
 * 第一点击分组进入页面的时候，获取group_id的值，并用临时条件GROUP_ID_TEMP存储
 * 当界面上条件没有填写时，添加group_id条件进行查询。如果界面上条件填写了则去除group_id条件查询
 * @author
 * @create 2017-07-11 17:43
 **/
public class GoodsConditionInterceptor extends AbsInterceptorDefaultAdapter {
    @Override
    public void doStart(ReportRequest rrequest, ReportBean rbean) {
        String groupId = rrequest.getStringAttribute("GROUP_ID","");
        String groupIdTemp = rrequest.getStringAttribute("GROUP_ID_TEMP","");
        String goodsCode = rrequest.getStringAttribute("GOODS_CODE","");
        String goodsNameCn = rrequest.getStringAttribute("GOODS_NAME_CN","");
        String contentId = rrequest.getStringAttribute("CONTENT_ID","");

        String groupCode = rrequest.getStringAttribute("GROUP_CODE","");
        String groupNameCn = rrequest.getStringAttribute("GROUP_NAME_CN","");
        if("goodsItems".equals(rbean.getPageBean().getId())) {
            if (!"".equals(goodsCode) || !"".equals(goodsNameCn) || !"".equals(contentId)) {
                rrequest.removeAttribute("GROUP_ID");
            } else {
                if(StringUtil.isNotEmpty(groupIdTemp)){
                    rrequest.setAttribute("GROUP_ID", groupIdTemp);
                    rrequest.getRequest().setAttribute("GROUP_ID", groupIdTemp);
                }else {
                    rrequest.setAttribute("GROUP_ID_TEMP", groupId);
                    rrequest.getRequest().setAttribute("GROUP_ID_TEMP", groupId);
                }
            }
        }else if("goodsGroupList".equals(rbean.getPageBean().getId())){
            if (!"".equals(groupCode) || !"".equals(groupNameCn) || !"".equals(contentId)) {
                rrequest.removeAttribute("GROUP_ID");
            } else {
                if(StringUtil.isNotEmpty(groupIdTemp)){
                    rrequest.setAttribute("GROUP_ID", groupIdTemp);
                    rrequest.getRequest().setAttribute("GROUP_ID", groupIdTemp);
                }else {
                    rrequest.setAttribute("GROUP_ID_TEMP", groupId);
                    rrequest.getRequest().setAttribute("GROUP_ID_TEMP", groupId);
                }
            }
        }
    }
}
