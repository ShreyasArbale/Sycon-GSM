package com.nexzen.sycongsm;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nexzen.sycongsm.customfonts.MyTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PHONE_CALL = 1;
    Dialog dialog;
    String msg = "";
    ImageView PowerOnButton, PowerOnByCall, GetInfoButton, PowerOffButton;

    MyTextView tvInfo;

    Button btnStatus;
    ImageView MoterOffImage;
    GifImageView StatusImage;
    DatabaseHandler db;
    private final static int SEND_SMS_PERMISSION_REQ = 2;
    String DeviceMobileNumber = "";
    MyTextView scrolling_text;
    private ProgressDialog pDialog;
    boolean BYCall = false;
    String Status = "";

    SimpleDateFormat Dateformat;
    SimpleDateFormat Timeformat;
    Date CurrentDate = null;
    Date CurrentTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int hour = cal.get(Calendar.HOUR_OF_DAY);
        final int minute = cal.get(Calendar.MINUTE);

        Dateformat = new SimpleDateFormat("dd/MM/yyyy");
        Timeformat = new SimpleDateFormat("HH:mm");
        try {
            CurrentDate = Dateformat.parse(String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
            CurrentTime = Timeformat.parse(String.valueOf(hour) + ":" + String.valueOf(minute));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        db = new DatabaseHandler(DashboardActivity.this);

//        db.deleteAllSchedule();
        //stopService(new Intent(this,ScheduleSMS.class));
        startService(new Intent(this, ScheduleSMS.class));
        List<Schedule> List = db.getAllSchedule();


        List<Users> loginList = db.getAllUsers();
        if (loginList.size() > 0) {
            DeviceMobileNumber = loginList.get(0).getDeviceMobileNo().toString();
        }

        if (checkPermission(Manifest.permission.SEND_SMS)) {

        } else {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
        }
        msg = "सध्याची मोटरची आणि लाईटची स्तिती माहित नाही, जाणून घेण्यासाठी माहिती चे बटन दाबा..!";

        scrolling_text = (MyTextView) findViewById(R.id.scrolling_text);
        scrolling_text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        scrolling_text.setSelected(true);
        scrolling_text.setSingleLine(true);
        scrolling_text.setText(msg);

        MoterOffImage = (ImageView) findViewById(R.id.MoterOffImage);
        StatusImage = (GifImageView) findViewById(R.id.StatusImage);
        StatusImage.setVisibility(View.GONE);

        tvInfo = (MyTextView) findViewById(R.id.tvInfo);

        PowerOnButton = (ImageView) findViewById(R.id.PowerOnButton);
        PowerOnByCall = (ImageView) findViewById(R.id.PowerOnByCall);
        GetInfoButton = (ImageView) findViewById(R.id.GetInfoButton);
        PowerOffButton = (ImageView) findViewById(R.id.PowerOffButton);
        btnStatus = (Button) findViewById(R.id.btnStatus);

        Intent i = getIntent();
        if (i.hasExtra("Status")) {
            Status = i.getStringExtra("Status");
            msg = i.getStringExtra("Msg");

            Log.d("my", Status + " " + msg);

            if (Status.contains("Motor ON") && Status.contains("Power ON")) {
                msg = "सध्या लाईट आहे आणि तुमची मोटर सुरु झालेली आहे, बंद करण्यासाठी खालील लाल बटन दाबा..!";
                scrolling_text.setText(msg);
                Loadtip(msg);

                StatusImage.setVisibility(View.VISIBLE);
                MoterOffImage.setVisibility(View.GONE);
                PowerOffButton.setImageResource(R.drawable.power_off_inactive);
                PowerOnButton.setImageResource(R.drawable.power_on_active);


            } else if (Status.contains("Motor OFF : SPP") && Status.contains("Power ON")) {
                msg = "सध्या लाईट आहे पण सिंगल फेजिंग ने तुमची मोटर बंद झालेली आहे..!";
                scrolling_text.setText(msg);
                Loadtip(msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);
            } else if (Status.contains("Power OFF")) {
                msg = "सध्या लाईट गेलेली आहे आणि तुमची मोटर बंद आहे..!";
                scrolling_text.setText(msg);
                Loadtip(msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);

            } else if (Status.contains("Motor OFF : Dry Run") && Status.contains("Power ON")) {
                msg = "सध्या लाईट आहे पण ड्रायरन ने तुमची मोटर बंद झालेली आहे..!";
                scrolling_text.setText(msg);
                Loadtip(msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);
            } else if (Status.contains("Starter fault") && Status.contains("Power ON")) {
                msg = "सध्या लाईट आहे पण स्टार्टर मधील दोशामुळे तुमची मोटर बंद झालेली आहे..!";
                scrolling_text.setText(msg);
                Loadtip(msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);
            } else if (Status.contains("MOTOR OFF") && Status.contains("Power ON")) {
                msg = "तुमची मोटर बंद झालेली आहे पण सध्या लाईट आहे ..!";
                scrolling_text.setText(msg);
                Loadtip(msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);
            }
        }

        SharedPreferences myPref = getSharedPreferences("MemberRegistrationData", MODE_PRIVATE);

        DeviceMobileNumber = myPref.getString("DeviceMobileNo", "");

        Log.d("my PowerOnButton", DeviceMobileNumber);

        PowerOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermission(Manifest.permission.SEND_SMS)) {
                    BYCall = false;
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(DeviceMobileNumber, null, "MON4", null, null);

                    pDialog = new ProgressDialog(DashboardActivity.this);
                    // Showing progress dialog before making http request
                    pDialog.setMessage("Loading...");
//                    pDialog.show();
                    Toast.makeText(DashboardActivity.this, "Power is On", Toast.LENGTH_SHORT).show();

                } else {
                    ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
                }

            }
        });

        PowerOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission(Manifest.permission.SEND_SMS)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(DeviceMobileNumber, null, "MOF6", null, null);
                    pDialog = new ProgressDialog(DashboardActivity.this);
                    // Showing progress dialog before making http request
                    pDialog.setMessage("Loading...");
