package com.example.chatapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chatapp.Adapter.CommentAdapter;
import com.example.chatapp.Activity.MainActivity;
import com.example.chatapp.Model.Comments;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_Comment extends Fragment {
    Toolbar toolbar_comment;
    RecyclerView re_comment;
    EditText edt_comment;
    ImageButton btn_send_comment;
    DatabaseReference myRef;
    FirebaseUser firebaseUser;
    CommentAdapter adapter;
    List<Comments>list;
    public Fragment_Comment() {
        // Required empty public constructor
    }
    public static Fragment getInstance(String post_id)
    {
        Fragment_Comment fragment_comment=new Fragment_Comment();
        Bundle bundle=new Bundle();
        bundle.putString("post_id",post_id);

        fragment_comment.setArguments(bundle);
        return fragment_comment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment__comment, container, false);
        iiit(view);
        MainActivity mainActivity= (MainActivity) getActivity();
        mainActivity.setSupportActionBar(toolbar_comment);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        re_comment.setLayoutManager(new LinearLayoutManager(getContext()));
        re_comment.setHasFixedSize(true);

        toolbar_comment.setTitle("Comment");
        toolbar_comment.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.getSupportFragmentManager().popBackStack();
            }
        });
        Bundle bundle=getArguments();
        String post_id=bundle.getString("post_id");
        String current_user=firebaseUser.getUid();
        adapter=new CommentAdapter(getList(post_id),getContext());
        re_comment.setAdapter(adapter);
        btn_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment=edt_comment.getText().toString();//lấy dữ liệu comment
                if (comment.equals(""))
                {
                }else {
                    myRef = FirebaseDatabase.getInstance().getReference("Comment").child(post_id);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("user_id", current_user);
                    hashMap.put("comment", comment);
                    myRef.push().setValue(hashMap);
                    edt_comment.setText("");
                }
            }
        });

        return view;
    }
//lấy comment cũ
    private List<Comments> getList(String post_id) {
        myRef=FirebaseDatabase.getInstance().getReference("Comment").child(post_id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Comments comments=dataSnapshot.getValue(Comments.class);
                    list.add(comments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list;
    }
    private void iiit(View view) {
        list=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        toolbar_comment=view.findViewById(R.id.toolbar_comment);
        re_comment=view.findViewById(R.id.re_comment);
        edt_comment=view.findViewById(R.id.edt_comment);
        btn_send_comment=view.findViewById(R.id.btn_send_comment);
    }
}