package com.example.project_myfit;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter("android:text")
    public static void setSelection(TextInputEditText editText, String text) {
        if (text != null) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }

    @androidx.databinding.BindingAdapter("setUri")
    public static void setUri(ImageView imageView, String uriString) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            Glide.with(imageView.getContext()).load(uri).into(imageView);
        }
    }

    @androidx.databinding.BindingAdapter("setDragHandle")
    public static void setDragHandle(ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).fallback(R.drawable.icon_drag_handle).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setAddIcon")
    public static void setAddIcon(ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).fallback(R.drawable.icon_add_image).into(imageView);
    }
}