//                    pDialog.show();
                    Toast.makeText(DashboardActivity.this, "Power is Off", Toast.LENGTH_SHORT).show();

                } else {
                    ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
                }
            }
        });

        PowerOnByCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + DeviceMobileNumber));
                if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    startActivity(callIntent);
                    finish();
                    BYCall = true;
                }

            }
        });

        GetInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent schedule = new Intent(DashboardActivity.this, ScheduleActivity.class);
                startActivity(schedule);
            }
        });
        Typeface typeface = ResourcesCompat.getFont(this, R.font.font_digital);
        tvInfo.setTypeface(typeface);
        tvInfo.setTypeface(tvInfo.getTypeface(), Typeface.BOLD);

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermission(Manifest.permission.SEND_SMS)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(DeviceMobileNumber, null, "STATUS", null, null);
                    pDialog = new ProgressDialog(DashboardActivity.this);
//                     Showing progress dialog before making http request
                    pDialog.setMessage("Loading...");
//                    pDialog.show();
                    Toast.makeText(DashboardActivity.this, "Wait for moment", Toast.LENGTH_LONG).show();

                } else {
                    ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
                }

                Intent intent = getIntent();
                String Shared_Status = intent.getStringExtra("msg");

                tvInfo.setText(Shared_Status);

