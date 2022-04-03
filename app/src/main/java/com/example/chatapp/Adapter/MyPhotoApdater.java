package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.Post;
import com.example.chatapp.R;

import java.util.List;

public class MyPhotoApdater extends RecyclerView.Adapter<MyPhotoApdater.ViewHolder>{
    List<Post>list;
    Context context;

    public MyPhotoApdater(List<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_photo,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Post post=list.get(position);
        Glide.with(context.getApplicationContext()).load(post.getImageURL()).into(holder.my_img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView my_img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            my_img=itemView.findViewById(R.id.my_img);

        }
    }
}
