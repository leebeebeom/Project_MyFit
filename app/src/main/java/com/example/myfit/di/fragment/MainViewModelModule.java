package com.example.myfit.di.fragment;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.ui.main.main.adapter.CategoryAdapter;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.BaseViewPagerAdapter;
import com.example.myfit.util.adapter.dragcallback.DragCallBackList;
import com.example.myfit.util.dragselect.DragSelect;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class MainViewModelModule {

    @ViewModelScoped
    @Provides
    public static BaseViewPagerAdapter.MainViewPagerAdapter provideMainViewPagerAdapter(CategoryAdapter[] categoryAdapters,
                                                                                        DragSelect[] dragSelects,
                                                                                        ItemTouchHelper[] itemTouchHelpers) {
        return new BaseViewPagerAdapter.MainViewPagerAdapter(categoryAdapters, dragSelects, itemTouchHelpers);
    }

    @ViewModelScoped
    @Provides
    public static CategoryAdapter[] provideCategoryAdapters(SizeLiveSet<CategoryTuple> selectedCategories) {
        return new CategoryAdapter[]{new CategoryAdapter(selectedCategories), new CategoryAdapter(selectedCategories), new CategoryAdapter(selectedCategories), new CategoryAdapter(selectedCategories)};
    }

    @Provides
    public static ItemTouchHelper[] provideMainItemTouchHelpers(CategoryAdapter[] categoryAdapters) {
        return new ItemTouchHelper[]{new ItemTouchHelper(new DragCallBackList(categoryAdapters[0])),
                new ItemTouchHelper(new DragCallBackList(categoryAdapters[1])),
                new ItemTouchHelper(new DragCallBackList(categoryAdapters[2])),
                new ItemTouchHelper(new DragCallBackList(categoryAdapters[3]))
        };
    }

    @Provides
    public static DragSelect[] provideDragSelects() {
        return new DragSelect[]{new DragSelect(), new DragSelect(), new DragSelect(), new DragSelect()};
    }
}
