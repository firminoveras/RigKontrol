package com.firmino.rigkontrol;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.kinterface.alerts.ConfirmationAlert;
import com.firmino.rigkontrol.kinterface.alerts.MessageAlert;
import com.firmino.rigkontrol.kinterface.views.KGate;
import com.firmino.rigkontrol.kinterface.views.KNumberPicker;
import com.firmino.rigkontrol.kinterface.views.KSeekBar;
import com.firmino.rigkontrol.kinterface.views.KStateButton;
import com.firmino.rigkontrol.kontroller.KFootSwitch;
import com.firmino.rigkontrol.kontroller.Kontroller;
import com.firmino.rigkontrol.midi.MidiKontroller;
import com.firmino.rigkontrol.presets.PresetItem;
import com.firmino.rigkontrol.presets.PresetListAdapter;
import com.firmino.rigkontrol.racks.Rack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Title
    private KGate mTitleGateKnob;
    private ImageView mTitleShowConfigButton;
    private KSeekBar mTitleVolumeOutSeekBar;
    private KSeekBar mTitleVolumeInSeekBar;

    //Options
    private Button mOptionsConnectButton;
    private KStateButton mOptionsShowStatusBar;
    private KStateButton mOptionsShowExtendedButtons;
    private LinearLayout mOptionsLayout;

    //Toolbox
    private TextView mToolPresetName;
    private ImageView mToolNextPresetButton;
    private ImageView mToolPrevPresetButton;

    //Status
    private ImageView mStatusImage;
    private TextView mStatusKontrollersText;
    private TextView mStatusConnectionText;
    private LinearLayout mStatusBarLayout;

    //Proprietes
    private Drawable mDrawableStatusSucess;
    private Drawable mDrawableStatusFail;
    private MidiKontroller mMidi;
    private Kontroller mKontroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);
        findViewById(R.id.Main_Layout).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mStatusConnectionText = findViewById(R.id.Main_Connection_Status_Text);
        mStatusBarLayout = findViewById(R.id.Main_StatusBar);
        mStatusKontrollersText = findViewById(R.id.Main_Status_Text);
        mStatusImage = findViewById(R.id.Main_Status_Image);
        mTitleShowConfigButton = findViewById(R.id.Main_Show_Configs);
        mTitleVolumeInSeekBar = findViewById(R.id.Main_VolumeIn);
        mTitleVolumeOutSeekBar = findViewById(R.id.Main_VolumeOut);
        mTitleGateKnob = findViewById(R.id.Main_Gate);
        mOptionsConnectButton = findViewById(R.id.Options_Connect);
        mOptionsShowExtendedButtons = findViewById(R.id.Options_ExpandButtons);
        mOptionsShowStatusBar = findViewById(R.id.Options_ShowStatusBar);
        mOptionsLayout = findViewById(R.id.Main_OptionsLayout);
        mKontroller = findViewById(R.id.Main_Kontroller);
        mDrawableStatusSucess = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sucess, null);
        mDrawableStatusFail = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_fail, null);
        mToolPresetName = findViewById(R.id.Tool_Preset_Title);
        mToolNextPresetButton = findViewById(R.id.Tool_Next_Preset);
        mToolPrevPresetButton = findViewById(R.id.Tool_Previous_Preset);

        findViewById(R.id.Tool_Clear_Preset).setOnClickListener(v -> clearPreset());
        findViewById(R.id.Main_Exit).setOnClickListener(view -> showExitAppDialog());
        mTitleShowConfigButton.setOnClickListener(l -> setOptionsVisible());
        findViewById(R.id.Tool_OpenPreset).setOnClickListener(v -> showOpenSetupDialog());
        mToolPresetName.setOnClickListener(v -> showEditPresetNameDialog());
        findViewById(R.id.Tool_Save_Preset_Button).setOnClickListener(v -> savePreset());
        ((KStateButton) findViewById(R.id.Tool_KontrollerVisible)).setOnKStateButtonChangeListener((kStateButton, isOn) -> setLiveModeOn(isOn));

        setupMidi();
        setupOptionsPanel();
        loadPreferences();


        Rack rack = new Rack(this);
        rack.setOnRackMidiListener((controlChange, value) -> mMidi.sendControlChange(controlChange, value));
        ((LinearLayout) findViewById(R.id.Main_Racks)).addView(rack);

    }

    @Override
    public void onBackPressed() {
        showExitAppDialog();
    }

    private void setupMidi() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            mMidi = new MidiKontroller(this);
            mOptionsConnectButton.setOnClickListener(v -> showConnectionDialog());
            mMidi.setOnMidiMessageSendListener(new MidiKontroller.OnMidiMessageSendListener() {
                @Override
                public void onMidiMessageSendSucess(int channel, int control, int value) {
                    mStatusImage.setImageDrawable(mDrawableStatusSucess);
                    mStatusKontrollersText.setText(String.format(Locale.getDefault(), getString(R.string.status_send), control, value, channel));
                }

                @Override
                public void onMidiMessageSendFailed(int channel, int control, int value, int erroId) {
                    mStatusImage.setImageDrawable(mDrawableStatusFail);
                    mStatusKontrollersText.setText(String.format(Locale.getDefault(), getString(R.string.status_erro), control, value, channel, erroId == MidiKontroller.ERRO_NOT_CONNECTED ? "Sem conexão" : "Erro de IO"));
                }
            });
            mMidi.setOnMidiConnectionChangedListener(this::connectionChanged);
            mKontroller.setOnConnectLedClickListener(this::showConnectionDialog);
        } else {
            mOptionsConnectButton.setText(R.string.not_midi_suported);
        }
        mTitleGateKnob.setOnKGateEnabledListener((isOn, controllerNumber) -> mMidi.sendControlChange(controllerNumber, isOn ? MidiKontroller.ON : MidiKontroller.OFF));
        mTitleGateKnob.setOnKGateValueChangeListener((progress, controllerNumber) -> mMidi.sendControlChange(controllerNumber, progress));
        mToolNextPresetButton.setOnClickListener(v -> mMidi.sendControlChange(getResources().getInteger(R.integer.cc_next_preset), MidiKontroller.ON));
        mToolPrevPresetButton.setOnClickListener(v -> mMidi.sendControlChange(getResources().getInteger(R.integer.cc_prev_preset), MidiKontroller.ON));
        mTitleVolumeInSeekBar.setOnKSeekBarValueChangeListener((seekBar, value, controllerNumber) -> mMidi.sendControlChange(controllerNumber, value));
        mTitleVolumeOutSeekBar.setOnKSeekBarValueChangeListener((seekBar, value, controllerNumber) -> mMidi.sendControlChange(controllerNumber, value));
        mKontroller.getButtonPedal().setOnKButtonStateChangeListener((button, valueOn, valueOff, isOn, controllerNumber) -> mMidi.sendControlChange(controllerNumber, isOn ? valueOn : valueOff));
        mKontroller.getSlider().setOnKSliderProgressChangeListener((kSlider, progress, controllerNumber) -> mMidi.sendControlChange(controllerNumber, progress));
        for (KFootSwitch b : mKontroller.getButtons())
            b.setOnKButtonStateChangeListener((button, valueOn, valueOff, isOn, controllerNumber) -> mMidi.sendControlChange(controllerNumber, isOn ? valueOn : valueOff));
    }

    private void setupOptionsPanel() {
        mOptionsShowExtendedButtons.setOnKStateButtonChangeListener((kStateButton, isOn) -> mKontroller.setExpandedMode(isOn));
        mOptionsShowStatusBar.setOnKStateButtonChangeListener((kStateButton, isOn) -> this.setStatusBarVisible(isOn));
        ((SeekBar) findViewById(R.id.Options_PedalSensivity)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float sensivity = (float) seekBar.getProgress() / 100;
                if (sensivity < 0.1f) sensivity = 0.1f;
                mKontroller.getPedal().setSensivity(sensivity);
                MessageAlert.create(MainActivity.this, MessageAlert.TYPE_SUCESS, getString(R.string.set_sensibility) + sensivity);
            }
        });
    }

    private void setLiveModeOn(boolean on) {
        LinearLayout main = findViewById(R.id.Container_Main);
        ScrollView racks = findViewById(R.id.Container_Racks);
        LinearLayout kontroller = findViewById(R.id.Container_Kontroller);
        int maxHeight = main.getHeight() - (main.getPaddingTop() * 2);
        int titleHeight = maxHeight - (int) (40 * getResources().getDisplayMetrics().density);
        ViewGroup.LayoutParams layoutParams = kontroller.getLayoutParams();
        layoutParams.height = maxHeight;
        kontroller.setLayoutParams(layoutParams);
        ValueAnimator anim = ValueAnimator.ofInt(on ? titleHeight : 0, on ? 0 : titleHeight);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParamsRacks = racks.getLayoutParams();
            layoutParamsRacks.height = (int) valueAnimator.getAnimatedValue();
            racks.setLayoutParams(layoutParamsRacks);
        });
        anim.setDuration(300);
        anim.start();
    }

    private void setStatusBarVisible(boolean visible) {
        int max = (int) (20 * getResources().getDisplayMetrics().density);
        ValueAnimator anim = ValueAnimator.ofInt(visible ? 0 : max, visible ? max : 0);
        anim.setDuration(getResources().getInteger(R.integer.animation_duration_options));
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams l = mStatusBarLayout.getLayoutParams();
            l.height = (int) animation.getAnimatedValue();
            mStatusBarLayout.setLayoutParams(l);
        });
        anim.start();
    }

    private void setOptionsVisible() {
        mKontroller.setPedalVisible(!mTitleShowConfigButton.isPressed());
        int max = (int) (300 * getResources().getDisplayMetrics().density);
        ValueAnimator anim = ValueAnimator.ofInt(mTitleShowConfigButton.isPressed() ? 0 : max, mTitleShowConfigButton.isPressed() ? max : 0);
        anim.setDuration(300);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams l = mOptionsLayout.getLayoutParams();
            l.width = (int) animation.getAnimatedValue();
            mOptionsLayout.setLayoutParams(l);
        });
        anim.start();
    }

    private void showExitAppDialog() {
        new ConfirmationAlert(getString(R.string.warning), getString(R.string.close_warning), MainActivity.this).setOnConfirmationAlertConfirm(() -> {
            savePreferences();
            MainActivity.this.finishAndRemoveTask();
            System.exit(0);
        });
    }

    private void showOpenSetupDialog() {
        View alertContent = getLayoutInflater().inflate(R.layout.layout_main_dialog_openpreset, findViewById(R.id.Open_Preset_Main));
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertContent);
        Dialog dialog = alert.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (500 * getResources().getDisplayMetrics().density), -2);
        ListView listView = alertContent.findViewById(R.id.Open_Preset_ListView);
        PresetListAdapter adapter = new PresetListAdapter(this);
        listView.setAdapter(adapter);
        adapter.clear();
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Rig Kontrol" + File.separator + "Presets");
        for (File preset : Objects.requireNonNull(dir.listFiles())) {
            if (preset.getAbsolutePath().endsWith(".xml")) {
                PresetItem item = new PresetItem(MainActivity.this);
                item.setup(preset);
                item.setOnClickListener(view -> {
                    loadPreset(((PresetItem) view).getFile());
                    dialog.dismiss();
                });
                adapter.addAll(item);
            }
        }
    }

    private void showEditPresetNameDialog() {
        View alertContent = getLayoutInflater().inflate(R.layout.layout_main_dialog_presetname, findViewById(R.id.Preset_Name_Main));
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertContent);
        Dialog dialog = alert.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (300 * getResources().getDisplayMetrics().density), -2);
        ((EditText) alertContent.findViewById(R.id.Preset_Name)).setText(mToolPresetName.getText());
        alertContent.findViewById(R.id.Preset_OK).setOnClickListener(v -> {
            if (((EditText) alertContent.findViewById(R.id.Preset_Name)).getText().toString().matches("[A-Za-z0-9 ]+")) {
                mToolPresetName.setText(((EditText) alertContent.findViewById(R.id.Preset_Name)).getText());
                dialog.dismiss();
            } else {
                ((EditText) alertContent.findViewById(R.id.Preset_Name)).setError(getString(R.string.special_character));
            }
        });
    }

    private void showConnectionDialog() {
        if (mMidi.isConnected()) {
            disconnect();
        } else {
            View alertContent = getLayoutInflater().inflate(R.layout.layout_main_dialog_connect, findViewById(R.id.Connection_Main));
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setView(alertContent);
            Dialog dialog = alert.show();
            Objects.requireNonNull(dialog.getWindow()).setLayout((int) (300 * getResources().getDisplayMetrics().density), -2);
            TextView usbStatus = alertContent.findViewById(R.id.Connection_USB_Status);
            Button connect = alertContent.findViewById(R.id.Connection_Connect);
            usbStatus.setText(mMidi.getMidi().getDevices().length > 0 ? getString(R.string.connected) : getString(R.string.not_connected));
            connect.setTextColor(getColor(mMidi.getMidi().getDevices().length > 0 ? R.color.white : android.R.color.holo_red_light));
            mMidi.getMidi().registerDeviceCallback(new MidiManager.DeviceCallback() {
                @Override
                public void onDeviceAdded(MidiDeviceInfo device) {
                    super.onDeviceAdded(device);
                    usbStatus.setText(mMidi.getMidi().getDevices().length > 0 ? getString(R.string.connected) : getString(R.string.not_connected));
                    connect.setTextColor(getColor(mMidi.getMidi().getDevices().length > 0 ? R.color.white : android.R.color.holo_red_light));
                }

                @Override
                public void onDeviceRemoved(MidiDeviceInfo device) {
                    super.onDeviceRemoved(device);
                    usbStatus.setText(mMidi.getMidi().getDevices().length > 0 ? getString(R.string.connected) : getString(R.string.not_connected));
                    connect.setTextColor(getColor(mMidi.getMidi().getDevices().length > 0 ? R.color.white : android.R.color.holo_red_light));
                }
            }, new Handler());
            connect.setOnClickListener(view -> {
                if (mMidi.getMidi().getDevices().length > 0) {
                    connect(((KNumberPicker) alertContent.findViewById(R.id.Connection_Channel)).getValue());
                    dialog.dismiss();
                }
            });
        }
    }

    private void savePreferences() {
        getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("inVolume", mTitleVolumeInSeekBar.getValue()).apply();
        getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("outVolume", mTitleVolumeOutSeekBar.getValue()).apply();
        getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("gateLevel", mTitleGateKnob.getValue()).apply();
        getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("gateOn", mTitleGateKnob.isOn()).apply();
    }

    private void loadPreferences() {
        mTitleVolumeInSeekBar.setValue(getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("inVolume", 100));
        mTitleVolumeOutSeekBar.setValue(getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("outVolume", 100));
        mTitleGateKnob.setValue(getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("gateLevel", 100));
        mTitleGateKnob.setOn(getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("gateOn", false));
    }

    private void clearPreset() {
        new ConfirmationAlert(getString(R.string.warning), getString(R.string.clear_warning), this).setOnConfirmationAlertConfirm(() -> {
            int buttonIndex = 0;
            for (KFootSwitch button : mKontroller.getButtons()) {
                button.setup(String.valueOf(buttonIndex + 1), buttonIndex + 1, MidiKontroller.ON, MidiKontroller.OFF, false);
                buttonIndex++;
            }
            mKontroller.getSlider().setup("0", 0);
            mKontroller.getButtonPedal().setup("9", 9, MidiKontroller.ON, MidiKontroller.OFF, false);
            mToolPresetName.setText(R.string.untitled);
            MessageAlert.create(MainActivity.this, MessageAlert.TYPE_SUCESS, getString(R.string.preset_cleaned));
        });
    }

    private void savePreset() {
        final File outputFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Rig Kontrol" + File.separator + "Presets" + File.separator + mToolPresetName.getText().toString() + ".xml");
        new ConfirmationAlert(getString(R.string.save_preset_title), outputFile.exists() ? getString(R.string.save_replace) : getString(R.string.save_preset), this).setOnConfirmationAlertConfirm(() -> {
            XmlSerializer xml = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            try {
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                xml.setOutput(writer);
                xml.startDocument("UTF-8", false);
                xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

                xml.startTag(null, "Kontroller");
                xml.attribute(null, "name", mToolPresetName.getText().toString());
                xml.attribute(null, "version", BuildConfig.VERSION_NAME);
                int buttonNumber = 0;
                for (KFootSwitch button : mKontroller.getButtons()) {
                    xml.startTag(null, "Button" + buttonNumber);
                    xml.attribute(null, "title", button.getKDescription());
                    xml.attribute(null, "holdmode", String.valueOf(button.isToggle()));
                    xml.attribute(null, "cc", String.valueOf(button.getKControllerNumber()));
                    xml.attribute(null, "valueOn", String.valueOf(button.getKValueOn()));
                    xml.attribute(null, "valueOff", String.valueOf(button.getKValueOff()));
                    xml.endTag(null, "Button" + buttonNumber);
                    buttonNumber++;
                }

                xml.startTag(null, "PedalSwitch");
                xml.attribute(null, "title", mKontroller.getButtonPedal().getKDescription());
                xml.attribute(null, "holdmode", String.valueOf(mKontroller.getButtonPedal().isToggle()));
                xml.attribute(null, "cc", String.valueOf(mKontroller.getButtonPedal().getKControllerNumber()));
                xml.attribute(null, "valueOn", String.valueOf(mKontroller.getButtonPedal().getKValueOn()));
                xml.attribute(null, "valueOff", String.valueOf(mKontroller.getButtonPedal().getKValueOff()));
                xml.endTag(null, "PedalSwitch");

                xml.startTag(null, "Pedal");
                xml.attribute(null, "title", String.valueOf(mKontroller.getSlider().getDesciption()));
                xml.attribute(null, "cc", String.valueOf(mKontroller.getSlider().getControllerNumber()));
                xml.endTag(null, "Pedal");

                xml.endTag(null, "Kontroller");

                xml.endDocument();
                xml.flush();
                outputStream.write(writer.toString().getBytes());
                outputStream.close();
                MessageAlert.create(MainActivity.this, MessageAlert.TYPE_SUCESS, getString(R.string.saved_sucess));
            } catch (FileNotFoundException ex) {
                MessageAlert.create(MainActivity.this, MessageAlert.TYPE_ERRO, getString(R.string.directory_not_found));
            } catch (IOException ex) {
                MessageAlert.create(MainActivity.this, MessageAlert.TYPE_ERRO, getString(R.string.erro_save_preset));
            }
        }).setOnConfirmationAlertCancel(() -> MessageAlert.create(MainActivity.this, MessageAlert.TYPE_ALERT, getString(R.string.save_canceled)));
    }

    private void loadPreset(File loadFile) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser tag = factory.newPullParser();
            InputStream is = new FileInputStream(loadFile);
            tag.setInput(is, null);
            while (true) {
                if (tag.getEventType() == XmlPullParser.START_TAG) {

                    if (tag.getName().toLowerCase().equals("kontroller")) {
                        if (Integer.parseInt(tag.getAttributeValue(null, "version").replace(".", "")) > Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", ""))) {
                            MessageAlert.create(this, MessageAlert.TYPE_ERRO, getString(R.string.requires_version) + tag.getAttributeValue(null, "version"));
                            break;
                        }
                        mToolPresetName.setText(tag.getAttributeValue(null, "name"));
                    }

                    if (tag.getName().toLowerCase().startsWith("button")) {
                        KFootSwitch button = mKontroller.getButtons()[Integer.parseInt(String.valueOf(tag.getName().charAt(tag.getName().length() - 1)))];
                        button.setup(tag.getAttributeValue(null, "title"), Integer.parseInt(tag.getAttributeValue(null, "cc")), Integer.parseInt(tag.getAttributeValue(null, "valueOn")), Integer.parseInt(tag.getAttributeValue(null, "valueOff")), tag.getAttributeValue(null, "holdmode").toLowerCase().equals("true"));
                    }

                    if (tag.getName().toLowerCase().equals("pedalswitch")) {
                        KFootSwitch button = mKontroller.getButtonPedal();
                        button.setup(tag.getAttributeValue(null, "title"), Integer.parseInt(tag.getAttributeValue(null, "cc")), Integer.parseInt(tag.getAttributeValue(null, "valueOn")), Integer.parseInt(tag.getAttributeValue(null, "valueOff")), tag.getAttributeValue(null, "holdmode").toLowerCase().equals("true"));
                    }

                    if (tag.getName().toLowerCase().equals("pedal")) {
                        mKontroller.getSlider().setup(tag.getAttributeValue(null, "title"), Integer.parseInt(tag.getAttributeValue(null, "cc")));
                    }

                }
                if (tag.getEventType() == XmlPullParser.END_DOCUMENT) {
                    MessageAlert.create(this, MessageAlert.TYPE_SUCESS, getString(R.string.load_sucessfull));
                    break;
                }
                tag.next();
            }
            is.close();
        } catch (XmlPullParserException e) {
            MessageAlert.create(this, MessageAlert.TYPE_ERRO, getString(R.string.file_corrupt));
        } catch (IOException e) {
            MessageAlert.create(this, MessageAlert.TYPE_ERRO, getString(R.string.unknow_erro));
        }
    }

    private void connect(int channel) {
        if (mMidi.connect(channel)) {
            mOptionsConnectButton.setText(R.string.online);
            mOptionsConnectButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_pressed, null));
            mOptionsConnectButton.setTextColor(getColor(R.color.bg_dark_gray));
            mMidi.getMidi().registerDeviceCallback(new MidiManager.DeviceCallback() {
                @Override
                public void onDeviceRemoved(MidiDeviceInfo device) {
                    super.onDeviceRemoved(device);
                    disconnect();
                }
            }, new Handler());
        } else {
            disconnect();
        }
    }

    private void disconnect() {
        mMidi.disconnect();
        mOptionsConnectButton.setText(R.string.offline);
        mOptionsConnectButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button, null));
        mOptionsConnectButton.setTextColor(getColor(R.color.white));
    }

    private void connectionChanged(boolean isConnected, int flag) {
        mKontroller.setConnectionLedOn(isConnected);
        String status = "";
        switch (flag) {
            case MidiKontroller.MIDI_CONNECTION_ERRO:
                status = getString(R.string.connection_erro);
                break;
            case MidiKontroller.MIDI_CONNECTION_SUCESS:
                status = getString(R.string.connection_sucess);
                break;
            case MidiKontroller.MIDI_DISCONNECTION_ERRO:
                status = getString(R.string.disconnection_erro);
                break;
            case MidiKontroller.MIDI_DISCONNECTION_SUCESS:
                status = getString(R.string.disconnection_sucess);
                break;
        }
        ((ImageView) findViewById(R.id.Main_Connection_Status_Image)).setImageDrawable((flag == MidiKontroller.MIDI_CONNECTION_ERRO || flag == MidiKontroller.MIDI_DISCONNECTION_ERRO) ? mDrawableStatusFail : mDrawableStatusSucess);
        mStatusConnectionText.setText(String.format("%s: %s", isConnected ? getString(R.string.connected_up) : getString(R.string.not_connected_up), status));
        MessageAlert.create(this, (flag == MidiKontroller.MIDI_CONNECTION_ERRO || flag == MidiKontroller.MIDI_DISCONNECTION_ERRO) ? MessageAlert.TYPE_ERRO : MessageAlert.TYPE_SUCESS, status);
    }
}