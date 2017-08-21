package com.zbsp.wepaysp.api.service.edu.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.zbsp.alipay.trade.model.ChargeItems;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.AliPayEnums.TradeState4AliPay;
import com.zbsp.wepaysp.common.exception.InvalidValueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.EnumUtil;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.edu.AlipayEduBill.OrderStatus;
import com.zbsp.wepaysp.po.edu.AlipayEduNotify;
import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill;
import com.zbsp.wepaysp.vo.edu.AlipayEduBillVO;


public class AlipayEduBillServiceImpl
    extends BaseService
    implements AlipayEduBillService {

    @Override
    public List<AlipayEduBillVO> doJoinTransQueryAlipayEduBill(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<AlipayEduBillVO> resultList = new ArrayList<AlipayEduBillVO>();
        StringBuilder jpqlBuilder = new StringBuilder("from AlipayEduBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);

        @SuppressWarnings("unchecked")
        List<AlipayEduBill> billList = (List<AlipayEduBill>) commonDAO.findObjectList(jpqlBuilder.toString(), jpqlMap, false, startIndex, maxResult);
        if (billList != null && !billList.isEmpty()) {
            for (AlipayEduBill bill : billList) {
                AlipayEduBillVO billVO = new AlipayEduBillVO();
                BeanCopierUtil.copyProperties(bill, billVO);
                
                // 组装账单费用项
                if (StringUtils.isNotBlank(bill.getChargeItem())) {
                    List<ChargeItems> chargeItems = JSONUtil.parseArray(bill.getChargeItem(), ChargeItems.class);
                    billVO.setChargeItems(chargeItems);
                }
                // 转换金额单位为元
                billVO.setAmountYuan(new BigDecimal(bill.getAmount()).divide(SysEnvKey.BIG_100));
                // 转换账单状态描述
                OrderStatus status = EnumUtil.getEnumByGetValueMethod(OrderStatus.class, bill.getOrderStatus());
                if (status != null)
                    billVO.setOrderStatusStr(status.getDesc());
                else
                    billVO.setOrderStatusStr("未知");
                
                resultList.add(billVO);
            }
        }
        return resultList;
    }

    @Override
    public int doJoinTransQueryAlipayEduBillCount(Map<String, Object> paramMap) {
        StringBuilder jpqlBuilder = new StringBuilder("select count(a.iwoid) from AlipayEduBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);
        return commonDAO.queryObjectCount(jpqlBuilder.toString(), jpqlMap, false);
    }
    
    /** 返回jpql查询参数 */
    private Map<String, Object> assembleQueryCondition(StringBuilder jpqlBuilder, Map<String, Object> paramMap) {
        String schoolNo = MapUtils.getString(paramMap, "schoolNo");
        String childName = MapUtils.getString(paramMap, "childName");
        String userMobile = MapUtils.getString(paramMap, "userMobile");
        String orderStatus = MapUtils.getString(paramMap, "orderStatus");
        String totalBillOid = MapUtils.getString(paramMap, "totalBillOid");// 总账单Oid

        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(schoolNo)) {
            jpqlBuilder.append(" and a.schoolNo=:SCHOOLNO");
            jpqlMap.put("SCHOOLNO", schoolNo);
        }
        if (StringUtils.isNotBlank(totalBillOid)) {
            jpqlBuilder.append(" and a.alipayEduTotalBillOid=:TOTALBILLOID");
            jpqlMap.put("TOTALBILLOID", totalBillOid);
        }
        if (StringUtils.isNotBlank(childName)) {
            jpqlBuilder.append(" and a.childName like :CHILDNAME");
            jpqlMap.put("CHILDNAME", "%" + childName + "%");
        }
        if (StringUtils.isNotBlank(userMobile)) {
            jpqlBuilder.append(" and a.userMobile like :USERMOBILE");
            jpqlMap.put("USERMOBILE", "%" + userMobile + "%");
        }
        if (StringUtils.isNotBlank(orderStatus)) {
            jpqlBuilder.append(" and a.orderStatus=:ORDERSTATUS");
            jpqlMap.put("ORDERSTATUS", orderStatus);
        }
        return jpqlMap;
    }

    @Override
    public void doTransBatchSaveAlipayEduBills(List<AlipayEduBill> billList) {
        Validator.checkArgument(billList == null, "billList 不能为空");
        commonDAO.saveList(billList, 100);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AlipayEduBill> doJoinTransQueryAlipayEduBillByStatus(String totalBillOid, OrderStatus status) {
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AlipayEduBill a where 1=1";
        if (StringUtils.isNotBlank(totalBillOid)) {
            jpql += " and a.alipayEduTotalBillOid=:TOTALBILLOID";
            jpqlMap.put("TOTALBILLOID", totalBillOid);
        }
        if (status != null) {
            jpql += " and a.orderStatus=:STATUS";
            jpqlMap.put("STATUS", status.name());
        }
        return (List<AlipayEduBill>) commonDAO.findObjectList(jpql, jpqlMap, false);
    }

    @Override
    public void doTransUpdateAlipayEduBill(AlipayEduBill bill) {
        Validator.checkArgument(bill == null, "bill 不能为空");
        commonDAO.update(bill);
    }

    @Override
    public AlipayEduBill doTransUpdateBillByAlipayEduNotify(AlipayEduNotify eduNotify) {
        Validator.checkArgument(eduNotify == null, "eduNotify 不能为空");
        Validator.checkArgument(eduNotify.getAlipayEduBill() == null, "eduNotify.alipayEduBill 不能为空");
        AlipayEduBill  bill = eduNotify.getAlipayEduBill();
        
        // 比较重要参数
        if (NumberUtils.compare(eduNotify.getTotalAmoun(), bill.getAmount()) != 0) {
            logger.warn( "检查通知内容 - 失败 - total_amount不一致，通知total_amount={}, 支付明细total_amount={}", eduNotify.getTotalAmoun(), bill.getAmount());
            throw new InvalidValueException("total_amount不一致");
        }
        
        if (StringUtils.isNotBlank(eduNotify.getSellerId()) && StringUtils.isNotBlank(bill.getSchoolPid()) && !StringUtils.equals(eduNotify.getSellerId(), bill.getSchoolPid())) {
            logger.warn("检查通知内容 - 失败 - seller_id不一致，通知seller_id={}, 支付明细seller_id={}", eduNotify.getSellerId(), bill.getSchoolPid());
            throw new InvalidValueException("seller_id不一致");
        }
        
        // 根据状态进行账单处理
        String tradeStatus = eduNotify.getTradeStatus();
        logger.info("教育缴费异步通知，支付状态：{}，k12OrderNo：{}，更新前状态：{}", tradeStatus, bill.getK12OrderNo(), bill.getOrderStatus());
        
        if (TradeState4AliPay.WAIT_BUYER_PAY.name().equalsIgnoreCase(tradeStatus)) {
            bill.setOrderStatus(OrderStatus.PAYING.name());
        } else if (TradeState4AliPay.TRADE_SUCCESS.name().equalsIgnoreCase(tradeStatus)) {
            bill.setOrderStatus(OrderStatus.PAY_SUCCESS.name());
            bill.setBuyerLogonId(eduNotify.getBuyerLogonId());
            bill.setGmtPayment(eduNotify.getGmtPayment());
            bill.setTradeNo(eduNotify.getTradeNo());
            
            // 更新账单已缴费总金额和已缴费人数
            AlipayEduTotalBill totalBill = commonDAO.findObject(AlipayEduTotalBill.class, bill.getAlipayEduTotalBillOid());
            totalBill.setReceiptCount(totalBill.getReceiptCount() + 1);
            totalBill.setReceiptMoney(totalBill.getReceiptMoney() + bill.getAmount());
            commonDAO.update(totalBill);
        } else if (TradeState4AliPay.TRADE_CLOSED.name().equalsIgnoreCase(tradeStatus) || TradeState4AliPay.TRADE_FINISHED.name().equalsIgnoreCase(tradeStatus)) {
            // TODO 交易关闭（未支付或全额退款）、
            logger.warn("教育缴费异步通知，支付状态：{}，未处理", tradeStatus);
        }
        
        commonDAO.update(bill);
        return bill;
    }
    
	@Override
	public AlipayEduBill doJoinTransQueryBillByOid(String billOid) {
		Validator.checkArgument(StringUtils.isBlank(billOid), "billOid不能为空");
		AlipayEduBill bill = commonDAO.findObject(AlipayEduBill.class, billOid);
		if (bill == null) {
			throw new NotExistsException("明细不存在Oi=" + billOid);
		}
		return bill;
	}

    @Override
    public void doTransUpdateBillList(List<AlipayEduBill> billList) {
        Validator.checkArgument(billList == null || billList.isEmpty(), "billList不能为空");
        commonDAO.updateList(billList);
    }

}
