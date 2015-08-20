package com.example.dius.blueclue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class PlayGame extends ActionBarActivity {

    TextView statusText;
    BluetoothAdapter bluetoothAdapter;
    Spinner devicesSpinner;
    ArrayAdapter<DeviceWrapper> dataAdapter;
    List<DeviceWrapper> list = new ArrayList<>();
    boolean finding = false;
    Button findGamesButton;
    Button findableButton;
    BluetoothChatService bluetoothService;
    Handler bluetoothEventHandler;
    String connectedDeviceName;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DeviceWrapper that = (DeviceWrapper) o;

            if (!device.equals(that.device)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return device.hashCode();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        findGamesButton = (Button)findViewById(R.id.findGamesButton);
        findableButton = (Button)findViewById(R.id.findableButton);

        findableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("-----> " + v);
                findableButton.setText("FINDABLE!");
                System.out.println("is bluetooth enabled: " + bluetoothAdapter.isEnabled());

                Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
        });

        statusText = (TextView)findViewById(R.id.statusText);
        devicesSpinner = (Spinner)findViewById(R.id.devices);

        findGamesButton = (Button)findViewById(R.id.findGamesButton);

        dataAdapter = new ArrayAdapter<DeviceWrapper>(this, android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devicesSpinner.setAdapter(dataAdapter);
        devicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Item selected!: " + position);
                DeviceWrapper device = dataAdapter.getItem(position);
                System.out.println("selected item: " + device.getDevice().getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("nothing!!!! TRA LA LA!");
            }
        });

        bluetoothEventHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                System.out.println("msg: " + msg);
                msg.getData();
                super.handleMessage(msg);

                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothChatService.STATE_CONNECTED:
                                statusText.setText("connected! ");
                                Intent playGameIntent = new Intent(PlayGame.this, GameplayActivity.class);
                                startActivity(playGameIntent);
                                break;
                            case BluetoothChatService.STATE_CONNECTING:
                                statusText.setText("connecting.... ");
                                break;
                            case BluetoothChatService.STATE_LISTEN:
                            case BluetoothChatService.STATE_NONE:
                                statusText.setText("NOT connected :(");
                                break;
                        }
                        break;
                    case Constants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        break;
                    case Constants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        System.out.println("got msg: " + readMessage);
                        break;
                    case Constants.MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                        Toast.makeText(PlayGame.this, "Connected to "
                                + connectedDeviceName, Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.MESSAGE_TOAST:
                        Toast.makeText(PlayGame.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                        break;
                }

            }

        };

        bluetoothService = BluetoothChatService.getInstance();
        bluetoothService.addHandler(bluetoothEventHandler);

        Button playButton = (Button)findViewById(R.id.playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            public static final boolean NOT_SECURE = false;

            @Override
            public void onClick(View v) {
                System.out.println("hello! I just got the click event: " + v);
                Object device = devicesSpinner.getSelectedItem();
                System.out.println("was object: " + device);
                DeviceWrapper deviceWrapper = (DeviceWrapper) devicesSpinner.getSelectedItem();
                System.out.println("--> " + deviceWrapper.getDevice().getName());
                System.out.println("--> " + deviceWrapper.getDevice().getAddress());
                bluetoothService.connect(deviceWrapper.getDevice(), NOT_SECURE);
                Intent playGameIntent = new Intent(PlayGame.this, GameplayActivity.class);
                playGameIntent.putExtra("com.example.blueclue.competitor", deviceWrapper.getDevice());
                playGameIntent.putExtra("isMaster", true);
                startActivity(playGameIntent);
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        findGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(finding){
                bluetoothAdapter.cancelDiscovery();
                findGamesButton.setText("Find");
                statusText.setText("Click on the Find button");
                finding = false;
            }else {
                System.out.println("Got a click on the FIND GAMES button!");
                bluetoothAdapter.startDiscovery();
                findGamesButton.setText("Stop!");
                statusText.setText("Scanning...");
                finding = true;
            }

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
                System.out.println("device found: " + device.getName() + "(" + device.getAddress() + ")");
                dataAdapter.add(new DeviceWrapper(device));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            }
        }
    };

}

