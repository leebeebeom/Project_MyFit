package com.example.project_myfit.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.project_myfit.R;
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
            Glide.with(imageView).load(uri).into(imageView);
        }else Glide.with(imageView).clear(imageView);
    }

    @androidx.databinding.BindingAdapter("setDragHandle")
    public static void setDragHandle(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView).load(dummy).error(R.drawable.icon_drag_handle).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setDragHandle2")
    public static void setDragHandle2(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView).load(dummy).error(R.drawable.icon_drag_handle2).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setAddIcon")
    public static void setAddIcon(@NotNull ImageView imageView, String imageUri) {
        if (imageUri == null) {
            Glide.with(imageView).load((Bitmap) null).error(R.drawable.icon_add_image).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else {
            Glide.with(imageView).clear(imageView);
            imageView.setVisibility(View.GONE);
        }
    }

    @androidx.databinding.BindingAdapter("setRecentSearchIcon")
    public static void setRecentSearchIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView).load(dummy).error(R.drawable.icon_recent_search).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setCloseIcon")
    public static void setCloseIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView).load(dummy).error(R.drawable.icon_close).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setFolderIcon")
    public static void setFolderIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView).load(dummy).error(R.drawable.icon_folder).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setClothIcon")
    public static void setClothIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView).load(dummy).error(R.drawable.icon_cloth).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setDotIcon")
    public static void setDotIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView).load(dummy).error(R.drawable.icon_dot).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setTreeTriangleIcon")
    public static void setTreeTriangleIcon(@NotNull ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView).load(dummy).error(R.drawable.icon_triangle_right).into(imageView);
    }

}
