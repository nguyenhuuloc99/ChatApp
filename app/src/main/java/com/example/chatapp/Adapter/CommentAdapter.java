package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.Comments;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    List<Comments>list;
    Context context;

    public CommentAdapter(List<Comments> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comments comments=list.get(position);
        holder.txt_cm.setText(comments.getComment());
        getInfor(holder.txt_name_cm,holder.img_user_cm,comments.getUser_id());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView img_user_cm;
        TextView  txt_name_cm,txt_cm;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        img_user_cm=itemView.findViewById(R.id.img_user_cm);
        txt_name_cm=itemView.findViewById(R.id.txt_name_cm);
        txt_cm=itemView.findViewById(R.id.txt_cm);
    }
    }
    private void getInfor(TextView username,CircleImageView img,String user_id)
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("User").child(user_id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Glide.with(context.getApplicationContext()).load(user.getImageURL()).into(img);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
