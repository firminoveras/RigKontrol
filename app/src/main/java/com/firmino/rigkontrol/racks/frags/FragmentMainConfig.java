package com.firmino.rigkontrol.racks.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firmino.rigkontrol.R;

import com.firmino.rigkontrol.kinterface.views.KButton;

public class FragmentMainConfig extends Fragment {

    private OnEditNameClickListener onEditNameClickListener = button -> {};
    private OnEditColorsClickListener onEditColorsClickListener = button -> {};
    private OnMidiConfigClickListener onMidiConfigClickListener = button -> {};
    private OnDeleteRacklickListener onDeleteRacklickListener = button -> {};
    private OnAddClickListener onAddClickListener = button -> {};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_rack_config_main, container, false);
        root.findViewById(R.id.Rack_Config_Main_EditTitle).setOnClickListener(v -> onEditNameClickListener.onEditNameClickListener((KButton) v));
        root.findViewById(R.id.Rack_Config_Main_EditColor).setOnClickListener(v -> onEditColorsClickListener.onEditColorsClickListener((KButton) v));
        root.findViewById(R.id.Rack_Config_Main_MidiSettings).setOnClickListener(v -> onMidiConfigClickListener.onMidiConfigClickListener((KButton) v));
        root.findViewById(R.id.Rack_Config_Main_DeleteRack).setOnClickListener(v -> onDeleteRacklickListener.onDeleteRacklickListener((KButton) v));
        root.findViewById(R.id.Rack_Config_Main_Add).setOnClickListener(v -> onAddClickListener.onAddClickListener((KButton) v));
        return root;
    }

    public void setOnEditNameClickListener(OnEditNameClickListener onEditNameClickListener) {
        this.onEditNameClickListener = onEditNameClickListener;
    }

    public void setOnEditColorsClickListener(OnEditColorsClickListener onEditColorsClickListener) {
        this.onEditColorsClickListener = onEditColorsClickListener;
    }

    public void setOnMidiConfigClickListener(OnMidiConfigClickListener onMidiConfigClickListener) {
        this.onMidiConfigClickListener = onMidiConfigClickListener;
    }

    public void setOnDeleteRacklickListener(OnDeleteRacklickListener onDeleteRacklickListener) {
        this.onDeleteRacklickListener = onDeleteRacklickListener;
    }

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    public interface OnEditNameClickListener {
        void onEditNameClickListener(KButton button);
    }

    public interface OnEditColorsClickListener {
        void onEditColorsClickListener(KButton button);
    }

    public interface OnMidiConfigClickListener {
        void onMidiConfigClickListener(KButton button);
    }

    public interface OnDeleteRacklickListener {
        void onDeleteRacklickListener(KButton button);
    }

    public interface OnAddClickListener {
        void onAddClickListener(KButton button);
    }

}
