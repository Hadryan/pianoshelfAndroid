package com.pianoshelf.joey.pianoshelf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Base activity for the purpose of implementing left panel on all activities.
 * Created by joey on 12/26/14.
 */
public class BaseActivity extends ActionBarActivity {
    private String[] listItems; // Array of items in the drawer, excluding the first item
    private DrawerLayout drawerLayout;
    private android.support.v7.app.ActionBarDrawerToggle drawerToggle;
    private TextView firstItem;

    private static final String LOG_TAG = "BaseActivity";

    // Protected Constants
    protected static final String SERVER_ADDR = Constants.SERVER_ADDR;
    protected static final String PIANOSHELF = "pianoshelf";
    protected static final String AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN";
    protected static final String ACTION_LOGIN = "ACTION_LOGIN";
    protected static final int RESULT_FAILED = 1;
    protected static final int TOKEN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        enableHttpResponseCache();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        firstItem = (TextView) findViewById(R.id.drawer_first_item);

        // Set the value for the first item of the list
        SharedPreferences sharedPreferences = getSharedPreferences(PIANOSHELF, MODE_PRIVATE);
        // Check the login token from shared preferences
        if (sharedPreferences.contains(AUTHORIZATION_TOKEN)) {
            firstItem.setText(getString(R.string.profile));
        } else {
            firstItem.setText(getString(R.string.login));
        }

        // Set the values for the rest of the list
        ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        listItems = getResources().getStringArray(R.array.drawer_text);
        drawerList.setAdapter(new DrawerAdapter(this,
                R.layout.adapter_drawer_list_item, listItems));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Attach listener to drawer open/close events
        drawerToggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawerLayout, R.string.drawer_open, R.string.drawer_closed){
            @Override
            public void	onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.drawer_title);
                //invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(R.string.action_title);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Manual override of setContentView class. Any class inheriting BaseActivity should be
     * inflating its view in the FrameLayout provided in BaseActivity instead of changing the
     * actual view.
     * @param layoutResID
     */
    @Override
    public void setContentView(int layoutResID) {
        FrameLayout content = (FrameLayout) findViewById(R.id.base_content);
        content.addView(getLayoutInflater().inflate(layoutResID, content, false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TOKEN_REQUEST:
                Log.i(LOG_TAG, "Result Code:" + String.valueOf(resultCode));
                switch (resultCode) {
                    case RESULT_OK:
                        String token = data.getStringExtra(AUTHORIZATION_TOKEN);
                        // Store the token in shared preferences, which is private
                        (getSharedPreferences(PIANOSHELF, MODE_PRIVATE).edit())
                                .putString(AUTHORIZATION_TOKEN, token).apply();
                        firstItem.setText(getString(R.string.profile));
                        break;
                    case RESULT_CANCELED:
                        // We don't care if the user has canceled the request
                        firstItem.setText(getString(R.string.login));
                        break;
                    case RESULT_FAILED:
                        // Show a dialog prompting that the login has failed
                        // if the activity has not finished
                        (getSharedPreferences(PIANOSHELF, MODE_PRIVATE).edit())
                                .remove(AUTHORIZATION_TOKEN).apply();
                        firstItem.setText(getString(R.string.login));
                        break;
                    default:
                        firstItem.setText(getString(R.string.login));
                        break;
                }
        }
    }

    /**
     * Action for the first drawer item
     * @param view
     */
    public void invokeFirstItem(View view) {
        Intent intent;
        String itemText = firstItem.getText().toString();
        if (itemText.equals(getString(R.string.login))) {
            intent = new Intent(ACTION_LOGIN, null, this, LoginView.class);
            startActivityForResult(intent, TOKEN_REQUEST);
            firstItem.setText(getString(R.string.logging_in));
        } else if (itemText.equals(getString(R.string.logout))) {
            firstItem.setText(getString(R.string.logging_out));
            SharedPreferences sharedPreferences =
                    getSharedPreferences(PIANOSHELF, MODE_PRIVATE);
            if (sharedPreferences.contains(AUTHORIZATION_TOKEN)) {
                (new LogoutTask()).execute(sharedPreferences.
                        getString(AUTHORIZATION_TOKEN, null));
                // Remove the authorization token. If logout fails, we don't care as
                // logging in again still returns the token
                (sharedPreferences.edit()).remove(AUTHORIZATION_TOKEN).apply();
            }
            firstItem.setText(getString(R.string.login));
        } else if (itemText.equals(getString(R.string.profile))) {
            SharedPreferences globalPreferences = getSharedPreferences(Constants.PIANOSHELF, MODE_PRIVATE);
            if (globalPreferences.contains(Constants.USERNAME)) {
                intent = new Intent(this, ProfileView.class);
                intent.putExtra("username", globalPreferences.getString(Constants.USERNAME, null));
                startActivity(intent);
            } else {
                throw new RuntimeException("No username found.");
            }
        }
        drawerLayout.closeDrawers();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Context context = parent.getContext();
            Intent intent;
            String currentString = listItems[position];
            if(currentString.equals(getString(R.string.home))) {
                intent = new Intent(context, Main.class);
                startActivity(intent);
            } else if(currentString.equals(getString(R.string.myshelf))) {

            } else if(currentString.equals(getString(R.string.downloads))) {

            }
            drawerLayout.closeDrawers();
        }
    }

    private class DrawerAdapter extends BaseAdapter {
        private Context context;
        private int layout;
        private List<String> list;
        public DrawerAdapter(Context context, int layout, List<String> list) {
            this.context = context;
            this.layout = layout;
            this.list = list;
        }

        public DrawerAdapter(Context context, int layout, String[] list) {
            this.context = context;
            this.layout = layout;
            this.list = Arrays.asList(list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                        inflate(layout, parent, false);
            }
            TextView listItem = (TextView) convertView.findViewById(R.id.drawer_text);
            listItem.setText(list.get(position));
            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    // http://android-developers.blogspot.ca/2011/09/androids-http-clients.html
    // Enables caching on Ice Cream Sandwich without affecting earlier releases.
    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.i(LOG_TAG, httpResponseCacheNotAvailable.toString());
        }
    }
}
