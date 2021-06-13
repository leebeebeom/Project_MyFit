package com.example.myfit.ui.dialog.sort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.databinding.LayoutDialogSortBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.util.constant.Sort;
import com.google.android.material.radiobutton.MaterialRadioButton;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseSortDialog extends BaseDialog {
    protected LayoutDialogSortBinding mBinding;

    private static final String CHECKED_SORT = "check sort";
    private int mCheckedNumber;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = LayoutDialogSortBinding.inflate(LayoutInflater.from(requireContext()));
        mCheckedNumber = savedInstanceState == null ? getRepository().getSort().getValue() : savedInstanceState.getInt(CHECKED_SORT);
        List<MaterialRadioButton> sortButtons = getSortButtons();
        addRadioButtonCheckListener(sortButtons);
        sortButtons.get(mCheckedNumber).setChecked(true);
    }

    protected abstract BaseRepository<?> getRepository();

    @NotNull
    private List<MaterialRadioButton> getSortButtons() {
        return Arrays.asList(mBinding.radioBtnCustom.button, mBinding.radioBtnCreate.button, mBinding.radioBtnCreateReverse.button,
                mBinding.radioBtnBrand.button, mBinding.radioBtnBrandReverse.button, mBinding.radioBtnName.button, mBinding.radioBtnNameReverse.button);
    }

    private void addRadioButtonCheckListener(@NotNull List<MaterialRadioButton> buttons) {
        int count = buttons.size();
        for (int i = 0; i < count; i++) {
            int finalI = i;
            buttons.get(i).setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mCheckedNumber = Sort.values()[finalI].getValue();

                    buttons.stream()
                            .filter(button -> button != buttonView)
                            .forEach(button -> button.setChecked(false));
                }
            });
        }
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder
                .setTitle(getString(R.string.all_sort_order))
                .setView(mBinding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            getRepository().changeSort(Sort.values()[mCheckedNumber]);
            dismiss();
        };
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHECKED_SORT, mCheckedNumber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
