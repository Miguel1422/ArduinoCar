package com.miguel.arduinocar;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn = findViewById(R.id.button);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }

    private void test() {

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
            public void error(Exception ex) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }).execute("20:18:04:15:44:74");
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
