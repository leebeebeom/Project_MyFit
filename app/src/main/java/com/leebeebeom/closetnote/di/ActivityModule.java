package com.leebeebeom.closetnote.di;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.ActivityMainBinding;
import com.leebeebeom.closetnote.util.adapter.AutoCompleteAdapter;

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
        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(context));
        binding.indicator.setVisibilityAfterHide(View.GONE);
        return binding;
    }

    @Provides
    public static AutoCompleteAdapter provideAutoCompleteAdapter(@ActivityContext Context context) {
        return new AutoCompleteAdapter(context, R.layout.item_auto_complete_texv_view, R.id.tv_item_ac_tv);
    }
}
