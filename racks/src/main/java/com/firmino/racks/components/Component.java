package com.firmino.racks.components;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.firmino.racks.R;

public class Component extends FrameLayout {

    private final Context mContext;
    private ImageView mConfigButton, mRemoveButton;

    public Component(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        inflate(mContext, R.layout.layout_component, this);
        mConfigButton = findViewById(R.id.Component_Config);
        mRemoveButton = findViewById(R.id.Component_Remove);

        /*
        mRemoveButton.setOnClickListener(view -> new ConfirmationAlert("Deleting a Component", getContext().getString(R.string.delete_component), mContext) {
            @Override
            public void onConfirmClick() {
                ((ViewGroup)getParent()).removeView(Component.this);
            }
        });

         */
    }
}
