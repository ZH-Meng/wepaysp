package com.zbsp.wepaysp.mobile.controller.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.common.Signature;
import com.zbsp.wepaysp.api.service.manage.SysUserService;
import com.zbsp.wepaysp.mobile.model.base.MobileRequest;
import com.zbsp.wepaysp.mobile.model.base.MobileResponse;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginRequest;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginResponse;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.partner.DealerEmployee;

@RestController
@RequestMapping("/user")
public class UserRestController {
    protected Logger logger = LogManager.getLogger(getClass());
    private final String key = "11111111111111111111111111111111";
    @Autowired
	private SysUserService sysUserService;
    
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public UserLoginResponse login(@RequestBody UserLoginRequest request) {
    	// FIXME check
    	logger.debug("request Data is " + request.toString());
    	String ip = "127.0.0.1";
    	UserLoginResponse response = null;
    	try {
			Map<String, Object> resMap = sysUserService.doTransUserLogin(request.getUserId(), request.getPasswd(), ip);
			SysUser user = (SysUser) MapUtils.getObject(resMap, "sysUser");
			String token = MapUtils.getString(resMap, "loginToken");
			DealerEmployee dealerE = user.getDealerEmployee();
			if (user.getDealerEmployee() == null) {
				return response;
			}
			response = new UserLoginResponse();
			response.setDealerEmployeeName(dealerE.getEmployeeName());
			response.setDealerEmployeeId(dealerE.getDealerEmployeeId());
			response.setSessionId(token);
			// FIXME
			response.setSignature(Signature.getSign(response, key));
			//response.setDealerCompany();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (response == null) {
				response = new UserLoginResponse();
				response.setResult(999);
				response.setMessage("登陆失败");
				try {
					response.setSignature(Signature.getSign(response, key));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
    	logger.debug("response Data is " + response.toString());
        return response;
    }
    
    @RequestMapping("login1")
    public String testLogin1(@RequestBody String requestBody) {
        return requestBody;
    }
    
    @RequestMapping(value = "login2", consumes = {"text/plain","application/json;charset=UTF-8"}, produces="application/json; charset=UTF-8")
    public String testLogin2(@RequestBody String requestBody) {
        return requestBody;
    }
    
    @RequestMapping(value = "login3", method = RequestMethod.POST)
    @ResponseBody
    public UserLoginResponse testLogin3(@RequestBody UserLoginRequest requestBody) {
    	System.out.println(requestBody.toString());
    	UserLoginResponse res = new UserLoginResponse();
    	res.setDealerCompany("火烧铺");
        return res;
    }
      
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "login4", consumes = {"text/plain","application/json"})
    @ResponseBody
    public Map testLogin4(@RequestBody Map map) {
    	Map<String, Object> map2 = new HashMap<String, Object>();
    	map2.put("success", true);
    	map2.put("data", "your data");
        return map;
    }
    
    @RequestMapping(value = "login5", method = RequestMethod.POST)
    @ResponseBody
    public MobileResponse testLogin5(@RequestBody MobileRequest request) {
    	UserLoginRequest req = (UserLoginRequest) request.getBody();
    	System.out.println(req.toString());
    	MobileResponse res = new MobileResponse();
    	UserLoginResponse u = new UserLoginResponse();
    	u.setDealerCompany("火烧铺");
    	res.setBody(u);
    	sysUserService.doJoinTransQuerySysUserList(new HashMap<String, Object>(), 0, -1);
        return res;
    }
    
}
