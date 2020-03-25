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

public class profileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private profileViewModel notificationsViewModel;
Button signout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
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
        signout=getView().findViewById(R.id.signout);
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
}
