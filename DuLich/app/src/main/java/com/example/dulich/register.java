package com.example.dulich;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class register extends Fragment {
    Button BTsignup;
    String urlAdress = "http://35.197.153.192:3000/user/register";
    String signUp = "user/register";
    EditText fullName;
    EditText email;
    EditText phone;
    EditText passWord;
    EditText confirmPw;
    String stringFullName;
    String stringEmail;
    String stringPhone;
    String stringPassWord;
    String stringConfirmPw;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login,container,false);

        BTsignup = view.findViewById(R.id.BTsignup);
        fullName = view.findViewById( R.id.ETfullName );
        email = view.findViewById( R.id.ETemail );
        phone = view.findViewById( R.id.ETphone );
        passWord = view.findViewById( R.id.ETpassword );
        confirmPw = view.findViewById( R.id.ETconfirmpw );

        BTsignup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInput();
               // Toast.makeText( getContext(), stringPhone=phone.getText().toString(),Toast.LENGTH_LONG).show();
                // sendPost();
               Call<ResponseBody> call = RetrofitClient
                       .getInstance()
                       .getApi()
                       .createUser(fullName.getText().toString(),email.getText().toString(),passWord.getText().toString(),phone.getText().toString());

                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code()==200) {

                                    Toast.makeText( getContext(),"Đăng ký thành công",Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();
                                }

                            else if (response.code()==400)
                                {

                                    Toast.makeText( getContext(), "Đăng ký lỗi", Toast.LENGTH_LONG ).show();
                                }
                            else
                            {

                                Toast.makeText( getContext(), "Server error on creating user", Toast.LENGTH_LONG ).show();
                            }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Toast.makeText( getContext(),t.getMessage(),Toast.LENGTH_LONG ).show();
                    }
                } );
            }
        } );


        return view;
    }

    boolean checkInput()
    {

        return false;
    }
    //Send theo kieu Json thuan
    /*
    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
        try {
            URL url = new URL(urlAdress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);


            JSONObject jsonParam = new JSONObject();
            jsonParam.put("fullName",fullName.getText().toString() );
            jsonParam.put("email", email.getText().toString());
            jsonParam.put("password",passWord.getText().toString());
            jsonParam.put("phone", phone.getText().toString());

            Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        });

        thread.start();
    }*/

}
