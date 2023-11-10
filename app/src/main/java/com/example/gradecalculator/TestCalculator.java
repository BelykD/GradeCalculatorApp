package com.example.gradecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestCalculator extends AppCompatActivity {

    private TextView percentOut, fractionOut, letterOut, titleText,
            descText, numQuestionText, numWrongText, outputPercent, outputFraction, outputLetter;
    private EditText numTotal, numWrong;
    private Button calculate, save;
    private double total, wrong, difference, percent;
    private final String FILENAME = "history.txt"; // Save file name
    private String currentDate, currentTime;
    private SharedPreferences sharedPreferences;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_calculator);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Text
        titleText = findViewById(R.id.bugReportTitle);
        descText = findViewById(R.id.testDescText);
        numQuestionText = findViewById(R.id.numQuestionText);
        numWrongText = findViewById(R.id.numWrongText);
        outputPercent = findViewById(R.id.outputPercent);
        outputFraction = findViewById(R.id.outputFraction);
        outputLetter = findViewById(R.id.outputLetter);
        // Outputs
        percentOut = findViewById(R.id.outputPercent);
        fractionOut = findViewById(R.id.outputFraction);
        letterOut = findViewById(R.id.outputLetter);
        // Inputs
        numTotal = findViewById(R.id.weight4);
        numWrong = findViewById(R.id.inputWrong);
        // Calculate and save buttons
        calculate = findViewById(R.id.calculate);
        save = findViewById(R.id.saveGradeTest);
        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);
        // Restrict wrong answer input to numbers only
        numTotal.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        numWrong.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        calculate.setOnClickListener(view -> {
            calculatePercentage(); // Calculate percentage
            calculateFraction(); // Calculate fraction
            calculateLetter(); // Calculate letter grade
        });
        save.setOnClickListener(view -> {
            writeFile();
            Toast.makeText(this, "Calculation saved local storage: History", Toast.LENGTH_SHORT).show();
        });
    }
    // Method to calculate percentage of test - Wrong answers / total questions * 100 = percentage
    public void calculatePercentage() {
        getValues();
        if (total < wrong) {
            percentOut.setText("Percentage: Invalid");
        } else {
            difference = total - wrong; // Remove wrong from total to get the difference
            percent = (difference / total) * 100;
            String oneDecimal = String.format("%.1f", percent); // One decimal places
            double roundedPercent = Double.parseDouble(oneDecimal); // Round percentage
            percentOut.setText("Percentage: " + roundedPercent + "%"); // Output percentage answer
        }
    }
    // Method to calculate fraction of test
    public void calculateFraction() {
        getValues();
        if (total < wrong) {
            fractionOut.setText("Fraction: Invalid");
        } else {
            difference = total - wrong; // Remove wrong from total to get the difference
            fractionOut.setText("Fraction: " + difference + "/" + total); // Output fraction answer
        }
    }
    // Method to calculate letter grade of test
    public void calculateLetter() {
        getValues();
        if (total < wrong) {
            letterOut.setText("Letter: Invalid");
        } else {
            difference = total - wrong; // Remove wrong from total to get the difference
            // Get percentage first then get related letter grade from it
            percent = (difference / total) * 100;
            String oneDecimal = String.format("%.0f", percent); // 0 decimal places
            double roundedPercent = Double.parseDouble(oneDecimal); // Round percentage
            // Get letter grade
            if (roundedPercent < 101 && roundedPercent > 89) { // A+
                letterOut.setText("Letter: A+");
            } else if (roundedPercent < 90 && roundedPercent > 84) { // A
                letterOut.setText("Letter: A");
            } else if (roundedPercent < 85 && roundedPercent > 79) { // A-
                letterOut.setText("Letter: A-");
            } else if (roundedPercent < 80 && roundedPercent > 76) { // B+
                letterOut.setText("Letter: B+");
            } else if (roundedPercent < 77 && roundedPercent > 72) { // B
                letterOut.setText("Letter: B");
            } else if (roundedPercent < 73 && roundedPercent > 69) { // B-
                letterOut.setText("Letter: B-");
            } else if (roundedPercent < 70 && roundedPercent > 64) { // C+
                letterOut.setText("Letter: C+");
            } else if (roundedPercent < 65 && roundedPercent > 59) { // C
                letterOut.setText("Letter: C");
            } else if (roundedPercent < 60 && roundedPercent > 54) { // C-
                letterOut.setText("Letter: C-");
            } else if (roundedPercent < 55 && roundedPercent > 49) { // D
                letterOut.setText("Letter: D");
            } else if (roundedPercent < 50 && roundedPercent >= 0) { // F
                letterOut.setText("Letter: F");
            } else {
                letterOut.setText("Letter: Invalid");
            }
        }
    }
    //
    public void getValues() {
        total = Double.parseDouble(String.valueOf(numTotal.getText())); // Get number of questions
        wrong = Double.parseDouble(String.valueOf(numWrong.getText())); // Get number of wrong questions
    }
    // Method to write to local file
    private void writeFile() {
        // Make sure GPA output has text in it
        String outputPercent = percentOut.getText().toString().trim(); // Get string for percentage
        String outputFraction = fractionOut.getText().toString().trim(); // Get string for fraction
        String outputLetter = letterOut.getText().toString().trim(); // Get string for letter grade
        if (!outputPercent.equalsIgnoreCase("Percentage:")) {
            Toast.makeText(this, "Calculation saved local storage: History", Toast.LENGTH_SHORT).show();
            // Creating a string to combine all 3 outputs
            String outputContent = outputPercent + " - " + outputFraction + " - " + outputLetter;
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
                titleText.setTextColor(getResources().getColor(R.color.lightBlue));
                descText.setTextColor(getResources().getColor(R.color.lightBlue));
                numQuestionText.setTextColor(getResources().getColor(R.color.lightBlue));
                numWrongText.setTextColor(getResources().getColor(R.color.lightBlue));
                numWrong.setTextColor(getResources().getColor(R.color.white));
                numTotal.setTextColor(getResources().getColor(R.color.white));
                outputPercent.setTextColor(getResources().getColor(R.color.lightBlue));
                outputFraction.setTextColor(getResources().getColor(R.color.lightBlue));
                outputLetter.setTextColor(getResources().getColor(R.color.lightBlue));
                save.setTextColor(getResources().getColor(R.color.white));
                calculate.setTextColor(getResources().getColor(R.color.white));
                calculate.setBackgroundColor(getResources().getColor(R.color.lightBlue));
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                titleText.setTextColor(getResources().getColor(R.color.darkBlue));
                descText.setTextColor(getResources().getColor(R.color.darkBlue));
                numQuestionText.setTextColor(getResources().getColor(R.color.darkBlue));
                numWrongText.setTextColor(getResources().getColor(R.color.darkBlue));
                numWrong.setTextColor(getResources().getColor(R.color.black));
                numTotal.setTextColor(getResources().getColor(R.color.black));
                outputPercent.setTextColor(getResources().getColor(R.color.darkBlue));
                outputFraction.setTextColor(getResources().getColor(R.color.darkBlue));
                outputLetter.setTextColor(getResources().getColor(R.color.darkBlue));
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
        outState.putString("numQuestionText", numQuestionText.getText().toString().trim());
        outState.putString("numWrongText", numWrongText.getText().toString().trim());
        // Saving output field to bundle
        outState.putString("percent", percentOut.getText().toString().trim());
        outState.putString("fraction", fractionOut.getText().toString().trim());
        outState.putString("letter", letterOut.getText().toString().trim());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restoring input field
        String inQuestion = savedInstanceState.getString("numQuestionText");
        String inQuestionWrong = savedInstanceState.getString("numWrongText");
        numQuestionText.setText(inQuestion);
        numWrongText.setText(inQuestionWrong);
        // Restoring output field
        String inPercent = savedInstanceState.getString("percent");
        String inFraction = savedInstanceState.getString("fraction");
        String inLetter = savedInstanceState.getString("letter");
        percentOut.setText(inPercent);
        fractionOut.setText(inFraction);
        letterOut.setText(inLetter);
    }
}
