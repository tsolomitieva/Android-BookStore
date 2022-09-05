package com.example.bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button placeOder;
    private TextView total;
    private DatabaseReference reff;
    CartAdapter.MyCartViewHolder.RecyclerViewListener listener;
    CartAdapter cartAdapter;
    ArrayList<CartItem> ItemList;
    CartItem item;
    float totalCost;
    Button order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_layout);

        recyclerView = findViewById(R.id.cart);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        placeOder = findViewById(R.id.placeorder);
        total= findViewById(R.id.total);


        ItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(this,ItemList, listener);
        recyclerView.setAdapter(cartAdapter);
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();



        reff = FirebaseDatabase.getInstance().getReference("Cart");

        //showing books in users cart
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.child(currentuser).getChildren()) {
                    item = dataSnapshot.getValue(CartItem.class);
                    ItemList.add(item);
                    // add (product price * product quantity) to total cost
                    totalCost= totalCost + (Float.parseFloat(item.getPrice())* Integer.parseInt(item.getQuantity()));


                }
                cartAdapter.notifyDataSetChanged();
                total.setText("Total: "+totalCost );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //complete order
        order = findViewById(R.id.placeorder);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                intent.putExtra("totalCost",totalCost);
                startActivity(intent);
            }
        });

    }


}