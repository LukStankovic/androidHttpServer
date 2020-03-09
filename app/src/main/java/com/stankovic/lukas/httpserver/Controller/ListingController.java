package com.stankovic.lukas.httpserver.Controller;

import com.stankovic.lukas.httpserver.File.FileReader;
import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;

import java.io.File;
import java.io.IOException;

public class ListingController extends BaseController  {

    private FileReader fileReader;

    private File[] foldersAndFiles;

    public ListingController() {
        super();
    }

    public ListingController(Response response, FileReader fileReader) {
        super();
        this.response = response;
        this.fileReader = fileReader;
        foldersAndFiles = fileReader.getFile().listFiles();
    }

    @Override
    public void render() throws IOException {
        if (foldersAndFiles != null) {
            response.setHttpStatusCode(HttpStatusCode.OK);
            response.returnListingResponse(foldersAndFiles);
        } else {
            response.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
            response.getResponse();
        }
    }
}
