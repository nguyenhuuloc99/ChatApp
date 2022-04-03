package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Callback.Iclick;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    List<User>list;
    Context context;
    Iclick iclick;

    public SearchAdapter(List<User> list, Context context, Iclick iclick) {
        this.list = list;
        this.context = context;
        this.iclick=iclick;
    }

    public SearchAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_phonebook,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user=list.get(position);
            if (user.getImageURL().equals("default"))
            {
                Glide.with(context.getApplicationContext()).load(R.drawable.image).into(holder.img_phonebook);
            }else
            {
                Glide.with(context.getApplicationContext()).load(user.getImageURL()).into(holder.img_phonebook);
            }

        holder.txt_name_phonebook.setText(user.getUsername());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iclick.Onclick(user);
                }
            });
            holder.btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.btn_follow.getText().equals("Follow"))
                    {
                        FirebaseDatabase.getInstance().getReference("Follow")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("following").child(user.getId()).setValue(true);
                        FirebaseDatabase.getInstance().getReference("Follow")
                                .child(user.getId())
                                .child("followers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        holder.btn_follow.setText("Unfollow");
                    }else
                    {
                        FirebaseDatabase.getInstance().getReference("Follow")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("following").child(user.getId()).removeValue();
                        FirebaseDatabase.getInstance().getReference("Follow")
                                .child(user.getId())
                                .child("followers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                        holder.btn_follow.setText("Follow");
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        AppCompatButton btn_follow;
        TextView txt_name_phonebook;
        CircleImageView img_phonebook,img_on,img_off;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name_phonebook=itemView.findViewById(R.id.txt_name_phonebook);
            img_phonebook=itemView.findViewById(R.id.img_phonebook);
            btn_follow=itemView.findViewById(R.id.btn_follow);

        }
    }
    private void checkFollow(AppCompatButton button)
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Follow");
    }
}
