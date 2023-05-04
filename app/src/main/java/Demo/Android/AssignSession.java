package Demo.Android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class AssignSession extends AppCompatActivityExtended  {
    Button dateset, logout, set;
    Button TimeStartSetup, TimeEndSetup;
    TextView resttime, worktime;
    private WebSocketManager webSocketManager;
    private DatePickerDialog datePickerDialog;
    String selectedDate;
    String selectedDatetimeStart, selectedTimeStart;
    String selectedDatetimeEnd, selectedTimeEnd;
    String selectedTimeWorkInter, selectedTimeRestInter;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    private int RestTimeSet = 300;//Count by seconds
    @Override
    protected void onCreate(Bundle savedInstanceState){
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_session);

        // ----------------  object to handle button
        set = findViewById(R.id.set);

        // ---------------- Create Websocket
        webSocketManager = new WebSocketManager(AssignSession.this);
        webSocketManager.start();

        // ---------------- Init view
        dateset  = findViewById(R.id.DateSetup);
        TimeStartSetup  = findViewById(R.id.TimeStartSetup);
        TimeEndSetup = findViewById(R.id.TimeEndSetup);
        long instance = new Date().getTime();

        Button back = (Button) findViewById(R.id.back);

        resttime = findViewById(R.id.resttime);
        worktime = findViewById(R.id.worktime);

        logout = (Button) findViewById(R.id.logout);
        initDatePicker();
        dateset.setText(getToday());

        // ---------------- Set up Bottom Bar
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(AssignSession.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
                switch (item.getId()){
                    case ID_HOME:
                        gotoMainActivity3();
                        break;
                    case ID_SETTING:
                        gotoWorkingActivity();
                        break;
                    default:
                        break;
                }
            }
        });
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch (item.getId()){
                    case ID_HOME:
                        name = "home";
                        break;
                    case ID_ACCOUNT:
                        name = "account";
                        break;
                    case ID_NOTE:
                        name = "notification";
                        break;
                    case ID_SETTING:
                        name = "setting";
                        break;
                    default:
                        name="Current";
                        current();
                        break;
                }
            }
        });
        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                switch (item.getId()){
                    case ID_HOME:
                        gotoMainActivity3();
                        break;
                    case ID_SETTING:
                        gotoWorkingActivity();
                        break;
                    default:
                        break;
                }
            }
        });
        //Dùng chức năng này phát triển module 2 - Cảnh báo giá trị vượt ngưỡng
        bottomNavigation.setCount(ID_NOTE,"4");
        bottomNavigation.show(ID_SETTING,true);


        // ---------------- Init listener
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDatetimeStart = selectedDate + " " + selectedTimeStart;
                selectedDatetimeEnd = selectedDate + " " + selectedTimeEnd;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "RequestScheduleBook");
                    jsonObject.put("UserID", 1);
                    jsonObject.put("time_start", selectedDatetimeStart);
                    jsonObject.put("time_end", selectedDatetimeEnd);
                    jsonObject.put("work_inter", selectedTimeWorkInter);
                    jsonObject.put("rest_inter", selectedTimeRestInter);
                    sendMessage(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                current();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backtoWorkmain();
            }
        });
    }

    // ---------------- Addition Method
    public String getToday(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        month = month + 1 ;
        return makeDateString(day,month,year);
    }
    public void popStartTimePicker(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectHour, int selectMin) {
                TimeStartSetup.setText(String.format(Locale.getDefault(), "%02d:%02d", selectHour, selectMin));
                selectedTimeStart = makeSelectedTime(selectHour, selectMin);
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style, onTimeSetListener,0,0,true);
        timePickerDialog.setTitle("Time Start");
        timePickerDialog.show();
    }
    public void popEndTimePicker(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectHour, int selectMin) {
                TimeEndSetup.setText(String.format(Locale.getDefault(), "%02d:%02d", selectHour, selectMin));
                selectedTimeEnd = makeSelectedTime(selectHour, selectMin);
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style, onTimeSetListener,0,0,true);
        timePickerDialog.setTitle("Time Start");
        timePickerDialog.show();
    }
    public void popWorkInterPicker(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectHour, int selectMin) {
                worktime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectHour, selectMin));
                selectedTimeWorkInter = makeSelectedTime(selectHour, selectMin);
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style, onTimeSetListener,0,0,true);
        timePickerDialog.setTitle("Work Interval");
        timePickerDialog.show();
    }
    public void popRestInterPicker(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectHour, int selectMin) {
                resttime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectHour, selectMin));
                selectedTimeRestInter = makeSelectedTime(selectHour, selectMin);
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style, onTimeSetListener,0,0,true);
        timePickerDialog.setTitle("Rest Interval");
        timePickerDialog.show();
    }
    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day,month,year);
                dateset.setText(date);
                selectedDate = makeSelectedDate(day, month, year);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this,style,dateSetListener,year,month,day);
    }
    private String makeDateString(int day,int month,int year){
        return day +" / "+  getMonthFormat(month)+ " / " + year;
    }
    private String makeSelectedDate(int day, int month, int year) {
        return String.format("%d-%02d-%02d", year, month, day);
    }
    private String makeSelectedTime(int hour, int minute) {
        return String.format("%02d:%02d:%02d", hour, minute, 0);
    }
    private String getMonthFormat(int month) {
        if(month == 1) return "thg 1";
        if(month == 2) return "thg 2";
        if(month == 3) return "thg 3";
        if(month == 4) return "thg 4";
        if(month == 5) return "thg 5";
        if(month == 6) return "thg 6";
        if(month == 7) return "thg 7";
        if(month == 8) return "thg 8";
        if(month == 9) return "thg 9";
        if(month == 10) return "thg 10";
        if(month == 11) return "thg 11";
        if(month == 12) return "thg 12";
        return "jan";
    }
    public void openDatePicker(View view){
        datePickerDialog.show();
    }
    public void sendMessage(JSONObject jsonObject) {
        this.webSocketManager.sendMessage(jsonObject);
    }
    public void LogOut() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void backtoWorkmain() {
        Intent intent = new Intent(this, WorkingActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void current() {
        Intent intent = new Intent(this, AssignSession.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoMainActivity3() {
        Intent intent = new Intent(this, MainActivity3.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoWorkingActivity() {
        Intent intent = new Intent(this, WorkingActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
}