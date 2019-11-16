package com.example.dulich;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.titlebar);
        textView = findViewById(R.id.titleBar);
        textView.setText("Travel Assistant");
        /*
        //check xem tai khoan da duoc dang nhap hay chua
        sharedPreferences = getSharedPreferences("isLogin",MODE_PRIVATE);


        //xu ly bottom navigation
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Thay doi fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new listTours()).commit();

         */

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()){
                case R.id.navigation_home:
                    selectedFragment = new listTours();

                    textView.setText("Travel Assistant");
                    break;
                case R.id.navigation_history:
                    selectedFragment = new tourDetail();

                    textView.setText("Tour Detail");
                    break;
                case R.id.navigation_map:
                    selectedFragment = new map();
                    textView.setText("Map");
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = new notifications();

                    textView.setText("Notifications");
                    break;
                case R.id.navigation_setting:

                    if (sharedPreferences.getBoolean("isLogIn", false))
                        selectedFragment = new user();
                    else
                       selectedFragment = new setting();

                    textView.setText("Setting");
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,selectedFragment).commit();
            return true;
        }
    };

}
