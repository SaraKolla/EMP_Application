
package com.example.emaapplication;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;;

public class ToDoFragment extends Fragment {



    private EditText editTitle;
    private EditText editDescription;
    private EditText dateedittView;
    private EditText timeedittView;

    final Calendar calendar = Calendar.getInstance();
    private String p_Title, p_Description, p_Date, p_Time, title, description, date, time ;

    // creating variables for our list view.
    private ListView taskListView;
    private EditText eText;
    private TimePickerDialog picker;
    private Button btnGet;
    // creating a new array list.
    private ArrayList<String> taskList;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    // creating a variable for database reference.
    DatabaseReference reference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);

        // initializing variables for listviews
        taskListView =view.findViewById(R.id.taskListView);

        // initializing our array list
        taskList = new ArrayList<String>();



        editTitle = view.findViewById(R.id.editTitle);
        editDescription = view.findViewById(R.id.editDescription);
        dateedittView = view.findViewById(R.id.dateedittView);
        timeedittView = view.findViewById(R.id.editText1);

        Button addTaskButton = view.findViewById(R.id.addTaskButton);

        addTaskButton.setOnClickListener(v -> {
            title = editTitle.getText().toString();
            description = editDescription.getText().toString();
            date = dateedittView.getText().toString();
            time = eText.getText().toString();

            // Create a new data object with the values
            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            data.put("description", description);
            data.put("date", date);
            data.put("time", time);

            // Push the data to the database
            database.child("EMP_ToDo").child(time).child(title).setValue(data)
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

        Button testTaskButton = view.findViewById(R.id.testTaskButton);


            // creating a new array adapter for our list view.
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, taskList);

            // below line is used for getting reference
            // of our Firebase Database.
            reference = FirebaseDatabase.getInstance().getReference();

            // in below line we are calling method for add child event
            // listener to get the child of our database.

            database.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // this method is called when new child is added to
                    // our data base and after adding new child
                    // we are adding that item inside our array list and
                    // notifying our adapter that the data in adapter is changed.
                    taskList.add(snapshot.getValue(String.class));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // this method is called when the new child is added.
                    // when the new child is added to our list we will be
                    // notifying our adapter that data has changed.
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    // below method is called when we remove a child from our database.
                    // inside this method we are removing the child from our array list
                    // by comparing with it's value.
                    // after removing the data we are notifying our adapter that the
                    // data has been changed.
                    taskList.remove(snapshot.getValue(String.class));
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // this method is called when we move our
                    // child in our database.
                    // in our code we are note moving any child.
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // this method is called when we get any
                    // error from Firebase with error.
                }
            });
            // below line is used for setting
            // an adapter to our list view.
            taskListView.setAdapter(adapter);



        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected task
                String selectedTask = ((TextView) view.findViewById(R.id.textTitle)).getText().toString();

                // Show a dialog box to ask the user whether they want to delete or update the task
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Task Options");
                builder.setMessage("What would you like to do with this task?");


                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Delete the selected task

                    }
                });

            }
        });

        final TextInputEditText calendarInput = view.findViewById(R.id.dateedittView);
        calendarInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a DatePickerDialog with the current date and set an OnDateSetListener
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // Update the calendar with the selected date
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);

                                // Update the calendar input text with the selected date
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                calendarInput.setText(dateFormat.format(calendar.getTime()));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

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
                                eText.setText(sHour + ":" + sMinute);
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

        return view;

    }




}