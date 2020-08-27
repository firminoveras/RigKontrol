package com.firmino.rigkontrol.racks.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.kinterface.views.KButton;

public class FragmentTitleConfig extends Fragment {

    private EditText mTitleText;
    private KButton mBackButton, mOkButton;
    private OnTitleBackClickListener onTitleBackClickListener = button -> {};
    private OnTitleRenameButtonClick onTitleRenameButtonClick = newTitle -> {};
    private OnTitleResumeListener onTitleResumeListener = title -> {};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_rack_config_title, container, false);
        mTitleText = root.findViewById(R.id.Rack_Config_Title_Text);
        mBackButton = root.findViewById(R.id.Rack_Config_Title_Back);
        mOkButton = root.findViewById(R.id.Rack_Config_Title_Ok);
        mBackButton.setOnClickListener(view -> onTitleBackClickListener.onTitleBackClickListener((KButton) view));
        mOkButton.setOnClickListener(view -> {
            if (mTitleText.getText().toString().matches("[A-Za-z0-9 ]+")) {
                mTitleText.setText(mTitleText.getText().toString().trim());
                onTitleRenameButtonClick.onTitleRenameButtonClick(mTitleText.getText().toString());
            } else mTitleText.setError("Invalid Characters.");
        });
        return root;
    }

    @Override
    public void onResume() {
        onTitleResumeListener.onTitleResumeListener(this.mTitleText);
        super.onResume();
    }

    public void setOnTitleBackClickListener(OnTitleBackClickListener onTitleBackClickListener) {
        this.onTitleBackClickListener = onTitleBackClickListener;
    }

    public void setOnTitleRenameButtonClick(OnTitleRenameButtonClick onTitleRenameButtonClick) {
        this.onTitleRenameButtonClick = onTitleRenameButtonClick;
    }

    public void setOnTitleResumeListener(OnTitleResumeListener onTitleResumeListener) {
        this.onTitleResumeListener = onTitleResumeListener;
    }

    public interface OnTitleBackClickListener {
        void onTitleBackClickListener(KButton button);
    }

    public interface OnTitleRenameButtonClick {
        void onTitleRenameButtonClick(String newTitle);
    }

    public interface OnTitleResumeListener {
        void onTitleResumeListener(EditText title);
    }

}

