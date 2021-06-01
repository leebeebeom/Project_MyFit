package com.example.myfit.data.model;

import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.model.model.RecentSearch;
import com.example.myfit.data.model.model.Size;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ModelFactory {
    @NotNull
    @Contract("_, _, _ -> new")
    public static Category makeCategory(String categoryName, int parentIndex, int sortNumber) {
        return new Category(parentIndex, sortNumber, categoryName);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static Folder makeFolder(String folderName, long parentId, int parentIndex, int sortNumber) {
        return new Folder(parentIndex, sortNumber, folderName, parentId);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static Size makeSize(long parentId, int parentIndex, int sortNumber) {
        return new Size(parentIndex, sortNumber, parentId);
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
}
