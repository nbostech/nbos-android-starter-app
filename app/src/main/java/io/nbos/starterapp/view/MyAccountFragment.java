package io.nbos.starterapp.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.greysonparrelli.permiso.Permiso;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import io.nbos.capi.api.v0.IdnCallback;
import io.nbos.capi.api.v0.models.RestMessage;
import io.nbos.capi.modules.identity.v0.IdentityApi;
import io.nbos.capi.modules.identity.v0.models.MemberApiModel;
import io.nbos.capi.modules.ids.v0.IDS;
import io.nbos.capi.modules.media.v0.MediaApi;
import io.nbos.capi.modules.media.v0.models.MediaApiModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.nbos.starterapp.R;
import io.nbos.starterapp.util.CircleTransform;
import io.nbos.starterapp.util.Prefrences;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;


public class MyAccountFragment extends Fragment implements Validator.ValidationListener {

    private static final int RESULT_LOAD_IMAGE = 999;
    @NotEmpty
    EditText email, firstName, lastName, phone;
    EditText description;

    ImageView profilePic, navProfilePic;
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
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navProfilePic = (ImageView) headerView.findViewById(R.id.profile);
        updateBtn.setOnClickListener(view -> validator.validate(true));
        profilePic = (ImageView) v.findViewById(R.id.profilepic);
        profilePic.setOnClickListener(view -> Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    // Permission granted!
                } else {
                    // Permission denied.
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog("Gallery Permission", "Needs Read External Storage Permission", null, callback);
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE));
        getProfile(String.valueOf(Prefrences.getUserId(getActivity())));
        getProfilePic(String.valueOf(Prefrences.getUserId(getActivity())));
        return v;
    }

    private void getProfile(String uuid) {
        IdentityApi identityApi = IDS.getModuleApi("identity");

        identityApi.getMemberDetails(uuid, new IdnCallback<MemberApiModel>() {


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
                updateProfilePic(Prefrences.getUserId(getActivity()), "profile", picturePath);
                cursor.close();
            }
        }
    }
    private void updateProfilePic(String id, String mediafor, String fileName) {

        Map<String, RequestBody> map = new HashMap<>();
        Uri imageUri = Uri.fromFile(new File(fileName));
        if (imageUri != null) {
            File file = new File(imageUri.getPath());
            String fileExtension = imageUri.getPath().substring(imageUri.getPath().lastIndexOf(".") + 1);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/" + fileExtension), file);
            if (file.exists()) {
                map.put("file\"; filename=\"" + file.getName() + "", fileBody);

            }
        }
        MediaApi mediaApi = IDS.getModuleApi("media");
        if(mediaApi !=null)

            mediaApi.uploadMedia(id,mediafor,map, new IdnCallback<RestMessage>() {


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

        MediaApi mediaApi = IDS.getModuleApi("media");
        if(mediaApi != null)
        mediaApi.getMedia(uuid,"profile",new IdnCallback<MediaApiModel>() {

            @Override
            public void onResponse(Response<MediaApiModel> response) {
                if (response.body() != null) {
                    Picasso.with(getActivity()).load(response.body().getMediaFileDetailsList().get(1)
                            .getMediapath())
                            .transform(new CircleTransform())
                            .placeholder(R.mipmap.ic_account_circle_black_48dp)
                            .into(profilePic);
                    Picasso.with(getActivity()).load(response.body().getMediaFileDetailsList().get(1)
                            .getMediapath())
                            .transform(new CircleTransform())
                            .placeholder(R.mipmap.ic_account_circle_black_48dp)
                            .into(navProfilePic);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println(t);
                Toast.makeText(getActivity(),R.string.networkError, Toast.LENGTH_SHORT).show();


            }



        });

    }
    private void updateProfile(MemberApiModel memberApiModel, String uuid){
        IdentityApi identityApi = IDS.getModuleApi("identity");
        identityApi.updateMemberDetails(uuid,memberApiModel, new IdnCallback<MemberApiModel>() {

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
        MemberApiModel memberApiModel = new MemberApiModel();
        memberApiModel.setFirstName(firstName.getText().toString());
        memberApiModel.setLastName(lastName.getText().toString());
        memberApiModel.setPhone(phone.getText().toString());
        memberApiModel.setDescription(description.getText().toString());
        updateProfile(memberApiModel,Prefrences.getUserId(getActivity()));
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