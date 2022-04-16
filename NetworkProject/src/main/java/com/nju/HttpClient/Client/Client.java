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
        this.responseHandler=new ResponseHandler(lastModifiedResourceCache,redirectResourceCache);
        this.clientInputStream=clientSocket.getInputStream();
        this.clientOutputStream=clientSocket.getOutputStream();
    }
//发出请求报文
    public HttpResponse sendRequest(HttpRequest requestMessage){
        HttpResponse httpResponse =null;
        try {




        }catch (Exception e){
            e.printStackTrace();
        }
        return httpResponse;
    }
}