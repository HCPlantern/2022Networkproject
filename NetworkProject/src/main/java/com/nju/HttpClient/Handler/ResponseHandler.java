package com.nju.HttpClient.Handler;


import com.nju.HttpClient.Client.Client;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Response.HttpResponse;
import com.nju.HttpClient.Components.Response.ResponseLine;
import com.nju.HttpClient.LocalCache.LastModifiedResourceCache;
import com.nju.HttpClient.LocalCache.RedirectResourceCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHandler implements Handler {
    LastModifiedResourceCache lastModifiedResourceCache;
    RedirectResourceCache redirectResourceCache;
    Client client;

    //TODO: 客户端处理响应报文的具体逻辑
    public HttpResponse handle(HttpRequest httpRequest,InputStream inputStream){
        return null;
    }

    //TODO: 对于响应码是200的处理(只需要将资源存到cache里就可以)
    public void handle200(HttpRequest httpRequest,HttpResponse httpResponse){
    }
    //TODO: 对于301的处理(永久重定向，需要更新redirectCache并且重构请求报文，重新发送报文)
    public HttpResponse handle301(HttpRequest oldRequest,HttpResponse httpResponse){
        return null;
    }
    //TODO: 对于302的处理(暂时重定向，不需要更新redirectCache，需要重构请求报文，重新发送报文)
    public HttpResponse handle302(HttpRequest oldRequest,HttpResponse httpResponse){
        return null;
    }
    //TODO: 对于304的处理(从lastModifiedResourceCache中获取数据部分就可以)
    public void handle304(HttpResponse httpResponse){
    }

    //TODO: 从服务端发过来的响应报文的字节流中提取响应行
    public ResponseLine parseResponseLine(InputStream inputStream){
        return null;
    }
    //TODO: 提取响应头部
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
