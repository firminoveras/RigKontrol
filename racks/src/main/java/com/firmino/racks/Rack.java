package com.firmino.racks;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
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

import com.firmino.racks.components.Component;
import com.firmino.racks.frags.ConfigFragmentAdapter;
import com.firmino.racks.frags.FragmentColorsConfig;
import com.firmino.racks.frags.FragmentMainConfig;
import com.firmino.racks.frags.FragmentMidiConfig;
import com.firmino.racks.frags.FragmentTitleConfig;
import com.firmino.racks.interfaceitems.RoundRackImageButton;

public class Rack extends RelativeLayout {

    private final Context mContext;
    private RoundRackImageButton mPowerButton, mMinimizeButton, mConfigButton;
    private LinearLayout mContainerLayout;
    private ViewPager2 mConfigPager;
    private ConfigFragmentAdapter mConfigFragAdapter;
    private FrameLayout mSaveLayout;
    private TextView mLittleTitle, mBigTitle;
    private OnRackOnListener onRackOnListener = (isOn, CC) -> {};
    private OnRackDeleteButtonClickListener onRackDeleteButtonClickListener = () -> {};
    private boolean isMinimized = false, isConfigModeOn = false;
    private int mControlChange = 0;

    FragmentMainConfig mFragMain;
    FragmentTitleConfig mFragTitle;
    FragmentColorsConfig mFragColors;
    FragmentMidiConfig mFragMidi;

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
        inflate(mContext, R.layout.rack_layout, this);
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

        mConfigFragAdapter = new ConfigFragmentAdapter(((FragmentActivity) mContext));
        mConfigFragAdapter.add(mFragMain);
        mConfigFragAdapter.add(mFragTitle);
        mConfigFragAdapter.add(mFragColors);
        mConfigFragAdapter.add(mFragMidi);

        mConfigPager.setAdapter(mConfigFragAdapter);

        mFragMain.setOnEditNameClickListener(button -> mConfigPager.setCurrentItem(1, true));
        mFragMain.setOnEditColorsClickListener(button -> mConfigPager.setCurrentItem(2, true));
        mFragMain.setOnMidiConfigClickListener(button -> mConfigPager.setCurrentItem(3, true));
        mFragMain.setOnDeleteRacklickListener(button -> onRackDeleteButtonClickListener.onRackDeleteButtonClickListener());
        mFragMain.setOnAddClickListener(button -> {
            //TODO: Parei aqui
            int unity = (int) (80 * mContext.getResources().getDisplayMetrics().density);
            if (mContainerLayout.getChildCount() < (mContainerLayout.getWidth() / unity) - 1) mContainerLayout.addView(new Component(mContext));
        });

        mFragTitle.setOnTitleBackClickListener(button -> mConfigPager.setCurrentItem(0, true));
        mFragTitle.setOnTitleResumeListener(title -> title.setText(getRackTitle()));
        mFragTitle.setOnTitleRenameButtonClick(this::setRackTitle);

        mFragColors.setOnColorsBackClickListener(() -> mConfigPager.setCurrentItem(0, true));
        mFragColors.setOnColorsBackgroundColorPickedListener(this::setRackBackgroundColor);
        mFragColors.setOnColorsForegroundColorPickedListener(this::setRackForegroundColor);

        mFragMidi.setOnMidiBackClickListener(() -> mConfigPager.setCurrentItem(0, true));
        mFragMidi.setOnMidiChangeListener(cc -> mControlChange = cc);
        mFragMidi.setOnMidiResumeListener(textView -> textView.setText(String.valueOf(mControlChange)));


        mPowerButton.setToggle(true);
        mPowerButton.setOnRackButtonClicked((isOn) -> onRackOnListener.onRackOnListener(isOn, mControlChange));

        mMinimizeButton.setToggle(false);
        mMinimizeButton.setOnRackButtonClicked((isOn) -> {
            if (isOn) setMinimizedMode(!isMinimized);
        });

        mConfigButton.setToggle(true);
        mConfigButton.setOnRackButtonClicked(this::setConfigModeEnabled);
    }

    public void setConfigModeEnabled(boolean enabled) {
        isConfigModeOn = enabled;
        int maxH = (int) (getResources().getDimension(R.dimen.rack_config_height));
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
            lay.height = enabled ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen.rack_inner_heigth);
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
        findViewById(R.id.Rack_Background).setBackgroundTintList(color);
    }

    public void setRackForegroundColor(ColorStateList color) {
        mBigTitle.setTextColor(color);
        mLittleTitle.setTextColor(color);
        findViewById(R.id.Rack_InnerLayout).setBackgroundTintList(color);
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

    public void setOnRackOnListener(OnRackOnListener onRackOnListener) {
        this.onRackOnListener = onRackOnListener;
    }

    public void setOnRackDeleteButtonClickListener(OnRackDeleteButtonClickListener onRackDeleteButtonClickListener) {
        this.onRackDeleteButtonClickListener = onRackDeleteButtonClickListener;
    }

    public interface OnRackOnListener {
        void onRackOnListener(boolean isOn, int ControlChange);
    }

    public interface OnRackDeleteButtonClickListener {
        void onRackDeleteButtonClickListener();
    }
}