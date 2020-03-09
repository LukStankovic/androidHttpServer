package com.stankovic.lukas.httpserver.Http.Request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.stankovic.lukas.httpserver.Controller.BaseController;
import com.stankovic.lukas.httpserver.Controller.CameraSnapshotController;
import com.stankovic.lukas.httpserver.Controller.FileController;
import com.stankovic.lukas.httpserver.Controller.ListingController;
import com.stankovic.lukas.httpserver.Controller.NotFoundController;
import com.stankovic.lukas.httpserver.File.FileReader;
import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;
import com.stankovic.lukas.httpserver.Http.Response.ResponseWriter;
import com.stankovic.lukas.httpserver.Libs.SizeConverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class RequestHandler implements Runnable {

    private final Semaphore semaphore;

    private Socket socket;

    private RequestReader requestReader;

    private Handler loggingHandler;

    private Response response;

    private BaseController controller;

    public RequestHandler(
            Socket socket,
            Handler loggingHandler,
            Semaphore semaphore
    ) {
        this.socket = socket;
        this.loggingHandler = loggingHandler;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            try {
                OutputStream o = socket.getOutputStream();
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(o));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                requestReader = new RequestReader(socket, o, in);
                requestReader.read();

                ResponseWriter responseWriter = new ResponseWriter(out, o);
                response = new Response(responseWriter);

                handleRequest();
            } catch (IOException e){
                Log.e("LS_SERVER", "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e("LS_SERVER", "ERROR: " + e.getMessage());
                Log.e("LS_SERVER", "ERROR: " + e.getStackTrace());
                sendMessage("request", "---------------\nError: " + e.getMessage() + "\n---------------");
            } finally {
                if (requestReader != null && response != null) {
                    // TODO LS
                    //sendMessage("request", requestReader.getMethod() + " " + requestReader.getUri() + " (" + SizeConverter.formatFileSize(response.getContentLength()) + ")");
                    //sendMessage("transferred_bytes", String.valueOf(response.getContentLength()));
                }

                semaphore.release();
                socket.close();
            }

        } catch (IOException e) {
            Log.e("LS_SERVER", "error");
            sendMessage("request", "IOException  - " + e.getMessage());
        }
    }

    private synchronized void sendMessage(String type, String messageText) {
        Bundle bundle = new Bundle();

        bundle.putString(type, messageText);
        Message message = Message.obtain();
        message.setData(bundle);

        loggingHandler.sendMessage(message);
    }

    private synchronized void handleRequest() throws IOException {
        FileReader fileReader = new FileReader(requestReader.getUri());
        File file = fileReader.getFile();

        Log.d("LS_SERVER", requestReader.getUri());
        if (requestReader.getUri().equals("/camera/snapshot/") || requestReader.getUri().equals("/camera/snapshot")) {
            controller = new CameraSnapshotController(response);
        } else if (!file.exists()) {
            controller = new NotFoundController(response);
        } else {
            if (file.isFile()) {
                controller = new FileController(response, fileReader);
            } else {
                controller = new ListingController(response, fileReader);
            }
        }

        controller.render();
    }

}
