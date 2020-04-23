package com.chrisgreenup.simonthree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.simon_info_button).setOnClickListener(this);
        findViewById(R.id.simonRewind_info_button).setOnClickListener(this);
        findViewById(R.id.simonSurprise_info_button).setOnClickListener(this);
        findViewById(R.id.play_simon_button).setOnClickListener(this);
        findViewById(R.id.play_simonSurprise_button).setOnClickListener(this);
        findViewById(R.id.play_simonRewind_button).setOnClickListener(this);
        findViewById(R.id.credit_button).setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        Intent intent = null;

        int id = view.getId();

        if (id == R.id.simon_info_button){
            intent = new Intent(getBaseContext(), GameInfo.class);
            intent.putExtra("title", "simon");
        }
        else if (id == R.id.simonRewind_info_button){
            intent = new Intent(getBaseContext(), GameInfo.class);
            intent.putExtra("title", "rewind");
        }
        else if (id == R.id.simonSurprise_info_button){
            intent = new Intent(getBaseContext(), GameInfo.class);
            intent.putExtra("title", "surprise");
        }
        else if (id == R.id.credit_button){
            intent = new Intent(getBaseContext(), GameInfo.class);
            intent.putExtra("title", "credit");
        }


        else if (id == R.id.play_simonRewind_button){
            intent = new Intent(getApplicationContext(), Game.class);
            intent.putExtra("game", "rewind");
            Log.i("TESTTT", "rewind");
        }
        else if (id == R.id.play_simonSurprise_button){
            intent = new Intent(getApplicationContext(), Game.class);
            intent.putExtra("game", "surprise");
            Log.i("TESTTT", "surprise");
        }
        else if (id == R.id.play_simon_button){
            intent = new Intent(getApplicationContext(), Game.class);
            intent.putExtra("game", "simon");
            Log.i("TESTTT", "classic");
        }

        startActivity(intent);
    }
}
