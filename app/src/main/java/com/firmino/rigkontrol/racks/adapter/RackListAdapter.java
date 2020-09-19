package com.firmino.rigkontrol.racks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.racks.RackItem;


public class RackListAdapter extends ArrayAdapter<RackItem> {

    public RackListAdapter(@NonNull Context context) {
        super(context, R.layout.layout_main_dialog_openrack_item);
    }

    private static class ViewHolder {
        LinearLayout layout;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        final RackListAdapter.ViewHolder holder;
        if (view == null) {
            holder = new RackListAdapter.ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_listview, parent, false);
            holder.layout = view.findViewById(R.id.ListView_Item);
            view.setTag(holder);
        }else{
            holder = (RackListAdapter.ViewHolder) view.getTag();
        }
        holder.layout.removeAllViews();
        holder.layout.addView(getItem(position));
        return view;
    }
}
