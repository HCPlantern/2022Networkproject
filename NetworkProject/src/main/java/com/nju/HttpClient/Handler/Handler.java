package com.nju.HttpClient.Handler;

import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;

import java.io.InputStream;

public interface Handler {
    public MessageHeader parseMessageHeader(InputStream inputStream);
    public MessageEntityBody parseMessageEntityBody(InputStream inputStream);
}
