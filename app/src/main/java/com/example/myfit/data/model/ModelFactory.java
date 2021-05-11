package com.example.myfit.data.model;

import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.size.Size;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ModelFactory {
    @NotNull
    @Contract("_, _, _ -> new")
    public static Category makeCategory(byte parentCategoryIndex, int orderNumber, String categoryName) {
        BaseInfo defaultCategoryInfo = createDefaultInfo(parentCategoryIndex, orderNumber);
        return new Category(defaultCategoryInfo, categoryName);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static Folder makeFolder(byte parentCategoryIndex, int orderNumber, String folderName, long parentId) {
        BaseInfo defaultFolderInfo = createDefaultInfo(parentCategoryIndex, orderNumber);
        return new Folder(defaultFolderInfo, folderName, parentId);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static Size makeSize(byte parentCategoryIndex, int orderNumber, long parentId) {
        BaseInfo defaultSizeInfo = createDefaultInfo(parentCategoryIndex, orderNumber);
        return new Size(defaultSizeInfo, parentId);
    }


    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    private static BaseInfo createDefaultInfo(byte parentCategoryIndex, int orderNumber) {
        return new BaseInfo(parentCategoryIndex, orderNumber);
    }
}
