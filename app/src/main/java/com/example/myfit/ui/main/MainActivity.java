package com.example.myfit.ui.main;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myfit.R;
import com.example.myfit.databinding.ActivityMainBinding;
import com.example.myfit.util.CommonUtil;
import com.example.myfit.util.KeyBoardUtil;

//TODO 트리뷰 롱클릭으로 삭제, 이름변경?
//TODO 탭레이아웃 뱃지 텍스트 사이즈

//TODO 구현 못한 거
//이름 변경 시 분신술 쓰는 거(서치뷰, 메인 카테고리)

public class MainActivity extends AppCompatActivity {
    public static boolean isKeyboardShowing;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private int topFabOriginVisibility;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);

        navController = Navigation.findNavController(this, R.id.hostFragmentMain);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.mainFragment, R.id.dailyLookFragment,
                R.id.wishListFragment, R.id.settingFragment).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        //remove bottomNav shadow
        binding.bottomNav.setBackgroundTintList(null);
        //align bottomNav icons
        binding.bottomNav.getMenu().getItem(2).setEnabled(false);

        addDestinationChangeListener();
        addKeyboardShowingListener();
    }

    private void addDestinationChangeListener() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            mainFragment(destination.getId());
            listFragment(destination.getId());
            sizeFragment(destination.getId());
        });
    }

    private void mainFragment(int id) {
        if (id == R.id.mainFragment) {
            fabChange(R.drawable.icon_search);
            binding.tvCustomTitle.setVisibility(View.VISIBLE);
        } else binding.tvCustomTitle.setVisibility(View.GONE);
    }

    private void listFragment(int id) {
        if (id == R.id.listFragment) fabChange(R.drawable.icon_add);
    }

    private void sizeFragment(int id) {
        if (id == R.id.sizeFragment) fabChange(R.drawable.icon_save);
    }

    private void fabChange(int resId) {
        binding.fab.hide();
        binding.fab.setImageResource(resId);
        binding.fab.show();
    }

    private void addKeyboardShowingListener() {
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (KeyBoardUtil.isKeyboardShowing(binding.getRoot())) keyboardShow();
            else keyboardHide();
        });
    }

    private void keyboardShow() {
        if (!isKeyboardShowing) {
            isKeyboardShowing = true;
            binding.fab.setVisibility(View.INVISIBLE);
            binding.bottomAppBar.setVisibility(View.INVISIBLE);
            topFabOriginVisibility = binding.fabTop.getVisibility();
            binding.fabTop.setVisibility(View.INVISIBLE);
        }
    }

    private void keyboardHide() {
        if (isKeyboardShowing) {
            isKeyboardShowing = false;
            binding.bottomAppBar.setVisibility(View.VISIBLE);
            binding.fab.show();
            binding.fabTop.setVisibility(topFabOriginVisibility);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        CommonUtil.editTextLosableFocus(ev, getCurrentFocus(), this);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }
}