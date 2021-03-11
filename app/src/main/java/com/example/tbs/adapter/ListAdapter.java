package com.example.tbs.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tbs.Models.reportModel;
import com.example.tbs.R;

import java.util.ArrayList;


public class ListAdapter  extends ArrayAdapter<reportModel> implements View.OnClickListener {
    private Context mContext;




    private static class ViewHolder {
        TextView id;
        TextView name;
        TextView destination;
        TextView date;

    }



    public ListAdapter(ArrayList<reportModel> data, Context context) {
        super(context, R.layout.report_list, data);
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;
    private ViewHolder viewHolder;
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        reportModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.report_list, parent, false);



            assert dataModel != null;


            result=convertView;



            System.out.println("Position="+position);
            convertView.setTag("companion"+position);


        } else {
            // viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_down : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        assert dataModel != null;
        viewHolder.name=convertView.findViewById(R.id.report);
            viewHolder.name.setText(dataModel.getName());
          /*  viewHolder.txtPrice.setText(dataModel.getPrice());
            viewHolder.txtType.setText(dataModel.getType());
          */
        // new LoadImageTask(this).execute(dataModel.getImage());
        return convertView;
    }

}
