package com.example.dulich;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearlyByPlaces extends AsyncTask<Object,String,String> {
    private String googleplaceDate, url;
    private GoogleMap mMap;
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        DownloadURL downloadURL = new DownloadURL();
        try {
            googleplaceDate = downloadURL.ReadTheURL( url );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleplaceDate;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearByPlacesList = null;
        DataParser dataParser = new DataParser();
        nearByPlacesList = dataParser.parse( s );
        DisplayNearbyPlaces(nearByPlacesList);
    }

    private void DisplayNearbyPlaces(List<HashMap<String,String>> nearByPlacesList){
        for (int i = 0;i<nearByPlacesList.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googleNearbyPlaces = nearByPlacesList.get( i );
            String nameOfPlace = googleNearbyPlaces.get( "place_name");
            String vicinty = googleNearbyPlaces.get( "vicinty");
            double latitude = Double.parseDouble(  googleNearbyPlaces.get( "lat") );
            double longitude =  Double.parseDouble( googleNearbyPlaces.get( "lng"));
            String reference = googleNearbyPlaces.get( "reference");

            LatLng latLng = new LatLng( latitude,longitude );
            markerOptions.position( latLng );
            markerOptions.title( nameOfPlace + ":" +vicinty);
            markerOptions.icon( BitmapDescriptorFactory.defaultMarker() );

            mMap.addMarker( markerOptions );
            // Add a marker in current location and move the camera
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,17F) );
        }
    }
}
