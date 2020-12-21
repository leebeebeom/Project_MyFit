package com.example.project_myfit.ui.main.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {ChildCategory.class, Size.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract CategoryDao categoryDao();

    public abstract SizeDao sizeDao();

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
                            childCategoryList.add(new ChildCategory("Long Pants", "Bottom", 3));
                            childCategoryList.add(new ChildCategory("Short Pants", "Bottom", 4));
                            childCategoryList.add(new ChildCategory("Jacket", "Outer", 5));
                            childCategoryList.add(new ChildCategory("Coat", "Outer", 6));
                            childCategoryList.add(new ChildCategory("Shoes", "ETC", 7));
                            //인서트
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
