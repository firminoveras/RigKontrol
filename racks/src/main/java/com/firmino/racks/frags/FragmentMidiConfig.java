package com.firmino.racks.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firmino.racks.R;
import com.firmino.racks.interfaceitems.SquareRackButton;

public class FragmentMidiConfig extends Fragment {

    private SquareRackButton mBackButton, mPlusButton, mMinusButton;
    private TextView mControlChangeText;
    private OnMidiBackClickListener onMidiBackClickListener = () -> {};
    private OnMidiResumeListener onMidiResumeListener = textView -> {};
    private OnMidiChangeListener onMidiChangeListener = cc -> {};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_rack_midi_config, container, false);
        mBackButton = root.findViewById(R.id.Rack_Config_Midi_Back);
        mPlusButton = root.findViewById(R.id.Rack_Config_Midi_Plus);
        mMinusButton = root.findViewById(R.id.Rack_Config_Midi_Minus);
        mControlChangeText = root.findViewById(R.id.Rack_Config_Midi_CC);

        mBackButton.setOnClickListener(view -> onMidiBackClickListener.onMidisBackClickListener());
        mPlusButton.setOnClickListener(view -> {
            int newValue = Integer.parseInt(mControlChangeText.getText().toString()) + 1;
            if (newValue <= 170) mControlChangeText.setText(String.valueOf(newValue));
            onMidiChangeListener.onMidiChangeListener(newValue);
        });
        mMinusButton.setOnClickListener(view -> {
            int newValue = Integer.parseInt(mControlChangeText.getText().toString()) - 1;
            if (newValue >= 0) mControlChangeText.setText(String.valueOf(newValue));
            onMidiChangeListener.onMidiChangeListener(newValue);
        });
        return root;
    }

    @Override
    public void onResume() {
        onMidiResumeListener.onMidiResumeListener(mControlChangeText);
        super.onResume();
    }

    public void setOnMidiBackClickListener(OnMidiBackClickListener onMidiBackClickListener) {
        this.onMidiBackClickListener = onMidiBackClickListener;
    }

    public void setOnMidiResumeListener(OnMidiResumeListener onMidiResumeListener) {
        this.onMidiResumeListener = onMidiResumeListener;
    }

    public void setOnMidiChangeListener(OnMidiChangeListener onMidiChangeListener) {
        this.onMidiChangeListener = onMidiChangeListener;
    }

    public interface OnMidiBackClickListener {
        void onMidisBackClickListener();
    }

    public interface OnMidiResumeListener {
        void onMidiResumeListener(TextView textView);
    }
    public interface OnMidiChangeListener {
        void onMidiChangeListener(int cc);
    }


}

