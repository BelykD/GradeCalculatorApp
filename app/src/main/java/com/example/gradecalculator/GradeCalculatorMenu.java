package com.example.gradecalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GradeCalculatorMenu extends AppCompatActivity {

    private TextView menuTitle;
    String[] menuItems = {"Grade Average", "Test Score",
            "Percent to GPA", "GPA to Letter"}; // Array of menu items
    private SharedPreferences sharedPreferences;
    private View v;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grade_calculator_menu);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        listView = (ListView)findViewById(R.id.gradeCalcListView);
        menuTitle = findViewById(R.id.menuTitle);

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);
        // Default ArrayAdapter with custom_light
        adapter = new ArrayAdapter<>(GradeCalculatorMenu.this, R.layout.custom_light, R.id.textView, menuItems);

        // Check for dark or light mode
        darkModeToggle();

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedItem = menuItems[pos];
                if (selectedItem.equals("Grade Average")) {
                    openGradeCalc(); // Opens grade calculator menu
                } else if (selectedItem.equals("Test Score")) {
                    openTestCalc(); // Opens history activity
                } else if (selectedItem.equals("Percent to GPA")) {
                    openGradeToGPA(); // Opens settings activity
                } else if (selectedItem.equals("GPA to Letter")) {
                    openGPAToLetter(); // Opens GPA to letter calculator page
                }
            }
        });
    }
    public void openGradeCalc() {
        Intent intent = new Intent(GradeCalculatorMenu.this, GradeCalculator.class);
        startActivity(intent);
    }
    public void openTestCalc() {
        Intent intent = new Intent(GradeCalculatorMenu.this, TestCalculator.class);
        startActivity(intent);
    }
    public void openGradeToGPA() {
        Intent intent = new Intent(GradeCalculatorMenu.this, PercentToGPACalculator.class);
        startActivity(intent);
    }
    public void openGPAToLetter() {
        Intent intent = new Intent(GradeCalculatorMenu.this, GPAToLetterCalculator.class);
        startActivity(intent);
    }
    // Dark mode toggle method
    public void darkModeToggle() { // Dark mode / light mode toggle handler
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);
        if (mode != null) {
            if (mode.equals("dark")) {
                v.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                menuTitle.setTextColor(getResources().getColor(R.color.lightBlue));
                // Change ArrayAdapter to custom_dark
                adapter = new ArrayAdapter<>(GradeCalculatorMenu.this, R.layout.custom_dark, R.id.textView, menuItems);
                listView.setAdapter(adapter);
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                menuTitle.setTextColor(getResources().getColor(R.color.darkBlue));
                // Change ArrayAdapter to custom_light
                adapter = new ArrayAdapter<>(GradeCalculatorMenu.this, R.layout.custom_light, R.id.textView, menuItems);
                listView.setAdapter(adapter);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
}
