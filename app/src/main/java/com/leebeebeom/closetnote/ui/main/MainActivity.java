package com.leebeebeom.closetnote.ui.main;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.actionBar.toolBar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.host_fragment_main);
        if (navHostFragment != null)
            mNavController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.loginFragment, R.id.mainFragment, R.id.dailyLookFragment,
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    //TODO
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "로그인 성공 id: " + account.getId(), Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO
//        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() ->
//                mKeyboardShowingLive.setValue(KeyBoardUtil.isKeyboardShowing(mBinding.getRoot())));

//        FirebaseUser firebaseUser = mAuth.getCurrentUser();
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