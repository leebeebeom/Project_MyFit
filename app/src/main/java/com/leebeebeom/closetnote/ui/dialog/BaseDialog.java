package com.leebeebeom.closetnote.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;

import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.ui.MainGraphViewModel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseDialog extends DialogFragment {
    public static final String ACTION_MODE_OFF = "action mode off";
    @Inject
    protected DialogBuilder mDialogBuilder;
    @Inject
    protected NavController mNavController;
    private MainGraphViewModel mMainGraphViewModel;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    protected abstract AlertDialog getAlertDialog();

    protected abstract View.OnClickListener getPositiveClickListener();

    protected void setBackStackLive() {
        getMainGraphViewModel().setBackStackEntryLive(getBackStack());
    }

    protected MainGraphViewModel getMainGraphViewModel() {
        if (mMainGraphViewModel == null) {
            NavBackStackEntry mainBackStack = getMainBackStack();
            mMainGraphViewModel = new ViewModelProvider(mainBackStack, HiltViewModelFactory.create(requireContext(), mainBackStack)).get(MainGraphViewModel.class);
        }
        return mMainGraphViewModel;
    }

    protected NavBackStackEntry getMainBackStack() {
        return mNavController.getBackStackEntry(R.id.nav_graph_main);
    }

    protected NavBackStackEntry getBackStack() {
        return mNavController.getBackStackEntry(getResId());
    }

    protected abstract int getResId();

    protected void setBackStackActionModeOff() {
        getBackStack().getSavedStateHandle().set(ACTION_MODE_OFF, null);
    }
}
