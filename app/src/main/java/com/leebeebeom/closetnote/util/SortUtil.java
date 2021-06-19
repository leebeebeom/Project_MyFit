package com.leebeebeom.closetnote.util;

import com.leebeebeom.closetnote.data.model.BaseModel;
import com.leebeebeom.closetnote.data.tuple.BaseTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.SizeTuple;
import com.leebeebeom.closetnote.util.constant.Sort;

import java.util.List;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SortUtil {
    public static void sortSizeTuples(Sort sort, List<SizeTuple> sizeTuples) {
        if (sort == Sort.SORT_CUSTOM)
            sizeTuples.sort((o1, o2) -> Integer.compare(o1.getSortNumber(), o2.getSortNumber()));
        else if (sort == Sort.SORT_CREATE)
            sizeTuples.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == Sort.SORT_CREATE_REVERSE)
            sizeTuples.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == Sort.SORT_NAME)
            sizeTuples.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        else if (sort == Sort.SORT_NAME_REVERSE)
            sizeTuples.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
        else if (sort == Sort.SORT_BRAND)
            sizeTuples.sort((o1, o2) -> o1.getBrand().compareTo(o2.getBrand()));
        else if (sort == Sort.SORT_BRAND_REVERSE)
            sizeTuples.sort((o1, o2) -> o2.getBrand().compareTo(o1.getBrand()));
        else if (sort == Sort.SORT_DELETED)
            sizeTuples.sort((o1, o2) -> Long.compare(o1.getDeletedTime(), o2.getDeletedTime()));
    }

    public static <T extends BaseModel> Stream<T> sortCategoryFolderStream(Sort sort, Stream<T> stream) {
        if (sort == Sort.SORT_CUSTOM)
            return stream.sorted((o1, o2) -> Integer.compare(o1.getSortNumber(), o2.getSortNumber()));
        else if (sort == Sort.SORT_CREATE)
            return stream.sorted((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == Sort.SORT_CREATE_REVERSE)
            return stream.sorted((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == Sort.SORT_NAME || sort == Sort.SORT_BRAND)
            return stream.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));
        else return stream.sorted((o1, o2) -> o2.getName().compareTo(o1.getName()));
    }

    public static <T extends BaseTuple> List<T> sortCategoryFolderTuples(Sort sort, List<T> tuples) {
        if (sort == Sort.SORT_CUSTOM)
            tuples.sort((o1, o2) -> Integer.compare(o1.getSortNumber(), o2.getSortNumber()));
        else if (sort == Sort.SORT_CREATE)
            tuples.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == Sort.SORT_CREATE_REVERSE)
            tuples.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == Sort.SORT_NAME || sort == Sort.SORT_BRAND)
            tuples.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        else tuples.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
        return tuples;
    }
}
