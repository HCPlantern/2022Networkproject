package com.nju.HttpClient.Components.Common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageHeader {
    //    用一个HashMap存储报文头部

    private HashMap<String, String> headers=new LinkedHashMap<>();

    public void putField(String fieldName,String fieldValue) {
        headers.put(fieldName,fieldValue);
    }
    //    得到对应字段的值
    public String getFieldValue(String fieldName){
        return headers.get(fieldName);
    }
    public String toString(){
        StringBuilder messageHeader=new StringBuilder();
        Set<String> headerFields=headers.keySet();
        for(String headerField:headerFields){
            messageHeader.append(headerField).append(": ").append(headers.get(headerField)).append("\r\n");
        }
        return messageHeader.toString();
    }
}
