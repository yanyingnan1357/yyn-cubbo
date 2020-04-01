package com.ke.yyn.client;

import com.ke.cubbo.client.Proxy;
import com.ke.yyn.common.HouseDTO;
import com.ke.yyn.common.HouseServiceFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)//让测试运行于Spring测试环境
@ContextConfiguration(locations = "classpath:application.xml")//Spring整合JUnit4测试时，使用注解引入多个配置文件
public class TestClient {
    @Autowired
    private Proxy proxy;
    private HouseServiceFacade houseServiceFacade;

    //对应dubbo中的@Reference注解 去应用服务
    @Before
    public void init() {
        houseServiceFacade = proxy.getInstance(HouseServiceFacade.class);
    }


    @Test
    public void testSave() throws Exception {
        houseServiceFacade.save(new HouseDTO(2, "002", "木山一区", BigDecimal.TEN));
    }

    @Test
    public void testDelete() throws Exception {
        houseServiceFacade.deleteById("002");
    }

    @Test
    public void testUpdate() throws Exception {
        houseServiceFacade.update(new HouseDTO(2, "002", "家和花园", BigDecimal.ONE));
    }

    @Test
    public void testGet() throws Exception {
        Thread.sleep(180000);
        HouseDTO houseDTO = houseServiceFacade.get("001");
        System.out.println("房屋信息返回成功:" + houseDTO);
    }
}
