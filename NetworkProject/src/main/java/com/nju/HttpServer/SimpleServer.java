package com.nju.HttpServer;

import com.nju.HttpServer.Handler.ServerHandler;
import com.nju.HttpServer.RequestExecutor.BasicExecutor;
import com.nju.HttpServer.RequestExecutor.ErrorExecutor;
import com.nju.HttpServer.RequestExecutor.LoginExecutor;
import com.nju.HttpServer.RequestExecutor.RegisterExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Timer;

public class SimpleServer {
    private static Logger logger = LogManager.getLogger(SimpleServer.class);
    public static ArrayList<BasicExecutor> Executors = new ArrayList();
    public static Timer timer = new Timer();

    public SimpleServer() {
    }

    public static void main(String[] args) {
        SimpleServer server = new SimpleServer();
        server.go();
    }

    private void go() {
        Executors.add(new LoginExecutor());
        Executors.add(new RegisterExecutor());
        Executors.add(new ErrorExecutor());
        Thread serverThread = new Thread(new ServerHandler(5000));
        serverThread.start();
    }
}