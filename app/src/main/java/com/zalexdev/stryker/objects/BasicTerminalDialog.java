package com.zalexdev.stryker.objects;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import com.zalexdev.stryker.R;

import java.util.Objects;

public class BasicTerminalDialog {
    private final Dialog dialog;
    private final TextView titleView;
    private final TextView commandView;
    private final TextView outputView;

    public BasicTerminalDialog(Context context) {
        dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.basic_terminal_dialog, null);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleView = view.findViewById(R.id.name);
        commandView = view.findViewById(R.id.model);
        outputView = view.findViewById(R.id.terminalView);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setCommand(String command) {
        commandView.setText(command);
    }

    public void setOutput(String output) {
        outputView.setText(output);
    }

    public TextView getOutput() {
        return outputView;
    }
}