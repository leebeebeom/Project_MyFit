package com.example.myfit.di;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.myfit.R;
import com.example.myfit.databinding.ActivityMainBinding;

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
    public static ActivityMainBinding provideActivityMainBinding(Activity activity) {
        return ActivityMainBinding.inflate(activity.getLayoutInflater());
    }

    @ActivityScoped
    @Provides
    public static NavController provideNavController(Activity activity) {
        return Navigation.findNavController(activity, R.id.host_fragment_main);
    }

    @Provides
    public static AppBarConfiguration provideAppBarConfiguration() {
        return new AppBarConfiguration.Builder(R.id.mainFragment, R.id.dailyLookFragment,
                R.id.wishListFragment, R.id.settingFragment).build();
    }

    @Qualifiers.ColorPrimary
    @ActivityScoped
    @Provides
    public static int provideColorPrimary(@ActivityContext Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        return typedValue.data;
    }

    @Qualifiers.ColorControl
    @ActivityScoped
    @Provides
    public static int provideColorControl(@ActivityContext Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
}
