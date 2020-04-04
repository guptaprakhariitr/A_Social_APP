package com.example.clone_insta.ui.feed;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clone_insta.MyAdapter;
import com.example.clone_insta.R;
import com.example.clone_insta.ui.profile.profileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class feedFragment extends Fragment {

    private feedViewModel dashboardViewModel;
    private FirebaseAuth mAuth;
    Boolean isloaded_cap=false,isloaded_url=false;
    private profileViewModel notificationsViewModel;
    private RecyclerView.LayoutManager layoutManager;
    String url2;
    private static ArrayList<String> names;
    FirebaseUser user;
    RecyclerView recyclerView;
    MyAdapter recyclerViewAdapter;
    Button refresh;
    private static ArrayList<String> rowsArrayList ;
    //ArrayList<String> likesArrayList = new ArrayList<>();
    private static ArrayList<String> url_images;
    String urll;
    StorageReference storageReference;
    FirebaseStorage storage;
    boolean isLoading = false;
    private Uri filePath;
    private String uid;
    private DatabaseReference myRef;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed, container, false);
        mAuth = FirebaseAuth.getInstance();
        rowsArrayList = new ArrayList<>();
        url_images = new ArrayList<>();
        names=new ArrayList<>();
        myRef = FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference();
        dashboardViewModel =
                new ViewModelProvider(this).get(feedViewModel.class);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        recyclerView = getView().findViewById(R.id.profile_post_recycler_feed);
        uid = user.getUid();
        refresh=getView().findViewById(R.id.refresh_feed);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        storage = FirebaseStorage.getInstance("gs://cloneinsta-5f275.appspot.com");
        storageReference = storage.getReference();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setBackgroundResource(android.R.color.transparent);
                initAdapter();
                Log.i("Tag_in","     imp     "+rowsArrayList.size());
            }
        });
        populateData();
    }
    @Override
    public void onStart() {
        super.onStart();

        initAdapter();
        Log.i("Tag_in","     imp     "+rowsArrayList.size());
        //        initScrollListener();

    }
    private void populateData() {
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        myRef = FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                /*    url_images.add(snapshot2.getValue().toString());
                    Log.i("Tag",snapshot2.getValue().toString());
                    i[1]++;
                    if(i[1]==10) {break;}

                isloaded_url=true;*/
               DatabaseReference ref_cap_in= snapshot2.child("posts").child("caption").getRef();
                    DatabaseReference ref_url_in=snapshot2.child("posts").child("photo").getRef();
                    final DatabaseReference ref_name=snapshot2.child("first").getRef();

                    final int[] i = {0,0};

                    //rowsArrayList.add("Item " + i);
                    ref_cap_in.addListenerForSingleValueEvent(new ValueEventListener() {
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

                            isloaded_cap=true;
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    ref_url_in.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot3 : dataSnapshot.getChildren()) {
                                url_images.add(snapshot3.getValue().toString());
                                ref_name.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        names.add(dataSnapshot.getValue(String.class));
                                        Log.i("Tag",dataSnapshot.getValue(String.class));

                                        isloaded_url=true;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                                Log.i("Tag",snapshot3.getValue().toString());
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


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        /*DatabaseReference ref_cap = myRef.child("caption");
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
                assignment(rowsArrayList);
                isloaded_cap=true;
            }
            public void assignment(ArrayList<String> arrayList)
            {
                Log.i("Tag","   out_-  "+arrayList.size());

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
*/
    }


    private void initAdapter() {
        Log.i("Tag_out"," "+rowsArrayList.size());
        recyclerViewAdapter = new MyAdapter(rowsArrayList,url_images,names);
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
