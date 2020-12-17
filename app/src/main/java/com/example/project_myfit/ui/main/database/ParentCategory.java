package com.example.project_myfit.ui.main.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class ParentCategory extends ExpandableGroup<ChildCategory> {

    public ParentCategory(String title, List<ChildCategory> items) {
        super(title, items);
    }
}
