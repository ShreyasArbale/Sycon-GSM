package com.nexzen.sycongsm;

public class Schedule {

    int ScheduleId;
    String ScheduleDate;
    String ScheduleEndDate;
    String StartTime;
    String EndTime;
    String Roll;
    String Status;
    public Schedule(){}

    public Schedule(int id, String date, String e_date, String s_time,String e_time,String s,String r) {
        ScheduleId = id;
        ScheduleDate = date;
        ScheduleEndDate = e_date;
        StartTime = s_time;
        EndTime = e_time;
        Status = s;
        Roll = r;
    }
    public int getScheduleId() {
        return ScheduleId;
    }

    public void setScheduleId(int scheduleId) {
        ScheduleId = scheduleId;
    }

    public String getScheduleDate() {
        return ScheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        ScheduleDate = scheduleDate;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getRoll() {
        return Roll;
    }

    public void setRoll(String roll) {
        Roll = roll;
    }

    public String getScheduleEndDate() {
        return ScheduleEndDate;
    }

    public void setScheduleEndDate(String scheduleEndDate) {
        ScheduleEndDate = scheduleEndDate;
    }
}
