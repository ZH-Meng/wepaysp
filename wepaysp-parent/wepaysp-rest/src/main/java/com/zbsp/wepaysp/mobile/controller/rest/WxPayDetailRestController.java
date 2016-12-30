package com.zbsp.wepaysp.mobile.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/app")
public class WxPayDetailRestController {
    
    @RequestMapping("/queryPayDetail")
    public String test(String payDetailOid) {
        return payDetailOid;
    }
}
