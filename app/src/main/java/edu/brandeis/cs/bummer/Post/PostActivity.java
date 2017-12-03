package edu.brandeis.cs.bummer.Post;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Object;
import android.content.Intent;
import android.graphics.Bitmap;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.*;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.ByteArrayOutputStream;
import java.util.List;


import com.google.firebase.storage.*;

import edu.brandeis.cs.bummer.MainActivity;
import edu.brandeis.cs.bummer.Utils.BottomNavigationHelper;
import edu.brandeis.cs.bummer.Profile.ProfileActivity;
import edu.brandeis.cs.bummer.R;
import edu.brandeis.cs.bummer.Utils.Permissions;



/**
 * Created by CaiweiWu on 2017/11/23.
 */

public class PostActivity extends AppCompatActivity {
    private static final int  CAMERA_REQUEST_CODE = 5;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    static final String TAG = "PostActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = PostActivity.this;
    private Bitmap bitmap;

    private List<Address> addresses = null;
    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";

    // Root Database Name for Firebase Database.
    //String Database_Path = "All_Image_Uploads_Database";
    String Database_Path = "location";

    // Creating button.
    Button ChooseButton, UploadButton, CameraButton, LocateButton;

    // Creating EditText.
    EditText ImageName;

    // Creating ImageView.
    ImageView SelectImage;



    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;



    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    private static final double CURRENT_LATTITUTE = 42.3669;
    private static final double CURRENT_LONGTITUTE = -71.2583;

    private FusedLocationProviderClient mFusedLocationClient;

    ProgressDialog progressDialog;

    private Boolean mLocationPermissionGranted = false;

    private String intentLocation = "";

    private static final String Fine_Location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Coarse_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int Location_Permission_Request_Code = 1234;

    private Location location_pri = new Location("dummyProvider");


    Boolean CheckImageViewEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        location_pri.setLatitude(CURRENT_LATTITUTE);
        location_pri.setLongitude(CURRENT_LONGTITUTE);

        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        //Assign ID'S to button.
        ChooseButton = (Button) findViewById(R.id.choose);
        UploadButton = (Button) findViewById(R.id.share);
        CameraButton = (Button) findViewById(R.id.camera);
        LocateButton = (Button) findViewById(R.id.btn_post_loc);

        getLocationPermission();

        // Assign ID's to EditText.
        ImageName = (EditText) findViewById(R.id.input_text);

        // Assign ID'S to image view.
        SelectImage = (ImageView) findViewById(R.id.image);

        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(PostActivity.this);

