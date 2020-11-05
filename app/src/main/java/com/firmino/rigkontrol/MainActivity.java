package com.firmino.rigkontrol;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.kinterface.alerts.ConfirmationAlert;
import com.firmino.rigkontrol.kinterface.alerts.MessageAlert;
import com.firmino.rigkontrol.kinterface.views.KButton;
import com.firmino.rigkontrol.kinterface.views.KImageButton;
import com.firmino.rigkontrol.kinterface.views.KListPicker;
import com.firmino.rigkontrol.kinterface.views.KNumberPicker;
import com.firmino.rigkontrol.kinterface.views.KSeekBar;
import com.firmino.rigkontrol.kinterface.views.KStateButton;
import com.firmino.rigkontrol.kontroller.KFootSwitch;
import com.firmino.rigkontrol.kontroller.Kontroller;
import com.firmino.rigkontrol.midi.MidiKontroller;
import com.firmino.rigkontrol.presets.PresetItem;
import com.firmino.rigkontrol.presets.PresetListAdapter;
import com.firmino.rigkontrol.racks.Rack;
import com.firmino.rigkontrol.racks.RackItem;
import com.firmino.rigkontrol.racks.adapter.RackListAdapter;
import com.firmino.rigkontrol.racks.components.Component;
import com.firmino.rigkontrol.racks.components.Potentiometer;
import com.firmino.rigkontrol.racks.components.PushButton;

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
    private KButton mTitleGateButton;
    private KImageButton mTitleShowConfigButton;
    private KSeekBar mTitleVolumeOutSeekBar;
    private KSeekBar mTitleVolumeInSeekBar;

    //Options
    private Button mOptionsConnectButton;
    private KStateButton mOptionsShowStatusBar;
    private KStateButton mOptionsShowExtendedButtons;
    private LinearLayout mOptionsLayout;

    //Toolbox
    private TextView mToolPresetName;
    private KImageButton mToolNextPresetButton;
    private KImageButton mToolPrevPresetButton;
    private KImageButton mToolLiveMode;
    private KImageButton mToolAddRack;

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
    private LinearLayout mRacks;

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
        mTitleGateButton = findViewById(R.id.Main_Gate);
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
        mToolLiveMode = findViewById(R.id.Tool_LiveMode);
        mToolAddRack = findViewById(R.id.Tool_Add_Rack);
        mRacks = findViewById(R.id.Container_Racks);

        findViewById(R.id.Tool_Clear_Preset).setOnClickListener(v -> clearPreset());
        findViewById(R.id.Main_Exit).setOnClickListener(view -> showExitAppDialog());
        mTitleShowConfigButton.setOnKImageButtonListener(this::setOptionsVisible);
        findViewById(R.id.Tool_OpenPreset).setOnClickListener(v -> showOpenPresetDialog());
        mToolPresetName.setOnClickListener(v -> showEditPresetNameDialog());
        findViewById(R.id.Tool_Save_Preset_Button).setOnClickListener(v -> savePreset());
        mToolLiveMode.setOnKImageButtonListener(this::setLiveMode);
        mToolAddRack.setOnClickListener(view -> showNewRackDialog());

        setupMidi();
        setupOptionsPanel();
        loadPreferences();
    }

    @Override
    public void onBackPressed() {
        showExitAppDialog();
    }


    private void addRack(Rack rack) {
        if (mRacks.getChildCount() < 15) {
            rack.setOnRackMidiListener((controlChange, value) -> mMidi.sendControlChange(controlChange, value));
            rack.setOnRackDeleteButtonClickListener(() -> new ConfirmationAlert(getString(R.string.title_delete_rack), getString(R.string.message_delete_rack), this).setOnConfirmationAlertConfirm(() -> mRacks.removeView(rack)));
            mRacks.addView(rack);
        } else {
            MessageAlert.create(this, MessageAlert.TYPE_ALERT, getString(R.string.max_number_racks_reached));
        }
    }

    private void addNewRack() {
        if (mRacks.getChildCount() < 15) {
            Rack rack = new Rack(this);
            rack.setOnRackMidiListener((controlChange, value) -> mMidi.sendControlChange(controlChange, value));
            rack.setOnRackDeleteButtonClickListener(() -> new ConfirmationAlert(getString(R.string.title_delete_rack), getString(R.string.message_delete_rack), this).setOnConfirmationAlertConfirm(() -> mRacks.removeView(rack)));
            mRacks.addView(rack);
        } else {
            MessageAlert.create(this, MessageAlert.TYPE_ALERT, getString(R.string.max_number_racks_reached));
        }
    }

    private void setLiveMode(boolean pressed) {
        mKontroller.setVisibility(pressed ? View.VISIBLE : View.GONE);
        mRacks.setVisibility(pressed ? View.GONE : View.VISIBLE);
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
        mTitleGateButton.setOnKButtonListener((isOn) -> mMidi.sendControlChange(getResources().getInteger(R.integer.cc_gate_on), isOn ? MidiKontroller.ON : MidiKontroller.OFF));
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

    private void setStatusBarVisible(boolean visible) {
        int max = (int) (20 * getResources().getDisplayMetrics().density);
        ValueAnimator anim = ValueAnimator.ofInt(visible ? 1 : max, visible ? max : 1);
        anim.setDuration(getResources().getInteger(R.integer.animation_duration_options));
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams l = mStatusBarLayout.getLayoutParams();
            l.height = (int) animation.getAnimatedValue();
            mStatusBarLayout.setLayoutParams(l);
        });
        if(visible){
            mStatusBarLayout.setVisibility(View.VISIBLE);
        }else{
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mStatusBarLayout.setVisibility(View.GONE);
                }
            });
        }

        anim.start();
    }

    private void setOptionsVisible(boolean visible) {
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

    private void showExitAppDialog() {
        new ConfirmationAlert(getString(R.string.warning), getString(R.string.close_warning), MainActivity.this).setOnConfirmationAlertConfirm(() -> {
            savePreferences();
            MainActivity.this.finishAndRemoveTask();
            System.exit(0);
        });
    }

    private void showNewRackDialog() {
        View alertContent = getLayoutInflater().inflate(R.layout.layout_main_dialog_newrack, findViewById(R.id.New_Rack_Main));
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setView(alertContent);
        Dialog dialog = alert.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (500 * getResources().getDisplayMetrics().density), -2);
        ListView listView = alertContent.findViewById(R.id.New_Rack_ListView);
        KListPicker filter = alertContent.findViewById(R.id.New_Rack_Category_Filter);
        RackListAdapter adapter = new RackListAdapter(this);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setDividerHeight((int) getResources().getDimension(R.dimen._4dp));
        adapter.clear();
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Rig Kontrol" + File.separator + "Racks");
        filter.setOnKListPikerItemSelectedListener((index, item) -> {
            adapter.clear();
            for (File preset : Objects.requireNonNull(dir.listFiles())) {
                if (preset.getAbsolutePath().endsWith(".xml")) {
                    RackItem rackFiltered = new RackItem(this);
                    rackFiltered.setup(preset);
                    if (rackFiltered.getCategory().equals(item) || item.equals(getResources().getStringArray(R.array.rack_categories)[0])) {
                        rackFiltered.setOnClickListener(view -> {
                            Rack newRack = new Rack(this);
                            newRack.loadRack(((RackItem) view).getFile());
                            addRack(newRack);
                            dialog.dismiss();
                        });
                        adapter.addAll(rackFiltered);
                    }
                }
            }
        });
        filter.setSelectedItem(0);
        alertContent.findViewById(R.id.New_Rack_Add_Button).setOnClickListener(v -> {
            addNewRack();
            dialog.dismiss();
        });
    }

    private void showOpenPresetDialog() {
        View alertContent = getLayoutInflater().inflate(R.layout.layout_main_dialog_openpreset, findViewById(R.id.Open_Preset_Main));
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertContent);
        Dialog dialog = alert.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (500 * getResources().getDisplayMetrics().density), -2);
        ListView listView = alertContent.findViewById(R.id.Open_Preset_ListView);
        PresetListAdapter adapter = new PresetListAdapter(this);
        listView.setDivider(null);
        listView.setDividerHeight((int) getResources().getDimension(R.dimen._2dp));
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
            connect.setTextColor(getColor(mMidi.getMidi().getDevices().length > 0 ? android.R.color.white : android.R.color.holo_red_light));
            mMidi.getMidi().registerDeviceCallback(new MidiManager.DeviceCallback() {
                @Override
                public void onDeviceAdded(MidiDeviceInfo device) {
                    super.onDeviceAdded(device);
                    usbStatus.setText(mMidi.getMidi().getDevices().length > 0 ? getString(R.string.connected) : getString(R.string.not_connected));
                    connect.setTextColor(getColor(mMidi.getMidi().getDevices().length > 0 ? android.R.color.white : android.R.color.holo_red_light));
                }

                @Override
                public void onDeviceRemoved(MidiDeviceInfo device) {
                    super.onDeviceRemoved(device);
                    usbStatus.setText(mMidi.getMidi().getDevices().length > 0 ? getString(R.string.connected) : getString(R.string.not_connected));
                    connect.setTextColor(getColor(mMidi.getMidi().getDevices().length > 0 ? android.R.color.white : android.R.color.holo_red_light));
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
        getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("gateOn", mTitleGateButton.isOn()).apply();
    }

    private void loadPreferences() {
        mTitleVolumeInSeekBar.setValue(getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("inVolume", 100));
        mTitleVolumeOutSeekBar.setValue(getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("outVolume", 100));
        mTitleGateButton.setOn(getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("gateOn", false));
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
            mRacks.removeAllViews();
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

                for (int rackIndex = 0; rackIndex < mRacks.getChildCount(); rackIndex++) {
                    Rack rack = (Rack) mRacks.getChildAt(rackIndex);
                    xml.startTag(null, "rack");
                    xml.attribute(null, "title", rack.getRackTitle());
                    xml.attribute(null, "cc", String.valueOf(rack.getControlChange()));
                    xml.attribute(null, "backgroundColor", String.valueOf(rack.getBackgroundColor().getDefaultColor()));
                    xml.attribute(null, "foregroundColor", String.valueOf(rack.getForegroundColor().getDefaultColor()));
                    xml.attribute(null, "version", BuildConfig.VERSION_NAME);

                    for (Component component : rack.getComponents()) {
                        String type = "";
                        switch (component.getType()) {
                            case Component.TYPE_POTENTIOMETER:
                                type = "potentiometer";
                                break;
                            case Component.TYPE_PUSH_BUTTON:
                                type = "push_button";
                                break;
                            case Component.TYPE_SLIDE:
                                type = "slide";
                                break;
                        }
                        xml.startTag(null, type);
                        xml.attribute(null, "cc", String.valueOf(component.getControlChange()));
                        xml.attribute(null, "title", component.getTitle());
                        if (component.getType() == Component.TYPE_POTENTIOMETER) {
                            xml.attribute(null, "knob_style", String.valueOf(((Potentiometer) component).getKnobStyle()));
                            xml.attribute(null, "markers_style", String.valueOf(((Potentiometer) component).getMarkerStyle()));
                        }
                        if (component.getType() == Component.TYPE_PUSH_BUTTON) xml.attribute(null, "button_style", String.valueOf(((PushButton) component).getStyle()));
                        xml.endTag(null, type);
                    }

                    xml.endTag(null, "rack");
                }

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
            mRacks.removeAllViews();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser tag = factory.newPullParser();
            InputStream is = new FileInputStream(loadFile);
            tag.setInput(is, null);
            while (tag.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (tag.getEventType() == XmlPullParser.START_TAG) {

                    if (tag.getName().toLowerCase().startsWith("kontroller")) {
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

                    if (tag.getName().toLowerCase().equals("rack")) {
                        Rack newRack = new Rack(this);
                        newRack.setRackTitle(tag.getAttributeValue(null, "title"));
                        newRack.setControlChange(Integer.parseInt((tag.getAttributeValue(null, "cc"))));
                        newRack.setRackBackgroundColor(ColorStateList.valueOf(Integer.parseInt(tag.getAttributeValue(null, "backgroundColor"))));
                        newRack.setRackForegroundColor(ColorStateList.valueOf(Integer.parseInt(tag.getAttributeValue(null, "foregroundColor"))));
                        while (true) {
                            if (tag.getEventType() == XmlPullParser.START_TAG) {
                                if (tag.getName() != null && tag.getName().toLowerCase().startsWith("potentiometer")) {
                                    Potentiometer newPot = new Potentiometer(this, newRack.getForegroundColor());
                                    newPot.setControlChange(Integer.parseInt(tag.getAttributeValue(null, "cc")));
                                    newPot.setTitle(tag.getAttributeValue(null, "title"));
                                    newPot.setKnobStyle(null, Integer.parseInt(tag.getAttributeValue(null, "knob_style")));
                                    newPot.setMarkersStyle(null, Integer.parseInt(tag.getAttributeValue(null, "markers_style")));
                                    newRack.addNewComponent(newPot);
                                }
                                if (tag.getName() != null && tag.getName().toLowerCase().startsWith("push_button")) {
                                    PushButton newPushButton = new PushButton(this, newRack.getForegroundColor());
                                    newPushButton.setControlChange(Integer.parseInt(tag.getAttributeValue(null, "cc")));
                                    newPushButton.setTitle(tag.getAttributeValue(null, "title"));
                                    newPushButton.setPushButtonStyle(Integer.parseInt(tag.getAttributeValue(null, "button_style")));
                                    newRack.addNewComponent(newPushButton);
                                }
                            } else if (tag.getEventType() == XmlPullParser.END_TAG && tag.getName() != null && tag.getName().toLowerCase().equals("rack")) break;
                            tag.next();
                        }
                        addRack(newRack);
                    }

                }
                tag.next();
            }
            MessageAlert.create(this, MessageAlert.TYPE_SUCESS, getString(R.string.load_sucessfull));
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
            mOptionsConnectButton.setTextColor(getColor(R.color.background_dark));
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
        mOptionsConnectButton.setTextColor(getColor(android.R.color.white));
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