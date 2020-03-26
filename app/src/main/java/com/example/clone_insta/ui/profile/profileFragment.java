package com.example.clone_insta.ui.profile;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.clone_insta.MainActivity;
import com.example.clone_insta.R;
import com.example.clone_insta.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private profileViewModel notificationsViewModel;
    Button signout;
    FirebaseUser user;
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
        signout=getView().findViewById(R.id.signout);
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

    }
    private   void intent_send_log_in(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void setdetail(){
        uid=user.getUid();
        myRef=FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference("user");
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // String value = dataSnapshot.getValue(String.class);
               // demoValue.setText(value);
                String name2=dataSnapshot.child("first").getValue(String.class)+" "+dataSnapshot.child("last").getValue(String.class);
                name.setText(name2);
                String country2=dataSnapshot.child("country").getValue(String.class);
                country.setText(country2);
                String email2=dataSnapshot.child("email").getValue(String.class);
                email.setText(email2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
