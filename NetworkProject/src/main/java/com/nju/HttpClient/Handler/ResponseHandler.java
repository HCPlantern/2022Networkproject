package com.nju.HttpClient.Handler;


import com.nju.HttpClient.Client.Client;
import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Request.RequestLine;
import com.nju.HttpClient.Components.Response.HttpResponse;
import com.nju.HttpClient.Components.Response.ResponseLine;
import com.nju.HttpClient.Components.Response.StatusCode;
import com.nju.HttpClient.LocalCache.LastModifiedResourceCache;
import com.nju.HttpClient.LocalCache.LocalResource;
import com.nju.HttpClient.LocalCache.RedirectResourceCache;
import com.nju.HttpClient.Utils.InputStreamReaderHelper;
import com.nju.HttpClient.Utils.TimeTransformer;
import com.nju.HttpClient.Utils.UriHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHandler implements Handler {
    LastModifiedResourceCache lastModifiedResourceCache;
    RedirectResourceCache redirectResourceCache;
    Client client;

    //TODO: 客户端处理响应报文的具体逻辑
    public HttpResponse handle(HttpRequest httpRequest, InputStream inputStream) {
        ResponseLine responseLine = parseResponseLine(inputStream);
        MessageHeader messageHeader = parseMessageHeader(inputStream);
        MessageEntityBody messageEntityBody = parseMessageEntityBody(inputStream, messageHeader);
        HttpResponse httpResponse = new HttpResponse(responseLine, messageHeader, messageEntityBody);
        int statusCode = responseLine.getStatusCode();
        if (statusCode == StatusCode.OK) {
            handle200(httpRequest, httpResponse);
            return httpResponse;
        } else if (statusCode == StatusCode.Moved_Permanently) {
            return handle301(httpRequest, httpResponse);
        } else if (statusCode == StatusCode.Found) {
            return handle302(httpRequest, httpResponse);
        } else if (statusCode == StatusCode.Not_Modified) {
            return handle304(httpRequest, httpResponse);
        } else {
            throw new IllegalArgumentException("Unsupported Statues Code.");
        }
    }

    //TODO: 对于响应码是200的处理(只需要将资源存到cache里就可以)
    public void handle200(HttpRequest httpRequest, HttpResponse httpResponse) {
        MessageHeader responseHeader = httpResponse.getResponseHeader();
        Long timeStamp;
        try {
            String lastModified = responseHeader.getFieldValue(HeaderFields.Last_Modified);
            if (lastModified == null) return;
            timeStamp = TimeTransformer.getTimestamp(lastModified);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        LocalResource resource = new LocalResource(timeStamp, httpResponse.getResponseEntityBody(), responseHeader.getFieldValue(HeaderFields.Content_Type));
        URI uri = getRequestUri(httpRequest);
        lastModifiedResourceCache.addModifiedLocalResource(uri, resource);
    }

    //TODO: 对于301的处理(永久重定向，需要更新redirectCache并且重构请求报文，重新发送报文)
    public HttpResponse handle301(HttpRequest httpRequest, HttpResponse httpResponse) {
        String newPath = httpResponse.getResponseHeader().getFieldValue(HeaderFields.Location);
        assert (newPath != null);
        // get new URI
        URI oldUri = getRequestUri(httpRequest);
        URI newUri = UriHelper.createUri(oldUri.getScheme(), oldUri.getHost(), oldUri.getPort(), newPath);
        redirectResourceCache.setNewUri(oldUri, newUri);
        // create new http request
        RequestLine oldRequestLine = httpRequest.getRequestLine();
        RequestLine newRequestLine = new RequestLine(oldRequestLine.getMethod(), newPath, oldRequestLine.getVersion());
        HttpRequest newHttpRequest = new HttpRequest(newRequestLine, httpRequest.getRequestHeader(), httpRequest.getRequestEntityBody());
        // resent the new request
        return client.sendRequest(newHttpRequest);
    }

    //TODO: 对于302的处理(暂时重定向，不需要更新redirectCache，需要重构请求报文，重新发送报文)
    public HttpResponse handle302(HttpRequest httpRequest, HttpResponse httpResponse) {
        String newPath = httpResponse.getResponseHeader().getFieldValue(HeaderFields.Location);
        assert (newPath != null);
        MessageHeader header = httpRequest.getRequestHeader();
        header.putField(HeaderFields.Host, newPath);
        // resent the new request.
        return client.sendRequest(httpRequest);
    }

    // TODO: 对于304的处理(从lastModifiedResourceCache中获取数据部分就可以)
    // In this case, httpResponse should be null. Check cache and set response body, then return.
    public HttpResponse handle304(HttpRequest httpRequest, HttpResponse httpResponse) {
        URI uri = getRequestUri(httpRequest);
        MessageEntityBody body = lastModifiedResourceCache.getModifiedLocalResource(uri).getMessageEntityBody();
        httpResponse.setResponseEntityBody(body);
        return httpResponse;
    }

    // get request URI from httpRequest: path in request line and host in request header.
    private URI getRequestUri(HttpRequest httpRequest) {
        String path = httpRequest.getRequestLine().getRequestURL();
        String host = httpRequest.getRequestHeader().getFieldValue(HeaderFields.Host);
        return UriHelper.createUri("http", host, path);
    }

    public ResponseLine parseResponseLine(InputStream inputStream) {
        String line;
        try {
            line = InputStreamReaderHelper.readLine(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert line != null;
        int firstBlank = line.indexOf(" ");
        String firstStr = line.substring(0, firstBlank).trim();
        int secondBlank = line.indexOf(" ", firstBlank + 1);
        String secondStr = line.substring(firstBlank + 1, secondBlank).trim();
        String thirdStr = line.substring(secondBlank + 1).trim();

        return new ResponseLine(firstStr, Integer.parseInt(secondStr), thirdStr);
    }

    @Override
    public MessageHeader parseMessageHeader(InputStream inputStream) {
        MessageHeader header = new MessageHeader();
        String line;
        String[] splitLine;
        while (true) {
            try {
                line = InputStreamReaderHelper.readLine(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (line == null) {
                // means header doesn't exists
                throw new IllegalArgumentException("Missing message header.");
            } else if (line.length() == 0) {
                // means header ends
                return header;
            }

            int splitIndex = line.indexOf(":");
            if (splitIndex == -1) throw new IllegalArgumentException("Illegal message header format.");
            header.putField(line.substring(0, splitIndex).trim(), line.substring(splitIndex + 1).trim());
        }
    }

    @Override
    public MessageEntityBody parseMessageEntityBody(InputStream inputStream, MessageHeader header) {
        MessageEntityBody body;
        String contentLenStr = header.getFieldValue(HeaderFields.Content_Length);
        try {
            if (contentLenStr != null) {
                body = new MessageEntityBody(inputStream.readNBytes(Integer.parseInt(contentLenStr)));
            } else {
                // TODO : gzip 格式
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return body;
    }

}
