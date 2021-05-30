package com.example.myfit.di.fragment;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.myfit.ui.main.main.adapter.CategoryAdapter;
import com.example.myfit.util.adapter.dragcallback.DragCallBackList;
import com.example.myfit.util.dragselect.DragSelect;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.scopes.FragmentScoped;

@Module
@InstallIn(FragmentComponent.class)
public class MainFragmentModule {

    @FragmentScoped
    @Provides
    public static CategoryAdapter[] provideCategoryAdapters(CategoryAdapter categoryAdapter) throws CloneNotSupportedException {
        return new CategoryAdapter[]{categoryAdapter,
                categoryAdapter.clone(),
                categoryAdapter.clone(),
                categoryAdapter.clone()};
    }

    @FragmentScoped
    @Provides
    public static ItemTouchHelper[] provideMainItemTouchHelpers(CategoryAdapter[] categoryAdapters) {
        return new ItemTouchHelper[]{new ItemTouchHelper(new DragCallBackList(categoryAdapters[0])),
                new ItemTouchHelper(new DragCallBackList(categoryAdapters[1])),
                new ItemTouchHelper(new DragCallBackList(categoryAdapters[2])),
                new ItemTouchHelper(new DragCallBackList(categoryAdapters[3]))
        };
    }

    @FragmentScoped
    @Provides
    public static DragSelect[] provideDragSelects(DragSelect dragSelect) throws CloneNotSupportedException {
        return new DragSelect[]{dragSelect, dragSelect.clone(), dragSelect.clone(), dragSelect.clone()};
    }
}
