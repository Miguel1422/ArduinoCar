package com.miguel.arduinocar;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private BluetoothSocket btSocket;
    private Button btnConectar;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText txtMac = findViewById(R.id.txtMac);
        btnConectar = findViewById(R.id.btnConectar);
        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mac = txtMac.getText().toString();
                connectBt(mac);
            }
        });


        Joystick joy = findViewById(R.id.joystick);

        joy.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {

            }

            @Override
            public void onDrag(float degrees, float offset) {
                char[] toSend = {'6', '3', '2', '1', '4', '7', '8', '9'};
                int val = ((int) degrees + 360) % 360;
                val = (val + 22) % 360;
                val = val / 45;
                Log.d(TAG, "Value to send " + toSend[val]);
                sendBt(toSend[val] + "");
            }

            @Override
            public void onUp() {
                Log.d(TAG, "Stopping car");
                sendBt("w");
            }
        });

    }

    private void connectBt(String mac) {
        btnConectar.setEnabled(false);
        new ConnectBT(new ConnectBT.OnConnectListener() {
            @Override
            public void success(BluetoothSocket btSocket) {
                MainActivity.this.btSocket = btSocket;
                Toast.makeText(MainActivity.this, "Bien", Toast.LENGTH_LONG).show();
                try {
                    btSocket.getOutputStream().write("b".getBytes());
                    Toast.makeText(MainActivity.this, "Modo bluetooth", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new ConnectBT.OnErrorListener() {
            @Override
            public void error(String ex) {
                Toast.makeText(MainActivity.this, "Error " + ex, Toast.LENGTH_LONG).show();
                btnConectar.setEnabled(true);
            }
        }).execute(mac);
    }

    private void sendBt(String content) {
        if (btSocket == null) return;
        try {
            Log.d("Sending", "Sending: " + content);
            btSocket.getOutputStream().write(content.getBytes());
        } catch (Exception e) {
            Log.e("Sending", "Failed to send : " + content + "----> " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            btSocket.close();
            btSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
