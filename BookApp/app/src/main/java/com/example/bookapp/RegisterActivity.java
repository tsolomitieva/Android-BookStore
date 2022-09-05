package com.example.bookapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextView error;
    Button singUp;
    TextView name;
    TextView email;
    TextView password;
    TextView password2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        singUp = findViewById(R.id.singUp);
        error = findViewById(R.id.error);
        name = findViewById(R.id.name);

        //register
        singUp.setOnClickListener((view->{
            registerUser();
        }));

    }

    //registration
    private void registerUser() {
         email = (TextView) findViewById(R.id.email);
         password = (TextView) findViewById(R.id.password1);
         password2 = (TextView) findViewById(R.id.password2);

        if (email.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();

        } else if (password.getText().toString().isEmpty()) {

            Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();

        } else if (!(password.getText().toString().equals(password2.getText().toString()))) {
            Toast.makeText(RegisterActivity.this, "Password and confirm password don't match", Toast.LENGTH_SHORT).show();

        } else {
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    SaveUserDetails();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });


        }


    }


    private void SaveUserDetails() {

        //get current user id
         String uid= firebaseAuth.getUid();

         //get user details
         UserDetails user= new UserDetails();
         user.setName(name.getText().toString());
         user.setEmail(email.getText().toString());
         user.setRole("user");




         DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
         ref.child(uid)
                 .setValue(user)
                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void unused) {
                         Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                     }

            })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(RegisterActivity.this, "Please try again ", Toast.LENGTH_SHORT).show();
                     }
                 });

    }

}