package com.nju.HttpServer.Controller.Executors;

import com.nju.HttpServer.Common.Template;
import com.nju.HttpServer.Http.HttpRequest;
import com.nju.HttpServer.Http.HttpResponse;

public class ErrorExecutor implements Executor {
    public HttpResponse handle(HttpRequest request) {
        // do something bad
        // 返回500页面
        return Template.generateStatusCode_500();
    }
}
