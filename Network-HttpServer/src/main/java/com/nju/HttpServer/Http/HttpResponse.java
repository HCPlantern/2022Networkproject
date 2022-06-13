package com.nju.HttpServer.Http;

import com.nju.HttpServer.Http.Components.*;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * http响应类，负责封装http响应
 **/
@Data
@AllArgsConstructor
public class HttpResponse implements Component {
    StatusLine statusLine;
    Headers headers;
    Body body;

    @Override
    public String ToString() {
        return statusLine.ToString() + headers.ToString() + '\n' + body.ToString();
    }

    @Override
    public byte[] ToBytes() {
        byte[] statusLine_b = statusLine.ToBytes();
        byte[] headers_b = headers.ToBytes();
        byte[] body_b = body.ToBytes();
        byte[] ret = new byte[statusLine_b.length + headers_b.length + body_b.length];
        System.arraycopy(statusLine_b, 0, ret, 0, statusLine_b.length);
        System.arraycopy(headers_b, 0, ret, statusLine_b.length, headers_b.length);
        System.arraycopy(body_b, 0, ret, statusLine_b.length + headers_b.length, body_b.length);
        return ret;
    }
}
