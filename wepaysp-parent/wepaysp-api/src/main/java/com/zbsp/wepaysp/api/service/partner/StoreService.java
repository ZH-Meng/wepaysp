package com.zbsp.wepaysp.api.service.partner;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.vo.partner.StoreVO;

/**
 * 门店Service
 */
public interface StoreService {
    
    /**
     * 根据主键查询门店信息.
     * 
     * @param storeOid 门店Oid
     * @return 门店对象
     */
    public StoreVO doJoinTransQueryStoreByOid(String storeOid);
	
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      dealerOid:       String类型，门店所属商户Oid，必填，精确查找
     *      storeName:     String类型，门店名称，根据此参数模糊查询
     *      storeTel:         String类型，门店联系电话，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<StoreVO> doJoinTransQueryStoreList(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      dealerOid:       String类型，门店所属商户Oid，必填，精确查找
     *      storeName:     String类型，门店名称，根据此参数模糊查询
     *      storeTel:         String类型，门店联系电话，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryStoreCount(Map<String, Object> paramMap);
    
    /**
     * 创建门店.
     * 
     * @param storeVO 要保存的门店对象 
     * @param creator 创建人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的门店对象VO
     * @throws AlreadyExistsException 如果门店登录名已存在
     */
    public StoreVO doTransAddStore(StoreVO storeVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException, IllegalAccessException;
    
    /**
     * 修改门店.
     * 
     * @param storeVO 要修改的门店对象
     * @param modifier 修改人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 修改成功返回修改后的门店对象VO
     * @throws AlreadyExistsException 如果门店登录名已存在
     */
    public StoreVO doTransUpdateStore(StoreVO storeVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException;
    
    /**
     * 获取门店级别二维码（如果不存在则生成）
     * @param qRCodeType 二维码类型：1为支付二维码，2为微信支付通知绑定二维码
     * @param storeOid
     * @param modifier 修改人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return StoreVO 包含二维码path
     */
    public StoreVO doTransGetQRCode(int qRCodeType, String storeOid, String modifier, String operatorUserOid, String logFunctionOid);

}
