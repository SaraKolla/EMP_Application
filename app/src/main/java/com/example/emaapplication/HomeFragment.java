package com.example.emaapplication;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.widget.TextView;
import java.lang.String;

public class HomeFragment extends Fragment implements LocationListener{

    private TextView cityTextView;
    private TextView noiseOutput;
    private LocationManager locationManager;
    private TextView dateTextView;

    private String timeText;
    private TextView greetingTextView;

    private Button bSave;
    private TextView locationTextView;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private TextView tempout, humout, windout;

    private RequestQueue requestQueue;

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1000;
    private final int REQUEST_MICROPHONE = 1500;
    private final int EXTERNAL_STORAGE = 1265;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bSave = view.findViewById(R.id.bSave);
        noiseOutput = (TextView) view.findViewById(R.id.noiseoutput);
        dateTextView = view.findViewById(R.id.tvmonthdate);
        cityTextView = view.findViewById(R.id.livecity);
        greetingTextView = view.findViewById(R.id.tvgreeting_msj);

        // Initialize the TextViews
        tempout = view.findViewById(R.id.tempout);
        humout = view.findViewById(R.id.humout);
        windout = view.findViewById(R.id.windout);

        // Create a new request queue
        requestQueue = Volley.newRequestQueue(getContext());
        loadNoiseLevelService();

        // Get current date
        Date currentDate = Calendar.getInstance().getTime();

        // Format date to display month and day
        DateFormat dateFormat = new SimpleDateFormat("yyyy MMMM dd");
        String formattedDate = dateFormat.format(currentDate);

        // Extract month and day from formatted date string
        String monthAndDay = formattedDate.substring(5);

        // Set the formatted date to the text view
        dateTextView.setText(monthAndDay);

        // Set the greeting based on the current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat real_time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = real_time.format(calendar.getTime());
        timeText = formattedTime;
        String t2 = timeText.replaceAll("\\W+","");

        int hour = Integer.parseInt(formattedTime.substring(0, 2));

        if (hour >= 5 && hour < 12) {
            greetingTextView.setText("Good morning!");
        } else if (hour >= 12 && hour < 18) {
            greetingTextView.setText("Good afternoon!");
        } else {
            greetingTextView.setText("Good evening!");
        }

        cityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickgooglemap();
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder DiaBox = new AlertDialog.Builder(getContext());
                View DiaView = getLayoutInflater().inflate(R.layout.home_popup_box, null);
                EditText sfiletext = (EditText) DiaView.findViewById(R.id.sfiletext);
                Button psave = (Button) DiaView.findViewById(R.id.dialog_saveB);

                // For Save Button
        psave.setOnClickListener(v -> {
            String SaveName = sfiletext.getText().toString();
            String time = timeText;
            String city = locationTextView.getText().toString();
            String yearMonthAndDate = formattedDate;
            String Temperature = tempout.getText().toString();
            String Humidity = humout.getText().toString();
            String Wind_Speed = windout.getText().toString();
            String Noise_Levels = noiseOutput.getText().toString();

            // Create a new data object with the values
            Map<String, Object> data = new HashMap<>();
            data.put("Save Name", SaveName);
            data.put("City", city);
            data.put("Time", time);
            data.put("Month_and_Date", yearMonthAndDate);
            data.put("Temperature", Temperature);
            data.put("Humidity", Humidity);
            data.put("Wind_Speed", Wind_Speed);
            data.put("Noise_Levels", Noise_Levels);

            // Push the data to the database
            database.child("EMP_Weather").child(SaveName).setValue(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

            DiaBox.setView(DiaView);
            AlertDialog dialog = DiaBox.create();
            dialog.show();

            }
        });
        return view;
    }
    private void clickgooglemap(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.navFrameid1, new GoogleMap())
                .addToBackStack(null)
                .commit();
    }
    private void loadWhetherAPI(String cityName){

        // Construct the API URL
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + "bbc7578fd06ba33dc5f2c409fee7af85" + "&units=metric";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, (Response.Listener<JSONObject>) response -> {
            try {
                Log.i("SDSDSD", response.toString());
                // Get the temperature data from the JSON response
                JSONObject main = response.getJSONObject("main");
                double temp = main.getDouble("temp");

                // Update the temperature TextView with the data
                tempout.setText(String.format("%.1f °C", temp));

                // Get the humidity data from the JSON response
                int humidity = main.getInt("humidity");

                // Update the humidity TextView with the data
                humout.setText(humidity + "%");

                // Get the wind speed data from the JSON response
                JSONObject wind = response.getJSONObject("wind");
                double speed = wind.getDouble("speed");

                // Update the wind speed TextView with the data
                windout.setText(String.format("%.1f m/s", speed));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                (Response.ErrorListener) error -> error.printStackTrace()
        );

        // Add the request to the queue
        requestQueue.add(jsonRequest);
    }

    private void loadNoiseLevelService(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            return;
        }

        try{// For Noise Level
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/dev/null");

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_MICROPHONE);
                return;

            }

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE);
                return;
            }

            recorder.prepare();
            recorder.start();

            final Handler handler =new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int amplitude = recorder.getMaxAmplitude();
                    double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
                    String formattedAmplitude = String.format("%.2f", amplitudeDb);

                    // format to two decimal numbers
                    noiseOutput.setText(formattedAmplitude + " dB");
                    handler.postDelayed(this, 1000);

                }
            }, 1000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Remove location updates to conserve battery
        locationManager.removeUpdates(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if the user has granted location permissions
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            // Request location permissions
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Get the city name from the location
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String cityName = addresses.get(0).getLocality();
            loadWhetherAPI(addresses.get(0).getLocality());
            // Update the UI with the city name
             locationTextView = getView().findViewById(R.id.livecity);
            locationTextView.setText(cityName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}