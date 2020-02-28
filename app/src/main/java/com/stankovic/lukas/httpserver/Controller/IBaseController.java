package com.stankovic.lukas.httpserver.Controller;

import com.stankovic.lukas.httpserver.Http.Response.Response;

import java.io.IOException;

public interface IBaseController {

    public void render() throws IOException;

}
