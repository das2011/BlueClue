package com.example.dius.blueclue;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;



public class GameplayActivity extends ActionBarActivity {

    Handler bluetoothMessageHandler;
    String bluetoothDeviceString;
    BluetoothDevice bluetoothDevice;
    BluetoothChatService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new PlaceholderFragment())
                .commit();
        }

        bluetoothMessageHandler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                System.out.println("msg: " + msg.getData());
                msg.getData();
                super.handleMessage(msg);

                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothChatService.STATE_CONNECTED:
                                System.out.println("connected! ");
                                break;
                            case BluetoothChatService.STATE_CONNECTING:
                                System.out.println("connecting.... ");
                                break;
                            case BluetoothChatService.STATE_LISTEN:
                                System.out.println("Listening!!!! in Game Play Activity");
                                break;
                            case BluetoothChatService.STATE_NONE:
                                System.out.println("NOT connected :(");
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
                        bluetoothDeviceString = msg.getData().getString(Constants.DEVICE_NAME);
                        Toast.makeText(GameplayActivity.this, "Connected to "
                                + bluetoothDeviceString, Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.MESSAGE_TOAST:
                        Toast.makeText(GameplayActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                        break;
                }

            }

        };

        bluetoothService = new BluetoothChatService(getApplicationContext(), bluetoothMessageHandler);
        bluetoothService.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gameplay, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onStart();
        Parcelable p = getIntent().getParcelableExtra("com.example.blueclue.competitor");
        System.out.println("HELLO! : " + p);
        if(p != null){
            bluetoothDevice = (BluetoothDevice)p;
            System.out.println("----> " + bluetoothDevice.getAddress());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_gameplay, container, false);
            return rootView;
        }
    }
}
