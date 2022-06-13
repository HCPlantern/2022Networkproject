package com.nju.HttpServer.Services;

import com.nju.HttpServer.Http.HttpRequest;
import com.nju.HttpServer.Http.HttpResponse;


public class ErrorService implements Service {
    public HttpResponse handle(HttpRequest request) throws Exception {
        // do something bad
        // 返回500页面
        throw new Exception("自定义异常，用于测试异常抛出");
    }
}
