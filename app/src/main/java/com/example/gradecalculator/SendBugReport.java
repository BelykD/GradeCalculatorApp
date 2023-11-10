package com.example.gradecalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SendBugReport extends AppCompatActivity {

    private EditText bugReportText;
    private Button submitBug;
    private TextView bugReportTitle;
    private SharedPreferences sharedPreferences;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_bug_report);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bugReportText = findViewById(R.id.bugReportText);
        submitBug = findViewById(R.id.submitBug);
        bugReportTitle = findViewById(R.id.bugReportTitle);

        // View used to edit background color for dark / light modes
        v = findViewById(R.id.content);

        submitBug.setOnClickListener(view -> {
            // Send if there is content in the bug description field
            if (!bugReportText.getText().toString().trim().isEmpty()) {
                sendEmail();
            } else {
                Toast.makeText(this, "Bug description is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Method to send email using intents
    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_TEXT, bugReportText.getText().toString().trim());
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"belykd19@mytru.ca"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "New Bug Report");
        startActivity(Intent.createChooser(intent, "Send Email"));
        Toast.makeText(this, "Bug Report Sent!", Toast.LENGTH_SHORT).show();
        System.out.println("Bug Report Sent!"); // For Debugging
    }
    // Dark mode toggle method
    public void darkModeToggle() { // Dark mode / light mode toggle handler
        sharedPreferences = getSharedPreferences("dark_mode", Context.MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", null);
        if (mode != null) {
            if (mode.equals("dark")) {
                v.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                bugReportTitle.setTextColor(getResources().getColor(R.color.lightBlue));
                submitBug.setTextColor(getResources().getColor(R.color.white));
                submitBug.setBackgroundColor(getResources().getColor(R.color.lightBlue));
            } else if (mode.equals("light")) {
                v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                bugReportTitle.setTextColor(getResources().getColor(R.color.darkBlue));
                submitBug.setTextColor(getResources().getColor(R.color.black));
                submitBug.setBackgroundColor(getResources().getColor(R.color.buttonBlue));
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        darkModeToggle();
    }
}
