package com.example.gradecalculator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

    private TextView welcome;
    String[] menuItems = {"Grade Calculators", "Exam Scheduler",
            "History", "Settings", "About the App"}; // Array of menu items
    private SharedPreferences sharedPreferences;
    private View v;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private String bodyText;
    ExamReceiver receiver = new ExamReceiver();
    private IntentFilter filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        registerReceiver(receiver, filter); // Registering broadcast receiver
        //receiver.sendNotification(ExamScheduler.this, "Test", "Test"); // For debugging

        bodyText = "Content provided is for informational purposes only. " +
                "It may not represent professional advice. Users should verify information " +
                "and consult a professional for specific advice or actions.";
        // Display disclaimer if app is being launched for the first time
        if (isFirstTime()) {
            displayAlertDialog(); // Display dialog disclaimer
            setFirstTimeTrue(false); // Set first time SharedPreferences so dialog does not pop up again
        }

        listView = (ListView)findViewById(R.id.menuListView);
        welcome = findViewById(R.id.welcome);

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);
        // Default ArrayAdapter with custom_light
        adapter = new ArrayAdapter<>(MainMenu.this, R.layout.custom_light, R.id.textView, menuItems);

        // Check for dark or light mode
        darkModeToggle();

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedItem = menuItems[pos];
                if (selectedItem.equals("Grade Calculators")) {
                    openGradeCalcMenu(); // Opens grade calculator menu
                } else if (selectedItem.equals("Exam Scheduler")) {
                    openExamScheduler();
                } else if (selectedItem.equals("History")) {
                    openHistory(); // Opens history activity
                } else if (selectedItem.equals("Settings")) {
                    openSettings(); // Opens settings activity
                } else if (selectedItem.equals("About the App")) {
                    openAbout(); // Opens about page
                }
            }
        });
    }
    public void openGradeCalcMenu() {
        Intent intent = new Intent(MainMenu.this, GradeCalculatorMenu.class);
        startActivity(intent);
    }
    public void openExamScheduler() {
        Intent intent = new Intent(MainMenu.this, ExamScheduler.class);
        startActivity(intent);
    }
    public void openHistory() {
        Intent intent = new Intent(MainMenu.this, History.class);
        startActivity(intent);
    }
    public void openSettings() {
        Intent intent = new Intent(MainMenu.this, Settings.class);
        startActivity(intent);
    }
    public void openAbout() {
        Intent intent = new Intent(MainMenu.this, About.class);
        startActivity(intent);
    }
    // Dark mode toggle method
    public void darkModeToggle() { // Dark mode / light mode toggle handler
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);
        if (mode != null) {
            if (mode.equals("dark")) {
                v.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                welcome.setTextColor(getResources().getColor(R.color.lightBlue));
                // Change ArrayAdapter to custom_dark
                adapter = new ArrayAdapter<>(MainMenu.this, R.layout.custom_dark, R.id.textView, menuItems);
                listView.setAdapter(adapter);
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                welcome.setTextColor(getResources().getColor(R.color.darkBlue));
                // Change ArrayAdapter to custom_light
                adapter = new ArrayAdapter<>(MainMenu.this, R.layout.custom_light, R.id.textView, menuItems);
                listView.setAdapter(adapter);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
    // Method to display disclaimer text
    private void displayAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("! Disclaimer !"); // Disclaimer title
        alertDialogBuilder.setMessage(bodyText); // Disclaimer body text
        // Button to dismiss dialog
        alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss(); // Remove dialog when ok is pressed
        });
        // Display the dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    // Methods to check for first time launch for disclaimer popup
    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences("disclaimer_popup", MODE_PRIVATE);
        return preferences.getBoolean("first_time", true);
    }
    private void setFirstTimeTrue(boolean isFirstTime) {
        SharedPreferences preferences = getSharedPreferences("disclaimer_popup", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("first_time", isFirstTime);
        editor.apply();
    }
}
