package com.example.project_myfit;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_myfit.databinding.ActivityMainBinding;
import com.example.project_myfit.util.CommonUtil;
import com.example.project_myfit.util.MyFitVariable;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY_ID;
import static com.example.project_myfit.util.MyFitConstant.FOLDER_ID;
import static com.example.project_myfit.util.MyFitConstant.PARENT_CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.PARENT_ID;
import static com.example.project_myfit.util.MyFitConstant.SIZE_ID;

//TODO 트리뷰 롱클릭으로 삭제, 이름변경?
//TODO 탭레이아웃 뱃지 텍스트 사이즈

//TODO 구현 못한 거
//이름 변경 시 분신술 쓰는 거(서치뷰, 메인 카테고리)

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private int mTopFabOriginVisibility;
    public ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        MainActivityViewModel model = new ViewModelProvider(this).get(MainActivityViewModel.class);

        setSupportActionBar(mBinding.toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.hostFragmentMain);
        if (navHostFragment != null) mNavController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.mainFragment, R.id.dailyLookFragment,
                R.id.wishListFragment, R.id.settingFragment).build();

        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.bottomNav, mNavController);

        //바텀네비게이션 뷰 그림자 제거
        mBinding.bottomNav.setBackgroundTintList(null);
        //바텀네비게이션 뷰 정렬
        mBinding.bottomNav.getMenu().getItem(2).setEnabled(false);

        //프래그먼트 변경 리스너
        destinationChangeListener(actionBar);
        //키보드 쇼잉 리스너
        keyboardShowingListener();

        if (getIntent().getExtras() != null && savedInstanceState == null) {
            if (MainActivityArgs.fromBundle(getIntent().getExtras()).getFolderId() != 0)
                searchViewFolderClick(model);
            else if (MainActivityArgs.fromBundle(getIntent().getExtras()).getSizeId() != 0)
                searchViewSizeClick(model);
        }
    }

    private void destinationChangeListener(ActionBar actionBar) {
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.mainFragment) {
                //메인 프래그먼트
                fabChange(R.drawable.icon_search);
                mBinding.tvCustomTitle.setVisibility(View.VISIBLE);
                actionBar.setDisplayShowTitleEnabled(false);//커스텀 타이틀
            } else if (destination.getId() == R.id.listFragment) {
                //리스트 프래그먼트
                fabChange(R.drawable.icon_add);
                mBinding.tvCustomTitle.setVisibility(View.GONE);
                actionBar.setDisplayShowTitleEnabled(true);//커스텀 타이틀 GONE
            } else if (destination.getId() == R.id.inputOutputFragment) {
                //인풋아웃풋 프래그먼트
                fabChange(R.drawable.icon_save);
                mBinding.tvCustomTitle.setVisibility(View.GONE);
                actionBar.setDisplayShowTitleEnabled(false);//모든 타이틀 숨기기
            }
        });
    }

    private void fabChange(int resId) {
        mBinding.fab.hide();
        mBinding.fab.setImageResource(resId);
        mBinding.fab.show();
    }

    private void keyboardShowingListener() {
        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (CommonUtil.isKeyboardShowing(mBinding.getRoot())) keyboardShow();
            else keyboardHide();
        });
    }

    private void keyboardShow() {
        if (!MyFitVariable.isKeyboardShowing) {
            MyFitVariable.isKeyboardShowing = true;
            mBinding.fab.setVisibility(View.INVISIBLE);
            mBinding.bottomAppBar.setVisibility(View.INVISIBLE);
            mTopFabOriginVisibility = mBinding.fabTop.getVisibility();
            mBinding.fabTop.setVisibility(View.INVISIBLE);
        }
    }

    private void keyboardHide() {
        if (MyFitVariable.isKeyboardShowing) {
            MyFitVariable.isKeyboardShowing = false;
            mBinding.bottomAppBar.setVisibility(View.VISIBLE);
            mBinding.fab.show();
            mBinding.fabTop.setVisibility(mTopFabOriginVisibility);
        }
    }

    private void searchViewFolderClick(@NotNull MainActivityViewModel model) {
        long parentId = MainActivityArgs.fromBundle(getIntent().getExtras()).getParentId();
        long folderId = MainActivityArgs.fromBundle(getIntent().getExtras()).getFolderId();
        long categoryId = model.getCategoryId(parentId);
        String parentCategory = model.getParentCategory();

        Bundle bundle = new Bundle();
        bundle.putLong(CATEGORY_ID, categoryId);
        bundle.putLong(FOLDER_ID, folderId);
        bundle.putString(PARENT_CATEGORY, parentCategory);

        NavGraph graph = mNavController.getGraph();
        graph.setStartDestination(R.id.listFragment);
        mNavController.setGraph(graph, bundle);
    }

    private void searchViewSizeClick(@NotNull MainActivityViewModel model) {
        long parentId = MainActivityArgs.fromBundle(getIntent().getExtras()).getParentId();
        long sizeId = MainActivityArgs.fromBundle(getIntent().getExtras()).getSizeId();
        String parentCategory = model.getSizeParentCategory(parentId);

        Bundle bundle = new Bundle();
        bundle.putLong(PARENT_ID, parentId);
        bundle.putLong(SIZE_ID, sizeId);
        bundle.putString(PARENT_CATEGORY, parentCategory);

        NavGraph graph = mNavController.getGraph();
        graph.setStartDestination(R.id.inputOutputFragment);
        mNavController.setGraph(graph, bundle);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
        CommonUtil.editTextLosableFocus(ev, getCurrentFocus(), this);
        return super.dispatchTouchEvent(ev);
    }
}