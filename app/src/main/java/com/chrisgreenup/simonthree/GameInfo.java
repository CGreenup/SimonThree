package com.chrisgreenup.simonthree;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameInfo extends AppCompatActivity {
    private static Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_info);

        String title;
        int infoId;
        int titleId;

        intent = getIntent();
        title = intent.getStringExtra("title");

        if ( "rewind".equals(title) ) {
            titleId = R.string.simon_rewind_title;
            infoId  = R.string.simon_rewind_info;

        }
        else if ( "surprise".equals(title) ) {
            titleId = R.string.simon_surprise_title;
            infoId  = R.string.simon_surprise_info;

        }
        else {
            titleId = R.string.simon_title;
            infoId  = R.string.simon_info;

        }

        ((TextView) findViewById(R.id.game_info_title_tv)).setText(titleId);
        ((TextView) findViewById(R.id.game_info_tv)).setText(infoId);

    }





}
