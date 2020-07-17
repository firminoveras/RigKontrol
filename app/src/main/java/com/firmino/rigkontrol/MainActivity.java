package com.firmino.rigkontrol;

import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firmino.rigkontrol.kontrollers.KButton;
import com.firmino.rigkontrol.kontrollers.KGate;
import com.firmino.rigkontrol.kontrollers.KSeekBar;
import com.firmino.rigkontrol.kontrollers.KStateButton;
import com.firmino.rigkontrol.kontrollers.Kontroller;
import com.firmino.rigkontrol.midi.MidiKontroller;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Title
    private KGate mTitleGateKnob;
    private ImageView mTitleShowConfigButton;
    private ImageView mTitleExitButton;
    private KSeekBar mTitleVolumeOutSeekBar;
    private KSeekBar mTitleVolumeInSeekBar;

    //Options
    private Button mOptionsChannelUpButton;
    private Button mOptionsChannelDownButton;
    private Button mOptionsConnectButton;
    private TextView mOptionsUsbStatusText;
    private TextView mOptionsChannelText;
    private KStateButton mOptionsShowStatusBar;
    private KStateButton mOptionsShowExtendedButtons;
    private LinearLayout mOptionsLayout;

    //Status
    private ImageView mStatusImage;
    private TextView mStatusKontrollersText;
    private TextView mStatusConnectionText;
    private LinearLayout mStatusBarLayout;

    //Proprietes
    private boolean isOptionsVisible;
    private int mChannel = 1;
    private Drawable mDrawableStatusSucess;
    private Drawable mDrawableStatusFail;
    private MidiKontroller mMidi;
    private Kontroller mKontroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.Main_Layout).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mStatusConnectionText = findViewById(R.id.Main_Connection_Status_Text);
        mStatusBarLayout = findViewById(R.id.Main_StatusBar);
        mStatusKontrollersText = findViewById(R.id.Main_Status_Text);
        mStatusImage = findViewById(R.id.Main_Status_Image);
        mTitleExitButton = findViewById(R.id.Main_Exit);
        mTitleShowConfigButton = findViewById(R.id.Main_Show_Configs);
        mTitleVolumeInSeekBar = findViewById(R.id.Main_VolumeIn);
        mTitleVolumeOutSeekBar = findViewById(R.id.Main_VolumeOut);
        mTitleGateKnob = findViewById(R.id.Main_Gate);
        mOptionsShowExtendedButtons = findViewById(R.id.Options_ExpandButtons);
        mOptionsShowStatusBar = findViewById(R.id.Options_ShowStatusBar);
        mOptionsConnectButton = findViewById(R.id.Options_connect);
        mOptionsChannelText = findViewById(R.id.Options_ch);
        mOptionsChannelUpButton = findViewById(R.id.Options_ch_up);
        mOptionsChannelDownButton = findViewById(R.id.Options_ch_down);
        mOptionsUsbStatusText = findViewById(R.id.Options_USB_Status);
        mOptionsLayout = findViewById(R.id.Main_OptionsLayout);
        mKontroller = findViewById(R.id.Main_Kontroller);
        isOptionsVisible = false;

        mTitleExitButton.setOnClickListener(view -> {
            new ConfirmationAlert("WARNING", getString(R.string.close_warning), MainActivity.this) {
                @Override
                public void onConfirmClick() {
                    MainActivity.this.finish();
                }

                @Override
                public void onCancelClick() {

                }
            };
        });

        mOptionsChannelDownButton.setOnClickListener(view -> {
            if (mChannel > 1 && !mMidi.isConnected()) mChannel--;
            mOptionsChannelText.setText(String.valueOf(mChannel));
        });
        mOptionsChannelUpButton.setOnClickListener(view -> {
            if (mChannel < 16 && !mMidi.isConnected()) mChannel++;
            mOptionsChannelText.setText(String.valueOf(mChannel));
        });
        mOptionsShowExtendedButtons.setOnKStateButtonChangeListener((kStateButton, isOn) -> mKontroller.setExpandedMode(isOn));
        mOptionsShowStatusBar.setOnKStateButtonChangeListener((kStateButton, isOn) -> this.setStatusBarVisible(isOn));

        mDrawableStatusSucess = getResources().getDrawable(R.drawable.ic_sucess, null);
        mDrawableStatusFail = getResources().getDrawable(R.drawable.ic_erro, null);

        mTitleShowConfigButton.setOnClickListener(l -> {
            setOptionsVisible(!isOptionsVisible);
            l.setBackground(getDrawable(this.isOptionsVisible ? R.drawable.bg_button_pressed : R.drawable.bg_button));
            ((ImageView) l).setImageTintList(ColorStateList.valueOf(getResources().getColor(this.isOptionsVisible ? R.color.bg_dark_gray : R.color.white, null)));
        });

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            mMidi = new MidiKontroller(this);
            updateUsbStatus();
            mMidi.getMidi().registerDeviceCallback(new MidiManager.DeviceCallback() {

                @Override
                public void onDeviceAdded(MidiDeviceInfo device) {
                    updateUsbStatus();
                }

                @Override
                public void onDeviceRemoved(MidiDeviceInfo device) {
                    mMidi.disconnect();
                    updateConnectButton(false);
                    updateUsbStatus();
                }
            }, new Handler());
            mMidi.setOnMidiConnectionChangedListener(this::connectionChanged);
            mOptionsConnectButton.setOnClickListener(view -> connect());
        } else {
            mOptionsUsbStatusText.setText(R.string.INCOMPATIBLE);
            mOptionsConnectButton.setText(R.string.usb_not_campatible);
            setConnectionOptionsEnabled(false);
        }

        mMidi.setOnMidiMessageSendListener(new MidiKontroller.OnMidiMessageSendListener() {
            @Override
            public void onMidiMessageSendSucess(int channel, int control, int value) {
                mStatusImage.setImageDrawable(mDrawableStatusSucess);
                mStatusKontrollersText.setText(String.format(Locale.getDefault(), "Enviado: [ CC = %03d | VL = %03d ] -> CH%02d", control, value, channel));
            }

            @Override
            public void onMidiMessageSendFailed(int channel, int control, int value, int erroId) {
                mStatusImage.setImageDrawable(mDrawableStatusFail);
                mStatusKontrollersText.setText(String.format(Locale.getDefault(), "Falhado: [ CC = %03d | VL = %03d ] -> CH%02d (%s)", control, value, channel, erroId == MidiKontroller.ERRO_NOT_CONNECTED ? "Sem conexão" : "Erro de IO"));
            }
        });

        mTitleGateKnob.setOnKGateListener(new KGate.OnKGateListener() {
            @Override
            public void onKGateEnabledListener(boolean isOn, int controllerNumber) {
                mMidi.sendControlChange(controllerNumber, isOn ? 127 : 0);
            }

            @Override
            public void onKGateValueChangeListener(int progress, int controllerNumber) {
                mMidi.sendControlChange(controllerNumber, progress);
            }
        });
        mTitleVolumeInSeekBar.setOnKSeekBarValueChangeListener((seekBar, value, controllerNumber) -> mMidi.sendControlChange(controllerNumber, value));
        mTitleVolumeOutSeekBar.setOnKSeekBarValueChangeListener((seekBar, value, controllerNumber) -> mMidi.sendControlChange(controllerNumber, value));
        mKontroller.getButtonPedal().setOnKButtonStateChangeListener((button, valueOn, valueOff, isOn, controllerNumber) -> mMidi.sendControlChange(controllerNumber, isOn ? valueOn : valueOff));
        mKontroller.getSlider().setOnKSliderProgressChangeListener((kSlider, progress, controllerNumber) -> mMidi.sendControlChange(controllerNumber, progress));
        for (KButton b : mKontroller.getButtons()) {
            b.setOnKButtonStateChangeListener((button, valueOn, valueOff, isOn, controllerNumber) -> mMidi.sendControlChange(controllerNumber, isOn ? valueOn : valueOff));
        }


        ((SeekBar) findViewById(R.id.Options_PedalSensivity)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float sensivity = (float) i / 100;
                if (sensivity < 0.1f) sensivity = 0.1f;
                mKontroller.getPedal().setSensivity(sensivity);
                addStatusGeral("Sensivity: " + sensivity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void connect() {
        if (mMidi.isConnected()) {
            mMidi.disconnect();
            updateConnectButton(false);
        } else {
            if (mMidi.connect(mChannel)) {
                updateConnectButton(true);
            }
        }
    }

    private void connectionChanged(boolean isConnected, int flag) {
        mKontroller.setConnectionLedOn(isConnected);
        String status = "";
        switch (flag) {
            case MidiKontroller.MIDI_CONNECTION_ERRO:
                status = "Connection erro.";
                break;
            case MidiKontroller.MIDI_CONNECTION_SUCESS:
                status = "Connection sucess";
                break;
            case MidiKontroller.MIDI_DISCONNECTION_ERRO:
                status = "Disconnection erro";
                break;
            case MidiKontroller.MIDI_DISCONNECTION_SUCESS:
                status = "Disconnection sucess";
                break;
        }
        ((ImageView) findViewById(R.id.Main_Connection_Status_Image)).setImageDrawable((flag == MidiKontroller.MIDI_CONNECTION_ERRO || flag == MidiKontroller.MIDI_DISCONNECTION_ERRO) ? mDrawableStatusFail : mDrawableStatusSucess);
        mStatusConnectionText.setText(String.format("%s: %s", isConnected ? "CONNECTED" : "NOT CONNECTED", status));
    }

    private void updateConnectButton(boolean connected) {
        mOptionsConnectButton.setBackground(getResources().getDrawable(connected ? R.drawable.bg_button_right_borderless_pressed : R.drawable.bg_button_right_borderless, null));
        mOptionsConnectButton.setText(connected ? "ONLINE" : "OFFLINE");
        mOptionsConnectButton.setTextColor(connected ? getResources().getColor(R.color.bg_dark_gray, null) : Color.WHITE);
    }

    private void updateUsbStatus() {
        boolean active = mMidi.getMidi().getDevices().length > 0;
        mOptionsUsbStatusText.setText(active ? R.string.READY : R.string.NOT_PLUGGED);
        setConnectionOptionsEnabled(active);
    }

    private void setConnectionOptionsEnabled(boolean enabled) {
        mOptionsConnectButton.setEnabled(enabled);
        mOptionsChannelDownButton.setEnabled(enabled);
        mOptionsChannelUpButton.setEnabled(enabled);
    }

    private void setStatusBarVisible(boolean visible) {
        int max = (int) (20 * getResources().getDisplayMetrics().density);
        ValueAnimator anim = ValueAnimator.ofInt(visible ? 0 : max, visible ? max : 0);
        anim.setDuration(300);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams l = mStatusBarLayout.getLayoutParams();
            l.height = (int) animation.getAnimatedValue();
            mStatusBarLayout.setLayoutParams(l);
        });
        anim.start();
    }

    private void setOptionsVisible(boolean visible) {
        isOptionsVisible = visible;
        mKontroller.setPedalVisible(!visible);
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

    public void addStatusGeral(String status) {
        ((TextView) findViewById(R.id.Main_Status_Geral_Text)).setText(status);
        new Handler().postDelayed(() -> ((TextView) findViewById(R.id.Main_Status_Geral_Text)).setText(""), 5000);
    }

}