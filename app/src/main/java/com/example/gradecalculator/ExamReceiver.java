package com.example.gradecalculator;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExamReceiver extends BroadcastReceiver {

    String examDate, dateText;
    private String currentDate, date;
    private final String FILENAME = "exam-notifications.txt"; // Save file name
    private ArrayList<String> notificationList = new ArrayList<>();
    private ArrayList<String> notificationListTrimmed = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_DATE_CHANGED)) {
            readFile(context);
            date = getDate(); // Get current date to compare to
            for (int i = 0; i < notificationList.size(); i++) {
                String[] ele = notificationList.get(i).split("-");
                String trimmedElement = ele[1].trim();
                notificationListTrimmed.add(trimmedElement); // Add element to trimmed list
            }
            System.out.println("Trimmed list: " + notificationListTrimmed); // For debugging
            System.out.println(getDate()); // For debugging
            System.out.println(notificationList); // For debugging
            // When date changes - send notification
            for (int i = 0; i < notificationListTrimmed.size(); i++) {
                if (String.valueOf(notificationListTrimmed.get(i)).equals(date)) {
                    dateText = notificationList.get(i); // Get related name and date
                    examDate = "Notification for scheduled exam date: " + dateText;
                    sendNotification(context, "Your exam is near!", examDate);
                }
            }
        }
        System.out.println("Date Changed"); // For debugging
    }
    public void sendNotification(Context context, String title, String body) {
        // Page that opens when notification is clicked
        Intent intent = new Intent(context, ExamSaves.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.arrow_dark) // Icon displayed
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)  // Set the intent to open when the notification is clicked
                .setAutoCancel(true); // Auto dismiss the notification when clicked

        // Show notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification channels are required for devices running Android 8.0 (API level 26) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Channel Name";
            String channelDescription = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", channelName, importance);
            channel.setDescription(channelDescription);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        assert notificationManager != null;
        notificationManager.notify(1, builder.build());
    }
    // Method to read local file for saved history
    public void readFile(Context context) {
        if (checkIfFileExists(context, FILENAME)) {
            // Read contents of file and update
            FileInputStream fis = null;
            try {
                fis = context.openFileInput(FILENAME);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String sLine = null;
            try {
                while ((sLine = br.readLine()) != null) {
                    notificationList.add(sLine); // Add saved history items to list
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File read!");
        } else {
            System.out.println("File doesn't exist!");
        }
    }
    public boolean checkIfFileExists(Context context, String fileName) {
        File file = context.getFileStreamPath(fileName);
        return file.exists();
    }
    // Method to get current date
    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        // Subtract one day - scheduler sends broadcast the day before
        //calendar.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentDate = dateFormat.format(calendar.getTime());

        return currentDate;
    }
}
