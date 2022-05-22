package com.huynguyen.puzzle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private Activity c;
    int resource;
    private ArrayList<Bitmap> bitmapList;
    private int imageWidth, imageHeight;


    public GridViewAdapter(Activity c,ArrayList<Bitmap> bitmapList, int imageWidth, int imageHeight)
    {
        this.c = c;
        this.bitmapList=bitmapList;
        this.imageWidth =imageWidth;
        this.imageHeight = imageHeight;
        //bitmapList = MainActivity.chunkedImages;
    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(c);
        imageView.setBackgroundColor(Color.BLACK);
        //imageView.requestLayout();
        //imageView.getLayoutParams().height=imageHeight;
        //imageView.getLayoutParams().width=imageWidth;
        //imageView.setLayoutParams(new ViewGroup.LayoutParams(imageWidth,imageHeight));
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,imageHeight));
        imageView.setPadding(5,5,0,0);
        imageView.setImageBitmap(bitmapList.get(position));
        return imageView;
    }
}
