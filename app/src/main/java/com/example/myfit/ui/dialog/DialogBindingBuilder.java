package com.example.myfit.ui.dialog;

import android.view.KeyEvent;

import com.example.myfit.R;
import com.example.myfit.databinding.ItemDialogEditTextBinding;

import javax.inject.Inject;

public class DialogBindingBuilder {
    private final ItemDialogEditTextBinding binding;

    @Inject
    public DialogBindingBuilder(ItemDialogEditTextBinding binding) {
        this.binding = binding;
        binding.et.requestFocus();
        this.showErrorIfMoreThan30Characters();
    }

    public DialogBindingBuilder setHint(String hint) {
        binding.setHint(hint);
        return this;
    }

    public DialogBindingBuilder setPlaceHolder(String placeHolder) {
        binding.setPlaceHolder(placeHolder);
        return this;
    }

    public DialogBindingBuilder setText(String text){
        binding.et.setText(text);
        return this;
    }

    private void showErrorIfMoreThan30Characters() {
        binding.et.setOnKeyListener((v, keyCode, event) -> {
            if (isTextLength30() && keyCode != KeyEvent.KEYCODE_DEL && keyCode != KeyEvent.FLAG_EDITOR_ACTION)
                binding.layout.setError(v.getContext().getString(R.string.dialog_et_max_length));
            else binding.layout.setErrorEnabled(false);
            return false;
        });
    }

    private boolean isTextLength30() {
        return String.valueOf(binding.et.getText()).length() == 30;
    }

    public ItemDialogEditTextBinding create() {
        return binding;
    }
}
