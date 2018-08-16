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