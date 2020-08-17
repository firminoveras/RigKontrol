package com.firmino.racks.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firmino.racks.R;
import com.firmino.racks.interfaceitems.SquareRackButton;

public class FragmentTitleConfig extends Fragment {

    private EditText mTitleText;
    private SquareRackButton mBackButton, mOkButton;
    private OnTitleBackClickListener onTitleBackClickListener = button -> {};
    private OnTitleRenameButtonClick onTitleRenameButtonClick = newTitle -> {};
    private OnTitleResumeListener onTitleResumeListener = title -> {};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_rack_edit_name, container, false);
        mTitleText = root.findViewById(R.id.Rack_Config_Title_Text);
        mBackButton = root.findViewById(R.id.Rack_Config_Title_Back);
        mOkButton = root.findViewById(R.id.Rack_Config_Title_Ok);
        mBackButton.setOnClickListener(view -> onTitleBackClickListener.onTitleBackClickListener((SquareRackButton) view));
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
        void onTitleBackClickListener(SquareRackButton button);
    }

    public interface OnTitleRenameButtonClick {
        void onTitleRenameButtonClick(String newTitle);
    }

    public interface OnTitleResumeListener {
        void onTitleResumeListener(EditText title);
    }

}

