package com.nexzen.sycongsm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MySheduleActivity extends AppCompatActivity {

    ListView SheduleList;
    ArrayList<Schedule> shedule_list = new ArrayList<Schedule>();
    Schedule Schedule_List;
    SheduleListAdapater adapter;
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DatabaseHandler(MySheduleActivity.this);
        List<Schedule> List = db.getAllSchedule();
        if(List.size()>0) {
            SheduleList = (ListView) findViewById(R.id.SheduleList);
            adapter = new SheduleListAdapater(MySheduleActivity.this,List);
            SheduleList.setAdapter(adapter);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
