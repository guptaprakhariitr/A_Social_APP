package com.example.clone_insta.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clone_insta.R;
import com.google.firebase.auth.FirebaseAuth;

public class signUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email,pass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_user);
    }
    public void createAccount(View view)
    {
      String email_s=email.getText().toString();
      String pass_s=pass.getText().toString();

    }
}
