package com.nju.HttpServer.Http;

import com.nju.HttpServer.Http.Components.*;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * http请求类，负责封装http请求
 **/
@Data
@AllArgsConstructor
public class HttpRequest implements Component {
    StartLine startLine;
    Headers headers;
    Body body;

    @Override
    public String ToString() {
        return startLine.ToString() + headers.ToString() + body.ToString();
    }

    @Override
    public byte[] ToBytes() {
        byte[] startLine_b = startLine.ToBytes();
        byte[] headers_b = headers.ToBytes();
        byte[] body_b = body.ToBytes();
        byte[] ret = new byte[startLine_b.length + headers_b.length + body_b.length];
        System.arraycopy(startLine_b, 0, ret, 0, startLine_b.length);
        System.arraycopy(headers_b, 0, ret, startLine_b.length, headers_b.length);
        System.arraycopy(body_b, 0, ret, startLine_b.length + headers_b.length, body_b.length);
        return ret;
    }

}
