package com.esipe.tpiotesp32;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class SetupEsp32Activity extends AppCompatActivity {
    Button btnsendWifi, btnsendTemperature, btnsendInterval;
    EditText edt_interval, edt_ssid, edt_password, edt_temperature;
    Set<BluetoothDevice> devices;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;

    /**
     * the MAC address for the chosen device
     */
    String macAdresseDevice = null;

    private ProgressDialog progressDialog;
    BluetoothAdapter myBluetoothAdapter = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it'
    //This the SPP for the arduino(AVR)
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private int newConnectionFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            macAdresseDevice = getIntent().getStringExtra("MAC_ADRESSE_DEVICE");

        setContentView(R.layout.activity_setup_esp32);
        btnsendWifi = findViewById(R.id.btn_send_resau);
        btnsendTemperature = findViewById(R.id.btn_send_temperature);
        btnsendInterval = findViewById(R.id.btn_send_interval);
        edt_interval = findViewById(R.id.edt_interval);
        edt_ssid = findViewById(R.id.edt_ssid);
        edt_password = findViewById(R.id.edt_password);
        edt_temperature = findViewById(R.id.edt_temperature_alert);


        btnsendWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ssidSTRING = edt_ssid.getText().toString();
                String psswString = edt_password.getText().toString();
                String message = "1:"+ssidSTRING+":"+psswString+"*";
                System.out.println(message);
                sendData(message);

            }
        });
        btnsendInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String interval= edt_interval.getText().toString();
                int intervalInt =  Integer.parseInt(interval.trim()) *1000;
                String message = "2:"+intervalInt+"*";
                sendData(message);
            }
        });
        btnsendTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAlert= edt_temperature.getText().toString();
                String message = "3:"+tempAlert+"*";
                sendData(message);
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        newConnectionFlag++;
        if (macAdresseDevice != null) {
            //call the class to connect to bluetooth
            if (newConnectionFlag == 1) {
                new ConnectBT().execute();
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute() {

            //show a progress dialog
            progressDialog = ProgressDialog.show(SetupEsp32Activity.this,
                    "Connecting...", "Please wait!!!");
        }

        //while the progress dialog is shown, the connection is done in background
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (btSocket == null || !isBtConnected) {
                    //get the mobile bluetooth device
                    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    //connects to the device's address and checks if it's available
                    BluetoothDevice bluetoothDevice = myBluetoothAdapter.getRemoteDevice(macAdresseDevice);

                    //create a RFCOMM (SPP) connection

                    btSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    //start connection

                    btSocket.connect();
                }

            } catch (IOException e) {
                //if the try failed, you can check the exception here
                System.out.println(e.getMessage());
                connectSuccess = false;
            }

            return null;
        }
        //after the doInBackground, it checks if everything went fine
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("LOG_TAG", connectSuccess + "");
            if (!connectSuccess) {
                showToast("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                isBtConnected = true;
                showToast("Connected");
            }
            progressDialog.dismiss();
        }
    }
    /**
     * used to send data to the micro controller
     *
     * @param data the data that will send prefer to be one char
     */
    private void sendData(String data) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(data.getBytes());
            } catch (IOException e) {
                showToast("Error");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
