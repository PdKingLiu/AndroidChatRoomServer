package com.example.server;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button start;
    private Button end;
    private EditText edit_port;
    private String port_server;
    private ServerSocket serverSocket = null;
    public static ArrayList<Socket> socketList = new ArrayList<>();
    private startServer startserver;

    class startServer extends Thread implements Runnable {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(Integer.parseInt(port_server));
                while (true) {
                    Socket s = serverSocket.accept();
                    socketList.add(s);
                    new Thread(new UserThread(s, socketList)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        edit_port = findViewById(R.id.input_port);
        edit_port.setText("12580");
        start.setOnClickListener(this);
        end.setOnClickListener(this);
        end.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                port_server = edit_port.getText().toString();
                try {
                    Integer.parseInt(port_server);
                } catch (Exception e) {
                    Toast.makeText(this, "端口错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                startserver = new startServer();
                startserver.start();
                Toast.makeText(this, "启动成功", Toast.LENGTH_SHORT).show();
                start.setEnabled(false);
                end.setEnabled(true);
                break;
            case R.id.end:
                try {
                    start.setEnabled(true);
                    end.setEnabled(false);
                    startserver.interrupt();
                    socketList.clear();
                    serverSocket.close();
                    Toast.makeText(this, "关闭成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "关闭异常", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}