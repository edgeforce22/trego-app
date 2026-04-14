package com.example.tregoapp.mechanic.network.socket;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {

    private static Socket socket;

    public static void init(String url) {
        if (socket != null) return; // 🔥 prevent duplicate init

        try {
            socket = IO.socket(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void connect() {
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    public static void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }
}