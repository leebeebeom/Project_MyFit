package com.leebeebeom.closetnote.util.bindingadapter;

import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVH;
import com.leebeebeom.closetnote.util.constant.Sort;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import static com.leebeebeom.closetnote.util.ActionModeImpl.ACTION_MODE_OFF;
import static com.leebeebeom.closetnote.util.ActionModeImpl.ACTION_MODE_ON;


public class ItemBindingAdapter {
    private static Animation sOpenAnimation;

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

    @BindingAdapter("setDragHandle")
    public static void setDragHandle(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_drag_handle).into(imageView);
    }

    @BindingAdapter("setDragHandle2")
    public static void setDragHandle2(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_drag_handle2).into(imageView);
    }

    @BindingAdapter("setAddIcon")
    public static void setAddIcon(@NotNull ImageView imageView, String imageUri) {
        imageView.setVisibility(imageUri == null ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("setAddIcon")
    public static void setAddIcon(@NotNull ImageView imageView, Uri uri) {
        imageView.setVisibility(uri == null ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("setAddIcon2")
    public static void setAddIcon2(ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_add).into(imageView);
    }

    @BindingAdapter("setRecentSearchIcon")
    public static void setRecentSearchIcon(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_recent_search).into(imageView);
    }

    @BindingAdapter("setCloseIcon")
    public static void setCloseIcon(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_close).into(imageView);
    }

    @BindingAdapter("setFolderIcon")
    public static void setFolderIcon(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_folder).into(imageView);
    }

    @BindingAdapter("setDotIcon")
    public static void setDotIcon(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_dot).into(imageView);
    }

    @BindingAdapter("dragHandleActionMode")
    public static void dragHandleActionMode(ImageView dragHandleIcon, BaseAdapter<?, ?, ?> adapter) {
        if (adapter.getActionModeState() == ACTION_MODE_ON)
            dragHandleIcon.setVisibility(adapter.getSort() == Sort.SORT_CUSTOM ? View.VISIBLE : View.GONE);
        else dragHandleIcon.setVisibility(View.GONE);
    }

    @BindingAdapter({"listActionMode", "holder"})
    public static void listActionMode(MaterialCardView cardView, BaseAdapter<?, ?, ?> adapter, BaseVH<?, ?> baseVH) {
        if (adapter.getActionModeState() == ACTION_MODE_ON) {
            if (sOpenAnimation == null)
                sOpenAnimation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.item_list_slide_right);
            cardView.setAnimation(sOpenAnimation);
        } else if (adapter.getActionModeState() == ACTION_MODE_OFF) {
            if (sOpenAnimation != null)
                sOpenAnimation = null;
            cardView.setAnimation(AnimationUtils.loadAnimation(cardView.getContext(), R.anim.item_list_slide_left));
            if (baseVH.getLayoutPosition() == adapter.getItemCount() - 1)
                adapter.setActionModeState(0);
        }
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

    @BindingAdapter("android:layout_marginStart")
    public static void setMargin(LinearLayoutCompat linearLayoutCompat, int margin) {
        LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams) linearLayoutCompat.getLayoutParams();
        layoutParams.setMarginStart(margin);
    }

    @BindingAdapter("arrowActive")
    public static void arrowActive(ImageView imageView, boolean active) {
        if (active)
            imageView.setImageResource(R.drawable.icon_triangle_down);
        else imageView.setImageResource(R.drawable.icon_triangle_right);

//        if (active)
//            Glide.with(imageView).load("").error(R.drawable.icon_triangle_down).into(imageView);
//        else Glide.with(imageView).load("").error(R.drawable.icon_triangle_right).into(imageView);
    }

    @BindingAdapter("folderActive")
    public static void folderActive(ImageView imageView, boolean active) {
        if (active)
            imageView.setImageResource(R.drawable.icon_folder_open);
        else imageView.setImageResource(R.drawable.icon_folder);

//        if (active)
//            Glide.with(imageView).load("").error(R.drawable.icon_folder_open).into(imageView);
//        else Glide.with(imageView).load("").error(R.drawable.icon_folder).into(imageView);
    }
}
