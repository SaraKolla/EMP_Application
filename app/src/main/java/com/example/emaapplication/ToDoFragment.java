package com.example.emaapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
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
import java.util.Map;


public class ToDoFragment extends Fragment {
    private EditText eTitle, eDescription, eDate, eTime;
    private Button saveButton;
    private String title, description, date, time;
    final Calendar calendar = Calendar.getInstance();

    private ListView listView;
    private ArrayAdapter<String> adapter;




    private ArrayList<String> dataList = new ArrayList<>();

    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("EMP_ToDo");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);

        // Retrieving the values of edit texts
        eTitle = view.findViewById(R.id.editTitle);
        eDescription = view.findViewById(R.id.editDescription);
        eDate = view.findViewById(R.id.dateedittView);
        eTime = view.findViewById(R.id.editText1);
        saveButton = view.findViewById(R.id.addTaskButton);
        listView = view.findViewById(R.id.taskListView);

        // Adapter to display tasks in the ListView
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, dataList);
        listView.setAdapter(adapter);

        //hi hi hi hi hi hi hi hi hi hi


        // Calendar View
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

        eTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Get current time
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    // Create a TimePickerDialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    // Update the EditText with the selected time
                                    String selectedTime = hourOfDay + ":" + minute;
                                    eTime.setText(selectedTime);

                                    // Clear the focus from EditText
                                    eTime.clearFocus();
                                }
                            },
                            hour,
                            minute,
                            true
                    );

                    // Show the TimePickerDialog
                    timePickerDialog.show();
                }
            }
        });

        // Sending data to Firebase
        saveButton.setOnClickListener(v -> {
            title = eTitle.getText().toString();
            description = eDescription.getText().toString();
            date = eDate.getText().toString();
            time = eTime.getText().toString();

            // Create a new data object with the values
            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            data.put("description", description);
            data.put("date", date);
            data.put("time", time);

            // Push the data to the database
            database.child(title).setValue(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                            // Clear the input fields
                            eTitle.setText("");
                            eDescription.setText("");
                            eDate.setText("");
                            eTime.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Fetch data from Firebase and populate in ListView
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                    if (data != null) {
                        String title = (String) data.get("title");
                        String description = (String) data.get("description");
                        String date = (String) data.get("date");
                        String time = (String) data.get("time");
                        dataList.add(title + "\n" +  "\n" + description + "\n"+ "\n"+ date +" | "+ time);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




        //update and delete
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Handle the long press event
                showOptionsDialog(position);
                return true;
            }
            private void showOptionsDialog(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("What do you want ?");
                builder.setItems(new CharSequence[]{"Delete", "Update", "Cancel"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                deleteTask(position);
                                break;
                            case 1:
                                updateTask(position);
                                break;
                            case 2:
                                dialogInterface.dismiss();
                                break;
                        }
                    }
                });
                builder.show();
            }

            private void deleteTask(int position) {
                String task = dataList.get(position);
                String[] parts = task.split("\n");
                String title = parts[0];

                // Remove the task from the database
                database.child(title).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Task deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error deleting task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            private void updateTask(int position) {
                String task = dataList.get(position);
                String[] parts = task.split("\n");
                String title = parts[0];

                // Retrieve the task details from the database
                database.child(title).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            if (data != null) {
                                String description = (String) data.get("description");
                                String date = (String) data.get("date");
                                String time = (String) data.get("time");

                                // Open the update dialog with the task details
                               showUpdateDialog(title, description, date, time);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Error fetching task details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void showUpdateDialog(final String title, String description, String date, String time) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Update Task");

                // Inflate the dialog layout from the dotodialog.xml file
                View dialogView = getLayoutInflater().inflate(R.layout.dotodialog, null);
                builder.setView(dialogView);

                // Initialize the EditText fields in the dialog
                EditText titleUpdateEditText = dialogView.findViewById(R.id.titleupdate);
                EditText descriptionUpdateEditText = dialogView.findViewById(R.id.desupdate);
                EditText dateUpdateEditText = dialogView.findViewById(R.id.dateupdate);
                EditText timeUpdateEditText = dialogView.findViewById(R.id.timeupdate);

                // Set the initial values for the EditText fields
                titleUpdateEditText.setText(title);
                descriptionUpdateEditText.setText(description);
                dateUpdateEditText.setText(date);
                timeUpdateEditText.setText(time);

                // Set the input type for the date and time EditText fields
                dateUpdateEditText.setInputType(InputType.TYPE_NULL);
                timeUpdateEditText.setInputType(InputType.TYPE_NULL);

                // Set click listeners for the date and time EditText fields
                dateUpdateEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Create a DatePickerDialog and set the selected date
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                requireContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        // Update the date EditText field with the selected date
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                        calendar.set(Calendar.YEAR, year);
                                        calendar.set(Calendar.MONTH, month);
                                        calendar.set(Calendar.DAY_OF_MONTH, day);
                                        dateUpdateEditText.setText(dateFormat.format(calendar.getTime()));
                                    }
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );
                        datePickerDialog.show();
                    }
                });

                timeUpdateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            // Create a TimePickerDialog and set the selected time
                            TimePickerDialog timePickerDialog = new TimePickerDialog(
                                    getActivity(),
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            // Update the time EditText field with the selected time
                                            String selectedTime = hourOfDay + ":" + minute;
                                            timeUpdateEditText.setText(selectedTime);
                                            timeUpdateEditText.clearFocus();
                                        }
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                            );
                            timePickerDialog.show();
                        }
                    }
                });

                // Set the positive button and its click listener
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get the updated values from the EditText fields
                        String updatedTitle = titleUpdateEditText.getText().toString();
                        String updatedDescription = descriptionUpdateEditText.getText().toString();
                        String updatedDate = dateUpdateEditText.getText().toString();
                        String updatedTime = timeUpdateEditText.getText().toString();

                        // Update the task in the database
                        updateTaskInDatabase(title, updatedTitle, updatedDescription, updatedDate, updatedTime);
                    }
                });

                // Set the negative button and its click listener
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                // Create and show the dialog
                builder.create().show();
            }

            private void updateTaskInDatabase(final String originalTitle, final String updatedTitle, final String updatedDescription, final String updatedDate, final String updatedTime) {
                // Check if the task title has changed
                if (!originalTitle.equals(updatedTitle)) {
                    // If the title has changed, remove the old task and add the updated task with the new title
                    database.child(originalTitle).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // Create a new data object with the updated values
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("title", updatedTitle);
                                    data.put("description", updatedDescription);
                                    data.put("date", updatedDate);
                                    data.put("time", updatedTime);

                                    // Push the updated data to the database with the new title
                                    database.child(updatedTitle).setValue(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getActivity(), "Task updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), "Error updating task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error updating task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // If the title has not changed, update the task with the existing title
                    Map<String, Object> data = new HashMap<>();
                    data.put("title", updatedTitle);
                    data.put("description", updatedDescription);
                    data.put("date", updatedDate);
                    data.put("time", updatedTime);

                    database.child(updatedTitle).updateChildren(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Task updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error updating task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
