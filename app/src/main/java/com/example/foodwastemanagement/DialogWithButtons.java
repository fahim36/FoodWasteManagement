/*
 * Copyright (c) 2019 by SurroundApps Inc.
 * All Rights Reserved
 * SurroundApps Inc.
 */

package com.example.foodwastemanagement;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;



public class DialogWithButtons extends Dialog implements
        View.OnClickListener {

    private Context context;
    private OnDialogButtonClickListener listener;
    private Button btnNegative, btnNeutral, btnPositive;
    private TextView title, subtitle;

    public DialogWithButtons(@NonNull Context context, OnDialogButtonClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_with_button);

        btnPositive = findViewById(R.id.btnPositive);
        btnNegative = findViewById(R.id.btnNegative);
        btnNeutral = findViewById(R.id.btnNeutral);
        title = findViewById(R.id.tvTitle);
        subtitle = findViewById(R.id.tvSubtitle);
        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
        btnNeutral.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPositive:
                listener.onPositiveClicked(this);
                break;

            case R.id.btnNegative:
                listener.onNegativeClicked(this);
                break;

            case R.id.btnNeutral:
                listener.onNeutralClicked(this);
                break;
        }
        dismiss();
    }

    public void setTitle(String s) {
        title.setText(s);
    }

    public void setSubtitle(String s) {
        subtitle.setVisibility(View.VISIBLE);
        subtitle.setText(s);
    }

    public void setPositiveButtonLabel(String s) {
        btnPositive.setText(s);
    }

    public void setNegativeButtonLabel(String s) {
        btnNegative.setText(s);
    }

    public void hideNegativeButton() {
        btnNegative.setVisibility(View.INVISIBLE);
    }

    public void goneNegativeButton() {
        btnNegative.setVisibility(View.GONE);
    }

    public void setNeutralButtonLabel(String s) {
        findViewById(R.id.vertical_line_1).setVisibility(View.VISIBLE);
        btnNeutral.setVisibility(View.VISIBLE);
        btnNeutral.setText(s);
    }

    public interface OnDialogButtonClickListener {
        void onPositiveClicked(DialogWithButtons d);
        void onNegativeClicked(DialogWithButtons d);
        void onNeutralClicked(DialogWithButtons d);
    }
}