//                Log.d("my load tip", Shared_Status);
//                dialog = new Dialog(DashboardActivity.this, android.R.style.Theme_Holo_Light_Dialog);
//                dialog.setContentView(R.layout.msg_pop_up);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                MyTextView txtMsg = (MyTextView) dialog.findViewById(R.id.txtMsg);
//                txtMsg.setText(Shared_Status);
//                ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
//                btnClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
            }
        });

        SharedPreferences prefs = getSharedPreferences("MotorStatus", MODE_PRIVATE);

        String Shared_Status = prefs.getString("Status", "");
        String Shared_Msg = prefs.getString("Msg", "");

        Log.d("my Shared_Status", Shared_Status + " " + Shared_Msg);

        if (!Shared_Status.equals("")) {
            if (Shared_Status.contains("Motor ON") && Shared_Status.contains("Power ON")) {
                msg = "सध्या लाईट आहे आणि तुमची मोटर सुरु झालेली आहे, बंद करण्यासाठी खालील लाल बटन दाबा..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.VISIBLE);
                MoterOffImage.setVisibility(View.GONE);
                PowerOffButton.setImageResource(R.drawable.power_off_inactive);
                PowerOnButton.setImageResource(R.drawable.power_on_active);

            } else if (Shared_Status.contains("Motor OFF : SPP") && Shared_Status.contains("Power ON")) {
                msg = "सध्या लाईट आहे पण सिंगल फेजिंग ने तुमची मोटर बंद झालेली आहे..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);

            } else if (Shared_Status.contains("Power OFF")) {
                msg = "सध्या लाईट गेलेली आहे आणि तुमची मोटर बंद आहे..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);

            } else if (Shared_Status.contains("Motor OFF : Dry Run") && Shared_Status.contains("Power ON")) {
                msg = "सध्या लाईट आहे पण ड्रायरन ने तुमची मोटर बंद झालेली आहे..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);

            } else if (Shared_Status.contains("Starter fault") && Shared_Status.contains("Power ON")) {
                msg = "सध्या लाईट आहे पण स्टार्टर मधील दोशामुळे तुमची मोटर बंद झालेली आहे..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);

            } else if (Shared_Status.contains("MOTOR OFF") && Shared_Status.contains("Power ON")) {
                msg = "तुमची मोटर बंद झालेली आहे पण सध्या लाईट आहे ..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);
            }
        }
    }


    public void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private static DashboardActivity ins;

    public static DashboardActivity getInstace() {
        return ins;
    }

    public void updateView(final String t, final String msgtxt) {
        DashboardActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                t.replaceAll("\n", " ");
                if (t.contains("Motor ON") && t.contains("Power ON")) {
                    msg = "सध्या लाईट आहे आणि तुमची मोटर सुरु झालेली आहे, बंद करण्यासाठी खालील लाल बटन दाबा..!";
                    SharedPreferences.Editor editor = getSharedPreferences("MotorStatus", MODE_PRIVATE).edit();
                    editor.putString("Status", t);
                    editor.putString("Msg", msg);
                    editor.apply();
                    scrolling_text.setText(msg);
                    Loadtip(msgtxt);

                    StatusImage.setVisibility(View.VISIBLE);
                    MoterOffImage.setVisibility(View.GONE);
                    PowerOffButton.setImageResource(R.drawable.power_off_inactive);
                    PowerOnButton.setImageResource(R.drawable.power_on_active);
                    hidePDialog();

                } else if (t.contains("Motor OFF : SPP") && t.contains("Power ON")) {

                    msg = "सध्या लाईट आहे पण सिंगल फेजिंग ने तुमची मोटर बंद झालेली आहे..!";
                    SharedPreferences.Editor editor = getSharedPreferences("MotorStatus", MODE_PRIVATE).edit();
                    editor.putString("Status", t);
                    editor.putString("Msg", msg);
                    editor.apply();
                    scrolling_text.setText(msg);
                    Loadtip(msgtxt);

                    StatusImage.setVisibility(View.GONE);
                    MoterOffImage.setVisibility(View.VISIBLE);
                    PowerOffButton.setImageResource(R.drawable.power_off_active);
                    PowerOnButton.setImageResource(R.drawable.power_on_inactive);
                    hidePDialog();
                } else if (t.contains("Power OFF")) {
                    msg = "सध्या लाईट गेलेली आहे आणि तुमची मोटर बंद आहे..!";
                    SharedPreferences.Editor editor = getSharedPreferences("MotorStatus", MODE_PRIVATE).edit();
                    editor.putString("Status", t);
                    editor.putString("Msg", msg);
                    editor.apply();
                    scrolling_text.setText(msg);
                    Loadtip(msgtxt);

                    StatusImage.setVisibility(View.GONE);
                    MoterOffImage.setVisibility(View.VISIBLE);
                    PowerOffButton.setImageResource(R.drawable.power_off_active);
                    PowerOnButton.setImageResource(R.drawable.power_on_inactive);
                    hidePDialog();

                } else if (t.contains("Motor OFF : Dry Run") && t.contains("Power ON")) {

                    msg = "सध्या लाईट आहे पण ड्रायरन ने तुमची मोटर बंद झालेली आहे..!";
                    SharedPreferences.Editor editor = getSharedPreferences("MotorStatus", MODE_PRIVATE).edit();
                    editor.putString("Status", t);
                    editor.putString("Msg", msg);
                    editor.apply();
                    scrolling_text.setText(msg);
                    Loadtip(msgtxt);

                    StatusImage.setVisibility(View.GONE);
                    MoterOffImage.setVisibility(View.VISIBLE);
                    PowerOffButton.setImageResource(R.drawable.power_off_active);
                    PowerOnButton.setImageResource(R.drawable.power_on_inactive);
                    hidePDialog();

                } else if (t.contains("Starter fault") && t.contains("Power ON")) {

                    msg = "सध्या लाईट आहे पण स्टार्टर मधील दोशामुळे तुमची मोटर बंद झालेली आहे..!";
                    SharedPreferences.Editor editor = getSharedPreferences("MotorStatus", MODE_PRIVATE).edit();
                    editor.putString("Status", t);
                    editor.putString("Msg", msg);
                    editor.apply();
                    scrolling_text.setText(msg);
                    Loadtip(msgtxt);

                    StatusImage.setVisibility(View.GONE);
                    MoterOffImage.setVisibility(View.VISIBLE);
                    PowerOffButton.setImageResource(R.drawable.power_off_active);
                    PowerOnButton.setImageResource(R.drawable.power_on_inactive);
                    hidePDialog();

                } else if (t.contains("MOTOR OFF") && t.contains("Power ON")) {
                    msg = "तुमची मोटर बंद झालेली आहे पण सध्या लाईट आहे ..!";
                    SharedPreferences.Editor editor = getSharedPreferences("MotorStatus", MODE_PRIVATE).edit();
                    editor.putString("Status", t);
                    editor.putString("Msg", msg);
                    editor.apply();
                    scrolling_text.setText(msg);
                    Loadtip(msgtxt);

                    StatusImage.setVisibility(View.GONE);
                    MoterOffImage.setVisibility(View.VISIBLE);
                    PowerOffButton.setImageResource(R.drawable.power_off_active);
                    PowerOnButton.setImageResource(R.drawable.power_on_inactive);
                    hidePDialog();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + DeviceMobileNumber));
                    startActivity(intent);
                    finish();
                } else {

                }
                return;

            case SEND_SMS_PERMISSION_REQ:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;
        }
    }

    public void Loadtip(String str) {
        Log.d("my load tip", str);
        dialog = new Dialog(DashboardActivity.this, android.R.style.Theme_Holo_Light_Dialog);
        dialog.setContentView(R.layout.msg_pop_up);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MyTextView txtMsg = (MyTextView) dialog.findViewById(R.id.txtMsg);
        txtMsg.setText(str);
        ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//        dialog.show();

    }

    private boolean checkPermission(String sendSms) {

        int checkpermission = ContextCompat.checkSelfPermission(this, sendSms);
        return checkpermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("MotorStatus", MODE_PRIVATE);

//        String Shared_Status = prefs.getString("Status", "");
        String Shared_Msg = prefs.getString("Msg", "");

        Intent intent = getIntent();
        String Shared_Status = intent.getStringExtra("msg");
//        Shared_Status = "Motor ON";

        Log.d("my status", Shared_Status + " " + Shared_Msg);
        if (Shared_Status != null && !Shared_Status.equals("")) {
            if (Shared_Status.contains("Motor ON")) {
//                && Shared_Status.contains("Power ON")
                msg = "सध्या लाईट आहे आणि तुमची मोटर सुरु झालेली आहे, बंद करण्यासाठी खालील लाल बटन दाबा..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.VISIBLE);
                MoterOffImage.setVisibility(View.GONE);
                PowerOffButton.setImageResource(R.drawable.power_off_inactive);
                PowerOnButton.setImageResource(R.drawable.power_on_active);

            } else if (Shared_Status.contains("MOTOR OFF")) {
//                && Shared_Status.contains("Power ON")
                msg = "तुमची मोटर बंद झालेली आहे पण सध्या लाईट आहे ..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);
            } else if (Shared_Status.contains("Motor OFF : Dry Run")) {
//                 && Shared_Status.contains("Power ON")
                msg = "सध्या लाईट आहे पण ड्रायरन ने तुमची मोटर बंद झालेली आहे..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);

            } else if (Shared_Status.contains("Starter fault")) {
//                 && Shared_Status.contains("Power ON")
                msg = "सध्या लाईट आहे पण स्टार्टर मधील दोशामुळे तुमची मोटर बंद झालेली आहे..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);

            } else if (Shared_Status.contains("Motor OFF : SPP")) {
//                 && Shared_Status.contains("Power ON")
                msg = "सध्या लाईट आहे पण सिंगल फेजिंग ने तुमची मोटर बंद झालेली आहे..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);

            } else if (Shared_Status.contains("Power OFF")) {
                msg = "सध्या लाईट गेलेली आहे आणि तुमची मोटर बंद आहे..!";
                scrolling_text.setText(msg);
                Loadtip(Shared_Msg);

                StatusImage.setVisibility(View.GONE);
                MoterOffImage.setVisibility(View.VISIBLE);
                PowerOffButton.setImageResource(R.drawable.power_off_active);
                PowerOnButton.setImageResource(R.drawable.power_on_inactive);

            }

            if (Shared_Status.contains("VRY=")){

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_services) {
            Intent services = new Intent(DashboardActivity.this, ServicesActivity.class);
            startActivity(services);
        } else if (id == R.id.nav_instruction) {
            Intent instruction = new Intent(DashboardActivity.this, InstructionActivity.class);
            startActivity(instruction);
        } else if (id == R.id.nav_procedure) {
            Intent procedure = new Intent(DashboardActivity.this, ProcedureActivity.class);
            startActivity(procedure);
        } else if (id == R.id.nav_backCall) {

            Intent backCall = new Intent(DashboardActivity.this, BackCallActivity.class);
            startActivity(backCall);

        } else if (id == R.id.nav_shedule) {
            Intent schedule = new Intent(DashboardActivity.this, MySheduleActivity.class);
            startActivity(schedule);
        } else if (id == R.id.nav_re_register) {

            db = new DatabaseHandler(DashboardActivity.this);
            db.deleteAllUsers();

            Intent register = new Intent(DashboardActivity.this, RegistrationActivity.class);
            startActivity(register);

        } else if (id == R.id.nav_contact) {
            Intent contact = new Intent(DashboardActivity.this, ConatctActivity.class);
            startActivity(contact);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
