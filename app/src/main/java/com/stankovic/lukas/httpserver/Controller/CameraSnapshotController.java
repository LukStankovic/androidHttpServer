package com.stankovic.lukas.httpserver.Controller;

import com.stankovic.lukas.httpserver.File.FileReader;
import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;

import java.io.File;
import java.io.IOException;

public class CameraSnapshotController extends BaseController {

    private FileReader fileReader;

    private File file;

    public CameraSnapshotController() {
        super();
    }

    public CameraSnapshotController(Response response) {
        super();
        this.response = response;
        this.fileReader = new FileReader("/snapshot.jpg");
        file = fileReader.getFile();
    }

    @Override
    public void render() throws IOException {
        if (file.isFile()) {
            response.setHttpStatusCode(HttpStatusCode.OK);
            response.setContentType(fileReader.getFileType());
            response.setContentLength(file.length());

            response.returnFileResponse(file);
        } else {
            response.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
        }
    }
}
