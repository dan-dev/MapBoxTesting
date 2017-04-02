package com.example.danny.mapboxtesting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapzen.android.lost.internal.LocationEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap map;
    private FloatingActionButton questionFAB;
    private LocationServices locationServices;
    Context context = this;

    private static final int PERMISSIONS_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, "pk.eyJ1IjoiZGFuLWRldiIsImEiOiJjajBwdHM1NTUwMGZ6Mndtd3BxaXpyb3FtIn0.z_ap6R88cPHTzu5RvMlvlQ");
        setContentView(R.layout.activity_main);

        /*
        questionFAB = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        questionFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionListActivity.class);
                startActivity(intent);
            }
        });*/

        final Button questionsBTN = (Button) findViewById(R.id.questionBtn);

        questionsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggleGps(true);
                Intent intent = new Intent(context, QuestionListActivity.class);
                startActivity(intent);
            }
        });

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                List<LatLng> coordsList = new ArrayList<LatLng>();
                final Place place = new Place();
                final List<Place> placeList = place.getPlacesList();

                int i = 0;
                for (Place p : placeList){
                    LatLng latLng = p.getCoord();
                    mapboxMap.addMarker(new MarkerOptions().position(new LatLng(latLng)).title(p.getName())).setId(i);
                    i++;
                }

                mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter(){
                    @Nullable
                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {
                        Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                        View v = getLayoutInflater().inflate(R.layout.layout_info_window, null);
                        TextView textView = (TextView) v.findViewById(R.id.text);
                        ImageView imageView = (ImageView) v.findViewById(R.id.image);
                        Button button = (Button) v.findViewById(R.id.button_go);
                        final int id = (int) marker.getId();
                        textView.setText(placeList.get(id).getName());
                        Resources resources = context.getResources();
                        final int res = resources.getIdentifier(placeList.get(id).getImage(), "drawable", context.getPackageName());
                        imageView.setImageResource(res);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "showing", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getBaseContext(), PlaceDetails.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        });
                        return v;
                    }
                });



            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            if (!locationServices.areLocationPermissionsGranted()) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            Location lastLocation = locationServices.getLastLocation();
            if (lastLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));
            }

            locationServices.addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        // Move the map camera to where the user location is and then remove the
                        // listener so the camera isn't constantly updating when the user location
                        // changes. When the user disables and then enables the location again, this
                        // listener is registered again and will adjust the camera once again.
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
                        locationServices.removeLocationListener(this);
                    }
                }
            });
            //floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
        } else {
            //floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation(true);
            }
        }
    }

}