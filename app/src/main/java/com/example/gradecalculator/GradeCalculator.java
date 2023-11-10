package com.example.gradecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GradeCalculator extends AppCompatActivity {

    private Button calculate, addRow, save;
    private LinearLayout container;
    private TextView gradeOutput, gradeCalcTitle, gradeCalcDesc, weightText, gradeText;
    private EditText weight1, weight2, weight3, weight4;
    private EditText grade1, grade2, grade3, grade4;
    private double totalWeight, gradeOut;
    private boolean extraRow;
    private SharedPreferences sharedPreferences;
    private View v;
    private int marginPixels;
    private int rowCounter = 0; // Unique identifier for each row added
    private final String FILENAME = "history.txt"; // Save file name
    private String currentDate, currentTime;
    // Lists to hold new row items
    List<EditText> weightEditTextList = new ArrayList<>();
    List<EditText> gradeEditTextList = new ArrayList<>();
    List<Double> rowResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grade_calculator);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        container = findViewById(R.id.container); // LinearLayout container to add elements to for "Add Row" button
        gradeOutput = findViewById(R.id.outputGrade);
        calculate = findViewById(R.id.calculate); // Calculate button
        addRow = findViewById(R.id.addRow); // Add row button
        save = findViewById(R.id.saveGrade);
        gradeCalcTitle = findViewById(R.id.bugReportTitle);
        gradeCalcDesc = findViewById(R.id.gradeCalcDesc);
        weightText = findViewById(R.id.weightText);
        gradeText = findViewById(R.id.gradeText);
        // Initializing each EditText for weights and grades
        weight1 = findViewById(R.id.weight1);
        weight2 = findViewById(R.id.weight2);
        weight3 = findViewById(R.id.weight3);
        weight4 = findViewById(R.id.weight4);
        grade1 = findViewById(R.id.grade1);
        grade2 = findViewById(R.id.grade2);
        grade3 = findViewById(R.id.grade3);
        grade4 = findViewById(R.id.grade4);
        // Setting to only accept numbers and "."
        weight1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weight2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weight3.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weight4.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        grade1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        grade2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        grade3.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        grade4.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        // Convert dp to pixels for layout margins
        marginPixels = (int) (20 * getResources().getDisplayMetrics().density + 0.5f);

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);

        extraRow = false; // Start as false until extra row button is pressed

        // Calculate button click listener
        calculate.setOnClickListener(view -> {
            getGradeTotal(); // Calling calculation logic method
        });
        // Add row button click listener
        addRow.setOnClickListener(view -> {
            addRowElements(container);
            extraRow = true; // Set to true to enable calculations for more than just the initial 4 rows
            // Check for dark mode when adding a new row
            sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
            String mode = sharedPreferences.getString("mode", null);
            if (mode.equals("dark")) {
                enableDarkModeExtraRows();
            } else if (mode.equals("light")) {
                disableDarkModeExtraRows();
            }
        });
        // Save button click listener
        save.setOnClickListener(view -> {
            writeFile();
        });
    }
    // Method to add rows
    public void addRowElements(LinearLayout container) {
        Toast.makeText(getBaseContext(), "New row added!", Toast.LENGTH_SHORT).show();

        LinearLayout newContainer = new LinearLayout(this); // New LinearLayout to contain new row
        newContainer.setOrientation(LinearLayout.HORIZONTAL); // Set orientation for new LinearLayout
        newContainer.setId(rowCounter++);

        EditText newWeight = new EditText(this); // New EditText for weight column
        EditText newGrade = new EditText(this); // New EditText for grade column
        // Defining parameters for new EditText elements
        LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout.LayoutParams layoutParameters = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParameters.setMargins(marginPixels, 0, marginPixels, 0);
        parameters.weight = 1; // Set weight to match other elements within the container
        newContainer.setLayoutParams(layoutParameters); // Applying new parameters to new LinearLayout
        newWeight.setLayoutParams(parameters); // Applying new parameters to both EditTexts
        newGrade.setLayoutParams(parameters);

        newWeight.setHint("10"); // Set hint
        newWeight.setTextSize(18); // Set font size
        newWeight.setTypeface(ResourcesCompat.getFont(this, R.font.designer)); // Set font
        newWeight.setGravity(Gravity.CENTER);
        newWeight.setId(View.generateViewId()); // Generate unique ID reference
        weightEditTextList.add(newWeight); // Add to reference list

        newGrade.setHint("85"); // Set hint
        newGrade.setTextSize(18); // Set font size
        newGrade.setTypeface(ResourcesCompat.getFont(this, R.font.designer)); // Set font
        newGrade.setGravity(Gravity.CENTER);
        newGrade.setId(View.generateViewId()); // Generate unique ID reference
        gradeEditTextList.add(newGrade); // Add to reference list

        container.addView(newContainer); // Adding new LinearLayout to container
        newContainer.addView(newWeight); // Adding new elements to container
        newContainer.addView(newGrade);
        System.out.println(gradeEditTextList.get(0).getText());
        System.out.println(gradeEditTextList.toString());
    }
    // Method to output the grade total and check for extra rows added by user
    public void getGradeTotal() {
        if (extraRow == false) {
            calculateGrade(); // If there is no extra rows - calculate the initial 4 rows
            gradeOutput.setText("Grade: " + gradeOut + "%"); // Outputting grade average to display
        } else { // Calculate extra rows
            // Getting contents of weights
            double weight1In = Double.parseDouble(String.valueOf(weight1.getText()));
            double weight2In = Double.parseDouble(String.valueOf(weight2.getText()));
            double weight3In = Double.parseDouble(String.valueOf(weight3.getText()));
            double weight4In = Double.parseDouble(String.valueOf(weight4.getText()));
            // Double of all weights combined
            double weightShow = getExtraRowWeight() + weight1In + weight2In + weight3In + weight4In;
            // Check if total weights = 100
            if (getExtraRowWeight() + weight1In + weight2In + weight3In + weight4In == 100) {
                if (calculateExtraRow() != 101.0) {
                    double finalGrade = calculateExtraRow() + calculateGrade(); // Add initial rows and added rows together
                    gradeOutput.setText("Grade: " + finalGrade + "%");
                    System.out.println("Initial Rows: " + calculateGrade() + " Extra Rows: " + calculateExtraRow());
                    System.out.println("Weight: " + weightShow); // For debugging
                } else {
                    Toast.makeText(this, "Invalid entries", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Weights do not equal 100", Toast.LENGTH_SHORT).show();
                System.out.println("Weight: " + weightShow); // For debugging
            }
        }
    }
    public double calculateGrade() {
        if (weight1.getText().toString().trim().isEmpty() || weight2.getText().toString().trim().isEmpty() ||
                weight3.getText().toString().trim().isEmpty() || weight4.getText().toString().trim().isEmpty() ||
                grade1.getText().toString().trim().isEmpty() || grade2.getText().toString().trim().isEmpty() ||
                grade3.getText().toString().trim().isEmpty() || grade4.getText().toString().trim().isEmpty())
        {
            Toast.makeText(getBaseContext(), "Missing Inputs", Toast.LENGTH_SHORT).show();
        } else {
            // Getting contents of weights
            double weight1In = Double.parseDouble(String.valueOf(weight1.getText()));
            double weight2In = Double.parseDouble(String.valueOf(weight2.getText()));
            double weight3In = Double.parseDouble(String.valueOf(weight3.getText()));
            double weight4In = Double.parseDouble(String.valueOf(weight4.getText()));
            // Getting contents of grades
            double grade1In = Double.parseDouble(String.valueOf(grade1.getText()));
            double grade2In = Double.parseDouble(String.valueOf(grade2.getText()));
            double grade3In = Double.parseDouble(String.valueOf(grade3.getText()));
            double grade4In = Double.parseDouble(String.valueOf(grade4.getText()));
            // Get total weighted grade
            gradeOut = (grade1In * (weight1In / 100)) + (grade2In * (weight2In / 100))
                    + (grade3In * (weight3In / 100)) + (grade4In * (weight4In / 100));
        }
        return gradeOut; // Return grade total
    }
    // Method to calculate rows past 4
    public double calculateExtraRow() {
        rowResults.clear(); // Clear rowResults
        double totalResult = 0.0; // Reset totalResult
        for (int i = 0; i < weightEditTextList.toArray().length; i++) {
            System.out.println(weightEditTextList.get(0));
            // Get elements from arrays of row elements
            String grade = gradeEditTextList.get(i).getText().toString().trim();
            String weight = weightEditTextList.get(i).getText().toString().trim();
            if (!grade.equals("") && !weight.equals("")) {
                double gradeDouble = Double.parseDouble(grade);
                double weightDouble = Double.parseDouble(weight);
                // Calculate weighted grade
                double result = gradeDouble * (weightDouble / 100);
                totalResult += result; // add to grade total for return value
                rowResults.add(result); // Add to list of results for debugging
                System.out.println(rowResults.toString()); // For debugging
                System.out.println("Total Result: " + totalResult); // For debugging
            } else {
                return 101.0; // Return a false input
            }
        }
        return totalResult;
    }
    // Method to get total weight of extra added rows
    public double getExtraRowWeight() {
        totalWeight = 0.0; // Reset totalWeight
        for (int i = 0; i < weightEditTextList.toArray().length; i++) {
            String weight = weightEditTextList.get(i).getText().toString().trim();
            totalWeight += Double.parseDouble(weight); // Add weight from row to totalWeight
        }
        System.out.println("Total Weight " + totalWeight); // For debugging
        return totalWeight;
    }
    // Dark mode toggle method
    public void darkModeToggle() { // Dark mode / light mode toggle handler
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);
        if (mode != null) {
            if (mode.equals("dark")) {
                v.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                gradeCalcTitle.setTextColor(getResources().getColor(R.color.lightBlue));
                gradeCalcDesc.setTextColor(getResources().getColor(R.color.lightBlue));
                weightText.setTextColor(getResources().getColor(R.color.lightBlue));
                gradeText.setTextColor(getResources().getColor(R.color.lightBlue));
                gradeOutput.setTextColor(getResources().getColor(R.color.lightBlue));
                save.setTextColor(getResources().getColor(R.color.white));
                addRow.setTextColor(getResources().getColor(R.color.white));
                calculate.setTextColor(getResources().getColor(R.color.white));
                calculate.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                enableDarkMode(); // Enable dark mode for all inputs
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                gradeCalcTitle.setTextColor(getResources().getColor(R.color.darkBlue));
                gradeCalcDesc.setTextColor(getResources().getColor(R.color.darkBlue));
                weightText.setTextColor(getResources().getColor(R.color.darkBlue));
                gradeText.setTextColor(getResources().getColor(R.color.darkBlue));
                gradeOutput.setTextColor(getResources().getColor(R.color.darkBlue));
                save.setTextColor(getResources().getColor(R.color.black));
                addRow.setTextColor(getResources().getColor(R.color.black));
                calculate.setTextColor(getResources().getColor(R.color.black));
                calculate.setBackgroundColor(getResources().getColor(R.color.buttonBlue));
                disableDarkMode(); // Disable dark mode for all inputs
            }
        }
    }
    public void enableDarkMode() {
        // Weights
        weight1.setTextColor(getResources().getColor(R.color.white));
        weight2.setTextColor(getResources().getColor(R.color.white));
        weight3.setTextColor(getResources().getColor(R.color.white));
        weight4.setTextColor(getResources().getColor(R.color.white));
        // Grades
        grade1.setTextColor(getResources().getColor(R.color.white));
        grade2.setTextColor(getResources().getColor(R.color.white));
        grade3.setTextColor(getResources().getColor(R.color.white));
        grade4.setTextColor(getResources().getColor(R.color.white));
        // Extra rows
        enableDarkModeExtraRows();
    }
    public void disableDarkMode() {
        // Weights
        weight1.setTextColor(getResources().getColor(R.color.black));
        weight2.setTextColor(getResources().getColor(R.color.black));
        weight3.setTextColor(getResources().getColor(R.color.black));
        weight4.setTextColor(getResources().getColor(R.color.black));
        // Grades
        grade1.setTextColor(getResources().getColor(R.color.black));
        grade2.setTextColor(getResources().getColor(R.color.black));
        grade3.setTextColor(getResources().getColor(R.color.black));
        grade4.setTextColor(getResources().getColor(R.color.black));
        // Extra rows
        disableDarkModeExtraRows();
    }
    // Method to set colors for extra rows
    public void enableDarkModeExtraRows() {
        for (int i = 0; i < weightEditTextList.toArray().length; i++) {
            // Iterate through array and set color for weight and grade in each row
            gradeEditTextList.get(i).setTextColor(getResources().getColor(R.color.white));
            weightEditTextList.get(i).setTextColor(getResources().getColor(R.color.white));
        }
    }
    // Method to set colors for extra rows
    public void disableDarkModeExtraRows() {
        for (int i = 0; i < weightEditTextList.toArray().length; i++) {
            // Iterate through array and set color for weight and grade in each row
            gradeEditTextList.get(i).setTextColor(getResources().getColor(R.color.black));
            weightEditTextList.get(i).setTextColor(getResources().getColor(R.color.black));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
    // Method to write to local file
    private void writeFile() {
        // Make sure GPA output has text in it
        String outputField = gradeOutput.getText().toString().trim();
        if (!outputField.equalsIgnoreCase("Grade:")) {
            Toast.makeText(this, "Calculation saved local storage: History", Toast.LENGTH_SHORT).show();
            String outputContent = gradeOutput.getText().toString() + "%";
            // Get current time stamp
            getDate(); // Year/Month/Day
            getTime(); // Time
            // Save total string with type of calculator - output - timestamp
            String content = "Grade Calculator - " + outputContent + " - " + currentDate + " " + currentTime;
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
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saving initial 4 rows of input fields to bundle
        outState.putString("weight1", weight1.getText().toString().trim());
        outState.putString("weight2", weight2.getText().toString().trim());
        outState.putString("weight3", weight2.getText().toString().trim());
        outState.putString("weight4", weight2.getText().toString().trim());
        outState.putString("grade1", grade1.getText().toString().trim());
        outState.putString("grade2", grade2.getText().toString().trim());
        outState.putString("grade3", grade3.getText().toString().trim());
        outState.putString("grade4", grade4.getText().toString().trim());
        // Saving output field to bundle
        outState.putString("output", gradeOutput.getText().toString().trim());
        // Save extra rows to bundle
        if (!weightEditTextList.isEmpty()) {
            for (int i = 0; i < weightEditTextList.toArray().length; i++) {
                // Get elements from arrays of row elements
                String grade = gradeEditTextList.get(i).getText().toString().trim(); // Getting grade
                String weight = weightEditTextList.get(i).getText().toString().trim(); // Getting weight
                // Saving individual elements to bundle
                outState.putString("gradeExtra" + i, grade);
                outState.putString("weightExtra" + i, weight);
            }
        }
        ArrayList<String> editTextContentsW = new ArrayList<>(); // Array list to store weights
        ArrayList<String> editTextContentsG = new ArrayList<>(); // Array list to store grades
        editTextContentsW.clear();
        editTextContentsG.clear();
        for (EditText editText : weightEditTextList) {
            editTextContentsW.add(editText.getText().toString());
        }
        for (EditText editText : gradeEditTextList) {
            editTextContentsG.add(editText.getText().toString());
        }
        outState.putStringArrayList("editTextContentsW", editTextContentsW); // Saving weight array list to bundle
        outState.putStringArrayList("editTextContentsG", editTextContentsG); // Saving grade array list to bundle
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restoring initial 4 rows of input fields
        String weight1out = savedInstanceState.getString("weight1");
        String weight2out = savedInstanceState.getString("weight2");
        String weight3out = savedInstanceState.getString("weight3");
        String weight4out = savedInstanceState.getString("weight4");
        weight1.setText(weight1out);
        weight2.setText(weight2out);
        weight3.setText(weight3out);
        weight4.setText(weight4out);
        String grade1out = savedInstanceState.getString("grade1");
        String grade2out = savedInstanceState.getString("grade2");
        String grade3out = savedInstanceState.getString("grade3");
        String grade4out = savedInstanceState.getString("grade4");
        weight1.setText(grade1out);
        weight2.setText(grade2out);
        weight3.setText(grade3out);
        weight4.setText(grade4out);
        // Restoring output field
        String inPercent = savedInstanceState.getString("output");
        gradeOutput.setText(inPercent);
        // Restore extra row data
        ArrayList<String> savedEditTextContentsW = savedInstanceState.getStringArrayList("editTextContentsW");
        ArrayList<String> savedEditTextContentsG = savedInstanceState.getStringArrayList("editTextContentsG");
        // Restore the rows of fields
        for (String text : savedEditTextContentsW) {
            addRowElements(container); // Re-create the rows
            // Find the corresponding EditText and set the saved text
            weightEditTextList.get(weightEditTextList.size() - 1).setText(text);
        }
        for (String text : savedEditTextContentsG) {

            // Find the corresponding EditText and set the saved text
            gradeEditTextList.get(gradeEditTextList.size() - 1).setText(text);
        }
        // Get extra rows from bundle
        if (!savedEditTextContentsW.isEmpty()) {
            for (int i = 0; i < savedEditTextContentsW.toArray().length; i++) {
                // Get elements from arrays of row elements
                String weight = savedInstanceState.getString("weightExtra" + i); // Getting weight
                // Setting row contents from bundle
                weightEditTextList.get(i).setText(weight);
            }
        }
        if (!savedEditTextContentsG.isEmpty()) {
            for (int i = 0; i < savedEditTextContentsW.toArray().length; i++) {
                // Get elements from arrays of row elements
                String grade = savedInstanceState.getString("gradeExtra" + i); // Getting grade
                // Setting row contents from bundle
                gradeEditTextList.get(i).setText(grade);
            }
        }
    }
}
