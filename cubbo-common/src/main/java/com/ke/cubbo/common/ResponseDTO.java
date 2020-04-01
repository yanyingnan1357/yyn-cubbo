package com.ke.cubbo.common;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseDTO {
    private String responseId;
    private String requestId;
    private boolean success;
    private Object result;
    private Throwable throwable; //如果有异常信息,在该对象中记录异常信息
}
