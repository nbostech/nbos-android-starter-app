package in.wavelabs.starterapp.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.gson.Gson;
import com.nbos.android.capi.AndroidApiContext;
import com.nbos.capi.api.v0.AbstractApiContext;
import com.nbos.capi.api.v0.ErrorUtils;
import com.nbos.capi.api.v0.IdnCallback;
import com.nbos.capi.api.v0.models.FieldErrorApiModel;
import com.nbos.capi.api.v0.models.ValidationErrorResponse;
import com.nbos.capi.modules.identity.v0.IdentityApi;
import com.nbos.capi.modules.identity.v0.models.NewMemberApiModel;
import com.nbos.capi.modules.identity.v0.models.SocialConnectApiModel;
import com.nbos.capi.modules.ids.v0.IDS;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import in.wavelabs.starterapp.R;
import in.wavelabs.starterapp.util.Prefrences;
import retrofit2.Response;

/**
 * Created by vivekkiran on 7/5/16.
 */

public class SocialLoginFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int FRAMEWORK_REQUEST_CODE = 1;
    private String initialStateParam;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_SMS = 0;
    private static String[] PERMISSIONS_SMS = {Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        final Validator validator = new Validator(getActivity());
//        validator.setValidationListener(getActivity());
        View v = inflater.inflate(R.layout.fragment_social_logins, container, false);
        initializeView(v);
        return v;
    }

    private void initializeView(View v) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Login with a social account !");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LoginManager.getInstance().registerCallback(AuthActivity.callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("Facebook Login Successful!");
                        System.out.println("Logged in user Details : ");
                        System.out.println("--------------------------");
                        System.out.println("User ID  : " + loginResult.getAccessToken().getUserId());
                        System.out.println("Authentication Token : " + loginResult.getAccessToken().getToken());
                        Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getActivity(), "Login cancelled by user!", Toast.LENGTH_LONG).show();
                        System.out.println("Facebook Login failed!!");

                    }

                    @Override
                    public void onError(FacebookException e) {
                        Toast.makeText(getActivity(), "Login unsuccessful!", Toast.LENGTH_LONG).show();
                        System.out.println("Facebook Login failed!!");
                    }
                });
