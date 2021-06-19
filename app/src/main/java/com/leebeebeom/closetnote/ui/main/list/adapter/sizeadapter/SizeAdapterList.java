package com.leebeebeom.closetnote.ui.main.list.adapter.sizeadapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leebeebeom.closetnote.data.tuple.tuple.SizeTuple;
import com.leebeebeom.closetnote.databinding.ItemVhSizeListBinding;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVHListener;
import com.leebeebeom.closetnote.util.adapter.viewholder.SizeListVH;
import com.leebeebeom.closetnote.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

@FragmentScoped
public class SizeAdapterList extends BaseAdapter<SizeTuple, BaseVHListener.SizeVHListener, SizeListVH> {

    @Inject
    public SizeAdapterList(SizeLiveSet<SizeTuple> selectedSizeTuples, Fragment fragment) {
        super(selectedSizeTuples, (BaseVHListener.SizeVHListener) fragment);
    }

    @Override
    public void setSort(Sort sort) {
        //TODO
    }

    @Override
    protected SizeListVH getViewHolder(@NotNull ViewGroup parent, BaseVHListener.SizeVHListener listener, SizeLiveSet<SizeTuple> selectedItems) {
        ItemVhSizeListBinding binding = ItemVhSizeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeListVH(binding, listener, selectedItems);
    }
}
