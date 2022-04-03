package com.example.chatapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Callback.Iclick;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    List<User>list;
    Context context;
    String lastmesage;
    Iclick iclick;
    public UserAdapter(List<User> list, Context context,Iclick iclick) {
        this.list = list;
        this.context = context;
        this.iclick=iclick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=list.get(position);
        holder.txt_name.setText(user.getUsername());
        if (user.getImageURL().equals("default"))
        {
            Glide.with(context.getApplicationContext()).load(R.drawable.image).into(holder.user_avt);
        }else
        {
            Glide.with(context.getApplicationContext()).load(user.getImageURL()).into(holder.user_avt);
        }
        if (user.getStatus().equals("online"))
        {
            holder.user_status_on.setVisibility(View.VISIBLE);
            holder.user_status_off.setVisibility(View.INVISIBLE);
        }else {
            holder.user_status_on.setVisibility(View.INVISIBLE);
            holder.user_status_off.setVisibility(View.VISIBLE);
        }
         //Hiển thị tin nhắn cuối cùng
            lastmesage(user.getId(),holder.last_message);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iclick.Onclick(user);
            }
        });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView user_avt;
        CircleImageView user_status_on,user_status_off;

        public  TextView txt_name,last_message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_avt=itemView.findViewById(R.id.user_avt);
            txt_name=itemView.findViewById(R.id.user_name);
            last_message=itemView.findViewById(R.id.last_message);
            user_status_on=itemView.findViewById(R.id.user_status_on);
            user_status_off=itemView.findViewById(R.id.user_status_off);
        }
    }
    private void lastmesage(final String userid,final TextView last_message) {
        lastmesage="default";
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))
                    {
                        if (chat.getSender().equals(firebaseUser.getUid()))
                        {
                            lastmesage="You :"+chat.getMessage();
                        }else
                        {
                            lastmesage=chat.getMessage();
                        }
                        if (chat.isIsseen()==false)
                        {
                            last_message.setTextColor(Color.RED);
                            last_message.setTypeface(null, Typeface.BOLD);
                        }else
                        {
                            last_message.setTextColor(Color.GRAY);
                            last_message.setTypeface(null, Typeface.NORMAL);
                        }
                    }
                }
                switch (lastmesage)
                {
                    case "default":
                        last_message.setText("No message");
                        break;
                    default:
                        last_message.setText(lastmesage);
                        break;
                }
                lastmesage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
