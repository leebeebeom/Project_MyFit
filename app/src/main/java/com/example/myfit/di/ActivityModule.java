package com.example.myfit.di;

import android.content.Context;
import android.view.LayoutInflater;

import com.example.myfit.R;
import com.example.myfit.databinding.ActivityMainBinding;
import com.example.myfit.util.adapter.AutoCompleteAdapter;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;

@Module
@InstallIn(ActivityComponent.class)
public class ActivityModule {

    @ActivityScoped
    @Provides
    public static ActivityMainBinding provideActivityMainBinding(@ActivityContext Context context) {
        return ActivityMainBinding.inflate(LayoutInflater.from(context));
    }

    @Provides
    public static AutoCompleteAdapter provideAutoCompleteAdapter(@ActivityContext Context context) {
        return new AutoCompleteAdapter(context, R.layout.item_auto_complete_texv_view, R.id.tv_item_ac_tv);
    }

    @Provides
    public static LayoutInflater provideLayoutInflater(@ActivityContext Context context) {
        return LayoutInflater.from(context);
    }
}
