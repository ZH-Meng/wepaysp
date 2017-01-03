package com.zbsp.wepaysp.mobile.controller.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zbsp.wepaysp.api.service.manage.SysUserService;
import com.zbsp.wepaysp.mobile.model.base.MobileRequest;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginRequest;
import com.zbsp.wepaysp.mobile.model.userlogin.v1_0.UserLoginResponse;

@RestController
@RequestMapping("/user")
public class TestUserRestController {

    @Autowired
    private SysUserService sysUserService;
    
    @RequestMapping("login1")
    public String testLogin1(@RequestBody String requestBody) {
        return requestBody;
    } 

    @RequestMapping(value = "login2", consumes = { "text/plain", "application/json;charset=UTF-8" }, produces = "application/json; charset=UTF-8")
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
    @RequestMapping(value = "login4", consumes = { "text/plain", "application/json" })
    @ResponseBody
    public Map testLogin4(@RequestBody Map map) {
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("success", true);
        map2.put("data", "your data");
        return map;
    }

    @RequestMapping(value = "login5", method = RequestMethod.POST)
    @ResponseBody
    public UserLoginResponse testLogin5(@RequestBody MobileRequest request) {
        UserLoginRequest req = (UserLoginRequest) request;
        System.out.println(req.toString());
        UserLoginResponse u = new UserLoginResponse();
        u.setDealerCompany("火烧铺");
        
        sysUserService.doJoinTransQuerySysUserList(new HashMap<String, Object>(), 0, -1);
        return u;
    }
    
}
