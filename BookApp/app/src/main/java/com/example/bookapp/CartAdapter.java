package com.example.bookapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<com.example.bookapp.CartAdapter.MyCartViewHolder> {

    Context context;
    ArrayList<CartItem> ItemList;

    static com.example.bookapp.CartAdapter.MyCartViewHolder.RecyclerViewListener listener;







    public CartAdapter(Context context, ArrayList<CartItem> ItemList, com.example.bookapp.CartAdapter.MyCartViewHolder.RecyclerViewListener listener) {
        this.context = context;
        this.ItemList = ItemList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public com.example.bookapp.CartAdapter.MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout,parent,false);
        return new com.example.bookapp.CartAdapter.MyCartViewHolder(view);




    }

    @Override
    public void onBindViewHolder(@NonNull com.example.bookapp.CartAdapter.MyCartViewHolder holder, int position) {



        CartItem item= ItemList.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText("Price: "+String.valueOf(item.getPrice()));
        holder.quantity.setText("Quantity: "+item.getQuantity());

        //get image from storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child(item.getImage());

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
        return ItemList.size();
    }

    public static class MyCartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView image;
        TextView price;
        TextView capacity;
        TextView quantity;
        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);

            title= itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            price= itemView.findViewById(R.id.price);
            capacity= itemView.findViewById(R.id.capacity);
            quantity = itemView.findViewById(R.id.quantity);

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

