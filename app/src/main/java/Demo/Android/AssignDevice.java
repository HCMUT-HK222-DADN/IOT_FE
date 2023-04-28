package Demo.Android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AssignDevice extends AppCompatActivityExtended  {
    Button timeset, dateset, logout, set;
    private WebSocketManager webSocketManager;
    private DatePickerDialog datePickerDialog;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    String[] itemDevice = {"Light", "Fan"};
    String[] ValueFan = {"0", "20", "40", "60", "80", "100"};
    String[] ValueButton = {"On", "Off"};
    String[] valueDevice = {};
    String selectedDevice, selectedValue;
    String selectedDatetime, selectedDate, selectedTime;

    Spinner Device, DeviceValue, DeviceId;
    ArrayAdapter<String> adapterItemDevice, adapterValue;
    int hour,minute;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    private int RestTimeSet = 300; //Count by seconds
    @Override
    protected void onCreate(Bundle savedInstanceState){
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviceschedule);

        // ----------------  object to handle button
        Device = findViewById(R.id.DeviceDrop);
        DeviceValue = findViewById(R.id.ValueDeviceDrop);
        set = findViewById(R.id.set);

        // ---------------- Init web socket
        webSocketManager = new WebSocketManager(AssignDevice.this);
        webSocketManager.start();

        // ---------------- Init UI
        adapterItemDevice = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemDevice);
        adapterItemDevice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Device.setAdapter(adapterItemDevice);

        adapterValue = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, valueDevice);
        adapterValue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DeviceValue.setAdapter(adapterValue);

        dateset  = findViewById(R.id.DateSetup);
        timeset  = findViewById(R.id.TimeSetup);
        long instance = new Date().getTime();
        TextView suggestion = (TextView) findViewById(R.id.suggestion);
        LinearLayout show_hide = (LinearLayout) findViewById(R.id.show_hide);
        Button back = (Button) findViewById(R.id.back);

        TextView restView = (TextView) findViewById(R.id.resttime);
        TextView rest = (TextView) findViewById(R.id.rest);

        Button b10min = (Button) findViewById(R.id.b10min);
        Button b15min = (Button) findViewById(R.id.b15min);
        Button b20min = (Button) findViewById(R.id.b20min);

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
                Toast.makeText(AssignDevice.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
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
                        backtoWorkmain();
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
        // Dùng chức năng này phát triển module 2 - Cảnh báo giá trị vượt ngưỡng
        bottomNavigation.setCount(ID_NOTE,"4");
        bottomNavigation.show(ID_SETTING,true);

        // ---------------- Init Listener
        Device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Update the Value rollbar according to the device rollbar
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(position==0 || position == 2){
//                            valueDevice = ValueButton.clone();
//                        }
//                        if(position==1){
//                            valueDevice = ValueFan.clone();
//                        }
//                        adapterValue.notifyDataSetChanged();
//                    }
//                });
                if(position==0 || position == 2){
                    adapterValue = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,ValueButton);
                }
                if(position==1){
                    adapterValue = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,ValueFan);
                }
                DeviceValue.setAdapter(adapterValue);
                // Update the device type that being selected
                selectedDevice = adapterView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        DeviceValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedValue = adapterView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        // Set up Show/Hide Rest Interval
        RestTimeSet = 300;
        suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hideflag = show_hide.getVisibility();
                if(hideflag == View.VISIBLE){
                    show_hide.setVisibility(View.GONE);
                    RestTimeSet = 300;
                    int min = RestTimeSet/60;
                    int second = RestTimeSet%60;
                    restView.setText(String.valueOf(min)+":"+String.valueOf(second)+"0");
                    rest.setText("Rest Time (Default):");
                    suggestion.setText("Click here to view more Suggestion");

                }
                else {
                    suggestion.setText("Close suggestion");
                    show_hide.setVisibility(View.VISIBLE);
                }
            }
        });
        b10min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestTimeSet = 600;
                int min = RestTimeSet/60;
                int second = RestTimeSet%60;
                restView.setText(String.valueOf(min)+":"+String.valueOf(second)+"0");
                rest.setText("Rest Time (Chosen from suggest):");
            }
        });
        b15min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestTimeSet = 900;
                int min = RestTimeSet/60;
                int second = RestTimeSet%60;
                restView.setText(String.valueOf(min)+":"+String.valueOf(second)+"0");
                rest.setText("Rest Time (Chosen from suggest):");
            }
        });
        b20min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestTimeSet = 1200;
                int min = RestTimeSet/60;
                int second = RestTimeSet%60;
                restView.setText(String.valueOf(min)+":"+String.valueOf(second)+"0");
                rest.setText("Rest Time (Chosen from suggest):");
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "RequestDeviceTimerBook");
                    jsonObject.put("Device", selectedDevice);
                    jsonObject.put("Value", Integer.parseInt(selectedValue));
                    jsonObject.put("TimeStart", selectedDatetime);
                    sendMessage(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // Logout to first page
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        // Back to Working Activity
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
    public void popTimePicker(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectHour, int selectMin) {
                hour = selectHour;
                minute = selectMin;
                timeset.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style, onTimeSetListener,hour,minute,true);
        timePickerDialog.setTitle("Time Setup");
        timePickerDialog.show();
    }
    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
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
    private String makeDateString(int day, int month, int year) {
        return day +" / "+  getMonthFormat(month)+ " / " + year;
    }
    private String makeSelectedDate(int day, int month, int year) {
        return year + "-" + month + "-" + day;
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
}