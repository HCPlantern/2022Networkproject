package com.nju.HttpServer.Services;

import com.nju.HttpServer.Http.Components.Headers;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
    public Util() {
    }

    private static Logger logger = LogManager.getLogger(StaticResourceService.class);

    /**
     * 文件类型 -> MIME类型
     */
    private static HashMap<String, String> MIME = new HashMap<>();

    /**
     * 永久移动的资源 对应状态码301
     * 301 Moved Permanently 永久移动。是指请求的资源已被永久的移动到新的URL，返回信息会包括新的URL，浏览器还会自动定向到新的URL。今后任何新的请求都应该使用新的URL来代替
     */
    public static HashMap<String, String> MovedPermanentlyResource = new HashMap<>();

    /**
     * 暂时移动的资源 对应状态码302
     * 302 Found 临时移动。与301类似。但是资源只是临时被移动。客户端应该继续使用原有的URI
     */
    public static HashMap<String, String> MovedTemporarilyResource = new HashMap<>();

    static {
        MIME.put(".html", "Content-Type: text/html");
        MIME.put(".png", "Content-Type: image/png");
        MIME.put(".jpg", "Content-Type: image/jpeg");
        MIME.put(".js", "Content-Type: text/javascript");
        MIME.put(".css", "Content-Type: text/css");
        MIME.put(".ico", "Content-Type: image/x-icon");
    }

    static {
        MovedPermanentlyResource.put("/movedPic.png", "/pic.png");
        MovedPermanentlyResource.put("/movedIndex.html", "/index.html");
        MovedTemporarilyResource.put("/movedPic2.png", "/pic.png");
        MovedTemporarilyResource.put("/movedPic2.jpg", "/pic.jpg");
        MovedTemporarilyResource.put("/movedIndex2.html", "/index.html");
    }

    //Todo:用HashMap等方式替代MIME类型分支
    public static void targetToMIME(String target, Headers headers) {
        String fileType = target.substring(target.lastIndexOf('.'));
        String mime = MIME.get(fileType);
        if (mime == null) {
            logger.error("不支持的MIME类型: " + fileType);
            return;
        }
        headers.addHeader(mime);
    }
}
