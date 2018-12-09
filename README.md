[客户端GitHub地址](https://github.com/PdKingLiu/AndroidChatRoomClient)
[服务器GitHub地址](https://github.com/PdKingLiu/AndroidChatRoomServer)
[Android多人聊天室—客户端](https://blog.csdn.net/CodeFarmer__/article/details/81748091)
### **先上图**
![这里写图片描述](https://img-blog.csdn.net/20180816193737339?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NvZGVGYXJtZXJfXw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### **主活动连接每个用户**

```
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
```

### **UserThread类为每个用户开一个线程**

```
package com.example.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

class UserThread implements Runnable {

    ArrayList<Socket> socketList;
    Socket socket;
    InputStream receive;
    OutputStream send;

    public UserThread(Socket s, ArrayList<Socket> sockets) {
        socketList = sockets;
        this.socket = s;
    }

    @Override
    public void run() {
        try {
            int len;
            byte[] b = new byte[1024 * 3];
            receive = socket.getInputStream();
            send = socket.getOutputStream();
            while (true) {
                while ((len = receive.read(b)) != -1) {
                    for (Socket sk : socketList) {
                        try {
                            if (sk.equals(socket)) {
                                continue;
                            }
                            OutputStream outputStream = sk.getOutputStream();
                            outputStream.write(b);
                        } catch (Exception e) {
                            MainActivity.socketList.remove(sk);
                            socketList.remove(sk);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### **布局**

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/pinone"
    android:orientation="vertical">

    <LinearLayout
        android:id="@+id/up"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_gravity="center"
        android:layout_marginBottom="192dp"
        android:orientation="horizontal">

        <TextView
            android:id="@+id/port"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="端口号"
            android:textSize="25dp" />

        <EditText
            android:id="@+id/input_port"
            android:layout_width="150dp"
            android:layout_height="wrap_content"
            android:textSize="25dp" />

    </LinearLayout>


    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_gravity="center"
        android:layout_marginBottom="84dp"
        android:orientation="vertical">

        <Button
            android:id="@+id/start"
            android:layout_width="230dp"
            android:layout_height="wrap_content"
            android:alpha="0.7"
            android:background="#F0FFFF"
            android:text="启动服务器"
            android:textSize="15sp" />

        <Button
            android:id="@+id/end"
            android:layout_width="230dp"
            android:layout_height="wrap_content"
            android:alpha="0.7"
            android:background="#F0FFFF"
            android:text="关闭服务器"
            android:textSize="15sp" />

    </LinearLayout>


</RelativeLayout>
```

### **关于 AndroidManifest.xml**

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.server">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ser"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name="com.example.server.MainActivity"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>

```


##### 其中加入了联网权限和禁止转屏

`<uses-permission android:name="android.permission.INTERNET" />`

`android:screenOrientation="portrait"`
