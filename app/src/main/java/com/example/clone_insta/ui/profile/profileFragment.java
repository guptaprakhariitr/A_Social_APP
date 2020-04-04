package com.example.clone_insta.ui.profile;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clone_insta.MyAdapter;
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
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class profileFragment extends Fragment {
    private FirebaseAuth mAuth;
    Boolean isloaded_cap=false,isloaded_url=false;
    private profileViewModel notificationsViewModel;
    private RecyclerView.LayoutManager layoutManager;
    Button signout;
    String url2;
    FirebaseUser user;
    RecyclerView recyclerView;
    MyAdapter recyclerViewAdapter;
    Button refresh;
    private static  ArrayList<String> rowsArrayList ;
    //ArrayList<String> likesArrayList = new ArrayList<>();
    private static ArrayList<String> url_images;
    private static  ArrayList<String> rowsArrayList_copy = new ArrayList<>();
    String urll;
    ImageView profile;
    Button upload, uploaddone;
    StorageReference storageReference;
    FirebaseStorage storage;
    ProgressBar progressBar;
    boolean isLoading = false;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private String uid;
    private DatabaseReference myRef;
    private TextView name, email, country;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        rowsArrayList = new ArrayList<>();
        url_images = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference();
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
        user = mAuth.getCurrentUser();
        recyclerView = getView().findViewById(R.id.profile_post_recycler);
        name = getView().findViewById(R.id.Name);
        country = getView().findViewById(R.id.country_profile);
        email = getView().findViewById(R.id.email_profile);
        progressBar = getView().findViewById(R.id.profile_progress);
        uid = user.getUid();
        uploaddone = getView().findViewById(R.id.upload_done);
        progressBar.setVisibility(View.VISIBLE);
        upload = getView().findViewById(R.id.upload_dp);
        profile = getView().findViewById(R.id.profile_pic);
        signout = getView().findViewById(R.id.signout);
        refresh=getView().findViewById(R.id.refresh);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        storage = FirebaseStorage.getInstance("gs://cloneinsta-5f275.appspot.com");
        storageReference = storage.getReference();
        setdetail();
        signout.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View v) {
                                           mAuth.getCurrentUser();
                                           mAuth.signOut();
                                           intent_send_log_in();
                                       }
                                   }
        );
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
                upload.setEnabled(true);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAdapter();
                Log.i("Tag_in","     imp     "+rowsArrayList.size());
            }
        });
        uploaddone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadconfig();
            }
        });
        populateData();
        Log.i("Tag_in","     imp     "+rowsArrayList_copy.size());

    }

    @Override
    public void onStart() {
        super.onStart();

            initAdapter();
            Log.i("Tag_in","     imp     "+rowsArrayList.size());
            //        initScrollListener();

    }

    private void intent_send_log_in() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void setdetail() {
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        myRef = FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // String value = dataSnapshot.getValue(String.class);
                // demoValue.setText(value);
                String name2 = "Name-\n" + dataSnapshot.child("first").getValue(String.class) + " " + dataSnapshot.child("last").getValue(String.class);
                name.setText(name2);
                String country2 = "Country-" + dataSnapshot.child("country").getValue(String.class);
                country.setText(country2);
                String email2 = "Email-\n" + dataSnapshot.child("email").getValue(String.class);
                email.setText(email2);
                urll = dataSnapshot.child("urlImage").getValue(String.class);
                if (!urll.equals("")) {
                    Glide.with(getContext())
                            .load(urll)
                            .into(profile);
                } else {
                    profile.setImageResource(R.drawable.noimage);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
                profile.setImageBitmap(bitmap);
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
                    .child(uid);

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

        myRef = FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        if (url2 != null) {
            myRef.child(uid).child("urlImage").setValue(url2);
            Glide.with(getContext())
                    .load(url2)
                    .into(profile);
            upload.setEnabled(true);
        }
    }

    public void uploadconfig() {

        myRef = FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        if (url2 != null) {
            myRef.child(uid).child("urlImage").setValue(url2);
            Glide.with(getContext())
                    .load(url2)
                    .into(profile);
            upload.setEnabled(true);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void populateData() {
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        myRef = FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user").child(uid).child("posts");
        DatabaseReference ref_cap = myRef.child("caption");
        DatabaseReference ref_posts = myRef.child("photo");
        final int[] i = {0,0};

            //rowsArrayList.add("Item " + i);
        ref_cap.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(!(snapshot.getValue().toString().equals("")))
                    { rowsArrayList.add(snapshot.getValue().toString());
                    Log.i("Tag",snapshot.getValue().toString()+"     "+rowsArrayList.size());
                    i[0]++;
                    if(i[0]==10) {break;}}
                }
                Log.i("Tag","   out  "+rowsArrayList.size());
                rowsArrayList_copy=rowsArrayList;
                assignment(rowsArrayList);
                isloaded_cap=true;
            }
        public void assignment(ArrayList<String> arrayList)
        {
            Log.i("Tag","   out_-  "+arrayList.size());
            rowsArrayList_copy=arrayList;
            Log.i("Tag","   out  "+rowsArrayList_copy.size());
        }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
            ref_posts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                        url_images.add(snapshot2.getValue().toString());
                        Log.i("Tag",snapshot2.getValue().toString());
                        i[1]++;
                        if(i[1]==10) {break;}
                    }
                isloaded_url=true;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

    }


    private void initAdapter() {
        Log.i("Tag_out"," "+rowsArrayList.size());
        recyclerViewAdapter = new MyAdapter(rowsArrayList, url_images);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }
    private void loadMore() {
        rowsArrayList.add(null);
        recyclerViewAdapter.notifyItemInserted(rowsArrayList.size() - 1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
             @Override
            public void run() {
                rowsArrayList.remove(rowsArrayList.size() - 1);
                int scrollPosition = rowsArrayList.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                final int nextLimit = currentSize + 10;

               /* while (currentSize - 1 < nextLimit) {
                    rowsArrayList.add("Item " + currentSize);
                    currentSize++;
                }*/
                 user = mAuth.getCurrentUser();
                 uid = user.getUid();
                 myRef = FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user").child(uid).child("posts");
                 DatabaseReference ref_cap = myRef.child("caption");
                 DatabaseReference ref_posts = myRef.child("photo");
                 final int[] i = {currentSize,currentSize};
                 {
                     //rowsArrayList.add("Item " + i);
                     final int finalCurrentSize1 = currentSize;
                     ref_cap.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(i[0]> finalCurrentSize1)
                                { rowsArrayList.add(snapshot.getValue().toString());}
                                 i[0]++;
                                 if(i[0]==nextLimit) {break;}
                             }
                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {
                         }
                     });
                     final int finalCurrentSize = currentSize;
                     ref_posts.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                 if(i[1]> finalCurrentSize)
                                 {   url_images.add(snapshot.getValue().toString());}
                                 i[1]++;
                                 if(i[1]==nextLimit) {break;}
                             }
                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {
                         }
                     });

                 }
                 currentSize=nextLimit;
                recyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }
}


