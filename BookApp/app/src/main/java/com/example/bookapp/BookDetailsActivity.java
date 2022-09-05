package com.example.bookapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BookDetailsActivity extends AppCompatActivity {


    TextView title;
    TextView price;
    TextView availability;
    TextView quant;
    TextView details;
    ImageView image;
    Button addToCart;
    ImageView plus;
    ImageView minus;
    String id;
    int quantity;


    ImageView speech;
    private TextToSpeech textToSpeech;


    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reff= db.getReference();
    long maxid=0; //tracks number of books in cart
    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onDestroy() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);


         title = findViewById(R.id.title);
         price = findViewById(R.id.price);
        availability = findViewById(R.id.availability);
         quant = findViewById(R.id.quantity);
         addToCart = findViewById(R.id.cart);
         plus = findViewById(R.id.plus);
         minus = findViewById(R.id.minus);
         details = findViewById(R.id.details);
         image= findViewById(R.id.image);
         Bundle extras = getIntent().getExtras();
         title.setText( extras.getString("title"));
         price.setText("Price: " +extras.getString("price"));
         availability.setText( "Availability: "+extras.getString("availability"));
         details.setText(extras.getString("details"));

        //get image from storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child(extras.getString("image"));

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });

         //save it for later
        id= extras.getString("bookId");


       // add in quantity
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity + 1;
                quant.setText(String.valueOf(quantity));

            }
        });

        // delete from quantity
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity - 1;
                quant.setText(String.valueOf(quantity));
            }
        });

         //if the book is not available
         if (extras.getString("availability").equals("0")){
             addToCart.setEnabled(false);
             addToCart.setText("Not Available");
             plus.setVisibility(View.GONE);
             minus.setVisibility(View.GONE);
             quant.setVisibility(View.GONE);

         }else {
             addToCart.setEnabled(true);
             addToCart.setText("Add to cart");
             addToCart.setVisibility(View.VISIBLE);
             plus.setVisibility(View.VISIBLE);
             minus.setVisibility(View.VISIBLE);
             quant.setVisibility(View.VISIBLE);
         }

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {     maxid=snapshot.child("Cart").child(currentuser).getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



         addToCart.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addingToCartList();
             }
         });


        speech = findViewById(R.id.speech);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result =  textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "Language not supported");
                    }else{
                        speech.setEnabled(true);
                    }
                }else {
                    Log.e("error", "Initialization failed");
                }
            }
        });

        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });



    }
    private void speak() {
        Bundle extras = getIntent().getExtras();
        textToSpeech.speak(extras.getString("details"),TextToSpeech.QUEUE_FLUSH, null);


    }

    // add book to shoping cart
    private void addingToCartList() {

        Bundle extras = getIntent().getExtras();

         // check if quantity is <= of availability
       if ( quantity > Integer.parseInt(extras.getString("availability") )){
           Toast.makeText(BookDetailsActivity.this, "Product not available in this quantity", Toast.LENGTH_SHORT).show();
       } else {

            //add product to shopping cart
           DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                   .child("Cart").child(currentuser);


           CartItem item = new CartItem();
           item.setItemId(extras.getString("id"));
           item.setTitle(extras.getString("title"));
           item.setImage(extras.getString("image"));
           item.setPrice((extras.getString("price")));
           item.setAvailability(extras.getString("availability"));
           item.setQuantity(String.valueOf(quantity));


           ref.child(id)
                   .setValue(item)
                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void unused) {
                           Toast.makeText(BookDetailsActivity.this, "Book added in your cart!", Toast.LENGTH_SHORT).show();
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(BookDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                       }
                   });
       }
    }


}
