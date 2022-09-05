package com.example.bookapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {


    TextView error;
    FirebaseAuth firebaseAuth;
    TextView email;
    TextView password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.login_layout);

        firebaseAuth= FirebaseAuth.getInstance();

        //login user
        MaterialButton login = (MaterialButton) findViewById(R.id.login);
        login.setOnClickListener(view -> {
            login();
        });


        //register user
        TextView register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toRegister();
            }

        });


    }

    //Login method
    protected void login() {
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password);
        error = findViewById(R.id.error);


        if (email.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();

        } else if (password.getText().toString().isEmpty()) {

            Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();

        } else {
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(LoginActivity.this, "User Logged in Successfully ", Toast.LENGTH_SHORT).show();
                    //check user's role
                    FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();

                    //check in db
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(firebaseUser.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //get user role(user or admin)
                                    String role= snapshot.child("role").getValue().toString();
                                    if (role.equals("user")) {
                                        startActivity(new Intent(LoginActivity.this, UserActivity.class));

                                    }else if (role.equals("admin")) {
                                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Wrong Email or Password ", Toast.LENGTH_SHORT).show();
                        }
                    });



        }
    }

    //Go to registration page
    public void toRegister(){
      Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
      startActivity(intent);

    }
}