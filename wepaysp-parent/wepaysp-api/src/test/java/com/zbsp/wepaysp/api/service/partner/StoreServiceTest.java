package com.zbsp.wepaysp.api.service.partner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.zbsp.wepaysp.vo.partner.StoreVO;


public class StoreServiceTest {
    
    private StoreService storeService = null;
    private ApplicationContext ac;
    @Before
    public void setUp()
        throws Exception {
        ac = new FileSystemXmlApplicationContext("config/applicationContext.xml");
        storeService = (StoreService) ac.getBean("storeService");
        Assert.assertNotNull(storeService);
    }

    @Test
    public void testQuery()
        throws Exception {
        //String storeOid = "2e19a0deb90a45348c584d52d5a18346";
        //String storeOid = "07786f262fda43ea90e26f3bcadfe63c";
        
        String storeOid="1a884bec86984aa38862ea9317c179e5";
        
        StoreVO store = storeService.doJoinTransQueryStoreByOid(storeOid);
        Assert.assertNotNull(store);
        System.out.println(store.getDealerCompany());
    }
}
