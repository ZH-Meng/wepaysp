package com.zbsp.wepaysp.manage.web.action.partner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.service.partner.PartnerService;
import com.zbsp.wepaysp.vo.partner.PartnerVO;

public class PartnerAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = -7078956274536886116L;

    private Map<String, Object> session;
    private PartnerVO partnerVO;
    private List<PartnerVO> partnerVoList;
    private PartnerService partnerService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        try {
            paramMap.put("buildType", SysUser.BuildType.create.getValue());

            partnerVoList = partnerService.doJoinTransQueryPartnerList(paramMap, start, size);
            rowCount = partnerService.doJoinTransQueryPartnerCount(paramMap);

        } catch (Exception e) {
            logger.error("代理商查询列表错误：" + e.getMessage());
            setAlertMessage("代理商查询列表错误：" + e.getMessage());
        }

        return "partnerList";
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    public String goToAddPartner() {
        return SUCCESS;
    }

    public String addPartner() {
        return SUCCESS;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public PartnerVO getPartnerVO() {
        return partnerVO;
    }

    public void setPartnerVO(PartnerVO partnerVO) {
        this.partnerVO = partnerVO;
    }

    public List<PartnerVO> getPartnerVoList() {
        return partnerVoList;
    }

    public void setPartnerVoList(List<PartnerVO> partnerVoList) {
        this.partnerVoList = partnerVoList;
    }

    public void setPartnerService(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

}
