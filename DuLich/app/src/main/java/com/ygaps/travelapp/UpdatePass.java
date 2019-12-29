package com.ygaps.travelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePass extends Fragment {

    EditText edtPass,edtPassNew,edtPassNew1;
    TextView btnUpdate;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String token;
    int userId;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);
        edtPass = view.findViewById(R.id.edtPass);
        edtPassNew = view.findViewById(R.id.edtPassNew1);
        edtPassNew1 = view.findViewById(R.id.edtPassNew2);
        btnUpdate = view.findViewById(R.id.btnUpdatePassword);
        sharedPreferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString( "token","" );
        userId = sharedPreferences.getInt( "userID" ,1);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_pass();
            }
        });
        return view;
    }

    private void update_pass(){
        if(edtPass.getText().toString().equals("")){
            Toast.makeText(getContext(),"Nhập đầy đủ thông tin !",Toast.LENGTH_LONG).show();
            return;
        }else if(edtPassNew.getText().toString().equals("")){
            Toast.makeText(getContext(),"Nhập đầy đủ thông tin !",Toast.LENGTH_LONG).show();
            return;
        }else if(edtPassNew1.getText().toString().equals("")){
            Toast.makeText(getContext(),"Nhập đầy đủ thông tin !",Toast.LENGTH_LONG).show();
            return;
        } else if (!edtPassNew.getText().toString().trim().equals(edtPassNew1.getText().toString().trim())){
            Toast.makeText(getContext(),"Mật khẩu không khớp",Toast.LENGTH_LONG).show();
            return;
        }
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .update_password(token,userId,edtPass.getText().toString(),edtPassNew.getText().toString());
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200)
                {
                    Toast.makeText(getContext(),"Update Password success !",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(),"Update Password unsuccessful !",Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(),"Get user info fail",Toast.LENGTH_LONG).show();
            }
        } );
    }
}
