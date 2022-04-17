package com.nju.HttpServer.Handler;

import com.nju.HttpServer.SimpleServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * AsynchronousSocketChannel:与客户端建立的连接
 * ServerHandler：为了继续异步与客户端建立连接，而传入的ServerHandler(八股，可以不用管)
 **/
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, ServerHandler> {
    private static Logger logger = LogManager.getLogger(AcceptHandler.class);

    /**
     * 系统与客户端成功建立连接后，会自动调用completed()
     *
     * @param channel:与客户端成功建立的连接,系统给你的
     * @param serverHandler:为了继续异步与客户端建立连接，而传入的ServerHandler(八股，可以不用管)
     **/
    @Override
    public void completed(AsynchronousSocketChannel channel, ServerHandler serverHandler) {
        try {
            logger.info("建立连接:" + channel.getRemoteAddress().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*分配一个1024字节的ByteBuffer供系统将收到的数据写入
         *这个大小应该是接受的request的最大长度，如果请求太大，可能会出错
         */
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //异步读，第三个参数为接收消息回调的业务handle
        channel.read(buffer,//读事件需要将数据写入这个buffer
                buffer,//给回调函数传入的参数，
                new RequestHandler(channel));

        /*继续接收其它客户端的请求。八股。
         *第一个参数依然需要传serverHandler，如果传null时，多个客户端连接会抛异常，但是不影响连接
         * */
        serverHandler.channel.accept(serverHandler, this);
    }

    /**
     * 系统与客户端建立连接失败，会自动调用failed()
     **/
    @Override
    public void failed(Throwable exc, ServerHandler attachment) {
        logger.warn("建立连接失败！");
        exc.printStackTrace();
    }
}
