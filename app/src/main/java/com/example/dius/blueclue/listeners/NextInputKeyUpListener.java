package com.example.dius.blueclue.listeners;

import android.view.KeyEvent;
import android.view.View;

import com.example.dius.blueclue.R;

public class NextInputKeyUpListener implements View.OnKeyListener {

    private View next;

    public NextInputKeyUpListener(View next) {
        System.out.println("New Listerner for NEXT!");
        this.next = next;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        System.out.println("just got key up!");
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return next.requestFocus();
        }
        return false;
    }
}
