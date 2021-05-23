package com.example.myfit.ui.dialog;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfit.R;
import com.example.myfit.ui.main.MainGraphViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseDialog extends DialogFragment {
    public static final String ACTION_MODE_OFF = "action mode off";
    @Inject
    protected DialogBuilder dialogBuilder;

    protected abstract AlertDialog getAlertDialog();

    protected abstract View.OnClickListener getPositiveClickListener();

    protected void setBackStackLive() {
        NavBackStackEntry mainBackStack = getMainBackStack();
        MainGraphViewModel mainGraphViewModel = new ViewModelProvider(mainBackStack, HiltViewModelFactory.create(requireContext(), mainBackStack)).get(MainGraphViewModel.class);
        mainGraphViewModel.setBackStackEntryLive(getBackStack());
    }

    protected NavBackStackEntry getMainBackStack() {
        return getNavController().getBackStackEntry(R.id.nav_graph_main);
    }

    protected abstract NavBackStackEntry getBackStack();

    protected NavBackStackEntry getGraphBackStack() {
        NavController navController = getNavController();
        return navController.getBackStackEntry(navController.getGraph().getId());
    }

    protected NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    protected void actionModeOff() {
        getBackStack().getSavedStateHandle().set(ACTION_MODE_OFF, null);
    }
}
