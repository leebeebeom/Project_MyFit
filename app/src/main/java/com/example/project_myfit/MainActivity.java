package com.example.project_myfit;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_myfit.databinding.ActivityMainBinding;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

//TODO 트리뷰 롱클릭으로 삭제, 이름변경?
//TODO 탭레이아웃 뱃지 텍스트 사이즈

//TODO 서치뷰 엔드아이콘 클릭하면 키보드 보이게

//TODO 구현 못한 거
//이름 변경 시 분신술 쓰는 거(서치뷰, 메인 카테고리)

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private boolean mIsKeyboardShowing;
    private int mTopFabOriginVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainActivityViewModel model = new ViewModelProvider(this).get(MainActivityViewModel.class);

        setSupportActionBar(binding.mainToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainHostFragment);
        if (navHostFragment != null) mNavController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.mainFragment, R.id.favoriteFragment, R.id.settingFragment).build();

        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.mainBottomNav, mNavController);

        //바텀네비게이션 뷰 그림자 제거
        binding.mainBottomNav.setBackgroundTintList(null);
        //바텀네비게이션 뷰 정렬
        binding.mainBottomNav.getMenu().getItem(2).setEnabled(false);

        //프래그먼트 변경 리스너
        destinationChangeListener(binding, actionBar);
        //키보드 쇼잉 리스너
        keyboardShowingListener(binding);

        if (getIntent().getExtras() != null && savedInstanceState == null) {
            if (MainActivityArgs.fromBundle(getIntent().getExtras()).getFolderId() != 0)
                searchViewFolderClick(model);
            else if (MainActivityArgs.fromBundle(getIntent().getExtras()).getSizeId() != 0)
                searchViewSizeClick(model);
        }
    }

    private void destinationChangeListener(ActivityMainBinding binding, ActionBar actionBar) {
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.mainFragment) {
                //메인 프래그먼트
                fabChange(binding, R.drawable.icon_search);
                binding.mainToolbarCustomTitle.setVisibility(View.VISIBLE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);//커스텀 타이틀
            } else if (destination.getId() == R.id.listFragment) {
                //리스트 프래그먼트
                fabChange(binding, R.drawable.icon_add);
                binding.mainToolbarCustomTitle.setVisibility(View.GONE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(true);//커스텀 타이틀 GONE
            } else if (destination.getId() == R.id.inputOutputFragment) {
                //인풋아웃풋 프래그먼트
                fabChange(binding, R.drawable.icon_save);
                binding.mainToolbarCustomTitle.setVisibility(View.GONE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);//모든 타이틀 숨기기
            }
        });
    }

    private void fabChange(@NotNull ActivityMainBinding binding, int resId) {
        binding.mainActivityFab.hide();
        binding.mainActivityFab.setImageResource(resId);
        binding.mainActivityFab.show();
    }

    private void keyboardShowingListener(@NotNull ActivityMainBinding binding) {
        View rootView = binding.getRoot();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);

            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15)
                keyboardShow(binding);
            else keyboardHide(binding);
        });
    }

    private void keyboardShow(@NotNull ActivityMainBinding binding) {
        if (!mIsKeyboardShowing) {
            mIsKeyboardShowing = true;
            binding.mainActivityFab.setVisibility(View.INVISIBLE);
            binding.mainBottomAppBar.setVisibility(View.INVISIBLE);
            mTopFabOriginVisibility = binding.mainTopFab.getVisibility();
            binding.mainTopFab.setVisibility(View.INVISIBLE);
        }
    }

    private void keyboardHide(@NotNull ActivityMainBinding binding) {
        if (mIsKeyboardShowing) {
            mIsKeyboardShowing = false;
            binding.mainBottomAppBar.setVisibility(View.VISIBLE);
            binding.mainActivityFab.show();
            binding.mainTopFab.setVisibility(mTopFabOriginVisibility);
        }
    }

    private void searchViewSizeClick(@NotNull MainActivityViewModel model) {
        long parentId = MainActivityArgs.fromBundle(getIntent().getExtras()).getParentId();
        long sizeId = MainActivityArgs.fromBundle(getIntent().getExtras()).getSizeId();
        String parentCategory = model.getSizeParentCategory(parentId);

        Bundle bundle = new Bundle();
        bundle.putLong("parentId", parentId);
        bundle.putLong("sizeId", sizeId);
        bundle.putString("parentCategory", parentCategory);

        NavGraph graph = mNavController.getGraph();
        graph.setStartDestination(R.id.inputOutputFragment);
        mNavController.setGraph(graph, bundle);
    }

    private void searchViewFolderClick(@NotNull MainActivityViewModel model) {
        long parentId = MainActivityArgs.fromBundle(getIntent().getExtras()).getParentId();
        long folderId = MainActivityArgs.fromBundle(getIntent().getExtras()).getFolderId();
        long categoryId = model.getCategoryId(parentId);
        String parentCategory = model.getParentCategory();

        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", categoryId);
        bundle.putLong("folderId", folderId);
        bundle.putString("parentCategory", parentCategory);

        NavGraph graph = mNavController.getGraph();
        graph.setStartDestination(R.id.listFragment);
        mNavController.setGraph(graph, bundle);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof TextInputEditText || v instanceof MaterialAutoCompleteTextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}