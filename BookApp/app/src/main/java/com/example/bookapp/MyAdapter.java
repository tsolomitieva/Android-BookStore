package com.example.bookapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Book> BookList;

    static MyViewHolder.RecyclerViewListener listener;
    static ArrayList<Book>  selecteditems;






    public MyAdapter(Context context, ArrayList<Book> bookList, MyViewHolder.RecyclerViewListener listener) {
        this.context = context;
        BookList = bookList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_layout,parent,false);
        return new MyViewHolder(view);




    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {



        Book book= BookList.get(position);
        holder.title.setText(book.getTitle());


        holder.price.setText("Price: "+String.valueOf(book.getPrice()));
        holder.capacity.setText("Αvailability: "+book.getAvailability());

        //get image from storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child(book.getImage());

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.image.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context.getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return BookList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView image;
        TextView price;
        TextView capacity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title= itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            price= itemView.findViewById(R.id.price);
            capacity= itemView.findViewById(R.id.capacity);


            itemView.setOnClickListener(this);



        }
        interface RecyclerViewListener{
            void onClick(View v, int position);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v , getAdapterPosition());
        }
    }
}
