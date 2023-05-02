package com.example.emaapplication;

import static android.content.ContentValues.TAG;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.Manifest;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HistoryFragment extends Fragment {

    private Spinner yearSpinner;
    private Spinner MonthSpinner;
    private Spinner dateSpinner;
    private ArrayAdapter<String> yearAdapter;
    private ArrayAdapter<String> MonthAdapter;
    private  ArrayAdapter<String> dateAdapter;
    private DatabaseReference mDatabase;
    private TextView dateoutput ,sCity, sHumidity, sDate, sNoise_Levels, sTemperature, sTime, sWind_Speed;

    private EditText eText;
    private TimePickerDialog picker;
    private Button btnGet, btnSearch;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);


        // get year
        yearSpinner = view.findViewById(R.id.yeardorpdownbox);
        yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getYearList());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        MonthSpinner = view.findViewById(R.id.Monthdorpdownbox);
        MonthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"-Select Month-","January","February","March","April","May","June","July","August","September","October","November","December"});
        MonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MonthSpinner.setAdapter(MonthAdapter);

        dateSpinner = view.findViewById(R.id.datedorpdownbox);
        dateAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"-Select Date-","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"});
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dateAdapter);

        sCity = view.findViewById(R.id.cityoutput);
        sHumidity = view.findViewById(R.id.Humidityoutput);
        sTime = view.findViewById(R.id.timeoutput);
        sDate = view.findViewById(R.id.dateoutput);
        sWind_Speed= view.findViewById(R.id.windoutput);
        sNoise_Levels = view.findViewById(R.id.Noise_leveloutput);
        sTemperature = view.findViewById(R.id.temperatureoutput);



        eText = view.findViewById(R.id.editText1);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(requireContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                eText.setText(sHour + ":" + sMinute + ":" + "17");
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
        btnGet = view.findViewById(R.id.button1);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Get values", Toast.LENGTH_SHORT).show();
            }
        });
        btnSearch = view.findViewById(R.id.s_button);

        //get values from DataBase

        btnSearch.setOnClickListener(v -> {
            String getYear = yearSpinner.getSelectedItem().toString();
            String getMonth = MonthSpinner.getSelectedItem().toString();
            String getDate = dateSpinner.getSelectedItem().toString();
            String finalDate = (getYear +" " + getMonth + " " + getDate);
            String getTime = eText.getText().toString();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("EMP_Weather").child(getTime).child(finalDate);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String p_City = snapshot.child("City").getValue(String.class);
                        String p_Humidity = snapshot.child("Humidity").getValue(String.class);
                        String p_Date = snapshot.child("Month_and_Date").getValue(String.class);
                        String p_Noise_Levels = snapshot.child("Noise_Levels").getValue(String.class);
                        String p_Temperature = snapshot.child("Temperature").getValue(String.class);
                        String p_Time = snapshot.child("Time").getValue(String.class);
                        String p_Wind_Speed = snapshot.child("Wind_Speed").getValue(String.class);

                        // Set the retrieved data to TextViews
                        sCity.setText(p_City);
                        sHumidity.setText(p_Humidity);
                        sDate.setText(p_Date);
                        sNoise_Levels.setText(p_Noise_Levels);
                        sTemperature.setText(p_Temperature);
                        sTime.setText(p_Time);
                        sWind_Speed.setText(p_Wind_Speed);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Error saving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        });

        return view;



    }

    private List<String> getYearList() {
        List<String> yearList = new ArrayList<>();

        // Get the current year
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        // Add the years you want to display in the drop-down menu
        for (int i = currentYear; i >= 2010; i--) {
            yearList.add(Integer.toString(i));
        }

        return yearList;
    }

}