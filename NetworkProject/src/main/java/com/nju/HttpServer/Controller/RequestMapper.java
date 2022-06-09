package com.nju.HttpServer.Controller;

import com.nju.HttpServer.Common.RequestMethod;
import com.nju.HttpServer.Executors.ErrorExecutor;
import com.nju.HttpServer.Executors.LoginExecutor;
import com.nju.HttpServer.Executors.RegisterExecutor;
import com.nju.HttpServer.Http.HttpRequest;
import com.nju.HttpServer.Http.HttpResponse;
import com.nju.HttpServer.Controller.Router.RequestMapping;

/**
 * 类Springboot风格的注解匹配器,匹配规则：精确匹配
 **/
public class RequestMapper {

    @RequestMapping(uri = "/login", method = RequestMethod.POST)
    public HttpResponse Login(HttpRequest request) throws Exception {
        return new LoginExecutor().handle(request);
    }

    @RequestMapping(uri = "/register", method = RequestMethod.POST)
    public HttpResponse Register(HttpRequest request) throws Exception {
        return new RegisterExecutor().handle(request);
    }

    @RequestMapping(uri = "/error", method = RequestMethod.GET)
    public HttpResponse Error(HttpRequest request) throws Exception {
        return new ErrorExecutor().handle(request);
    }
}
