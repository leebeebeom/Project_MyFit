package com.example.myfit.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.repository.AutoCompleteRepository;
import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.RecentSearchRepository;
import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.data.repository.dao.AutoCompleteDao;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.data.repository.dao.FolderDao;
import com.example.myfit.data.repository.dao.RecentSearchDao;
import com.example.myfit.data.repository.dao.SizeDao;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class DataModule {
    @ViewModelScoped
    @Provides
    public static CategoryRepository provideCategoryRepository(@ApplicationContext Context context,
                                                               @Qualifiers.MainSortPreference SharedPreferences mainSortPreference) {
        CategoryDao categoryDao = AppDataBase.getsInstance(context).categoryDao();
        return new CategoryRepository(categoryDao, mainSortPreference);
    }

    @ViewModelScoped
    @Provides
    public static FolderRepository provideFolderRepository(@ApplicationContext Context context,
                                                           @Qualifiers.ListSortPreference SharedPreferences listSortPreference,
                                                           @Qualifiers.FolderTogglePreference SharedPreferences folderTogglePreference) {
        FolderDao folderDao = AppDataBase.getsInstance(context).folderDao();
        return new FolderRepository(folderDao, listSortPreference, folderTogglePreference);
    }

    @ViewModelScoped
    @Provides
    public static SizeRepository provideSizeRepository(@ApplicationContext Context context,
                                                       @Qualifiers.ListSortPreference SharedPreferences listSortPreference,
                                                       @Qualifiers.ViewTypePreference SharedPreferences viewTypePreferences) {
        SizeDao sizeDao = AppDataBase.getsInstance(context).sizeDao();
        return new SizeRepository(sizeDao, listSortPreference, viewTypePreferences);
    }

    @ViewModelScoped
    @Provides
    public static RecentSearchRepository provideRecentSearchRepository(@ApplicationContext Context context) {
        RecentSearchDao recentSearchDao = AppDataBase.getsInstance(context).recentSearchDao();
        return new RecentSearchRepository(recentSearchDao);
    }

    @ViewModelScoped
    @Provides
    public static AutoCompleteRepository provideAutoCompleteRepository(@ApplicationContext Context context) {
        AutoCompleteDao autoCompleteDao = AppDataBase.getsInstance(context).autoCompleteDao();
        return new AutoCompleteRepository(autoCompleteDao);
    }
}
