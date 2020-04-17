package com.chrisgreenup.simonthree;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class Game extends AppCompatActivity
implements View.OnClickListener {


    //For determining the setup of the buttons:
    //in Simon: Surprise, the buttons will be grayscale and all will play the same note
    private String gameMode;

    //For determining the correct sequence of the button presses
    private ArrayList<buttonCommands> history;

    //For determining the user's place in the sequence
    private int index;

    private enum buttonCommands{
        RED, YELLOW, GREEN, BLUE;
        private static buttonCommands [] buttons = values();
        public buttonCommands next(){
            Random rand = new Random();
            return buttons[rand.nextInt(buttons.length)];
        }
    }

    buttonCommands button;

    @Override
    public void onClick(View view) {
        buttonCommands currentColor;

        switch (view.getId()){
            case (R.id.simon_button_0):
                currentColor = button.RED;
                break;
            case (R.id.simon_button_1):
                currentColor = button.YELLOW;
                break;
            case (R.id.simon_button_2):
                currentColor = button.GREEN;
                break;
            case (R.id.simon_button_3):
                currentColor = button.BLUE;
                break;
            default:
                currentColor = null;
                break;
        }

        ((ImageButton) view).setImageResource(R.drawable.light_on);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ((ImageButton) view).setImageResource(R.drawable.light_off);


        Log.i("SIMONSAYS", currentColor.toString());

        ButtonFlash bf = new ButtonFlash();
        bf.execute((ImageButton) view);

    }

    class ButtonFlash extends AsyncTask<ImageButton, Void, Void> {

        @Override
        protected Void doInBackground(final ImageButton... imageButtons) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageButtons[0].setImageResource(R.drawable.light_on);
                }
            });

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageButtons[0].setImageResource(R.drawable.light_off);
                }
            });

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board);

        //TODO: Set up buttons based on game mode (mainly color)

        String id = "simon_button_";
        for (int i = 0; i < 4; i++){
            int resID = getResources().getIdentifier(id + i, "id", getPackageName());
            ImageButton button = findViewById(resID);
            button.setImageResource(R.drawable.light_off);
            button.setOnClickListener(this);
        }

        if (history != null)
            history.clear();
    }


    class simonPlay extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            history.add(button.next());

            return null;
        }
    }
}
