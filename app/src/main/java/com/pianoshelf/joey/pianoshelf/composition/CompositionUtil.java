package com.pianoshelf.joey.pianoshelf.composition;

import android.graphics.Color;
import android.util.Log;

import com.pianoshelf.joey.pianoshelf.C;

import java.io.File;

/**
 * Created by joey on 13/11/15.
 */
public class CompositionUtil {
    public static final String LOG_TAG = "FullComposition Parser";

    // Parse a difficulty integer to a difficulty string
    public static String ParseDifficulty(int difficulty) {
        switch (difficulty) {
            case 0:
                return "No Rating";
            case 1:
                return "Beginner";
            case 2:
                return "Novice";
            case 3:
                return "Intermediate";
            case 4:
                return "Advanced";
            case 5:
                return "Expert";
            default: {
                Log.w(LOG_TAG, "out of switch difficult " + difficulty);
                return "No Rating";
            }
        }
    }

    public static int ParseDifficultyColor(int difficulty) {
        return Color.GRAY;
        /*
        switch (difficulty) {
            case 0: return Color.GRAY;
            case 1: return Color.GREEN;
            case 2: return Color.CYAN;
            case 3: return Color.YELLOW;
            case 4: return Color.MAGENTA;
            case 5: return Color.RED;
            default: throw new RuntimeException("Invalid difficulty");
        }*/
    }

    public static String parseViews(int views) {
        int thousandth = views;
        int thousandthPrev = 0;
        int counter = -1;
        while (thousandth != 0) {
            thousandthPrev = thousandth;
            thousandth = thousandth / 1000;
            ++counter;
        }
        String view = Integer.toString(thousandthPrev);
        switch (counter) {
            case -1:
                return view;
            case 0:
                return view;
            case 1:
                return view + 'K';
            case 2:
                return view + 'M';
            case 3:
                return view + 'B';
            default:
                return view + 'T';
        }
    }

    public static String offlineSheetFilename(String sheetUrl) {
        return sheetUrl.substring(sheetUrl.lastIndexOf('/') + 1);
    }

    public static String offlineDirPath(FullComposition sheetInfo) {
        return C.OFFLINE_ROOT_DIRECTORY + File.separator +
                sheetInfo.getUniqueurl() + File.separator + sheetInfo.getId();
    }

    public static String offlineSheetPath(FullComposition sheetInfo, int pos) {
        String sheetUrl = sheetInfo.getImages().get(pos);
        return offlineDirPath(sheetInfo) + offlineSheetFilename(sheetUrl);
    }

}
