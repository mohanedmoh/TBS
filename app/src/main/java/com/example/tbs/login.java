package com.example.tbs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbs.Network.Iokihttp;
import com.example.tbs.tabs.spacialReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class login extends AppCompatActivity {
private Iokihttp okhttp;
SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        shared = this.getSharedPreferences("com.example.tbs", Context.MODE_PRIVATE);

        okhttp=new Iokihttp();
        final EditText username=findViewById(R.id.username);
        final EditText password=findViewById(R.id.password);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login(username.getText().toString(),password.getText().toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void login(String username,String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        JSONArray jsonArray=new JSONArray();
        JSONObject json=new JSONObject();
        final JSONObject subJSON=new JSONObject();
        try {

            json.put("method","login");
            subJSON.put("username",username);
            subJSON.put("password",md5(password));
            System.out.println(md5(password)+"md5555555555555");
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getApplicationContext())) {
            okhttp.post(getApplicationContext().getResources().getString(R.string.url), json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }});
                    //  hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // hideLoading();
                    if (response.isSuccessful()) {
                       // System.out.println(response.body().string());
                        String responseStr = response.body().string();
                     //   System.out.println(responseStr+"SSSSSSSSSSss");

                        try {

                            JSONObject resJSON = new JSONObject(responseStr);
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                final JSONObject req = new JSONObject(resJSON.getString("data"));
                                System.out.println(req.getString("status")+":::::::::::::::::::::");
                                if(req.getString("status").equals("fail")){
                                    System.out.println("SUCCESS");
                                    try{
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.wrongusername), Toast.LENGTH_SHORT).show();
                                            }
                                        });}
                                    catch (Exception e){}
                                }
                                else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println("SUCCESS");

                                            try {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                                                    shared.edit().putBoolean("login",true).apply();
                                                    shared.edit().putString("key",req.getString("key")).apply();
                                                    openHome();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } else {
                                try{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                        }
                                    });}
                                catch (Exception e){}
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }

            });
        }

    }
    private void openHome(){
        Intent i=new Intent(getApplicationContext(),home.class);
        startActivity(i);
        finish();
    }
    private String md5(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        String original = password;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(original.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return  sb.toString();


    }
}
