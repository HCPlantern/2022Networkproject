package com.nju.HttpServer.Common;

/**
 * 请求方法的枚举类
 **/
public class RequestMethod {
    public static final String GET = "GET";
    public static final String POST = "POST";

    /**
     * 如果请求头中含有已定义的请求方法，转换为枚举方便以后处理
     **/
    public static final String parse(String s) {
        if (s.equalsIgnoreCase("get")) s = RequestMethod.GET;
        if (s.equalsIgnoreCase("post")) s = RequestMethod.POST;
        return s;
    }
}
