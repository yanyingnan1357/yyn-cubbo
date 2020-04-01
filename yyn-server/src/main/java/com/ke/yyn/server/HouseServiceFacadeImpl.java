package com.ke.yyn.server;

import com.ke.cubbo.server.Service;
import com.ke.yyn.common.HouseDTO;
import com.ke.yyn.common.HouseServiceFacade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component//zk上要存服务版本
@Service(HouseServiceFacade.class)//对应dubbo中的@Service注解 将服务发布出去
public class HouseServiceFacadeImpl implements HouseServiceFacade {

    public void save(HouseDTO houseDTO) {
        System.out.println("房屋添加成功: " + houseDTO);
    }

    public void deleteById(String buildingId) {
        System.out.println("房屋删除成功: " + buildingId);
    }

    public void update(HouseDTO houseDTO) {
        System.out.println("房屋修改成功: " + houseDTO);
    }

    public HouseDTO get(String buildingId) {
        System.out.println("房屋信息获取成功");
        return new HouseDTO(1, "001", "嘉年华", BigDecimal.TEN);
    }
}
