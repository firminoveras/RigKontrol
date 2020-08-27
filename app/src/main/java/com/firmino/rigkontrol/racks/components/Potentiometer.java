package com.firmino.rigkontrol.racks.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.kinterface.views.KButton;
import com.firmino.rigkontrol.kinterface.views.KListPicker;
import com.firmino.rigkontrol.kinterface.views.KNumberPicker;
import com.firmino.rigkontrol.kinterface.views.KTextEdit;

import it.beppi.knoblibrary.Knob;

public class Potentiometer extends Component {

    private static final int KNOB_SIMPLE = 0;
    private static final int KNOB_BLACK = 1;
    private static final int KNOB_MODERN = 2;
    private static final int KNOB_OCTOGONAL = 3;
    private static final int KNOB_BASIC = 4;

    private static final int MARKERS_CONTINUOUS = 0;
    private static final int MARKERS_NONE = 1;
    private static final int MARKERS_THREE_DOTS = 2;
    private static final int MARKERS_DEFAULT = 3;

    private final Knob mKnob;
    private final Context mContext;
    private int mKnobStyle = 1, mMarkerStyle = 1;
    private ColorStateList mForegroundColor;

    public Potentiometer(@NonNull Context context, ColorStateList foregroundColor) {
        super(context, foregroundColor);
        mContext = context;
        mForegroundColor = foregroundColor;
        mKnob = new Knob(context);
        mKnob.setNumberOfStates(130);
        mKnob.setMinAngle(-130);
        mKnob.setMaxAngle(130);
        mKnob.setBorderWidth(0);
        mKnob.setState(mKnob.getNumberOfStates() / 2);
        mKnob.setIndicatorRelativeLength(0f);
        mKnob.setKnobDrawableRotates(true);
        mKnob.setFreeRotation(false);

        setKnobStyle(mKnob, KNOB_MODERN);
        setMarkersStyle(mKnob, MARKERS_THREE_DOTS);

        mKnob.setOnStateChanged(this::setComponentValue);
        getContainer().addView(mKnob, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setOnConfigButtonClickedListener(this::showConfigDialog);
        setOnColorChangeListener(this::setKnobColor);
    }

    public void setKnobStyle(Knob target, int knobStyle) {
        int id;
        switch (knobStyle) {
            case KNOB_SIMPLE:
                id = R.drawable.bg_knob1;
                break;
            case KNOB_BLACK:
                id = R.drawable.bg_knob2;
                break;
            case KNOB_MODERN:
                id = R.drawable.bg_knob3;
                break;
            case KNOB_OCTOGONAL:
                id = R.drawable.bg_knob4;
                break;
            default:
                id = R.drawable.bg_knob5;
                break;
        }
        this.mKnobStyle = knobStyle;
        target.setKnobDrawable(ResourcesCompat.getDrawable(mContext.getResources(), id, null));
        target.setKnobDrawableRes(id);
        target.invalidate();
    }

    public void setMarkersStyle(Knob target, int markersStyle) {
        float markerAccentSize, markerSize;
        int markerWidth, markerAccentWidth, periodicity;
        switch (markersStyle) {
            case MARKERS_CONTINUOUS:
                markerAccentSize = .02f;
                markerSize = .02f;
                markerWidth = 4;
                markerAccentWidth = 4;
                periodicity = 1;
                break;

            case MARKERS_NONE:
                markerAccentSize = 0;
                markerSize = 0;
                markerWidth = 0;
                markerAccentWidth = 0;
                periodicity = 0;
                break;

            case MARKERS_THREE_DOTS:
                markerAccentSize = .05f;
                markerSize = 0;
                markerWidth = 0;
                markerAccentWidth = 8;
                periodicity = 64;
                break;

            default:
                markerAccentSize = .15f;
                markerSize = 0;
                markerWidth = 0;
                markerAccentWidth = 4;
                periodicity = 16;
                break;
        }

        this.mMarkerStyle = markersStyle;

        setKnobColor(mForegroundColor);

        target.setStateMarkersAccentRelativeLength(markerAccentSize);
        target.setStateMarkersRelativeLength(markerSize);
        target.setStateMarkersWidth(markerWidth);
        target.setStateMarkersAccentWidth(markerAccentWidth);
        target.setStateMarkersAccentPeriodicity(periodicity);
        target.setSelectedStateMarkerContinuous(false);
    }

    private void setKnobColor(ColorStateList color) {
        mKnob.setStateMarkersColor(ResourcesCompat.getColor(mContext.getResources(), android.R.color.transparent, null));
        mKnob.setStateMarkersAccentColor(color.getDefaultColor());
        mKnob.setSelectedStateMarkerColor(color.getDefaultColor());
    }

    public int getKnobStyle() {
        return mKnobStyle;
    }

    public int getMarkerStyle() {
        return mMarkerStyle;
    }

    private void showConfigDialog() {
        View contentView = inflate(mContext, R.layout.layout_component_pot_config, findViewById(R.id.Pot_Config_Root));
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(contentView);
        builder.setCancelable(false);
        Dialog dialog = builder.show();

        Knob knobExample = contentView.findViewById(R.id.Pot_Config_Knob_Example);
        KListPicker markersStyle = contentView.findViewById(R.id.Pot_Config_Markers_Style);
        KListPicker knobStyle = contentView.findViewById(R.id.Pot_Config_Knob_Style);
        KTextEdit title = contentView.findViewById(R.id.Pot_Config_Title);
        KNumberPicker cc = contentView.findViewById(R.id.Pot_Config_CC);
        KButton applyButton = contentView.findViewById(R.id.Pot_Config_Apply_Button);

        title.setTextValue(getTitle());

        setKnobStyle(knobExample, getKnobStyle());
        setMarkersStyle(knobExample, getMarkerStyle());
        markersStyle.setSelectedItem(getMarkerStyle());
        knobStyle.setSelectedItem(getKnobStyle());
        knobStyle.setOnKListPikerItemSelectedListener((index, item) -> {
            setKnobStyle(knobExample, index);
            setKnobStyle(mKnob, index);
        });
        markersStyle.setOnKListPikerItemSelectedListener((index, item) -> {
            setMarkersStyle(knobExample, index);
            setMarkersStyle(mKnob, index);
        });

        cc.setValue(getControlChange());

        applyButton.setOnClickListener(view -> {
            setTitle(title.getText());
            setControlChange(cc.getValue());
            dialog.dismiss();
        });
    }




}
