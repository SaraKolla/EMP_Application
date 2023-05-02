package com.example.emaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.emaapplication.databinding.ActivityMainBinding;
import com.example.emaapplication.databinding.ActivityWeatherLayerBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WeatherLayer extends AppCompatActivity {
        private ActivityWeatherLayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherLayerBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_weather_layer);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragement(new HomeFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragement(new HomeFragment());
                    break;
                case R.id.History:
                    replaceFragement(new HistoryFragment());
                    break;
                case R.id.ToDo:
                    replaceFragement(new ToDoFragment());
                    break;
            }

            return true;
        });
    }
    private void replaceFragement(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navFrameid1,fragment);
        fragmentTransaction.commit();
    }
}