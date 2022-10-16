package com.longmai.datademo.web.advise;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice {

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //这里可以拿到request信息来做特定的逻辑处理
        if (request.getURI().getPath().equals("/api/users")) {
            //测试添加http响应头
            response.getHeaders().set("X-Need-Masking", "laiyzhhhh");
        }

        return body;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }
}
