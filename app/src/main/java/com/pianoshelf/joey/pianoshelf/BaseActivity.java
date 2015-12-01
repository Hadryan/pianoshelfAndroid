package com.pianoshelf.joey.pianoshelf;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

import roboguice.util.temp.Ln;

/**
 * Base activity for the purpose of implementing left panel on all activities.
 * Created by joey on 12/26/14.
 */
public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    protected Toolbar mToolbar;

    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

    private static final String LOG_TAG = "BaseActivity";

    // Protected C
    protected static final String SERVER_ADDR = C.SERVER_ADDR;
    protected static final String PIANOSHELF = C.PIANOSHELF;
    protected static final String AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN";
    protected static final String ACTION_LOGIN = "ACTION_LOGIN";
    protected static final int RESULT_FAILED = 1;
    protected static final int TOKEN_REQUEST = 1;

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Ln.getConfig().setLoggingLevel(Log.ERROR);

        // Base activity should never define the toolbar
        //setContentView(R.layout.activity_base);

        //firstItem = (TextView) findViewById(R.id.drawer_first_item);

        // Set the values for the rest of the list
        /*ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        listItems = getResources().getStringArray(R.array.drawer_text);
        drawerList.setAdapter(new DrawerAdapter(this,
                R.layout.adapter_drawer_list_item, listItems));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());*/

        // Set the value for the first item of the list
        SharedPreferences sharedPreferences = getSharedPreferences(PIANOSHELF, MODE_PRIVATE);
        // Check the login token from shared preferences
        if (sharedPreferences.contains(AUTHORIZATION_TOKEN)) {
            //firstItem.setText(getString(R.string.profile));
        } else {
            //firstItem.setText(getString(R.string.login));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }


    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected Toolbar getActionBarToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
                Log.v(LOG_TAG, "Action bar set");
            }
        }
        return mToolbar;
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
     * different depending on whether the attendee indicated that they are attending the
     * event on-site vs. attending remotely.
     */
    private void setupNavDrawer() {
        Log.v(LOG_TAG, "nav drawer setup");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        // Abort drawer setup when drawer is not defined in the current xml file
        if (mDrawerLayout == null) {
            Log.v(LOG_TAG, "DrawerLayout null");
            return;
        } else {
            Log.v(LOG_TAG, "DrawerLayout present");
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        }

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        } else {
            Log.e(LOG_TAG, "DrawerLayout without toolbar.");
            return;
        }

        // Attach listener to drawer open/close events
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_closed);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Nav View is the actual drawer hidden from sight,
        // we inflate this view with an xml resource
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Log.e(LOG_TAG, "no drawer found.");
        }

    }

    /**
     * Naviagtion drawer actions
     */
    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }


    /**
     * Handle when a drawer item is selected
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }

    /**
     * Handle when a menu item is selected on the toolbar
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TOKEN_REQUEST:
                Log.i(LOG_TAG, "Result Code:" + String.valueOf(resultCode));
                switch (resultCode) {
                    case RESULT_OK:
                        firstItem.setText(getString(R.string.profile));
                        break;
                    case RESULT_CANCELED:
                        firstItem.setText(getString(R.string.login));
                        break;
                    case RESULT_FAILED:
                        firstItem.setText(getString(R.string.login));
                        break;
                    default:
                        firstItem.setText(getString(R.string.login));
                        break;
                }
        }
    }

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
                String authToken = sharedPreferences.getString(AUTHORIZATION_TOKEN, null);
                LogoutRequest request = new LogoutRequest(authToken);
                spiceManager.execute(request, null, DurationInMillis.ONE_MINUTE, null);
                (sharedPreferences.edit()).remove(AUTHORIZATION_TOKEN).apply();
            }
            firstItem.setText(getString(R.string.login));
        } else if (itemText.equals(getString(R.string.profile))) {
            SharedPreferences globalPreferences = getSharedPreferences(C.PIANOSHELF, MODE_PRIVATE);
            if (globalPreferences.contains(C.USERNAME)) {
                intent = new Intent(this, ProfileView.class);
                intent.putExtra("username", globalPreferences.getString(C.USERNAME, null));
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
            String currentString = "";// = listItems[position];
            if(currentString.equals(getString(R.string.home))) {
                intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            } else if(currentString.equals(getString(R.string.myshelf))) {

            } else if(currentString.equals(getString(R.string.downloads))) {

            }
            drawerLayout.closeDrawers();
        }
    }

    */
}
