package com.nju.HttpServer.Controller;

import com.nju.HttpServer.Common.RequestMethod;
import com.nju.HttpServer.Common.Template;
import com.nju.HttpServer.Services.ErrorService;
import com.nju.HttpServer.Services.LoginService;
import com.nju.HttpServer.Services.RegisterService;
import com.nju.HttpServer.Http.HttpRequest;
import com.nju.HttpServer.Http.HttpResponse;
import com.nju.HttpServer.Controller.Router.RequestMapping;

/**
 * 类Springboot风格的注解匹配器,匹配规则：精确匹配
 **/
public class RequestMapper {
    @RequestMapping(uri = "/", method = RequestMethod.GET)
    public HttpResponse Redirect(HttpRequest request) throws Exception {
        return Template.generateStatusCode_301("/index.html");
    }

    @RequestMapping(uri = "/login", method = RequestMethod.POST)
    public HttpResponse Login(HttpRequest request) throws Exception {
        return new LoginService().handle(request);
    }

    @RequestMapping(uri = "/register", method = RequestMethod.POST)
    public HttpResponse Register(HttpRequest request) throws Exception {
        return new RegisterService().handle(request);
    }

    @RequestMapping(uri = "/error", method = RequestMethod.GET)
    public HttpResponse Error(HttpRequest request) throws Exception {
        return new ErrorService().handle(request);
    }
}
