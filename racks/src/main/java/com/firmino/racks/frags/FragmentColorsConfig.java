package com.firmino.racks.frags;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firmino.racks.R;
import com.firmino.racks.interfaceitems.SquareRackButton;

public class FragmentColorsConfig extends Fragment {

    private LinearLayout mBackgroudColorsLayout, mForegroundColorsLayout;
    private SquareRackButton mBackButton;
    private OnColorsBackClickListener onColorsBackClickListener = () -> {};
    private OnColorsBackgroundColorPickedListener onColorsBackgroundColorPickedListener = colorStateList -> {};
    private OnColorsForegroundColorPickedListener onColorsForegroundColorPickedListener = colorStateList -> {};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_rack_edit_colors, container, false);
        mBackgroudColorsLayout = root.findViewById(R.id.Rack_Config_Colors_Background);
        mForegroundColorsLayout = root.findViewById(R.id.Rack_Config_Colors_Foreground);

        for (int i = 0; i < mBackgroudColorsLayout.getChildCount(); i++) {
            mBackgroudColorsLayout.getChildAt(i).setOnClickListener(v -> {
                ImageView colorCircle = (ImageView) v;
                for (int index = 0; index < mBackgroudColorsLayout.getChildCount(); index++) {
                    ViewGroup.LayoutParams lay = mBackgroudColorsLayout.getChildAt(index).getLayoutParams();
                    lay.height = (int) getResources().getDimension(R.dimen.small_color_circle_size);
                    lay.width = (int) getResources().getDimension(R.dimen.small_color_circle_size);
                    mBackgroudColorsLayout.getChildAt(index).setLayoutParams(lay);
                }

                ViewGroup.LayoutParams lay = colorCircle.getLayoutParams();
                lay.height = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                lay.width = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                colorCircle.setLayoutParams(lay);
                onColorsBackgroundColorPickedListener.onColorsBackgroundColorPickedListener(colorCircle.getBackgroundTintList());
            });
        }

        for (int i = 0; i < mForegroundColorsLayout.getChildCount(); i++) {
            mForegroundColorsLayout.getChildAt(i).setOnClickListener(v -> {
                ImageView colorCircle = (ImageView) v;
                for (int index = 0; index < mForegroundColorsLayout.getChildCount(); index++) {
                    ViewGroup.LayoutParams lay = mForegroundColorsLayout.getChildAt(index).getLayoutParams();
                    lay.height = (int) getResources().getDimension(R.dimen.small_color_circle_size);
                    lay.width = (int) getResources().getDimension(R.dimen.small_color_circle_size);
                    mForegroundColorsLayout.getChildAt(index).setLayoutParams(lay);
                }

                ViewGroup.LayoutParams lay = colorCircle.getLayoutParams();
                lay.height = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                lay.width = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                colorCircle.setLayoutParams(lay);

                onColorsForegroundColorPickedListener.onColorsForegroundColorPickedListener(colorCircle.getBackgroundTintList());

            });
        }
        mBackButton = root.findViewById(R.id.Rack_Config_Colors_Back);
        mBackButton.setOnClickListener(view -> onColorsBackClickListener.onColorsBackClickListener());
        return root;
    }

    public void setOnColorsBackClickListener(OnColorsBackClickListener onColorsBackClickListener) {
        this.onColorsBackClickListener = onColorsBackClickListener;
    }

    public void setOnColorsBackgroundColorPickedListener(OnColorsBackgroundColorPickedListener onColorsBackgroundColorPickedListener) {
        this.onColorsBackgroundColorPickedListener = onColorsBackgroundColorPickedListener;
    }

    public void setOnColorsForegroundColorPickedListener(OnColorsForegroundColorPickedListener onColorsForegroundColorPickedListener) {
        this.onColorsForegroundColorPickedListener = onColorsForegroundColorPickedListener;
    }

    public interface OnColorsBackClickListener {
        void onColorsBackClickListener();
    }

    public interface OnColorsBackgroundColorPickedListener{
        void onColorsBackgroundColorPickedListener(ColorStateList colorStateList);
    }
    public interface OnColorsForegroundColorPickedListener{
        void onColorsForegroundColorPickedListener(ColorStateList colorStateList);
    }
}
