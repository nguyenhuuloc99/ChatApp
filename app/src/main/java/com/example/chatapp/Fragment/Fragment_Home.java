package com.example.chatapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.PostAdapter;
import com.example.chatapp.Callback.Onclick;
import com.example.chatapp.Activity.MainActivity;
import com.example.chatapp.Model.Post;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Fragment_Home extends Fragment implements Onclick {
    RecyclerView re_home;
    Toolbar toobar_home;
    MainActivity mainActivity;
    PostAdapter postAdapter;
    List<Post>list;
    List<String>listFollow;
    public Fragment_Home() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment__home, container, false);
        mainActivity= (MainActivity) getActivity();
        mainActivity.show();
        mainActivity.setSupportActionBar(toobar_home);
        iniit(view);
        checkFollowing();
        re_home.setLayoutManager(new LinearLayoutManager(getContext()));
        re_home.setHasFixedSize(true);
        postAdapter=new PostAdapter(getContext(),readPost(),this::Onlick);
        re_home.setAdapter(postAdapter);
    //   Log.d("A",listFollow.size()+"");
        return view;
    }
    private void checkFollowing()
    {
        listFollow=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFollow.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren())
                {
                    String key=snapshot1.getKey();

                    listFollow.add(key);
                }
                readPost();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private List<Post> readPost() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Post");//tìm kiếm post
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post=dataSnapshot.getValue(Post.class);
                    for (String id :listFollow)
                    {
                        if (post.getId_user().equals(id))
                        {
                            list.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return list;
    }
    //
    private void iniit(View view) {
        list=new ArrayList<>();
        re_home=view.findViewById(R.id.re_home);
        toobar_home=view.findViewById(R.id.toobar_home);
        mainActivity.setSupportActionBar(toobar_home);
    }
    @Override
    public void Onlick(Post post) {
        FragmentTransaction fragmentTransaction=mainActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,Fragment_Comment.getInstance(post.getId_post()));
        fragmentTransaction.addToBackStack(null);
        mainActivity.hide();
        fragmentTransaction.commit();
    }
}