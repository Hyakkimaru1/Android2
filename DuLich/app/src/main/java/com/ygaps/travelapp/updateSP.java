package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ygaps.travelapp.Adapter.Stop_Point_Adapter;
import com.ygaps.travelapp.activity.MainActivity;
import com.ygaps.travelapp.activity.MapsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class updateSP extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener
{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private SearchView searchText;
    MarkerOptions markerOptions;
    LatLng latLng;

    ImageButton arrive;
    ImageButton leave;
    ImageButton editAddress;

    Button createListStopP;
    EditText edtArrive;
    EditText edtLeave;

    EditText editTextStopPoint;
    EditText editTextAddress;
    EditText editTextTimeLeave;
    EditText editTextSelectDayLeave;
    EditText editTextTimeArrive;
    EditText editTextSelectDay;
    EditText editTextMinC;
    EditText editTextMaxC;
    Button buttonCreateStopPoint;


    String token;
    String id;
    String tourName;
    long sStartDay, sEndDay;
    boolean check;
    int adult, children, position;
    long minC,maxC;
    boolean checkPlaceTour = true;

    Address source = null;
    Address des = null;
    String tourID = "";
    Intent intent;
    int pageIndex = 1;
    boolean flag_loading = false;
    boolean check_ActionList = false;
    FloatingActionButton makeStopPoint;

    int Radius = 1000;

    ArrayList<stopPoint> noteList = new ArrayList<stopPoint>();
    ArrayList<stopPoint> list_searchSP = new ArrayList<stopPoint>();
    com.ygaps.travelapp.serviceStopPoints serviceStopPoints ;

    Stop_Point_Adapter myAdapter;
    Stop_Point_Adapter stop_point_adapter = null;
    ListView listView;
    ListView search_SP_in_maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
getSupportActionBar().hide();
        setContentView( R.layout.activity_update_sp );

        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        id = intent.getStringExtra("id");
        position = intent.getIntExtra("position",0);


        //  Toast.makeText(this,idTour,Toast.LENGTH_SHORT ).show();
        editTextStopPoint = findViewById(R.id.editTextStopPoint);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextTimeLeave = findViewById(R.id.editTextTimeLeave);
        editTextMinC = findViewById(R.id.editTextMinC);
        editTextMaxC = findViewById(R.id.editTextMaxC);
        editTextSelectDay = findViewById(R.id.editTextSelectDay);
        editTextTimeArrive = findViewById(R.id.editTextTimeArrive);
        editTextSelectDayLeave = findViewById(R.id.editTextSelectDayLeave);
        editAddress = findViewById(R.id.imageButtonAddress);

        arrive = findViewById(R.id.imageButtonStartTime);
        leave = findViewById(R.id.imageButtonTimeLeave);
        edtArrive = findViewById(R.id.editTextSelectDay);
        edtLeave = findViewById(R.id.editTextSelectDayLeave);

        listView = findViewById( R.id.listStopPoint );
        search_SP_in_maps = findViewById( R.id.search_SP_in_maps );
        makeStopPoint = findViewById(R.id.makeStopPoint);
         buttonCreateStopPoint = findViewById( R.id.buttonCreateStopPoint );
        createListStopP = findViewById(R.id.createListStopPoint);



        readJson();
        createStoppoint();
        final RelativeLayout relativeLayout1 =findViewById(R.id.mapLayout);
         relativeLayout1.setVisibility(View.INVISIBLE);
        final RelativeLayout relativeLayout = findViewById(R.id.formStopPoint);
        relativeLayout.setVisibility(View.VISIBLE);


        search_SP_in_maps.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                stopPoint stop_point = list_searchSP.get( i );
                markerOptions = new MarkerOptions();
                latLng = new LatLng( stop_point.getLat(),stop_point.getLng() );
                markerOptions.position( latLng );
                markerOptions.title(stop_point.getName());
                if (stop_point.getProvinceId()==1){
                    markerOptions.icon( BitmapDescriptorFactory.fromResource(R.drawable.restaurant) );
                }
                else if (stop_point.getProvinceId()==2){
                    markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.hotel ));
                }
                else if (stop_point.getProvinceId()==3){
                    markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.rest_station ) );
                }
                else {
                    markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.other ) );
                }
                stop_point_adapter.clear();
                stop_point_adapter = null;
                mMap.addMarker( markerOptions );
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,18F) );
            }
        } );





        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RelativeLayout relativeLayout = findViewById(R.id.formStopPoint);
                relativeLayout.setVisibility(View.INVISIBLE);
                final RelativeLayout relativeLayout1 =findViewById(R.id.mapLayout);
                relativeLayout1.setVisibility(View.VISIBLE);



            }
        });

        makeStopPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout linearLayout = findViewById( R.id.listSP );
                final RelativeLayout relativeLayout1 = findViewById( R.id.mapLayout );
                if (noteList.size()>0) {
                    if (check_ActionList==false){
                        linearLayout.setVisibility( View.VISIBLE );
                        relativeLayout1.setVisibility(View.INVISIBLE);
                        Button endListSP = findViewById( R.id.endListSP );
                        endListSP.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                relativeLayout1.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility( View.INVISIBLE );
                                check_ActionList=!check_ActionList;
                            }
                        } );
                        check_ActionList=!check_ActionList;
                    }
                    else {
                        relativeLayout1.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility( View.INVISIBLE );
                        check_ActionList=!check_ActionList;
                    }

                   /* serviceStopPoints serviceStopPoints = null;
                    serviceStopPoints.setTourID( idTour );
                    serviceStopPoints.getStopPoints(noteList);
                    sendNetworkRequest(serviceStopPoints);*/
                }
                else {
                    Toast.makeText( updateSP.this,"Please make a stop point",Toast.LENGTH_SHORT ).show();
                }

            }
        });

        createListStopP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceStopPoints = new serviceStopPoints(id,noteList);
                //Toast.makeText(updateSP.this, tourID, Toast.LENGTH_SHORT).show();
                sendNetworkRequest(serviceStopPoints);
            }
        });

        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start();
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                End();
            }
        });

        searchText = findViewById( R.id.Search );

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
            init();
        }

        // Obtain the SupportMapFragment and get notified when the fragment_explore is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

    }

    private void sendNetworkRequest(serviceStopPoints svStopPoint){
        Log.e("AAAA23223",token);
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .stopPointsSet(token,svStopPoint);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {

                    Toast.makeText( updateSP.this, "Sửa stop points thành công",Toast.LENGTH_SHORT ).show();
                    Intent intentNew = new Intent(updateSP.this, MainActivity.class);
                    intentNew.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity( intentNew );

                }
                else if (response.code()==404)
                {
                    Toast.makeText( updateSP.this, "Tour is not found",Toast.LENGTH_SHORT ).show();
                }
                else if(response.code()==403) {
                    Toast.makeText( updateSP.this, "Not permission to add stop point",Toast.LENGTH_SHORT ).show();
                }
                else {
                    Toast.makeText( updateSP.this, "Server error on adding stop point",Toast.LENGTH_SHORT ).show();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(updateSP.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
    }

    private void init(){
        searchText.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                geoLocate(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length()>1 && s.charAt( 0 ) == '#'  ){
                    searchStopPoint(s.substring( 1 ));
                    if (stop_point_adapter!=null){
                        stop_point_adapter.clear();
                        stop_point_adapter = null;
                    }

                    pageIndex = 1;
                    flag_loading = false;
                }
                else{
                    if (stop_point_adapter!=null){
                        stop_point_adapter.clear();
                        stop_point_adapter = null;
                    }
                }
                return false;
            }
        } );
    }
    private void geoLocate(String searchString) {
        // mMap.clear();
        Geocoder geocoder = new Geocoder( updateSP.this );
        List<Address> list = new ArrayList<>(  );
        try
        {
            list = geocoder.getFromLocationName( searchString,2 );

        } catch (IOException e){
            Log.e( "TAG","geoLocate: IOException "+e.getMessage() );
        }

        if (list.size()>0){
            Address address = list.get( 0 );
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            findPlaceNearly( latitude,longitude );
            markerOptions = new MarkerOptions();
            latLng = new LatLng( address.getLatitude(),address.getLongitude() );
            markerOptions.position( latLng );
            markerOptions.title(searchString);
            markerOptions.icon( BitmapDescriptorFactory.fromResource(R.drawable.location_choose) );
            currentUserLocationMarker = mMap.addMarker( markerOptions );
            // Add a marker in current location and move the camera
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,18F) );
            if (  token != null && !token.equals( "" ))
            {

                CoordList coordList = new CoordList();
                coordList.setLat( address.getLatitude());
                coordList.setLong(address.getLongitude());
                getSuggest_Stoppoint suggest_stoppoint = new getSuggest_Stoppoint();
                suggest_stoppoint.setHasOneCoordinate( true );
                suggest_stoppoint.setCoordList( coordList );


                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .suggest_Stoppoint( token,suggest_stoppoint);

                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code()==200) {

                            String bodyListTour = null;
                            try {
                                bodyListTour = response.body().string();
                                //   Log.i("Suggest stoppoint:", bodyListTour);
                                JSONObject tourData = new JSONObject(bodyListTour);
                                JSONArray responseArray = tourData.getJSONArray("stopPoints");
                                //   Log.i("Suggest Length:", String.valueOf(responseArray.length()));
                                if (responseArray.length() > 0){
                                    for (int i = 0;i<responseArray.length();i++){
                                        JSONObject jb = responseArray.getJSONObject( i );
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        LatLng latLngStopPoint = new LatLng( jb.getDouble( "lat" ),jb.getDouble( "long" ) );
                                        markerOptions.position( latLngStopPoint );
                                        markerOptions.title( jb.getString( "name" ));
                                        if (jb.getInt( "serviceTypeId" )==1){
                                            markerOptions.icon( BitmapDescriptorFactory.fromResource(R.drawable.restaurant) );
                                        }
                                        else if (jb.getInt( "serviceTypeId" )==2){
                                            markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.hotel ));
                                        }
                                        else if (jb.getInt( "serviceTypeId" )==3){
                                            markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.rest_station ) );
                                        }
                                        else {
                                            markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.other ) );
                                        }

                                        currentUserLocationMarker = mMap.addMarker( markerOptions );

                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            //  Toast.makeText( updateSP.this,"GOOODDD",Toast.LENGTH_SHORT ).show();
                            //  Log.i("Suggest stoppoint:", response.toString());
                        }
                    }


                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );
            }
        }

    }

    private void searchStopPoint(final String searchString){

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getDestination( token,searchString, pageIndex,20);
        pageIndex++;
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200){
                    try {
                        String body = response.body().string();
                        JSONObject jsonObject = new JSONObject( body );

                        JSONArray jsonArray = jsonObject.getJSONArray( "stopPoints" );

                        if (jsonArray.length()>0){
                            for (int i = 0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject( i );

                                list_searchSP.add( new stopPoint( object.getString( "name" ),object.getString( "address" ),object.getInt( "provinceId" ),
                                        object.getDouble( "lat") ,object.getDouble( "long" ),object.getLong( "minCost" ),
                                        object.getLong( "maxCost" ),object.getInt( "serviceTypeId" )) );
                            }
                            if (stop_point_adapter == null)
                            {
                                stop_point_adapter = new Stop_Point_Adapter( updateSP.this,R.layout.item_stoppoint_layout,list_searchSP );
                                search_SP_in_maps.setAdapter( stop_point_adapter );
                            }
                            else {
                                stop_point_adapter.notifyDataSetChanged();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(updateSP.this,"Có lỗi, vui lòng thử lại sau.",Toast.LENGTH_LONG ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText( updateSP.this,"Có lỗi, vui lòng thử lại sau.",Toast.LENGTH_LONG ).show();
            }
        } );

        search_SP_in_maps.setOnScrollListener( new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(flag_loading == false)
                    {
                        flag_loading = true;
                        new Thread( new Runnable() {
                            @Override
                            public void run() {
                                additems(searchString);
                            }
                        } );
                    }
                }
            }
        } );
    }

    private void additems(String searchString) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getDestination( token, searchString, pageIndex, 20 );

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    pageIndex++;
                    flag_loading = false;
                    try {
                        String body = response.body().string();
                        JSONObject jsonObject = new JSONObject( body );

                        JSONArray jsonArray = jsonObject.getJSONArray( "stopPoints" );

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject( i );

                                list_searchSP.add( new stopPoint( object.getString( "name" ), object.getString( "address" ), object.getInt( "provinceId" ),
                                        object.getDouble( "lat" ), object.getDouble( "long" ), object.getLong( "minCost" ),
                                        object.getLong( "maxCost" ), object.getInt( "serviceTypeId" ) ) );
                            }
                            stop_point_adapter.notifyDataSetChanged();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText( updateSP.this, "Có lỗi, vui lòng thử lại sau.", Toast.LENGTH_LONG ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );
    }

    /**
     * Manipulates the fragment_explore once available.
     * This callback is triggered when the fragment_explore is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled( true );
            View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.setMargins(0, 250, 200, 0);
        }



        mMap.setOnMapLongClickListener( new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                markerOptions.zIndex( 5.0f );
                markerOptions.icon( BitmapDescriptorFactory.fromResource(R.drawable.location_choose) );
                currentUserLocationMarker = mMap.addMarker( markerOptions );
                // Add a marker in current location and move the camera
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,18F) );

            }
        } );



        mMap.setOnCameraMoveListener( new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng center = mMap.getCameraPosition().target;
                // Log.i( "LONGITUDEE _ LATITUDEE",String.valueOf( center.longitude )+" "+String.valueOf( center.latitude ) );


            }
        } );
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
        googleApiClient.connect();
    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION )!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale( this,Manifest.permission.ACCESS_FINE_LOCATION )){
                ActivityCompat.requestPermissions( this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else {
                ActivityCompat.requestPermissions( this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION )==PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient==null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled( true );

                    }
                }
                else
                {
                    Toast.makeText( this,"Permission Denied",Toast.LENGTH_SHORT ).show();
                }
                return;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval( 1100 );
        locationRequest.setFastestInterval( 1100 );
        locationRequest.setPriority( LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates( googleApiClient,locationRequest,this );
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (currentUserLocationMarker!=null)
        {
            currentUserLocationMarker.remove();
        }

        latLng = new LatLng( location.getLatitude(),location.getLongitude() );
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        findPlaceNearly(latitude,longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( latLng );
        markerOptions.title( "Vị trí hiện tại" );
        markerOptions.icon( BitmapDescriptorFactory.fromResource(R.drawable.location_choose) );

        currentUserLocationMarker = mMap.addMarker( markerOptions );
        if (  token != null && !token.equals( "" ))
        {

            CoordList coordList = new CoordList();
            coordList.setLat( location.getLatitude());
            coordList.setLong(location.getLongitude());
            getSuggest_Stoppoint suggest_stoppoint = new getSuggest_Stoppoint();
            suggest_stoppoint.setHasOneCoordinate( true );
            suggest_stoppoint.setCoordList( coordList );


            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .suggest_Stoppoint( token,suggest_stoppoint);

            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200) {

                        String bodyListTour = null;
                        try {
                            bodyListTour = response.body().string();
                            //    Log.i("Suggest stoppoint:", bodyListTour);
                            JSONObject tourData = new JSONObject(bodyListTour);
                            JSONArray responseArray = tourData.getJSONArray("stopPoints");
                            //    Log.i("Suggest Length:", String.valueOf(responseArray.length()));
                            if (responseArray.length() > 0){
                                for (int i = 0;i<responseArray.length();i++){
                                    JSONObject jb = responseArray.getJSONObject( i );
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    LatLng latLngStopPoint = new LatLng( jb.getDouble( "lat" ),jb.getDouble( "long" ) );
                                    markerOptions.position( latLngStopPoint );
                                    markerOptions.title( jb.getString( "name" ));
                                    if (jb.getInt( "serviceTypeId" )==1){
                                        markerOptions.zIndex( 1.0f );
                                        markerOptions.icon( BitmapDescriptorFactory.fromResource(R.drawable.restaurant) );
                                    }
                                    else if (jb.getInt( "serviceTypeId" )==2){
                                        markerOptions.zIndex( 2.0f );
                                        markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.hotel ));
                                    }
                                    else if (jb.getInt( "serviceTypeId" )==3){
                                        markerOptions.zIndex( 3.0f );
                                        markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.rest_station ) );
                                    }
                                    else {
                                        markerOptions.zIndex( 4.0f );
                                        markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.other ) );
                                    }

                                    currentUserLocationMarker = mMap.addMarker( markerOptions );
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        //   Toast.makeText( updateSP.this,"GOOODDD",Toast.LENGTH_SHORT ).show();
                        //   Log.i("Suggest stoppoint:", response.toString());
                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            } );
        }
        // Add a marker in current location and move the camera
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,18F) );
        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                    // Toast.makeText( updateSP.this, marker.getTitle(),Toast.LENGTH_SHORT).show();
                    editTextStopPoint.setText( marker.getTitle() );
                    final Address address = getAddress( marker.getPosition().latitude,marker.getPosition().longitude );
                    editTextAddress.setText( address.getAddressLine( 0 ) );
                    final RelativeLayout relativeLayout = findViewById(R.id.formStopPoint);
                    relativeLayout.setVisibility(View.VISIBLE);
                    final RelativeLayout relativeLayout1 =findViewById(R.id.mapLayout);
                    relativeLayout1.setVisibility(View.INVISIBLE);
                    Button button_x = findViewById( R.id.button_x );
                    button_x.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            relativeLayout.setVisibility(View.INVISIBLE);
                            relativeLayout1.setVisibility(View.VISIBLE);
                        }
                    } );

                    buttonCreateStopPoint.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(CheckData())
                            {
                                String dateInString = editTextSelectDay.getText().toString();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                Date dateTime = null;
                                try {
                                    dateTime = sdf.parse( dateInString );
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Log.e("date",dateTime.toString());
                                String dateInString2 = editTextSelectDayLeave.getText().toString();

                                Date dateTime2 = null;
                                try {
                                    dateTime2 = sdf.parse( dateInString2 );
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                stopPoint stopPoint = new stopPoint(noteList.get(position).getId(), editTextStopPoint.getText().toString(),editTextAddress.getText().toString(),
                                        1,address.getLatitude(),address.getLongitude(),dateTime.getTime(),dateTime2.getTime(), Integer.parseInt( editTextMinC.getText().toString() ),Integer.parseInt( editTextMaxC.getText().toString() ),noteList.get(position).getServiceTypeId()) ;
                                noteList.set(position,stopPoint);
                                myAdapter = new Stop_Point_Adapter( updateSP.this,R.layout.item_stoppoint_layout,noteList );

                                listView.setAdapter( myAdapter );
                                relativeLayout.setVisibility(View.INVISIBLE);
                                relativeLayout1.setVisibility(View.VISIBLE);
                                Toast.makeText( updateSP.this,"Đã sửa stop point",Toast.LENGTH_SHORT ).show();
                            }
                        }

                    } );

                return false;
            }
        } );
        if (googleApiClient!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates( googleApiClient,this );

        }


    }

    private void findPlaceNearly(double latitude, double longitude)
    {
        String restaurant = "restaurant", hotel = "hotel";
        Object transferDate[] = new Object[2];
        String url = getURL(latitude,longitude, restaurant);

        GetNearlyByPlaces getNearbyPlaces = new GetNearlyByPlaces();
        transferDate[0] = mMap;
        transferDate[1] = url;
        List<HashMap<String, String>> marker;
        getNearbyPlaces.execute(transferDate);

    }

    @org.jetbrains.annotations.NotNull
    private String getURL(double lat, double lng, String nearbyPlace){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+lat+","+lng);
        googlePlaceUrl.append("&radius="+Radius);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+getString(R.string.apikeyPlaceNearly));

        //  Log.i(">>>>>>>>>>>>>>>>>>>>>>", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }


    private boolean CheckData()
    {
        if (editTextStopPoint.getText().toString().isEmpty()||editTextAddress.getText().toString().isEmpty()
                ||editTextMinC.getText().toString().isEmpty() || editTextMaxC.getText().toString().isEmpty()
                ||editTextTimeArrive.getText().toString().isEmpty()||editTextSelectDay.getText().toString().isEmpty()
                || editTextTimeLeave.getText().toString().isEmpty() || editTextSelectDayLeave.getText().toString().isEmpty() )
        {
            Toast.makeText( this, "Vui lòng không để trống thông tin",Toast.LENGTH_SHORT ).show();
            return false;
        }
        if (Integer.parseInt(editTextMaxC.getText().toString()) < 0 ||
                Integer.parseInt(editTextMinC.getText().toString()) < 0 )
        {
            Toast.makeText( this, "Vui lòng không nhập số âm",Toast.LENGTH_SHORT ).show();
            return false;
        }
        //Check private or public
        return true;
    }

    private Address getAddress(double lat,double lng) {


        Address address = null;
        Geocoder geocoder = new Geocoder( updateSP.this );
        List<Address> list = new ArrayList<>(  );
        try
        {
            list = geocoder.getFromLocation( lat,lng,2 );

        } catch (IOException e){
            //Log.e( "TAG","geoLocate: IOException "+e.getMessage() );
        }

        if (list.size()>0){
            address = list.get( 0 );
        }
        return address;
    }

    private void Start(){
        final Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edtArrive.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, nam,thang,ngay);
        datePickerDialog.show();
    }

    private void End(){
        final Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edtLeave.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, nam,thang,ngay);
        datePickerDialog.show();
    }

    void readJson(){


        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTourInfo(token, Integer.parseInt(id));

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    noteList = new ArrayList<stopPoint>( );
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();

                        JSONObject stopPointData = new JSONObject(bodyListTour);
                        // Log.i("JSON",tourData.getString("total"));

                        JSONArray responseArray = stopPointData.getJSONArray("stopPoints");
                        Log.e("BBBBB",String.valueOf(responseArray.length()));
                        if (responseArray.length() > 0) {

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jb = responseArray.getJSONObject( i );
                                final Address address = getAddress(jb.getDouble("lat"),jb.getDouble("long"));
                                noteList.add( new stopPoint( jb.getString( "id" ),jb.getString( "name" ),address.getAddressLine(0) ,59, jb.getDouble("lat"),
                                        jb.getDouble("long"), jb.getLong( "leaveAt" ), jb.getLong( "arrivalAt" ),
                                        jb.getLong( "minCost" ),  jb.getLong( "maxCost" ), jb.getInt( "serviceTypeId" )  ) );


                            }
                            if (!noteList.isEmpty())
                            {

                                myAdapter = new Stop_Point_Adapter( getBaseContext(), R.layout.item_stoppoint_layout, noteList );
                                listView.setAdapter(myAdapter) ;
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    stopPoint stopPoint = noteList.get(position);

                    editTextStopPoint.setText(stopPoint.getName());
                    editTextAddress.setText(stopPoint.getAddress());
                    editTextTimeLeave.setText("11:30");
                    editTextTimeArrive.setText("17:00");
                    editTextMinC.setText(String.valueOf(stopPoint.getMinCost()));
                    editTextMaxC.setText(String.valueOf(stopPoint.getMaxCost()));
                    Date d = null;
                        d = new Date(stopPoint.getArrivalAt());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                    editTextSelectDay.setText( sdf.format( d ));
                    d = new Date(stopPoint.getLeaveAt());

                    editTextSelectDayLeave.setText( sdf.format( d ) );






                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );





    }
    void createStoppoint(){

        final RelativeLayout relativeLayout = findViewById(R.id.formStopPoint);
        relativeLayout.setVisibility(View.VISIBLE);
        final RelativeLayout relativeLayout1 =findViewById(R.id.mapLayout);
        relativeLayout1.setVisibility(View.INVISIBLE);
        Button button_x = findViewById( R.id.button_x );
        button_x.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.setVisibility(View.INVISIBLE);
                relativeLayout1.setVisibility(View.VISIBLE);
            }
        } );

        buttonCreateStopPoint.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckData())
                {
                    String dateInString = editTextSelectDay.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    Date dateTime = null;
                    try {
                        dateTime = sdf.parse( dateInString );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.e("date",dateTime.toString());
                    String dateInString2 = editTextSelectDayLeave.getText().toString();

                    Date dateTime2 = null;
                    try {
                        dateTime2 = sdf.parse( dateInString2 );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    stopPoint stopPoint = new stopPoint(noteList.get(position).getId(), editTextStopPoint.getText().toString(),editTextAddress.getText().toString(),
                            1,noteList.get(position).getLat(),noteList.get(position).getLng(),dateTime.getTime(),dateTime2.getTime(), Integer.parseInt( editTextMinC.getText().toString() ),Integer.parseInt( editTextMaxC.getText().toString() ),noteList.get(position).getServiceTypeId()) ;
                    noteList.set(position,stopPoint);
                    myAdapter = new Stop_Point_Adapter( updateSP.this,R.layout.item_stoppoint_layout,noteList );

                    listView.setAdapter( myAdapter );
                    relativeLayout.setVisibility(View.INVISIBLE);
                    relativeLayout1.setVisibility(View.VISIBLE);
                    Toast.makeText( updateSP.this,"Đã sửa stop point",Toast.LENGTH_SHORT ).show();
                }
            }

        } );
    }




}
