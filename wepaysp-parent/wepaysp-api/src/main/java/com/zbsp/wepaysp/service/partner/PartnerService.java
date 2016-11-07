package com.zbsp.wepaysp.service.partner;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.vo.partner.PartnerVO;

/**
 * 服务商Service，系统内部不区分服务商、代理商
 */
public interface PartnerService {
    
    /**
     * 根据主键查询服务商信息.
     * 
     * @param partnerOid 服务商Oid
     * @return 服务商对象
     */
    public PartnerVO doJoinTransQueryPartnerByOid(String partnerOid);
	
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      loginId:          String类型，登录名，根据此参数模糊查询
     *      contactor:     String类型，服务商联系人，根据此参数模糊查询
     *      company:		String类型，服务商公司，根据此参数模糊查询
     *      parentPartnerOid:         String类型，上级服务商Oid，根据此参数精确查询
     *      state:           Integer类型，服务商状态，根据此参数精确查询
     *      currentUserOid:         String类型，当前用户Oid，根据此参数精确查询
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
     *      loginId:          String类型，登录名，根据此参数模糊查询
     *      contactor:     String类型，服务商联系人，根据此参数模糊查询
     *      company:        String类型，服务商公司，根据此参数模糊查询
     *      parentPartnerOid:         String类型，上级服务商Oid，根据此参数精确查询
     *      state:           Integer类型，服务商状态，根据此参数精确查询
     *      currentUserOid:         String类型，当前用户Oid，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryPartnerCount(Map<String, Object> paramMap);
    
    /**
     * 创建服务商，同时创建关联服务商的用户
     * 
     * @param partnerVO 要保存的服务商对象 
     * @param creator 创建人名称
     * @param operatorUserOid 操作用户Oid，也是父服务商关联用户的Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的服务商对象VO
     * @throws AlreadyExistsException 如果服务商登录名已存在
     */
    public PartnerVO doTransAddPartner(PartnerVO partnerVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException;
    
    /**
     * 修改服务商.
     * 
     * @param partnerVO 要修改的服务商对象
     * @param modifier 修改人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 修改成功返回修改后的服务商对象VO
     * @throws AlreadyExistsException 如果服务商登录名已存在
     */
    public PartnerVO doTransUpdatePartner(PartnerVO partnerVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException;
    
}
