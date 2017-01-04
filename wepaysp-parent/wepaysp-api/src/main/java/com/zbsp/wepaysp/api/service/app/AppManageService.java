package com.zbsp.wepaysp.api.service.app;

import com.zbsp.wepaysp.po.app.AppManage;

/**
 * APP管理
 */
public interface AppManageService {

    /**
     * 查询出符合条件的最新版本的软件管理记录.
     * 
     * @param platformType 平台类型，取值参见，取值参见{@link com.iwt.vasoss.prvnterminal.po.operations.AppManage.SoftType}
     * @return 最新版本的软件管理记录
     */
    public AppManage doJoinTransQueryLatestApp(Integer softType);
    
}
