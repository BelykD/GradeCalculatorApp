package com.example.gradecalculator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ExamScheduler extends AppCompatActivity {

    private CalendarView calendarView ;
    private Button saveDate, viewSaves;
    private TextView examTitle, examDesc;
    private EditText notificationName;
    private final String FILENAME = "exam-notifications.txt"; // Save file name
    private Calendar selectedDate;
    private int day, month, year;
    private SharedPreferences sharedPreferences;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_scheduler);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        calendarView = findViewById(R.id.datePicker); // Calendar
        selectedDate = Calendar.getInstance();
        saveDate = findViewById(R.id.saveDate); // Buttons
        viewSaves = findViewById(R.id.viewSaves);
        examTitle = findViewById(R.id.examTitle); // TextView
        examDesc = findViewById(R.id.examDesc);
        notificationName = findViewById(R.id.notificationName); // EditText

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);

        // Check for dark or light mode
        darkModeToggle();

        saveDate.setOnClickListener(view -> {
            if (notificationName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println(getDate()); // Testing output
                Toast.makeText(this, "Notification set for: " + getDate(), Toast.LENGTH_SHORT).show();
                setNotification();
            }
        });
        // Button to open exam saves page
        viewSaves.setOnClickListener(view -> {
            openExamSaves();
        });
        // Listener to update date when calendar values are changed
        calendarView.setOnDateChangeListener((view, y, m, d) -> {
            // Update the selected date when the user picks a new date
            selectedDate.set(y, m, d);
            // Log.d("Selected Date", "Day: " + d + ", Month: " + (m + 1) + ", Year: " + y);
            setDate(y, m, d); // Setting newly selected date
        });
    }
    // Method to set date using the calendar view setOnDateChangeListener
    public void setDate(int y, int m, int d) {
        day = d; // Set new day
        month = m; // Set new month
        year = d; // Set new year
    }
    // Method to get currently selected date from DatePicker
    public String getDate() {
        // Get year, month, and day from DatePicker
        day = selectedDate.get(Calendar.DAY_OF_MONTH);
        month = selectedDate.get(Calendar.MONTH);
        year = selectedDate.get(Calendar.YEAR);
        String date = year + "/" + (month + 1) + "/" + day; // Create string of complete date

        return date;
    }
    public void setNotification() {
        writeFile();
    }
    // Method to write to local file
    private void writeFile() {
        // Getting date to be saved
        String nameSave = notificationName.getText().toString().trim();
        String output = nameSave + " - " + getDate();
        try {
            FileOutputStream fos = openFileOutput(FILENAME, MODE_APPEND);
            String contentNewLine = output + "\n";
            fos.write(contentNewLine.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("File written!");
    }
    // Dark mode toggle method
    public void darkModeToggle() { // Dark mode / light mode toggle handler
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);
        if (mode != null) {
            if (mode.equals("dark")) {
                v.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                examTitle.setTextColor(getResources().getColor(R.color.lightBlue));
                examDesc.setTextColor(getResources().getColor(R.color.lightBlue));
                notificationName.setTextColor(getResources().getColor(R.color.white));
                saveDate.setTextColor(getResources().getColor(R.color.white));
                viewSaves.setTextColor(getResources().getColor(R.color.white));
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                examTitle.setTextColor(getResources().getColor(R.color.darkBlue));
                examDesc.setTextColor(getResources().getColor(R.color.darkBlue));
                notificationName.setTextColor(getResources().getColor(R.color.black));
                saveDate.setTextColor(getResources().getColor(R.color.black));
                viewSaves.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
    // Method to open exam saves page
    public void openExamSaves() {
        Intent intent = new Intent(ExamScheduler.this, ExamSaves.class);
        startActivity(intent);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saving the state to Bundle after converting to long
        long timeInMillis = selectedDate.getTimeInMillis();
        outState.putLong("selectedDate", timeInMillis);
        // Saving name input field to bundle
        outState.putString("name", notificationName.getText().toString().trim());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the selected date as a long
        long timeInMillis = savedInstanceState.getLong("selectedDate");
        // Converting long back to a Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        selectedDate = calendar;
        // Set the restored date to calendarView
        calendarView.setDate(selectedDate.getTimeInMillis());
        // Restoring name input field to bundle
        String name = savedInstanceState.getString("name");
        notificationName.setText(name);
    }
}
