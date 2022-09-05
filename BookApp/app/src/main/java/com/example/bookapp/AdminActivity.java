package com.example.bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {

    Button add;
    TextView title;
    TextView image;
    TextView price;
    TextView availability;
    TextView bookId;
    TextView details;
    Button logout;
    FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.admin_layout);



        logout= findViewById(R.id.logout);
        //Logout
        logout.setOnClickListener(view -> {
            firebaseAuth.signOut();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));

        });


        title= findViewById(R.id.bookTitle);
        image= findViewById(R.id.bookImage);
        price= findViewById(R.id.bookPrice);
        availability= findViewById(R.id.bookAvailability);
        bookId= findViewById(R.id.bookId);
        add = findViewById(R.id.add);
        details = findViewById(R.id.bookDetails);



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });
    }

    //Adding book to Firebase
    private void addBook() {
        Book book= new Book();
        book.setBookId(bookId.getText().toString());
        book.setTitle(title.getText().toString());
        book.setImage(image.getText().toString());
        book.setPrice(price.getText().toString());
        book.setAvailability(availability.getText().toString());
        book.setDetails(details.getText().toString());



        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId.getText().toString())
                .setValue(book)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AdminActivity.this, "Book added successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });


    }


}
