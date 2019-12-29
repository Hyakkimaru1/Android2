package com.ygaps.travelapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.turf.TurfMeasurement;
import com.ygaps.travelapp.Adapter.MessageAdapter;
import com.ygaps.travelapp.Message;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static java.lang.Thread.sleep;

// classes needed to initialize map
// classes needed to add the location component
// classes needed to add a marker
// classes to calculate a route
// classes needed to launch navigation UI


public class maps_follow_thetour extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {
    // variables for adding location layer
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation

    SharedPreferences sharedPreferences;
    private Marker markerView;
    private Point destinationPoint;
    private Point destinationPoint1;
    private Point originPoint;
    String Authorization;
    int tourId;
    int userId;
    private List<Point> pointList = new ArrayList<>();
    private List<Point> friends = new ArrayList<>();
    int index = 0;
   // private Point curPoint;
    double distanceBetweenLastAndSecondToLastClickPoint = 0;
    boolean checkMain = true;
    boolean isEndTrip = true;
    FloatingActionButton notifi_speed,speed_40,speed_60,speed_80,notifi_road,notifi_police,chatChat;
    boolean isShow = false;

    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    //For chat in maps
    LinearLayout linearLayout;
    TextView nameTourMoving;
    Button hideChatInMap;
    ListView messages_in_map;
    ImageButton recordFile_in_map;
    EditText comment_of_user_in_map;
    ImageButton send_comment_in_map;
    ArrayList<Message> messages ;
    MessageAdapter messageAdapter = null;
    String storeComment;

    int leadTour;
    int indexRecord = 0;
    private static final String LOG_TAG = "AudioRecordFile";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    int Id_Login;
    boolean one=true;

    private Handler handler = new Handler();

