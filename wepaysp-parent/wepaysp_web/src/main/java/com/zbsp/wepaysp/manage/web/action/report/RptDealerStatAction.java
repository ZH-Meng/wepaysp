package com.zbsp.wepaysp.manage.web.action.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.api.service.report.RptDealerStatService;
import com.zbsp.wepaysp.vo.partner.StoreVO;
import com.zbsp.wepaysp.vo.report.RptDealerStatVO;

/**
 * 分润结算/资金结算
 * 
 * @author 孟郑宏
 */
public class RptDealerStatAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = 8509361391781384238L;
    private Map<String, Object> session;
    private RptDealerStatVO rptDealerStatVO;
    private List<RptDealerStatVO> rptDealerStatVoList;
    private RptDealerStatService rptDealerStatService;
    private List<StoreVO> storeVoList;
    private StoreService storeService;
    private int userLevel;
    private int partnerLevel;
    private String listType;// 对应不同菜单
    private String queryType; // day 按日期，month 按月份，其他值直接返回列表
    private String beginTime;
    private String endTime;
    private String monthTime;
    private String logPrefix;

    @Override
    protected String query(int start, int size) {
        String result = "rptDealerStatList";
        if ("partner".equals(listType)) {
            logPrefix = "查询代理商分润统计";
            result = "rptDealerStatList4Partner";
        } else if ("partnerEmployee".equals(listType)) {
            logPrefix = "查询代理商员工分润统计";
            result = "rptDealerStatList4Partner";
        } else if ("dealer".equals(listType)) {
            logPrefix = "查询商户门店资金结算";
        } else if ("dealerEmployee".equals(listType)) {
            logPrefix = "查询商户员工资金结算";
        } else {
            return "accessDenied";
        }
        logger.info("开始" + logPrefix);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (rptDealerStatVO == null) {
            rptDealerStatVO = new RptDealerStatVO();
        }
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // 校验权限

            // 用户级别，页面根据级别动态展示查询条件以及结果列表
            userLevel = manageUser.getUserLevel();
            // 初始化查询参数默认值
            initDefaultDatesArgs();

            if ("day".equals(queryType)) {
                paramMap.put("queryType", "day");
                paramMap.put("beginTime", TimeUtil.getDayStart(convertS2D(beginTime)));
                paramMap.put("endTime", TimeUtil.getDayEnd(convertS2D(endTime)));
            } else if ("month".equals(queryType)) {
                paramMap.put("queryType", "month");
                paramMap.put("beginTime", TimeUtil.getMonthStart(DateUtil.getDate(monthTime, "yyyy-MM")));
                paramMap.put("endTime", TimeUtil.getMonthEnd(DateUtil.getDate(monthTime, "yyyy-MM")));
            } else {
                logger.warn("未指定查询类型");
                setAlertMessage("必须指定按日期或按月查询");
                return "rptDealerStatList";
            }

            boolean flag = false;

            /* 根据用户的级别设置不同的查询条件 */
            if (manageUser.getUserLevel() == SysUser.UserLevel.partner.getValue()) {// 服务商
                if (manageUser.getDataPartner() != null) {
                    partnerLevel = manageUser.getDataPartner().getLevel();
                    if (partnerLevel == 1 || partnerLevel == 2 || partnerLevel == 3) {

                        if ("partner".equals(listType)) {// 代理商访问代理商分润统计
                            flag = true;
                            paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                            paramMap.put("partnerLevel", partnerLevel);
                            paramMap.put("partnerId", rptDealerStatVO.getPartnerId());
                            paramMap.put("currentPartnerId", manageUser.getDataPartner().getPartnerId());

                            //rowCount = rptDealerStatService.doJoinTransQueryRptDealerStatCount4Parnter(paramMap);
                            rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4Parnter(paramMap, 0, -1);
                        } else if ("partnerEmployee".equals(listType)) {// 代理商访问代理商员工分润统计
                            flag = true;
                            paramMap.put("partnerOid", manageUser.getDataPartner().getIwoid());
                            paramMap.put("partnerEmployeeId", rptDealerStatVO.getPartnerEmployeeId());

                            //rowCount = rptDealerStatService.doJoinTransQueryRptDealerStatCount4ParnterE(paramMap);
                            rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4ParnterE(paramMap, 0, -1);
                        }
                    }
                }
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.salesman.getValue()) {// 业务员
                if (manageUser.getDataPartnerEmployee() != null) {
                    if ("partnerEmployee".equals(listType)) {// 代理商员工访问代理商员工分润统计
                        flag = true;
                        paramMap.put("partnerEmployeeOid", manageUser.getDataPartnerEmployee().getIwoid());

                        //rowCount = rptDealerStatService.doJoinTransQueryRptDealerStatCount4ParnterE(paramMap);
                        rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4ParnterE(paramMap, 0, -1);
                    }
                }
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.dealer.getValue()) {// 商户
                if (manageUser.getDataDealer() != null) {
                    paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
                    // 查询管辖门店集合
                    storeVoList = storeService.doJoinTransQueryStoreList(paramMap, 0, -1);

                    paramMap.put("storeOid", rptDealerStatVO.getStoreOid());
                    if ("dealer".equals(listType)) {// 商户访问商户门店资金结算
                        flag = true;
                        //rowCount = rptDealerStatService.doJoinTransQueryRptDealerStatCount4Dealer(paramMap);
                        rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4Dealer(paramMap, 0, -1);
                    } else if ("dealerEmployee".equals(listType)) {// 商户访问商户员工资金结算
                        flag = true;
                        paramMap.put("dealerEmployeeId", rptDealerStatVO.getDealerEmployeeId());
                        //rowCount = rptDealerStatService.doJoinTransQueryRptDealerStatCount4DealerE(paramMap);
                        rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4DealerE(paramMap, 0, -1);
                    }
                }
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.cashier.getValue()) {// 收银员
                if (manageUser.getDataDealerEmployee() != null) {
                    if ("dealerEmployee".equals(listType)) {// 访问商户员工资金结算
                        flag = true;
                        paramMap.put("dealerEmployeeOid", manageUser.getDataDealerEmployee().getIwoid());

                        //rowCount = rptDealerStatService.doJoinTransQueryRptDealerStatCount4DealerE(paramMap);
                        rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4DealerE(paramMap, 0, -1);
                    }
                }
            } else if (manageUser.getUserLevel() == SysUser.UserLevel.shopManager.getValue()) {// 分店店长
            	if (manageUser.getDataDealerEmployee() != null && manageUser.getDataDealerEmployee().getStore() != null) {
            		paramMap.put("storeOid", manageUser.getDataDealerEmployee().getStore().getIwoid());
            		if ("dealer".equals(listType)) {// 商户访问商户门店资金结算
                        flag = true;
                        //rowCount = rptDealerStatService.doJoinTransQueryRptDealerStatCount4Dealer(paramMap);
                        rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4Dealer(paramMap, 0, -1);
                    } else if ("dealerEmployee".equals(listType)) {// 商户访问商户员工资金结算
                        flag = true;
                        paramMap.put("dealerEmployeeId", rptDealerStatVO.getDealerEmployeeId());
                        //rowCount = rptDealerStatService.doJoinTransQueryRptDealerStatCount4DealerE(paramMap);
                        rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4DealerE(paramMap, 0, -1);
                    }
                }
            }

            if (!flag) {
                logger.warn("当前用户无权" + logPrefix + "！");
                setAlertMessage("当前用户无权" + logPrefix + "！");
                return "accessDenied";
            }
        } catch (NotExistsException e) {
        	logger.warn(logPrefix + "错误：" + e.getMessage());
        	setAlertMessage(e.getMessage());
        } catch (Exception e) {
        	logger.error(logPrefix + "错误：" + e.getMessage());
        	setAlertMessage(logPrefix + "错误！");
        }

        logger.info(logPrefix + "结束");

        return result;
    }

    private void initDefaultDatesArgs() {
        if (StringUtils.isBlank(queryType)) {
            queryType = "day";
            beginTime = convertD2S(TimeUtil.getBeforeDayStart());
            endTime = convertD2S(TimeUtil.getBeforeDayEnd());
            String lastmonth = new SimpleDateFormat("yyyy-MM").format(TimeUtil.getDayBeforeMonth(new Date()));
            monthTime = monthTime == null ? lastmonth : monthTime;
        }
    }

    public String list() {
        initPageData(PageAction.defaultLargePageSize);
        return goCurrent();
    }

    /**
     * 服务商访问- 代理商分润统计
     * 
     * @return
     */
    public String list4Partner() {
        initPageData(PageAction.defaultLargePageSize);
        listType = "partner";
        return goCurrent();
    }

    /**
     * 服务商、业务员访问-代理商员工分润统计
     * 
     * @return
     */
    public String list4PartnerE() {
        initPageData(PageAction.defaultLargePageSize);
        listType = "partnerEmployee";
        return goCurrent();
    }

    /**
     * 商户访问- 商户门店资金结算
     * 
     * @return
     */
    public String list4Dealer() {
        initPageData(PageAction.defaultLargePageSize);
        listType = "dealer";
        return goCurrent();
    }

    /**
     * 商户、收银员访问- 商户员工资金结算
     * 
     * @return
     */
    public String list4DealerE() {
        initPageData(PageAction.defaultLargePageSize);
        listType = "dealerEmployee";
        return goCurrent();
    }

    private Date convertS2D(String dateStr) {
        return DateUtil.getDate(dateStr, "yyyy-MM-dd");
    }

    private String convertD2S(Date date) {
        return DateUtil.getDate(date, "yyyy-MM-dd");
    }

    public RptDealerStatVO getRptDealerStatVO() {
        return rptDealerStatVO;
    }

    public void setRptDealerStatVO(RptDealerStatVO rptDealerStatVO) {
        this.rptDealerStatVO = rptDealerStatVO;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMonthTime() {
        return monthTime;
    }

    public void setMonthTime(String monthTime) {
        this.monthTime = monthTime;
    }

    public List<RptDealerStatVO> getRptDealerStatVoList() {
        return rptDealerStatVoList;
    }

    public List<StoreVO> getStoreVoList() {
        return storeVoList;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public int getPartnerLevel() {
        return partnerLevel;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public void setRptDealerStatService(RptDealerStatService rptDealerStatService) {
        this.rptDealerStatService = rptDealerStatService;
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

}
