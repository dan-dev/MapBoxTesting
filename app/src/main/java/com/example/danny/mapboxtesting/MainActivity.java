package com.example.danny.mapboxtesting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, "pk.eyJ1IjoiZGFuLWRldiIsImEiOiJjajBwdHM1NTUwMGZ6Mndtd3BxaXpyb3FtIn0.z_ap6R88cPHTzu5RvMlvlQ");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                List<LatLng> coordsList = new ArrayList<LatLng>();
                final Place place = new Place();
                final List<Place> placeList = place.getPlacesList();

                int i = 0;
                for (Place p : placeList){
                    LatLng latLng = p.getCoord();
                    mapboxMap.addMarker(new MarkerOptions().position(new LatLng(latLng)).title(p.getName())).setId(i);
                    i++;
                }
                /*
                mapboxMap.addMarker(new MarkerOptions().position(new LatLng(41.157996, -8.629103)).title("Porto"));
                mapboxMap.addMarker(new MarkerOptions().position(new LatLng(41.232931, -8.622427)).title("Maia"));
                mapboxMap.addMarker(new MarkerOptions().position(new LatLng(38.749579, -9.150046)).title("Lisboa"));
                mapboxMap.addMarker(new MarkerOptions().position(new LatLng(41.330668, -8.566435)).title("Trofa"));*/

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

}