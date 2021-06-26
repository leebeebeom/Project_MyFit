package com.leebeebeom.closetnote.ui.main;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.ActivityMainBinding;
import com.leebeebeom.closetnote.util.KeyBoardUtil;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.experimental.Accessors;

import static com.leebeebeom.closetnote.ui.dialog.BaseDialog.ACTION_MODE_OFF;
import static com.leebeebeom.closetnote.util.ActionModeImpl.sActionMode;

//TODO 트리뷰 롱클릭으로 삭제, 이름변경?
//TODO 탭레이아웃 뱃지 텍스트 사이즈

//TODO 구현 못한 거
//이름 변경 시 분신술 쓰는 거(서치뷰, 메인 카테고리)

@AndroidEntryPoint
@Accessors(prefix = "m")
public class MainActivity extends AppCompatActivity {
    public final static String TAG = "로그";
    @Inject
    ActivityMainBinding mBinding;
    private int mTopFabOriginVisibility = View.INVISIBLE;
    @Getter
    private final MutableLiveData<Boolean> mKeyboardShowingLive = new MutableLiveData<>(false);
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.actionBar.toolBar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.host_fragment_main);
        if (navHostFragment != null)
            mNavController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.signInFragment, R.id.mainFragment, R.id.dailyLookFragment,
                R.id.wishListFragment, R.id.settingFragment).build();

        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.bottomNav, mNavController);

        mBinding.setActivity(this);
        mBinding.setLifecycleOwner(this);

        mKeyboardShowingLive.observe(this, showing -> {
            if (showing) {
                mTopFabOriginVisibility = mBinding.fabTop.getVisibility();
                mBinding.fabTop.hide();
            } else mBinding.fabTop.setVisibility(mTopFabOriginVisibility);
        });

        NavBackStackEntry mainBackStackEntry = mNavController.getBackStackEntry(R.id.nav_graph_main);
        MainGraphViewModel mainGraphViewModel = new ViewModelProvider(mainBackStackEntry, HiltViewModelFactory.create(this, mainBackStackEntry)).get(MainGraphViewModel.class);
        mainGraphViewModel.getBackStackEntryLive().observe(this, navBackStackEntry ->
                navBackStackEntry.getSavedStateHandle().getLiveData(ACTION_MODE_OFF).observe(navBackStackEntry, o -> {
                    if (sActionMode != null) sActionMode.finish();
                }));
    }

    private void firebaseAuthWithGoogle(String idToken) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() instanceof TextInputEditText || getCurrentFocus() instanceof MaterialAutoCompleteTextView) {
                View currentFocus = getCurrentFocus();
                Rect outRect = new Rect();
                currentFocus.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    currentFocus.clearFocus();
                    KeyBoardUtil.hideKeyBoard(currentFocus);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}