package com.nexzen.sycongsm;

import static android.support.v4.content.ContextCompat.startActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class GetAndVerifyRegistrationSMS extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];

                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();

                        Log.d("my msg ", msgs[i].getMessageBody());

                        SharedPreferences prefs = context.getSharedPreferences("MemberRegistrationData", context.MODE_PRIVATE);
                        final String DeviceMobileNo = prefs.getString("DeviceMobileNo", "NO_VALUE");
                        String UserMobileNo = prefs.getString("UserMobileNo", "NO_VALUE");
                        Toast.makeText(context, msg_from, Toast.LENGTH_SHORT).show();

                        if (msg_from.contains(DeviceMobileNo)) {

                            if (msgs[i].getMessageBody().contains("SMS OFF")) {

                                String msgBody = msgs[i].getMessageBody();

//                             Toast.makeText(context, msgBody, Toast.LENGTH_SHORT).show();
                                if (msgBody.contains(UserMobileNo)) {

                                    Toast.makeText(context, "मोबाईल नंबर यशस्वीरीत्या नोदवण्यात आला आहे..!", Toast.LENGTH_SHORT).show();
                                    DatabaseHandler db = new DatabaseHandler(context);
                                    db.deleteAllUsers();
                                    db.addUser(new Users(1, DeviceMobileNo, UserMobileNo));

                                    final SmsManager smsManager = SmsManager.getDefault();

//                                smsManager.sendTextMessage(DeviceMobileNo, null, "RG1-" + UserMobileNo + "\n" + "DRP-70\n" +
//                                        "OLP-140\n" + "POD-01\n" + "RST-000\n" + "ONT-000\n" + "OFT-000\n" + "3 PHASE\n" + "AUTO\n"
//                                        + "SMS ON\n" + "CALL ON\n" + "MOTOR OFF\n" + "END", null, null);

                                    smsManager.sendTextMessage(DeviceMobileNo, null, "SMSON", null, null);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
//                                    smsManager.sendTextMessage(DeviceMobileNumber, null, "SMSOFF", null, null);
                                            smsManager.sendTextMessage(DeviceMobileNo, null, "SMSON", null, null);
                                        }
                                    },10000);

                                    Intent dash = new Intent();
                                    dash.setClassName("com.nexzen.sycongsm", "com.nexzen.sycongsm.DashboardActivity");
                                    dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(dash);

                                } else {
                                    Toast.makeText(context, "Reply Number Not Matching", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Mobile Number Not Matching", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (msgs[i].getMessageBody().contains("VRY=")){
                            String msg = msgs[i].getMessageBody();
                            Log.d("my msg from motor", msg);
                            Intent dash = new Intent();
                            dash.putExtra("msg", msg);
                            dash.setClassName("com.nexzen.sycongsm", "com.nexzen.sycongsm.DashboardActivity");
                            dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(dash);
                        }

                        if (msgs[i].getMessageBody().contains("Motor ON")) {
                            String msg = "Motor ON";
                            Log.d("my msg from motor", msg);
                            Intent dash = new Intent();
                            dash.putExtra("msg", msg);
                            dash.setClassName("com.nexzen.sycongsm", "com.nexzen.sycongsm.DashboardActivity");
                            dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(dash);
                        }
                        else if (msgs[i].getMessageBody().contains("MOTOR OFF")) {
                            String msg = "MOTOR OFF";
                            Log.d("my msg from motor", msg);
                            Intent dash = new Intent();
                            dash.putExtra("msg", msg);
                            dash.setClassName("com.nexzen.sycongsm", "com.nexzen.sycongsm.DashboardActivity");
                            dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(dash);
                        }
                        else if (msgs[i].getMessageBody().contains("Motor OFF : Dry Run")) {
                            String msg = "Motor OFF:Dry Run";
                            Log.d("my msg from motor", msg);
                            Intent dash = new Intent();
                            dash.putExtra("msg", msg);
                            dash.setClassName("com.nexzen.sycongsm", "com.nexzen.sycongsm.DashboardActivity");
                            dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(dash);
                        }
                        else if (msgs[i].getMessageBody().contains("Starter fault")) {
                            String msg = "Starter fault";
                            Log.d("my msg from motor", msg);
                            Intent dash = new Intent();
                            dash.putExtra("msg", msg);
                            dash.setClassName("com.nexzen.sycongsm", "com.nexzen.sycongsm.DashboardActivity");
                            dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(dash);
                        }
                        else if (msgs[i].getMessageBody().contains("Motor OFF : SPP")) {
                            String msg = "Motor OFF : SPP";
                            Log.d("my msg from motor", msg);
                            Intent dash = new Intent();
                            dash.putExtra("msg", msg);
                            dash.setClassName("com.nexzen.sycongsm", "com.nexzen.sycongsm.DashboardActivity");
                            dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(dash);
                        }
                        else if (msgs[i].getMessageBody().contains("Power OFF")) {
                            String msg = "Power OFF";
                            Log.d("my msg from motor", msg);
                            Intent dash = new Intent();
                            dash.putExtra("msg", msg);
                            dash.setClassName("com.nexzen.sycongsm", "com.nexzen.sycongsm.DashboardActivity");
                            dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(dash);
                        }

                    }

                } catch (Exception e) {
                    Log.d("my exception", e.toString());
                }
            }
        }
    }


    public void Notification(Context context, String message) {
        // Set Notification Title

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, DashboardActivity.class);
        // Send data to NotificationView Class
        intent.putExtra("Status", message);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                // Set Icon
                .setSmallIcon(R.drawable.sycon_logo)
                // Set Ticker Message
                .setTicker(message)
                // Set Title
                .setContentTitle(message)
                // Set Text
                .setContentText(message)
                // Add an Action Button below Notification


                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }
}
