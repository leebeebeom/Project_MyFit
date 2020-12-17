package com.example.project_myfit.ui.main.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

//데이터 베이스 클래스
@Database(entities = {ChildCategory.class, Size.class}, version = 2, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract CategoryDao clothingDao();

    public abstract SizeDao topSizeDao();

    private static AppDataBase sInstance;

    public synchronized static AppDataBase getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDataBase.class, "db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sInstance;
    }
}
