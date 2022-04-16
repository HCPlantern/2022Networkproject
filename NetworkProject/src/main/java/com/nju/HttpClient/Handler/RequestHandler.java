package com.nju.HttpClient.Handler;

import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.LocalCache.LastModifiedResourceCache;
import com.nju.HttpClient.LocalCache.RedirectResourceCache;

//这个类用来处理Request请求

//这边我想按照我自己的思路来写
//我的思路是
//一开始我不要去重构http请求
//重构请求是得到了比如301 302报文之后才进行重构的
//那么重构的逻辑应该是在responseHandler里面写
//然后responseHandler里面重构request的部分使用requestHandler的方法




public class RequestHandler {
    LastModifiedResourceCache lastModifiedResourceCache;
    RedirectResourceCache redirectResourceCache;
    public RequestHandler(LastModifiedResourceCache lastModifiedResourceCache, RedirectResourceCache redirectResourceCache) {
        this.lastModifiedResourceCache = lastModifiedResourceCache;
        this.redirectResourceCache = redirectResourceCache;
    }
//    重构一下原始的请求报文
//    这里为什么需要重构呢
//    因为大作业要求我们处理了301 302 304这三种状态码
//    其中301 是永久移动 也就是路径永久变更 我们收到了301状态码之后 之后所有的请求报文中的url都需要改(这个是通过更改redirect缓存实现的)
//    对于302 是暂时性移动 我们只需要根据响应报文得到新的请求url 重构一下请求报文就可以了

    public HttpRequest reformatRequest(HttpRequest requestMessage){
        return null;
    }

//    这个是用来处理301永久重定向的
//
    public HttpRequest findRedirect(HttpRequest requestMessage){
        return null;
    }

















}
