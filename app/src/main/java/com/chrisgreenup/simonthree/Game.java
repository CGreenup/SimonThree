package com.chrisgreenup.simonthree;

//TODO: make comparison of user input to one place in the history
//TODO: add stuff to history
//TODO: make player wait for simon to finish showing pattern
//TODO: add high score
//TODO: add writing high score to file
//TODO: write high score from file during onCreate, unless file is empty

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Game extends AppCompatActivity
implements View.OnClickListener {


    //For determining the setup of the buttons:
    //in Simon: Surprise, the buttons will be grayscale and all will play the same note
    private String gameMode;

    //For determining the correct sequence of the button presses
    private ArrayList<buttonCommands> history;

    //For determining the user's place in the sequence
    private int index;

    private boolean playersTurn;

    private enum buttonCommands{
        RED, YELLOW, GREEN, BLUE;
        private static buttonCommands [] buttons = values();
        public buttonCommands next(){
            Random rand = new Random();
            return buttons[rand.nextInt(buttons.length)];
        }
    }

    private buttonCommands button = buttonCommands.RED;

    //Variables of imagebuttons to let Simon flash and beep them to signal the player
    private ImageButton imageButtonRed ;
    private ImageButton imageButtonYellow ;
    private ImageButton imageButtonGreen ;
    private ImageButton imageButtonBlue;


    private SoundPool soundPool;
    private Set<Integer> soundsLoaded;

    int beep1Id;
    int beep2Id;
    int beep3Id;
    int beep4Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board);
        soundsLoaded = new HashSet<Integer>();



        //TODO: Set up buttons based on game mode (mainly color)

        String id = "simon_button_";
        for (int i = 0; i < 4; i++){
            int resID = getResources().getIdentifier(id + i, "id", getPackageName());
            ImageButton imageButton = findViewById(resID);
            imageButton.setImageResource(R.drawable.light_off);
            imageButton.setOnClickListener(this);
        }

        if (history != null){
            history.clear();
        }else{
            history = new ArrayList<>();
        }
        history.add(button.next());

        imageButtonRed = findViewById(R.id.simon_button_0);
        imageButtonYellow = findViewById(R.id.simon_button_1);
        imageButtonGreen = findViewById(R.id.simon_button_2);
        imageButtonBlue = findViewById(R.id.simon_button_3);
    }


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

        Log.i("SIMONSAYS", currentColor.toString());

        //Make the button pressed beep
        //soundsLoaded = new HashSet<Integer>();

        //Make the button pressed flash
        ButtonFlash bf = new ButtonFlash();
        bf.execute((ImageButton) view);

        //Make the button pressed beep
        playBeep(currentColor);

        history.add(button.next());

        Log.i("SIMONHISTORY", "_____________________");
        for (int i = 0; i < history.size(); i++){
            Log.i("SIMONHISTORY", history.get(i).toString());
        }
        SimonSay ss = new SimonSay();
        ss.execute();

    }

    //Class for making the buttons on-screen flash as if there were an LED behind them
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
                Thread.sleep(200);
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

    private void playBeep(buttonCommands color){
        if (color.equals(button.RED)){
            if (soundsLoaded.contains(beep1Id)){
                soundPool.play(beep1Id, 1.0f, 1.0f,
                        0, 0, 1.0f);
            }
        }
        else if (color.equals(button.YELLOW)){
            if (soundsLoaded.contains(beep2Id)){
                soundPool.play(beep2Id, 1.0f, 1.0f,
                        0, 0, 1.0f);
            }
        }
        else if (color.equals(button.GREEN)){
            if (soundsLoaded.contains(beep3Id)){
                soundPool.play(beep3Id, 1.0f, 1.0f,
                        0, 0, 1.0f);
            }
        }
        else if (color.equals(button.BLUE)){
            if (soundsLoaded.contains(beep4Id)){
                soundPool.play(beep4Id, 1.0f, 1.0f,
                        0, 0, 1.0f);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setUsage(AudioAttributes.USAGE_GAME);

        SoundPool.Builder spBuilder = new SoundPool.Builder();
        spBuilder.setAudioAttributes(attrBuilder.build());
        spBuilder.setMaxStreams(2);

        soundPool = spBuilder.build();

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0){
                    Log.i("SOUND", "Sound loaded " + sampleId);
                    soundsLoaded.add(sampleId);
                }else{
                    Log.i("SOUND", "ERROR, cannot load sound status = " + status);
                }
            }
        });

        beep1Id  = soundPool.load(this, R.raw.bleep1, 1);
        beep2Id  = soundPool.load(this, R.raw.bleep2, 1);
        beep3Id  = soundPool.load(this, R.raw.bleep3, 1);
        beep4Id = soundPool.load(this, R.raw.bleep4, 1);
    }

    class SimonSay extends AsyncTask<Void, Void, Void>{
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            playersTurn = false;

            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ButtonFlash bf = new ButtonFlash();
            for(int i = 0; i < history.size(); i++) {
                bf.execute(getCorrespondingButton(history.get(i)));
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        private ImageButton getCorrespondingButton(buttonCommands color){
            if (color.equals(buttonCommands.RED))
                return imageButtonRed;
            else if (color.equals(buttonCommands.BLUE))
                return imageButtonBlue;
            else if (color.equals(buttonCommands.YELLOW))
                return imageButtonYellow;
            else if (color.equals(buttonCommands.GREEN))
                return imageButtonGreen;
            else{
                Log.i("SIMONHISTORY", "ERROR WITH getCorrespondingButton");
                return imageButtonRed;
            }
        }

        //After Simon's turn, set it to the players turn and allow them to press buttons
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            playersTurn = true;
        }
    }
}
