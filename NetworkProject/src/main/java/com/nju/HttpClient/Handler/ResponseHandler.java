package com.nju.HttpClient.Handler;


import com.nju.HttpClient.LocalCache.LastModifiedResourceCache;
import com.nju.HttpClient.LocalCache.RedirectResourceCache;

public class ResponseHandler {
    LastModifiedResourceCache lastModifiedResourceCache;
    RedirectResourceCache redirectResourceCache;

    public ResponseHandler(LastModifiedResourceCache lastModifiedResourceCache, RedirectResourceCache redirectResourceCache) {
        this.lastModifiedResourceCache = lastModifiedResourceCache;
        this.redirectResourceCache = redirectResourceCache;
    }
}
