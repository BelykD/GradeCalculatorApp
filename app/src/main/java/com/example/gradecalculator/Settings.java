package com.example.gradecalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Settings extends AppCompatActivity
{

    private Button darkMode, lightMode, clearCache, submitBug;
    private SharedPreferences sharedPreferences;
    private TextView settingsTitle;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        settingsTitle = findViewById(R.id.SettingsTitleText);
        darkMode = findViewById(R.id.darkMode);
        lightMode = findViewById(R.id.lightMode);
        clearCache = findViewById(R.id.clearCache);
        submitBug = findViewById(R.id.submitBug);

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);

        // SharedPreferences for settings dark mode / light mode
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);

        // Dark mode toggle button
        darkMode.setOnClickListener(view -> {
            // Setting SharedPreferences to dark mode
            sharedPreferences = getSharedPreferences("dark_mode", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("mode", "dark");
            editor.apply();
            darkModeToggle(); // Toggle mode on button click
            Toast.makeText(this, "Dark mode enabled", Toast.LENGTH_SHORT).show();
        });
        // Light mode toggle button
        lightMode.setOnClickListener(view -> {
            // Setting SharedPreferences to light mode
            sharedPreferences = getSharedPreferences("dark_mode", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("mode", "light");
            editor.apply();
            darkModeToggle(); // Toggle mode on button click
            Toast.makeText(this, "Dark mode disabled", Toast.LENGTH_SHORT).show();
        });
        // Clear cache button
        clearCache.setOnClickListener(view -> {
            clearAllCaches("history.txt");
            clearAllCaches("exam-notifications.txt");
            // SharedPreferences to reset main menu disclaimer
            SharedPreferences preferences = getSharedPreferences("disclaimer_popup", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("first_time", true);
            editor.apply();
            Toast.makeText(this, "Caches successfully cleared", Toast.LENGTH_SHORT).show();
        });
        // Bug report button
        submitBug.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, SendBugReport.class);
            startActivity(intent);
        });
    }
    // Method takes in fileName and clears data associated with said file
    public void clearAllCaches(String fileName) {
        // Method to clear local file of saved grades
        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.close();
            System.out.println("File cleared!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Dark mode toggle method
    public void darkModeToggle() { // Dark mode / light mode toggle handler
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);
        if (mode != null) {
            if (mode.equals("dark")) {
                v.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                settingsTitle.setTextColor(getResources().getColor(R.color.lightBlue));

            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                settingsTitle.setTextColor(getResources().getColor(R.color.darkBlue));
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
}
