package com.example.clone_insta.ui.upload;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        progressBar = getView().findViewById(R.id.progressbar);
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        gallery = getView().findViewById(R.id.upload_gallery);
        camera = getView().findViewById(R.id.upload_camera);
        upload = getView().findViewById(R.id.upload);
        caption = getView().findViewById(R.id.caption);
        progressBar.setVisibility(View.INVISIBLE);
        upload_image = getView().findViewById(R.id.image_uploaded);

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
                Log.i("Tag",caption.getText().toString());
                uploadconfig();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              select_camera_image();
            }
        });
    }
private void select_camera_image(){
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
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
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
          /* filePath = data.getData();
           Log.i("Tag",filePath.toString());
            upload_image.setImageURI(filePath);*/
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            upload_image.setImageBitmap(imageBitmap);
            FileOutputStream fileOutputStream=null;
            File file=getdisc();
            if (!file.exists() && !file.mkdirs())
            {
                Toast.makeText(getContext(),"sorry can not make dir",Toast.LENGTH_LONG).show();
                return;
            }
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyymmsshhmmss");
            String date=simpleDateFormat.format(new Date());
            String name="img"+date+".jpeg";
            String file_name=file.getAbsolutePath()+"/"+name; File new_file=new File(file_name);
            try {
                fileOutputStream =new FileOutputStream(new_file);
                Bitmap bitmap=viewToBitmap(upload_image,upload_image.getWidth(),upload_image.getHeight());
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                Toast.makeText(getContext(),"Success", Toast.LENGTH_LONG).show();
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            catch
            (FileNotFoundException e) {

            } catch (IOException e) {

            }
            refreshGallary(file);
            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imageBitmap, key , "insta_clone_post");
            SelectImage();
        }
    }
    private void refreshGallary(File file)
    { Intent i=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        i.setData(Uri.fromFile(file)); getContext().sendBroadcast(i);
    }
    private static Bitmap viewToBitmap(View view, int widh, int hight)
    {Log.i("tag","HellloHere");
        Bitmap bitmap=Bitmap.createBitmap(widh,hight, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap); view.draw(canvas);
        return bitmap;
    }
    private File getdisc(){
        File file= getContext().getDir(Environment.DIRECTORY_DCIM, Context.MODE_PRIVATE);
        return new File(file,"Clone");
    }
    @Override
    public void onResume() {
        super.onResume();
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
                String cap=caption.getText().toString();
                if(cap.equals("")) cap="<3";
                myRef.child(uid).child("posts").child("caption").child(key).setValue(cap);
            Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.INVISIBLE);
        myRef=FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        key=myRef.push().getKey();
    }
}
