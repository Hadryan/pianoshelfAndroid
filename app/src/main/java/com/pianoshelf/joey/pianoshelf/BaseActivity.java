package com.pianoshelf.joey.pianoshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pianoshelf.joey.pianoshelf.authentication.LoginView;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutMeta;
import com.pianoshelf.joey.pianoshelf.authentication.LogoutResponse;
import com.pianoshelf.joey.pianoshelf.authentication.UserInfo;
import com.pianoshelf.joey.pianoshelf.authentication.UserToken;
import com.pianoshelf.joey.pianoshelf.rest_api.HeaderInterceptor;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.rest_api.RWCallback;
import com.pianoshelf.joey.pianoshelf.rest_api.ResponseInterceptor;
import com.pianoshelf.joey.pianoshelf.rest_api.RetroShelf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Base activity for the purpose of implementing left panel on all activities.
 * Created by joey on 12/26/14.
 */
public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "BaseActivity";

    // Protected Constants
    protected static final String ACTION_LOGIN = "ACTION_LOGIN";
    protected static final int RESULT_FAILED = 1;

    // Retrofit
    protected Retrofit retrofit;
    protected RetroShelf apiService;

    // UI
    private DrawerLayout mDrawerLayout;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    protected Toolbar mToolbar;
    private ImageView mProfileImage;
    private TextView mUsername;

    // Storage
    protected SharedPreferenceHelper mSPHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSPHelper = new SharedPreferenceHelper(this);

        // Instantiate retrofit here since we need SharedPreferences from activity context
        retrofit = new Retrofit.Builder()
                .baseUrl(C.SERVER_ADDR)
                //.baseUrl(new HttpUrl.Builder().scheme("http").host("192.168.0.102").port(80).build())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                        //.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                        .addInterceptor(new ResponseInterceptor())
                        .addInterceptor(new HeaderInterceptor(mSPHelper.getAuthToken()))
                        .build())
                .build();
        apiService = retrofit.create(RetroShelf.class);

        // Base activity should never define the toolbar
        //setContentView(R.layout.activity_base);

        //firstItem = (TextView) findViewById(R.id.drawer_first_item);

        // Set the values for the rest of the list
        /*ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        listItems = getResources().getStringArray(R.array.drawer_text);
        drawerList.setAdapter(new DrawerAdapter(this,
                R.layout.adapter_drawer_list_item, listItems));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

        if (mToolbar == null) {
            Log.e(LOG_TAG, "DrawerLayout without toolbar.");
            return;
        } else {
            mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        }

        // Attach listener to drawer open/close events
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_closed);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Nav View is the actual drawer hidden from sight,
        // we inflate this view with an xml resource
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            View navHeader = navigationView.getHeaderView(0);
            if (navHeader != null) {
                mProfileImage = (ImageView) navHeader.findViewById(R.id.profile_image);
                Log.v(LOG_TAG, "Profile image view set " + mProfileImage);

                mUsername = (TextView) navHeader.findViewById(R.id.profile_username);
            }
        } else {
            mProfileImage = null;
            Log.e(LOG_TAG, "no drawer found.");
        }

    }

    /**
     * Navigation drawer actions
     */
    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    // All actions that needs to trigger when the user logs in is here
    // marked final here since derived classes will also @Subscribe their inherited onUserLogin
    // creating duplication
    @Subscribe
    public final void onUserLogin(UserInfo response) {
        onTokenChanged(response);
        onUserNameChanged(response);
        onProfileImageChanged(response);
    }

    private void onTokenChanged(UserInfo response) {
        Log.i(LOG_TAG, "User logged in " + response);
        String token = response.getAuth_token();
        if (!token.equals(mSPHelper.getAuthToken())) {
            mSPHelper.setUserAndToken(response.getUsername(), token);
        } else {
            Log.w(LOG_TAG, "Duplicate token");
        }
    }

    // Set profile name and description
    private void onUserNameChanged(UserInfo response) {
        String profileUsername = response.getUsername();
        if (mUsername != null) {
            mUsername.setText(profileUsername);
        } else {
            Log.w(LOG_TAG, "Profile username textview not present");
        }
    }

    // Set profile image
    private void onProfileImageChanged(UserInfo response) {
        if (response.getProfile_picture() != null && mProfileImage != null) {
            URL profileImageUrl = response.getProfile_picture();
            Log.i(C.AUTH, "Profile image url: " + profileImageUrl);
            Glide.with(this)
                    .load(profileImageUrl.toString())
                    .fitCenter()
                    .crossFade()
                    .into(mProfileImage);
            Log.i(LOG_TAG, "profile image set");
        } else {
            Log.i(LOG_TAG, "Profile imageview " + mProfileImage +
                    " User image " + response.getProfile_picture());
        }
    }

    // Listens to logoutResponse
    @Subscribe
    protected void resetProfileImage(LogoutResponse response) {
        Log.i(C.AUTH, "Logout response: " + response.getDetail());
        if (mProfileImage != null) {
            mProfileImage.setImageDrawable(
                    ResourcesCompat.getDrawable(
                            getResources(),
                            R.drawable.pianoshelf_logo_solid, null));

        }
    }


    /**
     * Handle when a drawer item is selected
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_login:
                Intent intent = new Intent(ACTION_LOGIN, null, this, LoginView.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
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

    // Authentication
    public void logout() {
        if (!mSPHelper.userLoggedIn()) {
            Log.w(C.AUTH, "Attempt to logout without login token! Logout aborted.");
            return;
        }

        apiService.logout()
                .enqueue(new RWCallback<RW<LogoutResponse, LogoutMeta>>() {
                    @Override
                    public void onResponse(Call<RW<LogoutResponse, LogoutMeta>> call, Response<RW<LogoutResponse, LogoutMeta>> response) {
                        super.onResponse(call, response);
                        int statusCode = response.body().getMeta().getCode();
                        if (statusCode == 200) {
                            mSPHelper.removeUserAndToken();
                            Log.i(C.AUTH, "Auth token removed");
                        } else {
                            Log.e(C.AUTH, "Invalid Response from logout request! " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<RW<LogoutResponse, LogoutMeta>> call, Throwable t) {
                        t.printStackTrace();
                        Log.e(C.AUTH, "Logout request failed! " + t.getLocalizedMessage());
                    }
                });
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
