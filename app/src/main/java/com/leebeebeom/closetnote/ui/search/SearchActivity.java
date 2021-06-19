//package com.leebeebeom.closetnote.ui.search;
//
//import android.os.Bundle;
//import android.view.MotionEvent;
//import android.view.View;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MediatorLiveData;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.leebeebeom.closetnote.R;
//import com.leebeebeom.closetnote.databinding.ActivitySearchBinding;
//import com.leebeebeom.closetnote.util.adapter.AutoCompleteAdapter;
//import com.leebeebeom.closetnote.util.CommonUtil;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//public class SearchActivity extends AppCompatActivity {
//
//    public ActivitySearchBinding mBinding;
//    private int mTopFabOriginVisibility;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mBinding = ActivitySearchBinding.inflate(getLayoutInflater());
//        setContentView(mBinding.getRoot());
//
//        setSupportActionBar(mBinding.toolBar);
//
//        SearchViewModel model = new ViewModelProvider(this).get(SearchViewModel.class);
//
//        keyboardShowingListener();
//
//        mBinding.acTv.requestFocus();
//        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.item_auto_complete_texv_view, R.id.tv_item_ac_tv);
//        mBinding.acTv.setAdapter(autoCompleteAdapter);
//        acLive(model, autoCompleteAdapter);
//    }
//
//    private void keyboardShowingListener() {
//        View rootView = mBinding.getRoot();
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            if (CommonUtil.isKeyboardShowing(rootView))
//                keyboardShowing();
//            else keyboardHide();
//        });
//
//    }
//
//    private void keyboardShowing() {
//        if (!MyFitVariable.isKeyboardShowing) {
//            MyFitVariable.isKeyboardShowing = true;
//            mTopFabOriginVisibility = mBinding.fabTop.getVisibility();
//            mBinding.fabTop.setVisibility(View.GONE);
//        }
//    }
//
//    private void keyboardHide() {
//        if(MyFitVariable.isKeyboardShowing){
//            MyFitVariable.isKeyboardShowing = false;
//            mBinding.fabTop.setVisibility(mTopFabOriginVisibility);
//        }
//    }
//
//    private void acLive(@NotNull SearchViewModel model, AutoCompleteAdapter autoCompleteAdapter) {
//        LiveData<List<String>> folderNameLive = model.getFolderNameLive();
//        LiveData<List<String>> sizeBrandLive = model.getSizeBrandLive();
//        LiveData<List<String>> sizeNameLive = model.getSizeNameLive();
//
//        MediatorLiveData<List<String>> mediatorLiveData = new MediatorLiveData<>();
//        mediatorLiveData.addSource(folderNameLive, mediatorLiveData::setValue);
//        mediatorLiveData.addSource(sizeBrandLive, mediatorLiveData::setValue);
//        mediatorLiveData.addSource(sizeNameLive, mediatorLiveData::setValue);
//
//        HashSet<String> autoCompleteHashSet = new HashSet<>();
//        List<String> autoCompleteList = new ArrayList<>();
//        mediatorLiveData.observe(this, stringList -> {
//            autoCompleteHashSet.clear();
//            autoCompleteList.clear();
//
//            acListAddValue(folderNameLive, autoCompleteHashSet);
//            acListAddValue(sizeBrandLive, autoCompleteHashSet);
//            acListAddValue(sizeNameLive, autoCompleteHashSet);
//
//            autoCompleteList.addAll(autoCompleteHashSet);
//            autoCompleteList.sort(String::compareTo);
//
//            autoCompleteAdapter.setItem(autoCompleteList);
//        });
//    }
//
//    private void acListAddValue(@NotNull LiveData<List<String>> liveData, HashSet<String> autoCompleteHashSet) {
//        if (liveData.getValue() != null)
//            for (String s : liveData.getValue()) autoCompleteHashSet.add(s.trim());
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
//        CommonUtil.editTextLosableFocus(ev, getCurrentFocus(), this);
//        return super.dispatchTouchEvent(ev);
//    }
//}