package com.firmino.rigkontrol.racks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.firmino.rigkontrol.BuildConfig;
import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.kinterface.alerts.ConfirmationAlert;
import com.firmino.rigkontrol.kinterface.alerts.MessageAlert;
import com.firmino.rigkontrol.kinterface.views.KButton;
import com.firmino.rigkontrol.kinterface.views.KListPicker;
import com.firmino.rigkontrol.kinterface.views.KRoundImageButton;
import com.firmino.rigkontrol.kinterface.views.KTextEdit;
import com.firmino.rigkontrol.midi.MidiKontroller;
import com.firmino.rigkontrol.racks.adapter.RackListAdapter;
import com.firmino.rigkontrol.racks.components.Component;
import com.firmino.rigkontrol.racks.components.Potentiometer;
import com.firmino.rigkontrol.racks.components.PushButton;
import com.firmino.rigkontrol.racks.components.Slider;
import com.firmino.rigkontrol.racks.frags.ConfigFragmentAdapter;
import com.firmino.rigkontrol.racks.frags.FragmentAddConfig;
import com.firmino.rigkontrol.racks.frags.FragmentColorsConfig;
import com.firmino.rigkontrol.racks.frags.FragmentMainConfig;
import com.firmino.rigkontrol.racks.frags.FragmentMidiConfig;
import com.firmino.rigkontrol.racks.frags.FragmentTitleConfig;

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
import java.util.Objects;

public class Rack extends RelativeLayout {

    private final Context mContext;
    private KRoundImageButton mPowerButton, mMinimizeButton, mConfigButton;
    private LinearLayout mComponentsLayout;
    private ViewPager2 mConfigPager;
    private ConfigFragmentAdapter mConfigFragAdapter;
    private View mSaveButton, mLoadButton, mSaveLoadLayout;
    private TextView mLittleTitle, mBigTitle;
    private OnRackMidiListener onRackMidiListener = (cc, value) -> {};
    private OnRackDeleteButtonClickListener onRackDeleteButtonClickListener = () -> {};
    private boolean isMinimized = false, isConfigModeOn = false;
    private int mControlChange = 0;
    private ColorStateList mForegroundColor, mBackgroundColor;

    private FragmentMainConfig mFragMain;
    private FragmentTitleConfig mFragTitle;
    private FragmentColorsConfig mFragColors;
    private FragmentMidiConfig mFragMidi;
    private FragmentAddConfig mFragAdd;

