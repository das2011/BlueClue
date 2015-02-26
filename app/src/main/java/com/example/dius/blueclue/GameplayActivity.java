package com.example.dius.blueclue;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dius.blueclue.listeners.NextInputKeyUpListener;
import com.example.dius.blueclue.listeners.SendNumbersKeyUpListener;

import org.androidannotations.annotations.ViewById;


public class GameplayActivity extends ActionBarActivity {

    private PlaceholderFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        if (savedInstanceState == null) {

            fragment = new PlaceholderFragment();

            getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gameplay, menu);
        return true;
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

        @ViewById EditText operand1;
        @ViewById EditText operand2;
        @ViewById TextView operator;

        @ViewById EditText digit1;
        @ViewById EditText digit2;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_gameplay, container, false);

            operand1.setOnKeyListener(new NextInputKeyUpListener(operand2));
            operand2.setOnKeyListener(new SendNumbersKeyUpListener(operand1, operator) {
                @Override
                public void action() {
                    // Send {this + operand + previous}
                }
            });

            digit1.setOnKeyListener(new NextInputKeyUpListener(digit2));
//            digit2.setOnKeyListener(new SendNumbersKeyUpListener() {
//                @Override
//                public void action() {
//
//                }
//            });

            return rootView;
        }
    }
}
