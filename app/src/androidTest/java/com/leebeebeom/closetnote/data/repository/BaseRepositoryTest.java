package com.leebeebeom.closetnote.data.repository;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class BaseRepositoryTest extends TestCase {
    private CategoryRepository mCategoryRepository;

    @Before
    public void init(){
        mCategoryRepository = new CategoryRepository(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void test(){
        List<CategoryTuple> categoryTuples = new ArrayList<>();
        List<List<CategoryTuple>> classifiedTuplesByParentIndex = mCategoryRepository.getClassifiedTuplesByParentIndex(categoryTuples);
        classifiedTuplesByParentIndex.size();
    }
}