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


import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

import com.stankovic.lukas.httpserver.Http.Request.RequestHandler;
import com.stankovic.lukas.httpserver.Http.Request.RequestReader;
import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;
import com.stankovic.lukas.httpserver.Http.Response.ResponseWriter;

public class SocketServer extends Thread {

    ServerSocket serverSocket;
    public final int port = 12345;
    boolean bRunning;

    private Handler loggingHandler;

    private EditText eTMaxThreads;

    public SocketServer(Handler handler, EditText eTMaxThreads) {
        super();

        this.loggingHandler = handler;
        this.eTMaxThreads = eTMaxThreads;
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

            String etMaxThreadsText = String.valueOf(eTMaxThreads.getText());
            int maxThreads = !etMaxThreadsText.equals("") ? Integer.parseInt(etMaxThreadsText) : 0;
            Semaphore semaphore = new Semaphore(maxThreads);

            while (bRunning) {
                Socket s = serverSocket.accept();
                Log.d("LS_SERVER", "available: " + semaphore.availablePermits());

                semaphore.acquire();
                Thread requestHandlerThread = new Thread(new RequestHandler(s, loggingHandler, semaphore));
                requestHandlerThread.start();
            }
        } catch (IOException e) {
            if (serverSocket != null && serverSocket.isClosed())
                Log.d("SERVER", "Normal exit");
            else {
                Log.d("SERVER", "Error");
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            Log.e("LS_SERVER", e.getMessage());
        } finally {
            serverSocket = null;
            bRunning = false;
        }
    }

}
