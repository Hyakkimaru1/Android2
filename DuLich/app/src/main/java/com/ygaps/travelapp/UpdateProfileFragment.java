package com.ygaps.travelapp;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.ygaps.travelapp.util.FileUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileFragment extends Fragment {
    private static final int REQUEST_PERMISSION = 123;
    private static final int REQUEST_CODE_CHOOSE_IMAGE = 113;
    ImageView imv;
    Button btnUpdate;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String token;
    RadioButton rdName,rdNu;
    EditText  edtName, edtEmail,edtPhone,edtBod;
    File fileAvatar = null;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        sharedPreferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString( "token","" );
        edtBod = view.findViewById(R.id.edtBirthday);
        edtName = view.findViewById(R.id.edtFullName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        rdName = view.findViewById(R.id.radioButton);
        rdNu = view.findViewById(R.id.radioButton2);
        imv = view.findViewById(R.id.imvAvatar);
        edtName.setText(sharedPreferences.getString( "name","" ));
        edtEmail.setText(sharedPreferences.getString( "email","" ));
        edtPhone.setText(sharedPreferences.getString( "Phone","" ));
        edtBod.setText(sharedPreferences.getString( "dob","" ));
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPermission();
            }
        });
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtName.getText().equals("")){
                    return;
                }else if(edtEmail.getText().equals("")){
                    return;
                }else if(edtPhone.getText().equals("")){
                    return;
                }
                int gt = -1;
                if(rdName.isChecked()==true){
                    gt = 1;
                }else {
                    gt = 0;
                }
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .update_profile( token,edtName.getText().toString(),edtEmail.getText().toString(),gt,edtBod.getText().toString() );
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200){
                            Toast.makeText( getContext(),"Update Thanh Công",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText( getContext(),"Update thất bại",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText( getContext(),"Update thất bại",Toast.LENGTH_SHORT).show();
                    }
                } );

                if(fileAvatar!= null){
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileAvatar);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", System.currentTimeMillis()+"png", requestFile);

                    Call<ResponseBody> callavatar = RetrofitClient
                            .getInstance()
                            .getApi()
                            .update_avatar(token,body);
                    callavatar.enqueue( new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code()==200){
                                Toast.makeText( getContext(),"Update Thanh Công",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText( getContext(),"Update thất bại",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText( getContext(),"Update thất bại",Toast.LENGTH_SHORT).show();
                        }
                    } );
                }
            }
        });

        return view;
    }

    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(getContext(), "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(getContext(), "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE_CHOOSE_IMAGE);

            }
        }else {
            Toast.makeText(getContext(), "Permission isn't granted1234 ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE_CHOOSE_IMAGE);

            } else {
                // User refused to grant permission.
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
           if(requestCode == REQUEST_CODE_CHOOSE_IMAGE){
                Uri url = data.getData();
                Picasso.get().load(url).into( imv );
                fileAvatar = new File(FileUtil.getPath(getContext(),url));
           }
    }
}
