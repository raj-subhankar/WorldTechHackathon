package com.yellowsoft.worldtechhackathon.activities;

/**
 * Created by subhankar on 11/21/2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationServices;
import com.yellowsoft.worldtechhackathon.network.ApiClient;
import com.yellowsoft.worldtechhackathon.network.ApiInterface;
import com.yellowsoft.worldtechhackathon.R;
import com.yellowsoft.worldtechhackathon.SessionManager;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.yellowsoft.worldtechhackathon.utils.FileUtils;

public class UploadActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener{
    // LogCat tag
    private static final String TAG = UploadActivity.class.getSimpleName();
    private Uri photoUri;
    private ProgressDialog pDialog;
    private SessionManager session;

    protected Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        setContentView(R.layout.activity_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
//        setSupportActionBar(toolbar);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Intent intent = getIntent();
        photoUri = intent.getParcelableExtra("imageUri");

        // Do something with the photo based on Uri
        Bitmap selectedImage = null;
        try {
            selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load the selected image into a preview
        ImageView ivPreview = (ImageView) findViewById(R.id.ivPhoto);
        ivPreview.setImageBitmap(selectedImage);
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Method to display the location on UI
     * */
    private void getLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            Toast.makeText(getApplicationContext(),
                    "Lat: " + latitude + " Long: " + longitude, Toast.LENGTH_LONG)
                    .show();

        } else {

            Log.d(TAG, "(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miUpload) {
            uploadFile(photoUri);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadFile(Uri fileUri) {

        pDialog.setMessage("Uploading ...");
        showDialog();

        session = new SessionManager(getApplicationContext());

        String mimeType = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        // create upload service client
        ApiInterface service =
                ApiClient.getClient().create(ApiInterface.class);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(mimeType), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photos", file.getName(), requestFile);

        // add another part within the multipart request
        //userId
        String userId = session.getId();
        RequestBody user =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), userId);

        // add another part within the multipart request
        EditText desc = (EditText) findViewById(R.id.etDescription);
        String bodyText = desc.getText().toString();

        RequestBody postBody =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), bodyText);

        RequestBody lat =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), Double.toString(latitude));

        RequestBody lng =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), Double.toString(longitude));

        Log.d(TAG, "lat: "+latitude+" long: "+longitude);

//        String token = session.getToken();
//        RequestBody tokenField =
//                RequestBody.create(
//                        MediaType.parse("multipart/form-data"), token);


        // finally, execute the request
        Call<ResponseBody> call = service.upload(user, body, postBody, lat, lng);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
                hideDialog();
                //Main activity
                hideDialog();
                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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
