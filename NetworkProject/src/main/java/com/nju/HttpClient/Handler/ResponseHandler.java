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
        MessageEntityBody messageEntityBody = parseMessageEntityBody(inputStream);
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
            timeStamp = TimeTransformer.getTimestamp(responseHeader.getFieldValue(HeaderFields.Last_Modified));
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
        URI newUri;
        try {
            newUri = new URI(oldUri.getScheme(), oldUri.getHost(), newPath, oldUri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
        RequestLine oldRequestLine = httpRequest.getRequestLine();
        RequestLine newRequestLine = new RequestLine(oldRequestLine.getMethod(), newPath, oldRequestLine.getVersion());
        HttpRequest newHttpRequest = new HttpRequest(newRequestLine, httpRequest.getRequestHeader(), httpRequest.getRequestEntityBody());
        // resent the new request.
        return client.sendRequest(newHttpRequest);
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
        URI uri;
        try {
            uri = new URI("http", host, path, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

    public ResponseLine parseResponseLine(InputStream inputStream) {
        String line;
        try {
            line = InputStreamReaderHelper.readLine(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] splitLine = line.split("\\s+");
        if (splitLine.length != 3) {
            throw new IllegalArgumentException("Illegal response line format.");
        }
        return new ResponseLine(splitLine[0], Integer.parseInt(splitLine[1]), splitLine[2]);
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

            splitLine = line.split(":\\s*");
            if (splitLine.length != 2) throw new IllegalArgumentException("Illegal message header format.");
            header.putField(splitLine[0], splitLine[1]);
        }
    }

    @Override
    public MessageEntityBody parseMessageEntityBody(InputStream inputStream) {
        MessageEntityBody body;
        try {
            body = new MessageEntityBody(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return body;
    }


}
