package com.example.myfit.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

public class Qualifiers {
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MainSortPreference {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MainSortPreferenceLive {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ListSortPreference {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ListSortPreferenceLive {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CategoryVHListener {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MainViewPagerAdapter {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MainActionModeListener {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MainAutoScrollListener {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ColorControl{}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ColorPrimary{}
}