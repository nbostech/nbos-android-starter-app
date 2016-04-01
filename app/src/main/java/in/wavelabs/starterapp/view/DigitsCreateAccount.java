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
import in.wavelabs.startersdk.ConnectionAPI.NBOSCallback;
import in.wavelabs.startersdk.ConnectionAPI.SocialApi;
import in.wavelabs.startersdk.DataModel.member.NewMemberApiModel;
import in.wavelabs.startersdk.DataModel.validation.ValidationMessagesApiModel;
import retrofit2.Response;

/**
 * Created by vineelanalla on 18/03/16.
 */
public class DigitsCreateAccount extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty
    EditText firstName, lastName;
    @NotEmpty
    @Email
    EditText emailId;
    Validator validator;
    String provider, authCredentials, digitsEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new Validator(this);
        validator.setValidationListener(this);
        setContentView(R.layout.digits_account_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
         provider = i.getStringExtra("provider");
         authCredentials = i.getStringExtra("authCredentials");
         digitsEmail = i.getStringExtra("emailId");
        firstName = (EditText) findViewById(R.id.firstname);
        lastName = (EditText) findViewById(R.id.lastname);
        emailId = (EditText) findViewById(R.id.emailtxt);
        emailId.setText(digitsEmail);
        Button signup  = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validator.validate(true);
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        Toast.makeText(DigitsCreateAccount.this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        digitsConnect(provider,authCredentials,digitsEmail,firstName.getText().toString().trim(),
                lastName.getText().toString().trim());

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(DigitsCreateAccount.this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(DigitsCreateAccount.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
    private void digitsConnect(String provider, String authCredentials, String emailId, String firstName, String lastName){
        SocialApi.digitsConnect(DigitsCreateAccount.this, provider,
                authCredentials, firstName, lastName, emailId, new NBOSCallback<NewMemberApiModel>() {


                    @Override
                    public void onSuccess(Response<NewMemberApiModel> response) {
                        Intent i = new Intent(DigitsCreateAccount.this, MainActivity.class);
                        startActivity(i);
                        DigitsCreateAccount.this.overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onFailure(Throwable t) {

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
