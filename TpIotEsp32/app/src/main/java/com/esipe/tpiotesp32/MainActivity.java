package com.esipe.tpiotesp32;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MaterialButton btnActivateBluetooth,btnScan;
    ListView listDevice;
    private ListAdapter mAdapter;
    private ArrayList<BluetoothDevice> mDeviceList;
    IntentFilter filter;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 100;
    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    ((TextView) new AlertDialog.Builder(this)
                            .setTitle("Runtime Permissions up ahead")
                            .setMessage(Html.fromHtml("<p>To find nearby bluetooth devices please click \"Allow\" on the runtime permissions popup.</p>"))
                            .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_ACCESS_COARSE_LOCATION);
                                    }
                                }
                            })
                            .show()
                            .findViewById(android.R.id.message))
                            .setMovementMethod(LinkMovementMethod.getInstance());       // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
            }
        }

        mAdapter= new ListAdapter(getApplicationContext());
        // On cherche à récupérer l'interface bluetooth du périphérique Android
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listDevice=findViewById(R.id.listDevice);
        // Si pas de module (d'interface) bluetooth sur le périphérique ...
        if ( bluetoothAdapter == null ) {
            Toast.makeText(
                    this,
                    "Bluetooth not supported on this deveice",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Si le bluetooth n'est pas activé, on propose de l'activer
        /*if ( ! bluetoothAdapter.isEnabled() ) {
            // Demande à activer l'interface bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/
        btnActivateBluetooth = findViewById( R.id.btnOnBluetooth );

        btnActivateBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();

                } else {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    startActivityForResult(intent, 1000);
                }
            }
        });

        // On enregistre un gestionnaire d'événements sur le bouton
        btnScan = findViewById( R.id.btnScan );
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                bluetoothAdapter.startDiscovery();
            }
        });
        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showToast("Enabled");
                }
                if (state == BluetoothAdapter.STATE_OFF) {
                    showToast("Disabled");
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();


            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                showToast("Scan finished");
                mAdapter.setData(mDeviceList);
                listDevice.setAdapter(mAdapter);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDeviceList.contains(device) && device!=null){
                    if(device.getName()!=null) {
                        mDeviceList.add(device);
                    }
                }
            }else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    showToast("Paired");
                    String mac=mAdapter.getMacadress();
                    System.out.println("mac1:"+ mac);
                    Intent myIntent = new Intent(getApplicationContext() , HomeMenuActivity.class);
                    myIntent.putExtra("MAC_ADRESSE_DEVICE", mac);
                    startActivity(myIntent);
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    showToast("Unpaired");
                }

            }
        }
    };


}
