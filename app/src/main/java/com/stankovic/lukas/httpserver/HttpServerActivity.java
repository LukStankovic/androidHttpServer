package com.stankovic.lukas.httpserver;

import android.os.Bundle;
import android.app.Activity;
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
import android.widget.TextView;

import com.stankovic.lukas.httpserver.Libs.SizeConverter;

public class HttpServerActivity extends Activity implements OnClickListener{

	private SocketServer s;

	private int transferedBytes = 0;

	private EditText etMaxThreads;

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
    }

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
}
