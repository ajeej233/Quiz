package com.staraj.quiza;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.staraj.quiza.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class signupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore database;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("We're Creating an Account");

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signupActivity.this,loginActivity.class));
                finishAffinity();
            }
        });


        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.nameBox.getText().toString().isEmpty() && binding.nameBox.getText().toString().equals("text")){
                    binding.nameBox.setError("Enter Name");
                }else if(binding.emailBox.getText().toString().isEmpty()){
                    binding.emailBox.setError("Enter Email");
                }else if (binding.passwordBox.getText().toString().isEmpty()){
                    binding.passwordBox.setError("Enter 6 Digit Password");
                }else{
                    String email,pass,name, referCode;
                    email = binding.emailBox.getText().toString();
                    pass = binding.passwordBox.getText().toString();
                    name = binding.nameBox.getText().toString();
                    referCode = binding.referBox.getText().toString();

                    final User user = new User(name, email, pass, referCode);
                    dialog.show();
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                String uid = task.getResult().getUser().getUid();
                                database
                                        .collection("users")
                                        .document(uid)
                                        .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull  Task<Void> task) {
                                        if(task.isSuccessful()){
                                            dialog.dismiss();
                                            startActivity(new Intent(signupActivity.this,MainActivity.class));
                                            finish();
                                        }else{
                                            Toast.makeText(signupActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT);
                                        }

                                    }
                                });
                                Toast.makeText(signupActivity.this,"Sucess",Toast.LENGTH_SHORT).show();
                            }else{
                                dialog.dismiss();
                                Toast.makeText(signupActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            }
        });
    }

}