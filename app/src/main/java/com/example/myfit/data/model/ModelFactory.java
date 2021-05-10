package com.example.myfit.data.model;

import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.size.SizeBottom;
import com.example.myfit.data.model.size.SizeEtc;
import com.example.myfit.data.model.size.SizeTop;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ModelFactory {
    @NotNull
    @Contract("_, _, _ -> new")
    public static Category makeCategory(byte parentCategoryIndex, int orderNumber, String categoryName) {
        DefaultInfo defaultCategoryInfo = createDefaultInfo(parentCategoryIndex, orderNumber);
        return new Category(defaultCategoryInfo, categoryName);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static Folder makeFolder(byte parentCategoryIndex, int orderNumber, String folderName, long parentId) {
        DefaultInfo defaultFolderInfo = createDefaultInfo(parentCategoryIndex, orderNumber);
        return new Folder(defaultFolderInfo, folderName, parentId);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static SizeTop makeSizeTop(byte parentCategoryIndex, int orderNumber, long parentId) {
        DefaultInfo defaultSizeInfo = createDefaultInfo(parentCategoryIndex, orderNumber);
        return new SizeTop(defaultSizeInfo, parentId);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static SizeBottom makeSizBottom(byte parentCategoryIndex, int orderNumber, long parentId) {
        DefaultInfo defaultSizeInfo = createDefaultInfo(parentCategoryIndex, orderNumber);
        return new SizeBottom(defaultSizeInfo, parentId);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static SizeEtc makeSizeEtc(byte parentCategoryIndex, int orderNumber, long parentId) {
        DefaultInfo defaultSizeInfo = createDefaultInfo(parentCategoryIndex, orderNumber);
        return new SizeEtc(defaultSizeInfo, parentId);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    private static DefaultInfo createDefaultInfo(byte parentCategoryIndex, int orderNumber) {
        return new DefaultInfo(parentCategoryIndex, orderNumber);
    }
}
