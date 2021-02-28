package com.example.project_myfit;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

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
    public static void setDragHandle(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).error(R.drawable.icon_drag_handle).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setDragHandle2")
    public static void setDragHandle2(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).error(R.drawable.icon_drag_handle2).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setAddIcon")
    public static void setAddIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).error(R.drawable.icon_add_image).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setRecentSearchIcon")
    public static void setRecentSearchIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).error(R.drawable.icon_recent_search).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setCloseIcon")
    public static void setCloseIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).error(R.drawable.icon_close).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setFolderIcon")
    public static void setFolderIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).error(R.drawable.icon_folder).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setClothIcon")
    public static void setClothIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).error(R.drawable.icon_cloth).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setDotIcon")
    public static void setDotIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).error(R.drawable.icon_dot).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setTreeTriangleIcon")
    public static void setTreeTriangleIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).error(R.drawable.icon_triangle_right).into(imageView);
    }

}
