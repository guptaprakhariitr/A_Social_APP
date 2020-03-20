package com.example.clone_insta.ui;
import java.lang.Object.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class signUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressBar prog;
    private EditText email,pass,first,last,country;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_user);
        email=findViewById(R.id.email_idnu);
        pass=findViewById(R.id.passwordnu);
        first=findViewById(R.id.firstname);
        last=findViewById(R.id.lastName);
        country=findViewById(R.id.country);
        prog=findViewById(R.id.indeterminateBar);
        prog.setVisibility(View.INVISIBLE);
    }
    public void createAccount(View view)
    {prog.setVisibility(View.VISIBLE);
        hideSoftKeyboard(this);
        prog.setProgress(0);
      final String email_s=email.getText().toString();
      final String pass_s=pass.getText().toString();
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
                            country.setText("");
                            first.setText("");
                            last.setText("");
                            email.setText("");
                            pass.setText("");
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
    private void updateUI(FirebaseUser user){
        prog.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(getBaseContext(),LoginActivity.class);
        startActivity(intent);
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
