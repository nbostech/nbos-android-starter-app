package in.wavelabs.starterapp.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.wavelabs.starterapp.R;
import in.wavelabs.startersdk.ConnectionAPI.MediaApi;
import in.wavelabs.startersdk.ConnectionAPI.NBOSCallback;
import in.wavelabs.startersdk.ConnectionAPI.UsersApi;
import in.wavelabs.startersdk.DataModel.media.MediaApiModel;
import in.wavelabs.startersdk.DataModel.member.MemberApiModel;
import in.wavelabs.startersdk.DataModel.validation.MessagesApiModel;
import in.wavelabs.startersdk.DataModel.validation.ValidationMessagesApiModel;
import in.wavelabs.startersdk.Utils.Prefrences;
import retrofit2.Response;


public class MyAccountFragment extends Fragment implements Validator.ValidationListener {

    private static final int RESULT_LOAD_IMAGE = 999;
    @NotEmpty
    EditText email, firstName, lastName;

    ImageView profilePic;
    Validator validator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        validator = new Validator(this);
        validator.setValidationListener(this);
        View v =  inflater.inflate(R.layout.editprofile_fragment, container, false);
        email = (EditText) v.findViewById(R.id.emailtxt);
        firstName = (EditText) v.findViewById(R.id.firstname);
        lastName = (EditText) v.findViewById(R.id.lastname);
        Button updateBtn = (Button) v.findViewById(R.id.update);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate(true);
            }
        });
        profilePic = (ImageView) v.findViewById(R.id.profilepic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
        getProfile();
        getProfilePic();
        return v;
    }

    private void getProfile() {
        UsersApi.getUserProfile(getActivity(), new NBOSCallback<MemberApiModel>() {

            @Override
            public void onSuccess(Response<MemberApiModel> response) {
                email.setText(response.body().getEmail());
                firstName.setText(response.body().getFirstName());
                lastName.setText(response.body().getLastName());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data)
        {
            Uri selectedImg = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImg,
                    filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            int columnIndex = 0;
            if (cursor != null) {
                columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String picturePath = cursor.getString(columnIndex);
                updateProfilePic(Prefrences.getUserId(getActivity()), picturePath);
                cursor.close();
            }
        }
    }
    private void updateProfilePic(long id, String fileName) {
        MediaApi.updateProfileImage(getActivity(), fileName, id, new NBOSCallback<MessagesApiModel>() {


            @Override
            public void onSuccess(Response<MessagesApiModel> response) {
               getProfilePic();

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

    private void getProfilePic() {
        MediaApi.getProfileImage(getActivity(), new NBOSCallback<MediaApiModel>() {

            @Override
            public void onSuccess(Response<MediaApiModel> response) {
                     Picasso.with(getActivity()).load(response.body().getMediaFileDetailsList().get(1).getMediapath()).into(profilePic);

            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println(t);
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
    private void updateProfile(String firstName, String lastName){
        UsersApi.updateProfile(getActivity(), firstName, lastName, new NBOSCallback<MemberApiModel>() {

            @Override
            public void onSuccess(Response<MemberApiModel> response) {
                getProfile();
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
    @Override
    public void onValidationSucceeded() {
        Toast.makeText(getActivity(), "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        updateProfile(firstName.getText().toString(),lastName.getText().toString());
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