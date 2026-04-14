package com.example.tregoapp.customer.network;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {

    private static Socket socket;
    private static String BASE_URL;

    private static final String TAG = "SOCKET_MANAGER";

    // ================= INIT =================
    public static void init(String baseUrl) {
        BASE_URL = baseUrl;

        if (socket != null && socket.connected()) {
            Log.d(TAG, "Socket already initialized and connected");
            return;
        }

        try {
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionAttempts = Integer.MAX_VALUE;
            options.reconnectionDelay = 1000;
            options.timeout = 20000;

            socket = IO.socket(BASE_URL, options);

            Log.d(TAG, "Socket initialized with URL: " + BASE_URL);

        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket init error", e);
        }
    }

    // ================= CONNECT =================
    public static void connect() {
        if (socket == null) {
            Log.e(TAG, "Socket is null. Call init() first.");
            return;
        }

        if (!socket.connected()) {
            socket.connect();
            Log.d(TAG, "Socket connecting...");
        } else {
            Log.d(TAG, "Socket already connected");
        }
    }

    // ================= DISCONNECT =================
    public static void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
            Log.d(TAG, "Socket disconnected");
        }
    }

    // ================= GET SOCKET =================
    public static Socket getSocket() {
        return socket;
    }

    // ================= IS CONNECTED =================
    public static boolean isConnected() {
        return socket != null && socket.connected();
    }
}
