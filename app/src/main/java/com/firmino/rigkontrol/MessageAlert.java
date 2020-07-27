package com.firmino.rigkontrol;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageAlert {
    public static final int TYPE_ALERT = 0;
    public static final int TYPE_ERRO = 1;
    public static final int TYPE_SUCESS = 2;

    public static void create(Context context, int messageType, String message) {
        View toastContent = ((Activity) context).getLayoutInflater().inflate(R.layout.toast_layout, ((MainActivity) context).findViewById(R.id.Toast_Main));
        Toast toast = new Toast(context);
        toast.setView(toastContent);
        switch (messageType) {
            case TYPE_ALERT:
                ((ImageView) toastContent.findViewById(R.id.Toast_Icon)).setImageResource(R.drawable.ic_alert);
                toastContent.findViewById(R.id.Toast_Main).setBackgroundTintList(ColorStateList.valueOf(context.getColor(android.R.color.holo_orange_dark)));
                break;
            case TYPE_ERRO:
                ((ImageView) toastContent.findViewById(R.id.Toast_Icon)).setImageResource(R.drawable.ic_erro);
                toastContent.findViewById(R.id.Toast_Main).setBackgroundTintList(ColorStateList.valueOf(context.getColor(android.R.color.holo_red_light)));
                break;
            default:
                ((ImageView) toastContent.findViewById(R.id.Toast_Icon)).setImageResource(R.drawable.ic_done);
                toastContent.findViewById(R.id.Toast_Main).setBackgroundTintList(ColorStateList.valueOf(context.getColor(android.R.color.holo_green_dark)));
                break;
        }
        ((TextView) toastContent.findViewById(R.id.Toast_Text)).setText(message);
        toast.setDuration(android.widget.Toast.LENGTH_LONG);
        toast.show();
    }
}
