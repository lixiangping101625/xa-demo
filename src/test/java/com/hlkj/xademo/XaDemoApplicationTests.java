package com.hlkj.xademo;

import com.hlkj.xademo.service.XAService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XaDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private XAService xaService;

    @Test
    public void testXA() {

        xaService.testXA();

    }

}
