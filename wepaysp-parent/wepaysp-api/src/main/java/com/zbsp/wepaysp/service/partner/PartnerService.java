package com.zbsp.wepaysp.service.partner;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.vo.partner.PartnerVO;

/**
 * 代理商Service
 */
public interface PartnerService {
    
    /**
     * 根据主键查询代理商信息.
     * 
     * @param partnerOid 代理商Oid
     * @return 代理商对象
     */
    public PartnerVO doJoinTransQueryPartnerByOid(String partnerOid);
	
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:          String类型，登录名，根据此参数模糊查询
     *      contactor:     String类型，代理商联系人，根据此参数模糊查询
     *      parentCompany:		String类型，上级代理商公司，根据此参数模糊查询
     *      parentPartnerOid:         String类型，上级代理商Oid，根据此参数精确查询
     *      state:           Integer类型，代理商状态，根据此参数精确查询
     *      
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<PartnerVO> doJoinTransQueryPartnerList(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:          String类型，登录名，根据此参数模糊查询
     *      contactor:     String类型，代理商联系人，根据此参数模糊查询
     *      parentPartnerOid:         String类型，上级代理商Oid，根据此参数精确查询
     *      state:           Integer类型，代理商状态，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryPartnerCount(Map<String, Object> paramMap);
    
    /**
     * 创建代理商.
     * 
     * @param Partner 要保存的代理商对象 
     * @param creator 创建人名称
     * @param operatorUserOid 操作用户Oid，也是父代理商关联用户的Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的代理商对象VO
     * @throws AlreadyExistsException 如果代理商登录名已存在
     */
    public PartnerVO doTransAddPartner(PartnerVO partner, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException;
    
    /**
     * 修改代理商.
     * 
     * @param Partner 要修改的代理商对象
     * @param modifier 修改人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 修改成功返回修改后的代理商对象VO
     * @throws AlreadyExistsException 如果代理商登录名已存在
     */
    public PartnerVO doTransUpdatePartner(PartnerVO partner, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException;
    
}
