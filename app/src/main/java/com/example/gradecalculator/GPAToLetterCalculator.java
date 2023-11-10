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

public class GPAToLetterCalculator extends AppCompatActivity {

    private EditText input;
    private Button calculate, toggleMode, save;
    private TextView output, titleText, descText;
    private boolean mode = true; // Toggle boolean to change GPA to letter / letter to GPA
    private final String FILENAME = "history.txt"; // Save file name
    private String currentDate, currentTime;
    private String content;
    private SharedPreferences sharedPreferences;
    private View v;
    private String hintState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpa_to_letter_calculator);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        input = findViewById(R.id.input); // EditText input field
        calculate = findViewById(R.id.toggle); // Calculate button
        output = findViewById(R.id.outputPercent); // TextView output to display letter grade
        toggleMode = findViewById(R.id.toggleMode); // Toggle button to change GPA to letter / letter to GPA
        titleText = findViewById(R.id.GPATitleText); // Top title text
        descText = findViewById(R.id.GPADescText); // Description text
        save = findViewById(R.id.saveLetterGPA); // Save button

        hintState = "4.00"; // Set default hint state

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);

        // InputFilter to restrict input to numbers and "." only by default
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        // Calculate button listener
        calculate.setOnClickListener(view -> { // Calculate button
            String inputText = input.getText().toString().trim();
            if (!inputText.isEmpty()) { // Only calculate if there is a value within the input field
                if (mode == true) {
                    CalculateGPA(); // Calculate GPA
                } else {
                    CalculateLetter(); // Calculate letter grade
                }
            }
        });
        // Toggle button listener
        toggleMode.setOnClickListener(view -> { // Toggle button
            if (mode == true) {
                toggleLetter(); // Call method to change to letter to GPA mode
                hintState = "A";
            } else {
                toggleGPA(); // Call method to change to GPA to letter mode
                hintState = "4.00";
            }
        });
        // Save button listener
        save.setOnClickListener(view -> {
            writeFile();
        });
    }
    // Method to toggle to GPA to Letter Grade mode
    public void toggleGPA() {
        mode = true; // Set mode for toggle next time button is pressed
        titleText.setText("GPA to Letter Grade"); // Change top title text
        input.setText(""); // Clear input field
        input.setHint("4.00"); // Change hint text
        input.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL); // Change to restrict input to numbers and "." only
        output.setText("Letter Grade:"); // Set text for output
    }
    // Method to toggle to Letter Grade to GPA mode
    public void toggleLetter() {
        mode = false; // Set mode for toggle next time button is pressed
        titleText.setText("Letter Grade to GPA"); // Change top title text
        input.setText(""); // Clear input field
        input.setHint("A"); // Change hint text
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); // Change to restrict to letters
        output.setText("GPA: "); // Set text for output
    }
    // Method to calculate a letter grade from a given GPA
    public void CalculateGPA() {
        double grade = Double.parseDouble(String.valueOf(input.getText()));
        System.out.println("Grade: " + grade);
        if (grade > 4.32 && grade < 4.34) { // A+
            output.setText("Letter Grade: A+");
        } else if (grade > 3.99 && grade < 4.33) { // A
            output.setText("Letter Grade: A");
        } else if (grade > 3.66 && grade < 4.01) { // A-
            output.setText("Letter Grade: A-");
        } else if (grade > 3.32 && grade < 3.68) { // B+
            output.setText("Letter Grade: B+");
        } else if (grade > 2.99 && grade < 3.34) { // B
            output.setText("Letter Grade: B");
        } else if (grade > 2.66 && grade < 3.01) { // B-
            output.setText("Letter Grade: B-");
        } else if (grade > 2.32 && grade < 2.68) { // C+
            output.setText("Letter Grade: C+");
        } else if (grade > 1.99 && grade < 2.34) { // C
            output.setText("Letter Grade: C");
        } else if (grade > 1.66 && grade < 2.01) { // C-
            output.setText("Letter Grade: C-");
        } else if (grade > 0.99 && grade < 1.68) { // D
            output.setText("Letter Grade: D");
        } else if (grade >= 0.00 && grade < 1.00) { // F
            output.setText("Letter Grade: F");
        } else {
            output.setText("Letter Grade: Invalid");
        }
    }
    // Method to calculate a GPA from a given letter grade
    public void CalculateLetter() {
        String inputLetter = String.valueOf(input.getText()).toUpperCase(); // Input letter grade value
        System.out.println("Input letter: " + inputLetter);
        switch(inputLetter) {
            case "A":
                output.setText("GPA: 4.00");
                break;
            case "A-":
                output.setText("GPA: 3.67");
                break;
            case "B+":
                output.setText("GPA: 3.33");
                break;
            case "B":
                output.setText("GPA: 3.00");
                break;
            case "B-":
                output.setText("GPA: 2.67");
                break;
            case "C+":
                output.setText("GPA: 2.34");
                break;
            case "C":
                output.setText("GPA: 2.00");
                break;
            case "C-":
                output.setText("GPA: 1.67");
                break;
            case "D+":
                output.setText("GPA: 1.33");
                break;
            case "D":
                output.setText("GPA: 1.00");
                break;
            case "D-":
                output.setText("GPA: 0.67");
                break;
            case "F":
                output.setText("GPA: 0.00");
                break;
            default:
                output.setText("GPA: Invalid");
        }
    }
    // Method to write to local file
    private void writeFile() {
        // Make sure output has text in it
        String outputField = output.getText().toString().trim();
        if (!outputField.equalsIgnoreCase("Letter Grade:")
                && !outputField.equalsIgnoreCase("GPA:")) {
            Toast.makeText(this, "Calculation saved local storage: History", Toast.LENGTH_SHORT).show();
            String outputContent = output.getText().toString();
            // Get current time stamp
            getDate(); // Year/Month/Day
            getTime(); // Time
            // Save total string with type of calculator - output - timestamp
            if (mode == true) { // Save with different titles depending on current mode
                content = "GPA to Letter - " + outputContent + " - " + currentDate + " " + currentTime;
            } else {
                content = "Letter to GPA - " + outputContent + " - " + currentDate + " " + currentTime;
            }
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
                output.setTextColor(getResources().getColor(R.color.lightBlue));
                input.setTextColor(getResources().getColor(R.color.white));
                save.setTextColor(getResources().getColor(R.color.white));
                toggleMode.setTextColor(getResources().getColor(R.color.white));
                calculate.setTextColor(getResources().getColor(R.color.white));
                calculate.setBackgroundColor(getResources().getColor(R.color.lightBlue));
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                titleText.setTextColor(getResources().getColor(R.color.darkBlue));
                descText.setTextColor(getResources().getColor(R.color.darkBlue));
                output.setTextColor(getResources().getColor(R.color.darkBlue));
                input.setTextColor(getResources().getColor(R.color.black));
                save.setTextColor(getResources().getColor(R.color.black));
                toggleMode.setTextColor(getResources().getColor(R.color.black));
                calculate.setTextColor(getResources().getColor(R.color.black));
                calculate.setBackgroundColor(getResources().getColor(R.color.buttonBlue));
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mode = true; // Reset mode
        darkModeToggle();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saving title to handle the changing title with toggle
        outState.putString("title", titleText.getText().toString().trim());
        // Saving input field to bundle
        outState.putString("input", input.getText().toString().trim());
        // Saving input field hint state
        outState.putString("inputHint", hintState);
        // Saving output field to bundle
        outState.putString("output", output.getText().toString().trim());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restoring title text
        String inTitle = savedInstanceState.getString("title");
        titleText.setText(inTitle);
        // Restoring input field
        String inField = savedInstanceState.getString("input");
        input.setText(inField);
        // Restoring input field hint state
        String hint = savedInstanceState.getString("inputHint");
        input.setHint(hint);
        // Restoring output field
        String inOutput = savedInstanceState.getString("output");
        output.setText(inOutput);
    }
}
