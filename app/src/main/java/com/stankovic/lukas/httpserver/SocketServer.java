package com.stankovic.lukas.httpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;


import android.os.Handler;
import android.util.Log;

import com.stankovic.lukas.httpserver.Http.Request.RequestHandler;

public class SocketServer extends Thread {

    ServerSocket serverSocket;
    public final int port = 12345;
    boolean bRunning;

    private Handler loggingHandler;

    public SocketServer(Handler handler) {
        super();

        this.loggingHandler = handler;
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.d("SERVER", "Error, probably interrupted in accept(), see log");
            e.printStackTrace();
        }
        bRunning = false;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            bRunning = true;

            while (bRunning) {
                Socket s = serverSocket.accept();
                Thread requestHandlerThread = new Thread(new RequestHandler(s, loggingHandler));
                requestHandlerThread.start();
            }
        } catch (IOException e) {
            if (serverSocket != null && serverSocket.isClosed())
                Log.d("SERVER", "Normal exit");
            else {
                Log.d("SERVER", "Error");
                e.printStackTrace();
            }
        } finally {
            serverSocket = null;
            bRunning = false;
        }
    }

}
