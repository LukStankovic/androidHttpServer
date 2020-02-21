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

import android.util.Log;

import com.stankovic.lukas.httpserver.File.FileReader;
import com.stankovic.lukas.httpserver.Http.Request.RequestHandler;
import com.stankovic.lukas.httpserver.Http.Request.RequestReader;
import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;
import com.stankovic.lukas.httpserver.Http.Response.ResponseWriter;

import javax.inject.Inject;

public class SocketServer extends Thread {

    ServerSocket serverSocket;
    public final int port = 12345;
    boolean bRunning;

    private RequestHandler requestHandler;

    @Inject
    public SocketServer(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
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
                OutputStream o = s.getOutputStream();
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(o));
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                try {
                    RequestReader requestReader = new RequestReader(s, o, in);
                    requestReader.read();

                    FileReader fileReader = new FileReader(requestReader.getUri());
                    File file = fileReader.getFile();

                    ResponseWriter responseWriter = new ResponseWriter(out, o);

                    if (!file.exists()) {
                        Response response = new Response(HttpStatusCode.NOT_FOUND, null, null, null);
                        responseWriter.setResponse(response);
                        responseWriter.writeResponseAndFlush();
                    } else {
                        if (file.isFile()) {
                            Response response = new Response(
                                HttpStatusCode.OK, fileReader.getFileType(), file.length(), null
                            );
                            responseWriter.setResponse(response);
                            responseWriter.writeResponseHeaderAndFlush();
                            responseWriter.writeFileAndFlush(file);
                        } else {
                            File[] foldersAndFiles = file.listFiles();
                            if (foldersAndFiles != null) {
                                Response response = new Response(
                                    HttpStatusCode.OK, null, null, null
                                );
                                responseWriter.setResponse(response);
                                responseWriter.writeListingAndFlush(foldersAndFiles);
                            }

                            responseWriter.flush();
                        }
                    }
                } catch (Exception e) {
                    Log.e("LS_SERVER", "ERROR: " + e.getMessage());
                } finally {
                    s.close();
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
