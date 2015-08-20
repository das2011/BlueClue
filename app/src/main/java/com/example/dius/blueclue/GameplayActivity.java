package com.example.dius.blueclue;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dius.blueclue.listeners.NextInputKeyUpListener;
import com.example.dius.blueclue.listeners.SendNumbersKeyUpListener;


public class GameplayActivity extends ActionBarActivity {

    private PlaceholderFragment fragment;

    Handler bluetoothMessageHandler;
    String bluetoothDeviceString;
    BluetoothDevice bluetoothDevice;
    BluetoothChatService bluetoothService;
    String equationString = "";

    private static GameManager myGame;


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

        myGame = GameManager.getInstance();

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

//                        if(equationString.equals("") && !myGame.isWaitingForAnswer()){
//                            myGame.startGameAsAnswerer();
//                        }
//
                        if(!readMessage.equals(equationString)){

                            if(readMessage.contains("---") && myGame.isWaitingForAnswer()) {
                                System.out.println(" we just got an answer!!!");
                                Answer answer = new Answer();
                                String answerString = readMessage.substring(3, readMessage.length());
                                answer.setValue(Integer.parseInt(answerString));
                                myGame.answerReceived(answer);
                            }else if(myGame.isNotStarted() || readMessage.contains(";") && myGame.isWaitingForQuestion()){
                                if(myGame.isNotStarted()){
                                    myGame.startGameAsAnswerer();
                                }
                                String[] splits = readMessage.split(";");
                                fragment.operand1.setText(splits[0]);
                                fragment.operand2.setText(splits[2]);
                                Question receivedQuestion = new Question();
                                receivedQuestion.setOperand1(Integer.parseInt(splits[0]));
                                receivedQuestion.setOperand2(Integer.parseInt(splits[2]));
                                receivedQuestion.setOperator(Question.Operator.PLUS);
                                myGame.questionReceived(receivedQuestion);

                                fragment.digit1.setText("");
                                fragment.digit2.setText("");
                                fragment.digit1.requestFocus();
                            }

                            equationString = readMessage;
                        }

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

        bluetoothService = BluetoothChatService.getInstance();
        bluetoothService.addHandler(bluetoothMessageHandler);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gameplay, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        EditText operand1;
        EditText operand2;
        TextView operator;
        TextView myScore;
        TextView theirScore;

        EditText digit1;
        EditText digit2;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_gameplay, container, false);

            operand1 = (EditText)rootView.findViewById(R.id.operand1);
            operand2 = (EditText)rootView.findViewById(R.id.operand2);
            operator = (TextView)rootView.findViewById(R.id.operator);
            myScore = (TextView)rootView.findViewById(R.id.myScore);
            theirScore = (TextView)rootView.findViewById(R.id.theirScore);

            operand1.setTextSize(20);
            operand2.setTextSize(20);

            digit1 = (EditText)rootView.findViewById(R.id.digit1);
            digit2 = (EditText)rootView.findViewById(R.id.digit2);

            operand1.setText("");
            operand2.setText("");

            operand1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count >= 1) {
                        operand2.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            operand2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count >= 1) {
                        System.out.println(" -- " + operand1.getText() + "--- " + operator.getText() + " -- " + operand2.getText());
                        if (operand1.getText().length() > 0 && operator.getText().length() > 0 && operand2.length() > 0) {

                            String equation = operand1.getText() + ";" + operator.getText() + ";" + operand2.getText();
                            System.out.println("equation was: " + equation);
                            Question question = new Question();
                            question.setOperand1(Integer.valueOf(operand1.getText().toString()).intValue());
                            question.setOperand2(Integer.valueOf(operand2.getText().toString()).intValue());
                            question.setOperator(Question.Operator.PLUS);
                            myGame.startGameAsQuestioner();
                            myGame.sendQuestion(question);
                            BluetoothChatService.getInstance().write(equation.getBytes());
                            myGame.questionSent();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            digit1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count >= 1) {
                        digit2.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            digit2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count >= 1) {
                        int val1 = Integer.valueOf(operand1.getText().toString());
                        int val2 = Integer.valueOf(operand2.getText().toString());

                        if(digit1.getText().length() > 0 && digit2.getText().length() > 0){
                            int answer = Integer.valueOf(digit1.getText().toString() + digit2.getText().toString());

                            Answer myAnswer = new Answer();
                            myAnswer.setValue(answer);

                            myGame.sendAnswer(myAnswer);

                            BluetoothChatService.getInstance().write(("---" + myAnswer.getValue()).getBytes());
//                            if(answer == val1 + val2){
//                                Toast.makeText(getActivity(), "CORRECT!", Toast.LENGTH_SHORT).show();
//                            }else{
//                                Toast.makeText(getActivity(), "LOST A POINT!", Toast.LENGTH_SHORT).show();
//                            }
                            myScore.setText("" + myGame.getMyScore());
                            theirScore.setText("" + myGame.getTheirScore());

                            operand1.setText("");
                            operand2.setText("");
                            digit1.setText("");
                            digit2.setText("");
                            operand1.requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            return rootView;
        }
    }
}
