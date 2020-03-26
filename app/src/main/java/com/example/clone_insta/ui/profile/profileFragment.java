package com.example.clone_insta.ui.profile;
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
import com.example.clone_insta.MainActivity;
import com.example.clone_insta.R;
import com.example.clone_insta.ui.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class profileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private profileViewModel notificationsViewModel;
    Button signout;
    String url2;
    FirebaseUser user;

    String urll;
    ImageView profile;
    Button upload,uploaddone;
    StorageReference storageReference;
    FirebaseStorage storage;
    ProgressBar progressBar;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private String uid;
   private DatabaseReference myRef;
   private TextView name,email,country;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference();

        notificationsViewModel =
               new ViewModelProvider(this).get(profileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user=mAuth.getCurrentUser();
        name=getView().findViewById(R.id.Name);
        country=getView().findViewById(R.id.country_profile);
        email=getView().findViewById(R.id.email_profile);
        progressBar=getView().findViewById(R.id.profile_progress);
        uid=user.getUid();
        uploaddone=getView().findViewById(R.id.upload_done);
        progressBar.setVisibility(View.INVISIBLE);
        upload=getView().findViewById(R.id.upload_dp);
        profile=getView().findViewById(R.id.profile_pic);
        signout=getView().findViewById(R.id.signout);
        storage = FirebaseStorage.getInstance("gs://cloneinsta-5f275.appspot.com");
        storageReference = storage.getReference();
        setdetail();
        signout.setOnClickListener(new  View.OnClickListener(){
            public void onClick (View v)
            {
                mAuth.getCurrentUser();
                mAuth.signOut();
                intent_send_log_in();
            }
        }
        );
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SelectImage();
                upload.setEnabled(true);
            }
        });
        uploaddone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               uploadconfig();
            }
        });
    }

    private   void intent_send_log_in(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void setdetail(){
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        myRef=FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // String value = dataSnapshot.getValue(String.class);
               // demoValue.setText(value);
                String name2="Name-\n"+dataSnapshot.child("first").getValue(String.class)+" "+dataSnapshot.child("last").getValue(String.class);
                name.setText(name2);
                String country2="Country-"+dataSnapshot.child("country").getValue(String.class);
                country.setText(country2);
                String email2="Email-\n"+dataSnapshot.child("email").getValue(String.class);
                email.setText(email2);
                urll= dataSnapshot.child("urlImage").getValue(String.class);
               if(!urll.equals("")){
                Glide.with(getContext())
                        .load(urll)
                        .into(profile);
               }
               else
               {
                   profile.setImageResource(R.drawable.noimage);
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Select Image method
    private void SelectImage()
    {

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
                profile.setImageBitmap(bitmap);
                uploadImage();
            }
        catch (IOException e)
        {
            Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
        }
        }
    }
    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {
            progressBar.setVisibility(View.VISIBLE);
            // Code for showing progressDialog while uploading
            upload.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            user=mAuth.getCurrentUser();
            uid=user.getUid();
            // Defining the child of storageReference
            final StorageReference ref
                    = storageReference
                    .child(uid);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    // Image uploaded successfully
                                   ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                       @Override
                                       public void onSuccess(Uri uri) {
                                           //Do what you want with the url
                                            url2=String.valueOf(uri);

                                           Log.i("Tag",url2);
                                       }
                                   }
                                   );
                                    // Dismiss dialog
                                    uid=user.getUid();

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
                        public void onFailure(@NonNull Exception e)
                        {

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
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    Toast.makeText(getContext(),"Working",Toast.LENGTH_SHORT).show();
                                }
                            });
        }

        myRef=FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
      if(url2!=null) {
          myRef.child(uid).child("urlImage").setValue(url2);
          Glide.with(getContext())
                  .load(url2)
                  .into(profile);
          upload.setEnabled(true);
      }
    }
    public void uploadconfig(){
        myRef=FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        if(url2!=null) {
            myRef.child(uid).child("urlImage").setValue(url2);
            Glide.with(getContext())
                    .load(url2)
                    .into(profile);
            upload.setEnabled(true);
        }
    }
}


