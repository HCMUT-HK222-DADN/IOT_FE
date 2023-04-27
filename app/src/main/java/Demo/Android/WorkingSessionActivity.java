package Demo.Android;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class WorkingSessionActivity extends AppCompatActivityExtended {
    Button logout;
    //TextView countdown;
    int timeSelect = 0;
    int timeProcess = 0;
    long pauseOfset = 0;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    ProgressBar progressBar;
    CountDownTimer timeCoundown;
    ImageButton addbtn,resetBtn;
    Button startBtn;
    TextView addTimetv;
    boolean isStart = true;
 //   Timer timer = new Timer();
//    TimerTask timerTask = new TimerTask() {
//        int progress = 15;
//        @Override
//        public void run() {
//            if (progress > 0) {
//                progress--;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressBar.setProgress(progress);
//                        countdown.setText(String.valueOf(progress));
//                    }
//                });
//            }
//        }
//    };

    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workingsession);
        addbtn = (ImageButton) findViewById(R.id.btnAdd);
        logout = (Button) findViewById(R.id.logout);

        startBtn = (Button) findViewById(R.id.btnPlayPause);
        resetBtn = (ImageButton) findViewById(R.id.ib_reset);
       // addTimetv = (TextView)findViewById(R.id.tv_addTime);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeFunction();
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimerSetup();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTime();
            }
        });
//        addTimetv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addExtraTime();
//            }
//        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        // ---------------- Create Websocket
        webSocketManager = new WebSocketManager(WorkingSessionActivity.this);
        webSocketManager.start();

        // ---------------- Init View



        // ---------------- Init listener

//        startSession.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                timer.schedule(timerTask, 0, 1000);
//            }
//        });
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(WorkingSessionActivity.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
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
                        name="";
                        break;
                }
            }
        });

        //Dùng chức năng này phát triển module 2 - Cảnh báo giá trị vượt ngưỡng
        bottomNavigation.setCount(ID_NOTE,"4");
        bottomNavigation.show(ID_HOME,true);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        timer.cancel();
//        timerTask.cancel();
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timeCoundown!=null)
        {
            timeCoundown.cancel();
            timeProcess=0;
        }
    }
    //  ---------------- Addition Method
    private void startTimerSetup() {
        Button startBtn = findViewById(R.id.btnPlayPause);
        if (timeSelect > timeProcess) {
            if (isStart) {
                startBtn.setText("Pause");
                startTimer(pauseOfset);
                isStart = false;
            } else {
                isStart = true;
                startBtn.setText("Resume");
                timePause();
            }
        } else {
            Toast.makeText(this, "Enter Time", Toast.LENGTH_SHORT).show();
        }
    }

    public void LogOut() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    private void setTimeFunction() {
        final Dialog timeDialog = new Dialog(this);
        timeDialog.setContentView(R.layout.add_dialog);
        final EditText timeSet = timeDialog.findViewById(R.id.etGetTime);
        final TextView timeLeftTv = findViewById(R.id.tvTimeLeft);
        final Button btnStart = findViewById(R.id.btnPlayPause);
        final ProgressBar progressBar = findViewById(R.id.pbTimer);

        timeDialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timeSet.getText().toString().isEmpty()) {
                    Toast.makeText(WorkingSessionActivity.this, "Enter Time Duration", Toast.LENGTH_SHORT).show();
                } else {
                    resetTime();
                    timeLeftTv.setText(timeSet.getText().toString());
                    btnStart.setText("Start");
                    timeSelect = Integer.parseInt(timeSet.getText().toString());
                    progressBar.setMax(timeSelect);
                }
                timeDialog.dismiss();
            }
        });
        timeDialog.show();
    }

    private void timePause()
    {
        if (timeCoundown!=null)
        {
            timeCoundown.cancel();
        }
    }

    private void startTimer(long pauseOffSetL) {
        ProgressBar progressBar = findViewById(R.id.pbTimer);
        progressBar.setProgress(timeProcess);
        timeCoundown = new CountDownTimer((timeSelect * 1000L) - pauseOffSetL * 1000L, 1000) {
            @Override
            public void onTick(long p0) {
                timeProcess++;
                pauseOfset = timeSelect - p0 / 1000;

                progressBar.setProgress(timeSelect - timeProcess);


                TextView timeLeftTv = findViewById(R.id.tvTimeLeft);
                timeLeftTv.setText(String.valueOf((timeSelect - timeProcess)/60)+":"+String.valueOf((timeSelect - timeProcess)%60));
            }

            @Override
            public void onFinish() {
                resetTime();
                Toast.makeText(WorkingSessionActivity.this, "Times Up!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
    private void resetTime() {
        if (timeCoundown != null) {
            timeCoundown.cancel();
            timeProcess = 0;
            timeSelect = 0;
            pauseOfset = 0;
            timeCoundown = null;
            Button startBtn = findViewById(R.id.btnPlayPause);
            startBtn.setText("Start");
            isStart = true;
            ProgressBar progressBar = findViewById(R.id.pbTimer);
            progressBar.setProgress(0);
            TextView timeLeftTv = findViewById(R.id.tvTimeLeft);
            timeLeftTv.setText("0");
        }
    }
    private void addExtraTime() {
        ProgressBar progressBar = findViewById(R.id.pbTimer);
        if (timeSelect!= 0) {
            timeSelect += 15;
            progressBar.setMax(timeSelect);
            timePause();
            startTimer(pauseOfset);
            Toast.makeText(this, "15 seconds added", Toast.LENGTH_SHORT).show();
        }
    }


}
