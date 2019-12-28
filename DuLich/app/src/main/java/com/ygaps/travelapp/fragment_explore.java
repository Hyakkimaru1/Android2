package com.ygaps.travelapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.ygaps.travelapp.Adapter.Stop_Point_Adapter;
import com.ygaps.travelapp.Adapter.rate_adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fragment_explore extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    SearchView searchView;
    ListView search_SP_in_maps;
    SharedPreferences preferences;
    String token;
    ArrayList<stopPoint> list_searchSP = new ArrayList<stopPoint>();
    Stop_Point_Adapter stop_point_adapter;
    int pageIndex = 1;
    String searchKey = null;
    MarkerOptions markerOptions;
    LatLng latLng;
    View mMapView;

    //Dialog stop point
    ImageButton reviews_sp;
    TextView nameSP;
    TextView place;
    TextView group;
    TextView priceMin;
    TextView priceMax;
    TextView textView6;
    TextView textView7;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    RatingBar ratingBar2;
    int total;
    int sum;
    ListView list_review_SP;
    TextView editText3;
    RatingBar ratingBar;
    Button button2;
    rate_adapter adapter;
    ArrayList<aRate> noteList;


    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    boolean flag_loading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_explore,container,false);
        search_SP_in_maps = view.findViewById( R.id.search_SP_in_mapsExplore );
        searchView = view.findViewById( R.id.SearchExplore );
        preferences = this.getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);

        token = preferences.getString( "token","" );

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
            init();
        }
        search_SP_in_maps.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                stopPoint stop_point = list_searchSP.get( i );
                markerOptions = new MarkerOptions();
                latLng = new LatLng( stop_point.getLat(),stop_point.getLng() );
                markerOptions.position( latLng );
                markerOptions.title(stop_point.getName());
                markerOptions.zIndex( Float.valueOf( stop_point.getId() ) );

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

        search_SP_in_maps.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final stopPoint stop_point = list_searchSP.get( i );

                final Dialog dialog = new Dialog( getContext() );
                dialog.setContentView( R.layout.dialog_rw_stop_point );
                reviews_sp = dialog.findViewById( R.id.reviewsStopPoint );
                nameSP = dialog.findViewById( R.id.place );
                place = dialog.findViewById( R.id.calendar );
                group = dialog.findViewById( R.id.group);
                priceMin = dialog.findViewById( R.id.priceMin );
                priceMax = dialog.findViewById( R.id.priceMax );
                textView6 = dialog.findViewById( R.id.textView6 );
                textView7 = dialog.findViewById( R.id.textView7 );
                textView2 = dialog.findViewById( R.id.textView2 );
                textView3 = dialog.findViewById( R.id.textView3 );
                textView4 = dialog.findViewById( R.id.textView4 );
                textView5 = dialog.findViewById( R.id.textView5 );
                ratingBar2 = dialog.findViewById( R.id.ratingBar2 );
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .getDetailSP( token, Integer.valueOf( stop_point.getId()) );
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200){
                            String body;
                            try {
                                body = response.body().string();

                                JSONObject object = new JSONObject( body );
                                nameSP.setText( object.getString( "name" ) );
                                place.setText( object.getString( "address" ) );
                                String serviceTypeId;
                                switch (object.getInt( "serviceTypeId" )){
                                    case 1:
                                        serviceTypeId = "Restaurant";
                                        break;
                                    case 2:
                                        serviceTypeId = "Hotel";
                                        break;
                                    case 3:
                                        serviceTypeId = "Rest Station";
                                        break;
                                    case 4:
                                        serviceTypeId = "Other";
                                        break;
                                    default:
                                        throw new IllegalStateException( "Unexpected value: " + object.getInt( "serviceTypeId" ) );
                                }
                                group.setText( serviceTypeId );
                                priceMin.setText( object.getString( "minCost" ) );
                                priceMax.setText( object.getString( "maxCost" ) );


                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText( getContext(),"SERVER ERROR",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );

                call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .getPointStatusSP( token, Integer.valueOf( stop_point.getId()));
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200){
                            String body;
                            try {

                                body = response.body().string();
                                JSONObject object = new JSONObject( body );


                                JSONArray jsonArray = object.getJSONArray( "pointStats" );
                                textView7.setText( jsonArray.getJSONObject( 4 ).getString( "total" ) );
                                textView2.setText( jsonArray.getJSONObject( 3 ).getString( "total" ) );
                                textView3.setText( jsonArray.getJSONObject( 2 ).getString( "total" ) );
                                textView4.setText( jsonArray.getJSONObject( 1 ).getString( "total" ) );
                                textView5.setText( jsonArray.getJSONObject( 0 ).getString( "total" ) );
                                total = jsonArray.getJSONObject( 4 ).getInt( "total" )*5 + jsonArray.getJSONObject( 3 ).getInt( "total" )*4+
                                        jsonArray.getJSONObject( 2 ).getInt( "total" )*3+jsonArray.getJSONObject( 1 ).getInt( "total" )*2+jsonArray.getJSONObject( 0 ).getInt( "total" )*1;
                                sum = jsonArray.getJSONObject( 4 ).getInt( "total" ) + jsonArray.getJSONObject( 3 ).getInt( "total" )+
                                        jsonArray.getJSONObject( 2 ).getInt( "total" )+jsonArray.getJSONObject( 1 ).getInt( "total" )+jsonArray.getJSONObject( 0 ).getInt( "total" );
                                if (sum == 0){
                                    sum = 1;
                                }
                                textView6.setText( String.valueOf( total/sum ) );
                                ratingBar2.setRating(  total/sum );

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText( getContext(),"SERVER ERROR",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );
                reviews_sp.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        DialogRate(Integer.valueOf(  stop_point.getId() ));
                    }
                } );
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                dialog.getWindow().setLayout((9*width)/10,(9*height)/10);
                dialog.show();
                return false;
            }
        } );
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapExplore);
        mapFragment.getMapAsync( this );
        mMapView = mapFragment.getView();
        return view;
    }


    private void init(){
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchStopPoint(s);
                pageIndex = 1;
                flag_loading = false;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 0){
                    searchStopPoint(s);
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

                                list_searchSP.add( new stopPoint(object.getString( "id" ), object.getString( "name" ),object.getString( "address" ),object.getInt( "provinceId" ),
                                        object.getDouble( "lat") ,object.getDouble( "long" ),object.getLong( "minCost" ),
                                        object.getLong( "maxCost" ),object.getInt( "serviceTypeId" )) );
                            }

                            if (stop_point_adapter == null)
                            {
                                stop_point_adapter = new Stop_Point_Adapter( getContext(),R.layout.item_stoppoint_layout,list_searchSP );
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
                    Toast.makeText(getContext(),"Có lỗi, vui lòng thử lại sau.",Toast.LENGTH_LONG ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText( getContext(),"Có lỗi, vui lòng thử lại sau.",Toast.LENGTH_LONG ).show();
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
                        } ).start();
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
                                        object.getLong( "maxCost" ), object.getInt( "serviceTypeId" )) );
                            }
                            stop_point_adapter.notifyDataSetChanged();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText( getContext(), "Có lỗi, vui lòng thử lại sau.", Toast.LENGTH_LONG ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval( 1100 );
        locationRequest.setFastestInterval( 1100 );
        locationRequest.setPriority( LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );
        if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED)
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
      //  double latitude = location.getLatitude();
      //  double longitude = location.getLongitude();
      //  MarkerOptions markerOptions = new MarkerOptions();
      //  markerOptions.position( latLng );
      //  markerOptions.title( "Vị trí hiện tại" );
      //  markerOptions.icon( BitmapDescriptorFactory.fromResource(R.drawable.location_choose) );

       // currentUserLocationMarker = mMap.addMarker( markerOptions );
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
                                    markerOptions.zIndex( Float.valueOf( jb.getString( "id" ) ) );
                                    //Log.e("EEEE",String.valueOf( markerOptions.getZIndex() ));
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
                        //   Toast.makeText( MapsActivity.this,"GOOODDD",Toast.LENGTH_SHORT ).show();
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
        if (googleApiClient!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates( googleApiClient,this );

        }

        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
               // Log.e("serviceID: ",String.valueOf( marker.getZIndex() ));
                final Dialog dialog = new Dialog( getContext() );
                dialog.setContentView( R.layout.dialog_rw_stop_point );
                reviews_sp = dialog.findViewById( R.id.reviewsStopPoint );
                nameSP = dialog.findViewById( R.id.place );
                place = dialog.findViewById( R.id.calendar );
                group = dialog.findViewById( R.id.group);
                priceMin = dialog.findViewById( R.id.priceMin );
                priceMax = dialog.findViewById( R.id.priceMax );
                textView6 = dialog.findViewById( R.id.textView6 );
                textView7 = dialog.findViewById( R.id.textView7 );
                textView2 = dialog.findViewById( R.id.textView2 );
                textView3 = dialog.findViewById( R.id.textView3 );
                textView4 = dialog.findViewById( R.id.textView4 );
                textView5 = dialog.findViewById( R.id.textView5 );
                ratingBar2 = dialog.findViewById( R.id.ratingBar2 );
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .getDetailSP( token, (int) marker.getZIndex());
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200){
                            String body;
                            try {
                                body = response.body().string();

                                JSONObject object = new JSONObject( body );
                                nameSP.setText( object.getString( "name" ) );
                                place.setText( object.getString( "address" ) );
                                String serviceTypeId;
                                switch (object.getInt( "serviceTypeId" )){
                                    case 1:
                                        serviceTypeId = "Restaurant";
                                        break;
                                    case 2:
                                        serviceTypeId = "Hotel";
                                        break;
                                    case 3:
                                        serviceTypeId = "Rest Station";
                                        break;
                                    case 4:
                                        serviceTypeId = "Other";
                                        break;
                                    default:
                                        throw new IllegalStateException( "Unexpected value: " + object.getInt( "serviceTypeId" ) );
                                }
                                group.setText( serviceTypeId );
                                priceMin.setText( object.getString( "minCost" ) );
                                priceMax.setText( object.getString( "maxCost" ) );


                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText( getContext(),"SERVER ERROR",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );

                call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .getPointStatusSP( token, (int) marker.getZIndex());
                call.enqueue( new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200){
                            String body;
                            try {

                                body = response.body().string();
                                JSONObject object = new JSONObject( body );

                                JSONArray jsonArray = object.getJSONArray( "pointStats" );
                                textView7.setText( jsonArray.getJSONObject( 4 ).getString( "total" ) );
                                textView2.setText( jsonArray.getJSONObject( 3 ).getString( "total" ) );
                                textView3.setText( jsonArray.getJSONObject( 2 ).getString( "total" ) );
                                textView4.setText( jsonArray.getJSONObject( 1 ).getString( "total" ) );
                                textView5.setText( jsonArray.getJSONObject( 0 ).getString( "total" ) );
                                total = jsonArray.getJSONObject( 4 ).getInt( "total" )*5 + jsonArray.getJSONObject( 3 ).getInt( "total" )*4+
                                        jsonArray.getJSONObject( 2 ).getInt( "total" )*3+jsonArray.getJSONObject( 1 ).getInt( "total" )*2+jsonArray.getJSONObject( 0 ).getInt( "total" )*1;
                                sum = jsonArray.getJSONObject( 4 ).getInt( "total" ) + jsonArray.getJSONObject( 3 ).getInt( "total" )+
                                        jsonArray.getJSONObject( 2 ).getInt( "total" )+jsonArray.getJSONObject( 1 ).getInt( "total" )+jsonArray.getJSONObject( 0 ).getInt( "total" );
                                if (sum == 0)
                                {
                                    sum = 1;
                                }
                                textView6.setText( String.valueOf( total/sum ) );
                                ratingBar2.setRating(  total/sum );

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText( getContext(),"SERVER ERROR",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                } );
                reviews_sp.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        DialogRate((int) marker.getZIndex());
                    }
                } );
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                dialog.getWindow().setLayout((9*width)/10,(9*height)/10);
                dialog.show();

                return false;
            }
        } );


    }
    private void DialogRate(final int id){


        final Dialog dialog1= new Dialog(getContext());
        dialog1.setContentView(R.layout.send_rv_stoppoint);

        list_review_SP = dialog1.findViewById( R.id.list_review_SP );
        editText3 = dialog1.findViewById( R.id.editText3 );
        ratingBar = dialog1.findViewById( R.id.ratingBar );
        button2 = dialog1.findViewById( R.id.button2 );

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getlistReview( token, id,1,100);
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();
                        noteList = new ArrayList<>( );
                        JSONObject stopPointData = new JSONObject(bodyListTour);
                        // Log.i("JSON",tourData.getString("total"));


                        JSONArray responseArray = stopPointData.getJSONArray("feedbackList");
                        //Log.e("feedbackList",String.valueOf(responseArray.length()));
                        if (responseArray.length() > 0) {

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jb = responseArray.getJSONObject( i );
                                noteList.add( new aRate( jb.getInt( "id" ),jb.getString( "name" ) , jb.getInt( "point" ),jb.getString( "feedback" ),
                                        jb.getString( "createdOn" )) );


                            }

                            adapter = new rate_adapter( getContext(), R.layout.item_rate, noteList );
                            list_review_SP.setAdapter(adapter) ;


                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else Log.e("RRRRRRRRrr",String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );

        button2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReview( id,editText3.getText().toString(), Math.round(ratingBar.getRating()));
                editText3.setText( "" );

            }
        } );
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        dialog1.getWindow().setLayout((9*width)/10,(9*height)/10);
        dialog1.show();

    }

    public void sendReview(int serviceId, String feedback1, int point){

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .sendFeedback(token,serviceId,feedback1,point);
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    String bodyTourCreate = null;
                    try {
                        bodyTourCreate = response.body().string();

                        JSONObject tourData = new JSONObject(bodyTourCreate);

                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText( getContext(),"Send error!!!",Toast.LENGTH_SHORT ).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );

        call = RetrofitClient
                .getInstance()
                .getApi()
                .getlistReview( token, serviceId,1,100);
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200) {
                    String bodyListTour = null;
                    try {
                        bodyListTour = response.body().string();
                        noteList.clear();
                        noteList = new ArrayList<>( );
                        JSONObject stopPointData = new JSONObject(bodyListTour);
                        // Log.i("JSON",tourData.getString("total"));


                        JSONArray responseArray = stopPointData.getJSONArray("feedbackList");
                        //Log.e("feedbackList",String.valueOf(responseArray.length()));
                        if (responseArray.length() > 0) {

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jb = responseArray.getJSONObject( i );
                                noteList.add( new aRate( jb.getInt( "id" ),jb.getString( "name" ) , jb.getInt( "point" ),jb.getString( "feedback" ),
                                        jb.getString( "createdOn" )) );


                            }
                            if (adapter != null){
                                adapter.clear();
                            }
                            adapter = new rate_adapter( getContext(), R.layout.item_rate, noteList );
                            list_review_SP.setAdapter(adapter) ;


                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else Log.e("RRRRRRRRrr",String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );


    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission( getContext(),Manifest.permission.ACCESS_FINE_LOCATION )!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale( getActivity(),Manifest.permission.ACCESS_FINE_LOCATION )){
                ActivityCompat.requestPermissions( getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else {
                ActivityCompat.requestPermissions( getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
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
                    if (ContextCompat.checkSelfPermission( getContext(),Manifest.permission.ACCESS_FINE_LOCATION )==PackageManager.PERMISSION_GRANTED)
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
                    Toast.makeText( getContext(),"Permission Denied",Toast.LENGTH_SHORT ).show();
                }
                return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled( true );
        }
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 250, 200, 0);
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder( getContext() )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
        googleApiClient.connect();
    }
}