    public Rack(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public Rack(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        inflate(mContext, R.layout.layout_rack, this);
        mBackgroundColor = ColorStateList.valueOf(Color.parseColor("#C5C5C5"));
        mForegroundColor = ColorStateList.valueOf(Color.parseColor("#232323"));

        mConfigPager = findViewById(R.id.Rack_Config_Pager);
        mSaveButton = findViewById(R.id.Rack_Save_Layout);
        mLoadButton = findViewById(R.id.Rack_Load_Layout);
        mComponentsLayout = findViewById(R.id.Rack_Container_Layout);
        mMinimizeButton = findViewById(R.id.Rack_MinimizeButton);
        mPowerButton = findViewById(R.id.Rack_OnButton);
        mConfigButton = findViewById(R.id.Rack_ConfigButton);
        mLittleTitle = findViewById(R.id.Rack_Little_Title);
        mBigTitle = findViewById(R.id.Rack_Big_Title);
        mSaveLoadLayout = findViewById(R.id.Rack_Save_Load_Layout);

        mFragMain = new FragmentMainConfig();
        mFragTitle = new FragmentTitleConfig();
        mFragColors = new FragmentColorsConfig();
        mFragMidi = new FragmentMidiConfig();
        mFragAdd = new FragmentAddConfig();

        mConfigFragAdapter = new ConfigFragmentAdapter(((FragmentActivity) mContext));
        mConfigFragAdapter.add(mFragMain);
        mConfigFragAdapter.add(mFragTitle);
        mConfigFragAdapter.add(mFragColors);
        mConfigFragAdapter.add(mFragMidi);
        mConfigFragAdapter.add(mFragAdd);

        mConfigPager.setAdapter(mConfigFragAdapter);

        mFragMain.setOnEditNameClickListener(button -> mConfigPager.setCurrentItem(1, true));
        mFragMain.setOnEditColorsClickListener(button -> mConfigPager.setCurrentItem(2, true));
        mFragMain.setOnMidiConfigClickListener(button -> mConfigPager.setCurrentItem(3, true));
        mFragMain.setOnAddClickListener(button -> mConfigPager.setCurrentItem(4, true));
        mFragMain.setOnDeleteRacklickListener(button -> onRackDeleteButtonClickListener.onRackDeleteButtonClickListener());

        mFragTitle.setOnTitleBackClickListener(button -> mConfigPager.setCurrentItem(0, true));
        mFragTitle.setOnTitleResumeListener(title -> title.setText(getRackTitle()));
        mFragTitle.setOnTitleRenameButtonClick(this::setRackTitle);

        mFragColors.setOnColorsBackClickListener(() -> mConfigPager.setCurrentItem(0, true));
        mFragColors.setOnColorsBackgroundColorPickedListener(this::setRackBackgroundColor);
        mFragColors.setOnColorsForegroundColorPickedListener(this::setRackForegroundColor);

        mFragMidi.setOnMidiBackClickListener(() -> mConfigPager.setCurrentItem(0, true));
        mFragMidi.setOnMidiChangeListener(cc -> mControlChange = cc);
        mFragMidi.setOnMidiResumeListener(textView -> textView.setText(String.valueOf(mControlChange)));

        mFragAdd.setOnAddBackButtonClickListener(() -> mConfigPager.setCurrentItem(0, true));
        mFragAdd.setOnAddPotButtonClickListener(() -> addNewComponent(new Potentiometer(mContext, mForegroundColor)));
        mFragAdd.setOnAddButtonButtonClickListener(() -> addNewComponent(new PushButton(mContext, mForegroundColor)));
        mFragAdd.setOnAddSliderButtonClickListener(() -> addNewComponent(new Slider(mContext, mForegroundColor)));

        mPowerButton.setToggle(true);
        mPowerButton.setOnRackButtonClicked((isOn) -> onRackMidiListener.onRackOnListener(mControlChange, MidiKontroller.toMidiSignal(isOn)));

        mMinimizeButton.setToggle(false);
        mMinimizeButton.setOnRackButtonClicked((isOn) -> {
            if (isOn) setMinimizedMode(!isMinimized);
        });

        mConfigButton.setToggle(true);
        mConfigButton.setOnRackButtonClicked(this::setConfigModeEnabled);

        mSaveButton.setOnClickListener(view -> showSaveRackDialog());
        mLoadButton.setOnClickListener(v -> showLoadRackDialog());

        mConfigPager.setVisibility(GONE);
    }


    public void setConfigModeEnabled(boolean enabled) {
        isConfigModeOn = enabled;
        if (isConfigModeOn) mConfigPager.setVisibility(VISIBLE);
        for (int index = 0; index < mComponentsLayout.getChildCount(); index++) {
            ((Component) mComponentsLayout.getChildAt(index)).setConfigModeEnabled(isConfigModeOn);
        }
        int maxH = (int) (getResources().getDimension(R.dimen._80dp));
        ValueAnimator anim = ValueAnimator.ofInt(enabled ? 0 : maxH, enabled ? maxH : 0);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams lay = mConfigPager.getLayoutParams();
            lay.height = (int) valueAnimator.getAnimatedValue();
            mConfigPager.setLayoutParams(lay);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isConfigModeOn) {
                    mConfigPager.setVisibility(GONE);
                    mConfigPager.setCurrentItem(0, false);
                }
            }
        });
        anim.setDuration(300);
        anim.start();
    }

    public boolean isConfigModeOn() {
        return isConfigModeOn;
    }


    public void setMinimizedMode(boolean enabled) {
        if (isMinimized != enabled) {
            isMinimized = enabled;
            mMinimizeButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), enabled ? R.drawable.ic_rack_maximize : R.drawable.ic_rack_minimize, null));
            if (enabled && mConfigButton.isOn()) mConfigButton.performClick();
            mSaveLoadLayout.setVisibility(enabled ? View.GONE : View.VISIBLE);
            mComponentsLayout.setVisibility(enabled ? View.GONE : View.VISIBLE);
            mConfigButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
            ViewGroup.LayoutParams lay = findViewById(R.id.Rack_InnerLayout).getLayoutParams();
            lay.height = enabled ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen._100dp);
            findViewById(R.id.Rack_InnerLayout).setLayoutParams(lay);
        }
    }


    public void setRackTitle(String title) {
        if (title.matches("[A-Za-z0-9 ]+")) {
            mLittleTitle.setText("");
            mBigTitle.setText("");
            String[] names = title.split(" ");
            if (names.length > 1) {
                StringBuilder littleText = new StringBuilder();
                for (int index = 0; index < names.length - 1; index++)
                    littleText.append(names[index]).append(" ");
                mLittleTitle.setText(littleText.toString().trim());
                mBigTitle.setText(names[names.length - 1]);
            } else mBigTitle.setText(names[0]);
        }
    }

    public String getRackTitle() {
        String title;
        if (mLittleTitle.getText().length() > 0) {
            title = (String.format("%s %s", mLittleTitle.getText().toString(), mBigTitle.getText().toString()));
        } else {
            title = (mBigTitle.getText().toString());
        }
        return title;
    }


    private void showSaveRackDialog() {
        View alertContent = inflate(mContext, R.layout.layout_rack_dialog_saverack, findViewById(R.id.Save_Preset_Main));
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setCancelable(true);
        alert.setView(alertContent);
        Dialog dialog = alert.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (500 * getResources().getDisplayMetrics().density), -2);
        KTextEdit title = alertContent.findViewById(R.id.Open_Save_Rack_Title);
        KListPicker category = alertContent.findViewById(R.id.Open_Save_Rack_Category);
        KButton saveButton = alertContent.findViewById(R.id.Open_Save_Rack_Button_OK);
        saveButton.setOnClickListener(view -> {
            saveRack(title.getText(), category.getSelectedItem());
            dialog.dismiss();
        });
    }

    private void showLoadRackDialog() {
        View alertContent = inflate(mContext, R.layout.layout_rack_dialog_loadrack, findViewById(R.id.Open_Save_Rack_Main));
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setCancelable(true);
        alert.setView(alertContent);
        Dialog dialog = alert.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (500 * getResources().getDisplayMetrics().density), -2);
        ListView listView = alertContent.findViewById(R.id.Open_Save_Rack_ListView);
        KListPicker filter = alertContent.findViewById(R.id.Open_Save_Rack_Category_Filter);
        RackListAdapter adapter = new RackListAdapter(mContext);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setDividerHeight((int) getResources().getDimension(R.dimen._4dp));
        adapter.clear();
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Rig Kontrol" + File.separator + "Racks");
        filter.setOnKListPikerItemSelectedListener((index, item) -> {
            adapter.clear();
            for (File preset : Objects.requireNonNull(dir.listFiles())) {
                if (preset.getAbsolutePath().endsWith(".xml")) {
                    RackItem rackFiltered = new RackItem(mContext);
                    rackFiltered.setup(preset);
                    if (rackFiltered.getCategory().equals(item) || item.equals(getResources().getStringArray(R.array.rack_categories)[0])) {
                        rackFiltered.setOnClickListener(view -> {
                            loadRack(((RackItem) view).getFile());
                            dialog.dismiss();
                        });
                        adapter.addAll(rackFiltered);
                    }
                }
            }
        });
        filter.setSelectedItem(0);
    }

    private void saveRack(String name, String category) {
        final File outputFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Rig Kontrol" + File.separator + "Racks" + File.separator + name + ".xml");
        new ConfirmationAlert(mContext.getString(R.string.save_preset_title), outputFile.exists() ? mContext.getString(R.string.save_replace) : mContext.getString(R.string.save_preset), mContext).setOnConfirmationAlertConfirm(() -> {
            XmlSerializer xml = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            try {
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                xml.setOutput(writer);
                xml.startDocument("UTF-8", false);
                xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

                xml.startTag(null, "rack");
                xml.attribute(null, "title", getRackTitle());
                xml.attribute(null, "cc", String.valueOf(mControlChange));
                xml.attribute(null, "backgroundColor", String.valueOf(mBackgroundColor.getDefaultColor()));
                xml.attribute(null, "foregroundColor", String.valueOf(mForegroundColor.getDefaultColor()));
                xml.attribute(null, "category", category);
                xml.attribute(null, "version", BuildConfig.VERSION_NAME);

                for (int i = 0; i < mComponentsLayout.getChildCount(); i++) {
                    Component child = (Component) mComponentsLayout.getChildAt(i);
                    String type = "";
                    switch (child.getType()) {
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
                    xml.attribute(null, "cc", String.valueOf(child.getControlChange()));
                    xml.attribute(null, "title", child.getTitle());
                    if (child.getType() == Component.TYPE_POTENTIOMETER) {
                        xml.attribute(null, "knob_style", String.valueOf(((Potentiometer) child).getKnobStyle()));
                        xml.attribute(null, "markers_style", String.valueOf(((Potentiometer) child).getMarkerStyle()));
                    }
                    if (child.getType() == Component.TYPE_PUSH_BUTTON) xml.attribute(null, "button_style", String.valueOf(((PushButton) child).getStyle()));
                    xml.endTag(null, type);
                }

                xml.endTag(null, "rack");

                xml.endDocument();
                xml.flush();
                outputStream.write(writer.toString().getBytes());
                outputStream.close();
                MessageAlert.create(mContext, MessageAlert.TYPE_SUCESS, mContext.getString(R.string.saved_sucess));
            } catch (FileNotFoundException ex) {
                MessageAlert.create(mContext, MessageAlert.TYPE_ERRO, mContext.getString(R.string.directory_not_found));
            } catch (IOException ex) {
                MessageAlert.create(mContext, MessageAlert.TYPE_ERRO, mContext.getString(R.string.erro_save_preset));
            }
        }).setOnConfirmationAlertCancel(() -> MessageAlert.create(mContext, MessageAlert.TYPE_ALERT, mContext.getString(R.string.save_canceled)));
    }

    public void loadRack(File file) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser tag = factory.newPullParser();
            InputStream is = new FileInputStream(file);
            tag.setInput(is, null);
            removeAllComponents();
            while (true) {
                if (tag.getEventType() == XmlPullParser.START_TAG) {
                    if (tag.getName().toLowerCase().equals("rack")) {
                        if (Integer.parseInt(tag.getAttributeValue(null, "version").replace(".", "")) > Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", ""))) {
                            MessageAlert.create(mContext, MessageAlert.TYPE_ERRO, mContext.getString(R.string.requires_version) + tag.getAttributeValue(null, "version"));
                            break;
                        }
                        setRackTitle(tag.getAttributeValue(null, "title"));
                        mControlChange = Integer.parseInt((tag.getAttributeValue(null, "cc")));
                        setRackBackgroundColor(ColorStateList.valueOf(Integer.parseInt(tag.getAttributeValue(null, "backgroundColor"))));
                        setRackForegroundColor(ColorStateList.valueOf(Integer.parseInt(tag.getAttributeValue(null, "foregroundColor"))));
                    }
                    if (tag.getName().toLowerCase().startsWith("potentiometer")) {
                        Potentiometer newPot = new Potentiometer(mContext, mForegroundColor);
                        newPot.setControlChange(Integer.parseInt(tag.getAttributeValue(null, "cc")));
                        newPot.setTitle(tag.getAttributeValue(null, "title"));
                        newPot.setKnobStyle(null, Integer.parseInt(tag.getAttributeValue(null, "knob_style")));
                        newPot.setMarkersStyle(null, Integer.parseInt(tag.getAttributeValue(null, "markers_style")));
                        addNewComponent(newPot);
                    }
                    if (tag.getName().toLowerCase().startsWith("push_button")) {
                        PushButton newPushButton = new PushButton(mContext, mForegroundColor);
                        newPushButton.setControlChange(Integer.parseInt(tag.getAttributeValue(null, "cc")));
                        newPushButton.setTitle(tag.getAttributeValue(null, "title"));
                        newPushButton.setPushButtonStyle(Integer.parseInt(tag.getAttributeValue(null, "button_style")));
                        addNewComponent(newPushButton);
                    }
                    //TODO: fazer essa parte do slide
                }
                if (tag.getEventType() == XmlPullParser.END_DOCUMENT) {
                    MessageAlert.create(mContext, MessageAlert.TYPE_SUCESS, mContext.getString(R.string.load_sucessfull));
                    break;
                }
                tag.next();
            }
            is.close();
            ((TextView) mSaveButton).setText(file.getName().replace(".xml", ""));
        } catch (XmlPullParserException e) {
            MessageAlert.create(mContext, MessageAlert.TYPE_ERRO, mContext.getString(R.string.file_corrupt));
        } catch (IOException e) {
            MessageAlert.create(mContext, MessageAlert.TYPE_ERRO, mContext.getString(R.string.unknow_erro));
        }
    }


    public void addNewComponent(Component component) {
        if (mComponentsLayout.getChildCount() < 10) {
            component.setOnComponentMidiControlChangeListener((cc, value) -> onRackMidiListener.onRackOnListener(cc, value));
            component.setConfigModeEnabled(isConfigModeOn);
            mComponentsLayout.addView(component);
        }
    }

    public void removeAllComponents() {
        mComponentsLayout.removeAllViews();
    }

    public Component[] getComponents() {
        Component[] components = new Component[mComponentsLayout.getChildCount()];
        for (int index = 0; index < mComponentsLayout.getChildCount(); index++) {
            components[index] = (Component) mComponentsLayout.getChildAt(index);
        }
        return components;
    }

    public int getComponentCount() {
        return mComponentsLayout.getChildCount();
    }


    public void setControlChange(int cc) {
        mControlChange = cc;
    }

    public int getControlChange() {
        return mControlChange;
    }


    public void setRackColor(int backgroundColor, int foregroundColor) {
        findViewById(R.id.Rack_Background).setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        mBigTitle.setTextColor(foregroundColor);
        mLittleTitle.setTextColor(foregroundColor);
        findViewById(R.id.Rack_InnerLayout).setBackgroundTintList(ColorStateList.valueOf(foregroundColor));
    }

    public void setRackBackgroundColor(ColorStateList color) {
        mBackgroundColor = color;
        findViewById(R.id.Rack_Background).setBackgroundTintList(color);
    }

    public ColorStateList getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setRackForegroundColor(ColorStateList color) {
        mForegroundColor = color;
        mBigTitle.setTextColor(color);
        mLittleTitle.setTextColor(color);
        findViewById(R.id.Rack_InnerLayout).setBackgroundTintList(color);
        for (int index = 0; index < mComponentsLayout.getChildCount(); index++) {
            ((Component) mComponentsLayout.getChildAt(index)).setForegroundColor(color);
        }
    }

    public ColorStateList getForegroundColor() {
        return mForegroundColor;
    }


    public void setOnRackMidiListener(OnRackMidiListener onRackMidiListener) {
        this.onRackMidiListener = onRackMidiListener;
    }

    public void setOnRackDeleteButtonClickListener(OnRackDeleteButtonClickListener onRackDeleteButtonClickListener) {
        this.onRackDeleteButtonClickListener = onRackDeleteButtonClickListener;
    }

    public interface OnRackMidiListener {
        void onRackOnListener(int controlChange, int value);
    }

    public interface OnRackDeleteButtonClickListener {
        void onRackDeleteButtonClickListener();
    }
}