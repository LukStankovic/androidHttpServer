package com.stankovic.lukas.httpserver.Http.Request;

import com.stankovic.lukas.httpserver.Http.Request.Controller.DirectoryListingController;
import com.stankovic.lukas.httpserver.Http.Request.Controller.FileController;
import com.stankovic.lukas.httpserver.Http.Request.Controller.NotFoundPageController;

public class RequestHandler {

    private RequestReader requestReader;
    private FileController fileController;
    private NotFoundPageController notFoundPageController;
    private DirectoryListingController directoryListingController;


    public RequestHandler(
        RequestReader requestReader,
        FileController fileController,
        DirectoryListingController directoryListingController,
        NotFoundPageController notFoundPageController
    ) {
        this.requestReader = requestReader;
        this.fileController = fileController;
        this.notFoundPageController = notFoundPageController;
        this.directoryListingController = directoryListingController;
    }

    public void handle() {

    }

}
