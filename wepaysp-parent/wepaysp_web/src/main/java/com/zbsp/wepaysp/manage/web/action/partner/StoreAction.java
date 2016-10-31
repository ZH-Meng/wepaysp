package com.zbsp.wepaysp.manage.web.action.partner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.service.partner.StoreService;
import com.zbsp.wepaysp.vo.partner.StoreVO;

/**
 * 门店管理
 * 
 * @author mengzh
 *
 */
public class StoreAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = -7078956274536886116L;

    private Map<String, Object> session;
    private StoreVO storeVO;
    private List<StoreVO> storeVoList;
    private StoreService storeService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            if (storeVO == null) {
                storeVO = new StoreVO();
            }
            paramMap.put("storeName", storeVO.getStoreName());
            paramMap.put("storeTel", storeVO.getStoreTel());
            // 所属商户
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (manageUser.getUserLevel() == null || manageUser.getUserLevel().intValue() > SysUser.UserLevel.dealer.getValue()) {// 系统用户或者商户级别以下的应用用户
                logger.warn("角色分配不当：商户级别以下用户不能查看门店列表");
                setAlertMessage("角色分配不当：商户级别以下用户不能查看门店列表");
                return "accessDenied";
            }
            String dealerOid = storeVO.getDealerOid();
            if (manageUser.getUserLevel().intValue() < SysUser.UserLevel.dealer.getValue()) {// 服务商、业务员
                if (StringUtils.isBlank(dealerOid)) {
                    logger.warn("门店查询列表失败：商户级别以上用户需要提供商户Oid才能查看门店列表");
                    setAlertMessage("门店查询列表失败：商户级别以上用户需要提供商户Oid才能查看门店列表");
                    return "accessDenied";
                }
            } else {// 商户
                if (manageUser.getDataDealer() == null) {
                    logger.warn("门店查询列表失败：当前用户属于商户级别，但是没有关联商户信息");
                    setAlertMessage("门店查询列表失败：当前用户属于商户级别，但是没有关联商户信息");
                    return "accessDenied";
                } else {
                    dealerOid = manageUser.getDataDealer().getIwoid();
                }
            }
            paramMap.put("dealerOid", dealerOid);
            storeVoList = storeService.doJoinTransQueryStoreList(paramMap, start, size);
            rowCount = storeService.doJoinTransQueryStoreCount(paramMap);
        } catch (Exception e) {
            logger.error("门店查询列表错误：" + e.getMessage());
            setAlertMessage("门店查询列表错误：" + e.getMessage());
        }
        return "storeList";
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    public String goToCreateStore() {
        logger.info("跳转创建门店页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isDealer(manageUser)) {
            logger.warn("角色分配不当：非商户用户不能创建门店");
            setAlertMessage("角色分配不当：非商户用户不能创建门店");
            return "accessDenied";
        }
        storeVO = null;
        return "createStore";
    }

    public String createStore() {
        logger.info("开始创建门店.");
        try {
            ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!isDealer(manageUser)) {
                logger.warn("角色分配不当：非商户用户不能创建门店");
                setAlertMessage("角色分配不当：非商户用户不能创建门店");
                return "accessDenied";
            }

            if (storeVO == null) {
                logger.warn("创建门店失败，参数" + storeVO + "为空！");
                setAlertMessage("创建门店失败！");
            }

            storeService.doTransAddStore(storeVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            logger.info("门店" + storeVO.getStoreName() + "添加成功");
            setAlertMessage("门店" + storeVO.getStoreName() + "添加成功");
            storeVO = null;
        } catch (AlreadyExistsException e) {
            logger.error("门店添加失败：" + e.getMessage());
            setAlertMessage("门店添加失败：" + e.getMessage());
            return "createStore";
        } catch (Exception e) {
            logger.error("门店添加错误：" + e.getMessage());
            setAlertMessage("门店添加错误：" + e.getMessage());
            return "error";
        }
        return list();
    }

    public String goToUpdateStore() {
        logger.info("跳转修改门店页面.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isDealer(manageUser)) {
            logger.warn("角色分配不当：非商户用户不能修改门店");
            setAlertMessage("角色分配不当：非商户用户不能修改门店");
            return "accessDenied";
        }
        if (storeVO != null && StringUtils.isNotBlank(storeVO.getIwoid())) {
            storeVO = storeService.doJoinTransQueryStoreByOid(storeVO.getIwoid());
        } else {
            logger.warn("非法修改门店，参数storeVO为空，storeVO.getIwoid()或者！");
            setAlertMessage("非法修改门店！");
            return list();
        }
        return "updateStore";
    }

    public String updateStore() {
        logger.info("开始修改门店.");
        try {
            if (storeVO != null && StringUtils.isNotBlank(storeVO.getIwoid())) {
                ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (!isDealer(manageUser)) {
                    logger.warn("角色分配不当：非商户用户不能修改门店");
                    setAlertMessage("角色分配不当：非商户用户不能修改门店");
                    return "accessDenied";
                }

                storeService.doTransUpdateStore(storeVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
                logger.info("门店" + storeVO.getStoreName() + "修改成功");
                setAlertMessage("门店" + storeVO.getStoreName() + "修改成功");
            } else {
                logger.warn("修改门店失败，参数storeVO或者storeVO.getIwoid()为空！");
                setAlertMessage("修改门店失败！");
                return list();
            }
        } catch (NotExistsException e) {
            logger.error("门店修改失败：" + e.getMessage());
            setAlertMessage("门店修改失败：" + e.getMessage());
            return list();
        } catch (Exception e) {
            logger.error("门店添加错误：" + e.getMessage());
            setAlertMessage("门店添加错误：" + e.getMessage());
            return "error";
        }
        return "updateStore";
    }

    /**
     * 是否是商户并且用户有关联商户
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

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public StoreVO getStoreVO() {
        return storeVO;
    }

    public void setStoreVO(StoreVO storeVO) {
        this.storeVO = storeVO;
    }

    public List<StoreVO> getStoreVoList() {
        return storeVoList;
    }

    public void setStoreVoList(List<StoreVO> storeVoList) {
        this.storeVoList = storeVoList;
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

}
