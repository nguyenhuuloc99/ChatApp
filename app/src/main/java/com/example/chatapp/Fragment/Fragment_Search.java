package com.example.chatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.chatapp.Activity.ChatActivity;
import com.example.chatapp.Adapter.SearchAdapter;
import com.example.chatapp.Callback.Iclick;
import com.example.chatapp.Activity.MainActivity;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Search extends Fragment {
    RecyclerView re_phonebook;
    SearchAdapter adapter;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    List<User>list;
    Toolbar toobar_phonebook;
    EditText edt_search;
    MainActivity mainActivity;
    public Fragment_Search() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment__search, container, false);
      mainActivity = (MainActivity) getActivity();
       mainActivity.show();
       re_phonebook=view.findViewById(R.id.re_phonebook);
        toobar_phonebook=view.findViewById(R.id.toobar_phonebook);
        edt_search=view.findViewById(R.id.edt_search);
        mainActivity.setSupportActionBar(toobar_phonebook);
       list=new ArrayList<>();
       re_phonebook.setLayoutManager(new LinearLayoutManager(getContext()));
       re_phonebook.setHasFixedSize(true);
       firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        adapter=new SearchAdapter(readUser(), getContext(), new Iclick() {
            @Override
            public void Onclick(User user) {
                Intent intent=new Intent(getContext(), ChatActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("userid",user.getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        re_phonebook.setAdapter(adapter);


        return view;
    }
    private List<User> readUser()
    {
        databaseReference=FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (edt_search.getText().toString().equals(""))
                {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        User user=dataSnapshot.getValue(User.class);

                        if (!user.getId().equals(firebaseUser.getUid()))
                        {
                            list.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list;
    }
    private void search(String search)
    {
        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        Query query=FirebaseDatabase.getInstance().getReference("User").orderByChild("search")
                .startAt(search).endAt(search+"\uf8ff");
      query.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              list.clear();
              for (DataSnapshot dataSnapshot:snapshot.getChildren())
              {
                  User user=dataSnapshot.getValue(User.class);
                  assert  user!=null;
                  assert currentUser!=null;
                  if(!user.getId().equals(currentUser.getUid()))
                    {
                        list.add(user);
                    }
              }
              adapter=new SearchAdapter(list, getContext(), new Iclick() {
                  @Override
                  public void Onclick(User user) {
                      Intent intent=new Intent(getContext(), ChatActivity.class);
                      Bundle bundle=new Bundle();
                      bundle.putString("userid",user.getId());
                      intent.putExtras(bundle);
                      startActivity(intent);
                  }
              });
              re_phonebook.setAdapter(adapter);
              adapter.notifyDataSetChanged();

          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }

    @Override
    public void onResume() {
        super.onResume();
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}