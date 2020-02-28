package com.stankovic.lukas.httpserver.Http.Request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.stankovic.lukas.httpserver.Controller.BaseController;
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

public class RequestHandler implements Runnable {

    private Socket socket;

    private RequestReader requestReader;

    private Handler loggingHandler;

    private Response response;

    private BaseController controller;

    public RequestHandler(
            Socket socket,
            Handler loggingHandler
    ) {
        this.socket = socket;
        this.loggingHandler = loggingHandler;
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
            } catch (Exception e) {
                Log.e("LS_SERVER", "ERROR: " + e.getMessage());
                sendMessage("request", "---------------\nError: " + e.getMessage() + "\n---------------");
            } finally {
                sendMessage("request", requestReader.getMethod() + " " + requestReader.getUri() + " (" + SizeConverter.formatFileSize(response.getContentLength()) + ")");
                sendMessage("transferred_bytes", String.valueOf(response.getContentLength()));
                socket.close();
            }

        } catch (IOException e) {
            Log.e("LS_SERVER", "error");
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

        if (!file.exists()) {
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
