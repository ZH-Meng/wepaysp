package com.zbsp.wepaysp.api.service.app;

import com.zbsp.wepaysp.mo.version.v1_0.CheckVersionResponse;

/**
 * APP管理
 */
public interface AppManageService {

    /**
     * 查询出符合条件的最新版本的软件管理记录.
     * 
     * @param platformType 平台类型，取值参见，取值参见{@link com.iwt.vasoss.prvnterminal.po.operations.AppManage.SoftType}
     * @throws IllegalArgumentException
     * @return CheckVersionResponse
     */
    public CheckVersionResponse doJoinTransQueryLatestApp(Integer softType) throws IllegalArgumentException;
    
}
