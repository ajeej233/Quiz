package com.staraj.quiza;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.staraj.quiza.databinding.ActivityLoginBinding;
import com.staraj.quiza.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in");

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(loginActivity.this,MainActivity.class));
            finish();
        }

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.emailBox.getText().toString().isEmpty()) {
                    binding.emailBox.setError("Enter Email Id ");
                } else if (binding.passwordBox.getText().toString().isEmpty()) {
                    binding.passwordBox.setError("Enter 6 Digit Password");

                } else {
                    String pass ,email;
                    pass=binding.passwordBox.getText().toString();
                    email = binding.emailBox.getText().toString();
                    dialog.show();
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()) {
                                startActivity(new Intent(loginActivity.this,MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(loginActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });



                }
                }



        });

        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginActivity.this, signupActivity.class));
                finishAffinity();
            }
        });


    }
}