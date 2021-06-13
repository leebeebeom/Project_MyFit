package com.example.myfit.ui.dialog.tree;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
@AllArgsConstructor
public class BaseTreeValue<T extends CategoryTuple> {
    @Getter
    private final T mTuple;

    public static class CategoryValue extends BaseTreeValue<CategoryTuple> {
        public CategoryValue(CategoryTuple tuple) {
            super(tuple);
        }
    }

    public static class FolderValue extends BaseTreeValue<FolderTuple> {
        public FolderValue(FolderTuple tuple) {
            super(tuple);
        }
    }
}
