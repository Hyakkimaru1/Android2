package com.ygaps.travelapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.Adapter.MessageAdapter;
import com.ygaps.travelapp.Message;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chat_tour extends AppCompatActivity   {

    ListView listView;
    ArrayList<Message> messages;
    MessageAdapter messageAdapter;
    SharedPreferences preferences;
    String token;
    int tourID;
    int Id_Login;
    ImageButton sendComment;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat_tour );

        preferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        token = preferences.getString( "token","" );
        Id_Login = preferences.getInt( "userID",-1 );

        //get tourID from Intent
        //
        tourID = 3874;

        listView = findViewById( R.id.messages_view );
        editText = findViewById( R.id.comment_of_user );
        sendComment =  findViewById( R.id.send_comment );
        getCommentList();


        sendComment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = editText.getText().toString();
                editText.setText( "" );
                comment = comment.trim();
                if (!comment.equals( "" )) {
                    Call<ResponseBody> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .sendComment( token,tourID,Id_Login,comment);
                    final String finalComment = comment;
                    call.enqueue( new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code()==200)
                            {

                                messages.add(new Message( finalComment,"","",Id_Login,true ));
                                messageAdapter.notifyDataSetChanged();
                                listView.setSelection(listView.getCount() - 1);
                            }
                            else {
                                Toast.makeText( chat_tour.this,"Vui lòng thử lại",Toast.LENGTH_SHORT ).show();
                            }

                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    } );
                }
            }
        } );
    }

    void getCommentList(){
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getCommentList( token,tourID,1,35);
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200)
                {
                    try {
                        String body = response.body().string();
                        messages = new ArrayList<>( );
                        JSONObject object = new JSONObject( body );

                        JSONArray jsonArr = object.getJSONArray( "commentList" );;
                        int userID;
                        if (jsonArr.length()>0){
                            for (int i=0;i <jsonArr.length();i++){
                                JSONObject jsonObject = jsonArr.getJSONObject( i );
                                userID = jsonObject.getInt( "id" );
                                //Log.e("userID: ", String.valueOf( userID ));
                                //Log.e("ID login: ",String.valueOf( Id_Login ));
                                if (userID == Id_Login){
                                    messages.add( new Message( jsonObject.getString( "comment" ),jsonObject.getString( "avatar" ),
                                            jsonObject.getString( "name" ),userID,true) );
                                }
                                else {
                                    messages.add( new Message( jsonObject.getString( "comment" ),jsonObject.getString( "avatar" ),
                                            jsonObject.getString( "name" ),userID,false) );
                                }
                            }
                            messageAdapter = new MessageAdapter( chat_tour.this,R.layout.friends_chat, messages );
                            listView.setAdapter( messageAdapter );
                            listView.setSelection(listView.getCount() - 1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );
    }
}
