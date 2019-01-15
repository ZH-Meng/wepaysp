package com.zbsp.wepaysp.api.service.app.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.app.AppManageService;
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mo.version.v1_0.CheckVersionResponse;
import com.zbsp.wepaysp.po.app.AppManage;

/**
 * APP管理
 */
public class AppManageServiceImpl extends BaseService implements AppManageService {

    @Override
    public CheckVersionResponse doJoinTransQueryLatestApp(Integer softType) throws IllegalArgumentException {
        Validator.checkArgument(!Validator.contains(AppManage.SoftType.class, softType), "客户端类型取值不正确");

        String sql = "select s from AppManage s where s.softType = :SOFTTYPE order by s.versionNumber desc";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("SOFTTYPE", softType);

        List<AppManage> appManageList = commonDAO.findObjectList(sql, paramMap, false, 0, 1);
        CheckVersionResponse response = null;
        String responseId = Generator.generateIwoid();
        
        if (appManageList != null && !appManageList.isEmpty()) {
            AppManage appManage = appManageList.get(0);
            response = new CheckVersionResponse(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getDesc(), responseId);
            response.setVersionName(appManage.getVersionName());
            response.setVersionNumber(appManage.getVersionNumber());
            response.setDescription(appManage.getVersionDesc());
            response.setDownloadUrl(appManage.getDownloadUrl());
            response.setMinNumber(appManage.getSupportMinVersionNumber());
            response.setMinVersion(appManage.getSupportMinVersionName());
            response.setTipType(appManage.getTipType());
            response.setFileSize(appManage.getFileSize());
        } else {
            logger.warn("没有版本信息，客户端类型：{}！", softType);
            response = new CheckVersionResponse(CommonResult.DATA_NOT_EXIST.getCode(), "版本信息" + CommonResult.DATA_NOT_EXIST.getDesc(), responseId);
        }
        
        return response;
    }

}
