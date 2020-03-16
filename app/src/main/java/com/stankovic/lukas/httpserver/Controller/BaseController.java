package com.stankovic.lukas.httpserver.Controller;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

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

    protected void sendMessage(String type, String messageText) {
        Bundle bundle = new Bundle();
        bundle.putString(type, messageText);
        Message message = Message.obtain();
        message.setData(bundle);

        response.getLoggingHandler().sendMessage(message);
    }
}
