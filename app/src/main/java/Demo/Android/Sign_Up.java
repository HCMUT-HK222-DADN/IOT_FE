package Demo.Android;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class Sign_Up extends AppCompatActivityExtended  {
    private WebSocketManager webSocketManager;
    private TextView serverStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // ---------------- Create Websocket
        webSocketManager = new WebSocketManager(Sign_Up.this);
        webSocketManager.start();

        // -------------- Create buttons and views
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        EditText ConfirmPassword = (EditText) findViewById(R.id.confirmpassword);
        Button Signup = (Button) findViewById(R.id.Signup);
        Button return_login = (Button) findViewById(R.id.Return_login);
        serverStatus = findViewById(R.id.serverStatus);
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To do BE work !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        });
        return_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Return_login();
                //To do BE work !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        });
        // ------- Create new websocket


        // -------- Create listener
//        return_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Return_login();
//            }
//        });
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    // ---------------------------- Additional method
    public void Return_login() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void updateServerStatus(String noti) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverStatus.setText(noti);
            }
        });
    }
}
