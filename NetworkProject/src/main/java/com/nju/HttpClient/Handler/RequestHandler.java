package com.nju.HttpClient.Handler;

import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Request.RequestLine;
import com.nju.HttpClient.LocalCache.LastModifiedResourceCache;
import com.nju.HttpClient.LocalCache.RedirectResourceCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestHandler implements Handler {
    LastModifiedResourceCache lastModifiedResourceCache;
    RedirectResourceCache redirectResourceCache;
//    在每一次发送请求报文之前都需要规范化一下
    //TODO: 重构一下请求报文
    public HttpRequest handle(HttpRequest requestMessage){
        return null;
    }

    //TODO: 查看当前请求的资源有没有被永久重定向 如果有永久重定向 原来的请求报文会被重构
    //这个方法只会处理永久重定向的,对于暂时重定向的不会重构请求报文
    public HttpRequest findRedirectPermanently(HttpRequest requestMessage){
        return null;
    }
    //TODO: 尝试给当前的报文中加上Last-Modified(如果lastModifiedResourceCache缓存中有的话就添,如果没有就不添),用于触发304
    public HttpRequest findLastModified(HttpRequest requestMessage){
        return null;
    }


    //TODO: 提取请求行
    public RequestLine parseRequestLine(InputStream inputStream){
        return null;
    }
    //TODO: 提取请求头
    @Override
    public MessageHeader parseMessageHeader(InputStream inputStream) {
        return null;
    }
    //TODO: 提取数据部分
    @Override
    public MessageEntityBody parseMessageEntityBody(InputStream inputStream) {
        return null;
    }
}
