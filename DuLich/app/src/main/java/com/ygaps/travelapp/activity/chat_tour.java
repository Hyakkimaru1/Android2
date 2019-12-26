package com.ygaps.travelapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    ImageButton recordFile;
    EditText editText;
    TextView textView;
    private MediaBrowserCompat mediaBrowser;
    private static final String LOG_TAG = "AudioRecordFile";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    String storeComment;
    int indexRecord = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat_tour );
        getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.titlebar);
        textView = findViewById(R.id.titleBar);

        preferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        token = preferences.getString( "token","" );
        Id_Login = preferences.getInt( "userID",-1 );
        textView.setText(preferences.getString( "nameTour","" ));

        //get tourID from Intent
        //
        tourID = 3874;

        listView = findViewById( R.id.messages_view );
        editText = findViewById( R.id.comment_of_user );
        sendComment =  findViewById( R.id.send_comment );
        recordFile = findViewById( R.id.recordFile );
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

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (messages.get( i ).getIsRecord()){
                        player = new MediaPlayer();
                        try {
                            player.setDataSource(messages.get( i ).getComment());
                            player.prepare();
                            player.start();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "prepare() failed");
                        }
                }
                else if(player!=null) {
                    player.release();
                    player = null;
                }
            }
        } );

        recordFile.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    if (checkPermission()){
                        startRecording();
                        storeComment = editText.getText().toString();
                        editText.setText( "Recording started!!" );
                    }
                   // Log.e("PERMISSION",String.valueOf( checkPermission() ));
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if (checkPermission()){
                        stopRecording();
                        editText.setText( storeComment );
                    }
                }
                return false;
            }
        } );

    }
    private String createFileName(){

        fileName = getExternalCacheDir().getAbsolutePath()+"/" + String.valueOf( indexRecord )+"recordtour.3gp";
        indexRecord++;
        return fileName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
        }
    }


    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(chat_tour.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(chat_tour.this,
                    Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions( this,new String[] {Manifest.permission.RECORD_AUDIO},REQUEST_RECORD_AUDIO_PERMISSION);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions( this,new String[] {Manifest.permission.RECORD_AUDIO},REQUEST_RECORD_AUDIO_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        } else {
            return true;
            // Permission has already been granted
        }
    }


    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(createFileName());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        messages.add(new Message( fileName,"","",Id_Login,true,true ));
        messageAdapter.notifyDataSetChanged();
        listView.setSelection(listView.getCount() - 1);
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
