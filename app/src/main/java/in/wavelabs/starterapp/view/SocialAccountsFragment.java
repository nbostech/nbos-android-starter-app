package in.wavelabs.starterapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.nbos.capi.modules.identity.v0.MemberApiModel;
import com.nbos.capi.modules.identity.v0.NewMemberApiModel;
import com.nbos.capi.modules.identity.v0.SocialConnectUrlResponse;

import java.util.Arrays;
import java.util.List;

import in.wavelabs.idn.ConnectionAPI.NBOSCallback;
import in.wavelabs.idn.ConnectionAPI.SocialApi;
import in.wavelabs.starterapp.R;
import retrofit2.Response;


public class SocialAccountsFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener, Validator.ValidationListener {
    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.social_accounts, container, false);

        final LoginButton authButton = (LoginButton) v.findViewById(R.id.facebook_connect);
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email"));

            }
        });

        callbackManager = CallbackManager.Factory.create();

        authButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
//                String accessTokens= loginResult.getAccessToken().getToken();
//                System.out.println(accessTokens);
//                Log.d("AccessToken:", accessToken);
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
        SignInButton signInButton = (SignInButton) v.findViewById(R.id.btn_sign_in);
        signInButton.setSize(SignInButton.SIZE_ICON_ONLY);
        signInButton.setScopes(gso.getScopeArray());
        Button facebook  = (Button) v.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authButton.performClick();

            }
        });
        Button linkedIn = (Button) v.findViewById(R.id.linkedIn);
        Button instagram = (Button) v.findViewById(R.id.instagram);
        Button github = (Button) v.findViewById(R.id.github);

        //webView = (WebView) v.findViewById(R.id.webView1);
     //   LinkedInLogin ll = (LinkedInLogin) getActivity().findViewById(R.id.linkedIn);

        linkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialApi.socialLogin(getActivity(),"linkedIn", new NBOSCallback<SocialConnectUrlResponse>() {

                    @Override
                    public void onResponse(Response<SocialConnectUrlResponse> response) {

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }


                });
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialApi.socialLogin(getActivity(), "instagram", new NBOSCallback<SocialConnectUrlResponse>() {

                    @Override
                    public void onResponse(Response<SocialConnectUrlResponse> response) {

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }



                });
            }
        });
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialApi.socialLogin(getActivity(), "gitHub", new NBOSCallback<SocialConnectUrlResponse>() {

                    @Override
                    public void onResponse(Response<SocialConnectUrlResponse> response) {

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }


                });
            }
        });

        return v;
    }



    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Connect success");

        if(requestCode == 10){
            System.out.println("Connect success REQUEST 10");
            if(resultCode == Activity.RESULT_OK){
               // System.out.println("Connect success RESULT OK");
                if (data != null) {

                    Bundle response = data.getExtras();
                    String service = response.getString("service");
                    String code = response.getString("code");
                    String state = response.getString("state");
                    authorizeAndConnect(service,code,state);

            }

        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

    }
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

                connect(service,authCode);

            }
            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            // updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }
    private void connect(String service, String accessToken){
        SocialApi.socialConnect(getActivity(),accessToken, service, new NBOSCallback<NewMemberApiModel>() {
            @Override
            public void onResponse(Response<NewMemberApiModel> response) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(),R.string.networkError, Toast.LENGTH_SHORT).show();

            }



        });
    }
 private void authorizeAndConnect(final String service, String code, String state){
     SocialApi.authorizeAndConnect(getActivity(), service,code, state, new NBOSCallback<NewMemberApiModel>() {

                        @Override
                        public void onResponse(Response<NewMemberApiModel> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(getActivity(),service +" connected successfully", Toast.LENGTH_SHORT).show();
                            }
//                            nbosCallback.onSuccess(response);

                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }

                    });
 }
}
