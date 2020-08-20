package com.firmino.racks.components;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.racks.R;

import it.beppi.knoblibrary.Knob;

public class Potentiometer extends Component {

    private Knob knob;
    private Context mContext;

    public Potentiometer(@NonNull Context context) {
        super(context);
        mContext = context;
        knob = new Knob(context);
        knob.setNumberOfStates(130);
        knob.setMinAngle(-130);
        knob.setMaxAngle(130);
        knob.setStateMarkersAccentPeriodicity(16);
        knob.setBorderWidth(0);
        knob.setState(knob.getNumberOfStates()/2);

        setPotTheme(1);
        knob.invalidate();
        getContainer().addView(knob);
    }

    public void setPotTheme(int theme){
        switch (theme){

            case 1:
                knob.setStateMarkersAccentColor(ResourcesCompat.getColor(mContext.getResources(), android.R.color.black, null));
                knob.setStateMarkersColor(ResourcesCompat.getColor(mContext.getResources(), android.R.color.transparent, null));
                knob.setSelectedStateMarkerColor(ResourcesCompat.getColor(mContext.getResources(), android.R.color.black, null));
                knob.setStateMarkersAccentRelativeLength(0);
                knob.setStateMarkersRelativeLength(0);
                knob.setStateMarkersWidth(0);

                knob.setIndicatorColor(Color.rgb(27, 27, 2));
                knob.setIndicatorRelativeLength(0f);

                knob.setKnobDrawableRotates(true);
                knob.setKnobDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.bg_knob2, null));
                knob.setKnobDrawableRes(R.drawable.bg_knob2);

                break;

            default:
                knob.setStateMarkersAccentColor(ResourcesCompat.getColor(mContext.getResources(), android.R.color.black, null));
                knob.setStateMarkersColor(ResourcesCompat.getColor(mContext.getResources(), android.R.color.transparent, null));
                knob.setSelectedStateMarkerColor(ResourcesCompat.getColor(mContext.getResources(), android.R.color.black, null));
                knob.setStateMarkersAccentRelativeLength(.12f);
                knob.setStateMarkersRelativeLength(0);
                knob.setStateMarkersWidth(0);

                knob.setIndicatorColor(Color.rgb(27, 27, 2));
                knob.setIndicatorRelativeLength(.5f);

                knob.setKnobDrawableRotates(false);
                knob.setKnobDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.bg_knob1, null));
                knob.setKnobDrawableRes(R.drawable.bg_knob1);
                break;
        }
    }
}
