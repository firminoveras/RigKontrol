package com.firmino.racks.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firmino.racks.R;
import com.firmino.racks.interfaceitems.SquareRackButton;

public class FragmentMainConfig extends Fragment {

    private OnEditNameClickListener onEditNameClickListener = button -> {};
    private OnEditColorsClickListener onEditColorsClickListener = button -> {};
    private OnMidiConfigClickListener onMidiConfigClickListener = button -> {};
    private OnDeleteRacklickListener onDeleteRacklickListener = button -> {};
    private OnAddClickListener onAddClickListener = button -> {};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_rack_main, container, false);
        root.findViewById(R.id.Rack_Config_Main_EditTitle).setOnClickListener(v -> onEditNameClickListener.onEditNameClickListener((SquareRackButton) v));
        root.findViewById(R.id.Rack_Config_Main_EditColor).setOnClickListener(v -> onEditColorsClickListener.onEditColorsClickListener((SquareRackButton) v));
        root.findViewById(R.id.Rack_Config_Main_MidiSettings).setOnClickListener(v -> onMidiConfigClickListener.onMidiConfigClickListener((SquareRackButton) v));
        root.findViewById(R.id.Rack_Config_Main_DeleteRack).setOnClickListener(v -> onDeleteRacklickListener.onDeleteRacklickListener((SquareRackButton) v));
        root.findViewById(R.id.Rack_Config_Main_Add).setOnClickListener(v -> onAddClickListener.onAddClickListener((SquareRackButton) v));
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
        void onEditNameClickListener(SquareRackButton button);
    }

    public interface OnEditColorsClickListener {
        void onEditColorsClickListener(SquareRackButton button);
    }

    public interface OnMidiConfigClickListener {
        void onMidiConfigClickListener(SquareRackButton button);
    }

    public interface OnDeleteRacklickListener {
        void onDeleteRacklickListener(SquareRackButton button);
    }

    public interface OnAddClickListener {
        void onAddClickListener(SquareRackButton button);
    }

}
