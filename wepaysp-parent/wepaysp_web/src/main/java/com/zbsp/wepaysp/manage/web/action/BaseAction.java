package com.zbsp.wepaysp.manage.web.action;


import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.RequestAware;

import com.opensymphony.xwork2.ActionSupport;
import com.zbsp.wepaysp.common.bean.MessageBean;

/**
 * 所有的Action必须强制继承 BaseAction
 * 
 * @author Magic
 *
 */
public abstract class BaseAction extends ActionSupport implements RequestAware {

    private static final long serialVersionUID = -3339032031318437880L;

    // 日志对象
    protected Logger logger = LogManager.getLogger(getClass());
    
    @SuppressWarnings({ "rawtypes" })
    private Map requestMap;
    
    /********************页面导航的返回值***************************/
    //成功
    public final static String SUCCESS = "success";
    
    //失败
    public final static String FAIL = "fail";
    
    //查询
    public final static String QUERY = "query";
    
    //列表
    public final static String LIST = "list";
    
    //新增    
    public final static String ADD = "add";
    
    //修改
    public final static String EDIT = "edit";
    
    //查看
    public final static String VIEW = "view";

    //删除
    public final static String DELETE = "delete";
    
    //打印
    public final static String PRINT = "print";
    
    /**
     * 初始化方法
     */
    public void init() {
        
    }
    
    @SuppressWarnings("rawtypes")
    public void setRequest(Map map){
        requestMap = map;
    }
    
    @SuppressWarnings("rawtypes")
    public Map getRequest(){
        return requestMap;
    }
    
    @SuppressWarnings("unchecked")
    private MessageBean getMessageBean(){
        MessageBean messageBean = (MessageBean) requestMap.get("messageBean");
        
        if(messageBean == null){
            messageBean = new MessageBean();
            requestMap.put("messageBean", messageBean);
        }
        
        return messageBean;
    }
    
    public void setAlertMessage(String message){
        getMessageBean().setAlertMessage(message);
    }
    
    public void setMessage(String message){
        getMessageBean().setMessage(message);
    }
}

