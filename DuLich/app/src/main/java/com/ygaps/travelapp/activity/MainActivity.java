package com.ygaps.travelapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ygaps.travelapp.MyCustomDialog;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.RetrofitClient;
import com.ygaps.travelapp.fragment_explore;
import com.ygaps.travelapp.history_tour_user;
import com.ygaps.travelapp.listTours;
import com.ygaps.travelapp.notifications;
import com.ygaps.travelapp.setting;
import com.ygaps.travelapp.user;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MyCustomDialog.NoticeDialogListener{
    TextView textView;
    SharedPreferences sharedPreferences;
    boolean check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.titlebar);
        textView = findViewById(R.id.titleBar);
        textView.setText("Travel Assistant");
        sharedPreferences = getSharedPreferences("isLogin",MODE_PRIVATE);
        check = sharedPreferences.getBoolean("isLogIn", false);
        //Log.e("CHECKK", String.valueOf( check ));
        //check xem tai khoan da duoc dang nhap hay chua
        if (check){
            FirebaseApp.initializeApp(this);
            new Thread( new Runnable() {
                @Override
                public void run() {
                    fireBase();
                }
            } ).start();

        }

       /* FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e( "newToken", newToken );
                //getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
            }
        } );
        */

        //xu ly bottom navigation
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Thay doi fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new listTours()).commit();


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
                    selectedFragment = new history_tour_user();

                    textView.setText("Tour Detail");
                    break;
                case R.id.navigation_map:
                    selectedFragment = new fragment_explore();
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
    void fireBase(){
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String Authorization = sharedPreferences.getString( "token","" );
        if (!Authorization.equals(""))
        {
            //Log.e("AAAAAAAAA",Authorization);
            String token = FirebaseInstanceId.getInstance().getToken();
            //Log.e( "TOKENNNNNNNNNNNNNNNNN", token );
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .register_Firebase(Authorization,token,android_id,1,"1.0");

            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200){
                        try {
                            String body = response.body().string();
                            Log.i("Firebase", body);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Log.i("Firebase", "false");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Fragment selectedFragment = new history_tour_user();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,selectedFragment).commit();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
