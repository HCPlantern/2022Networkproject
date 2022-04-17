package com.nju.HttpServer.Handler;

import com.nju.HttpServer.Common.Template;
import com.nju.HttpServer.Http.HttpRequest;
import com.nju.HttpServer.Http.HttpResponse;
import com.nju.HttpServer.Http.Util;
import com.nju.HttpServer.RequestExecutor.BasicExecutor;
import com.nju.HttpServer.RequestExecutor.StaticResourceHandler;
import com.nju.HttpServer.SimpleServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.TimerTask;

/****
 * Integer:声明read操作返回的类型（读数据个数），为负数则读取失败
 * ByteBuffer:调用读操作传入的类型，针对read函数第一个buffer
 * channel:用户读取信息或者发送信息的channel
 */
public class RequestHandler implements CompletionHandler<Integer, ByteBuffer> {
    private static Logger logger = LogManager.getLogger(RequestHandler.class);
    private AsynchronousSocketChannel channel;
    boolean isTimeout = false;
    public static TimerTask timerTask = null;
    private long timeout = 15L; //响应给客户端的keep-alive的时间，单位:秒

    public RequestHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    /**
     * 系统从channel中成功读取完数据后，会调用completed()
     *
     * @param result:读完的数据长度，如果非正，代表未读出数据
     * @param buffer:读完的数据存放的地方
     **/
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if (result <= 0) {
            logger.warn("未读出数据");
            return;
        }
        buffer.flip();//内核已经帮我们把数据写到buffer，现在切换到读模式，将数据从buffer中读出来
        @SuppressWarnings("开的字节数组byteMsg的大小有待商榷")
        byte[] byteMsg = new byte[buffer.remaining()]; //remaining()返回剩余的可用长度,此长度为实际读取的数据长度
        buffer.get(byteMsg);//buffer中内容转移到字节数组byteMsg

        try {
            //以UTF-8解码channel读出的字节数组byteMsg。
            String strMsg = new String(byteMsg, "UTF-8");
            logger.info("服务器收到请求，完整打印:" + System.lineSeparator() + strMsg);
            //根据请求匹配Executor进行处理并生成Response，逻辑与tfgg基本一致
            try {
                HttpRequest request = Util.String2Request(strMsg);
                String target = request.getStartLine().getTarget();
                String method = request.getStartLine().getMethod();
                HttpResponse response = null;
                BasicExecutor executor = null;

                // 如果请求一个静态资源，调用StaticResourceHandler
                if (StaticResourceHandler.isStaticTarget(target) && method.equalsIgnoreCase("get")) {
                    executor = new StaticResourceHandler();
                } else {
                    // 否则，在持有的executor中找到合适的，用这个executor处理请求
                    for (BasicExecutor e : SimpleServer.Executors) {
                        if (target.endsWith(e.getUrl()) && method.equalsIgnoreCase(e.getMethod())) {
                            executor = e;
                            break;
                        }
                    }
                }
                // 找不到合适的executor
                // 404: 没有对应的url; 405: 有对应的url但是没有对应的method
                if (executor == null) {
                    response = Template.generateStatusCode_404();
                    //todo 针对post静态资源会出现bug，不一定是404
                    for (BasicExecutor e : SimpleServer.Executors) {
                        if (target.endsWith(e.getUrl())) {
                            response = Template.generateStatusCode_405();
                            break;
                        }
                    }
                } else {
                    response = executor.handle(request);
                }

                handleKeepAlive(request, response);
                //发送response
                writeBytesToChannel(response.ToBytes());

                //如果请求头有Connection: close，即客户端希望立即关闭连接，isTimeout在handleKeepAlive()方法中被设为true
                if (isTimeout) {
                    closeChannel("因客户端不想长连接，立即");
                }
            } catch (Exception e) {
                //意料之外的异常，发送500状态码
                HttpResponse response = Template.generateStatusCode_500();
                try {
                    writeBytesToChannel(response.ToBytes());
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向channel异步写数据，即把响应发给客户端
     *
     * @param bytes:待写的字节数组
     **/
    private void writeBytesToChannel(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        channel.write(buffer, //从哪里拿数据
                buffer, //回调函数传入的参数
                //回调函数
                new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        //如果没有发送完则继续发送。
                        if (buffer.hasRemaining()) {
                            channel.write(buffer, buffer, this);
                        } else {
                            //否则写完了，继续异步读
                            if (channel.isOpen() && !isTimeout) {
                                ByteBuffer allocate = ByteBuffer.allocate(1024);
                                buffer.clear();
                                channel.read(allocate, allocate, new RequestHandler(channel));
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable e, ByteBuffer attachment) {
                        logger.warn("写入响应失败");
                        e.printStackTrace();
                    }
                });
    }

    /**
     * keep-alive的处理,设定channel定时关闭,并返回期望的长连接时长
     *
     * @param request:接收到的已封装好的http请求
     * @param response:可能需要添加Keep-Alive时长的响应
     **/
    private void handleKeepAlive(HttpRequest request, HttpResponse response) {
        if (request.getHeaders().getValue("Connection") != null &&
                request.getHeaders().getValue("Connection").equals("keep-alive")) {
            logger.debug("Connection" + request.getHeaders().getValue("Connection"));
            if (timerTask != null) {
                timerTask.cancel();
            }
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    isTimeout = true;
                    closeChannel("因长连接到期");
                }
            };
            SimpleServer.timer.schedule(timerTask, timeout * 1000L);
            response.getHeaders().addHeader("Keep-Alive", "timeout=" + timeout);
        } else if (request.getHeaders().getValue("Connection") != null &&
                request.getHeaders().getValue("Connection").equals("close")) {
            logger.debug("Connection" + request.getHeaders().getValue("Connection"));
            isTimeout = true;
        }
    }

    /**
     * 调用此方法关闭channel!
     *
     * @param logMsg:关闭channel时的日志
     **/
    private void closeChannel(String logMsg) {
        if (channel.isOpen()) {
            try {
                logger.debug(logMsg + "关闭channel输入输出流");
                channel.shutdownInput();
                channel.shutdownOutput();
                logger.debug(logMsg + "关闭channel");
                channel.close();
            } catch (IOException e) {
                logger.warn(logMsg + "关闭channel时异常");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        if (!channel.isOpen()) {
            return;
        }
        logger.warn("读取请求失败");
        exc.printStackTrace();
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}