package Demo.Android;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class Gate extends AppCompatActivityExtended {
    private WebSocketManager webSocketManager;

    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    private Button num1,num2,num3,num4,num5,num6,num7,num8,num9,num0,numOk,numdel;
    TextView PassPressed;
    String Pressed = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate);
        webSocketManager = new WebSocketManager(Gate.this);
        webSocketManager.start();
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(Gate.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch (item.getId()){
                    case ID_HOME:
                        name = "home";
                        moveToMain3();
                        break;
                    case ID_ACCOUNT:
                        name = "account";
                        break;
                    case ID_NOTE:
                        name = "notification";
                        moveToANotification();
                        break;
                    case ID_SETTING:
                        moveToSetting();

                        name = "setting";

                        break;
                    default:
                        name="Current";
                        break;
                }
            }
        });
        bottomNavigation.setCount(ID_NOTE,"4");
        //bottomNavigation.show(ID_SETTING,true);
        //
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        num5 = findViewById(R.id.num5);
        num6 = findViewById(R.id.num6);
        num7 = findViewById(R.id.num7);
        num8 = findViewById(R.id.num8);
        num9 = findViewById(R.id.num9);
        num0 = findViewById(R.id.num0);
        numdel = findViewById(R.id.numdel);
        numOk = findViewById(R.id.numOk);
        PassPressed = findViewById(R.id.passpressed);
        //Press Number
        num0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "0";
                PassPressed.setText(Pressed);
            }
        });
        num1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "1";
                PassPressed.setText(Pressed);
            }
        });
        num2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "2";
                PassPressed.setText(Pressed);
            }
        });
        num3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "3";
                PassPressed.setText(Pressed);
            }
        });
        num4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "4";
                PassPressed.setText(Pressed);
            }
        });
        num5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "5";
                PassPressed.setText(Pressed);
            }
        });
        num6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "6";
                PassPressed.setText(Pressed);
            }
        });
        num7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "7";
                PassPressed.setText(Pressed);
            }
        });
        num8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "8";
                PassPressed.setText(Pressed);
            }
        });
        num9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pressed += "9";
                PassPressed.setText(Pressed);
            }
        });
        numdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Pressed.equals("")) {
                    PassPressed.setText("");
                }
                else{
                Pressed = Pressed.substring(0, Pressed.length() - 1);
                PassPressed.setText(Pressed);}
            }
        });
        /// -----> Sử dụng string Pressed để nhận Password
        numOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // BE ....
            }
        });
    }
    public void moveToMain3(){
        Intent intent = new Intent(this, MainActivity3.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void moveToSetting(){
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void moveToANotification(){
        Intent intent = new Intent(this, Notification.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
}