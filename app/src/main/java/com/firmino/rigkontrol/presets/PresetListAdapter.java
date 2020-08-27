package com.firmino.rigkontrol.presets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.firmino.rigkontrol.R;


public class PresetListAdapter extends ArrayAdapter<PresetItem> {

    public PresetListAdapter(@NonNull Context context) {
        super(context, R.layout.layout_main_dialog_openpreset_item);
    }

    private static class ViewHolder {
        LinearLayout layout;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_main_dialog_openpreset_listview, parent, false);
            holder.layout = view.findViewById(R.id.ListView_Preset);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.layout.removeAllViews();
        holder.layout.addView(getItem(position));
        return view;
    }



}
