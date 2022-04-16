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

public class RequestHandler implements CompletionHandler<Integer //声明read操作返回的类型（读数据个数）
        , ByteBuffer> {//调用读操作传入的类型，针对read函数第一个buffer
    //用户读取信息或者发送信息的channel
    private static Logger logger = LogManager.getLogger(RequestHandler.class);
    private AsynchronousSocketChannel channel;
    boolean isTimeout = false;
    public static TimerTask timerTask = null;

    public RequestHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();//内核已经帮我们把数据写到buffer，现在切换到读模式，将数据从buffer中读出来
        byte[] byteMsg = new byte[buffer.remaining()]; //remaining()返回剩余的可用长度,此长度为实际读取的数据长度
        buffer.get(byteMsg);//buffer中内容转移到msg

        try {
            //以UTF-8解码channel读出的字节
            String strMsg = new String(byteMsg, "UTF-8");
            logger.info("服务器收到消息：\n" + strMsg);

            try {
                if (isTimeout) {
                    channel.close();
                    return;
                }
                // 把channel里转化出的字符串包装成Request
                HttpRequest request = Util.String2Request(strMsg);

                //Todo:keep-alive的处理
                if (request.getHeaders().getValue("Keep-Alive") != null) {
                    logger.debug("Keep-Alive");
                    String timeout = request.getHeaders().getValue("Keep-Alive");
                    if (timerTask != null) {
                        timerTask.cancel();
                    }
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            isTimeout = true;
                        }
                    };
                    SimpleServer.timer.schedule(timerTask, Integer.parseInt(timeout.substring(8)) * 1000L);
                }

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
                // 404: 没有对应的url 405: 有对应的url但是没有对应的method
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

                doWrite(response.ToBytes());
                //timer 如果再次收到请求，重置timer，否则就关闭

//            outToClient.close();
            } catch (Exception e) {
                HttpResponse response = Template.generateStatusCode_500();
                try {
                    doWrite(response.ToBytes());
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void doWrite(byte[] bytes) {
        //异步写数据
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        channel.write(buffer, //从哪里拿数据
                buffer, //回调函数传入的参数
                //回调函数
                new CompletionHandler<Integer, //写完成后返回的参数类型
                        ByteBuffer>() { //往回调函数传入的参数类型
                    // 因为第二个参数传入的是ByteBuffer类型
                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        //如果没有发送完则继续发送。
                        if (buffer.hasRemaining()) {
                            channel.write(buffer, buffer, this);
                        } else {
                            //异步读，
                            ByteBuffer allocate = ByteBuffer.allocate(1024);
                            buffer.clear();
                            //也可以直接传入attachment
                            channel.read(allocate, allocate, new RequestHandler(channel));
                        }
                    }

                    @Override
                    public void failed(Throwable e, ByteBuffer attachment) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        logger.error("读写失败");
        exc.printStackTrace();
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}