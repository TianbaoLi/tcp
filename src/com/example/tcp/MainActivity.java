package com.example.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		try {
			new Thread(new networkRecieve()).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final String SERVERIP = "127.0.0.1";// 服务器端IP
	private static final int SERVERPORT = 4001;// 端口号

	class networkRecieve implements Runnable {
		final EditText show = (EditText) findViewById(R.id.editRecieve);
		ServerSocket messageserver = null;
		Socket messagesocket = null;

		@Override
		public void run() {
			try {
				messageserver = new ServerSocket(SERVERPORT);
				while (true) {

					messagesocket = messageserver.accept();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(
									messagesocket.getInputStream()));
					String text = br.readLine();
					//show.setText(text);
					// text的值是对的，不过在子线程中不能对UI进行修改，这个我没完全弄明白，你自己再研究吧
					messageserver.close();
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		Button send = (Button) findViewById(R.id.buttonSend);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					new Thread(new networkSend()).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		return true;
	}

	class networkSend implements Runnable {
		EditText send = (EditText) findViewById(R.id.editSend);
		Socket messagesendsocket;
		OutputStream ops;

		@Override
		public void run() {
			try {
				messagesendsocket = new Socket(InetAddress.getLocalHost(),
						SERVERPORT);
				ops = messagesendsocket.getOutputStream();
				String datatosend = new String();
				datatosend = send.getText().toString();
				byte[] buf = datatosend.getBytes();
				ops.write(buf);
				ops.flush();
				messagesendsocket.close();
				ops.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
