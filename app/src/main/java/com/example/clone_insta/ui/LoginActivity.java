package com.example.clone_insta.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.clone_insta.MainActivity;
import com.example.clone_insta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email,pass;
    private Button login;
    signUp s=new signUp();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        email = findViewById(R.id.email_id);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.login);
            login.setEnabled(true);
            login.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    signUp.hideSoftKeyboard(LoginActivity.this);
                    signInFunc();
                }
            });
        email.addTextChangedListener(new TextWatcher() {

                                         @Override
                                         public void onTextChanged(CharSequence s, int start, int before, int count) {

                                             if (s.toString().trim().length() == 0) {
                                                 login.setEnabled(false);
                                             } else {
                                                 login.setEnabled(true);
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
        login.setEnabled(false);
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
           toMainIntent();
        }
    }

    public void  toMainIntent()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
startActivity(intent);
finish();
    }
public void signUpFunc(View view)
{
    Intent intent = new Intent(LoginActivity.this,
            signUp.class);
    startActivity(intent);
}
    public void signInFunc() {
        String email_s = email.getText().toString();
        String pass_s;

        if(pass.getText() != null){
         pass_s = pass.getText().toString();}
        else
        {pass_s="";}

        if (!(email_s.equals("") || pass_s.equals("")) ){
            mAuth.signInWithEmailAndPassword(email_s, pass_s)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user!=null && user.isEmailVerified()){
                                    pass.setText("");
                                    email.setText("");
                                    toMainIntent();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Email May Not Be Verified",

                                            Toast.LENGTH_SHORT).show();
                                    mAuth.getCurrentUser();
                                    mAuth.signOut();
                                    pass.setText("");
                                    email.setText("");

                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
        }
        else {
             Toast.makeText(LoginActivity.this, "FIELD EMPTY",
                    Toast.LENGTH_SHORT).show();}
    }
}
