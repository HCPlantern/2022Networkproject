package com.nju.HttpClient.Components.Common;

import com.nju.HttpClient.Utils.InputStreamReaderHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageHeader {
    //    用一个HashMap存储报文头部

    private HashMap<String, String> headers=new LinkedHashMap<>();

    public MessageHeader(InputStream inputStream) throws IOException {
        String line;
        while (true) {
            line = InputStreamReaderHelper.readLine(inputStream);
            if (line == null || "".equals(line)) {
                break;
            }
            String[] keyValue = line.split(":");
            headers.put(keyValue[0].trim(), keyValue[1].trim());
        }

    }

    public void putField(String fieldName,String fieldValue) {
        headers.put(fieldName,fieldValue);
    }
    //    得到对应字段的值
    public String getFieldValue(String fieldName){
        return headers.get(fieldName);
    }
    public String toString() {
        StringBuilder messageHeader = new StringBuilder();
        Set<String> headerFields = headers.keySet();
        for (String headerField : headerFields) {
            messageHeader.append(headerField).append(": ").append(headers.get(headerField)).append("\r\n");
        }
        return messageHeader.toString();
    }
    public byte[] toBytes(){
        String header=toString();
        return header.getBytes();
    }
}
