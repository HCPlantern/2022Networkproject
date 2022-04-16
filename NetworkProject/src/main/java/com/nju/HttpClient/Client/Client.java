package com.nju.HttpClient.Client;


import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Response.HttpResponse;
import com.nju.HttpClient.Handler.RequestHandler;
import com.nju.HttpClient.Handler.ResponseHandler;
import com.nju.HttpClient.LocalCache.LastModifiedResourceCache;
import com.nju.HttpClient.LocalCache.RedirectResourceCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Client {
//    客户端的成员变量
    Socket clientSocket;
    RequestHandler requestHandler;
    ResponseHandler responseHandler;
    LastModifiedResourceCache lastModifiedResourceCache;
    RedirectResourceCache redirectResourceCache;
    OutputStream clientOutputStream;
    InputStream clientInputStream;

//    客户端的构造函数
//    参数是需要连接的远程主机的地址和端口
    public Client(String hostname, Integer port) throws IOException {
        this.lastModifiedResourceCache=new LastModifiedResourceCache(new HashMap<>());
        this.redirectResourceCache=new RedirectResourceCache(new HashMap<>());
        this.clientSocket=new Socket(hostname,port);
        this.requestHandler=new RequestHandler(lastModifiedResourceCache,redirectResourceCache);
        this.responseHandler=new ResponseHandler(lastModifiedResourceCache,redirectResourceCache,this);
    }
//发出请求报文
    public HttpResponse sendRequest(HttpRequest requestMessage){
        HttpResponse httpResponse =null;
        try {
//            向服务端发送请求报文
            clientOutputStream=clientSocket.getOutputStream();
//            首先要重构一下
            requestMessage=requestHandler.handle(requestMessage);
            clientOutputStream.write(requestMessage.toBytes());
//            得到来自服务端的响应报文
            clientInputStream=clientSocket.getInputStream();
            httpResponse=responseHandler.handle(requestMessage,clientInputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        return httpResponse;
    }
}