package in.wavelabs.starterapp.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsAuthConfig;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsSession;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import in.wavelabs.starterapp.R;
import in.wavelabs.startersdk.ConnectionAPI.AuthApi;
import in.wavelabs.startersdk.ConnectionAPI.NBOSCallback;
import in.wavelabs.startersdk.ConnectionAPI.SocialApi;
import in.wavelabs.startersdk.DataModel.member.NewMemberApiModel;
import in.wavelabs.startersdk.DataModel.validation.ValidationMessagesApiModel;
import in.wavelabs.startersdk.Utils.Prefrences;
import io.fabric.sdk.android.Fabric;
import retrofit2.Response;

/**
 * Created by vineelanalla on 08/03/16.
 */
public class LoginFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener, Validator.ValidationListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private com.digits.sdk.android.AuthCallback callback;
    @NotEmpty(message = "Username cannot be empty")
    private EditText emailEditText;
    @Password(min = 5, scheme = Password.Scheme.ANY)
    private EditText passwordEditText;
    private static final int REQUEST_SMS = 0;
    Validator validator;
    private static String[] PERMISSIONS_SMS = {Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS};
//    CallbackManager callbackManager;
//    AccessTokenTracker accessTokenTracker;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
//        callbackManager = CallbackManager.Factory.create();
//
//
//    }
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        final Validator validator = new Validator(getActivity());
//        validator.setValidationListener(getActivity());
        validator = new Validator(this);
        validator.setValidationListener(this);

        View v = inflater.inflate(R.layout.activity_login_all, container, false);
        initializeView(v);
        return v;
    }
    private void initializeView(View v){

        requestSmsPermission();
     //   callbackManager = CallbackManager.Factory.create();

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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance()
                        .logInWithReadPermissions(getActivity(),
                                Arrays.asList("public_profile", "email"));

            }
        });

        loginButton.registerCallback(AuthActivity.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String service = "facebook";
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                String token = accessToken.getToken();
                connect(service,token);
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
        TextView rst = (TextView) v.findViewById(R.id.forgot);

        rst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ResetPasswordActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            }
        });
        TextView signup = (TextView) v.findViewById(R.id.register);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), SignUpActivity.class);
