package com.nju.HttpClient.Components.Common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntityBody {
    private byte[] body=new byte[0];
    //    转成字符串的时候需要根据特定的类型转换
    public String toString(String contentType) {
//        如果没有body部分
        if(body.length==0){
//            如果什么都没有的话应该返回一个\r\n
//            这样符合报文格式
            return "\r\n";
        }
        if(contentType!=null){
//            只要看分割下来的前一个 后一个是编码类型
            String[] contentTypes=contentType.split(";");
//            所有可以转为string的数据类型
            if(contentTypes[0].equals("text/html") || contentTypes[0].equals("application/x-www-form-urlencoded") || contentTypes[0].equals("text/plain")||contentTypes[0].equals("text/css")||contentTypes[0].equals("text/javascript")|| contentTypes[0].equals("application/json") || contentTypes[0].equals("text/xml")){
                return new String(body);
            }else {
                return "The message entity body is not supported to show here!";
            }
        }else {
            return "The message entity body is not supported to show here!";
        }
    }
    public byte[] toBytes(){
        return body;
    }
}

