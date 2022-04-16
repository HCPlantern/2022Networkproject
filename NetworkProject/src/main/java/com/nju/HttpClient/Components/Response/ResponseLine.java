package com.nju.HttpClient.Components.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//响应行
//格式：版本[S]状态码[S]短语[回车换行]
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLine {
    private String version;
    private String statusCode;
    private String reasonPhrase;

    public String toString(){
        return version+" "+statusCode+" "+reasonPhrase+"\r\n";
    }
    public byte[] toBytes(){
        return null;
    }
}
