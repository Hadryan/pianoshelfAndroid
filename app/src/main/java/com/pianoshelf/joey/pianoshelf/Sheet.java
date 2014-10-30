package com.pianoshelf.joey.pianoshelf;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by root on 10/29/14.
 */
public class Sheet extends Fragment{

    /**
     * We do not override the onCreate method, instead we use the
     * newInstance and Bundle static factory design pattern.
     * We will pass in the url of the sheet
     */
    public static Sheet newInstance(String sheetSrc) {
        Sheet sheet = new Sheet();
        Bundle args = new Bundle();
        //args.putInt("Name", num);
        args.putString("sheetSrc", sheetSrc);

        sheet.setArguments(args);
        return sheet;
    }
}
