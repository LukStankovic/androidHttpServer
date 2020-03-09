package com.stankovic.lukas.httpserver;

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
        mCamera.setDisplayOrientation(90);


        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(cameraTakingPictures, 0, 3, TimeUnit.SECONDS);
    }

    Runnable cameraTakingPictures = new Runnable() {
        public void run() {
            mCamera.startPreview();
            mCamera.takePicture(null, null, mPicture);
        }
    };

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.button1) {
			s = new SocketServer(handler, etMaxThreads);
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

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("LS_SERVER", "Path: " + Environment.getExternalStorageDirectory().getAbsolutePath());
            File pictureFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/snapshot.jpg");

            if (pictureFile == null){
                Log.d("LS_SERVER", "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("LS_SERVER", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("LS_SERVER", "Error accessing file: " + e.getMessage());
            }
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
}
