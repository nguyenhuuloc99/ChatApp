package com.example.chatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Callback.Iclick;
import com.example.chatapp.Activity.ChatActivity;
import com.example.chatapp.Activity.MainActivity;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.example.chatapp.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;


public class Fragment_Message extends Fragment {
    Toolbar toolbar;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    RecyclerView re_message;
    private List<String>idList;
    List<User>mUsers;
    UserAdapter userAdapter;
    MainActivity activity;
    public Fragment_Message() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG","onCreate");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment__message, container, false);
        toolbar=view.findViewById(R.id.toobar);
        re_message=view.findViewById(R.id.re_message);
        re_message.setLayoutManager(new LinearLayoutManager(getContext()));
        re_message.setHasFixedSize(true);

         activity= (MainActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        activity.show();

        //lấy ra id user có tin nhắn
        idList=new ArrayList<>();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()))
                    {
                        idList.add(chat.getSender());
                    }
                     if (chat.getSender().equals(firebaseUser.getUid()))
                    {
                        idList.add(chat.getReceiver());
                    }

                }
                getUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
            updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;

    }

    private void getUser() {
        mUsers=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        for (String id : idList) {
                            if (user.getId().equals(id)) {
                                if (!mUsers.contains(user)) {
                                    mUsers.add(user);
                                }
                            }
                        }
                    }
                }
                userAdapter=new UserAdapter(mUsers, getContext(), new Iclick() {
                    @Override
                    public void Onclick(User user) {
                        Intent intent=new Intent(getContext(), ChatActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("userid",user.getId());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                re_message.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateToken(String token)
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Token");
        Token token1=new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);
    }

}