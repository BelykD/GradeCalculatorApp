package com.example.gradecalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ExamSaves extends AppCompatActivity {

    private Button back, clear;
    private TextView examSavesTitle, examSavesDesc;
    private final String FILENAME = "exam-notifications.txt"; // Save file name
    private ListView examListView;
    private ArrayList<String> examList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_saves);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        examListView = (ListView)findViewById(R.id.examListView);

        examSavesTitle = findViewById(R.id.examSavesTitle); // TextViews
        examSavesDesc = findViewById(R.id.examSavesDesc);
        back = findViewById(R.id.backExam); // Buttons
        clear = findViewById(R.id.clearNotifications);

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);
        // Default ArrayAdapter with custom_light
        adapter = new ArrayAdapter<>(ExamSaves.this, R.layout.custom_light, R.id.textView, examList);

        // Check for dark or light mode
        darkModeToggle();

        readFile(); // Restore the history from local file

        examListView.setAdapter(adapter);

        back.setOnClickListener(view -> {
            openExamScheduler();
        });
        clear.setOnClickListener(view -> {
            clearFile(); // Clear local file
            examList.clear(); // Clear list of exam notification items
            adapter.notifyDataSetChanged(); // Update adapter
        });
    }
    // Method to clear local file of saved grades
    private void clearFile() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE);
            fos.close();
            System.out.println("File cleared!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method to read local file for saved history
    public void readFile() {
        if (checkIfFileExists(FILENAME)) {
            // Read contents of file and update
            FileInputStream fis = null;
            try {
                fis = openFileInput(FILENAME);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String sLine = null;
            try {
                while ((sLine = br.readLine()) != null) {
                    examList.add(sLine); // Add saved history items to list
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File read!");
        } else {
            System.out.println("File doesn't exist!");
        }
    }
    public boolean checkIfFileExists(String fileName) {
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }
    // Dark mode toggle method
    public void darkModeToggle() { // Dark mode / light mode toggle handler
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);
        if (mode != null) {
            if (mode.equals("dark")) {
                v.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                examSavesTitle.setTextColor(getResources().getColor(R.color.lightBlue));
                examSavesDesc.setTextColor(getResources().getColor(R.color.lightBlue));
                back.setTextColor(getResources().getColor(R.color.white));
                clear.setTextColor(getResources().getColor(R.color.white));
                // Change ArrayAdapter to custom_dark
                adapter = new ArrayAdapter<>(ExamSaves.this, R.layout.custom_dark_history, R.id.textView, examList);
                examListView.setAdapter(adapter);
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                examSavesTitle.setTextColor(getResources().getColor(R.color.darkBlue));
                examSavesDesc.setTextColor(getResources().getColor(R.color.darkBlue));
                back.setTextColor(getResources().getColor(R.color.black));
                clear.setTextColor(getResources().getColor(R.color.black));
                // Change ArrayAdapter to custom_light
                adapter = new ArrayAdapter<>(ExamSaves.this, R.layout.custom_light_history, R.id.textView, examList);
                examListView.setAdapter(adapter);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
    // Method to open exam scheduler page
    public void openExamScheduler() {
        Intent intent = new Intent(ExamSaves.this, ExamScheduler.class);
        startActivity(intent);
    }
}
