package com.example.gradecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;

public class PercentToGPACalculator extends AppCompatActivity {

    private EditText input;
    private Button calculate, save;
    private TextView output, percentTitle, percentDesc;
    private final String FILENAME = "history.txt"; // Save file name
    private String currentDate, currentTime;
    private SharedPreferences sharedPreferences;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.percent_to_gpa_calculator);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        input = findViewById(R.id.inputPercent); // Percentage input
        calculate = findViewById(R.id.toggle); // Calculate button
        save = findViewById(R.id.saveGradePercent); // Save button
        output = findViewById(R.id.outputPercent); // TextView output to display GPA
        percentTitle = findViewById(R.id.percentTitle);
        percentDesc = findViewById(R.id.percentDesc);

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);

        // InputFilter to restrict input to numbers only by default
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        calculate.setOnClickListener(view -> {
            String inputText = input.getText().toString().trim();
            if (!inputText.isEmpty()) { // Only calculate if there is a value within the input field
                calculateGPA();
            }
        });
        save.setOnClickListener(view -> {
            writeFile();
            Toast.makeText(this, "Calculation saved local storage: History", Toast.LENGTH_SHORT).show();
        });
    }
    public void calculateGPA() {
        int percentage = Integer.parseInt(String.valueOf(input.getText()));
        System.out.println("Percentage: " + percentage );
        if (percentage < 101 && percentage > 89) { // A+
            output.setText("GPA: 4.33");
        } else if (percentage < 90 && percentage > 84) { // A
            output.setText("GPA: 4.00");
        } else if (percentage < 85 && percentage > 79) { // A-
            output.setText("GPA: 3.67");
        } else if (percentage < 80 && percentage > 76) { // B+
            output.setText("GPA: 3.33");
        } else if (percentage < 77 && percentage > 72) { // B
            output.setText("GPA: 3.00");
        } else if (percentage < 73 && percentage > 69) { // B-
            output.setText("GPA: 2.67");
        } else if (percentage < 70 && percentage > 64) { // C+
            output.setText("GPA: 2.33");
        } else if (percentage < 65 && percentage > 59) { // C
            output.setText("GPA: 2.00");
        } else if (percentage < 60 && percentage > 54) { // C-
            output.setText("GPA: 1.67");
        } else if (percentage < 55 && percentage > 49) { // D
            output.setText("GPA: 1.00");
        } else if (percentage < 50 && percentage >= 0) { // F
            output.setText("GPA: 0.00");
        } else {
            output.setText("GPA: Invalid");
        }
    }
    // Method to write to local file
    private void writeFile() {
        // Make sure GPA output has text in it
        String outputField = output.getText().toString().trim();
        if (!outputField.equalsIgnoreCase("GPA:")) {
            Toast.makeText(this, "Calculation saved local storage: History", Toast.LENGTH_SHORT).show();
            String outputContent = output.getText().toString();
            // Get current time stamp
            getDate(); // Year/Month/Day
            getTime(); // Time
            // Save total string with type of calculator - output - timestamp
            String content = "Percent to GPA - " + outputContent + " - " + currentDate + " " + currentTime;
            try {
                FileOutputStream fos = openFileOutput(FILENAME, MODE_APPEND);
                String contentNewLine = content + "\n";
                fos.write(contentNewLine.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File written!");
        }
    }
    // Method to get current date
    public void getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = dateFormat.format(calendar.getTime());
    }
    // Method to get current time
    public void getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); // You can choose your desired time format
        currentTime = timeFormat.format(calendar.getTime());
    }
    // Dark mode toggle method
    public void darkModeToggle() { // Dark mode / light mode toggle handler
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);
        if (mode != null) {
            if (mode.equals("dark")) {
                v.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                percentTitle.setTextColor(getResources().getColor(R.color.lightBlue));
                percentDesc.setTextColor(getResources().getColor(R.color.lightBlue));
                output.setTextColor(getResources().getColor(R.color.lightBlue));
                input.setTextColor(getResources().getColor(R.color.white));
                save.setTextColor(getResources().getColor(R.color.white));
                calculate.setTextColor(getResources().getColor(R.color.white));
                calculate.setBackgroundColor(getResources().getColor(R.color.lightBlue));
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                percentTitle.setTextColor(getResources().getColor(R.color.darkBlue));
                percentDesc.setTextColor(getResources().getColor(R.color.darkBlue));
                output.setTextColor(getResources().getColor(R.color.darkBlue));
                input.setTextColor(getResources().getColor(R.color.black));
                save.setTextColor(getResources().getColor(R.color.black));
                calculate.setTextColor(getResources().getColor(R.color.black));
                calculate.setBackgroundColor(getResources().getColor(R.color.buttonBlue));
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saving input field to bundle
        outState.putString("input", input.getText().toString().trim());
        // Saving output field to bundle
        outState.putString("output", output.getText().toString().trim());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restoring input field
        String inField = savedInstanceState.getString("input");
        input.setText(inField);
        // Restoring output field
        String inOutput = savedInstanceState.getString("output");
        output.setText(inOutput);
    }
}
