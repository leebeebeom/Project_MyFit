package com.leebeebeom.closetnote.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.leebeebeom.closetnote.data.model.model.Category;
import com.leebeebeom.closetnote.data.model.model.Folder;
import com.leebeebeom.closetnote.data.model.model.RecentSearch;
import com.leebeebeom.closetnote.data.model.model.Size;
import com.leebeebeom.closetnote.data.repository.CategoryRepository;
import com.leebeebeom.closetnote.data.repository.dao.AutoCompleteDao;
import com.leebeebeom.closetnote.data.repository.dao.CategoryDao;
import com.leebeebeom.closetnote.data.repository.dao.FolderDao;
import com.leebeebeom.closetnote.data.repository.dao.RecentSearchDao;
import com.leebeebeom.closetnote.data.repository.dao.SizeDao;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.constant.ParentCategory;
import com.leebeebeom.closetnote.util.constant.SharedPreferenceKey;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Database(entities = {Category.class, Size.class, Folder.class, RecentSearch.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase sInstance;

    public synchronized static AppDataBase getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDataBase.class, "closetnote.db")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            List<Category> categories = new LinkedList<>();
                            categories.add(new Category(1, ParentCategory.TOP.getValue(), "반팔"));
                            categories.add(new Category(2, ParentCategory.TOP.getValue(), "긴팔"));
                            categories.add(new Category(3, ParentCategory.TOP.getValue(), "니트"));
                            categories.add(new Category(4, ParentCategory.TOP.getValue(), "후드"));
                            categories.add(new Category(5, ParentCategory.TOP.getValue(), "셔츠"));

                            categories.add(new Category(6, ParentCategory.BOTTOM.getValue(), "청바지"));
                            categories.add(new Category(7, ParentCategory.BOTTOM.getValue(), "슬랙스"));
                            categories.add(new Category(8, ParentCategory.BOTTOM.getValue(), "반바지"));

                            categories.add(new Category(9, ParentCategory.OUTER.getValue(), "Ma-1"));
                            categories.add(new Category(10, ParentCategory.OUTER.getValue(), "패딩"));
                            categories.add(new Category(11, ParentCategory.OUTER.getValue(), "야상"));
                            categories.add(new Category(12, ParentCategory.OUTER.getValue(), "후드 집업"));

                            categories.add(new Category(13, ParentCategory.ETC.getValue(), "신발"));
                            categories.add(new Category(14, ParentCategory.ETC.getValue(), "안경"));
                            categories.add(new Category(15, ParentCategory.ETC.getValue(), "목걸이"));
                            categories.add(new Category(16, ParentCategory.ETC.getValue(), "기타"));
                            setLargestSortNumber(context);
                            setId(categories);
                            new CategoryRepository(context).insert(categories);
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sInstance;
    }

    private static void setLargestSortNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferenceKey.CATEGORY_LARGEST_SORT_NUMBER.getValue(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(SharedPreferenceKey.LARGEST_SORT_NUMBER.getValue(), 17).apply();
    }

    private static void setId(List<Category> categories) {
        AtomicLong id = new AtomicLong(CommonUtil.createId());
        categories.forEach(category -> category.setId(id.incrementAndGet()));
    }

    public abstract CategoryDao categoryDao();

    public abstract SizeDao sizeDao();

    public abstract FolderDao folderDao();

    public abstract RecentSearchDao recentSearchDao();

    public abstract AutoCompleteDao autoCompleteDao();
}
