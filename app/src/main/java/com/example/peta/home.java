package com.example.peta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class home extends AppCompatActivity {
    CardView peta;
    CardView grafik;
    CardView lapor;
    CardView kontak;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        peta = findViewById(R.id.peta);
        grafik = findViewById(R.id.grafik);
        lapor = findViewById(R.id.lapor);
        kontak = findViewById(R.id.kontak);


        peta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(home.this, Map.class));
            }
        });

        grafik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(home.this, grafik.class));
            }
        });

        lapor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(home.this, lapor.class));
            }
        });

        kontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(home.this, kontak.class));
            }
        });

    }
}