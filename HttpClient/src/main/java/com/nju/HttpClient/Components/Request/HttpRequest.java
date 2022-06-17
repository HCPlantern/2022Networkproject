package com.nju.HttpClient.Components.Request;


import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

// http请求报文
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequest {
    private RequestLine requestLine;
    private MessageHeader requestHeader;
    private MessageEntityBody requestEntityBody;

    public HttpRequest (InputStream inputStream) throws IOException {
        this.requestLine = new RequestLine(inputStream);
        this.requestHeader = new MessageHeader(inputStream);
        this.requestEntityBody = new MessageEntityBody(inputStream);
    }
    public String toString() {
        StringBuilder requestMessage = new StringBuilder();
        requestMessage.append(requestLine.toString()).append(requestHeader.toString()).append("\r\n").append(requestEntityBody.toString(requestHeader.getFieldValue(HeaderFields.Content_Type)));
        return requestMessage.toString();
    }

    public byte[] toBytes() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }
}

