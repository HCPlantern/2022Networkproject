package com.nju.HttpServer.Handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, ServerHandler> {
    @Override
    public void completed(AsynchronousSocketChannel channel, ServerHandler serverHandler) {
        try {
            System.out.println("建立连接:" + channel.getRemoteAddress().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //异步读，第三个参数为接收消息回调的业务handle
        channel.read(buffer,//读事件需要将数据写入这个buffer
                buffer,//给回调函数传入的参数，
                new RequestHandler(channel));
        //继续接收其它客户端的请求。
        //第一个参数依然需要传serverHandler，如果传null时，多个客户端连接会抛异常，但是不影响连接
        serverHandler.channel.accept(serverHandler, this);
    }

    @Override
    public void failed(Throwable exc, ServerHandler attachment) {
        exc.printStackTrace();
    }
}
