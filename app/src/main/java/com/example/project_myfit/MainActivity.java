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

import com.example.project_myfit.databinding.ActivityMainBinding;
import com.example.project_myfit.data.Category;
import com.example.project_myfit.data.Folder;
import com.example.project_myfit.data.Size;

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

//TODO 트리뷰 나올때 [리스트뷰]랑 같은 순서로 나오게
//TODO 트리뷰 폴더 추가시 [리스트뷰]랑 같은 순서로 추가되게(가능?)
//TODO 트리뷰 나올때 [서치뷰]랑 같은 순서로 나오게
//TODO 트리뷰 폴더 추가시 [서치뷰]랑 같은 순서로 추가되게(가능?)

//TODO 엔드아이콘 클릭하면 키보드 보이게

//TODO 서치뷰에서 폴더 추가시 뱃지 변경

//TODO 구현 못한 거
//이름 변경 시 분신술 쓰는 거(서치뷰, 메인 카테고리)
//트리뷰 리크리에이트 유지

public class MainActivity extends AppCompatActivity {
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //view model
        MainActivityViewModel model = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //toolbar
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        //타이틀 안보이게(커스텀 타이틀 있음)
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);

        //navigation controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.host_fragment);
        if (navHostFragment != null) mNavController = navHostFragment.getNavController();

        //앱 바 연결
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment, R.id.favoriteFragment, R.id.settingFragment)
                .build();

        //action bar share
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

        //connect bottom navigation with navController
        NavigationUI.setupWithNavController(binding.bottomNav, mNavController);

        //bottom nav setting
        binding.bottomNav.setBackgroundTintList(null);
        binding.bottomNav.getMenu().getItem(2).setEnabled(false);

        //프래그먼트 변경 리스너
        destinationChangeListener(binding, actionBar);

        if (getIntent() != null) {
            Intent intent = getIntent();
            if (intent.getIntExtra(SIZE_ID, 0) != 0) {
                //서치뷰 사이즈 클릭
                searchViewSizeClick(model, intent);
            } else if (intent.getLongExtra(FOLDER_ID, 0) != 0) {
                //서치뷰 폴더 클릭
                searchViewFolderClick(model, intent);
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

    private void searchViewSizeClick(@NotNull MainActivityViewModel model, @NotNull Intent intent) {
        Size size = model.getRepository().getSize(intent.getIntExtra(SIZE_ID, 0));
        model.setSize(size);

        Category category = model.getRepository().getCategory(size.getFolderId());
        Folder folder = model.getRepository().getFolder(size.getFolderId());
        if (category != null) model.setCategory(category);
        else {
            category = getCategory(folder, model);
            model.setCategory(category);
            model.setFolder(folder);
        }
        mNavController.navigate(R.id.action_mainFragment_to_inputOutputFragment);
    }

    private void searchViewFolderClick(@NotNull MainActivityViewModel model, @NotNull Intent intent) {
        Folder folder = model.getRepository().getFolder(intent.getLongExtra(FOLDER_ID, 0));
        Category category = getCategory(folder, model);
        model.setFolder(folder);
        model.setCategory(category);
        mNavController.navigate(R.id.action_mainFragment_to_listFragment);
    }

    @NotNull
    private Category getCategory(@NotNull Folder folder, @NotNull MainActivityViewModel model) {
        Category category = model.getRepository().getCategory(folder.getFolderId());
        if (category == null) {
            Folder newFolder = model.getRepository().getFolder(folder.getFolderId());
            category = getCategory(newFolder, model);
        }
        return category;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setSupportActionBar(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}