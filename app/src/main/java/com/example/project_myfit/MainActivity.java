package com.example.project_myfit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_myfit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //ToolBar
        setSupportActionBar(binding.toolbar);

        //Top Level Destination 지정
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment, R.id.favoriteFragment, R.id.settingFragment).build();

        //NavController
        mNavController = Navigation.findNavController(this, R.id.host_fragment);

        //액티비티의 액션바를 호스트 프래그먼트 안의 프래그먼트들이 공유할 수 있게 해줌.
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

        //바텀네비게이션뷰와 NavController 연결.
        NavigationUI.setupWithNavController(binding.bottomNav, mNavController);
    }

    //업버튼
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) ||
                super.onSupportNavigateUp();
    }

}