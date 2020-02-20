package com.stankovic.lukas.httpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.stankovic.lukas.httpserver.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Response.Response;

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
            Log.d("SERVER", "Creating Socket");
            serverSocket = new ServerSocket(port);
            bRunning = true;
            while (bRunning) {
                Log.d("SERVER", "Socket Waiting for connection");
                Socket s = serverSocket.accept();
                Log.d("SERVER", "Socket Accepted");

                OutputStream o = s.getOutputStream();
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(o));
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                String line = in.readLine();
                int lineCount = 0;
                String method = "", uri = "";

                try {
                    while (line != null && !line.isEmpty()) {

                        if (lineCount == 0) {
                            method = getHttpMethod(line);
                            uri = getHttpUri(line);
                        }

                        lineCount++;
                        line = in.readLine();
                    }


                    String externalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String filePath = externalStorageDirectoryPath + uri;

                    File file = new File(filePath);

                    if (!file.exists()) {
                        Response response = new Response(HttpStatusCode.NOT_FOUND, null, null, null);
                        out.write(response.getResponse());

                        out.flush();
                    } else {
                        if (file.isFile()) {
                            String fileType = getFileType(filePath);
                            Response response = new Response(HttpStatusCode.OK, fileType, file.length(), null);

                            out.write(response.getResponseHeader());
                            out.flush();

                            FileInputStream fileInputStream = new FileInputStream(file);
                            byte[] fileBytes = new byte[2048];
                            while (fileInputStream.read(fileBytes) != 0) {
                                o.write(fileBytes);
                            }
                            o.flush();
                        } else {
                            File directory = new File(filePath + "/");
                            File[] foldersAndFiles = directory.listFiles();
                            if (foldersAndFiles != null) {
                                Response response = new Response(HttpStatusCode.OK, null, null, null);
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
                        Log.d("LS_SERVER", "Socket Closed");
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


    private String getHttpMethod(String line) {
        String[] parts = line.split(" ");

        return parts[0];
    }

    private String getHttpUri(String line) {
        String[] parts = line.split(" ");

        return parts[1];
    }

    private static String getFileType(String url) {
        String type = null;

        if (url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);;
        }

        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }

        return type;
    }

}
