package com.example.myfit.util;

import com.example.myfit.data.model.category.CategoryTuple;
import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.util.constant.SortValue;

import java.util.List;

public class SortUtil {
    public static <T extends CategoryTuple> void orderCategoryFolderTuples(int sort, List<T> tuples) {
        if (sort == SortValue.SORT_CUSTOM.getValue())
            tuples.sort((o1, o2) -> Integer.compare(o1.getOrderNumber(), o2.getOrderNumber()));
        else if (sort == SortValue.SORT_CREATE.getValue())
            tuples.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == SortValue.SORT_CREATE_REVERSE.getValue())
            tuples.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == SortValue.SORT_NAME.getValue() ||
                sort == SortValue.SORT_BRAND.getValue())
            tuples.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        else if (sort == SortValue.SORT_NAME_REVERSE.getValue() ||
                sort == SortValue.SORT_BRAND_REVERSE.getValue())
            tuples.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
        else if (sort == SortValue.SORT_DELETED.getValue())
            tuples.sort((o1, o2) -> Long.compare(o1.getDeletedTime(), o2.getDeletedTime()));
    }

    public static void orderSizeTuples(int sort, List<SizeTuple> sizeTuples) {
        if (sort == SortValue.SORT_CUSTOM.getValue())
            sizeTuples.sort((o1, o2) -> Integer.compare(o1.getOrderNumber(), o2.getOrderNumber()));
        else if (sort == SortValue.SORT_CREATE.getValue())
            sizeTuples.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == SortValue.SORT_CREATE_REVERSE.getValue())
            sizeTuples.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == SortValue.SORT_NAME.getValue())
            sizeTuples.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        else if (sort == SortValue.SORT_NAME_REVERSE.getValue())
            sizeTuples.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
        else if (sort == SortValue.SORT_BRAND.getValue())
            sizeTuples.sort((o1, o2) -> o1.getBrand().compareTo(o2.getBrand()));
        else if (sort == SortValue.SORT_BRAND_REVERSE.getValue())
            sizeTuples.sort((o1, o2) -> o2.getBrand().compareTo(o1.getBrand()));
        else if (sort == SortValue.SORT_DELETED.getValue())
            sizeTuples.sort((o1, o2) -> Long.compare(o1.getDeletedTime(), o2.getDeletedTime()));
    }
}
