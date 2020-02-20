package com.stankovic.lukas.httpserver.Http.Response;

public class Response {

    private HttpStatusCode httpStatusCode;

    private String contentType;

    private Long contentLength;

    private String body;

    private String response;

    public Response(HttpStatusCode httpStatusCode, String contentType, Long contentLength, String body) {
        this.httpStatusCode = httpStatusCode;
        this.contentType = contentType == null ? "text/html" : contentType;
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

    public String getResponseHeader() {
        return getHttpHeader();
    }

    public void buildBasicHtmlBodyPage() {
        body =  "<html><body>" + body + "</body></html>";
    }

    private String getHttpBody() {
        if (body == null && httpStatusCode == HttpStatusCode.NOT_FOUND) {
            body = "Not Found!";
            buildBasicHtmlBodyPage();

            return body;
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

}
