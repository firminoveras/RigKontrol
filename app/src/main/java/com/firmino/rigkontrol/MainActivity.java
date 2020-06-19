package com.firmino.rigkontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.firmino.rigkontrol.kontrollers.KButton;
import com.firmino.rigkontrol.kontrollers.KPedal;
import com.firmino.rigkontrol.kontrollers.KSlider;

public class MainActivity extends AppCompatActivity {

    ImageView mExpandButton;
    KSlider mSlider;
    KPedal mPedal;
    KButton[] mButton;
    KButton mButtonPedal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.lay).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mExpandButton = findViewById(R.id.Main_Bt_Expand);
        mButton = new KButton[]{
                findViewById(R.id.Main_BT1),
                findViewById(R.id.Main_BT2),
                findViewById(R.id.Main_BT3),
                findViewById(R.id.Main_BT4),
                findViewById(R.id.Main_BT5),
                findViewById(R.id.Main_BT6),
                findViewById(R.id.Main_BT7),
                findViewById(R.id.Main_BT8),
        };
        mSlider = findViewById(R.id.Main_Slider);
        mPedal = findViewById(R.id.Main_Pedal);
        mButtonPedal = findViewById(R.id.Main_BT_Pedal_Down);

        mPedal.setOnPedalValueChangeListener((pedal, value) -> {
            mSlider.setProgress(value);
            if (value > 120 && !mButtonPedal.isButtonEnabled()) {
                mButtonPedal.kontrollerButtonDown();
            } else if (value <= 120 && mButtonPedal.isButtonEnabled()) {
                mButtonPedal.kontrollerButtonUp();
            }
        });


        mButtonPedal.setPedalDownIcon(true);

        mExpandButton.setOnClickListener(v -> {
            for (KButton b : mButton) b.kontrollerInvertExpanded();
            mButtonPedal.kontrollerInvertExpanded();
            mSlider.setExpanded(!mSlider.isExpanded());
        });
    }
}