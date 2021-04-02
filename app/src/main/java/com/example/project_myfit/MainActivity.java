package com.example.project_myfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.FOLDER_ID;
import static com.example.project_myfit.MyFitConstant.SIZE_ID;

//TODO 디자인 패턴은 일단 나중에

//TODO 생명주기 안 코드들 메소드 화

//TODO 코드 최대한 간결하고 직관적으로

//TODO 변수 엥간하면 인자로 받기

//TODO 반복 코드들 유틸화

//TODO 트리뷰 롱클릭으로 삭제, 이름변경
//TODO 탭레이아웃 뱃지 텍스트 사이즈

//TODO 어댑터 셀렉트 포지션 포지션말고 아이디로

//TODO 트리뷰 폴더 추가시 [리스트뷰]랑 같은 순서로 추가되게(가능?)
//TODO 트리뷰 폴더 추가시 [서치뷰]랑 같은 순서로 추가되게(가능?)

//TODO 엔드아이콘 클릭하면 키보드 보이게

//TODO 서치뷰에서 폴더 추가시 뱃지 변경

//TODO 구현 못한 거
//이름 변경 시 분신술 쓰는 거(서치뷰, 메인 카테고리)
//트리뷰 리크리에이트 유지

public class MainActivity extends AppCompatActivity {
    //all checked
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainActivityViewModel model = new ViewModelProvider(this).get(MainActivityViewModel.class);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        //타이틀 안보이게(커스텀 타이틀 있음)
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.host_fragment);
        if (navHostFragment != null) mNavController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment, R.id.favoriteFragment, R.id.settingFragment)
                .build();

        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNav, mNavController);

        //바텀네비게이션 뷰 그림자 제거
        binding.bottomNav.setBackgroundTintList(null);
        //바텀네비게이션 뷰 정렬 맞추기
        binding.bottomNav.getMenu().getItem(2).setEnabled(false);

        //프래그먼트 변경 리스너
        destinationChangeListener(binding, actionBar, mNavController);

        if (getIntent() != null) {
            Intent intent = getIntent();
            if (intent.getIntExtra(SIZE_ID, 0) != 0) {
                //서치뷰 사이즈 클릭
                searchViewSizeClick(model, intent, mNavController);
            } else if (intent.getLongExtra(FOLDER_ID, 0) != 0) {
                //서치뷰 폴더 클릭
                searchViewFolderClick(model, intent, mNavController);
            }
        }
    }

    private void destinationChangeListener(ActivityMainBinding binding, ActionBar actionBar, @NotNull NavController navController) {
        //checked
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
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
        //checked
        binding.activityFab.hide();
        binding.activityFab.setImageResource(resId);
        binding.activityFab.show();
    }

    private void searchViewSizeClick(@NotNull MainActivityViewModel model, @NotNull Intent intent, NavController navController) {
        //checked
        Size size = model.getRepository().getSize(intent.getIntExtra(SIZE_ID, 0));
        model.setSize(size);

        Category category = model.getRepository().getCategory(size.getFolderId());
        Folder folder = model.getRepository().getFolder(size.getFolderId());
        if (category != null) model.setCategory(category);
        else {
            category = findParentCategory(folder, model);
            model.setCategory(category);
            model.setFolder(folder);
        }
        navController.navigate(R.id.action_mainFragment_to_inputOutputFragment);
    }

    private void searchViewFolderClick(@NotNull MainActivityViewModel model, @NotNull Intent intent, @NotNull NavController navController) {
        //checked
        Folder folder = model.getRepository().getFolder(intent.getLongExtra(FOLDER_ID, 0));
        Category category = findParentCategory(folder, model);
        model.setFolder(folder);
        model.setCategory(category);
        navController.navigate(R.id.action_mainFragment_to_listFragment);
    }

    @NotNull
    private Category findParentCategory(@NotNull Folder folder, @NotNull MainActivityViewModel model) {
        //checked
        Category category = model.getRepository().getCategory(folder.getFolderId());
        if (category == null) {
            Folder parentFolder = model.getRepository().getFolder(folder.getFolderId());
            category = findParentCategory(parentFolder, model);
        }
        return category;
    }

    @Override
    public boolean onSupportNavigateUp() {
        //checked
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}