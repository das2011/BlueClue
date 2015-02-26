package com.example.dius.blueclue.listeners;

import android.view.KeyEvent;
import android.view.View;

import com.example.dius.blueclue.R;

/**
 * Created by ggasser on 26/02/15.
 */
public class NextInputKeyUpListener implements  View.OnKeyListener {

    private View next;

    public NextInputKeyUpListener(View next) {
        this.next = next;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return next.requestFocus();
        }
        return false;
    }
}