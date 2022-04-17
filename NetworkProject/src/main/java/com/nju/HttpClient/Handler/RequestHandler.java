package com.nju.HttpClient.Handler;

import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Request.RequestLine;
import com.nju.HttpClient.LocalCache.LastModifiedResourceCache;
import com.nju.HttpClient.LocalCache.LocalResource;
import com.nju.HttpClient.LocalCache.RedirectResourceCache;
import com.nju.HttpClient.Utils.TimeTransformer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestHandler implements Handler {
    LastModifiedResourceCache lastModifiedResourceCache;
    RedirectResourceCache redirectResourceCache;
//    在每一次发送请求报文之前都需要规范化一下
    //TODO: 重构一下请求报文
    public HttpRequest handle(HttpRequest requestMessage) throws URISyntaxException {
        HttpRequest reformatRequest=null;
        if(requestMessage==null){
//            这边之后可以自己写异常或者打日志
            return null;
        }
//        重构时比较tricky的点首先是content-length
//        根据搜索得到的信息在http1.1及之后版本。如果是keep alive，则content-length和chunk必然是二选一。若是非keep alive，则和http1.0一样。content-length可有可无
//        我感觉对于请求报文来说加不加长度没啥影响(
        boolean isKeepAlive=requestMessage.getRequestHeader().getFieldValue(HeaderFields.Connection).equals("keep-alive");
        if(isKeepAlive){
            boolean isChunked=requestMessage.getRequestHeader().getFieldValue(HeaderFields.Transfer_Encoding).equals("chunked");
            if(!isChunked){
                requestMessage.getRequestHeader().putField("content-length",requestMessage.getRequestEntityBody().toBytes().length+"");
            }
        }
//      这边的顺序有没有影响? 是先找重定向的还是先找last-modified?
//        应该是先找重定向的，如果先找last-modified 可能这个资源已经被重定向了，这个时候的last-modified没有意义
        requestMessage=findRedirectPermanently(requestMessage);
        requestMessage=findLastModified(requestMessage);
        return requestMessage;
    }

    //TODO: 查看当前请求的资源有没有被永久重定向 如果有永久重定向 原来的请求报文会被重构
    //这个方法只会处理永久重定向的,对于暂时重定向的不会重构请求报文
    public HttpRequest findRedirectPermanently(HttpRequest requestMessage) throws URISyntaxException {
//        提取旧的主机和路径
        String oldHost=requestMessage.getRequestHeader().getFieldValue(HeaderFields.Host);
        String oldPath=requestMessage.getRequestLine().getRequestURL();
        URI oldURI=new URI("http",oldHost,oldPath,null);
//        获得新的请求路径
        URI newURI=redirectResourceCache.getNewUri(oldURI);
        if(newURI!=null){
            String newHost=newURI.getHost();
            String newPath=newURI.getPath();
            requestMessage.getRequestLine().setRequestURL(newPath);
            requestMessage.getRequestHeader().putField(HeaderFields.Host,newHost);
        }
        return requestMessage;
    }
    //TODO: 尝试给当前的报文中加上Last-Modified(如果lastModifiedResourceCache缓存中有的话就添,如果没有就不添),用于触发304
    public HttpRequest findLastModified(HttpRequest requestMessage) throws URISyntaxException {
        String resourceHost=requestMessage.getRequestHeader().getFieldValue(HeaderFields.Host);
        String resourcePath=requestMessage.getRequestLine().getRequestURL();
        URI resourceURI=new URI("http",resourceHost,resourcePath,null);
        LocalResource localResource=lastModifiedResourceCache.getModifiedLocalResource(resourceURI);
        if(localResource!=null){
            Long timeStap= localResource.getTimeStamp();
            requestMessage.getRequestHeader().putField(HeaderFields.If_Modified_Since, TimeTransformer.toTimeString(timeStap));
        }
        return requestMessage;
    }
//    这些对于请求报文来说不要写
    public RequestLine parseRequestLine(InputStream inputStream){
        return null;
    }
    @Override
    public MessageHeader parseMessageHeader(InputStream inputStream) {
        return null;
    }
    @Override
    public MessageEntityBody parseMessageEntityBody(InputStream inputStream) {
        return null;
    }
}
