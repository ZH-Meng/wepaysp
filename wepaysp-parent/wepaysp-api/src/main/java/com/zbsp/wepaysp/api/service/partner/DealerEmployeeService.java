package com.zbsp.wepaysp.api.service.partner;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.vo.partner.DealerEmployeeVO;

/**
 * 商户员工（收银员）Service
 */
public interface DealerEmployeeService {
    
    /**
     * 根据主键查询商户员工信息.
     * 
     * @param dealerEmployeeOid 商户员工Oid
     * @return 商户员工对象
     */
    public DealerEmployeeVO doJoinTransQueryDealerEmployeeByOid(String dealerEmployeeOid);
	
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *		dealerOid:			 		String类型，商户Oid，根据此参数精确查询
     *		storeOid:					String类型，商户Oid，根据此参数精确查询
     *      dealerEmployeeOid   String类型，商户员工Oid，根据此参数精确查询
     *      employeeName:    	 String类型，商户员工姓名，根据此参数模糊查询
     *      moblieNumber:         String类型，商户员工手机号，根据此参数模糊查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<DealerEmployeeVO> doJoinTransQueryDealerEmployeeList(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *		dealerOid:			 		String类型，商户Oid，根据此参数精确查询
     *      employeeName:    	 String类型，商户员工姓名，根据此参数模糊查询
     *      moblieNumber:         String类型，商户员工手机号，根据此参数模糊查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryDealerEmployeeCount(Map<String, Object> paramMap);
    
    /**
     * 创建商户员工，同时创建关联员工的用户
     * 
     * @param dealerEmployeeVO 要保存的商户员工对象 
     * @param creator 创建人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的商户员工对象VO
     * @throws AlreadyExistsException 如果商户员工登录名已存在
     */
    public DealerEmployeeVO doTransAddDealerEmployee(DealerEmployeeVO dealerEmployeeVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException, IllegalAccessException;
    
    /**
     * 修改商户员工.
     * 
     * @param dealerEmployeeVO 要修改的商户员工对象
     * @param modifier 修改人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 修改成功返回修改后的商户员工对象VO
     * @throws AlreadyExistsException 如果商户员工登录名已存在
     */
    public DealerEmployeeVO doTransUpdateDealerEmployee(DealerEmployeeVO dealerEmployeeVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException;
    
    /**
     * 商户员工修改退款密码.
     * 
     * @param dealerEmployeeOid 商户员工Oid
     * @param oldPwd 原退款密码
     * @param newPwd 新退款密码
     * @return 修改成功返回修改后的商户员工对象VO
     * @throws IllegalAccessException 如果原密码不正确
     */
    public DealerEmployeeVO doTransModifyRefundPwd(String dealerEmployeeOid, String oldPwd, String newPwd, String modifier, String operatorUserOid, String logFunctionOid) throws IllegalAccessException;
    
    /**
     * 商户重置后台商户员工退款密码.
     * 
     * @param dealerEmployeeOid 要重置密码的商户员工Oid
     * @param newPwd 重置后的新密码
     * @param operatorOid 执行重置操作的用户Oid
     * @param operatorName 执行重置操作的用户名称
     * @param logFunctionOid 日志功能项Oid
     * @return 修改成功返回修改后的商户员工对象VO
     */
    public DealerEmployeeVO doTransResetRefundPwd(String dealerEmployeeOid, String newPwd, String operatorOid, String operatorName, String logFunctionOid);

    /**
     * 获取收银员级别二维码（如果不存在则生成）
     * @param qRCodeType 二维码类型：1为支付二维码，2为微信支付通知绑定二维码
     * @param dealerEmployeeOid
     * @param modifier 修改人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return DealerEmployeeVO 包含二维码path
     */
    public DealerEmployeeVO doTransGetQRCode(int qRCodeType, String dealerEmployeeOid, String modifier, String operatorUserOid, String logFunctionOid);

    /**
     * 获取当前收银员归属的顶级服务商Oid
     * @param dealerEmployeeOid
     * @return topPartnerOid
     */
	public String doJoinTransGetTopPartnerOid(String dealerEmployeeOid);
    
}
