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

import com.firmino.rigkontrol.kontrollers.KButton;
import com.firmino.rigkontrol.kontrollers.KGate;
import com.firmino.rigkontrol.kontrollers.KSeekBar;
import com.firmino.rigkontrol.kontrollers.KStateButton;
import com.firmino.rigkontrol.kontrollers.Kontroller;
import com.firmino.rigkontrol.ktools.ConfirmationAlert;
import com.firmino.rigkontrol.midi.MidiKontroller;
import com.firmino.rigkontrol.presets.PresetItem;
import com.firmino.rigkontrol.presets.PresetListAdapter;

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

    //TODO: poder criar um preset vazio

    //Title
    private KGate mTitleGateKnob;
    private ImageView mTitleShowConfigButton;
    private ImageView mTitleExitButton;
    private KSeekBar mTitleVolumeOutSeekBar;
    private KSeekBar mTitleVolumeInSeekBar;

    //Options
    private Button mOptionsConnectButton;
    private KStateButton mOptionsShowStatusBar;
    private KStateButton mOptionsShowExtendedButtons;
    private LinearLayout mOptionsLayout;

    //Toolbox
    private ImageView mToolOpenSetupButton;
    private TextView mToolPresetName;
    private Button mToolSavePresetButton;
    private ImageView mToolNextPresetButton;
    private ImageView mToolPrevPresetButton;
    private KStateButton mToolKontrollerVisibleStateButton;

    //Status
    private ImageView mStatusImage;
    private TextView mStatusKontrollersText;
    private TextView mStatusConnectionText;
    private LinearLayout mStatusBarLayout;

    //Proprietes
    private boolean isConnected = false;
    boolean hasMidiSupport;
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
        mOptionsConnectButton = findViewById(R.id.Options_Connect);
        mOptionsShowExtendedButtons = findViewById(R.id.Options_ExpandButtons);
        mOptionsShowStatusBar = findViewById(R.id.Options_ShowStatusBar);
        mOptionsLayout = findViewById(R.id.Main_OptionsLayout);
        mKontroller = findViewById(R.id.Main_Kontroller);
        mDrawableStatusSucess = getResources().getDrawable(R.drawable.ic_sucess, null);
        mDrawableStatusFail = getResources().getDrawable(R.drawable.ic_fail, null);
        mToolOpenSetupButton = findViewById(R.id.Tool_OpenPreset);
        mToolPresetName = findViewById(R.id.Tool_Preset_Title);
        mToolSavePresetButton = findViewById(R.id.Tool_Save_Preset_Button);
        mToolKontrollerVisibleStateButton = findViewById(R.id.Tool_KontrollerVisible);
        mToolNextPresetButton = findViewById(R.id.Tool_Next_Preset);
        mToolPrevPresetButton = findViewById(R.id.Tool_Previous_Preset);
        hasMidiSupport = getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI);

        mTitleExitButton.setOnClickListener(view -> showCloseApplicationDialog());
        mTitleShowConfigButton.setOnClickListener(l -> setOptionsVisible());
        mToolOpenSetupButton.setOnClickListener(v -> showOpenSetupDialog());
        mToolPresetName.setOnClickListener(v -> showEditPresetNameDialog());
        mToolSavePresetButton.setOnClickListener(v -> savePreset());
        mToolKontrollerVisibleStateButton.setOnKStateButtonChangeListener((kStateButton, isOn) -> setLiveModeOn(isOn));

        setupMidi();
        setupOptionsPanel();
        loadPreferences();
    }

    @Override
    public void onBackPressed() {
        showCloseApplicationDialog();
    }

    private void setupMidi() {
        if (hasMidiSupport) {
            mMidi = new MidiKontroller(this);
            mOptionsConnectButton.setOnClickListener(v -> showConnectionDialog());
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
        for (KButton b : mKontroller.getButtons())
            b.setOnKButtonStateChangeListener((button, valueOn, valueOff, isOn, controllerNumber) -> mMidi.sendControlChange(controllerNumber, isOn ? valueOn : valueOff));
    }

    private void setLiveModeOn(boolean isOn) {
        LinearLayout main = findViewById(R.id.Container_Main);
        ScrollView racks = findViewById(R.id.Container_Racks);
        LinearLayout kontroller = findViewById(R.id.Container_Kontroller);
        int maxHeight = main.getHeight() - (main.getPaddingTop() * 2);
        int titleHeight = maxHeight - (int) (40 * getResources().getDisplayMetrics().density);
        ViewGroup.LayoutParams layoutParams = kontroller.getLayoutParams();
        layoutParams.height = maxHeight;
        kontroller.setLayoutParams(layoutParams);
        ValueAnimator anim = ValueAnimator.ofInt(isOn ? titleHeight : 0, isOn ? 0 : titleHeight);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParamsRacks = racks.getLayoutParams();
            layoutParamsRacks.height = (int) valueAnimator.getAnimatedValue();
            racks.setLayoutParams(layoutParamsRacks);
        });
        anim.setDuration(300);
        anim.start();
    }

    private void showCloseApplicationDialog() {
        new ConfirmationAlert(getString(R.string.warning), getString(R.string.close_warning), MainActivity.this) {
            @Override
            public void onConfirmClick() {
                getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("inVolume", mTitleVolumeInSeekBar.getValue()).apply();
                getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("outVolume", mTitleVolumeOutSeekBar.getValue()).apply();
                getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("gateLevel", mTitleGateKnob.getValue()).apply();
                getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("gateOn", mTitleGateKnob.isOn()).apply();
                MainActivity.this.finish();
            }
        };
    }

    private void showOpenSetupDialog() {
        View alertContent = getLayoutInflater().inflate(R.layout.dialog_open_preset, findViewById(R.id.Open_Preset_Main));
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertContent);
        Dialog dialog = alert.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (500 * getResources().getDisplayMetrics().density), -2);

        ListView listView = alertContent.findViewById(R.id.Open_Preset_ListView);
        PresetListAdapter adapter = new PresetListAdapter(this);
        listView.setAdapter(adapter);
        adapter.clear();
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Presets");
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

    private void loadPreferences() {
        mTitleVolumeInSeekBar.setValue(getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("inVolume", 100));
        mTitleVolumeOutSeekBar.setValue(getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("outVolume", 100));
        mTitleGateKnob.setValue(getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("gateLevel", 100));
        mTitleGateKnob.setOn(getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("gateOn", false));
    }

    private void showEditPresetNameDialog() {
        View alertContent = getLayoutInflater().inflate(R.layout.dialog_preset_name, findViewById(R.id.Preset_Name_Main));
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
                ((EditText) alertContent.findViewById(R.id.Preset_Name)).setError("Special characters are not allowed in preset name.");
            }
        });
    }

    private void savePreset() {
        final File outputFile = new File(Environment.getExternalStorageDirectory() + "/Presets/" + mToolPresetName.getText().toString() + ".xml");
        new ConfirmationAlert(getString(R.string.save_preset_title), outputFile.exists() ? getString(R.string.save_replace) : getString(R.string.save_preset)
                , this) {
            @Override
            public void onConfirmClick() {
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
                    for (KButton button : mKontroller.getButtons()) {
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
            }
        }.setOnConfirmationAlertCancel(() -> MessageAlert.create(MainActivity.this, MessageAlert.TYPE_ALERT, getString(R.string.save_canceled)));
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
                            MessageAlert.create(this, MessageAlert.TYPE_ERRO, "Requires version " + tag.getAttributeValue(null, "version"));
                            break;
                        }
                        mToolPresetName.setText(tag.getAttributeValue(null, "name"));
                    }

                    if (tag.getName().toLowerCase().startsWith("button")) {
                        KButton button = mKontroller.getButtons()[Integer.parseInt(String.valueOf(tag.getName().charAt(tag.getName().length() - 1)))];
                        button.setup(
                                tag.getAttributeValue(null, "title"),
                                Integer.parseInt(tag.getAttributeValue(null, "cc")),
                                Integer.parseInt(tag.getAttributeValue(null, "valueOn")),
                                Integer.parseInt(tag.getAttributeValue(null, "valueOff")),
                                tag.getAttributeValue(null, "holdmode").toLowerCase().equals("true")
                        );
                    }

                    if (tag.getName().toLowerCase().equals("pedalswitch")) {
                        KButton button = mKontroller.getButtonPedal();
                        button.setup(
                                tag.getAttributeValue(null, "title"),
                                Integer.parseInt(tag.getAttributeValue(null, "cc")),
                                Integer.parseInt(tag.getAttributeValue(null, "valueOn")),
                                Integer.parseInt(tag.getAttributeValue(null, "valueOff")),
                                tag.getAttributeValue(null, "holdmode").toLowerCase().equals("true")
                        );
                    }

                    if (tag.getName().toLowerCase().equals("pedal")) {
                        mKontroller.getSlider().setup(
                                tag.getAttributeValue(null, "title"),
                                Integer.parseInt(tag.getAttributeValue(null, "cc"))
                        );
                    }

                }
                if (tag.getEventType() == XmlPullParser.END_DOCUMENT) {
                    MessageAlert.create(this, MessageAlert.TYPE_SUCESS, "Load Sucessfull");
                    break;
                }
                tag.next();
            }
            is.close();
        } catch (XmlPullParserException e) {
            MessageAlert.create(this, MessageAlert.TYPE_ERRO, "The file is corrupted");
        } catch (IOException e) {
            MessageAlert.create(this, MessageAlert.TYPE_ERRO, "Unknow erro");
        }
    }

    private void showConnectionDialog() {
        if (isConnected) {
            disconnect();
        } else {
            View alertContent = getLayoutInflater().inflate(R.layout.dialog_connect, findViewById(R.id.Connection_Main));
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setView(alertContent);
            Dialog dialog = alert.show();
            Objects.requireNonNull(dialog.getWindow()).setLayout((int) (300 * getResources().getDisplayMetrics().density), -2);
            TextView usbStatus = alertContent.findViewById(R.id.Connection_USB_Status);
            TextView channel = alertContent.findViewById(R.id.Connection_Channel);
            Button chUp = alertContent.findViewById(R.id.Connection_Channel_Up);
            Button chDown = alertContent.findViewById(R.id.Connection_Channel_Down);
            Button connect = alertContent.findViewById(R.id.Connection_Connect);
            usbStatus.setText(mMidi.getMidi().getDevices().length > 0 ? "Connected" : "Not Connected");
            connect.setTextColor(getColor(mMidi.getMidi().getDevices().length > 0 ? R.color.white : android.R.color.holo_red_light));
            mMidi.getMidi().registerDeviceCallback(new MidiManager.DeviceCallback() {
                @Override
                public void onDeviceAdded(MidiDeviceInfo device) {
                    super.onDeviceAdded(device);
                    usbStatus.setText(mMidi.getMidi().getDevices().length > 0 ? "Connected" : "Not Connected");
                    connect.setTextColor(getColor(mMidi.getMidi().getDevices().length > 0 ? R.color.white : android.R.color.holo_red_light));
                }

                @Override
                public void onDeviceRemoved(MidiDeviceInfo device) {
                    super.onDeviceRemoved(device);
                    usbStatus.setText(mMidi.getMidi().getDevices().length > 0 ? "Connected" : "Not Connected");
                    connect.setTextColor(getColor(mMidi.getMidi().getDevices().length > 0 ? R.color.white : android.R.color.holo_red_light));
                }
            }, new Handler());
            chDown.setOnClickListener(v -> {
                if (Integer.parseInt(channel.getText().toString()) > 1)
                    channel.setText(String.valueOf(Integer.parseInt(channel.getText().toString()) - 1));
            });
            chUp.setOnClickListener(v -> {
                if (Integer.parseInt(channel.getText().toString()) < 16)
                    channel.setText(String.valueOf(Integer.parseInt(channel.getText().toString()) + 1));
            });
            connect.setOnClickListener(view -> {
                if (mMidi.getMidi().getDevices().length > 0 && Integer.parseInt(channel.getText().toString()) >= 1 && Integer.parseInt(channel.getText().toString()) <= 16) {
                    connect(Integer.parseInt(channel.getText().toString()));
                    dialog.dismiss();
                }
            });
        }
    }

    private void disconnect() {
        mMidi.disconnect();
        isConnected = false;
        mOptionsConnectButton.setText(R.string.offline);
        mOptionsConnectButton.setBackground(getDrawable(R.drawable.bg_button));
        mOptionsConnectButton.setTextColor(getColor(R.color.white));
    }

    private void connect(int channel) {
        if (mMidi.connect(channel)) {
            isConnected = true;
            mOptionsConnectButton.setText(R.string.online);
            mOptionsConnectButton.setBackground(getDrawable(R.drawable.bg_button_pressed));
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
        MessageAlert.create(this, (flag == MidiKontroller.MIDI_CONNECTION_ERRO || flag == MidiKontroller.MIDI_DISCONNECTION_ERRO) ? MessageAlert.TYPE_ERRO : MessageAlert.TYPE_SUCESS, status);
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

    private void setupOptionsPanel() {
        mOptionsShowExtendedButtons.setOnKStateButtonChangeListener((kStateButton, isOn) -> mKontroller.setExpandedMode(isOn));
        mOptionsShowStatusBar.setOnKStateButtonChangeListener((kStateButton, isOn) -> this.setStatusBarVisible(isOn));
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

    public void addStatusGeral(String status) {
        ((TextView) findViewById(R.id.Main_Status_Geral_Text)).setText(status);
        new Handler().postDelayed(() -> ((TextView) findViewById(R.id.Main_Status_Geral_Text)).setText(""), 5000);
    }

}