package com.pianoshelf.joey.pianoshelf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


/**
 * This is the main logic page
 * This does not have to be the front page
 */
public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            //case R.id.actionBarSearch:
                // Process search keypress from action bar
                //return true;
            default:
               return super.onOptionsItemSelected(item);
        }
    }


    // Invoke SheetView
    public void invokeSheetView(View view){
        Intent intent = new Intent(this, SheetView.class);

        intent.putExtra("sheetMusicUrl", "http://192.168.42.61:5000/api/sheetmusic/1/");

        startActivity(intent);
    }
}
