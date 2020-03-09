package com.stankovic.lukas.httpserver.Controller;

import com.stankovic.lukas.httpserver.File.FileReader;
import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;

import java.io.File;
import java.io.IOException;

public class FileController extends BaseController {

    private FileReader fileReader;

    private File file;

    public FileController() {
        super();
    }

    public FileController(Response response, FileReader fileReader) {
        super();
        this.response = response;
        this.fileReader = fileReader;
        file = fileReader.getFile();
    }

    @Override
    public void render() throws IOException {
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setContentType(fileReader.getFileType());
        response.setContentLength(file.length());

        response.returnFileResponse(file);
    }
}
