package com.example.dulich;

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

import com.squareup.picasso.Picasso;

public class user extends Fragment {
    ImageView imageView;
    TextView textView;
    Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false) ;
        SharedPreferences preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);

        textView = view.findViewById( R.id.name );
        imageView=view.findViewById(R.id.avatar);
        textView.setText( preferences.getString( "fullName","") );
        if (!preferences.getString( "avatar","" ).equals(""))
            Picasso.get().load( preferences.getString( "avatar","" )).into( imageView );
        return view;
    }
}
