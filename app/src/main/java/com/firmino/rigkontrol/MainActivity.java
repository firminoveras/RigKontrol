package com.firmino.rigkontrol;

import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firmino.rigkontrol.kontrollers.KButton;
import com.firmino.rigkontrol.kontrollers.KGate;
import com.firmino.rigkontrol.kontrollers.KSeekBar;
import com.firmino.rigkontrol.kontrollers.KStateButton;
import com.firmino.rigkontrol.kontrollers.Kontroller;
import com.firmino.rigkontrol.midi.MidiKontroller;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView mStatusImage, mShowConfigButton;
    private TextView mStatusText, mOptionsUsbStatus;
    private Drawable mDrawableStatusSucess, mDrawableStatusFail;
    private Kontroller mKontroller;
    private KGate mGate;
    private MidiKontroller mMidi;
    private KSeekBar mVolumeIn, mVolumeOut;
    private LinearLayout mOptionsLayout, mStatusBar;
    private boolean isOptionsVisible;
    private KStateButton mShowStatusBar, mShowExtendedButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.lay).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mOptionsUsbStatus = findViewById(R.id.Options_USB_Status);
        mStatusBar = findViewById(R.id.Main_StatusBar);
        mShowConfigButton = findViewById(R.id.Main_Show_Configs);
        mOptionsLayout = findViewById(R.id.Main_OptionsLayout);
        mVolumeIn = findViewById(R.id.Main_VolumeIn);
        mVolumeOut = findViewById(R.id.Main_VolumeOut);
        mGate = findViewById(R.id.Main_Gate);
        mKontroller = findViewById(R.id.Main_Kontroller);
        mStatusImage = findViewById(R.id.Main_Status_Image);
        mStatusText = findViewById(R.id.Main_Status_Text);
        mShowExtendedButtons = findViewById(R.id.Options_ExpandButtons);
        mShowStatusBar = findViewById(R.id.Options_ShowStatusBar);

        isOptionsVisible = false;

        mShowExtendedButtons.setOnKStateButtonChangeListener((kStateButton, isOn) -> mKontroller.setExpandedMode(isOn));
        mShowStatusBar.setOnKStateButtonChangeListener((kStateButton, isOn) -> this.setStatusBarVisible(isOn));

        mDrawableStatusSucess = getResources().getDrawable(R.drawable.ic_sucess, null);
        mDrawableStatusFail = getResources().getDrawable(R.drawable.ic_erro, null);

        mShowConfigButton.setOnClickListener(l -> {
            setOptionsVisible(!isOptionsVisible);
            l.setBackground(getDrawable(this.isOptionsVisible ? R.drawable.bg_button_pressed : R.drawable.bg_button));
            ((ImageView) l).setImageTintList(ColorStateList.valueOf(getResources().getColor(this.isOptionsVisible ? R.color.bg_dark_gray : R.color.white, null)));
        });

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            mMidi = new MidiKontroller(this);
            mMidi.getMidi().registerDeviceCallback(new MidiManager.DeviceCallback(){

                public void onDeviceAdded(MidiDeviceInfo device) {
                    mOptionsUsbStatus.setText(R.string.NOT_PLUGGED);
                }

                public void onDeviceRemoved(MidiDeviceInfo device) {
                    mOptionsUsbStatus.setText(R.string.READY);
                }
            }, new Handler());
        }else{
            mOptionsUsbStatus.setText(R.string.INCOMPATIBLE);
        }

        mMidi.connect(1);
        mMidi.setOnMidiMessageSendListener(new MidiKontroller.OnMidiMessageSendListener() {
            @Override
            public void onMidiMessageSendSucess(int channel, int control, int value) {
                mStatusImage.setImageDrawable(mDrawableStatusSucess);
                mStatusText.setText(String.format(Locale.getDefault(), "Enviado: [ CC = %03d | VL = %03d ] -> CH%02d", control, value, channel));
            }

            @Override
            public void onMidiMessageSendFailed(int channel, int control, int value, int erroId) {
                mStatusImage.setImageDrawable(mDrawableStatusFail);
                mStatusText.setText(String.format(Locale.getDefault(), "Falhado: [ CC = %03d | VL = %03d ] -> CH%02d (%s)", control, value, channel, erroId == MidiKontroller.ERRO_NOT_CONNECTED ? "Sem conexão" : "Erro de IO"));
            }
        });

        mGate.setOnKGateListener(new KGate.OnKGateListener() {
            @Override
            public void onKGateEnabledListener(KGate kGate, boolean isOn, int controllerNumber) {
                mMidi.sendControlChange(controllerNumber, isOn ? 127 : 0);
            }

            @Override
            public void onKGateValueChangeListener(KGate kGate, int progress, int controllerNumber) {
                mMidi.sendControlChange(controllerNumber, progress);
            }
        });
        mVolumeIn.setOnKSeekBarValueChangeListener((seekBar, value, controllerNumber) -> mMidi.sendControlChange(controllerNumber, value));
        mVolumeOut.setOnKSeekBarValueChangeListener((seekBar, value, controllerNumber) -> mMidi.sendControlChange(controllerNumber, value));

        for (KButton b : mKontroller.getButtons()) {
            b.setOnKButtonStateChangeListener((button, valueOn, valueOff, isOn, controllerNumber) -> mMidi.sendControlChange(controllerNumber, isOn ? valueOn : valueOff));
        }
        mKontroller.getButtonPedal().setOnKButtonStateChangeListener((button, valueOn, valueOff, isOn, controllerNumber) -> mMidi.sendControlChange(controllerNumber, isOn ? valueOn : valueOff));
        mKontroller.getSlider().setOnKSliderProgressChangeListener((kSlider, progress, controllerNumber) -> mMidi.sendControlChange(controllerNumber, progress));
    }

    private void setStatusBarVisible(boolean visible) {
        int max = (int) (20 * getResources().getDisplayMetrics().density);
        ValueAnimator anim = ValueAnimator.ofInt(visible ? 0 : max, visible ? max : 0);
        anim.setDuration(300);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams l = mStatusBar.getLayoutParams();
            l.height = (int) animation.getAnimatedValue();
            mStatusBar.setLayoutParams(l);
        });
        anim.start();
    }

    private void setOptionsVisible(boolean visible) {
        isOptionsVisible = visible;
        int max = (int) (300 * getResources().getDisplayMetrics().density);
        ValueAnimator anim = ValueAnimator.ofInt(visible ? 0 : max, visible ? max : 0);
        anim.setDuration(300);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams l = mOptionsLayout.getLayoutParams();
            l.width = (int) animation.getAnimatedValue();
            mOptionsLayout.setLayoutParams(l);
        });
        anim.start();
    }

}