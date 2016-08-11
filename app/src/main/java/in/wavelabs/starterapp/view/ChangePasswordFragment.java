package in.wavelabs.starterapp.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.nbos.capi.api.v0.ErrorUtils;
import com.nbos.capi.api.v0.FieldErrorApiModel;
import com.nbos.capi.api.v0.IdnCallback;
import com.nbos.capi.api.v0.RestMessage;
import com.nbos.capi.api.v0.ValidationErrorResponse;
import com.nbos.capi.modules.identity.v0.IdentityApi;
import com.nbos.capi.modules.identity.v0.UpdatePasswordApiModel;
import com.nbos.capi.modules.ids.v0.IDS;

import java.io.IOException;
import java.util.List;

import in.wavelabs.starterapp.R;
import retrofit2.Response;


public class ChangePasswordFragment extends Fragment implements Validator.ValidationListener {

    @Password(min = 5, scheme = Password.Scheme.ANY)
    private EditText currentPassword, newPassword;
    Validator validator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        validator = new Validator(this);
        validator.setValidationListener(this);
        View v = inflater.inflate(R.layout.change_password, container, false);
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

    private void changePassword(String oldPassword, String newPassword) {
        final IdentityApi identityApi = IDS.getModuleApi("identity");
        UpdatePasswordApiModel updatePasswordApiModel = new UpdatePasswordApiModel();
        updatePasswordApiModel.setPasssword(oldPassword);
        updatePasswordApiModel.setNewPassword(newPassword);
        identityApi.updateCredentials(updatePasswordApiModel, new IdnCallback<RestMessage>() {

            @Override
            public void onResponse(Response<RestMessage> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();

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
