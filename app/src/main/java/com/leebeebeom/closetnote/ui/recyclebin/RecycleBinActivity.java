//package com.leebeebeom.closetnote.ui.recyclebin;
//
//import android.os.Bundle;
//import android.view.MotionEvent;
//import android.view.View;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.navigation.NavController;
//import androidx.navigation.fragment.NavHostFragment;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
//
//import com.leebeebeom.closetnote.R;
//import com.leebeebeom.closetnote.databinding.ActivityRecycleBinBinding;
//import com.leebeebeom.closetnote.util.CommonUtil;
//
//import org.jetbrains.annotations.NotNull;
//
//public class RecycleBinActivity extends AppCompatActivity {
//
//    private NavController mNavController;
//    private AppBarConfiguration mAppBarConfiguration;
//    public ActivityRecycleBinBinding mBinding;
//    private int mTopFabOriginVisibility;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mBinding = ActivityRecycleBinBinding.inflate(getLayoutInflater());
//        setContentView(mBinding.getRoot());
//
//        setSupportActionBar(mBinding.toolBar);
//        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.hostFragmentRecycleBin);
//        if (navHostFragment != null) mNavController = navHostFragment.getNavController();
//
//        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.recycleBinMain).build();
//
//        keyboardShowingListener();
//
//        destinationChangeListener();
//    }
//
//    private void keyboardShowingListener() {
//        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            if (CommonUtil.isKeyboardShowing(mBinding.getRoot()))
//                keyboardShow();
//            else keyboardHide();
//        });
//    }
//
//    private void keyboardShow() {
//        if (!MyFitVariable.isKeyboardShowing) {
//            MyFitVariable.isKeyboardShowing = true;
//            mTopFabOriginVisibility = mBinding.fabTop.getVisibility();
//            mBinding.fabTop.setVisibility(View.GONE);
//        }
//    }
//
//    private void keyboardHide() {
//        if (MyFitVariable.isKeyboardShowing) {
//            MyFitVariable.isKeyboardShowing = false;
//            mBinding.fabTop.setVisibility(mTopFabOriginVisibility);
//        }
//    }
//
//    private void destinationChangeListener() {
//        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            if (destination.getId() == R.id.recycleBinMain) {
//                mBinding.tvTitle.setVisibility(View.VISIBLE);
//                mBinding.acTvLayout.setVisibility(View.GONE);
//            } else if (destination.getId() == R.id.recycleBinSearch) {
//                mBinding.tvTitle.setVisibility(View.GONE);
//                mBinding.acTvLayout.setVisibility(View.VISIBLE);
//            }
//        });
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
//        CommonUtil.editTextLosableFocus(ev, getCurrentFocus(), this);
//        return super.dispatchTouchEvent(ev);
//    }
//}