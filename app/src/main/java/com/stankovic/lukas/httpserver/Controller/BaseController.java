package com.stankovic.lukas.httpserver.Controller;

import com.stankovic.lukas.httpserver.Http.Response.Response;

import java.io.IOException;

public class BaseController implements IBaseController {

    protected Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void render() throws IOException {
        response.returnResponse();
    }
}
