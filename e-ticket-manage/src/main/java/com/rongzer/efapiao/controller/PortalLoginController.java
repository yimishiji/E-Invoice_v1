package com.rongzer.efapiao.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.StringUtil;
import com.rongzer.rdp.web.controller.system.AuthBaseController;
import com.rongzer.rdp.web.controller.system.HttpControllConstrant.Login;
import com.rongzer.rdp.web.dao.system.LoginMapper;
import com.rongzer.rdp.web.domain.system.LoginUser;
import com.rongzer.rdp.web.service.system.LoginService;
import com.rongzer.rdp.web.service.system.MenuInfoService;

@Controller
public class PortalLoginController  extends AuthBaseController{
	@Autowired
	private LoginService loginService;
	@Autowired
	private MenuInfoService menuInfoService;
	@Autowired
	private LoginMapper loginMapper;

	
	public void setRdpLang(HttpServletRequest request,HttpSession session) {
		//取语言集
		String strLang = (String)request.getParameter("lang");
		if (StringUtil.isNotEmpty(strLang))
		{
			session.setAttribute("__rdp_lang",strLang);
		}else
		{
			strLang = "";
			session.removeAttribute("__rdp_lang");
		}
		
		//更新用户的语言
		if (session.getAttribute("LoginUser") != null)
		{
			LoginUser user = (LoginUser)session.getAttribute("LoginUser");
			if (StringUtil.isNotEmpty(user.getLoginUserId()))
			{
				String updateLanguage = "UPDATE R_SYS_USER_INFO SET USER_LANGUAGE='"+strLang+"' WHERE USER_ID='"+user.getLoginUserId()+"'";
				RDPUtil.updateData(updateLanguage);
			}
		}
	}
	
	@RequestMapping(value = "portal")
	public String portalLogin(HttpServletRequest request) {
		String loginFalseUrl = "portal/false";
		String ivUser = request.getHeader("iv-user");
		if(StringUtil.isEmpty(ivUser)){
			return loginFalseUrl;
		}else{

			//本地登陆
			Map<String, String> userMap = loginMapper.login(ivUser);
			//判断用户是否存在
			if (null == userMap || userMap.isEmpty()) {
				return loginFalseUrl;
			}else{
				LoginUser user = new LoginUser();
				user.setLoginUserId(userMap.get("USERID"));
				user.setLoginUserName(userMap.get("NAMECN"));
				user.setLoginUserGroupId(userMap.get("GROUPID"));
				user.setUserLanguage(userMap.get("USER_LANGUAGE"));
				user.setUserInfo(userMap);
				if (userMap.get("NAMECN").equals("admin"))
				{
					user.setSuperUser(true);
				}

				HttpSession session = request.getSession();
				//登录用户信息
				session.setAttribute("LoginUser", user);
				//菜单头部信息
				session.setAttribute("TopMenu", menuInfoService.getTopMenuInfo(user.getLoginUserId()));
				//wabacus 功能权限
				session.setAttribute("NonResource",loginService.getNonResourcebyUserId(user.getLoginUserId()));
				
				//用户的功能数据权限
				session.setAttribute("UserDataPermission", loginService.getUserDataPermission(user.getLoginUserId()));
				
				//用户的审核权限集合
				session.setAttribute("AUDITGROUPS", loginService.getUserAuditGroups(user.getLoginUserGroupId()));
				
				//处理父、子组织
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("GROUP_ID", user.getLoginUserGroupId());
				Map<String,Object> mapGroupInfo = RDPUtil.execBaseBizService("GroupBizService","getParentChild",params);
				String parentIds = "";
				String childIds = "";

				if (mapGroupInfo != null)
				{
					List<String> lisParentIds = (List<String>)mapGroupInfo.get("PARENT_GROUP_IDS");
					List<String> lisChildIds = (List<String>)mapGroupInfo.get("CHILD_GROUP_IDS");
					
					if (lisParentIds != null)
					{
						for (String groupId : lisParentIds)
						{
							parentIds +="\""+groupId+"\",";
						}
						if (parentIds.endsWith(","))
						{
							parentIds = parentIds.substring(0,parentIds.length()-1);
						}
						
					}
					
					if (lisChildIds != null)
					{
						for (String groupId : lisChildIds)
						{
							childIds +="\""+groupId+"\",";
						}
						if (childIds.endsWith(","))
						{
							childIds = childIds.substring(0,childIds.length()-1);
						}
					}

				}
				
				if (parentIds.length()<2)
				{
					parentIds = "\"\"";
				}
				if (childIds.length()<2)
				{
					childIds = "\"\"";
				}
				
				session.setAttribute("PARENT_GROUPIDS", parentIds);
				session.setAttribute("CHILD_GROUPIDS", childIds);

				
				String strLang = (String)request.getParameter("lang");
				if (StringUtil.isNotEmpty(strLang))
				{
					//取语言集
					setRdpLang(request,session);
				}else
				{
					//使用用户当前己选择的语言
					String userLanuguage = user.getUserLanguage();
					if (StringUtil.isNotEmpty(userLanuguage))
					{
						session.setAttribute("__rdp_lang",userLanuguage);
					}
				}
			}
			
		}
		return Login.FORWARD_MAIN_PAGE;
	}
}


