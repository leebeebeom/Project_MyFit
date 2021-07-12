package com.leebeebeom.closetnote.util.bindingadapter;

import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVH;
import com.leebeebeom.closetnote.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import static com.leebeebeom.closetnote.util.ActionModeImpl.ACTION_MODE_OFF;
import static com.leebeebeom.closetnote.util.ActionModeImpl.ACTION_MODE_ON;
import static com.leebeebeom.closetnote.util.ActionModeImpl.sActionMode;


public class ItemBindingAdapter {
    private static Animation sOpenAnimation;
    private static Animation sCloseAnimation;

    @BindingAdapter("android:text")
    public static void text(@NotNull MaterialTextView textView, int i) {
        String number = String.valueOf(i);
        textView.setText(number);
    }

    @BindingAdapter("setUri")
    public static void setUri(ImageView imageView, String uriString) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            Glide.with(imageView).load(uri).into(imageView);
        } else Glide.with(imageView).clear(imageView);
    }

    @BindingAdapter("setUri")
    public static void setUri(ImageView imageView, Uri uri) {
        if (uri != null)
            Glide.with(imageView).load(uri).into(imageView);
        else Glide.with(imageView).clear(imageView);
    }

    @BindingAdapter("addIconVisibility")
    public static void addIconVisibility(@NotNull ImageView imageView, String imageUri) {
        imageView.setVisibility(imageUri == null ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("addIconVisibility")
    public static void addIconVisibility(@NotNull ImageView imageView, Uri uri) {
        imageView.setVisibility(uri == null ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("dragHandleActionMode")
    public static void dragHandleActionMode(ImageView dragHandleIcon, BaseAdapter<?, ?, ?> adapter) {
        if (adapter.getActionModeState() == ACTION_MODE_ON)
            dragHandleIcon.setVisibility(adapter.getSort() == Sort.SORT_CUSTOM ? View.VISIBLE : View.GONE);
        else dragHandleIcon.setVisibility(View.GONE);
    }

    @BindingAdapter({"listActionMode", "holder"})
    public static void listActionMode(MaterialCardView cardView, BaseAdapter<?, ?, ?> adapter, BaseVH<?, ?> baseVH) {
        if (sOpenAnimation == null)
            sOpenAnimation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.item_list_slide_right);
        if (sCloseAnimation == null)
            sCloseAnimation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.item_list_slide_left);

        if (sActionMode != null && adapter.getActionModeState() == ACTION_MODE_ON && cardView.getAnimation() != sOpenAnimation)
            cardView.startAnimation(sOpenAnimation);
        else if (adapter.getActionModeState() == ACTION_MODE_OFF && cardView.getAnimation() == sOpenAnimation)
            cardView.startAnimation(sCloseAnimation);
    }

    @BindingAdapter("gridActionMode")
    public static void gridActionMode(MaterialCheckBox checkBox, BaseAdapter<?, ?, ?> adapter) {
        if (adapter.getActionModeState() == ACTION_MODE_ON)
            checkBox.setVisibility(View.VISIBLE);
        else if (adapter.getActionModeState() == ACTION_MODE_OFF) {
            checkBox.jumpDrawablesToCurrentState();
            checkBox.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("marginStart")
    public static void marginStart(ConstraintLayout layout, int margin) {
        LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams) layout.getLayoutParams();
        layoutParams.setMarginStart(margin);
    }

    @BindingAdapter("arrowActive")
    public static void arrowActive(ImageView imageView, boolean active) {
        if (active)
            imageView.setImageResource(R.drawable.icon_triangle_down);
        else imageView.setImageResource(R.drawable.icon_triangle_right);
    }

    @BindingAdapter("folderActive")
    public static void folderActive(ImageView imageView, boolean active) {
        if (active)
            imageView.setImageResource(R.drawable.icon_folder_open);
        else imageView.setImageResource(R.drawable.icon_folder);
    }
}
