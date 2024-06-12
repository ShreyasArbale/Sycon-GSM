package com.nexzen.sycongsm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.List;

public class BackCallActivity extends AppCompatActivity {

    Switch OnOfAction;
    DatabaseHandler db;
    private static final int REQUEST_PHONE_CALL = 1;
    private final static int SEND_SMS_PERMISSION_REQ = 2;
    String DeviceMobileNumber = "";
    String UserMobileNumber = "";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DatabaseHandler(BackCallActivity.this);

        SharedPreferences myPref = getSharedPreferences("MemberRegistrationData", MODE_PRIVATE);

        DeviceMobileNumber = myPref.getString("DeviceMobileNo", "");
        UserMobileNumber = myPref.getString("UserMobileNo", "");

        Log.d("my ", UserMobileNumber);

        List<Users> loginList = db.getAllUsers();
        if (loginList.size() > 0) {
            DeviceMobileNumber = loginList.get(0).getDeviceMobileNo().toString();
            UserMobileNumber = loginList.get(0).getUserMobileNo().toString();
        }

        if (checkPermission(Manifest.permission.SEND_SMS)) {

        } else {
            ActivityCompat.requestPermissions(BackCallActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
        }

        OnOfAction = (Switch) findViewById(R.id.OnOfAction);

        SharedPreferences prefs = getSharedPreferences("BackCallData", MODE_PRIVATE);
        String CallStatus = prefs.getString("CallStatus", "NO_VALUE");

        if (CallStatus.equals("Y")) {
            OnOfAction.setChecked(true);
            OnOfAction.setText("चालू आहे..!");
        } else {
            OnOfAction.setChecked(false);
            OnOfAction.setText("बंद आहे..!");
        }

        OnOfAction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    OnOfAction.setText("चालू आहे..!");
                    if (checkPermission(Manifest.permission.SEND_SMS)) {
                        Log.d("my u num", DeviceMobileNumber );
                        Log.d("my u num",  UserMobileNumber);

                        SmsManager smsManager = SmsManager.getDefault();
//                        smsManager.sendTextMessage(DeviceMobileNumber, null, "RG1-" + UserMobileNumber + "\n" + "DRP-70\n" + "DLP-140\n" +
//                                        "POD-01\n" + "RST-000\n" + "ONT-000\n" + "OFT-000\n" + "3 PHASE\n" + "AUTO\n" + "SMS ON\n" + "CALL ON\n" + "MOTOR OFF\n" +
//                                        "END", null, null);

                        smsManager.sendTextMessage(DeviceMobileNumber, null, "CALLON", null, null);

                        SharedPreferences.Editor editor = getSharedPreferences("BackCallData", MODE_PRIVATE).edit();
                        editor.putString("CallStatus", "Y");
                        editor.apply();
                    } else {
                        ActivityCompat.requestPermissions(BackCallActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
                    }
                } else {
                    OnOfAction.setText("बंद आहे..!");
                    if (checkPermission(Manifest.permission.SEND_SMS)) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(DeviceMobileNumber, null, "CALLOFF", null, null);
                        SharedPreferences.Editor editor = getSharedPreferences("BackCallData", MODE_PRIVATE).edit();
                        editor.putString("CallStatus", "N");
                        editor.apply();
                    } else {
                        ActivityCompat.requestPermissions(BackCallActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
                    }
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

    private boolean checkPermission(String sendSms) {

        int checkpermission = ContextCompat.checkSelfPermission(this, sendSms);
        return checkpermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