//        // If the access token is available already assign it.
        LoginButton loginButton = (LoginButton) v.findViewById(R.id.facebook_login);
        TextView loginwithPhone = (TextView) v.findViewById(R.id.loginWithPhone);
        loginwithPhone.setOnClickListener(view -> onLogin(LoginType.PHONE));
        loginButton.setOnClickListener(view -> LoginManager.getInstance()
                .logInWithReadPermissions(getActivity(),
                        Arrays.asList("public_profile", "email")));

        loginButton.registerCallback(AuthActivity.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String service = "facebook";
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                String token = accessToken.getToken();
                connect(service, token);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                System.out.println(error.toString());

            }

        });
        //    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.googleClientId))
                .requestServerAuthCode(this.getString(R.string.googleClientId))
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.


        SignInButton signInButton = (SignInButton) v.findViewById(R.id.btn_sign_in);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        v.findViewById(R.id.btn_sign_in).setOnClickListener(view -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
        // logoutAccount();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

    }

    public void logoutAccount() {
        AccessToken accesstkn = AccessToken.getCurrentAccessToken();

        if (accesstkn != null) {

            LoginManager.getInstance().logOut();
        }


        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

    }

    private void onLogin(final LoginType loginType) {
        final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
        final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder
                = new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType, AccountKitActivity.ResponseType.CODE);
        configurationBuilder.setFacebookNotificationsEnabled(true);
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);

        initialStateParam = UUID.randomUUID().toString();
        configurationBuilder.setInitialAuthState(initialStateParam);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
        //else {
        // If the user has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the user silently.  Cross-device
        // single sign-on will occur in this branch.
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
        //}
    }


    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        for (Fragment fragment : getChildFragmentManager().getFragments()) {
//            fragment.onActivityResult(requestCode, resultCode, data);
//            callbackManager.onActivityResult(requestCode, resultCode, data);
//
//        }
        //  if(requestCode == 64206){
        //  }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        if (requestCode == FRAMEWORK_REQUEST_CODE) {
            final AccountKitLoginResult loginResult =
                    data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            final String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                final String authorizationCode = loginResult.getAuthorizationCode();
                final long tokenRefreshIntervalInSeconds = loginResult.getTokenRefreshIntervalInSeconds();
                if (authorizationCode != null) {
                    toastMessage = String.format(
                            "Success:%s...",
                            authorizationCode.substring(0, 10));
                    authorizeAndConnect("accountKit", authorizationCode, loginResult.getFinalAuthorizationState());
                } else {
                    toastMessage = "Unknown response type";
                }
            }
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                System.out.println("AuthCode::" + acct.getServerAuthCode());
                String idToken = acct.getIdToken();
                String authCode = acct.getServerAuthCode();
                String service = "googlePlus";

                authorizeAndConnect(service, authCode, "");

            }
            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            // updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }

    private void connect(String service, String accessToken) {
        final IdentityApi identityApi = IDS.getModuleApi("identity");
        SocialConnectApiModel socialConnectApiModel = new SocialConnectApiModel();
        socialConnectApiModel.setAccessToken(accessToken);
        Map map = AbstractApiContext.get("app").getClientCredentials();
        String clientId = (String) map.get("client_id");
        socialConnectApiModel.setClientId(clientId);
        identityApi.connect(socialConnectApiModel, service, new IdnCallback<NewMemberApiModel>() {
            @Override
            public void onResponse(Response<NewMemberApiModel> response) {

                if (response.code() == 200) {
                    Prefrences.setUserId(getActivity(), response.body().getMember().getUuid());
                    Prefrences.setFirstName(getActivity(), response.body().getMember().getFirstName());
                    Prefrences.setLastName(getActivity(), response.body().getMember().getLastName());
                    Prefrences.setEmailId(getActivity(), response.body().getMember().getEmail());
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    i.putExtra("flag", response.body().getMember().isExternal());
                    startActivity(i);
                } else {
                    ValidationErrorResponse validationErrorResponse = ErrorUtils.parseError(identityApi,ValidationErrorResponse.class,response);
                    if (validationErrorResponse != null) {
                        for (FieldErrorApiModel fieldErrorApiModel : validationErrorResponse.getErrors()) {
                            Log.i("response", fieldErrorApiModel.getObjectName());
                        }
                    }
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(), R.string.networkError, Toast.LENGTH_SHORT).show();

            }


        });
    }

    private void authorizeAndConnect(String service, String authCode, String state) {
        final IdentityApi identityApi = IDS.getModuleApi("identity");
        identityApi.authorize(service, authCode, state, new IdnCallback<NewMemberApiModel>() {

            @Override
            public void onResponse(Response<NewMemberApiModel> response) {

                if (response.code() == 200) {
                    Prefrences.setUserId(getActivity(), response.body().getMember().getUuid());
                    Prefrences.setFirstName(getActivity(), response.body().getMember().getFirstName());
                    Prefrences.setLastName(getActivity(), response.body().getMember().getLastName());
                    Prefrences.setEmailId(getActivity(), response.body().getMember().getEmail());
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    i.putExtra("flag", response.body().getMember().isExternal());
                    startActivity(i);
                } else {
                    ValidationErrorResponse  validationErrorResponse = ErrorUtils.parseError(identityApi,ValidationErrorResponse.class,response);
                    if (validationErrorResponse != null) {
                        for (FieldErrorApiModel fieldErrorApiModel : validationErrorResponse.getErrors()) {
                            Log.i("response", fieldErrorApiModel.getObjectName());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(), R.string.networkError, Toast.LENGTH_SHORT).show();

            }

        });
    }


}
