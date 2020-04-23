package com.chrisgreenup.simonthree;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Game extends AppCompatActivity
        implements View.OnClickListener {

    private static Intent intent;


    //For determining the setup of the buttons:
    //in Simon: Surprise, the buttons will be grayscale and all will play the same note
    private String gameMode;

    //For determining the correct sequence of the button presses
    private ArrayList<buttonCommands> history;

    //For determining the user's place in the sequence
    private int index;
    //For determing if the player can press buttons
    private boolean playersTurn;

    //Variable for keeping track of the highest score
    private int highscore;

    private int currentscore;

    //Enum of all of the colors, and a method to get a random color
    private enum buttonCommands {
        RED, YELLOW, GREEN, BLUE;
        private static buttonCommands[] buttons = values();

        public buttonCommands next() {
            Random rand = new Random();
            return buttons[( this.ordinal() + rand.nextInt(buttons.length) ) % 4];
        }
    }

    private buttonCommands button = buttonCommands.RED;

    //Variables of imagebuttons to let Simon flash and beep them to signal the player
    private ImageButton imageButtonRed;
    private ImageButton imageButtonYellow;
    private ImageButton imageButtonGreen;
    private ImageButton imageButtonBlue;


    private SoundPool soundPool;
    private Set<Integer> soundsLoaded;

    //beep ids to have sound setup in onResume, but played in onClick
    int beep1Id;
    int beep2Id;
    int beep3Id;
    int beep4Id;
    int failId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board);
        soundsLoaded = new HashSet<Integer>();

        intent   = getIntent();
        gameMode = intent.getStringExtra("game");

        currentscore = 0;

        TextView highscoreTv = findViewById(R.id.high_score_tv);
        highscoreTv.setText(getSavedHighscore());


        String id = "simon_button_";
        for (int i = 0; i < 4; i++) {
            int resID = getResources().getIdentifier(id + i , "id" , getPackageName());
            ImageButton imageButton = findViewById(resID);
            imageButton.setImageResource(R.drawable.light_off);
            imageButton.setOnClickListener(this);
        }

        setupExtraButton(gameMode);

        restartGame();

    }

    //Initializes the instance variables of the imageButtons
    //Doubles in function to change the buttons gray if the game mode is Simon: Surprise
    void setupExtraButton(String gameMode) {
        imageButtonRed    = findViewById(R.id.simon_button_0);
        imageButtonYellow = findViewById(R.id.simon_button_1);
        imageButtonGreen  = findViewById(R.id.simon_button_2);
        imageButtonBlue   = findViewById(R.id.simon_button_3);

        if (gameMode.equals("surprise")){
            imageButtonBlue.setBackgroundColor(0xff818181);
            imageButtonRed.setBackgroundColor(0xff818181);
            imageButtonYellow.setBackgroundColor(0xff818181);
            imageButtonGreen.setBackgroundColor(0xff818181);
        }
        else{
            imageButtonRed.setBackgroundColor(getResources().getColor(R.color.simonRed));
            imageButtonYellow.setBackgroundColor(getResources().getColor(R.color.simonYellow));
            imageButtonGreen.setBackgroundColor(getResources().getColor(R.color.simonGreen));
            imageButtonBlue.setBackgroundColor(getResources().getColor(R.color.simonBlue));
        }
    }

    @Override
    public void onClick(View view) {
        if (playersTurn){
            buttonCommands currentColor;

            switch (view.getId()){
                case ( R.id.simon_button_0 ):
                    currentColor = buttonCommands.RED;
                    break;
                case ( R.id.simon_button_1 ):
                    currentColor = buttonCommands.YELLOW;
                    break;
                case ( R.id.simon_button_2 ):
                    currentColor = buttonCommands.GREEN;
                    break;
                case ( R.id.simon_button_3 ):
                    currentColor = buttonCommands.BLUE;
                    break;
                default:
                    currentColor = null;
                    break;
            }

            //Make the button pressed beep
            //soundsLoaded = new HashSet<Integer>();

            //Make the button pressed flash
            ButtonFlash bf = new ButtonFlash();
            bf.execute((ImageButton) view);

            //Make the button pressed beep
            playBeep(currentColor);

            //If the player has entered the correct sequence
            if (pickedTheRightColor(currentColor)){
                if (index == history.size()){
                    updateScores();
                    SimonSay ss = new SimonSay();
                    ss.execute();
                }
            }
            else{
                restartGame();
            }

        }

    }

    private void updateScores() {
        currentscore = history.size();

        if (currentscore > highscore){
            highscore = currentscore;
            saveHighscore();
        }

        TextView scoreTv = findViewById(R.id.high_score_tv);
        String s = getResources().getString(R.string.highscore_text) + " " + highscore;
        scoreTv.setText(s);
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

    private void playBeep(buttonCommands color) {

        if (!gameMode.equals("surprise")){
            if (color.equals(button.RED)){
                if (soundsLoaded.contains(beep1Id)){
                    soundPool.play(beep1Id , 1.0f , 1.0f ,
                            0 , 0 , 1.0f);
                }
            }
            else if (color.equals(button.YELLOW)){
                if (soundsLoaded.contains(beep2Id)){
                    soundPool.play(beep2Id , 1.0f , 1.0f ,
                            0 , 0 , 1.0f);
                }
            }
            else if (color.equals(button.GREEN)){
                if (soundsLoaded.contains(beep3Id)){
                    soundPool.play(beep3Id , 1.0f , 1.0f ,
                            0 , 0 , 1.0f);
                }
            }
            else if (color.equals(button.BLUE)){
                if (soundsLoaded.contains(beep4Id)){
                    soundPool.play(beep4Id , 1.0f , 1.0f ,
                            0 , 0 , 1.0f);
                }
            }
        }
        else{
            if (soundsLoaded.contains(beep3Id)){
                soundPool.play(beep3Id , 1.0f , 1.0f ,
                        0 , 0 , 1.0f);
            }
        }

    }

    private void restartGame() {
        if (history != null){
            history.clear();
            if (soundsLoaded.contains(failId)){
                soundPool.play(failId , 1.0f , 1.0f ,
                        0 , 0 , 1.0f);
            }
        }
        else{
            history = new ArrayList<>();
        }

        SimonSay simonSay = new SimonSay();
        simonSay.execute();
    }

    private boolean pickedTheRightColor(buttonCommands color) {
        //If the player isn't playing Simon Rewind, use normal rules
        if (!gameMode.equals("rewind")){
            if (history.get(index).equals(color)){
                index++;
                return true;
            }
        }
        //otherwise, use rules that are backwards compared to Simon
        else{
            int size = history.size() - 1;
            if (history.get(size - index).equals(color)){
                index++;
                return true;
            }
        }

        return false;
    }

    private void saveHighscore() {

        FileOutputStream fos = null;
        try {
            String filename = gameMode + ".txt";

            fos = openFileOutput(filename , Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println(highscore);

            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getSavedHighscore() {
        FileInputStream fis = null;
        String filename = gameMode + ".txt";
        String output = getResources().getString(R.string.highscore_text) + " ";

        try {
            fis = openFileInput(filename);

            Scanner scanner = new Scanner(fis);

            if (scanner.hasNextInt())
                highscore = scanner.nextInt();

            output += highscore;

            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;

    }


    //Sets up the audio
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
            public void onLoadComplete(SoundPool soundPool , int sampleId , int status) {
                if (status == 0){
                    Log.i("SOUND" , "Sound loaded " + sampleId);
                    soundsLoaded.add(sampleId);
                }
                else{
                    Log.i("SOUND" , "ERROR, cannot load sound status = " + status);
                }
            }
        });

        beep1Id = soundPool.load(this , R.raw.bleep1 , 1);
        beep2Id = soundPool.load(this , R.raw.bleep2 , 1);
        beep3Id = soundPool.load(this , R.raw.bleep3 , 1);
        beep4Id = soundPool.load(this , R.raw.bleep4 , 1);
        failId  = soundPool.load(this , R.raw.fail , 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundPool != null){
            soundPool.release();
            soundPool = null;

            soundsLoaded.clear();
        }
    }

    //Algorithm for controlling Simon, the opponent
    class SimonSay extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            playersTurn = false;
            history.add(button.next());

            Log.i("DSIMONHISTORY" , "_____________________");
            for (int i = 0; i < history.size(); i++) {
                Log.i("DSIMONHISTORY" , history.get(i).toString());
            }


            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < history.size(); i++) {
                try {
                    playBeep(history.get(i));
                    buttonFlash(getCorrespondingButton(history.get(i)));
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        private void buttonFlash(final ImageButton button) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setImageResource(R.drawable.light_on);
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
                    button.setImageResource(R.drawable.light_off);
                }
            });


        }

        private ImageButton getCorrespondingButton(buttonCommands color) {
            if (color.equals(buttonCommands.RED))
                return imageButtonRed;
            else if (color.equals(buttonCommands.BLUE))
                return imageButtonBlue;
            else if (color.equals(buttonCommands.YELLOW))
                return imageButtonYellow;
            else if (color.equals(buttonCommands.GREEN))
                return imageButtonGreen;
            else{
                Log.i("DSIMONHISTORY" , "ERROR WITH getCorrespondingButton");
                return imageButtonRed;
            }
        }

        //After Simon's turn, set it to the players turn and allow them to press buttons
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            playersTurn = true;
            index       = 0;
        }
    }
}
