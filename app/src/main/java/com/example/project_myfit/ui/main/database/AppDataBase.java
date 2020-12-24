package com.example.project_myfit.ui.main.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.project_myfit.ui.main.listfragment.database.Converters;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.example.project_myfit.ui.main.listfragment.database.ListFolderDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {ChildCategory.class, Size.class, ListFolder.class}, version = 2, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDataBase extends RoomDatabase {
    public abstract CategoryDao categoryDao();

    public abstract SizeDao sizeDao();

    public abstract ListFolderDao listFolderDao();

    private static AppDataBase sInstance;

    public synchronized static AppDataBase getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDataBase.class, "db")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            List<ChildCategory> childCategoryList = new ArrayList<>();
                            childCategoryList.add(new ChildCategory("Short Sleeve", "Top", 1));
                            childCategoryList.add(new ChildCategory("Long Sleeve", "Top", 2));
                            childCategoryList.add(new ChildCategory("Knit", "Top", 3));
                            childCategoryList.add(new ChildCategory("Hood", "Top", 4));
                            childCategoryList.add(new ChildCategory("Shirt", "Top", 5));
                            childCategoryList.add(new ChildCategory("Jean", "Bottom", 6));
                            childCategoryList.add(new ChildCategory("Slacks", "Bottom", 7));
                            childCategoryList.add(new ChildCategory("Short Pant", "Bottom", 8));
                            childCategoryList.add(new ChildCategory("Track Pant", "Bottom", 9));
                            childCategoryList.add(new ChildCategory("Ma-1", "Outer", 10));
                            childCategoryList.add(new ChildCategory("Shall Parka", "Outer", 11));
                            childCategoryList.add(new ChildCategory("M65", "Outer", 12));
                            childCategoryList.add(new ChildCategory("Hood Zip-Up", "Outer", 13));
                            childCategoryList.add(new ChildCategory("Shoes", "ETC", 14));
                            childCategoryList.add(new ChildCategory("Glass", "ETC", 15));
                            childCategoryList.add(new ChildCategory("Necklace", "ETC", 16));
                            childCategoryList.add(new ChildCategory("Ring", "ETC", 17));
                            //insert
                            new Thread(() -> {
                                for (ChildCategory childCategory : childCategoryList) {
                                    getsInstance(context).categoryDao().insert(childCategory);
                                }
                            }).start();
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sInstance;
    }
}
