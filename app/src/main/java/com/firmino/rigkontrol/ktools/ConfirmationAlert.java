package com.firmino.rigkontrol.ktools;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.firmino.rigkontrol.R;

import java.util.Objects;

public abstract class ConfirmationAlert implements OnConfirmationAlertConfirm {

    private OnConfirmationAlertCancel onConfirmationAlertCancel;

    public ConfirmationAlert(String title, String message, Context context) {
        onConfirmationAlertCancel = () -> {};
        View alertContent = View.inflate(context, R.layout.dialog_confirmation_message, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setView(alertContent);
        alert.setCancelable(false);
        AlertDialog dialog = alert.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (300 * context.getResources().getDisplayMetrics().density), -2);

        ((TextView) alertContent.findViewById(R.id.Alert_Confirmation_Title)).setText(title);
        ((TextView) alertContent.findViewById(R.id.Alert_Confirmation_Message)).setText(message);

        alertContent.findViewById(R.id.Alert_Confirmation_Cancel).setOnClickListener(v -> {
            onConfirmationAlertCancel.onCancelClick();
            dialog.dismiss();
        });

        alertContent.findViewById(R.id.Alert_Confirmation_Confirm).setOnClickListener(v -> {
            onConfirmClick();
            dialog.dismiss();
        });

    }


    public void setOnConfirmationAlertCancel(OnConfirmationAlertCancel onConfirmationAlertCancel) {
        this.onConfirmationAlertCancel = onConfirmationAlertCancel;
    }
}

interface OnConfirmationAlertConfirm {
    void onConfirmClick();
}