//                startActivity(i);
//                getActivity().overridePendingTransition(0, 0);
                ((AuthActivity)getActivity()).getViewPager().setCurrentItem(1);


            }
        });
        emailEditText = (EditText) v.findViewById(R.id.emailtxt);
        passwordEditText = (EditText) v.findViewById(R.id.password);

        Button login = (Button) v.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validator.validate(true);
            }
        });
        v.findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        callback = new com.digits.sdk.android.AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                Toast.makeText(getActivity(),
                        "Authentication Successful for " + phoneNumber, Toast.LENGTH_SHORT).show();
                // userIdView.setText(getString(R.string.user_id, session.getId()));
                if (session.getAuthToken() instanceof TwitterAuthToken) {
                    final TwitterAuthToken authToken = (TwitterAuthToken) session.getAuthToken();
                    //tokenView.setText(getString(R.string.token, authToken.token));
                    //secretView.setText(getString(R.string.secret, authToken.secret));
                    session.getEmail();
                    DigitsOAuthSigning oauthSigning = new DigitsOAuthSigning(getAuthConfig(getActivity()), authToken);

                    Map authHeaders = oauthSigning.getOAuthEchoHeadersForVerifyCredentials();

                    Log.i(TAG, phoneNumber + " " + session.getId() + " " +
                            authHeaders.get("X-Auth-Service-Provider") + " : "
                            + authHeaders.get("X-Verify-Credentials-Authorization")
                    );
                    String provider =  authHeaders.get("X-Auth-Service-Provider").toString();
                    String authCredentials = authHeaders.get("X-Verify-Credentials-Authorization").toString();
                    String emailId = session.getEmail().getAddress();
//                    digitsConnect(provider,authCredentials,emailId);
                    Intent i  = new Intent(getActivity(), DigitsCreateAccount.class);
                    i.putExtra("provider",provider);
                    i.putExtra("authCredentials", authCredentials);
                    i.putExtra("emailId", emailId);
                    startActivity(i);
                    // digitsConnect(authToken.token);

                }
            }

            @Override
            public void failure(DigitsException error) {
                Toast.makeText(getActivity(), error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };


        DigitsAuthButton digitsButton = (DigitsAuthButton) v.findViewById(R.id.auth_button);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession digitsSession, String s) {

            }

            @Override
            public void failure(DigitsException e) {

            }
        });
        digitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DigitsAuthConfig.Builder digitsAuthConfigBuilder = new DigitsAuthConfig.Builder()
                        .withAuthCallBack(callback)
                        .withPhoneNumber("+91" + Prefrences.getPhoneNumber(getActivity()))
                        .withEmailCollection()
                        .withThemeResId(R.style.AppThemeDark);

                Digits.authenticate(digitsAuthConfigBuilder.build());
            }
        });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

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
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                System.out.println("AuthCode::" +acct.getServerAuthCode());
                String idToken = acct.getIdToken();
                String authCode = acct.getServerAuthCode();
                String service = "googlePlus";

                authorizeAndConnect(service,authCode);

            }
            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            // updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }

    private void requestSmsPermission() {
        Log.i(TAG, "SMS permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_SMS)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.RECEIVE_SMS)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");

                            ActivityCompat.requestPermissions(getActivity(),
                                    PERMISSIONS_SMS,
                                    REQUEST_SMS);

        } else {

            // Camera permission has not been granted yet. Request it directly.
            //   ActivityCompat.requestPermissions(this, PERMISSIONS_SMS, REQUEST_SMS);
            //   TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //  String mPhoneNumber = tMgr.getLine1Number();

            Prefrences.setPhoneNumber(getActivity(), 0L);

        }

        // END_INCLUDE(camera_permission_request)

    }
    private void login(){
        AuthApi.login(getActivity(), emailEditText.getText().toString(),passwordEditText.getText().toString(),
                new NBOSCallback<NewMemberApiModel>() {
                    @Override
                    public void onSuccess(Response<NewMemberApiModel> response) {
                        if(response.isSuccessful()){

                            Intent i = new Intent(getActivity(), MainActivity.class);

                            startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getActivity(),R.string.networkError, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onValidationError(List<ValidationMessagesApiModel> validationError) {

                    }

                    @Override
                    public void authenticationError(String authenticationError) {

                    }

                    @Override
                    public void unknownError(String unknownError) {

                    }


                });


    }
    private void connect(String service, String accessToken){
        SocialApi.socialConnect(getActivity(),accessToken, service, new NBOSCallback<NewMemberApiModel>() {
            @Override
            public void onSuccess(Response<NewMemberApiModel> response) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(),R.string.networkError, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onValidationError(List<ValidationMessagesApiModel> validationError) {

            }

            @Override
            public void authenticationError(String authenticationError) {

            }

            @Override
            public void unknownError(String unknownError) {

            }


        });
    }

    private void authorizeAndConnect(String service, String authCode){
        SocialApi.authorizeAndConnect(getActivity(), service, authCode, "", new NBOSCallback<NewMemberApiModel>() {

            @Override
            public void onSuccess(Response<NewMemberApiModel> response) {
                System.out.println(response);

                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(),R.string.networkError, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onValidationError(List<ValidationMessagesApiModel> validationError) {

            }

            @Override
            public void authenticationError(String authenticationError) {

            }

            @Override
            public void unknownError(String unknownError) {

            }

        });
    }




    private static TwitterAuthConfig getAuthConfig(Context context){
        final TwitterAuthConfig authConfig =  new TwitterAuthConfig("fUs0SZi3ZHrKTWamffDIV5jb3", "J5FlL2vay2wtJ4VYXW2VDRG0Fp4GlEv5nSTUVnFKZT7rZFy948");
        Fabric.with(context, new TwitterCore(authConfig), new Digits());
        return authConfig;
    }
    @Override
    public void onValidationSucceeded() {
        Toast.makeText(getActivity(), "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        login();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
