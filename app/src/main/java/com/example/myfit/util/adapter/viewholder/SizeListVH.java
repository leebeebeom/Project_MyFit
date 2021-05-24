package com.example.myfit.util.adapter.viewholder;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.databinding.ItemSizeListBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class SizeListVH extends BaseVH<SizeTuple, SizeVHListener> {
    private final ItemSizeListBinding binding;

    public SizeListVH(@NotNull ItemSizeListBinding binding, SizeVHListener listener, Set<SizeTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.binding = binding;

        binding.cbFavorite.setOnClickListener(v -> listener.sizeFavoriteClick(getTuple()));
    }

    @Override
    public void bind(SizeTuple tuple) {
        binding.setTuple(tuple);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return binding.cb;
    }

    @Override
    public AppCompatImageView getDragHandleIcon() {
        return binding.iconDragHandle;
    }

    @Override
    protected void setDraggingView() {
        setItemViewTranslationZ(itemView, 10);
        binding.cb.setVisibility(View.INVISIBLE);
        binding.layoutContents.setAlpha(0.7f);
        binding.iv.setAlpha(0.5f);
    }

    @Override
    protected void setDropView() {
        setItemViewTranslationZ(itemView, 0);
        binding.cb.setVisibility(View.VISIBLE);
        binding.layoutContents.setAlpha(1);
        binding.iv.setAlpha(1f);
    }

    @Override
    public MaterialCardView getCardView() {
        return binding.cardView;
    }
}

