package in.wavelabs.starterapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.nbos.capi.api.v0.ErrorUtils;
import com.nbos.capi.api.v0.IdnCallback;
import com.nbos.capi.api.v0.models.FieldErrorApiModel;
import com.nbos.capi.api.v0.models.ValidationErrorResponse;
import com.nbos.capi.modules.identity.v0.IdentityApi;
import com.nbos.capi.modules.identity.v0.models.MemberSignupModel;
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
public class RegisterFragment extends Fragment implements Validator.ValidationListener {

    @NotEmpty
    private EditText firstName, lastName, userName;

    @Checked(message = "You must agree to the terms.")
    private CheckBox checkBox;

    @NotEmpty
    @Email
    private EditText emailEditText;

    @Password(min = 5, scheme = Password.Scheme.ANY)
    private EditText passwordEditText;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RegisterFragment newInstance() {

        return new RegisterFragment();
    }

    Validator validator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        validator = new Validator(this);
        validator.setValidationListener(this);
        View v = inflater.inflate(R.layout.fragment_signup_all, container, false);

        firstName = (EditText) v.findViewById(R.id.firstname);
        lastName = (EditText) v.findViewById(R.id.lastname);
        emailEditText = (EditText) v.findViewById(R.id.emailtxt);
        userName = (EditText) v.findViewById(R.id.username);
        passwordEditText = (EditText) v.findViewById(R.id.password);
        checkBox = (CheckBox) v.findViewById(R.id.checkBox);
        Button signup = (Button) v.findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate(true);

            }
        });
        return v;
    }

    private void createAccount(MemberSignupModel memberSignupModel) {
        final IdentityApi identityApi = IDS.getModuleApi("identity");
        identityApi.signup(memberSignupModel, new IdnCallback<NewMemberApiModel>() {

            @Override
            public void onResponse(Response<NewMemberApiModel> response) {
                Log.i("Success", "Registration Response");

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


    @Override
    public void onValidationSucceeded() {
        Toast.makeText(getActivity(), "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        MemberSignupModel memberSignupModel = new MemberSignupModel();
        memberSignupModel.setFirstName(firstName.getText().toString().trim());
        memberSignupModel.setLastName(lastName.getText().toString().trim());
        memberSignupModel.setUsername(userName.getText().toString().trim());
        memberSignupModel.setEmail(emailEditText.getText().toString());
        memberSignupModel.setPassword(passwordEditText.getText().toString());
        createAccount(memberSignupModel);

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
