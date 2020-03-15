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

    private FileReader fileReader;

    private File file;

    byte[] takenImage;

    private Camera mCamera;

    private CameraPreview mPreview;

    public CameraStreamController() {
        super();
    }

    public CameraStreamController(Response response, Socket socket, Context context) {
        super();
        this.response = response;
        this.fileReader = new FileReader("/snapshot.jpg");
        file = fileReader.getFile();
        this.socket = socket;
    }

    @Override
    public void render() throws IOException {
        Log.d("LS_SERVER", "mcamer:" + mCamera);
        while (!socket.isClosed()) {

            takenImage = HttpServerActivity.takenImage;

            response.setHttpStatusCode(HttpStatusCode.OK);
            response.setContentType("multipart/x-mixed-replace;boundary=lsboundary");
            response.setContentLength((long) takenImage.length);
            response.returnMJpegStream(takenImage);
       }

    }

}
