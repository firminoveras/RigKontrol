package com.firmino.racks.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import androidx.annotation.NonNull;

import com.firmino.racks.R;
import com.firmino.rigalerts.ConfirmationAlert;

public class Component extends FrameLayout {

    private final Context mContext;
    private ImageView mConfigButton, mRemoveButton;
    private RelativeLayout mConfigLayout;
    private LinearLayout mComponentContainer;
    private OnConfigButtonClickedListener onConfigButtonClickedListener = () -> {};

    public Component(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        inflate(mContext, R.layout.layout_component, this);
        mConfigButton = findViewById(R.id.Component_Config);
        mRemoveButton = findViewById(R.id.Component_Remove);
        mConfigLayout = findViewById(R.id.Component_Config_Layout);
        mComponentContainer = findViewById(R.id.Component_container);

        setLayoutParams(new TableLayout.LayoutParams((int) getResources().getDimension(R.dimen.zero), ViewGroup.LayoutParams.MATCH_PARENT, 1));

        mRemoveButton.setOnClickListener(view -> new ConfirmationAlert("Deleting a Component", getContext().getString(R.string.delete_component), mContext) {
            @Override
            public void onConfirmClick() {
                ((ViewGroup) getParent()).removeView(Component.this);
            }
        });

        mConfigButton.setOnClickListener(view -> onConfigButtonClickedListener.onConfigButtonClickedListener());


    }

    public void setConfigModeEnabled(boolean enabled) {
        mConfigLayout.setEnabled(enabled);
        mConfigButton.setEnabled(enabled);
        mRemoveButton.setEnabled(enabled);
        ValueAnimator anim = ValueAnimator.ofFloat(enabled ? 0 : 1.0f, enabled ? 1.0f : 0);
        anim.addUpdateListener(valueAnimator -> mConfigLayout.setAlpha((Float) valueAnimator.getAnimatedValue()));
        anim.setDuration(500);
        anim.start();
    }

    public LinearLayout getContainer() {
        return mComponentContainer;
    }

    public interface OnConfigButtonClickedListener{
        void onConfigButtonClickedListener();
    }
}
