package com.stankovic.lukas.httpserver.Http.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class RequestReader {

    private String method;

    private String uri;

    private Socket socket;

    private OutputStream outputStream;

    private BufferedReader bufferedReader;

    public RequestReader(
        Socket socket,
        OutputStream outputStream,
        BufferedReader bufferedReader
    ) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.bufferedReader = bufferedReader;
    }

    public void read() throws IOException {
        String line = bufferedReader.readLine();
        int lineCount = 0;

        while (line != null && !line.isEmpty()) {

            if (lineCount == 0) {
                method = getHttpMethod(line);
                uri = getHttpUri(line);
            }

            lineCount++;
            line = bufferedReader.readLine();
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

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
