package com.ygaps.travelapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ygaps.travelapp.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class setting extends Fragment {
    int RC_SIGN_IN = 001;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    Button login;
    TextView signUp;
    TextView username;
    TextView password;
    private static final String TAG = setting.class.getSimpleName();
    CallbackManager callbackManager;
    LoginButton fbLoginButton;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login,container,false) ;

        signInButton = view.findViewById(R.id.sign_in_button);
        login = view.findViewById(R.id.logIn);
        username = view.findViewById( R.id.username );
        password = view.findViewById( R.id.password );
        preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        editor = preferences.edit();
        //Chuyen qua man hinh dang ky
        signUp = view.findViewById( R.id.signup );
        signUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new register();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.frame_container,fragment);
                fr.addToBackStack(null);
                fr.commit();
            }
        } );

        getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .logInUser(username.getText().toString(),password.getText().toString());

                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                        if (response.code()==200) {
                            String bodyLogin = null;
                            try {
                                bodyLogin = response.body().string();
                                try {
                                JSONObject jsonObject = new JSONObject(bodyLogin);
                                editor = preferences.edit();
                                editor.putString( "token",jsonObject.getString( "token") );
                                editor.putInt( "userID",jsonObject.getInt( "userId") );
                                editor.putString( "Email",username.getText().toString());
                                editor.putBoolean( "isLogIn",true );
                                editor.commit();
                                Fragment fragment = new user();
                                FragmentTransaction fr = getFragmentManager().beginTransaction();
                                fr.replace(R.id.frame_container,fragment);
                                fr.commit();
                                Toast.makeText( getContext(), "Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                            //    Log.i("DATA DATA", jsonObject.getString( "token"));
                            //    Log.i("DATA DATA", jsonObject.getString( "token"));
                            //    Log.i("DATA DATA", jsonObject.getString( "token"));


                                } catch (JSONException e) {
                                        e.printStackTrace();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        else if (response.code()==400)
                        {

                            Toast.makeText( getContext(), "Missing email/phone or password", Toast.LENGTH_LONG ).show();
                        }
                        else
                        {

                            Toast.makeText( getContext(), "Wrong email/phone or password", Toast.LENGTH_LONG ).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Toast.makeText( getContext(),t.getMessage(),Toast.LENGTH_LONG ).show();
                    }
                } );

            }
        } );

        //fb
        callbackManager =  CallbackManager.Factory.create();
        fbLoginButton = (LoginButton)view.findViewById(R.id.login_button);
        fbLoginButton.setFragment(this);
        //fb
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
               Log.i( "Login Facebook success.",loginResult.getAccessToken().getToken());
              //  Toast.makeText( getContext(), "Login FB thành công", Toast.LENGTH_SHORT ).show();
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .logInByFB(loginResult.getAccessToken().getToken());
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200)
                        {
                            try {
                                String bodyFB = response.body().string();
                                JSONObject jsonObject = new JSONObject(bodyFB);
                                editor = preferences.edit();
                               // editor.putString( "avatar",jsonObject.getString("avatar"));
                                editor.putString( "fullName",jsonObject.getString("userId"));
                                editor.putString( "token",jsonObject.getString( "token" ));
                                editor.putInt( "userID",jsonObject.getInt( "userId") );
                                editor.putBoolean( "isLogIn",true );
                                editor.commit();
                                Fragment fragment = new user();
                                FragmentTransaction fr = getFragmentManager().beginTransaction();
                                fr.replace(R.id.frame_container,fragment);
                                fr.commit();
                                Toast.makeText( getContext(), "Đăng nhập thành công",Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if( response.code()==400)
                        {
                            Toast.makeText( getContext(), "Login by facebook failed",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText( getContext(), "Error update or insert user",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText( getContext(),t.getMessage(),Toast.LENGTH_LONG ).show();
                    }
                } );
               // getFbInfo();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient( (MainActivity)getActivity(), gso);

        signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        } );
        return view;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount  account = GoogleSignIn.getLastSignedInAccount(getActivity());
            if(account==null)
            {
                account = completedTask.getResult(ApiException.class);
            }

            String token = account.getIdToken();

            getGGToken(token);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("ERROR", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void getGGToken( String token){
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .logInByGG(token);

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.code()==200) {
                    String bodyLogin = null;
                    try {
                        bodyLogin = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(bodyLogin);
                            editor = preferences.edit();
                            editor.putString( "avatar",jsonObject.getString("avatar"));
                            editor.putString( "fullName",jsonObject.getString("fullName"));
                            editor.putString("token", jsonObject.getString("token"));
                            editor.putInt("userID", jsonObject.getInt("userId"));
                            editor.commit();
                            Toast.makeText( getContext(), "Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                            //    Log.i("DATA DATA", jsonObject.getString( "token"));
                            //    Log.i("DATA DATA", jsonObject.getString( "token"));
                            //    Log.i("DATA DATA", jsonObject.getString( "token"));

                            Fragment fragment = new user();
                            FragmentTransaction fr = getFragmentManager().beginTransaction();
                            fr.replace(R.id.frame_container,fragment);
                            fr.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                else if (response.code()==400)
                {

                    Toast.makeText( getContext(), "Login by google failed", Toast.LENGTH_LONG ).show();
                }
                else
                {

                    Toast.makeText( getContext(), "Error update or insert user", Toast.LENGTH_LONG ).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText( getContext(),t.getMessage(),Toast.LENGTH_LONG ).show();
            }
        } );

    }
    //fb
    private void getFbInfo() {

        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(final JSONObject me, GraphResponse response) {

                            if (me != null) {

                                editor.putBoolean( "isLogIn",true );
                                try {
                                    //editor.putString( "avatar", me.getString(""));
                                    editor.putString( "Email", me.getString( "name" ));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                editor.commit();
                                Fragment fragment = new user();
                                FragmentTransaction fr = getFragmentManager().beginTransaction();
                                fr.replace(R.id.frame_container,fragment);
                                fr.commit();
                            }
                        }
                    });
            //Xử lý facebook
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();

        }
    }

}
