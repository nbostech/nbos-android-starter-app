package io.nbos.starterapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.greysonparrelli.permiso.PermisoActivity;

import io.nbos.android.capi.AndroidApiContext;
import io.nbos.capi.api.v0.IdnCallback;
import io.nbos.capi.api.v0.models.RestMessage;
import io.nbos.capi.api.v0.models.TokenApiModel;
import io.nbos.capi.modules.identity.v0.IdentityApi;
import io.nbos.capi.modules.ids.v0.IDS;
import io.nbos.capi.modules.media.v0.MediaApi;
import io.nbos.capi.modules.media.v0.models.MediaApiModel;
import com.squareup.picasso.Picasso;

import io.nbos.starterapp.R;
import io.nbos.starterapp.util.CircleTransform;
import io.nbos.starterapp.util.Prefrences;
import retrofit2.Response;

public class MainActivity extends PermisoActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    static CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this, new TwitterCore(authConfig), new Digits());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Fragment vehicleEntryFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.maincontainer, vehicleEntryFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.name);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        final ImageView profilePic = (ImageView) headerView.findViewById(R.id.profile);
        name.setText(Prefrences.getFirstName(MainActivity.this) + " " + Prefrences.getLastName(MainActivity.this));
        email.setText(Prefrences.getEmailId(MainActivity.this));
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
        if (getIntent().getExtras().getString("flag") != null && getIntent().getExtras().getString("flag").contains("skipLogin")) {
            navigationView.getMenu().findItem(R.id.nav_password).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_account).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_social).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);

            name.setText("Login/Register");
            email.setText("");

        }
        String userId = Prefrences.getUserId(MainActivity.this);
        if (userId != null) {
            MediaApi mediaApi = IDS.getModuleApi("media");
            if (mediaApi != null) {
                mediaApi.getMedia(userId, "profile", new IdnCallback<MediaApiModel>() {
                    @Override
                    public void onResponse(Response<MediaApiModel> response) {
                        if (response.body() != null) {
                            Picasso.with(MainActivity.this).load(response.body()
                                    .getMediaFileDetailsList()
                                    .get(1).getMediapath())
                                    .transform(new CircleTransform())
                                    .placeholder(R.mipmap.ic_account_circle_white_48dp)
                                    .into(profilePic);
                        } else {
                            Picasso.with(MainActivity.this).load(R.mipmap.ic_account_circle_white_48dp).into(profilePic);
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.println(t);
                        Toast.makeText(MainActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();


                    }


                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Fragment vehicleEntryFragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.maincontainer, vehicleEntryFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_account) {
            MyAccountFragment newFragment = new MyAccountFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.maincontainer, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            // Handle the camera action
        } else if (id == R.id.nav_password) {
            // Create fragment and give it an argument specifying the article it should show
            ChangePasswordFragment newFragment = new ChangePasswordFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.maincontainer, newFragment);
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_social) {
            SocialConnectsFragment newFragment = new SocialConnectsFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.maincontainer, newFragment);
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        IdentityApi identityApi = IDS.getModuleApi("identity");
        identityApi.logout(new IdnCallback<RestMessage>() {
            @Override
            public void onResponse(Response<RestMessage> response) {
                if (response.isSuccessful()) {
                    AndroidApiContext.get("app").setUserToken("identity",null);
                    Prefrences.clearPrefrences(MainActivity.this);
                    Intent i = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    SharedPreferences settings = getSharedPreferences("customer", Context.MODE_PRIVATE);
                    settings.edit().clear().apply();
                } else {
                    AndroidApiContext.get("app").setUserToken("identity",null);
                    Prefrences.clearPrefrences(MainActivity.this);
                    Intent i = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    SharedPreferences settings = getSharedPreferences("customer", Context.MODE_PRIVATE);
                    settings.edit().clear().apply();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(MainActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();

            }


        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);

            Toast.makeText(MainActivity.this, "FACEBOOK CONNECT SUCCESS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}
