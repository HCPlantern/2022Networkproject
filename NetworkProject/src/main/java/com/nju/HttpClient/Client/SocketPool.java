package com.nju.HttpClient.Client;

import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Request.HttpRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
// 使用连接池来管理socket
// 如果是短连接,用完直接关闭
// 如果是长连接,用完不会关闭,存放到socketpool中
// 每一次连接的时候,需要首先去socketpool查一下有没有可用的socket
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketPool {
    private HashMap<String, Socket> clientsocketHashMap;

    public Socket createClientSocket(HttpRequest requestMessage) throws URISyntaxException, IOException {
        String host = requestMessage.getRequestHeader().getFieldValue(HeaderFields.Host);
//        首先从clientsocketHashMap找一找有没有对应的socket
//        1)如果没有找到,创建一个
//        2)如果找到了，要进一步判断这个socket可不可用(其实就是判断是不是长连接)
//           a) 如果这个socket已经关了 说明之前和这个主机的所有连接都是短连接，用完就关闭了
//           b) 如果这个socket没有关 说明是长连接 继续复用这个socket
        Socket clientSocket = clientsocketHashMap.get(host);
        if (clientSocket != null) {
            if (clientSocket.isClosed()) {
                clientsocketHashMap.remove(host);
            } else {
                return clientSocket;
            }
        }
        String path = requestMessage.getRequestLine().getRequestURL();
        URI uri = new URI("http", host, path, null);
        int port = uri.getPort() == -1 ? 80 : uri.getPort();
        clientSocket = new Socket(host, port);
        String connection = requestMessage.getRequestHeader().getFieldValue(HeaderFields.Connection);
        boolean isKeepAlive = connection != null && connection.equals("keep-alive");
        clientSocket.setKeepAlive(isKeepAlive);

        clientsocketHashMap.put(host, clientSocket);
        return clientSocket;
    }
    public void removeSocket(String host) throws IOException {
        Socket clientSocket = clientsocketHashMap.get(host);
        if (clientSocket != null) {
            clientSocket.close();
        }
        clientsocketHashMap.remove(host);
    }
}
