package com.nju.HttpClient.Components.Response;


import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

// http响应报文
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponse {
    private ResponseLine responseLine;
    private MessageHeader responseHeader;
    private MessageEntityBody responseEntityBody;

    public String toString(){
        StringBuilder responseMessage=new StringBuilder();
        responseMessage.append(responseLine.toString()).append(responseHeader.toString()).append("\r\n").append(responseEntityBody.toString(responseHeader.getFieldValue(HeaderFields.Content_Type)));
        return responseMessage.toString();
    }

    public byte[] toBytes(){
        return null;
    }

    public void saveBody(String path)throws IOException{
        responseEntityBody.save(path);
    }



}
