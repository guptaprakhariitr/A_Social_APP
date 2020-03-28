package com.example.clone_insta.ui.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.clone_insta.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class uploadFragment extends Fragment {
    Button gallery, camera, upload;
    EditText caption;
    ImageView upload_image;
    String url2;
    public String key;
    FirebaseUser user;
    StorageReference storageReference;
    FirebaseStorage storage;
    private FirebaseAuth mAuth;
    String urll;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private String uid;
    private DatabaseReference myRef;
    ProgressBar progressBar;
    private uploadViewModel homeViewModel;
    private static final int CAMERA_REQUEST_CODE=1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference();
        homeViewModel =
                new ViewModelProvider(this).get(uploadViewModel.class);
        View root = inflater.inflate(R.layout.fragment_upload, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        gallery = getView().findViewById(R.id.upload_gallery);
        camera = getView().findViewById(R.id.upload_camera);
        upload = getView().findViewById(R.id.upload);
        caption = getView().findViewById(R.id.caption);
        progressBar.setVisibility(View.INVISIBLE);
        upload_image = getView().findViewById(R.id.image_uploaded);
        progressBar = getView().findViewById(R.id.progressbar);
        storage = FirebaseStorage.getInstance("gs://cloneinsta-5f275.appspot.com");
        storageReference = storage.getReference();
        myRef=FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
             key=myRef.push().getKey();
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(View.VISIBLE);
               SelectImage();
            }
        });
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                uploadconfig();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }
        });
    }

    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();


            // Setting image on image view using Bitmap
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getActivity().getContentResolver(),
                                filePath);
                upload_image.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){

           filePath = data.getData();

           /*            mSelectImage.setImageURI(mImageUri);
           CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        Bitmap mImageUri1 = (Bitmap) data.getExtras().get("data");
         mSelectImage.setImageBitmap(mImageUri1);

          Toast.makeText(this, "Image saved to:\n" +
                  data.getExtras().get("data"), Toast.LENGTH_LONG).show();
*/
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getActivity().getContentResolver(),
                                filePath);
                upload_image.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {
            progressBar.setVisibility(View.VISIBLE);
            // Code for showing progressDialog while uploading
            upload.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            user = mAuth.getCurrentUser();
            uid = user.getUid();
            // Defining the child of storageReference
            final StorageReference ref
                    = storageReference
                    .child(uid).child(key);
            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded successfully
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                  @Override
                                                                                  public void onSuccess(Uri uri) {
                                                                                      //Do what you want with the url
                                                                                      url2 = String.valueOf(uri);

                                                                                      Log.i("Tag", url2);
                                                                                  }
                                                                              }
                                    );
                                    // Dismiss dialog
                                    uid = user.getUid();

                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast
                                            .makeText(getContext(),
                                                    "Image Selected!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    upload.setEnabled(true);
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast
                                    .makeText(getContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    Toast.makeText(getContext(), "Working", Toast.LENGTH_SHORT).show();
                                }
                            });
        }

    }
    public void uploadconfig(){
        myRef=FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        if(url2!=null) {
            myRef.child(uid).child("posts").child("photo").child(key).setValue(url2);
            if(caption.getText()==null) {
                myRef.child(uid).child("posts").child("caption").child(key).setValue("");
            }
            else{
                String cap=caption.getText().toString();
                myRef.child(uid).child("posts").child("caption").child(key).setValue(cap);
            }
            Glide.with(getContext())
                    .load(url2)
                    .into(upload_image);
            upload.setEnabled(true);
        }
        progressBar.setVisibility(View.INVISIBLE);
        myRef=FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        key=myRef.push().getKey();
    }
}
