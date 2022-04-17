package com.nju.HttpServer.RequestExecutor;

import com.nju.HttpServer.Http.HttpRequest;
import com.nju.HttpServer.Http.HttpResponse;

/**
 * 抽象父类
 * 请求地址+请求方法确定了executor是什么
 **/
public abstract class BasicExecutor {
    /**
     * eg /login
     */
    String url;

    /**
     * eg POST
     */
    String method;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public abstract HttpResponse handle(HttpRequest request) throws Exception;
}
