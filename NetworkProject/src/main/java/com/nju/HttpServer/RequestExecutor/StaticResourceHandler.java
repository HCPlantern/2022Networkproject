package com.nju.HttpServer.RequestExecutor;

import com.nju.HttpServer.Common.Template;
import com.nju.HttpServer.Http.Components.Body;
import com.nju.HttpServer.Http.Components.Headers;
import com.nju.HttpServer.Http.Components.StatusLine;
import com.nju.HttpServer.Http.HttpRequest;
import com.nju.HttpServer.Http.HttpResponse;
import com.nju.HttpServer.Common.StatusCode;
import com.nju.HttpServer.SimpleServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class StaticResourceHandler extends BasicExecutor {
    private static Logger logger = LogManager.getLogger(StaticResourceHandler.class);
    public static HashMap<String, String> MovedPermanentlyResource = new HashMap<>();
    public static HashMap<String, String> MovedTemporarilyResource = new HashMap<>();
    //todo:304状态码
    public static HashMap<String, String> ModifiedTime = new HashMap<>();

    public StaticResourceHandler() {
        MovedPermanentlyResource.put("/movedPic.png", "/pic.png");
        MovedPermanentlyResource.put("/movedIndex.html", "/index.html");
        MovedTemporarilyResource.put("/movedPic2.png", "/pic.png");
        MovedTemporarilyResource.put("/movedIndex2.html", "/index.html");
    }

    public static boolean isStaticTarget(String target) {
        target = target.substring(target.lastIndexOf("/") + 1);
        return target.contains(".");
    }

    public HttpResponse handle(HttpRequest request) throws Exception {
        StatusLine statusLine = null;
        Headers headers = new Headers();
        Body body = new Body();
        String target = request.getStartLine().getTarget();

        String host = headers.getValue("Host");

        if (MovedPermanentlyResource.containsKey(target)) {
            return Template.generateStatusCode_301(MovedPermanentlyResource.get(target));
        } else if (MovedTemporarilyResource.containsKey(target)) {
            return Template.generateStatusCode_302(MovedTemporarilyResource.get(target));
        } else {
            statusLine = new StatusLine(1.1, 200, "OK");
        }

        if (target.endsWith(".html")) {
            headers.addHeader("Content-Type", "text/html");
        } else if (target.endsWith(".png")) {
            headers.addHeader("Content-Type", "image/png");
        } else if (target.endsWith(".jpg")) {
            headers.addHeader("Content-Type", "image/jpeg");
        } else if (target.endsWith(".js")) {
            headers.addHeader("Content-Type", "text/javascript");
        }
        //重定向静态资源路径到public文件夹
        String path = "src/main/public/" + target.substring(target.lastIndexOf("/") + 1);

        // add length
        File f = new File(path);
        headers.addHeader("Content-Length", Long.toString(f.length()));

        // add last modified
        Date fileLastModifiedTime = new Date(f.lastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//        System.out.println(sdf.format(fileLastModifiedTime));
        headers.addHeader("Last-Modified", sdf.format(fileLastModifiedTime));

        String time = request.getHeaders().getValue("If-Modified-Since");
        if (time != null) {
            Date Limit = sdf.parse(time);
            if (Limit.compareTo(fileLastModifiedTime) > 0) {
                return Template.generateStatusCode_304();
            }
        }

        byte[] bytesArray = new byte[(int) f.length()];
        try {
            FileInputStream fis = new FileInputStream(f);
            fis.read(bytesArray);
            //read file into bytes[]
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpResponse(new StatusLine(1.1, StatusCode.NOT_FOUND.getCode(), "Not Found"), new Headers(), new Body());
        }


        body.setData(bytesArray);

        return new HttpResponse(statusLine, headers, body);
    }
}
