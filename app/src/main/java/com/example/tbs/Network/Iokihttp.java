package com.example.tbs.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.tbs.R;

import java.net.InetAddress;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Iokihttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    public Call post(String url, String json, Callback callback) {
System.out.println("url is :"+url);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(callback);
            return call;
    }

    public boolean isNetworkConnected(Context context) {
        if(isConnected(context)){
            return true;}
        else{
            Toast.makeText(context,context.getResources().getString(R.string.check_internet),Toast.LENGTH_SHORT).show();
            return false;
        }

    }
    private boolean isConnected(Context context){

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    public boolean isInternetAvailable(Context context) {
        try {
            InetAddress ipAddr = InetAddress.getByName(context.getResources().getString(R.string.url));
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

}
