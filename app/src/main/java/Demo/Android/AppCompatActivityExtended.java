package Demo.Android;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.List;

public class AppCompatActivityExtended extends AppCompatActivity {
    static protected int userID;
    static protected int notiNum;
    static protected WorkingScheduleData selectedWorkingScheduleData;
    public void updateSensorValue(JSONObject jsonObject) {}
    public void updateHumiValue(JSONObject jsonObject) {}
    public void updateLightValue(JSONObject jsonObject) {}
    public void updateTempValue(JSONObject jsonObject) {}
    public void updateMotionValue(JSONObject jsonObject) {}
    public void updateDeviceScheduleList(JSONObject jsonObject) {}
    public void updateWorkingScheduleList(JSONObject jsonObject) {}
    public void updateServerStatus(String noti) {}
    public void updateAssignDevice(JSONObject jsonObject) {}
    public void login(JSONObject jsonObject) {}
    public void deviceControl(JSONObject jsonObject) {}
    public void deviceInit(JSONObject jsonObject) {}
    public void deviceControlAuto(JSONObject jsonObject) {}
}
