package com.zbsp.wepaysp.manage.web.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONArray;
import com.opensymphony.xwork2.ActionSupport;
import com.zbsp.wepaysp.api.service.dic.zone.AlipayEduRegionService;

@Controller  
public class ProvinceAction extends ActionSupport{  
    @Autowired  
    private AlipayEduRegionService alipayEduRegionService;  
 
   
    private int province_id;  
    private int city_id;  
  
    public int getCity_id() {  
        return city_id;  
    }  
    public void setCity_id(int city_id) {  
        this.city_id = city_id;  
    }  
    public int getProvince_id() {  
        return province_id;  
    }  
    public void setProvince_id(int province_id) {  
        this.province_id = province_id;  
    }  
    public String ajaxProvince(){  
        HttpServletResponse response = ServletActionContext.getResponse();    
        response.setContentType("text/html;charset=UTF-8");       
        response.setCharacterEncoding("UTF-8");// 防止弹出的信息出现乱码      
        PrintWriter writer=null;  
        try {  
            writer = response.getWriter();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
//        provinceList=alipayEduRegionService.findAllProvince();  
//        JSONArray jsonArray = JSONArray.fromObject(provinceList);   
//        writer.print(jsonArray.toString());  
        return null;  
    }  
    public String ajaxCity(){  
        HttpServletResponse response = ServletActionContext.getResponse();    
        response.setContentType("text/html;charset=UTF-8");       
        response.setCharacterEncoding("UTF-8");// 防止弹出的信息出现乱码      
        PrintWriter writer=null;  
        try {  
            writer = response.getWriter();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
//        provinceList=alipayEduRegionService.findAllProvince();  
//        cityList=alipayEduRegionService.findAllCity(province_id);  
//        JSONArray jsonArray = JSONArray.fromObject(cityList);   
//        writer.print(jsonArray.toString());  
        return null;  
    }  
    public String ajaxTown(){  
        HttpServletResponse response = ServletActionContext.getResponse();    
        response.setContentType("text/html;charset=UTF-8");       
        response.setCharacterEncoding("UTF-8");// 防止弹出的信息出现乱码      
        PrintWriter writer=null;  
        try {  
            writer = response.getWriter();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
//        townList=alipayEduRegionService.findAllTown(city_id);  
//        JSONArray jsonArray = JSONArray.fromObject(townList);   
//        writer.print(jsonArray.toString());  
  
        return null;  
    }  
  
}  