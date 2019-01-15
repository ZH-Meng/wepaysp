package com.zbsp.wepaysp.api.service.partner;

import java.util.List;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.vo.partner.SchoolVO;

/**
 * 商户Service
 */
public interface SchoolService {
    
   
    public List<SchoolVO> doJoinTransQuerySchoolList(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      loginId:                         String类型，登录名，根据此参数模糊查询
     *      contactor:                      String类型，商户联系人，根据此参数模糊查询
     *      moblieNumber:             String类型，手机号，根据此参数模糊查询
     *      company:                         String类型，商户公司，根据此参数模糊查询
     *      partnerOid:                     String类型，服务商Oid，必填，根据此参数精确查询
     *      partnerEmployeeOid:      String类型，业务员Oid，非必填，如果非空，根据此参数精确查询
     *      coreDataFlag:         String类型，核心数据修改开关,on 开 off关，根据此参数精确查询
     *      state:           Integer类型，商户状态，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryDealerCount(Map<String, Object> paramMap);
    
    /**
     * 创建商户，同时创建商户默认门店，以及关联商户的用户
     * 
     * @param dealerVO 要保存的商户对象 
     * @param creator 创建人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的商户对象VO
     * @throws AlreadyExistsException 如果商户登录名已存在
     */
    public SchoolVO doTransAddSchool(SchoolVO schoolVO, String creator, String operatorUserOid, String logFunctionOid, String schoolNO)
        throws AlreadyExistsException, NotExistsException, AlipayApiException;
   
    /**
     * 获取当前商户归属的顶级服务商Oid
     * @param dealerOid
     * @return topPartnerOid
     */
    public String doJoinTransGetTopPartnerOid(String dealerOid);
}
