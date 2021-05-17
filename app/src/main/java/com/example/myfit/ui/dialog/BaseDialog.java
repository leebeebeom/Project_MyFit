package com.example.myfit.ui.dialog;

import android.app.AlertDialog;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public abstract class BaseDialog extends DialogFragment {
    protected abstract AlertDialog getAlertDialog();

    protected abstract View.OnClickListener getPositiveClickListener();
}
