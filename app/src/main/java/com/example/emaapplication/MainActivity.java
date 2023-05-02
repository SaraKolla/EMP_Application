package com.example.emaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button Gob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Gob=findViewById(R.id.gob);
        Gob.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,WeatherLayer.class);
                startActivity(intent);
                finish();
            }
        });





    }
}