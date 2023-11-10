package com.example.gradecalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class History extends AppCompatActivity {

    private Button clear;
    private ListView historyListView;
    private final String FILENAME = "history.txt"; // Save file name
    private ArrayList<String> historyList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private TextView historyTitle, historyDesc;
    private SharedPreferences sharedPreferences;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        historyListView = (ListView)findViewById(R.id.historyListView);
        historyTitle = findViewById(R.id.historyTitle);
        historyDesc = findViewById(R.id.historyDesc);
        clear = findViewById(R.id.clear);

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);
        // Default ArrayAdapter with custom_light
        adapter = new ArrayAdapter<>(History.this, R.layout.custom_light, R.id.textView, historyList);

        // Check for dark or light mode
        darkModeToggle();

        historyListView.setAdapter(adapter);

        readFile(); // Restore the history from local file

        // Button to clear all local storage saves
        clear.setOnClickListener(view -> {
            clearFile(); // Clear local file
            historyList.clear(); // Clear list of history items
            adapter.notifyDataSetChanged(); // Update adapter
            Toast.makeText(this, "History successfully cleared", Toast.LENGTH_SHORT).show();
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
                    historyList.add(sLine); // Add saved history items to list
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
                historyTitle.setTextColor(getResources().getColor(R.color.lightBlue));
                historyDesc.setTextColor(getResources().getColor(R.color.lightBlue));
                clear.setTextColor(getResources().getColor(R.color.white));
                clear.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                // Change ArrayAdapter to custom_dark
                adapter = new ArrayAdapter<>(History.this, R.layout.custom_dark_history, R.id.textView, historyList);
                historyListView.setAdapter(adapter);
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                historyTitle.setTextColor(getResources().getColor(R.color.darkBlue));
                historyDesc.setTextColor(getResources().getColor(R.color.darkBlue));
                clear.setTextColor(getResources().getColor(R.color.black));
                clear.setBackgroundColor(getResources().getColor(R.color.buttonBlue));
                // Change ArrayAdapter to custom_light
                adapter = new ArrayAdapter<>(History.this, R.layout.custom_light_history, R.id.textView, historyList);
                historyListView.setAdapter(adapter);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
}
