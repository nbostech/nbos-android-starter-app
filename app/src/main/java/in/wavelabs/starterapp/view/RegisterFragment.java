package in.wavelabs.starterapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import in.wavelabs.starterapp.R;
import in.wavelabs.startersdk.ConnectionAPI.AuthApi;
import in.wavelabs.startersdk.ConnectionAPI.NBOSCallback;
import in.wavelabs.startersdk.DataModel.member.NewMemberApiModel;
import in.wavelabs.startersdk.DataModel.validation.ValidationMessagesApiModel;
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
        View v = inflater.inflate(R.layout.activity_signup_all, container, false);

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
    private void createAccount(String firstName, String lastName, String username, String email, String password){
        AuthApi.createAccount(getActivity(),email,username,firstName,lastName,password, new NBOSCallback<NewMemberApiModel>() {
            @Override
            public void onSuccess(Response<NewMemberApiModel> response) {

                if (response.isSuccessful()) {
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


    @Override
    public void onValidationSucceeded() {
        Toast.makeText(getActivity(), "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        createAccount(firstName.getText().toString().trim(),
                lastName.getText().toString().trim(),
                userName.getText().toString().trim(),
                emailEditText.getText().toString(),
                passwordEditText.getText().toString());

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
