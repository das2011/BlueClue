package com.example.dius.blueclue.listeners;

import android.view.KeyEvent;
import android.view.View;

/**
 * Created by elgaby on 26/02/15.
 */
public abstract class SendNumbersKeyUpListener implements View.OnKeyListener {

    private View previous;
    private View operator;

    public SendNumbersKeyUpListener(View previous, View operator) {
        this.operator = operator;
        this.previous = previous;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return true;
    }

    public abstract void action();
}
