package com.zbsp.wepaysp.manage.web.action.edu;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.zbsp.wepaysp.manage.web.action.PageAction;

public class AlipayEduBillAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = -8734218055007641937L;
    private Map<String, Object> session;
    
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    @Override
    protected String query(int start, int size) {
        return null;
    }
    
    public String billList() {
        return "eduBillList";
    }
    
    public void sendBill() {
        
    }
    
    public String billDetail() {
        return "eduBillDetail";
    }
    
    public String exportBill() {
        return "eduBillDetail";
    }
    

}
