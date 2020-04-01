package com.ke.cubbo.common;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestDTO {
    private String requestId;
    private String className;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] parameterTypes;
}
