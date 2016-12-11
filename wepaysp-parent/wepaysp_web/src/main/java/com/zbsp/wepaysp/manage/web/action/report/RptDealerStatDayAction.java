package com.zbsp.wepaysp.manage.web.action.report;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
 * 按天查询资金结算
 * 
 * @author 孟郑宏
 */
public class RptDealerStatDayAction
    extends PageAction {
    

    private static final long serialVersionUID = 8509361391781384238L;
    private RptDealerStatVO rptDealerStatVO;
    private List<RptDealerStatVO> rptDealerStatVoList;
    private RptDealerStatService rptDealerStatService;
    private List<StoreVO> storeVoList;
    private StoreService storeService;
    private int userLevel;
    private String listType;// 对应不同菜单
    private String queryDate;

    @Override
    protected String query(int start, int size) {
        String logPrefix = "按天查看结算";
        logger.info("开始" + logPrefix);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (rptDealerStatVO == null) {
            rptDealerStatVO = new RptDealerStatVO();
        }
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // 用户级别，页面根据级别动态展示查询条件以及结果列表
            userLevel = manageUser.getUserLevel();
            Date queryDay = null;
            boolean todayFlag = false;
            if (StringUtils.isBlank(queryDate)) {
                queryDay = new Date();
                queryDate = convertD2S(queryDay);
                todayFlag = true;
            } else {
                queryDay = convertS2D(queryDate);
                if (TimeUtil.timeEqual(queryDay, TimeUtil.getDayStart(new Date()))) {
                    todayFlag = true;
                }
            }
            paramMap.put("beginTime", TimeUtil.getDayStart(queryDay));
            paramMap.put("endTime", TimeUtil.getDayEnd(queryDay));
            paramMap.put("queryType", "day");
            boolean flag = false;

            /* 根据用户的级别设置不同的查询条件 */
            if (isDealer(manageUser)) {// 商户
                flag = true;
                listType = "dealer"; // 门店资金结算
                paramMap.put("dealerOid", manageUser.getDataDealer().getIwoid());
                // 查询管辖门店集合
                /*storeVoList = storeService.doJoinTransQueryStoreList(paramMap, 0, -1);
                paramMap.put("storeOid", rptDealerStatVO.getStoreOid());*/
                
                // 当天结算由交易明细表合计，当日之前结算从结算表查
                if (todayFlag) {
                    rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatTodayList4Dealer(paramMap, 0, -1);
                } else {
                    rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4Dealer(paramMap, 0, -1);
                }
            } else if (isCashier(manageUser)) {// 收银员
                flag = true;
                listType = "dealerEmployee";
                paramMap.put("dealerEmployeeOid", manageUser.getDataDealerEmployee().getIwoid());
                if (todayFlag) {
                    rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatTodayList4DealerE(paramMap, 0, -1);
                } else {
                    rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4DealerE(paramMap, 0, -1);
                }
            } else if (isStoreManager(manageUser)) {// 分店店长
                flag = true;
                listType = "dealerEmployee";
                paramMap.put("storeOid", manageUser.getDataDealerEmployee().getStore().getIwoid());
                //paramMap.put("dealerEmployeed", rptDealerStatVO.getDealerEmployeeId());
                if (todayFlag) {
                    rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatTodayList4Store(paramMap, 0, -1);
                } else {
                    rptDealerStatVoList = rptDealerStatService.doJoinTransQueryRptDealerStatList4Dealer(paramMap, 0, -1);
                }
            }

            if (!flag) {
                logger.warn("无权" + logPrefix);
                setAlertMessage("无权" + logPrefix);
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

        return "rptDealerStatByDayList";
    }

    /**
     * 当日结算（商户、收银员可见）
     * 
     * @return
     */
    public String listByDay() {
        // initPageData(PageAction.defaultLargePageSize);
        return goCurrent();
    }

    /**
     * 是否是商户
     * 
     * @return
     */
    private boolean isDealer(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.dealer.getValue() || manageUser.getDataDealer() == null) {// 非商户
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是普通收银员
     * 
     * @return
     */
    private boolean isCashier(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.cashier.getValue() || manageUser.getDataDealerEmployee() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是店长
     * 
     * @return
     */
    private boolean isStoreManager(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.shopManager.getValue() || manageUser.getDataDealerEmployee() == null || manageUser.getDataDealerEmployee().getStore() == null) {
                return false;
            }
        }
        return true;
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

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
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

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public void setRptDealerStatService(RptDealerStatService rptDealerStatService) {
        this.rptDealerStatService = rptDealerStatService;
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

}
