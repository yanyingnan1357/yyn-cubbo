package com.ke.yyn.common;

import lombok.*;

import java.math.BigDecimal;

/**
 * 房屋信息
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HouseDTO {
    private int id;//id
    private String buildingId;//房屋编号
    private String resclockName;//小区名称
    private BigDecimal price;//房屋价格
}
