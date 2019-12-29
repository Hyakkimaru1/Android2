package com.ygaps.travelapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends Fragment {
    RadioButton typeMail,typePhone;
    EditText edtValue,edtOtp,edtPassnew;
    Button btnSend,btnConfirm;
    String type;
    int userId = -1;
    ConstraintLayout view1,view2;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        typeMail = view.findViewById(R.id.rdEmail);
        typePhone = view.findViewById(R.id.rdPhone);
        edtValue = view.findViewById(R.id.edtValue);
        edtOtp = view.findViewById(R.id.edtOtp);
        edtPassnew = view.findViewById(R.id.edtPassnew);
        btnSend = view.findViewById(R.id.btnSend);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        view1 = view.findViewById(R.id.viewOtp);
        view2 = view.findViewById(R.id.viewConfirm);
        view2.setVisibility(View.GONE);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVeryfi();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewPass();
            }
        });
        return view;
    }
    private void sendVeryfi(){
        if(typePhone.isChecked()){
            type ="phone";
        }else {
            type ="email";
        }
        if(edtValue.getText().toString().equals("")){
            Toast.makeText(getContext(),"Nhập đầy đủ thông tin !",Toast.LENGTH_LONG).show();
            return;
        }
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .Request_OTP(type,edtValue.getText().toString().trim());
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200)
                {
                    Toast.makeText(getContext(),"Send OTP Success !",Toast.LENGTH_LONG).show();
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    // Xem api trả về data là lấy UserId để lưu vào biến -> thực hiện cho api reset lại password
                    //userId = ?????
                }else {
                    Toast.makeText(getContext(),"Send OTP Unsuccessful !",Toast.LENGTH_LONG).show();
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(),"Send OTP Unsuccessful !",Toast.LENGTH_LONG).show();
            }
        } );
    }

    private void sendNewPass(){

        if(edtOtp.getText().toString().equals("")){
            Toast.makeText(getContext(),"Nhập đầy đủ thông tin !",Toast.LENGTH_LONG).show();
            return;
        }else if(edtPassnew.getText().toString().equals("")){
            Toast.makeText(getContext(),"Nhập đầy đủ thông tin !",Toast.LENGTH_LONG).show();
            return;
        }
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .Verify_OTP(userId,edtOtp.getText().toString().trim(),edtPassnew.getText().toString().trim());

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200)
                {
                    Toast.makeText(getContext(),"Reset Success !",Toast.LENGTH_LONG).show();
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(getContext(),"Reset Unsuccessful !",Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(),"Reset Unsuccessful !",Toast.LENGTH_LONG).show();
            }
        } );
    }
}
