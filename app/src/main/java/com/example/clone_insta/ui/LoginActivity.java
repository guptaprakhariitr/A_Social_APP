package com.example.clone_insta.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.clone_insta.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
    }
public void signUpfunc(View view)
{
    Intent intent = new Intent(LoginActivity.this,
            signUp.class);
    startActivity(intent);
}
}
