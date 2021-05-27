package com.example.myfit.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.repository.dao.AutoCompleteDao;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.data.repository.dao.FolderDao;
import com.example.myfit.data.repository.dao.RecentSearchDao;
import com.example.myfit.data.repository.dao.SizeDao;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DataModule {
    private static final String SORT_MAIN = "sort main";
    private static final String SORT_LIST = "sort list";
    public static final String SORT = "sort";

    @Singleton
    @Provides
    public static CategoryDao provideCategoryDao(AppDataBase appDataBase) {
        return appDataBase.categoryDao();
    }

    @Singleton
    @Provides
    public static FolderDao provideFolderDao(AppDataBase appDataBase) {
        return appDataBase.folderDao();
    }

    @Singleton
    @Provides
    public static SizeDao provideSizeDao(AppDataBase appDataBase) {
        return appDataBase.sizeDao();
    }

    @Singleton
    @Provides
    public static RecentSearchDao provideRecentSearchDao(AppDataBase appDataBase) {
        return appDataBase.recentSearchDao();
    }

    @Singleton
    @Provides
    public static AutoCompleteDao provideAutoCompleteDao(AppDataBase appDataBase) {
        return appDataBase.autoCompleteDao();
    }

    @Singleton
    @Provides
    public static AppDataBase provideAppDataBase(@ApplicationContext Context context) {
        return AppDataBase.getsInstance(context);
    }

    @Qualifiers.MainSortPreference
    @Singleton
    @Provides
    public static SharedPreferences provideMainSortPreference(@ApplicationContext Context context) {
        return context.getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE);
    }

    @Qualifiers.MainSortPreferenceLive
    @Singleton
    @Provides
    public static IntegerSharedPreferenceLiveData provideMainSorePreferenceList(@Qualifiers.MainSortPreference SharedPreferences mainSortPreference) {
        return new IntegerSharedPreferenceLiveData(mainSortPreference, SORT, Sort.SORT_CUSTOM.getValue());
    }

    @Qualifiers.ListSortPreference
    @Singleton
    @Provides
    public static SharedPreferences provideListSortPreference(@ApplicationContext Context context) {
        return context.getSharedPreferences(SORT_LIST, Sort.SORT_CUSTOM.getValue());
    }

    @Qualifiers.ListSortPreferenceLive
    @Singleton
    @Provides
    public static IntegerSharedPreferenceLiveData provideListSortPreferenceList(@Qualifiers.ListSortPreference SharedPreferences listSortPreference) {
        return new IntegerSharedPreferenceLiveData(listSortPreference, SORT, Sort.SORT_CUSTOM.getValue());
    }
}
