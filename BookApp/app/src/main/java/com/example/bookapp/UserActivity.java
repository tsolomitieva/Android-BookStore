package com.example.bookapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class UserActivity extends AppCompatActivity {
    TextView email;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    MyAdapter.MyViewHolder.RecyclerViewListener listener;
    DatabaseReference ref;
    ArrayList<Book> bookList;
    MyAdapter myAdapter;
    Book book;
    ImageView cart;
    Button logout;
    ImageView voice;
    boolean listening=false;
    SearchView search;


    int child=1;
    SpeechRecognizer speechRecognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        setOnClickListener();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        email=findViewById(R.id.email);
        email.setText(firebaseUser.getEmail().toString());

        recyclerView=findViewById(R.id.booklist);
        ref = FirebaseDatabase.getInstance().getReference("Books");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();
        myAdapter = new MyAdapter(this,bookList, listener);
        recyclerView.setAdapter(myAdapter);

        logout= findViewById(R.id.logout);
        //Logout
        logout.setOnClickListener(view -> {
            firebaseAuth.signOut();
            startActivity(new Intent(UserActivity.this, LoginActivity.class));

        });

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(UserActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);

        }

        search = findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        voice = findViewById(R.id.voice);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listening=false){
                    speechRecognizer.startListening(speechRecognizerIntent);
                    listening= true;

                }else{
                    speechRecognizer.stopListening();
                    listening = false;
                }
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data =results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                bookList.clear();
                search.setQuery(data.get(0),false);



            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        //showing books
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    book = dataSnapshot.getValue(Book.class);
                    bookList.add(book);
                }
                myAdapter.notifyDataSetChanged();
                child++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


    private void Search(String query) {
         if (!query.equals(" ")){
            bookList.clear();
            ref = FirebaseDatabase.getInstance().getReference().child("Books");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Book book = dataSnapshot.getValue(Book.class);
                        if (book.getTitle().equals(query)) {
                            bookList.add(book);

                        }

                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            bookList.clear();
            ref = FirebaseDatabase.getInstance().getReference().child("Books");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Book book = dataSnapshot.getValue(Book.class);
                        bookList.add(book);



                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        myAdapter = new MyAdapter(this,bookList, listener);
        recyclerView.setAdapter(myAdapter);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode==1){
            if (grandResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this ,"Permission Granted", Toast.LENGTH_LONG);

            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG);
            }
        }
    }


    private void setOnClickListener() {
        listener = new MyAdapter.MyViewHolder.RecyclerViewListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("bookId",bookList.get(position).getBookId());
                intent.putExtra("title",bookList.get(position).getTitle());
                intent.putExtra("image",bookList.get(position).getImage());
                intent.putExtra("price",bookList.get(position).getPrice());
                intent.putExtra("availability",bookList.get(position).getAvailability());
                intent.putExtra("details",bookList.get(position).getDetails());


                startActivity(intent);
            }
        };
    }
}
