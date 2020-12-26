package com.example.project_myfit.ui.main.adapter;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.DragCallBack;
import com.example.project_myfit.ui.main.database.ChildCategory;

import org.jetbrains.annotations.NotNull;

public class ProviderChild extends BaseNodeProvider {
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private final DragCallBack.DragStartListener mListener;

    public ProviderChild(DragCallBack.DragStartListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemViewType() {
        return 2;
    }

    @Override
    public int getLayoutId() {
        //Root
        return R.layout.item_main_fragment_child;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, BaseNode baseNode) {
        //Bind
        ChildCategory childCategory = (ChildCategory) baseNode;
        baseViewHolder.setText(R.id.child_text, childCategory.getChildCategory());
        //Swipe
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(baseViewHolder.getView(R.id.swipeLayout), String.valueOf(((ChildCategory) baseNode).getId()));
        baseViewHolder.getView(R.id.child_category_drag_handle).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mListener.startDrag(baseViewHolder);
            }
            return false;
        });
    }
}
