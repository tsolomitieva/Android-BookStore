package com.example.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth= FirebaseAuth.getInstance();
        checkUser();
        //login user
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(view -> {
            toLogin();
        });
        //register user
        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(view -> {
            toRegister();
        });
    }

    //check if user is already logged in
    private void checkUser() {
        FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
        //if user is already logged in
        if (firebaseUser!=null){

            //check in db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //get user role(user or admin)
                            String role= snapshot.child("role").getValue().toString();
                            if (role.equals("user")) {
                                startActivity(new Intent(MainActivity.this, UserActivity.class));
                                finish();
                            }else if (role.equals("admin")) {
                                startActivity(new Intent(MainActivity.this, AdminActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }

    //Go to Login page
    public void toLogin(){
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
       startActivity(intent);

    }


    //Go to registration page
    public void toRegister(){
       Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);

    }
}