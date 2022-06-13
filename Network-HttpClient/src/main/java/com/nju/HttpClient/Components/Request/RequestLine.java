package com.nju.HttpClient.Components.Request;

import com.nju.HttpClient.Utils.InputStreamReaderHelper;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;

// 表示请求行
// 请求行格式：方法[S]URL[S]版本[换行符]
@Data
public class RequestLine {
    private String method;
    private String requestURL;
    private String version="HTTP/1.1";

    public RequestLine(String method, String requestURL) {
        this.method = method;
        this.requestURL = requestURL;
    }
    public RequestLine(String method, String requestURL, String version) {
        this.method = method;
        this.requestURL = requestURL;
        this.version = version;
    }

    public RequestLine(InputStream inputStream) throws IOException {
        String[] line = InputStreamReaderHelper.readLine(inputStream).split(" ");
        method = line[0];
        requestURL = line[1];
        if (line.length == 3 && !version.equals(line[2])) {
            version = line[2];
        }
    }

    public String toString(){
        return this.method+" "+this.requestURL+" "+this.version+"\r\n";
    }
    public byte[] toBytes(){
        return null;
    }
}

