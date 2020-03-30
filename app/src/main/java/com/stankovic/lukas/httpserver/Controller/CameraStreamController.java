package com.stankovic.lukas.httpserver.Controller;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import com.stankovic.lukas.httpserver.Camera.CameraPreview;
import com.stankovic.lukas.httpserver.File.FileReader;
import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;
import com.stankovic.lukas.httpserver.HttpServerActivity;
import com.stankovic.lukas.httpserver.HttpServerService;
import com.stankovic.lukas.httpserver.Libs.SizeConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CameraStreamController extends BaseController {

    private Socket socket;

    public CameraStreamController() {
        super();
    }

    public CameraStreamController(Response response, Socket socket) {
        super();
        this.response = response;
        this.socket = socket;
    }

    @Override
    public void render() throws IOException {
        this.sendMessage("request", "GET /camera/stream/ (" + SizeConverter.formatFileSize(HttpServerService.takenImage.length) + ")");
        byte[] takenImage = HttpServerService.takenImage;
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setContentType("multipart/x-mixed-replace;boundary=lsboundary");
        response.setContentLength((long) takenImage.length);

        while (!socket.isClosed()) {
            takenImage = HttpServerService.takenImage;
            response.returnMJpegStream(takenImage);
            this.sendMessage("transferred_bytes", String.valueOf(takenImage.length));
       }
    }

}
