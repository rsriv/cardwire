package com.mapps.rishi.cardwire;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketApplication extends Application {
    private static SocketApplication xxx;
    public static Context context = null;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("wob", getApplicationContext()+"");
        context = getApplicationContext();
    }
}