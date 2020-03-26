package com.stankovic.lukas.httpserver;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.stankovic.lukas.httpserver.Camera.CameraPreview;
import com.stankovic.lukas.httpserver.Libs.SizeConverter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpServerActivity extends Activity implements OnClickListener{

	private SocketServer s;

	private int transferedBytes = 0;

	private EditText etMaxThreads;


    private Camera mCamera;

    private CameraPreview mPreview;



    public static byte[] takenImage;

    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message inputMessage) {
            TextView textView = findViewById(R.id.textView);
            String requestMessage = inputMessage.getData().getString("request");
            if (requestMessage != null) {
                textView.append(requestMessage + "\n");
            }
            String transferredBytesMessage = inputMessage.getData().getString("transferred_bytes");
            if (transferredBytesMessage != null) {
                transferedBytes += Integer.parseInt(transferredBytesMessage);
                TextView transferedBytesTextView = findViewById(R.id.transferedBytesTextView);
                transferedBytesTextView.setText("Celkem přeneseno: " + SizeConverter.formatFileSize(transferedBytes));
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_server);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button btn1 = (Button)findViewById(R.id.button1);
        Button btn2 = (Button)findViewById(R.id.button2);
         
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        TextView textView = findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        etMaxThreads = (EditText) findViewById(R.id.etMaxThreads);
        mCamera = getCameraInstance();

        if (mCamera != null) {
            mCamera.startPreview();
            mCamera.setPreviewCallback(mPreviewCallback);
        }
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.button1) {
			s = new SocketServer(handler, etMaxThreads, this);
			s.start();
		}
		if (v.getId() == R.id.button2) {
			s.close();
			try {
				s.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
            c = Camera.open(0);
        }
        catch (Exception e){
            Log.e("LS_SERVER", "Camera doesnt exists");
        }
        return c;
    }

}
