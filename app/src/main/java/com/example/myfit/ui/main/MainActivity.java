package com.example.myfit.ui.main;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myfit.databinding.ActivityMainBinding;
import com.example.myfit.util.KeyBoardUtil;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.experimental.Accessors;

//TODO 트리뷰 롱클릭으로 삭제, 이름변경?
//TODO 탭레이아웃 뱃지 텍스트 사이즈

//TODO 구현 못한 거
//이름 변경 시 분신술 쓰는 거(서치뷰, 메인 카테고리)

@AndroidEntryPoint
@Accessors(prefix = "m")
public class MainActivity extends AppCompatActivity {
    @Inject
    NavController mNavController;
    @Inject
    ActivityMainBinding mBinding;
    @Inject
    AppBarConfiguration mAppBarConfiguration;

    @Getter
    private final MutableLiveData<Boolean> mKeyboardShowing = new MutableLiveData<>(false);
    private int mTopFabOriginVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding.setActivity(this);
        mBinding.setLifecycleOwner(this);

        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.bottomNav, mNavController);

        mNavController.addOnDestinationChangedListener((controller, destination, arguments) ->
                mBinding.setDestinationId(destination.getId()));

        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() ->
                mKeyboardShowing.setValue(KeyBoardUtil.isKeyboardShowing(mBinding.getRoot())));

        mKeyboardShowing.observe(this, showing -> {
            if (showing) {
                mTopFabOriginVisibility = mBinding.fabTop.getVisibility();
                mBinding.fabTop.setVisibility(View.INVISIBLE);
            } else mBinding.fabTop.setVisibility(mTopFabOriginVisibility);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() instanceof TextInputEditText || getCurrentFocus() instanceof MaterialAutoCompleteTextView) {
                Rect outRect = new Rect();
                getCurrentFocus().getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    getCurrentFocus().clearFocus();
                    KeyBoardUtil.hideKeyBoard(getCurrentFocus());
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}