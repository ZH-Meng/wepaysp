package com.zbsp.wepaysp.service.dic;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class ZoneServiceImplTest {
    
    private ZoneService zoneService;
    
    @Before
    public void setUp() throws Exception {
        ApplicationContext ac = new FileSystemXmlApplicationContext("config/applicationContext.xml");
        zoneService = (ZoneService) ac.getBean("zoneService");
    }
    
    @Test 
    public void testDoTrans() {
        System.out.println("------------");
        System.out.println(zoneService);
    }
    
}
