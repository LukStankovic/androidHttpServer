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
import java.util.concurrent.Semaphore;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

import com.stankovic.lukas.httpserver.Http.Request.RequestHandler;
import com.stankovic.lukas.httpserver.Http.Request.RequestReader;
import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;
import com.stankovic.lukas.httpserver.Http.Response.ResponseWriter;

public class SocketServer extends Thread {

    private Context context;
    ServerSocket serverSocket;
    public final int port = 12345;
    boolean bRunning;

    private Handler loggingHandler;

    private int maxThreads;

    public SocketServer(Handler handler, int maxThreads, Context context) {
        super();

        this.loggingHandler = handler;
        this.maxThreads = maxThreads;
        this.context = context;
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

            Semaphore semaphore = new Semaphore(maxThreads);

            while (bRunning) {
                Socket s = serverSocket.accept();

               if (semaphore.tryAcquire()) {
                    Thread requestHandlerThread = new Thread(new RequestHandler(s, loggingHandler, semaphore, context));
                    requestHandlerThread.start();
               } else {
                    Log.e("LS_SERVER", "Server busy");
               }
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
