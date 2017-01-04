package com.zbsp.wepaysp.api.service.app.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.app.AppManageService;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.app.AppManage;

/**
 * APP管理
 */
public class AppManageServiceImpl extends BaseService implements AppManageService {

    @SuppressWarnings("unchecked")
    @Override
    public AppManage doJoinTransQueryLatestApp(Integer softType) {
        Validator.checkArgument(!Validator.contains(AppManage.SoftType.class, softType), "客户端类型取值不正确");

        String sql = "select s from AppManage s where s.softType = :SOFTTYPE order by s.versionNumber desc";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("SOFTTYPE", softType);

        List<AppManage> appManageList = (List<AppManage>) commonDAO.findObjectList(sql, paramMap, false, 0, 1);

        if (appManageList != null && !appManageList.isEmpty()) {
            return appManageList.get(0);
        } else {
            throw new NotExistsException("没有版本信息，客户端类型：" + softType);
        }
    }

}
