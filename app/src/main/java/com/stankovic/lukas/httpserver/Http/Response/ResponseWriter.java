package com.stankovic.lukas.httpserver.Http.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class ResponseWriter {

    private BufferedWriter bufferedWriter;

    private OutputStream outputStream;

    private Response response;

    public ResponseWriter(
        BufferedWriter bufferedWriter,
        OutputStream outputStream
    ) {
        this.bufferedWriter = bufferedWriter;
        this.outputStream = outputStream;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void writeFileAndFlush(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[2048];
        int length;
        while ((length = fileInputStream.read(fileBytes)) != 0) {
            outputStream.write(fileBytes, 0, length);
        }
        outputStream.flush();
    }

    public void writeListingAndFlush(File[] filesAndFolders) throws IOException {
        StringBuilder body = new StringBuilder("<h1>Folder structure</h1>");

        // TODO LS if is not root /
        body.append("<li><a href='../'><--</a></li>\n");

        for (File folderOrFile : filesAndFolders) {
            body.append("<li><a href='").append(folderOrFile.getName());

            if (folderOrFile.isDirectory()) {
                body.append("/");
            }

            body.append("'>").append(folderOrFile.getName()).append("</a></li>\n");
        }
        body.append("</ul>\n");

        response.setBody(body.toString());
        response.buildBasicHtmlBodyPage();

        bufferedWriter.write(response.getResponse());


        bufferedWriter.flush();
    }

    public void writeResponseHeaderAndFlush() throws IOException {
        writeAndFlush(response.getResponseHeader());
    }

    public void writeResponseAndFlush() throws IOException {
        writeAndFlush(response.getResponse());
    }

    public void write(String out) throws IOException {
        bufferedWriter.write(out);
    }

    public void flush() throws IOException {
        bufferedWriter.flush();
    }

    public void writeAndFlush(String out) throws IOException {
        bufferedWriter.write(out);
        bufferedWriter.flush();
    }
}
