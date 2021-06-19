package com.leebeebeom.closetnote.util.bindingadapter;

import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.ui.dialog.DialogBuilder;
import com.leebeebeom.closetnote.util.OnTextChange;
import com.leebeebeom.closetnote.util.adapter.AutoCompleteAdapter;
import com.leebeebeom.closetnote.util.constant.ViewType;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Set;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter("android:text")
    public static void text(TextInputEditText editText, String text) {
        if (text != null) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }

    @androidx.databinding.BindingAdapter("toggle")
    public static void toggle(ImageView imageView, boolean folderToggle) {
        imageView.setImageResource(folderToggle ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_left);
    }

    @androidx.databinding.BindingAdapter("setItems")
    public static void setItems(MaterialAutoCompleteTextView autoCompleteTextView, Set<String> items) {
        if (items != null){
            AutoCompleteAdapter adapter = (AutoCompleteAdapter) autoCompleteTextView.getAdapter();
            adapter.setItem(new ArrayList<>(items));
        }
    }

    @androidx.databinding.BindingAdapter("android:paddingHorizontal")
    public static void setPadding(RecyclerView recyclerView, int viewType) {
        if (viewType == ViewType.LIST_VIEW.getValue()) {
            int padding = recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen._8sdp);
            recyclerView.setPadding(padding, recyclerView.getPaddingTop(), padding, recyclerView.getPaddingBottom());
        } else
            recyclerView.setPadding(0, recyclerView.getPaddingTop(), 0, recyclerView.getPaddingBottom());
    }

    @androidx.databinding.BindingAdapter("layoutManager")
    public static void setLayoutManager(RecyclerView recyclerView, int viewType) {
        if (viewType == ViewType.LIST_VIEW.getValue())
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
        else
            recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2, RecyclerView.VERTICAL, false));
    }

    @androidx.databinding.BindingAdapter("setOnKeyListener")
    public static void setOnKeyListener(TextInputLayout textInputLayout, String dummy) {
        if (textInputLayout.getEditText() != null) {
            textInputLayout.getEditText().setOnKeyListener((v, keyCode, event) -> {
                if (isTextLength30(textInputLayout.getEditText()) && keyCode != KeyEvent.KEYCODE_DEL && keyCode != KeyEvent.FLAG_EDITOR_ACTION)
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.dialog_et_max_length));
                else textInputLayout.setErrorEnabled(false);
                return false;
            });
        }
    }

    private static boolean isTextLength30(EditText editText) {
        return String.valueOf(editText.getText()).length() == 30;
    }

    @androidx.databinding.BindingAdapter("addTextChangedListener")
    public static void addTextChangedListener(TextInputEditText editText, DialogBuilder dialogBuilder){
        editText.addTextChangedListener(new OnTextChange() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialogBuilder.setPositiveEnabledByInputText(s);
            }
        });
    }
}
