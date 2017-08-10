package com.zbsp.wepaysp.api.service.main.edu;

import java.util.Map;

public interface AlipayEduBillMainService {

    public Map<String, Object> handleAlipayPayEduBillNotify(Map<String, String> paramMap);
    
}
