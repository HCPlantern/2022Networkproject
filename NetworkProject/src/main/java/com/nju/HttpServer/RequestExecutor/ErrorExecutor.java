package com.nju.HttpServer.RequestExecutor;

import com.nju.HttpServer.Http.HttpRequest;
import com.nju.HttpServer.Http.HttpResponse;

public class ErrorExecutor extends BasicExecutor{

    public ErrorExecutor(){
        this.url = "/error";
        this.method = "get";
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws Exception{
        // do something bad
        throw new Exception("error");
    }
}
