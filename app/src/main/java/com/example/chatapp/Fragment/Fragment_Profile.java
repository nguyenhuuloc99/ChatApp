package com.example.chatapp.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.MyPhotoApdater;
import com.example.chatapp.Activity.MainActivity;
import com.example.chatapp.Model.Like;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_Profile extends Fragment {
    CircleImageView img_profile;
    Toolbar toolbar_profile;
    TextView txt_post_profile,txt_follow_profile,txt_like_profile,txt_name_profile,txt_setting;
    RecyclerView re_profile;
    MainActivity mainActivity;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    MyPhotoApdater apdater;
    List<Post>postList;
    Context context;
    List<String>listpost_id;
    int cout=0;
    public Fragment_Profile() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment__profile, container, false);
        iniit(view);
        mainActivity= (MainActivity) getActivity();
        mainActivity.show();
        context=mainActivity.getApplicationContext();
        mainActivity.setSupportActionBar(toolbar_profile);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        re_profile.setLayoutManager(new GridLayoutManager(getContext(),2));
        re_profile.setHasFixedSize(true);
        String useId=firebaseUser.getUid();
        getImage_Name(useId);
        getPost(useId);
        getFollowing(useId);
        txt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_setting();
            }
        });
        apdater=new MyPhotoApdater(getPhoto(useId),getContext());
        re_profile.setAdapter(apdater);
        //txt_like_profile.setText(count_like()+"");

        return view;
    }

    private List<Post> getPhoto(String useId) {
        postList=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post=dataSnapshot.getValue(Post.class);
                    if (post.getId_user().equals(useId))
                    {
                        postList.add(post);
                    }

                }
                apdater.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return postList;
    }

    private void open_setting() {
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,new Fragment_Setting());
        fragmentTransaction.addToBackStack(null);
        mainActivity.hide();
        fragmentTransaction.commit();
    }

    private void getFollowing(String useId) {
        databaseReference=FirebaseDatabase.getInstance().getReference("Follow").child(useId)
        .child("following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt_follow_profile.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPost(String useId) {
        databaseReference=FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post=dataSnapshot.getValue(Post.class);
                    if (post.getId_user().equals(useId))
                    {
                        i++;
                        listpost_id.add(post.getId_post());
                      //  count_like(txt_like_profile);
                    }
                }
                txt_post_profile.setText(i+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private  int count_like(TextView txt)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Like");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              for (DataSnapshot snapshot1:snapshot.getChildren())
              {
                  Like like=snapshot1.getValue(Like.class);
                  for (String id : listpost_id)
                  {
                      if (like.getId_post().equals(id))
                      {
                          cout= (int) (cout+snapshot1.getChildrenCount());
                          cout ++;

                      }
                  }
              }
              txt.setText(cout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return cout;
    }

    private void getImage_Name(String useId) {
        databaseReference= FirebaseDatabase.getInstance().getReference("User").child(useId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (user.getImageURL().equals("default"))
                {
                    //Glide.with(context).load().into(img_profile.)
                    img_profile.setImageResource(R.drawable.image);
                }else
                {
                    Glide.with(context.getApplicationContext()).load(user.getImageURL()).into(img_profile);
                }

                txt_name_profile.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void iniit(View view) {
        listpost_id=new ArrayList<>();
        toolbar_profile=view.findViewById(R.id.toolbar_profile);
        img_profile=view.findViewById(R.id.img_profile);
        txt_post_profile=view.findViewById(R.id.txt_post_profile);
        txt_follow_profile=view.findViewById(R.id.txt_follow_profile);
        txt_like_profile=view.findViewById(R.id.txt_like_profile);
        txt_name_profile=view.findViewById(R.id.txt_name_profile);
        img_profile=view.findViewById(R.id.img_profile);
        re_profile=view.findViewById(R.id.re_profile);
        txt_setting=view.findViewById(R.id.txt_setting);
    }
}