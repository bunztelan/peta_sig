package com.example.peta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Objects;

public class SelectLocation extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private Button buttonSubmit;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        bindView();
        setupToolbar();
        setupMap();
    }

    private void bindView(){
        toolbar = findViewById(R.id.my_toolbar);
        buttonSubmit = findViewById(R.id.buttonSelectLocation);
        mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    private void setupToolbar(){
        toolbar.setTitle("Pilih Lokasi Bencana");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


    private void setupMap(){
        //memastikan mapfragment tidak null
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap=map;
    }
}