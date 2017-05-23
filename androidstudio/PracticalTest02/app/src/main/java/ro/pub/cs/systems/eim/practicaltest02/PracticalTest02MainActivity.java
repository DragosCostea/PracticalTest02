package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    // Server widgets
    private EditText serverPortEditText = null;
    private Button startServerButton = null;
    private Button stopServerButton = null;

    // Client widgets

    private Button setButton = null;
    private Button resetButton = null;
    private Button pollButton = null;
    private EditText enterDateEditText = null;
    private TextView showResultTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    int server_status = 0;

    private ServerButton serverButtonClickListener = new ServerButton();
    private class ServerButton implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            if (server_status == 1) {
                if (serverThread != null) {
                    serverThread.stopThread();
                }
                server_status = 0;
            }
            else {

                String serverPort = serverPortEditText.getText().toString();
                if (serverPort == null || serverPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                serverThread = new ServerThread(Integer.parseInt(serverPort));
                if (serverThread.getServerSocket() == null) {
                    Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                    return;
                }
                serverThread.start();

                server_status = 1;
            }
        }

    }


    private SetButton setButtonClickListener = new SetButton();
    private class SetButton implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = enterDateEditText.getText().toString();
            String clientPort = serverPortEditText.getText().toString();

            if (date == null || date.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            String clientAddress = "127.0.0.1";
            clientThread = new ClientThread(
                    clientAddress, 0 , date, showResultTextView,  Integer.parseInt(clientPort)
            );
            clientThread.start();
        }

    }


    private ResetButton resetButtonClickListener = new ResetButton();
    private class ResetButton implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = enterDateEditText.getText().toString();
            String clientPort = serverPortEditText.getText().toString();

            if (date == null || date.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            String clientAddress = "127.0.0.1";
            clientThread = new ClientThread(
                    clientAddress, 1 , date, showResultTextView,  Integer.parseInt(clientPort)
            );
            clientThread.start();
        }

    }


    private PollButton pollButtonClickListener = new PollButton();
    private class PollButton implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = enterDateEditText.getText().toString();
            String clientPort = serverPortEditText.getText().toString();

            if (date == null || date.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            String clientAddress = "127.0.0.1";
            clientThread = new ClientThread(
                    clientAddress, 2 , date, showResultTextView,  Integer.parseInt(clientPort)
            );
            clientThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");


        serverPortEditText = (EditText)findViewById(R.id.enter_port);
        startServerButton = (Button)findViewById(R.id.start_server_button);
        startServerButton.setOnClickListener(serverButtonClickListener);
        stopServerButton = (Button)findViewById(R.id.stop_server_button);
        stopServerButton.setOnClickListener(serverButtonClickListener);


        setButton = (Button)findViewById(R.id.set_button);
        setButton.setOnClickListener(setButtonClickListener);

        resetButton = (Button)findViewById(R.id.reset_button);
        resetButton.setOnClickListener(resetButtonClickListener);

        pollButton = (Button)findViewById(R.id.poll_button);
        pollButton.setOnClickListener(pollButtonClickListener);

        enterDateEditText = (EditText)findViewById(R.id.enter_date);

        showResultTextView = (TextView)findViewById((R.id.result_here_text_view));
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
