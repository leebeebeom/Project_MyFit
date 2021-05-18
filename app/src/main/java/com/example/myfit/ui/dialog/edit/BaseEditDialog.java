package com.example.myfit.ui.dialog.edit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfit.R;
import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.DialogBindingBuilder;
import com.example.myfit.ui.main.MainGraphViewModel;
import com.example.myfit.util.KeyBoardUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseEditDialog extends BaseDialog {
    public static final String EDIT_NAME = "name";
    @Inject
    protected DialogBindingBuilder dialogBindingBuilder;
    private ItemDialogEditTextBinding binding;
    private String oldName;
    private BaseEditViewModel model;
    private NavBackStackEntry navBackStackEntry;
    private NavController navController;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldName = getName();
        String inputText = savedInstanceState == null ? oldName : savedInstanceState.getString(EDIT_NAME);
        binding = getBinding(inputText);

        navController = NavHostFragment.findNavController(this);
        navBackStackEntry = getBackStackEntry(navController);
        setBackStackLive(navController);

        NavBackStackEntry editGraphBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        model = getModel(editGraphBackStackEntry);
    }

    protected abstract String getName();

    private ItemDialogEditTextBinding getBinding(String inputText) {
        return dialogBindingBuilder
                .setHint(getHint())
                .setPlaceHolder(getPlaceHolder())
                .setText(inputText).create();
    }

    protected abstract String getHint();

    protected abstract String getPlaceHolder();

    protected abstract NavBackStackEntry getBackStackEntry(NavController navController);

    private void setBackStackLive(@NotNull NavController navController) {
        NavBackStackEntry mainBackStackEntry = navController.getBackStackEntry(R.id.nav_graph_main);
        MainGraphViewModel mainGraphViewModel = new ViewModelProvider(mainBackStackEntry, HiltViewModelFactory.create(requireContext(), mainBackStackEntry)).get(MainGraphViewModel.class);
        mainGraphViewModel.setBackStackEntryLive(navBackStackEntry);
    }

    protected abstract BaseEditViewModel getModel(NavBackStackEntry editGraphBackStackEntry);

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        model.getIsExistingLive().observe(this, isExisting -> {
            if (isExisting != null) {
                if (isExisting) {
                    navigateSameNameDialog(navController);
                    KeyBoardUtil.hideKeyBoard(binding.et);
                } else {
                    model.update();
                    navBackStackEntry.getSavedStateHandle().set(EDIT_NAME, null);
                    dismiss();
                }
                model.getIsExistingLive().setValue(null);
            }
        });

        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder.makeEditTextDialog(getTitle(), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener(model, getInputText()))
                .setPositiveEnabledByIsInputText(binding.et.getText(), oldName)
                .setPositiveEnabledByIsChangedText(binding.et, oldName)
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .create();
    }

    protected abstract String getTitle();

    protected abstract View.OnClickListener getPositiveClickListener(BaseEditViewModel model, String inputText);

    protected abstract void navigateSameNameDialog(NavController navController);

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EDIT_NAME, getInputText());
    }

    @NotNull
    protected String getInputText() {
        return String.valueOf(binding.et.getText());
    }
}
