package Demo.Android;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class WorkingSessionActivity extends AppCompatActivityExtended {
    ListView list_view;
    List<SessionData> dataList;
    SessionAdapter adapter;
    Button logout;
    int timeSelect = 0;
    int timeProcess = 0;
    long pauseOfset = 0;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    CountDownTimer timeCountdown;
    ImageButton addbtn, resetBtn;
    Button startBtn;
    TextView timeLeftTv, sessionStatus;
    ProgressBar progressBar;
    boolean isRunning = false;
    SimpleDateFormat intervalFormat = new SimpleDateFormat("HH:mm");
    TimeZone gmt = TimeZone.getTimeZone("GMT");
    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workingsession);

        // ---------------- Create object to handle button
        addbtn = (ImageButton) findViewById(R.id.btnAdd);
        logout = (Button) findViewById(R.id.logout);

        startBtn = (Button) findViewById(R.id.btnPlayPause);
        resetBtn = (ImageButton) findViewById(R.id.ib_reset);
        timeLeftTv = (TextView) findViewById(R.id.tvTimeLeft);
        sessionStatus = (TextView) findViewById(R.id.sessionStatus);
        progressBar = findViewById(R.id.pbTimer);
        intervalFormat.setTimeZone(gmt);

        list_view = findViewById(R.id.list_view);

        // ---------------- Create Websocket
        webSocketManager = new WebSocketManager(WorkingSessionActivity.this);
        webSocketManager.start();

        // ---------------- Init View

        // ---------------- Init List
        dataList = new ArrayList<>();
        dataList.add(new SessionData("Study", "00:01"));
        dataList.add(new SessionData("Rest", "00:01"));
        adapter = new SessionAdapter(this, dataList);
        list_view.setAdapter(adapter);
        list_view.setChoiceMode(ListView.CHOICE_MODE_NONE);

        // ---------------- Init timeCountdown
        resetTime();
        initTimeCountDown();

        // ---------------- Init listener
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimerSetup();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skip();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timeCountdown!=null)
        {
            timeCountdown.cancel();
            timeProcess=0;
        }
    }
    //  ---------------- Addition Method
    @SuppressLint("SetTextI18n")
    private void initTimeCountDown() {
        // Pop the first item of list
        if (dataList.size() == 0) return;
        SessionData objectData = dataList.get(0);
        String sessionStatusString = objectData.getType();
        String sessionIntervalString = objectData.getInterval();
        Log.w("WorkingSessionActivity", sessionIntervalString);
        dataList.remove(0);
        adapter.notifyDataSetChanged();
        // Set view items and number
        int totalSeconds = 0;
        try {
            Date interval = intervalFormat.parse(sessionIntervalString);
            totalSeconds = (int) (interval.getTime() / 1000);
            Log.w("WorkingSessionActivity", String.valueOf(totalSeconds));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int totalMinutes = totalSeconds / 60;
        Log.w("WorkingSessionActivity", "totalMinutes: " + String.valueOf(totalMinutes));
        String minuteString = Integer.toString(totalMinutes) + ":00";
        Log.w("WorkingSessionActivity", "timeLeftTv: " + minuteString);
        int finalTotalSeconds = totalSeconds;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeProcess = 0;
                pauseOfset = 0;
                sessionStatus.setText(sessionStatusString);
                timeLeftTv.setText(minuteString);
                startBtn.setText("Start");
                timeSelect = finalTotalSeconds;
                progressBar.setMax(timeSelect);
            }
        });
        // Run Countdown if it is running status
        Log.w("WorkingSessionActivity", "isRunning: " + String.valueOf(isRunning));
        if (isRunning) {
            Log.w("WorkingSessionActivity", "Countdown continue");
            startBtn.setText("Pause");
            startTimer(pauseOfset);
        }
    }
    private void startTimerSetup() {
        Button startBtn = findViewById(R.id.btnPlayPause);
        if (timeSelect > timeProcess) {
            if (!isRunning) {
                startBtn.setText("Pause");
                startTimer(pauseOfset);
                isRunning = true;
            } else {
                isRunning = false;
                startBtn.setText("Resume");
                timePause();
            }
        } else {
            Toast.makeText(this, "Enter Time", Toast.LENGTH_SHORT).show();
        }
    }
    private void timePause()
    {
        if (timeCountdown!=null)
        {
            timeCountdown.cancel();
        }
    }
    private void startTimer(long pauseOffSetL) {
        ProgressBar progressBar = findViewById(R.id.pbTimer);
        progressBar.setProgress(timeProcess);
        timeCountdown = new CountDownTimer((timeSelect * 1000L) - pauseOffSetL * 1000L, 1000) {
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
                Toast.makeText(WorkingSessionActivity.this, "Finish one session!", Toast.LENGTH_SHORT).show();
                initTimeCountDown();
            }
        }.start();
    }
    private void resetTime() {
        if (timeCountdown != null) {
            timeCountdown.cancel();
            timeProcess = 0;
            timeSelect = 0;
            pauseOfset = 0;
            timeCountdown = null;
            startBtn.setText("Start");
            isRunning = false;
            progressBar.setProgress(0);
            timeLeftTv.setText("0");
        }
    }
    private void skip() {
        if (timeCountdown != null) {
            timeCountdown.cancel();
            timeCountdown = null;
        }
        if (dataList.size() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startBtn.setText("Start");
                    isRunning = false;
                    progressBar.setProgress(0);
                    timeLeftTv.setText("0");
                }
            });
        } else {
            initTimeCountDown();
        }

    }
    public void parsingSessionList() {

    }
    public void LogOut() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
}
