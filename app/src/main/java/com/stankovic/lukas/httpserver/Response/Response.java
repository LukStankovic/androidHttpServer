package com.stankovic.lukas.httpserver.Response;

public class Response {

    private HttpStatusCode httpStatusCode;

    private String contentType;

    private String contentLength;

    private String body;

    private String response;

    public Response(HttpStatusCode httpStatusCode, String contentType, String contentLength, String body) {
        this.httpStatusCode = httpStatusCode;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.response = "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResponse() {
        addToResponse(getHttpHeader());
        addToResponse(getHttpBody());

        return response;
    }

    private String getHttpBody() {
        if (body == null) {
            return "<html><body>Not Found!</body></html>";
        }

        return body;
    }

    private String getHttpHeader() {
        String httpHeader = "HTTP/1.0 " + httpStatusCode.getHttpStatusCodeHeadText() + "\n" +
            "Content-Type: " + contentType + "\n";

        if (contentLength != null){
            httpHeader += "Content-Length:" + contentLength + "\n";
        }
        httpHeader += "\n";

        return httpHeader;
    }

    private void addToResponse(String text) {
        response += text;
    }

}