        // Adding click listener to Choose image button.
        ChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });
        if(checkPermissionsArray(edu.brandeis.cs.bummer.Utils.Permissions.PERMISSIONS)){

        }else{
            verifyPermissions(edu.brandeis.cs.bummer.Utils.Permissions.PERMISSIONS);
        }
        CameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if((checkPermissions(Manifest.permission.CAMERA))) {
                    Log.d(TAG, "onClick: starting camera");
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
            }


        });


        // Adding click listener to Upload image button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling method to upload selected image on Firebase storage.
                UploadImageFileToFirebaseStorage();

            }
        });
        LocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
                Log.d(TAG, "onClick: locationbtn" + intentLocation);


            }
        });


        // setup buttom nav bar
        setupBottomNavigationView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                SelectImage.setImageBitmap(bm);

                // After selecting image change choose button above text.
                ChooseButton.setText("Image Selected");

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        else if(requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: done taking a photo.");
            Log.d(TAG, "onActivityResult: attempting to navigate to final share screen.");





            try{
                bitmap = (Bitmap) data.getExtras().get("data");
                Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                // Setting up bitmap selected image into ImageView.
                SelectImage.setImageBitmap(bitmap);


            }catch (NullPointerException e){
                Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
            }


        }
    }
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                PostActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(PostActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.

        StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis());

        if(bitmap == null){
            try {

                // Getting selected image into Bitmap.
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);


            } catch (IOException e) {

                e.printStackTrace();
            }
        }


        byte[] bytes = getBytesFromBitmap(bitmap, 100);


        UploadTask uploadTask = null;
        uploadTask = storageReference2nd.putBytes(bytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();


                // Getting image name from EditText and store into string variable.
                String TempImageName = ImageName.getText().toString().trim();

                // Hiding the progressDialog after done uploading.
                progressDialog.dismiss();

                // Showing toast message after done uploading.
                Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                //Get location file path
                String loc_path = toLatLonBin(location_pri.getLatitude(), location_pri.getLongitude());
                Log.e(TAG, "onSuccess: " + loc_path);


                @SuppressWarnings("VisibleForTests")
                ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName, taskSnapshot.getDownloadUrl().toString());

                // Getting image upload ID.
                String ImageUploadId = databaseReference.child(loc_path).push().getKey();


                // Adding image upload id s child element into databaseReference.
                databaseReference.child(loc_path).child(ImageUploadId).setValue(imageUploadInfo);

                addToDBUserPhoto(imageUploadInfo, ImageUploadId);

                //kevin:
                //databaseReference.child(loc_path).push().setValue(imageUploadInfo);



                //navigate to the main feed so the user can see their photo
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Photo upload failed.");
                Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // Setting progressDialog Title.
                progressDialog.setTitle("Image is Uploading...");
            }
        });





    }
    //TODO
    public void addToDBUserPhoto(ImageUploadInfo imageUploadInfo, String photoKey) {

        String Database_Path = "user_photos";
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(photoKey).setValue(imageUploadInfo);

    }
    /*
     * Get the String representation in DB of LatLon
     */
    public static String toLatLonBin(double lat, double lon) {
        double newLat = Math.round(lat * 10000) / 10000.0;
        double newLon = Math.round(lon * 10000) / 10000.0;
        return (newLat + "x" + newLon).replace('.', '_');
    }
    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationHelper.setupBottomNavigation(bottomNavigationViewEx);
        BottomNavigationHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
    public void getDeviceLocation(){
        Log.d(TAG,"getDeviceLocation: getting the devices current location");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionGranted){
                final Task location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        String errorMessage = "";

                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");


                            if ((Location)task.getResult() == null){
                                Log.d(TAG, "onComplete: intentLoc "+ CURRENT_LATTITUTE);

                                //intentLocation = CURRENT_LATTITUTE+ "*" + CURRENT_LONGTITUTE;

                            } else {
                                location_pri = (Location)task.getResult();
                                //intentLocation = location_pri.getLatitude()+ "*" + location_pri.getLongitude();
                                Log.d(TAG, "onComplete: intentLoc"+ location_pri.getLatitude() + " " + location_pri.getLongitude());



                            }
                            Geocoder geocoder = new Geocoder(PostActivity.this);
                            try {
                                addresses = geocoder.getFromLocation(
                                        location_pri.getLatitude(),
                                        location_pri.getLongitude(),
                                        // In this sample, get just a single address.
                                        1);

                                StringBuilder sb = new StringBuilder(addresses.get(0).getAddressLine(0));
                                sb.append(",");
                                sb.append(addresses.get(0).getAddressLine(1));
                                sb.append(",");
                                sb.append(addresses.get(0).getAddressLine(2));


                                LocateButton.setText(sb.toString());
                            } catch (IOException ioException) {
                                // Catch network or other I/O problems.
                                errorMessage = getString(R.string.service_not_available);
                                Log.e(TAG, errorMessage, ioException);
                            } catch (IllegalArgumentException illegalArgumentException) {
                                // Catch invalid latitude or longitude values.
                                errorMessage = getString(R.string.invalid_lat_long_used);
                                Log.e(TAG, errorMessage + ". " +
                                        "Latitude = " + location_pri.getLatitude() +
                                        ", Longitude = " +
                                        location_pri.getLongitude(), illegalArgumentException);
                            }




                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                        }
                    }
                });
            }

        }catch(SecurityException e){
            Log.e(TAG,"getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }


    public void getLocationPermission() {
        Log.d(TAG,"Getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Fine_Location) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Coarse_Location) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;

            }else{
                ActivityCompat.requestPermissions(this,permissions,Location_Permission_Request_Code);
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,Location_Permission_Request_Code);
        }
    }

}





//    FirebaseStorage storage = FirebaseStorage.getInstance();// Folder path for Firebase Storage.
//    String Storage_Path = "All_Image_Uploads/";
//
//    // Root Database Name for Firebase Database.
//    String Database_Path = "All_Image_Uploads_Database";
//    // Creating URI.
//    Uri FilePathUri;
//
//    // Creating StorageReference and DatabaseReference object.
//    DatabaseReference databaseReference;
//
//    // Image request code for onActivityResult() .
//    int Image_Request_Code = 7;
//
//    ProgressDialog progressDialog ;
//
//    private static final String TAG = "PostActivity";
//    private Context mContext = PostActivity.this;
//    private static final int ACTIVITY_NUM = 2;
//    private StorageReference mStorageRef;
//    private Button share_btn;
//    private View imageView;
//    private EditText ImageDesc;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_post);
//        mStorageRef = FirebaseStorage.getInstance().getReference();
//        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
//        imageView = (ImageView) findViewById(R.id.image);
//        ImageDesc = (EditText)findViewById(R.id.input_text);
//        share_btn = (Button) findViewById(R.id.share);
//        progressDialog = new ProgressDialog(PostActivity.this);
//        share_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadPhoto();
//            }
//        });
//    }
//
//
//
//
//    public void uploadPhoto() {
//        Uri file = Uri.fromFile(new File("/Users/wucaiwei/Documents/GitHub/bummer/app/src/main/res/drawable-xhdpi/headshot.png"));
//        StorageReference riversRef = mStorageRef.child("drawable/headshot.png");
//
//        riversRef.putFile(file)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful uploads
//                        // ...
//                    }
//                });
//    }
//checking