    //getRoude
    private MapboxDirections client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //access tokens of your account in strings.xml
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_maps_follow_thetour);
        mapView = findViewById(R.id.mapView);
        sharedPreferences = getSharedPreferences("isLogin",MODE_PRIVATE);
        Authorization = sharedPreferences.getString( "token","" );
        Id_Login = sharedPreferences.getInt( "userID",-1 );
        //Get tourID
        Intent intent = getIntent();
        String tour = intent.getStringExtra( "tourId") ;
        tourId = Integer.valueOf( tour );


        //Get userID
        userId = Id_Login;
        chatChat = findViewById( R.id.chatChat );
        linearLayout = findViewById( R.id.chatInMap );
        nameTourMoving = findViewById( R.id.nameTourMoving );
        hideChatInMap = findViewById( R.id.hideChatInMap );
        messages_in_map = findViewById( R.id.messages_in_map );
        recordFile_in_map = findViewById( R.id.recordFile_in_map );
        comment_of_user_in_map = findViewById( R.id.comment_of_user_in_map );
        send_comment_in_map = findViewById(R.id.send_comment_in_map );

        notifi_speed = findViewById( R.id.notifi_speed );
        speed_40 = findViewById( R.id.speed_40 );
        speed_60 = findViewById( R.id.speed_60 );
        speed_80 = findViewById( R.id.speed_80 );
        notifi_road = findViewById( R.id.notifi_road );
        notifi_police = findViewById( R.id.notifi_police );

        recordFile_in_map.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    if (checkPermission()){
                        startRecording();
                        storeComment = comment_of_user_in_map.getText().toString();
                        comment_of_user_in_map.setText( "Recording started!!" );
                    }
                    // Log.e("PERMISSION",String.valueOf( checkPermission() ));
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if (checkPermission()){
                        stopRecording();
                        comment_of_user_in_map.setText( storeComment );
                    }
                }
                return false;
            }
        } );

        send_comment_in_map.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = comment_of_user_in_map.getText().toString();
                comment_of_user_in_map.setText( "" );
                comment = comment.trim();
                if (!comment.equals( "" )) {
                    Call<ResponseBody> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .sendChatChat( Authorization,tourId,userId,comment);

                    final String finalComment = comment;
                    call.enqueue( new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code()==200)
                            {

                                messages.add(new Message( finalComment,"","",Id_Login,true ));
                                if (messageAdapter == null){
                                    messageAdapter = new MessageAdapter( maps_follow_thetour.this,R.layout.my_message, messages );
                                    messages_in_map.setAdapter( messageAdapter );
                                }
                                else {
                                    messageAdapter.notifyDataSetChanged();
                                }
                                messages_in_map.setSelection(messages_in_map.getCount() - 1);
                            }
                            else {
                                Toast.makeText( maps_follow_thetour.this,"Vui lòng thử lại",Toast.LENGTH_SHORT ).show();
                            }

                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    } );
                }
            }
        } );

        messages_in_map.setOnItemClickListener( new AdapterView.OnItemClickListener() {
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

        chatChat.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifi_speed.hide();
                chatChat.hide();
                if (isShow == false){
                    notifi_road.hide();
                    notifi_police.hide();
                }
                else {
                    speed_80.hide();
                    speed_60.hide();
                    speed_40.hide();
                }
                linearLayout.setVisibility( View.VISIBLE );
                hideChatInMap.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        linearLayout.setVisibility( View.INVISIBLE );
                        notifi_speed.show();
                        chatChat.show();
                        if (isShow == false){
                            notifi_road.show();
                            notifi_police.show();
                        }
                        else {
                            speed_80.show();
                            speed_60.show();
                            speed_40.show();
                        }
                    }
                } );
                getChat();


            }
        } );
        speed_40.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .sendNotification(Authorization,locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude(),tourId,userId,3,40,"");
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200){
                            Toast.makeText( maps_follow_thetour.this,"Gửi thành công",Toast.LENGTH_SHORT ).show();
                        }
                        else {
                            Toast.makeText( maps_follow_thetour.this,"Gửi thất bại",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );
            }
        } );
        speed_60.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .sendNotification(Authorization,locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude(),tourId,userId,3,60,"");
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200){
                            Toast.makeText( maps_follow_thetour.this,"Gửi thành công",Toast.LENGTH_SHORT ).show();
                        }
                        else {
                            Toast.makeText( maps_follow_thetour.this,"Gửi thất bại",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );
            }
        } );
        speed_80.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .sendNotification(Authorization,locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude(),tourId,userId,3,80,"");
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200){
                            Toast.makeText( maps_follow_thetour.this,"Gửi thành công",Toast.LENGTH_SHORT ).show();
                        }
                        else {
                            Toast.makeText( maps_follow_thetour.this,"Gửi thất bại",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );
            }
        } );
        notifi_road.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .sendNotification(Authorization,locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude(),tourId,userId,2,"");
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200){
                            Toast.makeText( maps_follow_thetour.this,"Gửi thành công",Toast.LENGTH_SHORT ).show();
                        }
                        else {
                            Toast.makeText( maps_follow_thetour.this,"Gửi thất bại",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );
            }
        } );
        notifi_police.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .sendNotification(Authorization,locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude(),tourId,userId,1,"");
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200){
                            Toast.makeText( maps_follow_thetour.this,"Gửi thành công",Toast.LENGTH_SHORT ).show();
                        }
                        else {
                            Toast.makeText( maps_follow_thetour.this,"Gửi thất bại",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );
            }
        } );
        notifi_speed.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShow == false){
                    Show();
                    isShow = true;
                }
                else {
                    Hide();
                    isShow = false;
                }

            }
        } );
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync( this );

    }

    private void getChat(){
        messages = new ArrayList<>( );
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getChatChat( Authorization,tourId,1,35);
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200)
                {
                    try {
                        String body = response.body().string();

                        JSONObject object = new JSONObject( body );

                        JSONArray jsonArr = object.getJSONArray( "notiList" );

                        //Log.e("CHAT CHAT: ",body);

                        int userID;
                        if (jsonArr.length()>0){
                            for (int i=0;i <jsonArr.length();i++){
                                JSONObject jsonObject = jsonArr.getJSONObject( i );
                                userID = jsonObject.getInt( "userId" );
                                Log.e("userID: ", String.valueOf( userID ));
                                Log.e("ID login: ",String.valueOf( Id_Login ));
                                if (userID == Id_Login){
                                    messages.add( new Message( jsonObject.getString( "notification" ),jsonObject.getString( "avatar" ),
                                            jsonObject.getString( "name" ),userID,true) );
                                }
                                else {
                                    messages.add( new Message( jsonObject.getString( "notification" ),jsonObject.getString( "avatar" ),
                                            jsonObject.getString( "name" ),userID,false) );
                                }
                            }
                            //Log.e("CHAT CHAT: ",messages.get( 1 ).getComment());
                            messageAdapter = new MessageAdapter( maps_follow_thetour.this,R.layout.my_message, messages );
                            messages_in_map.setAdapter( messageAdapter );
                            messages_in_map.setSelection(messages_in_map.getCount() - 1);
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
    private void Show(){
        notifi_road.hide();
        notifi_police.hide();
        speed_40.show();
        speed_60.show();
        speed_80.show();
    }
    private void Hide(){
        speed_40.hide();
        speed_60.hide();
        speed_80.hide();
        notifi_road.show();
        notifi_police.show();
    }
    private String createFileName(){

        fileName = getExternalCacheDir().getAbsolutePath()+"/" + String.valueOf( indexRecord )+"recordtour.3gp";
        indexRecord++;
        return fileName;
    }


    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(maps_follow_thetour.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(maps_follow_thetour.this,
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
        try{
            recorder.stop();
            recorder.release();
            recorder = null;
            Log.e("FILE NAME",fileName);
            messages.add(new Message( fileName,"","",Id_Login,true,true ));
            if (messageAdapter == null){
                messageAdapter = new MessageAdapter( maps_follow_thetour.this,R.layout.my_message, messages );
                messages_in_map.setAdapter( messageAdapter );
            }
            else {
                messageAdapter.notifyDataSetChanged();
            }

            messages_in_map.setSelection(messages_in_map.getCount() - 1);

        }catch(RuntimeException stopException){
            //handle cleanup here
        }

    }

    private void getRoute(final MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview( DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response

                if (response.body() == null) {
                    Log.e("No routes found",", make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e("No routes found","!!!");
                    return;
                }

// Get the directions route
                currentRoute = response.body().routes().get(0);

// Make a toast which displays the route's distance

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

// Retrieve and update the source designated for showing the directions route
                            if (one){
                            style.addSource( new GeoJsonSource(ROUTE_SOURCE_ID,
                                    FeatureCollection.fromFeatures(new Feature[] {}) ));
                            LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

                            // Add the LineLayer to the map. This layer will display the directions route.
                            routeLayer.setProperties(
                                    lineCap( Property.LINE_CAP_ROUND),
                                    lineJoin(Property.LINE_JOIN_ROUND),
                                    lineWidth(7f),
                                    lineColor( Color.parseColor("#1BA8F0")));

                            style.addLayer( routeLayer );
                            one = false;
                            }
                            GeoJsonSource source = style.getSourceAs( ROUTE_SOURCE_ID );
// Create a LineString with the directions route's geometry and
// reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                //Log.e("NICEEEE","NICEEEE");
                                source.setGeoJson( FeatureCollection.fromFeature(
                                        Feature.fromGeometry( LineString.fromPolyline(currentRoute.geometry(), PRECISION_6))));
                            }
                            else {
                                Log.e("AAAAA","AAAAAAAA");
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                //Timber.e("Error: " + throwable.getMessage());
                Toast.makeText(maps_follow_thetour.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                mapboxMap.addOnMapClickListener(maps_follow_thetour.this);
            }
        });
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTourInfo(Authorization,tourId);

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();

                       // Log.i("Suggest stoppoint:", bodyListTour);
                        JSONObject tourData = new JSONObject(bodyListTour);
                        leadTour = tourData.getInt( "hostId" );
                        JSONArray responseArray = tourData.getJSONArray("stopPoints");
                        //    Log.i("Suggest Length:", String.valueOf(responseArray.length()));
                        IconFactory iconFactory = IconFactory.getInstance( maps_follow_thetour.this );
                        Icon icon = null;

                        LatLng point = null;
                        if (responseArray.length() > 0){
                            for (int i = 0;i<responseArray.length();i++){
                                JSONObject jb = responseArray.getJSONObject( i );

                                point = new LatLng( jb.getDouble( "lat" ) ,jb.getDouble( "long" ) ) ;
                                if (jb.getInt( "serviceTypeId" )==1){
                                    icon = iconFactory.fromResource( R.drawable.restaurant );
                                    markerView = mapboxMap.addMarker(new MarkerOptions()
                                            .position(point)
                                            .icon( icon )
                                            .title(jb.getString( "name" )));
                                }
                                else if (jb.getInt( "serviceTypeId" )==2){
                                    icon = iconFactory.fromResource( R.drawable.hotel );
                                    markerView = mapboxMap.addMarker(new MarkerOptions()
                                            .position(point)
                                            .icon( icon )
                                            .title(jb.getString( "name" )));
                                }
                                else if (jb.getInt( "serviceTypeId" )==3){
                                    icon = iconFactory.fromResource( R.drawable.rest_station );
                                    markerView = mapboxMap.addMarker(new MarkerOptions()
                                            .position(point)
                                            .icon( icon )
                                            .title(jb.getString( "name" )));
                                }
                                else {
                                    icon = iconFactory.fromResource( R.drawable.other );
                                    markerView = mapboxMap.addMarker(new MarkerOptions()
                                            .position(point)
                                            .icon( icon )
                                            .title(jb.getString( "name" )));
                                }
                                pointList.add(Point.fromLngLat(jb.getDouble( "long" ),jb.getDouble( "lat" )));
                            }
                           // curPoint = pointList.get( 0 );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    //   Toast.makeText( MapsActivity.this,"GOOODDD",Toast.LENGTH_SHORT ).show();
                    //   Log.i("Suggest stoppoint:", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        } );



    }


    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return true;
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mod
                locationComponent.setCameraMode( CameraMode.TRACKING );
                //final boolean[] check = {true};
            new Thread( new Runnable() {
                public void run() {
                    try {
                        while (checkMain || isEndTrip) {

                            sleep( 1000 );
                            if (locationComponent.getLastKnownLocation() != null) {

                                if (pointList.size() > 0) {
                                    originPoint = Point.fromLngLat( locationComponent.getLastKnownLocation().getLongitude(),
                                            locationComponent.getLastKnownLocation().getLatitude() );
                                    // Log.i("Size: ",String.valueOf( pointList.size() ));
                                    if (isEndTrip) {
                                        Handler handler1 = new Handler( Looper.getMainLooper() );
                                        handler1.post( new Runnable() {
                                            @Override
                                            public void run() {
                                                getRoute(mapboxMap, originPoint, pointList.get( index ) );
                                            }
                                        } );

                                    }

                                    //index++;
                                    checkMain = false;
                                }
                            }
                        }
                        return;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            } ).start();
            new Thread( new Runnable() {
                @Override
                public void run() {
                    try {
                        while (isEndTrip) {
                            sleep( 1000 );
                            if (!checkMain && isEndTrip) {
                                originPoint = Point.fromLngLat( locationComponent.getLastKnownLocation().getLongitude(),
                                        locationComponent.getLastKnownLocation().getLatitude() );
                                if (isEndTrip==false){
                                    return;
                                }
                                distanceBetweenLastAndSecondToLastClickPoint = TurfMeasurement.distance( originPoint, pointList.get( index ) );
                            }
                            //Log.e("Space in:",String.valueOf( distanceBetweenLastAndSecondToLastClickPoint ) + "index: " + String.valueOf( index ) +"LENGTH TOUR"+ pointList.size() );
                            if (distanceBetweenLastAndSecondToLastClickPoint < 0.01) {
                                index++;
                                if (index >= pointList.size()) {

                                    isEndTrip = false;

                                    if (userId == leadTour) {
                                        Call<ResponseBody> call = RetrofitClient
                                                .getInstance()
                                                .getApi()
                                                .finshTrip( Authorization, tourId );
                                        call.enqueue( new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.code() == 200) {
                                                    handler.post( new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText( maps_follow_thetour.this, "Đã gửi thông báo đến toàn tour", Toast.LENGTH_SHORT ).show();
                                                        }
                                                    } );
                                                } else {
                                                    handler.post( new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText( maps_follow_thetour.this, "Thông báo tới tour thất bại", Toast.LENGTH_SHORT ).show();
                                                        }
                                                    } );
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        } );
                                    }

                                    handler.post( new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder aleartDialog = new AlertDialog.Builder( maps_follow_thetour.this );
                                            aleartDialog.setTitle( "Tour finshed" );
                                            aleartDialog.setMessage( "Congratulations!!!" );
                                            aleartDialog.setPositiveButton( "OK!", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent( getBaseContext(), MainActivity.class );
                                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                                    startActivity( intent );
                                                }
                                            } );
                                            aleartDialog.show();
                                        }
                                    } );


                                } else {
                                   // getRoute( originPoint, pointList.get( index ) );
                                }


                            }
                        }
                        return;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            } ).start();
            new Thread( new Runnable() {
                @Override
                public void run() {
                    try {
                        while (isEndTrip) {
                            if (!checkMain) {
                                Call<ResponseBody> call = RetrofitClient
                                        .getInstance()
                                        .getApi()
                                        .sendCoordinate( Authorization, userId, tourId, locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude() );

                                call.enqueue( new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.code() == 200) {
                                            String bodyListTour = null;
                                            try {
                                                bodyListTour = response.body().string();

                                                JSONArray responseArray = new JSONArray( bodyListTour );
                                                //    Log.i("Suggest Length:", String.valueOf(responseArray.length()));
                                                IconFactory iconFactory = IconFactory.getInstance( maps_follow_thetour.this );
                                                Icon icon = null;

                                                LatLng point = null;
                                                if (responseArray.length() > 0) {
                                                    for (int i = 0; i < responseArray.length(); i++) {
                                                        JSONObject jb = responseArray.getJSONObject( i );
                                                        point = new LatLng( jb.getDouble( "lat" ), jb.getDouble( "long" ) );
                                                        // Log.e( "userID", String.valueOf( userId ) );
                                                        // Log.e( "userID api", jb.getString( "id" ) );
                                                        if (jb.getInt( "id" ) != userId) {
                                                            icon = iconFactory.fromResource( R.drawable.friends );
                                                            markerView = mapboxMap.addMarker( new MarkerOptions()
                                                                    .position( point )
                                                                    .icon( icon )
                                                                    .title( jb.getString( "id" ) ) );
                                                        }
                                                        //friends.add(Point.fromLngLat(jb.getDouble( "long" ),jb.getDouble( "lat" )));
                                                    }
                                                    // curPoint = pointList.get( 0 );
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }


                                        } else {
                                            //   Toast.makeText( MapsActivity.this,"GOOODDD",Toast.LENGTH_SHORT ).show();
                                            //   Log.i("Suggest stoppoint:", response.toString());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                } );

                                call = RetrofitClient
                                        .getInstance()
                                        .getApi()
                                        .getNotification( Authorization, tourId, 1, 200 );
                                call.enqueue( new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.code() == 200) {
                                            try {
                                                String body = response.body().string();
                                                // Log.e("AAAAAA",body);
                                                JSONObject jsonObject = new JSONObject( body );
                                                JSONArray jsonArray = jsonObject.getJSONArray( "notiList" );
                                                IconFactory iconFactory = IconFactory.getInstance( maps_follow_thetour.this );
                                                Icon icon = null;
                                                LatLng point = null;
                                                if (jsonArray.length() > 0 && isEndTrip) {
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject object = jsonArray.getJSONObject( i );

                                                        if (!object.getString( "notificationType" ).equals( "null" )) {
                                                            point = new LatLng( object.getDouble( "lat" ), object.getDouble( "long" ) );

                                                            if (object.getInt( "notificationType" ) == 1) {
                                                                icon = iconFactory.fromResource( R.drawable.police );
                                                                markerView = mapboxMap.addMarker( new MarkerOptions()
                                                                        .position( point )
                                                                        .icon( icon )
                                                                        .title( object.getString( "note" ) ) );
                                                            } else if (object.getInt( "notificationType" ) == 2) {
                                                                icon = iconFactory.fromResource( R.drawable.notifi_road );
                                                                markerView = mapboxMap.addMarker( new MarkerOptions()
                                                                        .position( point )
                                                                        .icon( icon )
                                                                        .title( object.getString( "note" ) ) );
                                                            } else if (object.getInt( "notificationType" ) == 3) {
                                                                if (object.getInt( "speed" ) <= 40) {
                                                                    icon = iconFactory.fromResource( R.drawable.speed_40 );
                                                                    markerView = mapboxMap.addMarker( new MarkerOptions()
                                                                            .position( point )
                                                                            .icon( icon )
                                                                            .title( object.getString( "note" ) ) );
                                                                } else if (object.getInt( "speed" ) <= 60) {
                                                                    icon = iconFactory.fromResource( R.drawable.speed_60 );
                                                                    markerView = mapboxMap.addMarker( new MarkerOptions()
                                                                            .position( point )
                                                                            .icon( icon )
                                                                            .title( object.getString( "note" ) ) );
                                                                } else if (object.getInt( "speed" ) <= 80) {
                                                                    icon = iconFactory.fromResource( R.drawable.speed_80 );
                                                                    markerView = mapboxMap.addMarker( new MarkerOptions()
                                                                            .position( point )
                                                                            .icon( icon )
                                                                            .title( object.getString( "note" ) ) );
                                                                }

                                                            }

                                                        }
                                                    }

                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Log.e( "AAAAAA", "false" );
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                } );

                            }
                            sleep( 10000 );

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            } ).start();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isEndTrip = false;
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        isEndTrip = false;
        finish();
    }

}