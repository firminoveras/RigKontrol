package com.firmino.rigkontrol.racks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.racks.Rack;


public class RackListAdapter extends ArrayAdapter<Rack> {

    public RackListAdapter(@NonNull Context context) {
        super(context, R.layout.layout_listview);
    }

    public static class ViewHolder {
        LinearLayout container;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_listview, parent, false);
            holder.container = view.findViewById(R.id.ListView_Item);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.container.removeAllViews();
        holder.container.addView(getItem(position));
        return view;
    }



}
