package com.nju.HttpClient.LocalCache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedirectResourceCache {
//    hashmap含义:主机+路径+资源
    HashMap<String, HashMap<String,LocalResource>> redirectResourceCache;
}
