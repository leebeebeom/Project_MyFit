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
    public @interface ViewTypePreference {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ViewTypePreferenceLive {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FolderTogglePreference {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FolderTogglePreferenceLive {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ColorControl {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ColorPrimary {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FolderItemTouchHelper {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SizeItemTouchHelperList {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SizeItemTouchHelperGrid {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Padding8dp {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Padding60dp {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NavigationTextViewSize {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MainFab {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TopFab {
    }
}