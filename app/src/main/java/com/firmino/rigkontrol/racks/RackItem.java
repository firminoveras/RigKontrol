package com.firmino.rigkontrol.racks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.BuildConfig;
import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.kinterface.alerts.MessageAlert;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class RackItem extends LinearLayout {

    private final TextView mLittleTitle, mBigTitle, mDate, mCategory;
    private final LinearLayout mContainer;
    private final Context mContext;
    private File mFile;
    private final Drawable mBgDown, mBgUp;
    private ColorStateList mForegroundColor;

    public RackItem(Context context) {
        super(context);
        mContext = context;
        inflate(context, R.layout.layout_main_dialog_openrack_item, this);
        mLittleTitle = findViewById(R.id.OpenRack_Small_Title);
        mBigTitle = findViewById(R.id.OpenRack_Big_Title);
        mDate = findViewById(R.id.OpenRack_Date);
        mCategory = findViewById(R.id.OpenRack_Category);
        mContainer = findViewById(R.id.OpenRack_Container);

        mBgUp = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_unicolor, null);
        mBgDown = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_highlight, null);
    }

    public void setup(File file) {
        mFile = file;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser tag = factory.newPullParser();
            InputStream is = new FileInputStream(file);
            tag.setInput(is, null);
            mContainer.removeAllViews();
            while (tag.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (tag.getEventType() == XmlPullParser.START_TAG) {
                    if (tag.getName().toLowerCase().equals("rack")) {
                        if (Integer.parseInt(tag.getAttributeValue(null, "version").replace(".", "")) > Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", ""))) {
                            MessageAlert.create(mContext, MessageAlert.TYPE_ERRO, mContext.getString(R.string.requires_version) + tag.getAttributeValue(null, "version"));
                            break;
                        }
                        setRackTitle(tag.getAttributeValue(null, "title"));
                        findViewById(R.id.OpenRack_Background).setBackgroundTintList(ColorStateList.valueOf(Integer.parseInt(tag.getAttributeValue(null, "backgroundColor"))));
                        setRackForegroundColor(ColorStateList.valueOf(Integer.parseInt(tag.getAttributeValue(null, "foregroundColor"))));
                        mCategory.setText(tag.getAttributeValue(null, "category"));
                    }
                    if (tag.getName().toLowerCase().startsWith("potentiometer")) {
                        ImageView newPot = new ImageView(mContext);
                        newPot.setLayoutParams(new LayoutParams((int) getResources().getDimension(R.dimen._0dp), (int) getResources().getDimension(R.dimen._30dp), 1));
                        newPot.setImageResource(R.drawable.ic_add_pot);
                        newPot.setImageTintList(mForegroundColor);
                        mContainer.addView(newPot);
                    }
                    if (tag.getName().toLowerCase().startsWith("push_button")) {
                        ImageView newButton = new ImageView(mContext);
                        newButton.setLayoutParams(new LayoutParams((int) getResources().getDimension(R.dimen._0dp), (int) getResources().getDimension(R.dimen._30dp), 1));
                        newButton.setImageResource(R.drawable.ic_add_button);
                        newButton.setImageTintList(mForegroundColor);
                        mContainer.addView(newButton);
                    }
                    if (tag.getName().toLowerCase().startsWith("slide")) {
                        ImageView newButton = new ImageView(mContext);
                        newButton.setLayoutParams(new LayoutParams((int) getResources().getDimension(R.dimen._0dp), (int) getResources().getDimension(R.dimen._30dp), 1));
                        newButton.setImageResource(R.drawable.ic_add_slider);
                        newButton.setImageTintList(mForegroundColor);
                        mContainer.addView(newButton);
                    }
                }
                tag.next();
            }
            is.close();
        } catch (XmlPullParserException e) {
            MessageAlert.create(mContext, MessageAlert.TYPE_ERRO, mContext.getString(R.string.file_corrupt));
        } catch (IOException e) {
            MessageAlert.create(mContext, MessageAlert.TYPE_ERRO, mContext.getString(R.string.unknow_erro));
        }
        mDate.setText(new Date(file.lastModified()).toString());

    }

    public File getFile() {
        return mFile;
    }

    @Override
    public boolean performClick() {
        findViewById(R.id.OpenRack_Main).setBackground(mBgUp);
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        findViewById(R.id.OpenRack_Main).setBackground(event.getAction() == MotionEvent.ACTION_DOWN ? mBgDown : mBgUp);
        if (event.getAction() == MotionEvent.ACTION_UP) performClick();
        return super.onTouchEvent(event);
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
        mLittleTitle.setVisibility(mLittleTitle.getText().length() > 0 ? VISIBLE : GONE);
    }

    public void setRackForegroundColor(ColorStateList color) {
        mLittleTitle.setTextColor(color);
        mBigTitle.setTextColor(color);
        mForegroundColor = color;
    }

    public String getCategory(){
        return mCategory.getText().toString();
    }

}
