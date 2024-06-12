package com.nexzen.sycongsm;

public class Users {


    public Users(){}

    public Users(int customerId, String deviceMobileNo, String userMobileNo) {
        CustomerId = customerId;
        DeviceMobileNo = deviceMobileNo;
        UserMobileNo = userMobileNo;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public String getDeviceMobileNo() {
        return DeviceMobileNo;
    }

    public void setDeviceMobileNo(String deviceMobileNo) {
        DeviceMobileNo = deviceMobileNo;
    }

    public String getUserMobileNo() {
        return UserMobileNo;
    }

    public void setUserMobileNo(String userMobileNo) {
        UserMobileNo = userMobileNo;
    }

    //private variables
    int CustomerId;
    String DeviceMobileNo;
    String UserMobileNo;



}
