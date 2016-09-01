package io.nbos.starterapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import io.nbos.capi.api.v0.ErrorUtils;
import io.nbos.capi.api.v0.IdnCallback;
import io.nbos.capi.api.v0.models.FieldErrorApiModel;
import io.nbos.capi.api.v0.models.ValidationErrorResponse;
import io.nbos.capi.modules.identity.v0.IdentityApi;
import io.nbos.capi.modules.identity.v0.models.NewMemberApiModel;
import io.nbos.capi.modules.identity.v0.models.SocialConnectApiModel;
import io.nbos.capi.modules.ids.v0.IDS;

import java.util.ArrayList;
import java.util.List;

import io.nbos.starterapp.R;
import io.nbos.starterapp.adapter.AvailableSocialConnectsAdapter;
import io.nbos.starterapp.adapter.ConnectedSocialConnectsAdapter;
import io.nbos.starterapp.models.SocialConnects;
import retrofit2.Response;


public class SocialConnectsFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener, Validator.ValidationListener {
    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    RecyclerView socialAccounts, connectedAccounts;
    RecyclerView.LayoutManager mLayoutManager, mConnectedLayoutManager;
    List<SocialConnects> socialConnects;
    AvailableSocialConnectsAdapter mAvailableAdapter;
    ConnectedSocialConnectsAdapter mConnectedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.social_accounts, container, false);
        socialAccounts = (RecyclerView) v.findViewById(R.id.socialAccounts);
        connectedAccounts = (RecyclerView) v.findViewById(R.id.connectedAccounts);
        mLayoutManager = new GridLayoutManager(getActivity(), 4);
        mConnectedLayoutManager = new GridLayoutManager(getActivity(), 4);

        socialAccounts.setLayoutManager(mLayoutManager);
        connectedAccounts.setLayoutManager(mConnectedLayoutManager);

        // connectedAccounts.setLayoutManager(mLayoutManager);
        socialConnects = new ArrayList<>();
        SocialConnects socialConnect = new SocialConnects();
        socialConnect.setConnectName("Facebook");
        socialConnects.add(socialConnect);
        SocialConnects socialConnect2 = new SocialConnects();
        socialConnect2.setConnectName("Google");
        socialConnects.add(socialConnect2);
        SocialConnects socialConnect3 = new SocialConnects();
        socialConnect3.setConnectName("Instagram");
        socialConnects.add(socialConnect3);
        SocialConnects socialConnect4 = new SocialConnects();
        socialConnect4.setConnectName("gitHub");
        socialConnects.add(socialConnect4);
        SocialConnects socialConnect5 = new SocialConnects();
        socialConnect5.setConnectName("linkedIn");
        socialConnects.add(socialConnect5);


        mAvailableAdapter = new AvailableSocialConnectsAdapter(getContext(), socialConnects);
        mConnectedAdapter = new ConnectedSocialConnectsAdapter(getContext(), socialConnects);

        socialAccounts.setAdapter(mAvailableAdapter);
        connectedAccounts.setAdapter(mConnectedAdapter);

        LoginButton ab = new LoginButton(getActivity());
//        ab.findViewById(R.id.facebook);
//        ab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email"));
//
//            }
//        });

        callbackManager = CallbackManager.Factory.create();

        ab.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                String accessTokens= loginResult.getAccessToken().getToken();
//                System.out.println(accessTokens);
//                Log.d("AccessToken:", accessToken);
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
        SignInButton signInButton = new SignInButton(getActivity());
        signInButton.findViewById(R.id.btn_sign_in);
        signInButton.setSize(SignInButton.SIZE_ICON_ONLY);
        signInButton.setScopes(gso.getScopeArray());

//        Button linkedIn = (Button) v.findViewById(R.id.linkedIn);
//        Button instagram = (Button) v.findViewById(R.id.instagram);
//        Button github = (Button) v.findViewById(R.id.github);

        //webView = (WebView) v.findViewById(R.id.webView1);
        //   LinkedInLogin ll = (LinkedInLogin) getActivity().findViewById(R.id.linkedIn);

//        linkedIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IdentityApi identityApi = IDS.getModuleApi("identity");
//
//                identityApi.socialWebViewLogin("linkedIn", new IdnCallback<SocialConnectUrlResponse>() {
//
//                    @Override
//                    public void onResponse(Response<SocialConnectUrlResponse> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//
//                    }
//
//
//                });
//            }
//        });
//        instagram.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IdentityApi identityApi = IDS.getModuleApi("identity");
//                identityApi.socialWebViewLogin("instagram", new IdnCallback<SocialConnectUrlResponse>() {
//
//                    @Override
//                    public void onResponse(Response<SocialConnectUrlResponse> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//
//                    }
//
//
//
//                });
//            }
//        });
//        github.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IdentityApi identityApi = IDS.getModuleApi("identity");
//                identityApi.socialWebViewLogin("gitHub", new IdnCallback<SocialConnectUrlResponse>() {
//
//                    @Override
//                    public void onResponse(Response<SocialConnectUrlResponse> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//
//                    }
//
//
//                });
//            }
//        });

        return v;
    }


    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Connect success");
        // if(requestCode == 10){
        System.out.println("Connect success REQUEST 10");
        if (resultCode == Activity.RESULT_OK) {
            // System.out.println("Connect success RESULT OK");
            if (data != null) {
                Bundle response = data.getExtras();
                String service = response.getString("service");
                String code = response.getString("code");
                String state = response.getString("state");
                authorizeAndConnect(service, code, state);
            }

            //    }
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
                System.out.println("AuthCode::" + acct.getServerAuthCode());
                String idToken = acct.getIdToken();
                String authCode = acct.getServerAuthCode();
                String service = "googlePlus";

                connect(service, authCode);

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

    private void connect(String service, String accessToken) {
        IdentityApi identityApi = IDS.getModuleApi("identity");
        SocialConnectApiModel socialConnectApiModel = new SocialConnectApiModel();
        socialConnectApiModel.setAccessToken(accessToken);
        identityApi.connect(socialConnectApiModel, service, new IdnCallback<NewMemberApiModel>() {
            @Override
            public void onResponse(Response<NewMemberApiModel> response) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(), R.string.networkError, Toast.LENGTH_SHORT).show();

            }


        });
    }

    private void authorizeAndConnect(final String service, String code, String state) {
        final IdentityApi identityApi = IDS.getModuleApi("identity");

        identityApi.authorize(service, code, state, new IdnCallback<NewMemberApiModel>() {

            @Override
            public void onResponse(Response<NewMemberApiModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), service + " connected successfully", Toast.LENGTH_SHORT).show();
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

            }

        });
    }
}
