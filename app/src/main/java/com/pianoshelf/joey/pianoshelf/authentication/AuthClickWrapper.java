package com.pianoshelf.joey.pianoshelf.authentication;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;

/**
 * Created by Me on 5/7/2017.
 */

public class AuthClickWrapper implements View.OnClickListener {
    Context mContext;
    SharedPreferenceHelper mSpHelper;
    View.OnClickListener mListener;

    public AuthClickWrapper(Context context, View.OnClickListener listener) {
        mContext = context;
        mSpHelper = new SharedPreferenceHelper(context);
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mSpHelper.isLoggedIn()) {
            mListener.onClick(v);
        } else {
            Toast.makeText(mContext,
                    "Please Login or Signup for Pianoshelf to perform this action",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
