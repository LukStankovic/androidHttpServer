package com.stankovic.lukas.httpserver.Controller;

import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;
import com.stankovic.lukas.httpserver.HttpServerActivity;

import java.io.IOException;

public class CameraSnapshotController extends BaseController {

    public CameraSnapshotController() {
        super();
    }

    public CameraSnapshotController(Response response) {
        super();
        this.response = response;
    }

    @Override
    public void render() throws IOException {
        byte[] takenImage = HttpServerActivity.takenImage;

        if (takenImage.length > 0) {
            response.setHttpStatusCode(HttpStatusCode.OK);
            response.setContentType("image/jpeg");
            response.setContentLength((long) takenImage.length);

            response.returnBytesResponse(takenImage);
        } else {
            response.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
        }
    }
}
