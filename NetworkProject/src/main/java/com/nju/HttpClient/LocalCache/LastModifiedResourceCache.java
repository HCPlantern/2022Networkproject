package com.nju.HttpClient.LocalCache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

// 这个类主要辅助请求报文在请求头里面添加If-Modified-Since
// 主要还是为了处理304的情况
// 如果是304 说明这个资源在本地 直接读取
//
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastModifiedResourceCache {
//    hashmap存的是:主机+路径+资源
    private HashMap<String, HashMap<String, LocalResource>>lastModifiedResourceCache;
}