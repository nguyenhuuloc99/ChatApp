package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Callback.Onclick;
import com.example.chatapp.Model.Post;
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

public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.ViewHolder>{
    Context context;
    List<Post>list;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Onclick onclick;
    public PostAdapter(Context context, List<Post> list,Onclick onclick) {
        this.context = context;
        this.list = list;
        this.onclick=onclick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Post post=list.get(position);
            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(context).load(post.getImageURL()).into(holder.img_post2);
      Info(holder.txt_username_post,holder.img_profile_post,holder.publisher_post,post.getId_user());
        if (post.getDescription().equals(""))
        {
            holder.des_post.setVisibility(View.GONE);
        }else {
            holder.des_post.setVisibility(View.VISIBLE);
            holder.des_post.setText(post.getDescription());
        }
        //like
        holder.heart_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (holder.heart_post.getTag().equals("like"))
              {
                    reference=FirebaseDatabase.getInstance().getReference("Like").child(post.getId_post())
                    .child(firebaseUser.getUid());//id người like
                    reference.setValue(true);
              }else
              {
                  reference=FirebaseDatabase.getInstance().getReference("Like").child(post.getId_post())
                          .child(firebaseUser.getUid());
                  reference.removeValue();
              }

            }
        });
        holder.img_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick.Onlick(post);
            }
        });
       // like anh
        isLike(holder.heart_post,post.getId_post());
        //đếm số like
        count_like(holder.likes_post,post.getId_post());
        showComment(holder.comment_post,post.getId_post());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView img_profile_post;
        ImageView img_post2,heart_post,img_comment;
        TextView txt_username_post,likes_post,publisher_post,des_post,comment_post;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_profile_post=itemView.findViewById(R.id.img_profile_post);
            img_post2=itemView.findViewById(R.id.img_post2);
            heart_post=itemView.findViewById(R.id.heart_post);
            img_comment=itemView.findViewById(R.id.img_comment);
            txt_username_post=itemView.findViewById(R.id.txt_username_post);
            likes_post=itemView.findViewById(R.id.likes_post);
            publisher_post=itemView.findViewById(R.id.publisher_post);
            des_post=itemView.findViewById(R.id.des_post);
            comment_post=itemView.findViewById(R.id.comment_post);
        }
    }
    private void Info(TextView txt_username_post,CircleImageView img_profile_post,TextView publisher_post,String user_id)
    {
       reference = FirebaseDatabase.getInstance().getReference("User").child(user_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (user.getImageURL().equals("default"))
                {
                    img_profile_post.setImageResource(R.drawable.image);
                }else {
                    Glide.with(context.getApplicationContext()).load(user.getImageURL()).into(img_profile_post);
                }
                txt_username_post.setText(user.getUsername());
                publisher_post.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void count_like(TextView likes_post,String post_id)
    {
        reference=FirebaseDatabase.getInstance().getReference("Like").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes_post.setText(snapshot.getChildrenCount()+" Likes");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void isLike(ImageView img,String post_id)
    {
        reference=FirebaseDatabase.getInstance().getReference("Like").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists())
                {
                    img.setImageResource(R.drawable.heart_2);
                    img.setTag("liked");
                }else
                {
                    img.setImageResource(R.drawable.heart);
                    img.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showComment(TextView txtcm,String post_id)
    {
        reference=FirebaseDatabase.getInstance().getReference("Comment").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               txtcm.setText("View all "+snapshot.getChildrenCount()+" comment");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
