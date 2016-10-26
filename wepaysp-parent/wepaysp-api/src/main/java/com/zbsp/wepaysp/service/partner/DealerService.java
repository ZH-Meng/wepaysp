package com.zbsp.wepaysp.service.partner;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.vo.partner.DealerVO;

/**
 * 商户Service
 */
public interface DealerService {
    
    /**
     * 根据主键查询商户信息.
     * 
     * @param dealerOid 商户Oid
     * @return 商户对象
     */
    public DealerVO doJoinTransQueryDealerByOid(String dealerOid);
	
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:          String类型，登录名，根据此参数模糊查询
     *      contactor:     String类型，商户联系人，根据此参数模糊查询
     *      parentCompany:		String类型，上级商户公司，根据此参数模糊查询
     *      parentDealerOid:         String类型，上级商户Oid，根据此参数精确查询
     *      state:           Integer类型，商户状态，根据此参数精确查询
     *      
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<DealerVO> doJoinTransQueryDealerList(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:          String类型，登录名，根据此参数模糊查询
     *      contactor:     String类型，商户联系人，根据此参数模糊查询
     *      parentDealerOid:         String类型，上级商户Oid，根据此参数精确查询
     *      state:           Integer类型，商户状态，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryDealerCount(Map<String, Object> paramMap);
    
    /**
     * 创建商户.
     * 
     * @param Dealer 要保存的商户对象 
     * @param creator 创建人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的商户对象VO
     * @throws AlreadyExistsException 如果商户登录名已存在
     */
    public DealerVO doTransAddDealer(DealerVO dealer, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException, IllegalAccessException;
    
    /**
     * 修改商户.
     * 
     * @param Dealer 要修改的商户对象
     * @param modifier 修改人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 修改成功返回修改后的商户对象VO
     * @throws AlreadyExistsException 如果商户登录名已存在
     */
    public DealerVO doTransUpdateDealer(DealerVO dealer, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException;
    
}
