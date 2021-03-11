package com.example.tbs.tabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbs.Models.reportModel;
import com.example.tbs.Network.Iokihttp;
import com.example.tbs.R;
import com.example.tbs.table_report;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link spacialReport.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link spacialReport#newInstance} factory method to
 * create an instance of this fragment.
 */
public class spacialReport extends Fragment  implements DatePickerDialog.OnDateSetListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Iokihttp okhttp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public ListView listView;
    public ArrayList<reportModel> dataModels;
    public ListAdapter adapter;
    public View view;
    String[] accountsNoMain,accountsNoSub;
    private SharedPreferences shared;
    View  date_picker_include;
    DatePickerDialog dpd;
    String check_in, check_out = "";
    Calendar now;

    private OnFragmentInteractionListener mListener;

    public spacialReport() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment spacialReport.
     */
    // TODO: Rename and change types and number of parameters
    public static spacialReport newInstance(String param1, String param2) {
        spacialReport fragment = new spacialReport();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_spacial_report, container, false);
        okhttp=new Iokihttp();
        shared = getActivity().getSharedPreferences("com.example.tbs", Context.MODE_PRIVATE);
        date_picker_include = view.findViewById(R.id.date_picker_include);

        now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        date_picker_include.findViewById(R.id.check_inR).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getActivity().getFragmentManager(), "check_in");
            }
        });
        date_picker_include.findViewById(R.id.check_outR).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getActivity().getFragmentManager(), "check_out");
            }
        });
        try {
            getList(shared.getString("section","0"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (view.getTag() == "check_in") {
            check_in = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            ((TextView) (date_picker_include.findViewById(R.id.check_inT))).setText(check_in);
        } else {
            check_out = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            ((TextView) (date_picker_include.findViewById(R.id.check_outT))).setText(check_out);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void setMain(JSONArray jsonArray) throws JSONException {
        final Spinner accounts=view.findViewById(R.id.main);
        String[] accountsNamesMain = new String[jsonArray.length() + 1];
        accountsNoMain = new String[jsonArray.length() + 1];
        accountsNamesMain[0] = getResources().getString(R.string.all);
        accountsNoMain[0] = "0";
        for (int x = 0, z = 1; x < jsonArray.length(); x++, z++) {
            accountsNamesMain[z] = jsonArray.getJSONObject(x).get("AccName").toString();
            accountsNoMain[z] = jsonArray.getJSONObject(x).get("AccNo").toString();
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountsNamesMain);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                accounts.setAdapter(adapter);
            }
        });
        accounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    getSub(String.valueOf(accountsNoMain[accounts.getSelectedItemPosition()]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    private void setSub(JSONArray jsonArray) throws JSONException {
        final Spinner accounts=view.findViewById(R.id.sub);
        String[] accountsNamesSub = new String[jsonArray.length() + 1];
        accountsNoSub = new String[jsonArray.length() + 1];
        accountsNamesSub[0] = getResources().getString(R.string.all);
        accountsNoSub[0] = "0";
        for (int x = 0, z = 1; x < jsonArray.length(); x++, z++) {
            accountsNamesSub[z] = jsonArray.getJSONObject(x).get("AccName").toString();
            accountsNoSub[z] = jsonArray.getJSONObject(x).get("AccNo").toString();
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountsNamesSub);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                accounts.setAdapter(adapter);
            }
        });
    }
    private void getSub(String id) throws JSONException {
        System.out.println("ID IS : "+id);
        JSONArray jsonArray=new JSONArray();
        JSONObject json=new JSONObject();
        final JSONObject subJSON=new JSONObject();
        subJSON.put("main_condition_id",id);
        subJSON.put("section",shared.getString("section","0"));
        subJSON.put("key",shared.getString("key","0"));
        try {

            json.put("method","getSubConditions");
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getContext())) {
            okhttp.post(getContext().getResources().getString(R.string.url), json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    spacialReport.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }});
                    //  hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                JSONObject req = new JSONObject(resJSON.getString("data"));
                                JSONArray sub=req.getJSONArray("sub_condition");
                                setSub(sub);
                            } else {
                                try{
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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

    private void getList(String id) throws JSONException {
        System.out.println("ID IS : "+id);
        JSONArray jsonArray=new JSONArray();
        JSONObject json=new JSONObject();
        final JSONObject subJSON=new JSONObject();
        subJSON.put("type",1);
        subJSON.put("section",id);
        subJSON.put("key",shared.getString("key","0"));
        try {

            json.put("method","getAllReports");
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getContext())) {
            okhttp.post(getContext().getResources().getString(R.string.url), json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    spacialReport.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }});
                    //  hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                JSONObject req = new JSONObject(resJSON.getString("data"));
                                if(!req.getString("reports").isEmpty()|| !req.getString("reports").equals("null")) {
                                    JSONArray reports = req.getJSONArray("reports");
                                    setList(reports);
                                }
                                JSONArray main = req.getJSONArray("main_condition");
                                setMain(main);
                            } else {
                                try{
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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
    public void setList(JSONArray companionsA) throws JSONException {
        System.out.println("11");
        listView=view.findViewById(R.id.report_list);
        System.out.println("22");
        dataModels= new ArrayList<>();
        System.out.println("length=" + companionsA.length());
        if (companionsA.length()>0) {
            spacialReport.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                    //  hideLoading();
                }
            });
        }
        else {
            // System.out.println("33"+y);
            for (int x = 0; x < companionsA.length(); x++) {
                reportModel dataModel = new reportModel();
                dataModel.setId(((JSONObject)companionsA.get(x)).getString("id"));
                dataModel.setName(((JSONObject)companionsA.get(x)).getString("name"));


                dataModels.add(dataModel);
            }

            adapter = new com.example.tbs.adapter.ListAdapter(dataModels, getContext());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View views, int position, long id) {
                    reportModel dataModel = dataModels.get(position);
                    Spinner main=view.findViewById(R.id.main);
                    Spinner sub=view.findViewById(R.id.sub);

                    String mainS=accountsNoMain[main.getSelectedItemPosition()];
                    String subS=accountsNoMain[sub.getSelectedItemPosition()];

                    openTable(dataModel.getId(), mainS, subS);
                    //   intent.putExtra();
                    // startActivity(intent);
                }
            });
            spacialReport.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    public void openTable(String id, String main, String sub) {
        Intent intent=new Intent(getContext(), table_report.class);
        intent.putExtra("id",id);
        intent.putExtra("main", main);
        intent.putExtra("sub", sub);

        startActivity(intent);
    }
}
