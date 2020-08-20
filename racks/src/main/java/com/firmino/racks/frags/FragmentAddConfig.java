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

public class FragmentAddConfig extends Fragment {

    private SquareRackButton mBackButton, mAddPotButton, mAddButtonButton, mAddSliderButton;
    private OnAddBackButtonClickListener onAddBackButtonClickListener = () -> {};
    private OnAddPotButtonClickListener onAddPotButtonClickListener = () -> {};
    private OnAddButtonButtonClickListener onAddButtonButtonClickListener = () -> {};
    private OnAddSliderButtonClickListener onAddSliderButtonClickListener = () -> {};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_rack_add, container, false);
        mBackButton = root.findViewById(R.id.Rack_Config_Add_Back);
        mAddPotButton = root.findViewById(R.id.Rack_Config_Add_Pot);
        mAddButtonButton = root.findViewById(R.id.Rack_Config_Add_Button);
        mAddSliderButton = root.findViewById(R.id.Rack_Config_Add_Slider);

        mBackButton.setOnClickListener(view -> onAddBackButtonClickListener.onAddBackButtonClickListener());
        mAddPotButton.setOnClickListener(view -> onAddPotButtonClickListener.onAddPotButtonClickListener());
        mAddButtonButton.setOnClickListener(view -> onAddButtonButtonClickListener.onAddButtonButtonClickListener());
        mAddSliderButton.setOnClickListener(view -> onAddSliderButtonClickListener.onAddSliderButtonClickListener());

        return root;
    }

    public void setOnAddBackButtonClickListener(OnAddBackButtonClickListener onAddBackButtonClickListener) {
        this.onAddBackButtonClickListener = onAddBackButtonClickListener;
    }

    public void setOnAddPotButtonClickListener(OnAddPotButtonClickListener onAddPotButtonClickListener) {
        this.onAddPotButtonClickListener = onAddPotButtonClickListener;
    }

    public void setOnAddButtonButtonClickListener(OnAddButtonButtonClickListener onAddButtonButtonClickListener) {
        this.onAddButtonButtonClickListener = onAddButtonButtonClickListener;
    }

    public void setOnAddSliderButtonClickListener(OnAddSliderButtonClickListener onAddSliderButtonClickListener) {
        this.onAddSliderButtonClickListener = onAddSliderButtonClickListener;
    }

    public interface OnAddBackButtonClickListener {
        void onAddBackButtonClickListener();
    }

    public interface OnAddPotButtonClickListener {
        void onAddPotButtonClickListener();
    }

    public interface OnAddButtonButtonClickListener {
        void onAddButtonButtonClickListener();
    }

    public interface OnAddSliderButtonClickListener {
        void onAddSliderButtonClickListener();
    }

}

