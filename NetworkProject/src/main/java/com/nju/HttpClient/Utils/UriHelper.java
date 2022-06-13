package com.nju.HttpClient.Utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 将 host 中的 port 分离以便于创建 URI
 * 若无 port 则自动添加 80
 */
public class UriHelper {
    public static URI createUri(String scheme, String host, String path) {
        // 检查是否有端口号
        try {
            if (host.contains(":")) {
                String[] temp = host.split(":");
                return new URI(scheme, null, temp[0], Integer.parseInt(temp[1]), path, null, null);
            } else {
                return new URI(scheme, null, host, 80, path, null, null);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static URI createUri(String scheme, String host, Integer port, String path) {
        try {
            return new URI(scheme, null, host, port, path, null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHttpHost(URI uri) {
        return uri.getHost().concat(":").concat(String.valueOf(uri.getPort()));
    }
}
