package com.nexzen.sycongsm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jb on 02/08/2017.
 */

public class SheduleListAdapater extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private Typeface Tf;
    Context context;
    private List<Schedule> List;
   DatabaseHandler db;

    public SheduleListAdapater(Activity activity, List<Schedule> _list) {

        this.activity = activity;
        this.List = _list;


    }

    @Override
    public int getCount() {
        return List.size();
    }

    @Override
    public Object getItem(int position) {
        return List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.schedule_item, null);

        final TextView txtScheduleId = (TextView) convertView.findViewById(R.id.ScheduleId);
        final TextView txtScheduleDate = (TextView) convertView.findViewById(R.id.ScheduleDate);
        final TextView txtScheduleEndDate = (TextView) convertView.findViewById(R.id.ScheduleEndDate);
        final TextView txtStartTime = (TextView) convertView.findViewById(R.id.StartTime);
        final TextView txtEndTime = (TextView) convertView.findViewById(R.id.EndTime);

        db = new DatabaseHandler(activity);
        final Schedule Sl = List.get(position);

        // id
        txtScheduleId.setText(""+Sl.getScheduleId());

        final String date=Sl.getScheduleDate();
        String[] items1 = date.split("-");
        final String day=items1[2];
        final String month=items1[1];
        final String year=items1[0];

        final String edate=Sl.getScheduleEndDate();
        String[] items2 = edate.split("-");
        final String eday=items2[2];
        final String emonth=items2[1];
        final String eyear=items2[0];

        txtScheduleDate.setText("मोटर सुरु करावयाची तारीख : "+day+"/"+month+"/"+year);
        txtScheduleEndDate.setText("मोटर बंद करावयाची तारीख : "+eday+"/"+emonth+"/"+eyear);

        final String StartTime=Sl.getStartTime();
        int SH = 0;
        int SM = 0;
        String displayStime = "";
        //txtStartTime.setText("सुरु करण्याची वेळ : "+StartTime);
        if(!StartTime.equals("")) {
            String[] StartTimeParts = StartTime.split(":");
            SH = Integer.parseInt(StartTimeParts[0]);
            SM = Integer.parseInt(StartTimeParts[1]);
            txtStartTime.setText("सुरु करण्याची वेळ : "+SH%12 + ":" + SM + " " + ((SH>=12) ? "PM" : "AM"));
            displayStime = SH%12 + ":" + SM + " " + ((SH>=12) ? "PM" : "AM");
        }



        final String EndTime=Sl.getEndTime();
        txtEndTime.setText("बंद करण्याची वेळ : "+EndTime);
        int EH = 0;
        int EM = 0;
        String displayEtime = "";
        if(!EndTime.equals("")) {
            String[] EndTimeParts = EndTime.split(":");
            EH = Integer.parseInt(EndTimeParts[0]);
            EM = Integer.parseInt(EndTimeParts[1]);
            txtEndTime.setText("बंद करण्याची वेळ : "+EH%12 + ":" + EM + " " + ((EH>=12) ? "PM" : "AM"));
            displayEtime = EH%12 + ":" + EM + " " + ((EH>=12) ? "PM" : "AM");
        }


        ImageView btnDelete = (ImageView) convertView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteSchedule(txtScheduleId.getText().toString());
                activity.finish();
                activity.startActivity(activity.getIntent());
            }
        });
        final String finalDisplayStime = displayStime;
        final String finalDisplayEtime = displayEtime;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Sl.getStatus().toString().equals("N"))
                {
                    Intent schedule = new Intent(activity,ScheduleActivity.class);
                    schedule.putExtra("Action","UPDATE");
                    schedule.putExtra("Id",txtScheduleId.getText().toString());
                    schedule.putExtra("Status","N");
                    schedule.putExtra("Date",date);
                    schedule.putExtra("EDate",edate);
                    schedule.putExtra("DisplayDate",day+"/"+month+"/"+year);
                    schedule.putExtra("DisplayEDate",eday+"/"+emonth+"/"+eyear);
                    schedule.putExtra("STime",StartTime);
                    schedule.putExtra("ETime",EndTime);
                    schedule.putExtra("DisplaySTime", finalDisplayStime);
                    schedule.putExtra("DisplayETime", finalDisplayEtime);
                    schedule.putExtra("Roll",Sl.getRoll());
                    activity.startActivity(schedule);
                    activity.finish();
                }
                else
                {
                    Toast.makeText(activity, "हा शेड्युल सुरु असल्याने तुम्ही तो बदलू शकत नाही..!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }
}
