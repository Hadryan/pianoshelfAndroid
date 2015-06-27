package com.pianoshelf.joey.pianoshelf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ryan on 2015-06-26.
 */
public class TabbedFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = (View) inflater.inflate(R.layout.tabbed_fragment, container, false);

        return view;
    }

}

