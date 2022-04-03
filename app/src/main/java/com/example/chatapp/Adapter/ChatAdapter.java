package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final int MSG_LEFT=0;
    public static final int MSG_RIGT=1;
    String imageURL;
    List<Chat>list;
    Context context;
    FirebaseUser firebaseUser;
    public ChatAdapter(List<Chat> list, Context context,String imageURL) {
        this.list = list;
        this.context = context;
        this.imageURL=imageURL;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView txt_seen;
        CircleImageView image_profile;
        TextView txt_message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile);
            txt_message=itemView.findViewById(R.id.txt_message);
            txt_seen=itemView.findViewById(R.id.txt_seen);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_LEFT)
        {
            View view=LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new ChatAdapter.ViewHolder(view);
        }else
        {
            View view=LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Chat chat=list.get(position);
            holder.txt_message.setText(chat.getMessage());
            if (imageURL.equals("default"))
            {
                holder.image_profile.setImageResource(R.drawable.image);
            }else
            {
                Glide.with(context).load(imageURL).into(holder.image_profile);
            }
            //kiểm tra tin nhắn cuối cungf
           if (position==list.size()-1)
           {
               if (chat.isIsseen()==false)
               {
                   holder.txt_seen.setText("Đã gửi");
               }else
               {
                   holder.txt_seen.setText("Đã xem");
               }
           }else
           {
               holder.txt_seen.setVisibility(View.GONE);
           }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return MSG_RIGT;
        }else
        {
            return MSG_LEFT;
        }

    }
}
