package com.nju.HttpClient;

import com.nju.HttpClient.Client.Client;
import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Request.Method;
import com.nju.HttpClient.Components.Request.RequestLine;
import com.nju.HttpClient.Components.Response.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

//TODO: 客户端程序入口
public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        try {
            // Directly input header from system in
            HttpRequest httpRequest = new HttpRequest(System.in);
            HttpResponse httpResponse = client.sendRequest(httpRequest);
            System.out.println(httpResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
