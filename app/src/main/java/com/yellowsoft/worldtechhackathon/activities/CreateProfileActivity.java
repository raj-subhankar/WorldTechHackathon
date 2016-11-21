package com.yellowsoft.worldtechhackathon.activities;

/**
 * Created by subhankar on 11/21/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yellowsoft.worldtechhackathon.R;
import com.yellowsoft.worldtechhackathon.SessionManager;
import com.yellowsoft.worldtechhackathon.models.AuthResult;
import com.yellowsoft.worldtechhackathon.network.ApiClient;
import com.yellowsoft.worldtechhackathon.network.ApiInterface;
import com.yellowsoft.worldtechhackathon.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import okhttp3.ResponseBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProfileActivity extends Activity {
    String userName, userId;
    EditText etUsername;
    ImageView ivUserProfile;
    private ProgressDialog pDialog;
    private SessionManager session;
    Uri photoUri;

    public final static int PICK_PHOTO_CODE = 1046;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        final Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        userId = getIntent().getStringExtra("userid");

//        // Check if user is already logged in or not
//        if (session.isLoggedIn()) {
//            // User is already logged in. Take him to main activity
//            Intent i = new Intent(this,
//                    CameraActivity.class);
//            startActivity(i);
//            finish();
//        }

//        final Button signOut = (Button) findViewById(R.id.log_out_button);
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AccountKit.logOut();
//                finish();
//            }
//        });
        etUsername = (EditText) findViewById(R.id.etUsername);
        ivUserProfile = (ImageView) findViewById(R.id.ivUserProfilePic);

        Button btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = etUsername.getText().toString();
                uploadFile(userName);
            }
        });

        ivUserProfile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    startActivityForResult(intent, PICK_PHOTO_CODE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Load the selected image into a preview
            ivUserProfile.setImageBitmap(selectedImage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void uploadFile(String userName) {

        pDialog.setMessage("Uploading ...");
        showDialog();

        String mimeType = getContentResolver().getType(photoUri);
        Log.d("mime type", "mime"+mimeType);
        // create upload service client
        ApiInterface service =
                ApiClient.getClient().create(ApiInterface.class);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, photoUri);
        Log.d("uri", photoUri.toString());
        Log.d("file", file.toString());

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(mimeType), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photos", file.getName(), requestFile);

        // add another part within the multipart request
        String name = userName;
        RequestBody postName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), name);

        // finally, execute the request
        Call<AuthResult> call = service.createProfile(userId, postName, body);
        call.enqueue(new Callback<AuthResult>() {
            @Override
            public void onResponse(Call<AuthResult> call,
                                   Response<AuthResult> response) {
                int statusCode = response.code();
                AuthResult result = response.body();
                Boolean success = result.getSuccess();
                if (success) {
                    // user successfully logged in
                    // Create login session
                    String name = result.getName();
                    String id = result.getId();
                    String email = result.getEmail();
                    String image = result.getImageUrl();
                    Log.d("profilepic", result.getImageUrl());
                    session.setLogin(true, id, email, name, image);

                    // Now store the user in SQLite
//                    String token = result.getToken();

                    // Inserting row in users table
//                    db.addUser(name, email, uid, created_at);
                } else {
                    //Error in login. Get the error message
                    String errorMsg = result.getMessage().toString();
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
                pDialog.dismiss();
                Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<AuthResult> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}


