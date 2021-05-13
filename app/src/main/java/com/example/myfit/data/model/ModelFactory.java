package com.example.myfit.data.model;

import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.size.Size;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ModelFactory {
    @NotNull
    @Contract("_, _, _ -> new")
    public static Category makeCategory(String categoryName, byte parentIndex, int orderNumber) {
        BaseInfo defaultCategoryInfo = createDefaultInfo(parentIndex, orderNumber);
        return new Category(defaultCategoryInfo, categoryName);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static Folder makeFolder(String folderName, long parentId, byte parentIndex, int orderNumber) {
        BaseInfo defaultFolderInfo = createDefaultInfo(parentIndex, orderNumber);
        return new Folder(defaultFolderInfo, folderName, parentId);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static Size makeSize(long parentId, byte parentIndex, int orderNumber) {
        BaseInfo defaultSizeInfo = createDefaultInfo(parentIndex, orderNumber);
        return new Size(defaultSizeInfo, parentId);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    private static BaseInfo createDefaultInfo(byte parentIndex, int orderNumber) {
        return new BaseInfo(parentIndex, orderNumber);
    }
}
