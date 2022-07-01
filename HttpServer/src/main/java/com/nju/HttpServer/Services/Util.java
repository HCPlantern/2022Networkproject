package com.nju.HttpServer.Services;

import com.nju.HttpServer.Http.Components.Headers;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
    public Util() {
    }

    private static Logger logger = LogManager.getLogger(StaticResourceService.class);
    private static HashMap<String, String> MIME = new HashMap<>();

    static {
        MIME.put(".html", "Content-Type: text/html");
        MIME.put(".png", "Content-Type: image/png");
        MIME.put(".jpg", "Content-Type: text/jpeg");
        MIME.put(".js", "Content-Type: text/javascript");
        MIME.put(".css", "Content-Type: text/css");
        MIME.put(".ico", "Content-Type: image/x-icon");
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
