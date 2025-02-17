package com.stankovic.lukas.httpserver;

import android.app.Service;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import android.widget.Toast;

import java.io.ByteArrayOutputStream;


public class HttpServerService extends Service {

    private Handler handler = null;

    private int maxThreads = 0;

    private final IBinder mIBinder = new LocalBinder();

    public SocketServer s = null;

    private Camera mCamera;

    public static byte[] takenImage;

    class LocalBinder extends Binder
    {
        HttpServerService getInstance()
        {
            return HttpServerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent != null) {
            maxThreads = intent.getIntExtra("maxThreads", 5);
        } else {
            s = new SocketServer(null, 10, this);
            s.start();
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();
        Toast.makeText(this, "Starting service", Toast.LENGTH_SHORT).show();

        mCamera = getCameraInstance();
        if (mCamera != null) {
            mCamera.startPreview();
            mCamera.setPreviewCallback(mPreviewCallback);
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Stopping service", Toast.LENGTH_SHORT).show();
        try {
            s.close();
            s.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(handler != null) {
            handler = null;
        }
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            takenImage = convertYuvToJpeg(data, camera);
        }

        private byte[] convertYuvToJpeg(byte[] data, Camera camera) {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int quality = 100;
            image.compressToJpeg(new Rect(0, 0, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height), quality, baos);//this line decreases the image quality

            return baos.toByteArray();
        }
    };

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            Log.e("LS_SERVER", "Camera doesnt exists");
        }
        return c;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
        s = new SocketServer(handler, maxThreads, this);
        s.start();
    }
}
