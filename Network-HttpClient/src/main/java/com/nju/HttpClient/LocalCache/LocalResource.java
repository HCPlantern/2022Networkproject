package com.nju.HttpClient.LocalCache;

import com.nju.HttpClient.Components.Common.MessageEntityBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 这个类表示存储在本地缓存中的资源
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalResource{
//    表示获取时的时间
//    用于判断是不是304
    private Long timeStamp;
//    数据部分
    private MessageEntityBody messageEntityBody;
//    数据类型
    private String contentType;
}
