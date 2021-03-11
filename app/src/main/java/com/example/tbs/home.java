package com.example.tbs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbs.Models.gridModel;
import com.example.tbs.Network.Iokihttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class home extends AppCompatActivity {
private Iokihttp okhttp;
JSONArray sections;
GridView gridView;
ArrayList<gridModel> dataModels;
private SharedPreferences shared ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        okhttp=new Iokihttp();
        shared = this.getSharedPreferences("com.example.tbs", Context.MODE_PRIVATE);
        getSections();
        // init();
    }
    public void openFI(){
        Intent intent=new Intent(getApplicationContext(), report_main.class);
        startActivity(intent);
    }
    private void getSections(){
        JSONArray jsonArray=new JSONArray();
        JSONObject json=new JSONObject();
        final JSONObject subJSON=new JSONObject();
        try {

            json.put("method","getSections");
            subJSON.put("key",shared.getString("key",""));
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
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        try {

                            JSONObject resJSON = new JSONObject(responseStr);
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                final JSONObject req = new JSONObject(resJSON.getString("data"));
                                final JSONArray sections=req.getJSONArray("sections");
                                     try {
                                         initGrid(sections);
                                     } catch (IOException e) {
                                         e.printStackTrace();
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
    private void initGrid(JSONArray sections) throws IOException {
        int length=sections.length();
        final int columnNo=2;
        int rowNo=0;
        if(length%2!=0){
            length=length+1;
        }
        rowNo=length/2;
        final GridLayout section_grid=findViewById(R.id.section_grid);
        final int finalRowNo = rowNo;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                section_grid.setColumnCount(columnNo);
                section_grid.setRowCount(finalRowNo);
            }
        });


        for (int x=0,c=0,r=0;x<length;x++,c++){
            if (c == columnNo) {
                c = 0;
                r++;
            }
           LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View section_card = inflater.inflate(R.layout.section_card,null);

            final CardView section_cardView=section_card.findViewById(R.id.section_cardView);
            section_cardView.setId(getID("1",x));
            ImageView icon=section_cardView.findViewById(R.id.section_icon);
            icon.setId(getID("2",x));
            TextView lable=section_cardView.findViewById(R.id.section_label);
            lable.setId(getID("3",x));
            JSONObject section= null;
            try {
                section = sections.getJSONObject(x);
                lable.setText(section.getString("title"));
                section_cardView.setTag(section.getString("id"));
                URL url = new URL(getResources().getString(R.string.imgUrl)+section.getString("image"));
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                icon.setImageBitmap(bmp);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
            //icon.setImageURI(Uri.parse(section.getString("img")));
            section_cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMainReport(Integer.parseInt(view.getTag().toString()));
                }
            });
            GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            if (r == 0 && c == 0) {
                Log.e("", "spec");
                colspan = GridLayout.spec(GridLayout.UNDEFINED, 2);
                rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
            }
            final GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colspan);
            if(section_cardView.getParent() != null) {
                ((ViewGroup)section_cardView.getParent()).removeView(section_cardView); // <- fix
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    section_grid.addView(section_cardView);
                }
            });
        }
    }
    private void openMainReport(int id){
        Intent intent=new Intent(getApplicationContext(),report_main.class);
shared.edit().putString("section",String.valueOf(id)).apply();
startActivity(intent);
    }
    private int getID(String id,int number){
        String s=id+number;
        return Integer.parseInt(s);
    }
}
