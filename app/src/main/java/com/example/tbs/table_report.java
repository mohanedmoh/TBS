package com.example.tbs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.evrencoskun.tableview.TableView;
import com.example.tbs.Models.Cell;
import com.example.tbs.Models.ColumnHeader;
import com.example.tbs.Models.RowHeader;
import com.example.tbs.Network.Iokihttp;
import com.example.tbs.adapter.TableAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class table_report extends AppCompatActivity {

    private Iokihttp okhttp;
    private SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_report);
        okhttp=new Iokihttp();
        shared = this.getSharedPreferences("com.example.tbs", Context.MODE_PRIVATE);

        getReport(getIntent().getExtras().getString("id"),getIntent().getExtras().getString("from"),getIntent().getExtras().getString("to"));
    }
    public void getReport(String id,String from,String to){
            System.out.println("ID IS : "+id);
            JSONArray jsonArray=new JSONArray();
            JSONObject json=new JSONObject();
            final JSONObject subJSON=new JSONObject();
            try {

                json.put("method","reportData");
                subJSON.put("report_id",id);
                subJSON.put("from_date",from);
                subJSON.put("to_date",to);
                subJSON.put("key",shared.getString("key","0"));
                if (getIntent().getExtras().containsKey("main")) {
                    subJSON.put("main_condition_id", getIntent().getExtras().getString("main"));
                    subJSON.put("sub_condition_id", getIntent().getExtras().getString("sub"));
                }
                json.put("data",subJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(okhttp.isNetworkConnected(getApplicationContext())) {
                okhttp.post(getResources().getString(R.string.url), json.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("FAIL");
                       runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                            }});
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // hideLoading();

                        if (response.isSuccessful()) {
                            String responseStr = response.body().string();
                            try {
                                System.out.println(responseStr+"+++++");
                                JSONObject resJSON = new JSONObject(responseStr);
                                final JSONArray subresJSON = new JSONArray(resJSON.getString("data"));
                                if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                fillTable(subresJSON);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    try{
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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
    public void fillTable(JSONArray table) throws JSONException {

        View tableReport=findViewById(R.id.tableReport);
        TableView tableView = tableReport.findViewById(R.id.tableView);



        initializeTableView(tableView,table);

        // Create Column Header list
    }
    private void initializeTableView(TableView tableView,JSONArray table) throws JSONException {
        List<ColumnHeader> mColumnHeaderList = new ArrayList<>();
        List<List<Cell>> mCellList = new ArrayList<>();
        List<RowHeader> mRowHeaderList = new ArrayList<>();

        JSONArray columnHeader=table.getJSONArray(0);
        JSONArray row=table.getJSONArray(1);

        for (int x=0;x<columnHeader.length();x++){
            mColumnHeaderList.add(new ColumnHeader(columnHeader.getString(x),columnHeader.getString(x)));
        }
        for (int y=0;y<row.length();y++){
            RowHeader rh=new RowHeader(String.valueOf(y),String.valueOf(y));
            mRowHeaderList.add(rh);
        }
        for(int z=0;z<row.length();z++){
            List<Cell> c=new ArrayList<>();
            JSONArray rowData=row.getJSONArray(z);
            for(int q=columnHeader.length()-1;q>=0;q--){
                try {
                    JSONObject jsonObject = new JSONObject(rowData.getString(q));
                    String date=jsonObject.getString("date");
                    date=  date.split(" ")[0];
                    c.add(0,new Cell("1",date));
                }
                catch (JSONException js){
                    c.add(0,new Cell("1",rowData.getString(q)));
                }
            }
            mCellList.add(c);
        }
        // Create TableView Adapter
        TableAdapter tableAdapter= new TableAdapter(getApplicationContext());
                tableView.setAdapter(tableAdapter);
             tableAdapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);
        // Create listener
        //tableView.setTableViewListener(new MyTableViewListener(tableView));
    }
}
