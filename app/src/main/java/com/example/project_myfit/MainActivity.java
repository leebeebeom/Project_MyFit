package com.example.project_myfit;

import android.graphics.Rect;
import android.os.Bundle;
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

import org.jetbrains.annotations.NotNull;

//TODO 트리뷰 롱클릭으로 삭제, 이름변경?
//TODO 탭레이아웃 뱃지 텍스트 사이즈

//TODO 서치뷰 엔드아이콘 클릭하면 키보드 보이게

//TODO 서치뷰에서 폴더 추가시 뱃지 변경

//TODO 구현 못한 거
//이름 변경 시 분신술 쓰는 거(서치뷰, 메인 카테고리)
//서치뷰에서 폴더, 사이즈 클릭 후 리크리에이트시 어플 종료

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

        MainActivityModel model = new ViewModelProvider(this).get(MainActivityModel.class);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.host_fragment);
        if (navHostFragment != null) mNavController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.mainFragment, R.id.favoriteFragment, R.id.settingFragment).build();

        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNav, mNavController);

        //바텀네비게이션 뷰 그림자 제거
        binding.bottomNav.setBackgroundTintList(null);
        //바텀네비게이션 뷰 정렬 맞추기
        binding.bottomNav.getMenu().getItem(2).setEnabled(false);

        //프래그먼트 변경 리스너
        destinationChangeListener(binding, actionBar);
        //키보드 쇼잉 리스너
        keyBoardShowingListener(binding);

        if (getIntent().getExtras() != null) {
            if (MainActivityArgs.fromBundle(getIntent().getExtras()).getFolderId() != 0) {
                long parentId = MainActivityArgs.fromBundle(getIntent().getExtras()).getParentId();
                long folderId = MainActivityArgs.fromBundle(getIntent().getExtras()).getFolderId();
                long categoryId = model.getCategoryId(parentId);
                String parentCategory = model.getParentCategory();

                Bundle bundle = new Bundle();
                bundle.putLong("category_id", categoryId);
                bundle.putLong("folder_id", folderId);
                bundle.putString("parent_category", parentCategory);

                NavGraph graph = mNavController.getGraph();
                graph.setStartDestination(R.id.listFragment);
                mNavController.setGraph(graph, bundle);

            } else if (MainActivityArgs.fromBundle(getIntent().getExtras()).getSizeId() != 0) {
                long parentId = MainActivityArgs.fromBundle(getIntent().getExtras()).getParentId();
                long sizeId = MainActivityArgs.fromBundle(getIntent().getExtras()).getSizeId();
                String parentCategory = model.getSizeParentCategory(parentId);

                Bundle bundle = new Bundle();
                bundle.putLong("parent_id", parentId);
                bundle.putLong("size_id", sizeId);
                bundle.putString("parent_category", parentCategory);

                NavGraph graph = mNavController.getGraph();
                graph.setStartDestination(R.id.inputOutputFragment);
                mNavController.setGraph(graph, bundle);
            }
        }
    }

    private void destinationChangeListener(ActivityMainBinding binding, ActionBar actionBar) {
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.mainFragment) {
                //메인 프래그먼트
                fabChange(binding, R.drawable.icon_search);
                binding.toolbarCustomTitle.setVisibility(View.VISIBLE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);//커스텀 타이틀
            } else if (destination.getId() == R.id.listFragment) {
                //리스트 프래그먼트
                fabChange(binding, R.drawable.icon_add);
                binding.toolbarCustomTitle.setVisibility(View.GONE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(true);//커스텀 타이틀 GONE
            } else if (destination.getId() == R.id.inputOutputFragment) {
                //인풋아웃풋 프래그먼트
                fabChange(binding, R.drawable.icon_save);
                binding.toolbarCustomTitle.setVisibility(View.GONE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);//모든 타이틀 숨기기
            }
        });
    }

    private void fabChange(@NotNull ActivityMainBinding binding, int resId) {
        binding.activityFab.hide();
        binding.activityFab.setImageResource(resId);
        binding.activityFab.show();
    }

    private void keyBoardShowingListener(@NotNull ActivityMainBinding binding) {
        View rootView = binding.getRoot();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);

            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                if (!mIsKeyboardShowing) {
                    mIsKeyboardShowing = true;
                    binding.activityFab.setVisibility(View.INVISIBLE);
                    binding.bottomAppBar.setVisibility(View.INVISIBLE);
                    mTopFabOriginVisibility = binding.topFab.getVisibility();
                    binding.topFab.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mIsKeyboardShowing) {
                    mIsKeyboardShowing = false;
                    binding.bottomAppBar.setVisibility(View.VISIBLE);
                    binding.activityFab.show();
                    binding.topFab.setVisibility(mTopFabOriginVisibility);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}