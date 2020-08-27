package com.firmino.rigkontrol.kinterface.alerts;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.kinterface.views.KTitle;

import java.util.Objects;

public class ConfirmationAlert {

    private OnConfirmationAlertCancel onConfirmationAlertCancel;
    private OnConfirmationAlertConfirm onConfirmationAlertConfirm;

    public ConfirmationAlert(String title, String message, Context context) {
        onConfirmationAlertCancel = () -> {};
        onConfirmationAlertConfirm = () -> {};
        View alertContent = View.inflate(context, R.layout.layout_alerts_confirmationalert, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setView(alertContent);
        alert.setCancelable(false);
        AlertDialog dialog = alert.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (300 * context.getResources().getDisplayMetrics().density), -2);

        ((KTitle) alertContent.findViewById(R.id.Alert_Confirmation_Title)).setText(title);
        ((TextView) alertContent.findViewById(R.id.Alert_Confirmation_Message)).setText(message);

        alertContent.findViewById(R.id.Alert_Confirmation_Cancel).setOnClickListener(v -> {
            onConfirmationAlertCancel.onCancelClick();
            dialog.dismiss();
        });

        alertContent.findViewById(R.id.Alert_Confirmation_Confirm).setOnClickListener(v -> {
            onConfirmationAlertConfirm.onConfirmClick();
            dialog.dismiss();
        });
    }

    public ConfirmationAlert setOnConfirmationAlertCancel(OnConfirmationAlertCancel onConfirmationAlertCancel) {
        this.onConfirmationAlertCancel = onConfirmationAlertCancel;
        return this;
    }

    public ConfirmationAlert setOnConfirmationAlertConfirm(OnConfirmationAlertConfirm onConfirmationAlertConfirm) {
        this.onConfirmationAlertConfirm = onConfirmationAlertConfirm;
        return this;
    }

    public interface OnConfirmationAlertConfirm {
        void onConfirmClick();
    }

    public interface OnConfirmationAlertCancel {
        void onCancelClick();
    }

}