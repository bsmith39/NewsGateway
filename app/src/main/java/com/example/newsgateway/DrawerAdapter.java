package com.example.newsgateway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawerAdapter extends BaseAdapter {

    Context context;
    ArrayList<Drawer> drawers;

    public DrawerAdapter(Context context, ArrayList<Drawer> drawers){
        this.drawers = drawers;
        this.context = context;
    }

    @Override
    public int getCount(){
        return drawers.size();
    }

    @Override
    public Object getItem(int pos){
        return drawers.get(pos);
    }

    @Override
    public long getItemId(int pos){
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        View view = convertView;
        if(view == null){
            view = (LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
        }

        Drawer drawer = drawers.get(pos);
        TextView itemText = view.findViewById(R.id.itemText);
        itemText.setTextColor(drawer.getColor());
        itemText.setText(drawer.getName());
        return view;
    }
}
