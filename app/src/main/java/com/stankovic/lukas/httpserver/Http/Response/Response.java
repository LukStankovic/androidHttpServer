package com.stankovic.lukas.httpserver.Http.Response;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;

public class Response {

    private HttpStatusCode httpStatusCode;

    private String contentType;

    private Long contentLength;

    private String body;

    private String response;

    private ResponseWriter responseWriter;

    private Handler loggingHandler;

    public Response(ResponseWriter responseWriter, Handler loggingHandler) {
        this.responseWriter = responseWriter;
        this.loggingHandler = loggingHandler;
    }

    public Response(HttpStatusCode httpStatusCode, String contentType, Long contentLength) {
        this.httpStatusCode = httpStatusCode;
        this.contentType = contentType == null ? "text/html" : contentType;
        this.contentLength = contentLength;
        this.response = "";
    }

    public void returnResponse() throws IOException {
        responseWriter.setResponse(this);
        responseWriter.writeResponseAndFlush();
    }

    public void returnFileResponse(File file) throws IOException {
        responseWriter.setResponse(this);
        responseWriter.writeResponseHeaderAndFlush();
        responseWriter.writeFileAndFlush(file);
    }

    public void returnBytesResponse(byte[] bytes) throws IOException {
        responseWriter.setResponse(this);
        responseWriter.writeResponseHeaderAndFlush();
        responseWriter.writeBytesAndFlush(bytes);
    }

    public void returnMJpegStream(byte[] takenImage) throws IOException {
        this.setContentLength(null);
        responseWriter.setResponse(this);
        responseWriter.writeResponseHeaderAndFlush();

        responseWriter.writeAndFlush("--lsboundary\n");
        responseWriter.writeAndFlush("Content-Type: image/jpeg\n");
        responseWriter.writeAndFlush("Content-Length: " + takenImage.length + "\n\n");

        responseWriter.writeBytesAndFlush(takenImage);
    }

    public void returnListingResponse(File[] foldersAndFiles) throws IOException {
        responseWriter.setResponse(this);
        responseWriter.writeListingAndFlush(foldersAndFiles);
    }

    public String getResponse() {
        addToResponse(getHttpHeader());
        addToResponse(getHttpBody());
        contentLength = (long)response.length();

        return response;
    }

    public String getResponseHeader() {
        return getHttpHeader();
    }

    public void buildBasicHtmlBodyPage() {
        body =  "<html><body>" + body + "</body></html>";
        contentLength = (long) body.length();
    }

    private String getHttpBody() {
        if (body == null && httpStatusCode == HttpStatusCode.NOT_FOUND) {
            body = "Not Found!";
            buildBasicHtmlBodyPage();
        }

        return body;
    }

    private String getHttpHeader() {
        String httpHeader = "HTTP/1.0 " + httpStatusCode.getHttpStatusCodeHeadText() + "\n" +
            "Content-Type: " + contentType + "\n";

        if (contentLength != null) {
            httpHeader += "Content-Length:" + contentLength + "\n";
        }
        httpHeader += "\n";

        return httpHeader;
    }

    private void addToResponse(String text) {
        response += text;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Handler getLoggingHandler() {
        return loggingHandler;
    }
}
