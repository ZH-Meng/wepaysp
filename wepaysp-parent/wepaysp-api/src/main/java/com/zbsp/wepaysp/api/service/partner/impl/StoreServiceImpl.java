package com.zbsp.wepaysp.api.service.partner.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
import com.zbsp.wepaysp.common.constant.EnumDefine.QRCodeType;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.QRCodeUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.google.zxing.WriterException;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.partner.StoreService;
import com.zbsp.wepaysp.vo.partner.StoreVO;

public class StoreServiceImpl
    extends BaseService
    implements StoreService {

    private SysLogService sysLogService;
    private SysConfigService sysConfigService;
    
    @Override
    public StoreVO doJoinTransQueryStoreByOid(String storeOid) {
        Validator.checkArgument(StringUtils.isBlank(storeOid), "门店Oid不能为空！");
        StoreVO storeVO = new StoreVO();
        Store store = commonDAO.findObject(Store.class, storeOid);
        if (store != null) {
            BeanCopierUtil.copyProperties(store, storeVO);
            // 商户
            Dealer d = store.getDealer();
            if (d != null) {
                storeVO.setDealerCompany(d.getCompany());
                // 服务商
                if (d.getPartner() != null) {
                    storeVO.setPartnerCompany(d.getPartner().getCompany());
                }
            }
        }
        return storeVO;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<StoreVO> doJoinTransQueryStoreList(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<StoreVO> resultList = new ArrayList<StoreVO>();

        /* 所属某商户下的所有门店查询 */
        // String state = MapUtils.getString(paramMap, "state");
        String storeName = MapUtils.getString(paramMap, "storeName");
        String storeTel = MapUtils.getString(paramMap, "storeTel");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        Validator.checkArgument(StringUtils.isBlank(dealerOid), "商户Oid不能为空！");

        StringBuffer sql = new StringBuffer("select distinct(s) from Store s, Dealer d where s.dealer=d");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(storeName)) {
            sql.append(" and s.storeName like :STORENAME");
            sqlMap.put("STORENAME", "%" + storeName + "%");
        }
        if (StringUtils.isNotBlank(storeTel)) {
            sql.append(" and s.storeTel like :STORETEL");
            sqlMap.put("STORETEL", "%" + storeTel + "%");
        }

        sql.append(" and s.dealer.iwoid = :DEALEROID");
        sqlMap.put("DEALEROID", dealerOid);

        sql.append(" order by s.storeId desc");
        List<Store> storeList = (List<Store>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (storeList != null && !storeList.isEmpty()) {
            for (Store store : storeList) {
                StoreVO vo = new StoreVO();
                BeanCopierUtil.copyProperties(store, vo);
                // 所属商户
                Dealer d = store.getDealer();
                vo.setDealerCompany(d.getCompany());
                // 服务商
                if (d.getPartner() != null) {
                    vo.setPartnerCompany(d.getPartner().getCompany());
                }
                resultList.add(vo);
            }
        }

        return resultList;
    }

    @Override
    public int doJoinTransQueryStoreCount(Map<String, Object> paramMap) {
        /* 所属某商户下的所有门店查询 */
        // String state = MapUtils.getString(paramMap, "state");
        String storeName = MapUtils.getString(paramMap, "storeName");
        String storeTel = MapUtils.getString(paramMap, "storeTel");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        Validator.checkArgument(StringUtils.isBlank(dealerOid), "商户Oid不能为空！");

        StringBuffer sql = new StringBuffer("select count(distinct s.iwoid) from Store s, Dealer d where s.dealer=d");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(storeName)) {
            sql.append(" and s.storeName like :STORENAME");
            sqlMap.put("STORENAME", "%" + storeName + "%");
        }
        if (StringUtils.isNotBlank(storeTel)) {
            sql.append(" and s.storeTel like :STORETEL");
            sqlMap.put("STORETEL", "%" + storeTel + "%");
        }

        sql.append(" and s.dealer.iwoid = :DEALEROID");
        sqlMap.put("DEALEROID", dealerOid);

        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @Override
    public StoreVO doTransAddStore(StoreVO storeVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException, IllegalAccessException {
        Validator.checkArgument(storeVO == null, "门店对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        //Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(storeVO.getStoreName()), "门店名称不能为空");
        // Validator.checkArgument(StringUtils.isBlank(storeVO.getStoreTel()), "门店联系电话不能为空");
        Validator.checkArgument(StringUtils.isBlank(storeVO.getDealerOid()), "商户Oid不能为空");
        
        Map<String, Object> paramMap = new HashMap<String, Object>();

        // 查找所属商户
        Dealer dealer = commonDAO.findObject(Dealer.class, storeVO.getDealerOid());
        if (dealer == null) {
            throw new NotExistsException("未找到商户信息！");
        }

        // 创建门店
        Store store = new Store();
        store.setIwoid(Generator.generateIwoid());
        store.setDealer(dealer);
        // 获取 门店ID下一个序列值
        String sql = "select nextval('" + SysSequenceCode.STORE + "') as sequence_value";
        paramMap.clear();
        Object seqObj = commonDAO.findObject(sql, paramMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("门店Id对应序列记录不存在");
        }
        String storeId = Generator.generateSequenceNum((Integer) seqObj, SysSequenceMultiple.STORE);
        store.setStoreId(storeId);
        store.setStoreAddress(storeVO.getStoreAddress());
        store.setStoreName(storeVO.getStoreName());
        store.setStoreTel(storeVO.getStoreTel());
        store.setCreator(creator);
        commonDAO.save(store, false);

        BeanCopierUtil.copyProperties(store, storeVO);

        Date processTime = new Date();

        // 增加门店日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建门店[门店ID=" + store.getStoreId() + ", 门店名称=" + store.getStoreName() + ", 联系人=" + store.getStoreAddress() + "]", processTime, processTime, null, store.toString(), SysLog.State.success.getValue(), store.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());

        return storeVO;
    }

    @Override
    public StoreVO doTransUpdateStore(StoreVO storeVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
        Validator.checkArgument(storeVO == null, "门店对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(storeVO.getIwoid()), "服务商Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(modifier), "修改人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        //Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(storeVO.getStoreName()), "门店名称不能为空");
        // Validator.checkArgument(StringUtils.isBlank(storeVO.getStoreTel()), "门店联系电话不能为空");

        Date processBeginTime = new Date();
        // 查找商户
        Store store = commonDAO.findObject(Store.class, storeVO.getIwoid());
        if (store == null) {
            throw new NotExistsException("未找到要修改的门店对象");
        }

        String storeStr = store.toString();
        store.setStoreName(storeVO.getStoreName());
        store.setStoreAddress(storeVO.getStoreAddress());
        store.setStoreTel(storeVO.getStoreTel());
        store.setQrCode(storeVO.getQrCode());
        store.setRemark(storeVO.getRemark());

        store.setModifier(modifier);
        commonDAO.update(store);

        String newStoreStr = store.toString();
        Date processEndTime = new Date();
        // 记录日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "修改门店[门店名称：" + store.getStoreName() + " 联系电话：" + store.getStoreTel() + "门店地址：" + store.getStoreAddress() + "]", processBeginTime, processEndTime, storeStr, newStoreStr, SysLog.State.success.getValue(), store.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());

        BeanCopierUtil.copyProperties(store, storeVO);
        return storeVO;
    }

    @Override
    public StoreVO doTransGetQRCode(int qRCodeType, String storeOid, String modifier, String operatorUserOid, String logFunctionOid) {
        Validator.checkArgument(StringUtils.isBlank(storeOid), "门店Oid不能为空！");
        StoreVO storeVO = new StoreVO();

        Store store = commonDAO.findObject(Store.class, storeOid);
        if (store == null) {
            throw new NotExistsException("门店不存在");
        }
        String qrCodePath = null;
        if (QRCodeType.PAY.getValue() == qRCodeType) {
        	qrCodePath = store.getQrCodePath();
        } else if (QRCodeType.BIND_PAY_NOTICE.getValue() == qRCodeType) {
        	qrCodePath = store.getBindQrCodePath();
        } else {
        	throw new IllegalArgumentException("参数错误，二维码类型不支持" + qRCodeType);
        }
        
        boolean qrCodeExist = false;
        if (StringUtils.isNotBlank(qrCodePath)) {
            File qrCodeFile = new File(qrCodePath);
            if (qrCodeFile.exists() && qrCodeFile.isFile()) {
                qrCodeExist = true;
            }
        }
        if (StringUtils.isBlank(qrCodePath) || !qrCodeExist) {
            String storeStr = store.toString();
            Dealer dealer = store.getDealer();
            Validator.checkArgument((dealer == null || StringUtils.isBlank(dealer.getIwoid())), "门店缺少商户信息无法生成二维码");
            String partnerOid = dealer.getPartner1Oid();// 所属顶级服务商Oid
            Validator.checkArgument(StringUtils.isBlank(partnerOid), "商户信息缺少partnerOid无法生成二维码");
            
            Map<String, Object> partnerMap = sysConfigService.getPartnerCofigInfoByPartnerOid(partnerOid);
            if (partnerMap == null || partnerMap.isEmpty()) {
                throw new NotExistsException("服务商信息配置不存在，partnerOid=" + partnerOid);
            }
            String appid = MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID);// 微信公众号ID
            
            String qrURL = null;
            Map<String, String> urlParamMap = new HashMap<String, String>();
            urlParamMap.put("partnerOid", partnerOid);
            urlParamMap.put("dealerOid", dealer.getIwoid());
            urlParamMap.put("storeOid", store.getIwoid());
            if (QRCodeType.PAY.getValue() == qRCodeType) {
                Validator.checkArgument(StringUtils.isBlank(SysConfig.payClientCheckURL), "未配置支付客户端检查地址无法生成支付二维码");
            } else if (QRCodeType.BIND_PAY_NOTICE.getValue() == qRCodeType) {
                Validator.checkArgument(StringUtils.isBlank(SysConfig.bindCallBackURL), "未配置微信支付通知绑定扫码回调地址无法生成二维码");
            }
            qrURL = Generator.generateQRURL(qRCodeType, appid, SysConfig.bindCallBackURL, SysConfig.payClientCheckURL, urlParamMap);
            
            // 生成二维码对应链接
            logger.info("门店-" + store.getStoreName() + "("+ dealer.getCompany() + ")生成业务二维码，类型：" + qRCodeType + "，URL：" + qrURL);

            // 路径生成规则：服务商ID/商户ID/门店ID
            String relativePath = dealer.getPartner().getPartnerId() + File.separator + dealer.getDealerId() + File.separator + store.getStoreId();
            File filePath = new File(SysConfig.qRCodeRootPath + File.separator + relativePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            String fileName = Generator.generateIwoid();
            // 生成二维码图片
            try {
                QRCodeUtil.writeToFile(qrURL, filePath.getPath(), fileName);
                logger.info("门店-" + store.getStoreName() + "("+ dealer.getCompany() + ")生成二维码（类型：" + qRCodeType + "）图片");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriterException e) {
                e.printStackTrace();
            }
            
            String pathTemp = filePath.getPath() + File.separator + fileName + ".png";
            // 更新门店二维码地址信息
            if (QRCodeType.PAY.getValue() == qRCodeType) {
            	store.setQrCodePath(pathTemp);
            } else if (QRCodeType.BIND_PAY_NOTICE.getValue() == qRCodeType) {
            	store.setBindQrCodePath(pathTemp);
            }
            commonDAO.update(store);
            
            Date processEndTime = new Date();
            // 记录修改日志
            sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "修改门店二维码信息[二维码地址：" + pathTemp + "]", processEndTime, processEndTime, storeStr, store.toString(), SysLog.State.success.getValue(), store.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());
        }
        BeanCopierUtil.copyProperties(store, storeVO);
        return storeVO;
    }
    
	@Override
	public String doJoinTransGetTopPartnerOid(String storeOid) {
		Validator.checkArgument(StringUtils.isBlank(storeOid), "storeOid不能为空！");
        String topPartnerOid = null;
        Store store = commonDAO.findObject(Store.class, storeOid);
        if (store != null) {
            // 商户
            Dealer d = store.getDealer();
            if (d != null) {
            	topPartnerOid = d.getPartner1Oid();
            }
        }
        return topPartnerOid;
	}
    
    public void setSysConfigService(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
