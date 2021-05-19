package com.example.myfit.ui.dialog;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseDialog extends DialogFragment {
    public static final String ACTION_MODE_OFF = "action mode off";
    @Inject
    protected DialogBuilder dialogBuilder;

    protected abstract AlertDialog getAlertDialog();

    protected abstract View.OnClickListener getPositiveClickListener();
}
