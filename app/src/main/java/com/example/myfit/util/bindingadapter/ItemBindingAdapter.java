package com.example.myfit.util.bindingadapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myfit.R;
import com.example.myfit.ui.dialog.tree.holder.BaseTreeHolder;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVH;
import com.example.myfit.util.adapter.viewholder.ViewPagerVH;
import com.example.myfit.util.constant.AutoScrollFlag;
import com.example.myfit.util.constant.Sort;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import static com.example.myfit.util.ActionModeImpl.ACTION_MODE_OFF;
import static com.example.myfit.util.ActionModeImpl.ACTION_MODE_ON;
import static com.example.myfit.util.adapter.viewholder.BaseVH.sDragging;


public class ItemBindingAdapter {
    private static Animation sOpenAnimation;

    @androidx.databinding.BindingAdapter("android:text")
    public static void text(@NotNull TextView textView, int i) {
        String number = String.valueOf(i);
        textView.setText(number);
    }

    @androidx.databinding.BindingAdapter("setUri")
    public static void setUri(ImageView imageView, String uriString) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            Glide.with(imageView).load(uri).into(imageView);
        } else Glide.with(imageView).clear(imageView);
    }

    @androidx.databinding.BindingAdapter("setDragHandle")
    public static void setDragHandle(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_drag_handle).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setDragHandle2")
    public static void setDragHandle2(@NotNull ImageView imageView, String dummy) {
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

    @androidx.databinding.BindingAdapter("setAddIcon2")
    public static void setAddIcon2(ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_add).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setRecentSearchIcon")
    public static void setRecentSearchIcon(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_recent_search).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setCloseIcon")
    public static void setCloseIcon(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_close).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setFolderIcon")
    public static void setFolderIcon(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_folder).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setDotIcon")
    public static void setDotIcon(@NotNull ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_dot).into(imageView);
    }

    @androidx.databinding.BindingAdapter("setTriangleIcon")
    public static void setTriangleIcon(ImageView imageView, String dummy) {
        Glide.with(imageView).load(dummy).error(R.drawable.icon_triangle_right).into(imageView);
    }

    @androidx.databinding.BindingAdapter("toggleNode")
    public static void toggleNode(LinearLayoutCompat linearLayout, BaseTreeHolder<?> holder) {
        if (!holder.getNode().getChildren().isEmpty())
            linearLayout.setOnClickListener(v -> holder.getTreeView().toggleNode(holder.getNode()));
    }

    @BindingAdapter("holder")
    public static void itemViewClick(FrameLayout itemView, BaseVH<?, ?> holder) {
        itemView.setOnClickListener(v -> {
            if (holder != null)
                holder.itemViewClick();
        });
    }

    @BindingAdapter("holder")
    public static void itemViewLongClick(FrameLayout itemView, BaseVH<?, ?> holder) {
        itemView.setOnLongClickListener(v -> {
            if (holder != null)
                holder.getListener().itemViewClick(holder.getTuple());
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @BindingAdapter("holder")
    public static void dragIconTouch(ImageView dragHandleIcon, BaseVH<?, ?> holder) {
        dragHandleIcon.setOnTouchListener((v, event) -> {
            if (holder != null && event.getAction() == MotionEvent.ACTION_DOWN && !sDragging)
                holder.dragStart();
            return false;
        });
    }

    @BindingAdapter("holder")
    public static void favoriteClick(MaterialCheckBox favoriteCheckBox, BaseVH.BaseSizeVH holder) {
        favoriteCheckBox.setOnClickListener(v -> holder.getListener().favoriteClick(holder.getTuple()));
    }

    @SuppressLint("ClickableViewAccessibility")
    @BindingAdapter("holder")
    public static void rvTouch(RecyclerView recyclerView, ViewPagerVH holder) {
        ViewPagerVH.AutoScrollListener listener = holder.getListener();
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getRawY() < 250)
                listener.dragAutoScroll(AutoScrollFlag.UP);
            else if (event.getRawY() > 2000)
                listener.dragAutoScroll(AutoScrollFlag.DOWN);
            else if (event.getRawY() < 2000 && event.getRawY() > 250)
                listener.dragAutoScroll(AutoScrollFlag.STOP);
            return false;
        });
    }

    @BindingAdapter("adapter")
    public static void setDragHandleVisibility(ImageView dragHandleIcon, BaseAdapter<?, ?, ?> adapter) {
        if (adapter.getActionModeState() == ACTION_MODE_ON)
            dragHandleIcon.setVisibility(adapter.getSort() == Sort.SORT_CUSTOM ? View.VISIBLE : View.GONE);
        else dragHandleIcon.setVisibility(View.GONE);
    }

    @BindingAdapter("adapter")
    public static void setListActionMode(MaterialCardView cardView, BaseAdapter<?, ?, ?> adapter) {
        if (adapter.getActionModeState() == ACTION_MODE_ON) {
            if (sOpenAnimation == null)
                sOpenAnimation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.item_list_slide_right);
            cardView.setAnimation(sOpenAnimation);
        } else if (adapter.getActionModeState() == ACTION_MODE_OFF) {
            if (sOpenAnimation != null)
                sOpenAnimation = null;
            cardView.setAnimation(AnimationUtils.loadAnimation(cardView.getContext(), R.anim.item_list_slide_left));
        }
    }

    @BindingAdapter("adapter")
    public static void setGridActionMode(MaterialCheckBox checkBox, BaseAdapter<?, ?, ?> adapter) {
        if (adapter.getActionModeState() == ACTION_MODE_ON)
            checkBox.setVisibility(View.VISIBLE);
        else if (adapter.getActionModeState() == ACTION_MODE_OFF) {
            checkBox.jumpDrawablesToCurrentState();
            checkBox.setVisibility(View.GONE);
        }
    }
}
