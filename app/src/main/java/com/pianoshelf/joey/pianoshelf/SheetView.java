package com.pianoshelf.joey.pianoshelf;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by joey on 24/10/14.
 * Page/Activity for viewing sheet music
 * Goal: Swipe left/right to move pages (intuitive)
 * Goal: Auto-Hiding navigation buttons after some time {Left, Right, Page Number}
 * Goal: Intuitive back button
 */
public class SheetView extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheetview);
    }
}
