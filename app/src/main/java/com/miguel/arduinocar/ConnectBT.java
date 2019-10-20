package com.miguel.arduinocar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;

public class ConnectBT extends AsyncTask<String, Void, BluetoothSocket> {
    private OnConnectListener listener;
    private OnErrorListener errorListener;

    public interface OnConnectListener {
        void success(BluetoothSocket btSocket);
    }
    public interface OnErrorListener {
        void error(Exception ex);
    }

    private boolean connectSuccess = true; //if it's here, it's almost connected

    public ConnectBT(OnConnectListener listener, OnErrorListener errorListener) {

        this.listener = listener;
        this.errorListener = errorListener;
    }
    public ConnectBT(OnConnectListener listener) {
        this(listener, null);
    }

    @Override
    protected BluetoothSocket doInBackground(String... strings) {
        try {
            String address = strings[0];
            BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
            BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
            BluetoothSocket btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));//create a RFCOMM (SPP) connection
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            btSocket.connect();//start connection
            return btSocket;
        } catch (IOException e) {
            connectSuccess = false;//if the try failed, you can check the exception here
            if (errorListener != null) errorListener.error(e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        super.onPostExecute(bluetoothSocket);
        if (listener != null && bluetoothSocket != null) {
            listener.success(bluetoothSocket);
        }

    }

    @Override
    protected void onPreExecute() {
    }
}