package com.ygaps.travelapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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
//        sendRegistrationToServer( FirebaseInstanceId.getInstance().getToken());
        Log.e("CHECKK", String.valueOf( check ));
        //check xem tai khoan da duoc dang nhap hay chua
        if (check){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.v("hihi", "getInstanceId failed", task.getException());
                                return;
                            }
                            // Get new Instance ID token
                            String fmcToken = task.getResult().getToken();
                            // Log and toast
                           // Log.e("FMC Token",fmcToken);
                            Log.d( "TTT", "onComplete: " + fmcToken +'-'+ sharedPreferences.getString( "token","" ));
                            sendRegistrationToServer(fmcToken);
                        }
                    });
                }
            }).start();

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

                    textView.setText("Home");
                    break;
                case R.id.navigation_history:
                    selectedFragment = new history_tour_user();

                    textView.setText("History");
                    break;
                case R.id.navigation_map:
                    selectedFragment = new fragment_explore();
                    textView.setText("Explore");
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
        final String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final String Authorization = sharedPreferences.getString( "token","" );
        if (!Authorization.equals(""))
            {FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener( new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    String token = task.getResult().getToken();
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
            } );
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

    private void sendRegistrationToServer(String fcmToken) {
        // TODO: Implement this method to send token to your app server.
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final String Authorization = sharedPreferences.getString( "token","" );
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .registerFirebase(Authorization,fcmToken, android_id,1 , "1.0");
        Log.e("Authorization",Authorization);
        Log.e("fcmToken",fcmToken);
        Log.e("deviceId",Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Firebase","OK");
                } else {
                    Toast.makeText(MainActivity.this, "Not ready for notification", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
