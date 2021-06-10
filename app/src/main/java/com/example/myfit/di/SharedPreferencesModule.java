package com.example.myfit.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.constant.ViewType;
import com.example.myfit.util.sharedpreferencelive.BooleanSharedPreferenceLiveData;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class SharedPreferencesModule {
    private static final String SORT_MAIN = "sort main";
    private static final String SORT_LIST = "sort list";
    public static final String SORT = "sort";
    public static final String VIEW_TYPE = "view type";
    public static final String FOLDER_TOGGLE = "folder toggle";

    @Qualifiers.MainSortPreference
    @ViewModelScoped
    @Provides
    public static SharedPreferences provideMainSortPreference(@ApplicationContext Context context) {
        return context.getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE);
    }

    @Qualifiers.MainSortPreferenceLive
    @ViewModelScoped
    @Provides
    public static IntegerSharedPreferenceLiveData provideMainSorePreferenceList(@Qualifiers.MainSortPreference SharedPreferences mainSortPreference) {
        return new IntegerSharedPreferenceLiveData(mainSortPreference, SORT, Sort.SORT_CUSTOM.getValue());
    }

    @Qualifiers.ListSortPreference
    @ViewModelScoped
    @Provides
    public static SharedPreferences provideListSortPreference(@ApplicationContext Context context) {
        return context.getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE);
    }

    @Qualifiers.ListSortPreferenceLive
    @ViewModelScoped
    @Provides
    public static IntegerSharedPreferenceLiveData provideListSortPreferenceLive(@Qualifiers.ListSortPreference SharedPreferences listSortPreference) {
        return new IntegerSharedPreferenceLiveData(listSortPreference, SORT, Sort.SORT_CUSTOM.getValue());
    }

    @Qualifiers.ViewTypePreference
    @ViewModelScoped
    @Provides
    public static SharedPreferences provideViewTypePreferences(@ApplicationContext Context context) {
        return context.getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
    }

    @Qualifiers.ViewTypePreferenceLive
    @ViewModelScoped
    @Provides
    public static IntegerSharedPreferenceLiveData provideViewTypeReferenceLive(@Qualifiers.ViewTypePreference SharedPreferences viewTypePreference) {
        return new IntegerSharedPreferenceLiveData(viewTypePreference, VIEW_TYPE, ViewType.LIST_VIEW.getValue());
    }

    @Qualifiers.FolderTogglePreference
    @ViewModelScoped
    @Provides
    public static SharedPreferences provideFolderTogglePreference(@ApplicationContext Context context) {
        return context.getSharedPreferences(FOLDER_TOGGLE, Context.MODE_PRIVATE);
    }

    @Qualifiers.FolderTogglePreferenceLive
    @ViewModelScoped
    @Provides
    public static BooleanSharedPreferenceLiveData provideFolderTogglePreferenceLive(@Qualifiers.FolderTogglePreference SharedPreferences folderTogglePreferences) {
        return new BooleanSharedPreferenceLiveData(folderTogglePreferences, FOLDER_TOGGLE, false);
    }
}
