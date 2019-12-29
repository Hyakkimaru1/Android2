package com.ygaps.travelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.ygaps.travelapp.data.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class user extends Fragment {
    ImageView imageView;
    TextView txtName,txtEmail,txtPhone,txtGender,txtBirth;
    Button btnLogout,btnUpdate,btnUpdatePass;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String token;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false) ;
        sharedPreferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString( "token","" );
        getChat();
        btnLogout = view.findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString( "token","" );
                editor.putBoolean("isLogIn",false);
                editor.commit();
                Fragment fragment = new setting();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.frame_container,fragment);
                fr.addToBackStack(null);
                fr.commit();

            }
        });
        btnUpdatePass = view.findViewById(R.id.btnUpdatePass);
        txtName = view.findViewById( R.id.name_friend );
        txtEmail = view.findViewById( R.id.txtEmail );
        txtPhone = view.findViewById( R.id.txtPhone );
        txtGender = view.findViewById(R.id.txtGender);
        imageView = view.findViewById(R.id.avatar);
        btnUpdate = view.findViewById(R.id.btnUpdateProfile);
        txtBirth = view.findViewById(R.id.txtBirthday);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UpdateProfileFragment();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.frame_container,fragment);
                fr.addToBackStack(null);
                fr.commit();

            }
        });
        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new UpdatePass();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.frame_container,fragment);
                fr.addToBackStack(null);
                fr.commit();
            }
        });
        if (!sharedPreferences.getString( "avatar","" ).equals(""))
            Picasso.get().load( sharedPreferences.getString( "avatar","" )).into( imageView );
        return view;
    }

    private void getChat(){
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getUserInfo(token);
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200)
                {
                    try {
                        String body = response.body().string();

                        JSONObject object = new JSONObject( body );
                        Gson gson = new Gson();
                        UserInfo info = gson.fromJson(object.toString(),UserInfo.class);
                        if(!info.getFullName().isEmpty()) {
                            txtName.setText(chuanHoaDanhTuRieng(info.getFullName()));
                        }
                        txtEmail.setText("Email: "+info.getEmail());
                        txtPhone.setText("Phone: "+info.getPhone());
                        txtBirth.setText("Birthday: "+info.getDob().substring(0,10));
                        switch (info.getGender()){
                            case 0 : txtGender.setText("Gender : Nữ");
                                break;
                            case 1 : txtGender.setText("Gender : Nam");
                                break;
                            default: txtGender.setText("Gender : ---");
                        }
                        Picasso.get().load(info.getAvatar()).into( imageView );

                        editor.putString( "email",info.getEmail() );
                        editor.putString("Phone",info.getPhone());
                        editor.putString("name",info.getFullName());
                        editor.putString("dob",info.getDob().substring(0,10));
                        editor.commit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(),"Get user info fail",Toast.LENGTH_LONG).show();
            }
        } );
    }

    public String chuanHoaDanhTuRieng(String str) {
        str = chuanHoa(str);
        String temp[] = str.split(" ");
        str = ""; // ? ^-^
        for (int i = 0; i < temp.length; i++) {
            str += String.valueOf(temp[i].charAt(0)).toUpperCase() + temp[i].substring(1);
            if (i < temp.length - 1)
                str += " ";
        }
        return str;
    }
    public String chuanHoa(String str) {
        str = str.trim();
        str = str.replaceAll("\\s+", " ");
        return str;
    }
}
