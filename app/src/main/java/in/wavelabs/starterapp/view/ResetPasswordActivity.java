package in.wavelabs.starterapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import in.wavelabs.starterapp.R;
import in.wavelabs.startersdk.ConnectionAPI.AuthApi;
import in.wavelabs.startersdk.ConnectionAPI.NBOSCallback;
import in.wavelabs.startersdk.DataModel.validation.MessagesApiModel;
import in.wavelabs.startersdk.DataModel.validation.ValidationMessagesApiModel;
import retrofit2.Response;

/**
 * Created by vineelanalla on 14/01/16.
 */
public class ResetPasswordActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    @Email
    private EditText emailEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Validator validator = new Validator(this);
        validator.setValidationListener(this);
        setContentView(R.layout.forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emailEditText = (EditText) findViewById(R.id.emailtxt);
        final Button reset = (Button) findViewById(R.id.reset_password);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validator.validate(true);
            }

        });
    }
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onValidationSucceeded() {
        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        resetPassword();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void resetPassword(){
        AuthApi.resetPassword(ResetPasswordActivity.this, emailEditText.getText().toString(), new NBOSCallback<MessagesApiModel>() {

            @Override
            public void onSuccess(Response<MessagesApiModel> response) {
                if (response.isSuccessful()) {
                    Intent i = new Intent(ResetPasswordActivity.this, ResetPasswordSuccess.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(ResetPasswordActivity.this,R.string.networkError, Toast.LENGTH_SHORT).show();

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
}
