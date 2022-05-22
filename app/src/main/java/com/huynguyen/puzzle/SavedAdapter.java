package com.huynguyen.puzzle;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SavedAdapter extends ArrayAdapter<FileSave> {

Activity context;
int resource;

    public SavedAdapter(Activity context,int resource)
    {
        super(context,resource);
        this.context=context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View customView=inflater.inflate(this.resource,null);
        TextView txtRank = (TextView) customView.findViewById(R.id.txtRank);
        ImageView itemImg = (ImageView) customView.findViewById(R.id.imgFileSave);
        TextView txtTimeSave = (TextView) customView.findViewById(R.id.txtTimeSave);
        TextView txtMovesSave = (TextView) customView.findViewById(R.id.txtMovesSave);
        TextView txtDate = (TextView) customView.findViewById(R.id.txtDate);
        FileSave fileSave = (FileSave) getItem(position);

        itemImg.setImageBitmap(fileSave.getImg());
        String minute = "";
        String second = "";
        if(fileSave.getMinutes()<10) {
            minute = "0"+fileSave.getMinutes();
        }
        else {
            minute = String.valueOf(fileSave.getMinutes());
        }
        if(fileSave.getSeconds()<10) {
            second = "0"+fileSave.getSeconds();
        }
        else {
            second = String.valueOf(fileSave.getSeconds());
        }
        txtTimeSave.setText(minute+":"+second);
        txtMovesSave.setText(fileSave.getMoves()+"");
        txtDate.setText(fileSave.getDate()+"");
        return customView;
    }
}
