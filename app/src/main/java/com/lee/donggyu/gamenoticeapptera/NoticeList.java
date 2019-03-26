package com.lee.donggyu.gamenoticeapptera;

/**
 * Created by donggyu.lee on 2018/11/17.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NoticeList extends ArrayAdapter<String>{

    private final Activity context;
    private final List<String> web;
    private final Integer[] imageId;

    public NoticeList(Activity context,
                      List<String> web, Integer[] imageId) {
        super(context, R.layout.list_notice, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_notice, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        txtTitle.setText(web.get(position));


        // TODO : title Icon Image -> think another way
        try {
            imageView.setImageResource(imageId[position]);
        } catch (Exception e) {
            imageView.setImageResource(imageId[0]);
        }

        return rowView;
    }
}