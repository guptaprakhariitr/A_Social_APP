package com.example.clone_insta.ui;
import java.lang.Object.*;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clone_insta.R;
import com.example.clone_insta.user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressBar prog;
    String uid;
    Button signup;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private EditText email,pass,first,last,country;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        myRef=FirebaseDatabase.getInstance("https://cloneinsta-5f275.firebaseio.com/").getReference();
      myRef.child("hello").setValue("emial");
        setContentView(R.layout.fragment_new_user);
        email=findViewById(R.id.email_idnu);
        pass=findViewById(R.id.passwordnu);
        first=findViewById(R.id.firstname);
        last=findViewById(R.id.lastName);
        country=findViewById(R.id.country);
        prog=findViewById(R.id.indeterminateBar);
        prog.setVisibility(View.INVISIBLE);
        signup=findViewById(R.id.signUpnu);
        email.addTextChangedListener(new TextWatcher() {

                                         @Override
                                         public void onTextChanged(CharSequence s, int start, int before, int count) {

                                             if (s.toString().trim().length() == 0) {
                                                 signup.setEnabled(false);
                                             } else {
                                                 signup.setEnabled(true);
                                             }
                                         }

                                         @Override
                                         public void beforeTextChanged(CharSequence s, int start, int count,
                                                                       int after) {
                                             // TODO Auto-generated method stub

                                         }

                                         @Override
                                         public void afterTextChanged(Editable s) {
                                             // TODO Auto-generated method stub

                                         }

                                     }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        signup.setEnabled(false);
    }
    public void createAccount(View view)
    {prog.setVisibility(View.VISIBLE);
        hideSoftKeyboard(this);
        prog.setProgress(0);
        final String email_s=email.getText().toString();
      final String pass_s=pass.getText().toString();
      final String first_s=first.getText().toString();
        final String last_s=last.getText().toString();
        final String country_s=country.getText().toString();
        final DatabaseReference my2=myRef.child("user");

        if(!(email_s.equals("") || pass_s.equals("") || first_s.equals("") || country_s.equals("")))
        {
            mAuth.createUserWithEmailAndPassword(email_s, pass_s)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(signUp.this,"SUCCESS",Toast.LENGTH_SHORT).show();
                            prog.setVisibility(View.INVISIBLE);
                          uid=user.getUid();
                            my2.child(uid).child("email").setValue(email_s);
                            my2.child(uid).child("first").setValue(first_s);
                            my2.child(uid).child("last").setValue(last_s);
                            my2.child(uid).child("country").setValue(country_s);
                            country.setText("");
                            first.setText("");
                            last.setText("");
                            email.setText("");
                            pass.setText("");
                            sendVerificationEmail();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            prog.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
    else{
        Toast.makeText(this,"FILL IN ALL THE FILEDS ",Toast.LENGTH_LONG).show();
            prog.setVisibility(View.INVISIBLE);
        }
    }
    private void sendVerificationEmail()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // email sent
                                // after email is sent just logout the user and finish this activity
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(signUp.this, LoginActivity.class));
                                finish();
                            } else {
                                // email not sent, so display message and restart the activity or do whatever you wish to do
                                Toast.makeText(signUp.this, "Failed to send Email verification", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(signUp.this, "User Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                startActivity(new Intent(signUp.this, LoginActivity.class));
                                finish();
                            }
                        }
                    });
        }
        else
        {                                        Toast.makeText(signUp.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(signUp.this, LoginActivity.class));
            finish();
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null){
            inputMethodManager.hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }}
}
