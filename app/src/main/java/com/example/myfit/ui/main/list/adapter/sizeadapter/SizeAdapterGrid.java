package com.example.myfit.ui.main.list.adapter.sizeadapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.databinding.ItemVhSizeGridBinding;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVHListener;
import com.example.myfit.util.adapter.viewholder.SizeGridVH;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

@FragmentScoped
public class SizeAdapterGrid extends BaseAdapter<SizeTuple, BaseVHListener.SizeVHListener, SizeGridVH> {

    @Inject
    public SizeAdapterGrid(SizeLiveSet<SizeTuple> selectedItems) {
        super(selectedItems);
    }

    @Override
    protected SizeGridVH getViewHolder(@NotNull ViewGroup parent, BaseVHListener.SizeVHListener listener, Set<SizeTuple> selectedItemIds) {
        ItemVhSizeGridBinding binding = ItemVhSizeGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeGridVH(binding, listener, selectedItemIds);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeGridVH holder, int position) {
        super.onBindViewHolder(holder, position);
    }
}
