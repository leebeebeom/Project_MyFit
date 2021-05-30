package com.example.myfit.data;

import com.example.myfit.data.model.BaseInfo;
import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.model.model.RecentSearch;
import com.example.myfit.data.model.model.size.Size;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ModelFactory {
    @NotNull
    @Contract("_, _, _ -> new")
    public static Category makeCategory(String categoryName, int parentIndex, int orderNumber) {
        BaseInfo defaultCategoryInfo = createDefaultInfo(parentIndex, orderNumber);
        return new Category(defaultCategoryInfo, categoryName);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static Folder makeFolder(String folderName, long parentId, int parentIndex, int orderNumber) {
        BaseInfo defaultFolderInfo = createDefaultInfo(parentIndex, orderNumber);
        return new Folder(defaultFolderInfo, folderName, parentId);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static Size makeSize(long parentId, int parentIndex, int orderNumber) {
        BaseInfo defaultSizeInfo = createDefaultInfo(parentIndex, orderNumber);
        return new Size(defaultSizeInfo, parentId);
    }

    @NotNull
    public static RecentSearch makeRecentSearch(String word, int type) {
        String date = getRecentSearchDate();
        return new RecentSearch(word, date, type);
    }

    @NotNull
    private static String getRecentSearchDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    private static BaseInfo createDefaultInfo(int parentIndex, int orderNumber) {
        return new BaseInfo(parentIndex, orderNumber);
    }
}
