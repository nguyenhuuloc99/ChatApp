package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.example.chatapp.Notification.ApiService;
import com.example.chatapp.Notification.Client;
import com.example.chatapp.Notification.Data;
import com.example.chatapp.Notification.Response;
import com.example.chatapp.Notification.Sender;
import com.example.chatapp.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar_chat;
    CircleImageView circle_chat;
    TextView txt_chat;
    RecyclerView re_chat;
    EditText edt_message;
    ImageButton btn_send_message;
    DatabaseReference myRef;
    FirebaseUser firebaseUser;
    ChatAdapter chatAdapter;
    List<Chat> chatList;
    TextView txt_status;
    ValueEventListener check_seen;
    ApiService apiService;


    boolean isnotify=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        iiit();
        re_chat.setHasFixedSize(true);
        re_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Bundle bundle=getIntent().getExtras();
        String userid=bundle.getString("userid");
        check_seen(userid);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        setSupportActionBar(toolbar_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_chat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        apiService= Client.getRetrofit("https:/fcm.googleapis.com/").create(ApiService.class);

        myRef= FirebaseDatabase.getInstance().getReference("User").child(userid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                txt_chat.setText(user.getUsername());
                if (user.getImageURL().equals("default"))
                {
                    circle_chat.setImageResource(R.drawable.image);
                }else
                {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).error(R.drawable.image).into(circle_chat);
                }
                if (user.getStatus().equals("online"))
                {
                    txt_status.setText("online");
                }else
                {
                    txt_status.setText("offline");
                }
                readMessage(firebaseUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isnotify=true;
                String message=edt_message.getText().toString();
                String receiver=userid;
                String sender=firebaseUser.getUid();
                if (!message.equals(""))
                {
                    sendMessage(message,receiver,sender);

                }else {
                    Toast.makeText(getApplicationContext(),"Vui lòng không để trống",Toast.LENGTH_SHORT).show();
                }
                edt_message.setText("");

            }
        });

    }
    private void sendMessage(String message, String receiver, String sender) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("message",message);
        hashMap.put("receiver",receiver);
        hashMap.put("sender",sender);
        hashMap.put("isseen",false);
        databaseReference.child("Chats").push().setValue(hashMap);
        //thiếu add user to chat fragment
        String msg=message;

       DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                sendNotification(receiver,user.getUsername(),msg);
                isnotify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, String username, String msg) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Token");
        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Token token=dataSnapshot.getValue(Token.class);
                    Data data=new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,username + ": " + msg,"New message",receiver);
                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if (response.code()==200)
                            {
                                if (response.body().success!=1)
                                {
                                    Toast.makeText(ChatActivity.this,"failed", Toast.LENGTH_SHORT).show();
                                }else
                                {
                                    Toast.makeText(ChatActivity.this,"thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void iiit() {
        txt_status=findViewById(R.id.txt_status);
        toolbar_chat=findViewById(R.id.toolbar_chat);
        circle_chat=findViewById(R.id.circle_chat);
        txt_chat=findViewById(R.id.txt_chat);
        re_chat=findViewById(R.id.re_chat);
        edt_message=findViewById(R.id.edt_message);
        btn_send_message=findViewById(R.id.btn_send_message);
    }
    private void readMessage(String myid,String uersid,String url)
    {
        chatList=new ArrayList<>();
        DatabaseReference  myRef=FirebaseDatabase.getInstance().getReference().child("Chats");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot :snapshot.getChildren())
                {
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(uersid) ||
                            chat.getReceiver().equals(uersid) && chat.getSender().equals(myid))
                    {
                        chatList.add(chat);
                    }
                    chatAdapter =new ChatAdapter(chatList,getApplicationContext(),url);
                    chatAdapter.notifyDataSetChanged();
                    re_chat.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void check_seen(String user_id)
    {
        myRef= FirebaseDatabase.getInstance().getReference("Chats");
        check_seen=myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Chat chat=snapshot1.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(user_id))
                    {
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot1.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        myRef.removeEventListener(check_seen);
    }
}