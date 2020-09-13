package com.firmino.rigkontrol.racks;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.kinterface.views.KRoundImageButton;
import com.firmino.rigkontrol.racks.components.Component;
import com.firmino.rigkontrol.racks.components.Potentiometer;
import com.firmino.rigkontrol.racks.components.PushButton;
import com.firmino.rigkontrol.racks.frags.ConfigFragmentAdapter;
import com.firmino.rigkontrol.racks.frags.FragmentAddConfig;
import com.firmino.rigkontrol.racks.frags.FragmentColorsConfig;
import com.firmino.rigkontrol.racks.frags.FragmentMainConfig;
import com.firmino.rigkontrol.racks.frags.FragmentMidiConfig;
import com.firmino.rigkontrol.racks.frags.FragmentTitleConfig;

public class Rack extends RelativeLayout {

    private final Context mContext;
    private KRoundImageButton mPowerButton, mMinimizeButton, mConfigButton;
    private LinearLayout mContainerLayout;
    private ViewPager2 mConfigPager;
    private ConfigFragmentAdapter mConfigFragAdapter;
    private FrameLayout mSaveLayout;
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
        mSaveLayout = findViewById(R.id.Rack_Save_Layout);
        mContainerLayout = findViewById(R.id.Rack_Container_Layout);
        mMinimizeButton = findViewById(R.id.Rack_MinimizeButton);
        mPowerButton = findViewById(R.id.Rack_OnButton);
        mConfigButton = findViewById(R.id.Rack_ConfigButton);
        mLittleTitle = findViewById(R.id.Rack_Little_Title);
        mBigTitle = findViewById(R.id.Rack_Big_Title);

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

        mPowerButton.setToggle(true);
        mPowerButton.setOnRackButtonClicked((isOn) -> onRackMidiListener.onRackOnListener(mControlChange, isOn ? 127 : 0));

        mMinimizeButton.setToggle(false);
        mMinimizeButton.setOnRackButtonClicked((isOn) -> {
            if (isOn) setMinimizedMode(!isMinimized);
        });

        mConfigButton.setToggle(true);
        mConfigButton.setOnRackButtonClicked(this::setConfigModeEnabled);
    }

    private void addNewComponent(Component component) {
        if (mContainerLayout.getChildCount() < 10){
            component.setOnComponentMidiControlChangeListener((cc, value) -> onRackMidiListener.onRackOnListener(cc, value));
            mContainerLayout.addView(component);
        }
    }

    public void setConfigModeEnabled(boolean enabled) {
        isConfigModeOn = enabled;
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            ((Component) mContainerLayout.getChildAt(index)).setConfigModeEnabled(isConfigModeOn);
        }
        int maxH = (int) (getResources().getDimension(R.dimen._80dp));
        ValueAnimator anim = ValueAnimator.ofInt(enabled ? 0 : maxH, enabled ? maxH : 0);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams lay = mConfigPager.getLayoutParams();
            lay.height = (int) valueAnimator.getAnimatedValue();
            mConfigPager.setLayoutParams(lay);
        });
        anim.setDuration(300);
        anim.start();
    }

    public void setMinimizedMode(boolean enabled) {
        if (isMinimized != enabled) {
            isMinimized = enabled;
            mMinimizeButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), enabled ? R.drawable.ic_rack_maximize : R.drawable.ic_rack_minimize, null));
            if (enabled && mConfigButton.isOn()) mConfigButton.performClick();
            mSaveLayout.setVisibility(enabled ? View.GONE : View.VISIBLE);
            mContainerLayout.setVisibility(enabled ? View.GONE : View.VISIBLE);
            mConfigButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
            ViewGroup.LayoutParams lay = findViewById(R.id.Rack_InnerLayout).getLayoutParams();
            lay.height = enabled ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen._100dp);
            findViewById(R.id.Rack_InnerLayout).setLayoutParams(lay);
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

    public void setRackBackgroundColor(ColorStateList color) {
        mBackgroundColor = color;
        findViewById(R.id.Rack_Background).setBackgroundTintList(color);
    }

    public void setRackForegroundColor(ColorStateList color) {
        mForegroundColor = color;
        mBigTitle.setTextColor(color);
        mLittleTitle.setTextColor(color);
        findViewById(R.id.Rack_InnerLayout).setBackgroundTintList(color);
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            ((Component) mContainerLayout.getChildAt(index)).setForegroundColor(color);
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

    public void setRackColor(int backgroundColor, int foregroundColor) {
        findViewById(R.id.Rack_Background).setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        mBigTitle.setTextColor(foregroundColor);
        mLittleTitle.setTextColor(foregroundColor);
        findViewById(R.id.Rack_InnerLayout).setBackgroundTintList(ColorStateList.valueOf(foregroundColor));
    }

    public boolean isConfigModeOn() {
        return isConfigModeOn;
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