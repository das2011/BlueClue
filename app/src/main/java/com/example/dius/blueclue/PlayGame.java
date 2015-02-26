package com.example.dius.blueclue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class PlayGame extends ActionBarActivity {

    TextView statusText;
    BluetoothAdapter bluetoothAdapter;
    Spinner devicesSpinner;
    ArrayAdapter<DeviceWrapper> dataAdapter;
    List<DeviceWrapper> list = new ArrayList<>();

    class DeviceWrapper {

        private BluetoothDevice device;

        public DeviceWrapper(BluetoothDevice device) {
            this.device = device;
        }

        @Override
        public String toString() {
            String label = device.getName();
            if(label == null || label.trim().length() == 0) {
                label = "Untitled";
            }
            return label + " (" + device.getAddress() + ")";
        }

        public BluetoothDevice getDevice() {
            return device;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        Button findGamesButton = (Button)findViewById(R.id.findGamesButton);

        statusText = (TextView)findViewById(R.id.statusText);
        devicesSpinner = (Spinner)findViewById(R.id.devices);

        //list.add("-Choose device-");

        dataAdapter = new ArrayAdapter<DeviceWrapper>(this, android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devicesSpinner.setAdapter(dataAdapter);


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        findGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Got a click on the FIND GAMES button!");
                bluetoothAdapter.startDiscovery();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                statusText.setText("device found: " + device.getName() + "(" + device.getAddress() + ")");
                list.add(new DeviceWrapper(device));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            }
        }
    };

}

