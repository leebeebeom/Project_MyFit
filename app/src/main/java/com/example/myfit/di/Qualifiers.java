package com.example.myfit.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

public class Qualifiers {
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
    public @interface NavigationTextViewSize {
    }
}