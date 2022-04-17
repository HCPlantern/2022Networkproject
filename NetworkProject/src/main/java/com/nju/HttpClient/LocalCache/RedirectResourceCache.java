package com.nju.HttpClient.LocalCache;

import lombok.AllArgsConstructor;

import java.net.URI;
import java.util.HashMap;

// 不该使用 @Data 以及 @NoArgsConstructor
@AllArgsConstructor
public class RedirectResourceCache {
//    hashmap含义:主机+路径+新的URI
    private HashMap<URI, URI> redirectResourceCache;
    public URI getNewUri(URI uri){
        return redirectResourceCache.get(uri);
    }
    public void setNewUri(URI oldUri, URI newUri) {
        redirectResourceCache.put(oldUri, newUri);
    }
}
