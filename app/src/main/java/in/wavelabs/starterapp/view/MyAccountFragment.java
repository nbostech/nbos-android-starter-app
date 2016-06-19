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
import com.nbos.capi.api.v0.RestMessage;
import com.nbos.capi.modules.identity.v0.MemberApiModel;
import com.nbos.capi.modules.media.v0.MediaApiModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.wavelabs.idn.ConnectionAPI.MediaApi;
import in.wavelabs.idn.ConnectionAPI.NBOSCallback;
import in.wavelabs.idn.ConnectionAPI.UsersApi;
import in.wavelabs.starterapp.R;
import in.wavelabs.starterapp.util.CircleTransform;
import in.wavelabs.starterapp.util.Prefrences;
import retrofit2.Response;


public class MyAccountFragment extends Fragment implements Validator.ValidationListener {

    private static final int RESULT_LOAD_IMAGE = 999;
    @NotEmpty
    EditText email, firstName, lastName, phone;
    EditText description;

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
        firstName = (EditText) v.findViewById(R.id.firstName);
        firstName.setText(Prefrences.getFirstName(getActivity()));
        lastName = (EditText) v.findViewById(R.id.lastName);
        lastName.setText(Prefrences.getLastName(getActivity()));
        email = (EditText) v.findViewById(R.id.emailtxt);
        email.setText(Prefrences.getEmailId(getActivity()));
        phone = (EditText) v.findViewById(R.id.phone);
        phone.setText(Prefrences.getPhoneNumber(getActivity()).toString());
        description = (EditText) v.findViewById(R.id.description);
        description.setText(Prefrences.getDescription(getActivity()));
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
        getProfile(String.valueOf(Prefrences.getUserId(getActivity())));
        getProfilePic(String.valueOf(Prefrences.getUserId(getActivity())));
        return v;
    }

    private void getProfile(String uuid) {
        UsersApi.getUserProfile(getActivity(),uuid, new NBOSCallback<MemberApiModel>() {


            @Override
            public void onResponse(Response<MemberApiModel> response) {

            }

            @Override
            public void onFailure(Throwable t) {

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
    private void updateProfilePic(String id, String fileName) {
        MediaApi.updateProfileImage(getActivity(), fileName, id, new NBOSCallback<RestMessage>() {


            @Override
            public void onResponse(Response<RestMessage> response) {
               getProfilePic(String.valueOf(Prefrences.getUserId(getActivity())));

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(),R.string.networkError, Toast.LENGTH_SHORT).show();

            }




        });
    }

    private void getProfilePic(String uuid) {
        MediaApi.getProfileImage(getActivity(),uuid,new NBOSCallback<MediaApiModel>() {

            @Override
            public void onResponse(Response<MediaApiModel> response) {
                if (response.body() != null) {
                    Picasso.with(getActivity()).load(response.body().getMediaFileDetailsList().get(1)
                            .getMediapath())
                            .transform(new CircleTransform())
                            .placeholder(R.mipmap.ic_account_circle_black_48dp)
                            .into(profilePic);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println(t);
                Toast.makeText(getActivity(),R.string.networkError, Toast.LENGTH_SHORT).show();


            }



        });

    }
    private void updateProfile(String firstName, String lastName,Long phone,String description, String uuid){
        UsersApi.updateProfile(getActivity(), firstName, lastName,phone,description,uuid, new NBOSCallback<MemberApiModel>() {

            @Override
            public void onResponse(Response<MemberApiModel> response) {
                getProfile(response.body().getUuid());
            }

            @Override
            public void onFailure(Throwable t) {

            }

        });
    }
    @Override
    public void onValidationSucceeded() {
        Toast.makeText(getActivity(), "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        updateProfile(firstName.getText().toString(),lastName.getText().toString(),Long.valueOf(phone.getText().toString()),description.getText().toString(),Prefrences.getUserId(getActivity()));
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