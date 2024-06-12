package com.nexzen.sycongsm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.nexzen.sycongsm.customfonts.MyButton;
import com.nexzen.sycongsm.customfonts.MyEditText;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    MyEditText edtDeviceNumber, edtMobileNumber;
    MyButton btnRegister;
    private static final int REQUEST_PHONE_CALL = 1;
    private final static int SEND_SMS_PERMISSION_REQ = 2;
    private final static int RECEIVE_SMS_PERMISSION_REQ = 3;
    boolean IsAllRight = true;
    String DeviceMobileNumber = "";
    String UserMobileNumber = "";
    DatabaseHandler db;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        db = new DatabaseHandler(RegistrationActivity.this);
        List<Users> loginList = db.getAllUsers();

        if (loginList.size() > 0) {
            Intent i = new Intent(RegistrationActivity.this, DashboardActivity.class);
            hidePDialog();
            startActivity(i);
            finish();
        }

        if (checkPermission(Manifest.permission.SEND_SMS)) {

        } else {
            ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
        }


        edtDeviceNumber = (MyEditText) findViewById(R.id.DeviceNumber);
        edtMobileNumber = (MyEditText) findViewById(R.id.MobileNumber);

        btnRegister = (MyButton) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog = new ProgressDialog(RegistrationActivity.this);
//                             Showing progress dialog before making http request
                pDialog.setMessage("Loading...");
                pDialog.show();

                if (edtDeviceNumber.getText().toString().length() == 0 && edtMobileNumber.getText().toString().length() == 0) {
                    Toast.makeText(RegistrationActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else if (edtDeviceNumber.getText().toString().length() == 10 && edtMobileNumber.getText().toString().length() == 10){

                    if (!edtDeviceNumber.getText().toString().equals("")) {
                        IsAllRight = true;
                        DeviceMobileNumber = edtDeviceNumber.getText().toString();
                    } else {
                        IsAllRight = false;
                        edtDeviceNumber.setError("Compulsory!");
                    }
                    if (!edtMobileNumber.getText().toString().equals("")) {
                        IsAllRight = true;
                        UserMobileNumber = edtMobileNumber.getText().toString();
                    } else {
                        IsAllRight = false;
                        edtMobileNumber.setError("Compulsory!");
                    }
                    if (IsAllRight) {

                       // db.addUser(new Users(0, DeviceMobileNumber, UserMobileNumber));

                        if (checkPermission(Manifest.permission.SEND_SMS)) {
                             final SmsManager smsManager = SmsManager.getDefault();

                            smsManager.sendTextMessage(DeviceMobileNumber, null, "RD", null, null);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    smsManager.sendTextMessage(DeviceMobileNumber, null, "SMSOFF", null, null);
                                }
                            },10000);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    smsManager.sendTextMessage(DeviceMobileNumber, null, "RG" + UserMobileNumber, null, null);

                                    SharedPreferences.Editor editor = getSharedPreferences("MemberRegistrationData", MODE_PRIVATE).edit();
                                    editor.putString("DeviceMobileNo", DeviceMobileNumber.toString());
                                    editor.putString("UserMobileNo", UserMobileNumber.toString());
                                    editor.apply();

                                    pDialog.dismiss();

                                    Intent i = new Intent(RegistrationActivity.this, DashboardActivity.class);
                                    startActivity(i);
                                }
                            }, 25000);
                        }

                        else {
                            ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);

                        }
                    }
                }
            }
        });
    }

    public void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private boolean checkPermission(String sendSms) {

        int checkpermission = ContextCompat.checkSelfPermission(this, sendSms);
        return checkpermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        int hasContactPermission = ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.RECEIVE_SMS);

        if (hasContactPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_PERMISSION_REQ);
        } else {
            //Toast.makeText(AddContactsActivity.this, "Contact Permission is already granted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT < 23) {
                        //your code here
                    } else {
                        requestSmsPermission();
                    }
                } else {

                }
                return;

            case SEND_SMS_PERMISSION_REQ:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    } else {

                    }
                } else {

                }
                break;
            case RECEIVE_SMS_PERMISSION_REQ:
                // Check if the only required permission has been granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Contact Permission is Granted", Toast.LENGTH_SHORT).show();

                } else {

                }
                break;
        }
    }
}
