package in.wavelabs.starterapp.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.nbos.capi.api.v0.ErrorUtils;
import com.nbos.capi.api.v0.IdnCallback;
import com.nbos.capi.api.v0.models.FieldErrorApiModel;
import com.nbos.capi.api.v0.models.ValidationErrorResponse;
import com.nbos.capi.modules.identity.v0.IdentityApi;
import com.nbos.capi.modules.identity.v0.models.LoginModel;
import com.nbos.capi.modules.identity.v0.models.NewMemberApiModel;
import com.nbos.capi.modules.ids.v0.IDS;

import java.io.IOException;
import java.util.List;
import in.wavelabs.starterapp.R;
import in.wavelabs.starterapp.util.Prefrences;
import retrofit2.Response;

/**
 * Created by vineelanalla on 08/03/16.
 */
public class LoginFragment extends Fragment implements Validator.ValidationListener {
    private static final String TAG = "SignInActivity";

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

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        final Validator validator = new Validator(getActivity());
//        validator.setValidationListener(getActivity());
        validator = new Validator(this);
        validator.setValidationListener(this);

        View v = inflater.inflate(R.layout.fragment_login_all, container, false);
        initializeView(v);
        return v;
    }
    private void initializeView(View v){

        requestSmsPermission();
     //   callbackManager = CallbackManager.Factory.create();
        TextView rst = (TextView) v.findViewById(R.id.forgot);

        rst.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), ResetPasswordActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(0, 0);
        });
        TextView signup = (TextView) v.findViewById(R.id.register);
        signup.setOnClickListener(view -> {
//                Intent i = new Intent(getActivity(), SignUpActivity.class);
//                startActivity(i);
//                getActivity().overridePendingTransition(0, 0);
            ((AuthActivity)getActivity()).getViewPager().setCurrentItem(1);


        });
        emailEditText = (EditText) v.findViewById(R.id.emailtxt);
        passwordEditText = (EditText) v.findViewById(R.id.password);

        TextView login = (TextView) v.findViewById(R.id.login);
        login.setOnClickListener(view -> validator.validate(true));
        TextView loginWithSocial = (TextView) v.findViewById(R.id.loginWithSocial);
        TextView skipLogin = (TextView) v.findViewById(R.id.skipLogin);

        loginWithSocial.setOnClickListener(view -> {
            Fragment vehicleEntryFragment = new SocialLoginFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.maincontainer, vehicleEntryFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        skipLogin.setOnClickListener(view -> {
           Intent i = new Intent(getActivity(), MainActivity.class);
            i.putExtra("flag","skipLogin");
            startActivity(i);
            getActivity().finish();
        });


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
        final IdentityApi identityApi = IDS.getModuleApi("identity");
        LoginModel loginModel = new LoginModel();
        loginModel.setUsername(emailEditText.getText().toString());
        loginModel.setPassword(passwordEditText.getText().toString());
        identityApi.login(loginModel, new IdnCallback<NewMemberApiModel>() {
            @Override
            public void onResponse(Response<NewMemberApiModel> response) {
                Log.i("Success", "Login Response");

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
            public void onFailure(Throwable throwable) {
                Log.i("Error","Login Failure");
                Toast.makeText(getActivity(),R.string.networkError, Toast.LENGTH_SHORT).show();

            }
        });

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
