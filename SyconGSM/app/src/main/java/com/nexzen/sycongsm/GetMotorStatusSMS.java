package com.nexzen.sycongsm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GetMotorStatusSMS extends BroadcastReceiver {
    SimpleDateFormat Dateformat;
    SimpleDateFormat Timeformat;
    Date CurrentDate = null;
    Date CurrentTime = null;
    @Override
    public void onReceive(Context context, Intent intent) {

     // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        DatabaseHandler db = new DatabaseHandler(context);
                        List<Schedule> List = db.getAllSchedule();
                        Calendar cal = Calendar.getInstance();
                        //cal.add(Calendar.MINUTE,5);
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        int minute = cal.get(Calendar.MINUTE);

                        Dateformat = new SimpleDateFormat("dd/MM/yyyy");
                        Timeformat = new SimpleDateFormat("HH:mm");
                        try {
                            CurrentDate = Dateformat.parse(String.valueOf(day)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year));
                            CurrentTime = Timeformat.parse(String.valueOf(hour)+":"+String.valueOf(minute));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences prefs = context.getSharedPreferences("MemberRegistrationData", context.MODE_PRIVATE);
                        String DeviceMobileNo = prefs.getString("DeviceMobileNo", "NO_VALUE");

                        if(msg_from.equals(DeviceMobileNo) || msg_from.equals("0"+DeviceMobileNo) || msg_from.equals("91"+DeviceMobileNo)|| msg_from.equals("+91"+DeviceMobileNo) ) {
                            String msgBody = msgs[i].getMessageBody();
                            String msg = "NONE";
                            Intent dash = new Intent(context,DashboardActivity.class);
                            dash.putExtra("Status",msgBody);

                            msgBody.replaceAll("\n", " ");
                            if(msgBody.contains("Play") && msgBody.contains("PLAY"))
                            {
                                Toast.makeText(context, "Play Audio", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                }catch(Exception e){
//
                }
            }
        }
    }

    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

}
