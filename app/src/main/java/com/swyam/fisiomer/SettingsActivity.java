package com.swyam.fisiomer;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Reyes Yam on 04/03/2017.
 */


public class SettingsActivity extends PreferenceActivity {

    boolean reiniciar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        reiniciar = getIntent().getBooleanExtra("reiniciar",false);

    }

    @Override
    public void onBackPressed(){
        if(reiniciar){
            Intent intent = new Intent(SettingsActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }else{
            super.onBackPressed();
        }
    }
}