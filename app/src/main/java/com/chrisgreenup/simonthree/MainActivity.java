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

    }

    @Override
    public void onClick(View view) {
        Intent intent;

        if ((view.getId()) == R.id.simon_info_button){
            intent = new Intent(getBaseContext(), GameInfo.class);
            intent.putExtra("title", "simon");
        }
        else if (view.getId() == R.id.simonRewind_info_button){
            intent = new Intent(getBaseContext(), GameInfo.class);
            intent.putExtra("title", "rewind");
        }
        else if (view.getId() == R.id.simonSurprise_info_button){
            intent = new Intent(getBaseContext(), GameInfo.class);
            intent.putExtra("title", "surprise");
        }
        else if (view.getId() == R.id.play_simonRewind_button){
            intent = new Intent(getBaseContext(), Game.class);
            intent.putExtra("game", "rewind");
        }
        else if (view.getId() == R.id.play_simonSurprise_button){
            intent = new Intent(getBaseContext(), Game.class);
            intent.putExtra("game", "surprise");
        }
        else{
            intent = new Intent(getBaseContext(), Game.class);
            intent.putExtra("game", "simon");
        }

        startActivity(intent);
    }
}
