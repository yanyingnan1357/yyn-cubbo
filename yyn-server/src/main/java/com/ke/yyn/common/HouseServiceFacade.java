package com.ke.yyn.common;

//对外提供服务的facade接口
public interface HouseServiceFacade {

    void save(HouseDTO houseDTO);


    void deleteById(String buildingId);


    void update(HouseDTO houseDTO);


    HouseDTO get(String buildingId);
}
