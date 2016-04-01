package in.wavelabs.starterapp.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import in.wavelabs.starterapp.R;
import in.wavelabs.startersdk.ConnectionAPI.AuthApi;
import in.wavelabs.startersdk.ConnectionAPI.NBOSCallback;
import in.wavelabs.startersdk.DataModel.validation.MessagesApiModel;
import in.wavelabs.startersdk.DataModel.validation.ValidationMessagesApiModel;
import retrofit2.Response;


public class ChangePasswordFragment extends Fragment implements Validator.ValidationListener{

    @Password(min = 5, scheme = Password.Scheme.ANY)
    private EditText currentPassword,newPassword;
Validator validator;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        validator = new Validator(this);
        validator.setValidationListener(this);
        View v =  inflater.inflate(R.layout.change_password, container, false);
        currentPassword = (EditText) v.findViewById(R.id.currentPassword);
        newPassword = (EditText) v.findViewById(R.id.newPassword);
        Button changePassword = (Button) v.findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate(true);

            }
        });

        return v;
    }
    private void changePassword(String oldPassword, String newPassword){
        AuthApi.changePassword(getActivity(), oldPassword, newPassword, new NBOSCallback<MessagesApiModel>() {

            @Override
            public void onSuccess(Response<MessagesApiModel> response) {
                Toast.makeText(getActivity(),R.string.success,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(),R.string.networkError,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValidationError(List<ValidationMessagesApiModel> validationError) {
                Toast.makeText(getActivity(),R.string.validationError,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void authenticationError(String authenticationError) {
                Toast.makeText(getActivity(),R.string.authenticationError,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void unknownError(String unknownError) {
                Toast.makeText(getActivity(),R.string.unknownError,Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onValidationSucceeded() {
        Toast.makeText(getActivity(), "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        changePassword(currentPassword.getText().toString(), newPassword.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)s
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
