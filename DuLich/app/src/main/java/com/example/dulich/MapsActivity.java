package com.example.dulich;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
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
    private EditText searchText;
    MarkerOptions markerOptions;
    LatLng latLng;

    EditText editTextStopPoint;
    EditText editTextAddress;
    Spinner serviceType;
    Spinner province;
    EditText editTextTimeArrive;
    EditText editTextSelectDay;
    EditText editTextTimeLeave;
    EditText editTextSelectDayLeave;
    EditText editTextMinC ;
    EditText editTextMaxC;


    int Radius = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );


        searchText = findViewById( R.id.Search );

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
            init();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
    }

    private void init(){
        searchText.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {

                if (actionID == EditorInfo.IME_ACTION_SEARCH||actionID==EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction()==KeyEvent.ACTION_DOWN || keyEvent.getAction()==KeyEvent.KEYCODE_ENTER){
                    geoLocate();

                }
                return false;
            }
        } );
    }

    private void geoLocate() {
        mMap.clear();
        String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder( MapsActivity.this );
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
            markerOptions.title( searchString );
            markerOptions.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) );
            currentUserLocationMarker = mMap.addMarker( markerOptions );
            // Add a marker in current location and move the camera
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,17F) );
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
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
            rlp.setMargins(0, 1500, 180, 0);
        }

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

        Toast.makeText( this,location.getLatitude()+"  "+location.getLongitude(),Toast.LENGTH_SHORT ).show();
        latLng = new LatLng( location.getLatitude(),location.getLongitude() );
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        findPlaceNearly(latitude,longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( latLng );
        markerOptions.title("Vị trí hiện tại");
        markerOptions.icon( BitmapDescriptorFactory.defaultMarker() );

        currentUserLocationMarker = mMap.addMarker( markerOptions );
        // Add a marker in current location and move the camera
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,17F) );
        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText( MapsActivity.this, marker.getTitle(),Toast.LENGTH_SHORT).show();
                   

                return false;
            }
        } );
        if (googleApiClient!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates( googleApiClient,this );

        }
    }

    private void findPlaceNearly(double latitude,double longitude)
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

    private String getURL(double lat, double lng,String nearbyPlace){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+lat+","+lng);
        googlePlaceUrl.append("&radius="+Radius);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+getString(R.string.apikeyPlaceNearly));

        Log.i(">>>>>>>>>>>>>>>>>>>>>>", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }


    private boolean CheckData()
    {
        if (editTextStopPoint.getText().toString().isEmpty()||editTextAddress.getText().toString().isEmpty()
                ||editTextMinC.getText().toString().isEmpty() || editTextMaxC.getText().toString().isEmpty()
                ||editTextTimeArrive.getText().toString().isEmpty()||editTextSelectDay.getText().toString().isEmpty()
                || editTextTimeLeave.getText().toString().isEmpty() || editTextSelectDayLeave.getText().toString().isEmpty() )
        {
            Toast.makeText( MapsActivity.this, "Vui lòng không để trống thông tin",Toast.LENGTH_SHORT ).show();
            return false;
        }
        if (Integer.parseInt(editTextMaxC.getText().toString()) < 0 ||
                Integer.parseInt(editTextMinC.getText().toString()) < 0 )
        {
            Toast.makeText( MapsActivity.this, "Vui lòng không nhập số âm",Toast.LENGTH_SHORT ).show();
            return false;
        }
        return true;
        //Check private or public
    }

    private Address getNameMarker(double lat,double lng) {

        Geocoder geocoder = new Geocoder( MapsActivity.this );
        Address address= null;
        List<Address> list = new ArrayList<>(  );
        try
        {
            list = geocoder.getFromLocation( lat,lng,2 );

        } catch (IOException e){
            Log.e( "TAG","geoLocate: IOException "+e.getMessage() );
        }

        if (list.size()>0){
             address = list.get( 0 );

        }
        return address;
    }
}
