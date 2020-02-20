package com.stankovic.lukas.httpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.stankovic.lukas.httpserver.File.FileReader;
import com.stankovic.lukas.httpserver.Http.Request.RequestReader;
import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;

public class SocketServer extends Thread {

    ServerSocket serverSocket;
    public final int port = 12345;
    boolean bRunning;

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

                    if (!file.exists()) {
                        Response response = new Response(HttpStatusCode.NOT_FOUND, null, null, null);
                        out.write(response.getResponse());

                        out.flush();
                    } else {
                        if (file.isFile()) {
                            Response response = new Response(
                                HttpStatusCode.OK, fileReader.getFileType(), file.length(), null
                            );

                            out.write(response.getResponseHeader());
                            out.flush();

                            FileInputStream fileInputStream = new FileInputStream(file);
                            byte[] fileBytes = new byte[2048];
                            while (fileInputStream.read(fileBytes) != 0) {
                                o.write(fileBytes);
                            }
                            o.flush();

                        } else {
                            File[] foldersAndFiles = file.listFiles();
                            if (foldersAndFiles != null) {
                                Response response = new Response(
                                        HttpStatusCode.OK, null, null, null
                                );
                                StringBuilder body = new StringBuilder("<h1>Folder structure</h1>");

                                // TODO LS if is not root /
                                body.append("<li><a href='../'><--</a></li>\n");

                                for (File folderOrFile : foldersAndFiles) {
                                    body.append("<li><a href='").append(folderOrFile.getName());

                                    if (folderOrFile.isDirectory()) {
                                        body.append("/");
                                    }

                                   body.append("'>").append(folderOrFile.getName()).append("</a></li>\n");
                                }
                                body.append("</ul>\n");

                                response.setBody(body.toString());
                                response.buildBasicHtmlBodyPage();

                                out.write(response.getResponse());
                            }

                            out.flush();
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
