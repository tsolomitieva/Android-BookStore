package com.example.bookapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OrderActivity extends AppCompatActivity {

    //orders cost
    TextView totalCost;

    Button complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_layout);



        totalCost = findViewById(R.id.totalCost);
        complete = findViewById(R.id.complete);

        Bundle extras= getIntent().getExtras();
        totalCost.setText(extras.getString("totalCost"));



        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrderActivity.this, "Thanks for your order", Toast.LENGTH_SHORT).show();
            }
        });




    }

}
