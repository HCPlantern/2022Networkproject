package com.nju.HttpClient.LocalCache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.HashMap;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedirectResourceCache {
//    hashmap含义:主机+路径+新的URI
    private HashMap<URI, URI> redirectResourceCache;
    public URI getnewURI(URI uri){
        return redirectResourceCache.get(uri);
    }
}
