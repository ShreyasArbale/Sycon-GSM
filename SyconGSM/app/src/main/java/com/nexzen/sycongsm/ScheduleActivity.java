package com.nexzen.sycongsm;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nexzen.sycongsm.customfonts.MyButton;
import com.nexzen.sycongsm.customfonts.MyEditText;

import java.util.Calendar;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    MyEditText edtScheduleDate, edtScheduleEndDate, edtStartTime, edtEndTime;
    MyEditText edtScheduleDateDisplay, edtScheduleEndDateDisplay, edtStartTimeDisplay, edtEndTimeDisplay;
    MyButton btnRegister;
    Switch RollAction;
    int ScheduleId;
    String ScheduleDate, ScheduleEndDate, StartTime, EndTime;
    DatabaseHandler db;
    private ProgressDialog pDialog;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mEndDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListenerStart;
    private TimePickerDialog.OnTimeSetListener mTimeSetListenerEnd;
    boolean IsAllRight = false;

    String Action = "INSERT";
    String Id = "0";
    String Status = "N";
    String SDate = "";
    String EDate = "";
    String DisplaySDate = "";
    String DisplayEDate = "";
    String STime = "";
    String ETime = "";
    String DisplaySTime = "";
    String DisplayETime = "";
    String Roll = "N";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        edtScheduleDate = (MyEditText) findViewById(R.id.ScheduleDate);
        edtScheduleEndDate = (MyEditText) findViewById(R.id.ScheduleEndDate);
        edtStartTime = (MyEditText) findViewById(R.id.StartTime);
        edtEndTime = (MyEditText) findViewById(R.id.EndTime);
        edtScheduleDateDisplay = (MyEditText) findViewById(R.id.ScheduleDateDisplay);
        edtScheduleEndDateDisplay = (MyEditText) findViewById(R.id.ScheduleEndDateDisplay);
        edtStartTimeDisplay = (MyEditText) findViewById(R.id.StartTimeDisplay);
        edtEndTimeDisplay = (MyEditText) findViewById(R.id.EndTimeDisplay);
        btnRegister = (MyButton) findViewById(R.id.btnRegister);
        RollAction = (Switch) findViewById(R.id.RollAction);
        Roll = "N";
        Intent i = getIntent();
        if (i.hasExtra("Action")) {
            Action = i.getStringExtra("Action");
            Id = i.getStringExtra("Id");
            Status = i.getStringExtra("Status");
            SDate = i.getStringExtra("Date");
            EDate = i.getStringExtra("EDate");
            DisplaySDate = i.getStringExtra("DisplayDate");
            DisplayEDate = i.getStringExtra("DisplayEDate");
            STime = i.getStringExtra("STime");
            ETime = i.getStringExtra("ETime");
            DisplaySTime = i.getStringExtra("DisplaySTime");
            DisplayETime = i.getStringExtra("DisplayETime");
            Roll = i.getStringExtra("Roll");
            edtScheduleDate.setText(SDate);
            edtScheduleDateDisplay.setText(DisplaySDate);
            edtScheduleEndDate.setText(EDate);
            edtScheduleEndDateDisplay.setText(DisplayEDate);
            edtStartTime.setText(STime);
            edtStartTimeDisplay.setText(DisplaySTime);
            edtEndTime.setText(ETime);
            edtEndTimeDisplay.setText(DisplayETime);

            if (Roll.equals("Y")) {
                RollAction.setText("होय..!");
                RollAction.setChecked(true);
                Roll = "Y";
            } else {
                RollAction.setText("नाही..!");
                RollAction.setChecked(false);
                Roll = "N";
            }
            btnRegister.setText("बदल करा");
        }
        db = new DatabaseHandler(ScheduleActivity.this);
        List<Schedule> Schedulelist = db.getAllSchedule();

        Log.d("my schedule", "here");

        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int hour = cal.get(Calendar.HOUR_OF_DAY);
        final int minute = cal.get(Calendar.MINUTE);

        edtScheduleDateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(
                        ScheduleActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        edtScheduleEndDateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(
                        ScheduleActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mEndDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtScheduleEndDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                edtScheduleEndDateDisplay.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        };

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtScheduleDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                edtScheduleDateDisplay.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        };


        edtStartTimeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialogTime1 = new TimePickerDialog(
                        ScheduleActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListenerStart,
                        hour, minute, false);
                dialogTime1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogTime1.show();
            }
        });

        mTimeSetListenerStart = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                edtStartTime.setText(hourOfDay + ":" + minute);
                edtStartTimeDisplay.setText(hourOfDay % 12 + ":" + minute + " " + ((hourOfDay >= 12) ? "PM" : "AM"));
            }
        };

        edtEndTimeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialogTime2 = new TimePickerDialog(
                        ScheduleActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListenerEnd,
                        hour, minute, false);
                dialogTime2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogTime2.show();
            }
        });

        mTimeSetListenerEnd = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                edtEndTime.setText(hourOfDay + ":" + minute);
                edtEndTimeDisplay.setText(hourOfDay % 12 + ":" + minute + " " + ((hourOfDay >= 12) ? "PM" : "AM"));

            }
        };

        RollAction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    RollAction.setText("होय..!");
                    Roll = "Y";
                } else {
                    RollAction.setText("नाही..!");
                    Roll = "N";
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtScheduleDate.getText().toString().equals("")) {
                    IsAllRight = true;
                } else {
                    IsAllRight = false;
                    edtScheduleDate.setError("Compalsory.!");
                }

                if (!edtScheduleEndDate.getText().toString().equals("")) {
                    IsAllRight = true;
                } else {
                    IsAllRight = false;
                    edtScheduleEndDate.setError("Compalsory.!");
                }

                if (!edtStartTime.getText().toString().equals("")) {
                    IsAllRight = true;
                } else {
                    IsAllRight = false;
                    edtStartTime.setError("Compalsory.!");
                }

                if (!edtEndTime.getText().toString().equals("")) {
                    IsAllRight = true;
                } else {
                    IsAllRight = false;
                    edtEndTime.setError("Compalsory.!");
                }

                if (IsAllRight) {

                    if (Action.equals("INSERT")) {
                        List<Schedule> Schedulelist = db.getAllSchedule();
                        int MaxId = (Schedulelist.size() + 1);

                        db.addSchedule(new Schedule(MaxId, edtScheduleDate.getText().toString(), edtScheduleEndDate.getText().toString(), edtStartTime.getText().toString(), edtEndTime.getText().toString(), "N", Roll));
                        Toast.makeText(ScheduleActivity.this, "शेड्युल यशस्वीरित्या साठवण्यात आला आहे...!", Toast.LENGTH_SHORT).show();
                        Intent schedule = new Intent(ScheduleActivity.this, MySheduleActivity.class);
                        startActivity(schedule);
                    } else {

                        db.updateSchedule(new Schedule(Integer.parseInt(Id), edtScheduleDate.getText().toString(), edtScheduleEndDate.getText().toString(), edtStartTime.getText().toString(), edtEndTime.getText().toString(), "N", Roll));
                        Toast.makeText(ScheduleActivity.this, "शेड्युल यशस्वीरित्या बदलण्यात आला आहे...!", Toast.LENGTH_SHORT).show();
                        Intent schedule = new Intent(ScheduleActivity.this, MySheduleActivity.class);
                        startActivity(schedule);
                        finish();
                    }

                    edtScheduleDate.setText("");
                    edtStartTime.setText("");
                    edtEndTime.setText("");

                    edtScheduleDateDisplay.setText("");
                    edtStartTimeDisplay.setText("");
                    edtEndTimeDisplay.setText("");
                    Roll = "N";
                    RollAction.setChecked(false);
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


}
