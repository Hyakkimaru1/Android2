package com.ygaps.travelapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
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
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
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
    private List<Point> pointList = new ArrayList<>();
    int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //access tokens of your account in strings.xml
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_maps_follow_thetour);
        mapView = findViewById(R.id.mapView);
        sharedPreferences = getSharedPreferences("isLogin",MODE_PRIVATE);
        Authorization = sharedPreferences.getString( "token","" );
        tourId = 4698;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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
        // Check marker if existed => delete
      /*  if(destinationPoint!=null)
            markerView.remove();
        // draw marker
        IconFactory iconFactory = IconFactory.getInstance( maps_follow_thetour.this );
        Icon icon = iconFactory.fromResource( R.drawable.place );
        markerView = mapboxMap.addMarker(new MarkerOptions()
                .position(point)
                .icon( icon )
                .title("Eiffel Tower "));
        destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());
        double distanceBetweenLastAndSecondToLastClickPoint = 0;
        distanceBetweenLastAndSecondToLastClickPoint = TurfMeasurement.distance( originPoint, destinationPoint);
        Log.e("Space in:",String.valueOf( distanceBetweenLastAndSecondToLastClickPoint ) );
        // get router to draw Direction
        getRoute(originPoint, destinationPoint); */
        return true;
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
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
            // Set the component's camera mode
            locationComponent.setCameraMode( CameraMode.TRACKING);

            new Thread( new Runnable() {
                public void run() {
                    try {
                        boolean check = true;
                        while (check){
                            sleep(500);

                            if (locationComponent.getLastKnownLocation() != null){

                                originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                                        locationComponent.getLastKnownLocation().getLatitude());
                                Log.i("Size: ",String.valueOf( pointList.size() ));
                                getRoute(originPoint,pointList.get(index));
                                index++;
                                check = false;
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            } ).start();

            /*
            */
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}