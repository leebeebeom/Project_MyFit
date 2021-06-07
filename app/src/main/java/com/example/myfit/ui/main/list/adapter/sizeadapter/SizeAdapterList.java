package com.example.myfit.ui.main.list.adapter.sizeadapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.databinding.ItemVhSizeListBinding;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVHListener;
import com.example.myfit.util.adapter.viewholder.SizeListVH;
import com.example.myfit.util.constant.ViewType;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

@FragmentScoped
public class SizeAdapterList extends BaseAdapter<SizeTuple, BaseVHListener.SizeVHListener, SizeListVH> {

    @Inject
    public SizeAdapterList(SizeLiveSet<SizeTuple> selectedItems) {
        super(selectedItems);
    }

    @Override
    protected SizeListVH getViewHolder(@NotNull ViewGroup parent, BaseVHListener.SizeVHListener listener, Set<SizeTuple> selectedItemIds) {
        ItemVhSizeListBinding binding = ItemVhSizeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeListVH(binding, listener, selectedItemIds);
    }
}
