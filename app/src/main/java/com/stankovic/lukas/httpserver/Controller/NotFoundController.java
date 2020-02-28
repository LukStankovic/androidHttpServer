package com.stankovic.lukas.httpserver.Controller;

import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;

import java.io.IOException;

public class NotFoundController extends BaseController {

    public NotFoundController() {
        super();
        response.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
    }

    public NotFoundController(Response response) {
        super();
        this.response = response;
        response.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
    }

}
