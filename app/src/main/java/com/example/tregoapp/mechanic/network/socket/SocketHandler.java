package com.example.tregoapp.mechanic.network.socket;

import org.json.JSONObject;

import io.socket.client.Socket;

public class SocketHandler {

    private final Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    public void joinShop(String shopId) {
        if (socket != null) {
            socket.emit("join_shop", shopId);
        }
    }

    // 🔔 New request
    public void onNewRequest(OnNewRequest callback) {

        socket.off("new_request");

        socket.on("new_request", args -> {
            if (args.length == 0) return;

            JSONObject data = (JSONObject) args[0];

            callback.onNew(data);
        });
    }

    public interface OnNewRequest {
        void onNew(JSONObject data);
    }

    // ❌ Remove request
    public void onRequestRemoved(OnRemove callback) {

        socket.off("request_removed"); // 🔥 prevent duplicate listeners

        socket.on("request_removed", args -> {
            if (args.length == 0) return;

            JSONObject data = (JSONObject) args[0];
            callback.onRemove(data.optString("requestId"));
        });
    }

    public interface OnRemove {
        void onRemove(String requestId);
    }
}