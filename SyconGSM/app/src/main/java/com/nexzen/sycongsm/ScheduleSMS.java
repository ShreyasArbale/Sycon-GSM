package com.nexzen.sycongsm;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduleSMS extends Service {
    DatabaseHandler db;
    SimpleDateFormat Dateformat;
    SimpleDateFormat Timeformat;
    Date CurrentDate = null;
    Date CurrentTime = null;
    MediaPlayer player;
    String DeviceMobileNumber ="";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //player = MediaPlayer.create(this,Settings.System.DEFAULT_RINGTONE_URI);
        //player.setLooping(true);
        //player.start();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            //@Override
            public void run() {
                ScheduleSMSSender();
            }
        },0,1000);//Update text every second
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void ScheduleSMSSender() {
        db = new DatabaseHandler(this);
        List<Users> loginList=db.getAllUsers();
        if(loginList.size()>0){
            DeviceMobileNumber = loginList.get(0).getDeviceMobileNo().toString();
        }

        SharedPreferences myPref = getSharedPreferences("MemberRegistrationData", MODE_PRIVATE);
        DeviceMobileNumber = myPref.getString("DeviceMobileNo", "");

        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int hour = cal.get(Calendar.HOUR_OF_DAY);
        final int minute = cal.get(Calendar.MINUTE);

        Dateformat = new SimpleDateFormat("dd/MM/yyyy");
        Timeformat = new SimpleDateFormat("HH:mm");
        try {
            CurrentDate = Dateformat.parse(String.valueOf(day)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year));
            CurrentTime = Timeformat.parse(String.valueOf(hour)+":"+String.valueOf(minute));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        List<Schedule> List=db.getAllSchedule();

        if(List.size()>0){

            for(int i=0; i<List.size(); i++) {
                String ScheduleDateParts =List.get(i).getScheduleDate();
                String[] items1 = ScheduleDateParts.split("-");
                String Sday=items1[2];
                String Smonth=items1[1];
                String Syear=items1[0];
                Date ScheduleDate = null;

                String ScheduleEndDateParts =List.get(i).getScheduleEndDate();
                String[] itemsE = ScheduleDateParts.split("-");
                String Eday=itemsE[2];
                String Emonth=itemsE[1];
                String Eyear=itemsE[0];
                Date ScheduleEndDate = null;

                String ScheduleStartTimeParts =List.get(i).getStartTime();
                String[] items2 = ScheduleStartTimeParts.split(":");
                String SH=items2[0];
                String SM=items2[1];
                Date StartTime = null;

                String ScheduleEndTimeParts =List.get(i).getEndTime();
                String[] items3 = ScheduleEndTimeParts.split(":");
                String EH=items3[0];
                String EM=items3[1];
                Date EndTime = null;

                Date NewEndTime = null;

                if(List.get(i).getStatus().equals("N")){

                    try {
                        ScheduleDate = Dateformat.parse(Sday+"/"+Smonth+"/"+Syear);
                        StartTime = Timeformat.parse(String.valueOf(SH)+":"+String.valueOf(SM));

                        if(CurrentDate.compareTo(ScheduleDate)==0)
                        {
                            if(CurrentTime.compareTo(StartTime)==0)
                            {
                                db.updateScheduleStatus(List.get(i).getScheduleId(),"Y");
                                SmsManager smsManager=SmsManager.getDefault();
                                smsManager.sendTextMessage(DeviceMobileNumber,null,"MON4",null,null);
                                Log.d("my device no", DeviceMobileNumber);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if(List.get(i).getStatus().equals("Y")){
                    try {
                        ScheduleEndDate = Dateformat.parse(Eday+"/"+Emonth+"/"+Eyear);
                        EndTime = Timeformat.parse(String.valueOf(EH)+":"+String.valueOf(EM));

                        if(CurrentDate.compareTo(ScheduleEndDate)==0)
                        {
                            if(CurrentTime.compareTo(EndTime)==0)
                            {
                                db.updateScheduleStatus(List.get(i).getScheduleId(),"X");
                                SmsManager smsManager=SmsManager.getDefault();
                                smsManager.sendTextMessage(DeviceMobileNumber,null,"MOF6",null,null);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else if(List.get(i).getStatus().equals("A")){
                    try {
                        ScheduleEndDate = Dateformat.parse(Eday+"/"+Emonth+"/"+Eyear);
                        EndTime = Timeformat.parse(String.valueOf(EH)+":"+String.valueOf(EM));

                        if(CurrentDate.compareTo(ScheduleEndDate)==0)
                        {
                            if(CurrentTime.compareTo(EndTime)==0)
                            {
                                db.updateScheduleStatus(List.get(i).getScheduleId(),"X");
                                SmsManager smsManager=SmsManager.getDefault();
                                smsManager.sendTextMessage(DeviceMobileNumber,null,"MOF6",null,null);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.d("my exception" , e.toString());
                    }
                }
            }

        }
    }
}