package com.firmino.rigkontrol;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class KDialog extends Dialog {
    public KDialog(@NonNull Context context) {
        super(context);
    }

    public KDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected KDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
