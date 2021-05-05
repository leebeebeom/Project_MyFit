package com.example.project_myfit.util;

import com.example.project_myfit.data.model.ParentModel;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class FilterUtil {
    public static <T extends ParentModel> void classify(@NotNull T t, List<List<T>> list) {
        switch (t.getParentCategory()) {
            case TOP:
                list.get(0).add(t);
                break;
            case BOTTOM:
                list.get(1).add(t);
                break;
            case OUTER:
                list.get(2).add(t);
                break;
            default:
                list.get(3).add(t);
        }
    }

    public static <T extends ParentModel> List<T> getCombineList(@NotNull List<List<T>> list) {
        return Stream.of(list.get(0), list.get(1), list.get(2), list.get(3))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
