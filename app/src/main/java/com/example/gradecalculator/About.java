package com.example.gradecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {

    private TextView aboutTitle, aboutDesc;
    private ImageView arrowIcon;
    private SharedPreferences sharedPreferences;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        aboutTitle = findViewById(R.id.AboutTitleText);
        aboutDesc = findViewById(R.id.aboutDesc);
        arrowIcon = findViewById(R.id.arrowIcon);

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);

        // Check for dark or light mode
        darkModeToggle();
    }
    // Dark mode toggle method
    public void darkModeToggle() { // Dark mode / light mode toggle handler
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);
        if (mode != null) {
            if (mode.equals("dark")) {
                v.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                aboutTitle.setTextColor(getResources().getColor(R.color.lightBlue));
                aboutDesc.setTextColor(getResources().getColor(R.color.white));
                arrowIcon.setImageResource(R.drawable.arrow_light);
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                aboutTitle.setTextColor(getResources().getColor(R.color.darkBlue));
                aboutDesc.setTextColor(getResources().getColor(R.color.black));
                arrowIcon.setImageResource(R.drawable.arrow_dark);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
}
