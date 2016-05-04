package com.pianoshelf.joey.pianoshelf;

import android.os.Environment;

import java.io.File;

/**
 * C for Constants
 * Created by joey on 12/29/14.
 */
public class C {
    public static final String
            PIANOSHELF = "pianoshelf",
            SERVER_ADDR = "https://www.pianoshelf.com/",
            USERNAME = "USERNAME",
            TOKEN_PREFIX = "TOKEN ",
            AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN",
            AUTHORIZATION_HEADER = "Authorization",
            SHELF_CONTENT = "SHELF_CONTENT",
            SHELF_URL = "SHELF_URL",
            SHELF_USER = "SHELF_USER",
            OFFLINE_ROOT_DIRECTORY = Environment.getExternalStorageDirectory()
                    + File.separator + PIANOSHELF + File.separator;

    // Logging tags
    public static final String
            FILE_IO = "fileIO",
            AUTH = "djAuth";

    public static final int
            RESULT_FAILED = 1;
}
