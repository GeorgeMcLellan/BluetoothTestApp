package com.example.george.bluetoothtestapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int ENABLE_REQUEST = 1;
    private BluetoothAdapter mBluetoothAdapter;

    private static final String TAG = "MainActivity";


    String deviceAddress = null;
    String deviceName = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null){
            if (!mBluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_REQUEST);
                Log.d(TAG, "Starting intent");
            }
        }else{
            Log.d(TAG, "mBluetoothAdapter is null");
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){
            // There are paired devices. Get the name and deviceAddress of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                deviceName = device.getName();
                deviceAddress = device.getAddress(); // MAC deviceAddress
                Log.d(TAG, "Device Paired : "+deviceName+" ... "+deviceAddress);

                new ConnectBT().execute();
                Log.d(TAG, "Executing ConnectBT Async Task");
            }
        }

        ToggleButton buttonToggle = (ToggleButton) findViewById(R.id.toggleButton);

        buttonToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try{
                    if (b){
                        btSocket.getOutputStream().write("n".getBytes());
                        Log.d(TAG, "Turning ON");
                    }else{
                        btSocket.getOutputStream().write("f".getBytes());
                        Log.d(TAG, "Turning OFF");

                    }
                }catch (IOException e){
                    Log.d(TAG, "IOException : " + e.getMessage());
                    msg("IOException");
                }
            }
        });

    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);//connects to the device's deviceAddress and checks if it's available
                    btSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                    Log.d(TAG,"doInBackground: starting connection...");
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
                Log.d(TAG, "IOException : "+e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                Log.d(TAG, "onPostExecute : Connection Failed");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
        }
    }








}
