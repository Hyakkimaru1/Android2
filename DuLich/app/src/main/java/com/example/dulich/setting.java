package com.example.dulich;

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
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


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
                                editor.putString( "isLogIn",jsonObject.getString( "token") );
                                Toast.makeText( getContext(), "Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                            //    Log.i("DATA DATA", jsonObject.getString( "token"));
                            //    Log.i("DATA DATA", jsonObject.getString( "token"));
                            //    Log.i("DATA DATA", jsonObject.getString( "token"));

                                Fragment fragment = new listTours();
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
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = view.findViewById(R.id.login_button);
        //fb
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "======Facebook login success======");
                Log.d(TAG, "Facebook Access Token: " + loginResult.getAccessToken().getToken());
                //Toast.makeText(setting.this, "Login Facebook success.",LENGTH_SHORT).show();

                getFbInfo();
            }

            @Override
            public void onCancel() {
                //Toast.makeText(setting.this, "Login Facebook cancelled.", LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "======Facebook login error======");
                Log.e(TAG, "Error: " + error.toString());
                //Toast.makeText(setting.this, "Login Facebook error.", LENGTH_SHORT).show();
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            SharedPreferences preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean( "isLogIn",true );
            if (account.getPhotoUrl()!=null)
                editor.putString( "avatar",account.getPhotoUrl().toString());
            editor.putString( "Email",account.getEmail());
            editor.commit();
            Fragment fragment = new user();
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.frame_container,fragment);
            fr.commit();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("ERROR", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    //fb
    private void getFbInfo() {
        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(final JSONObject me, GraphResponse response) {
                            if (me != null) {
                             //   Log.i("Login: ", me.optString("name"));
                             //   Log.i("ID: ", me.optString("id"));

                                Toast.makeText(getContext(), "Name: " + me.optString("name"), Toast.LENGTH_SHORT).show();
                               // Toast.makeText(getContext(), "ID: " + me.optString("id"), Toast.LENGTH_SHORT).show();
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

    //fb
    public void onActivityResultFB(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
