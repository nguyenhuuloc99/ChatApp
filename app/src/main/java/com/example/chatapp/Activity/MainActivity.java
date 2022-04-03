package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.chatapp.Fragment.Fragment_Home;
import com.example.chatapp.Fragment.Fragment_Message;
import com.example.chatapp.Fragment.Fragment_Profile;
import com.example.chatapp.Fragment.Fragment_Search;
import com.example.chatapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottom_nav;
    FrameLayout framelayout;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        bottom_nav=findViewById(R.id.bottom_nav);
        framelayout=findViewById(R.id.framelayout);
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,new Fragment_Home());
        fragmentTransaction.commit();

        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment=null;
                switch (item.getItemId())
                {
                    case R.id.menu_message:
                        fragment=new Fragment_Message();
                        break;
                    case R.id.menu_phone:
                        fragment=new Fragment_Search();
                        break;

                    case R.id.menu_profile:
                        fragment=new Fragment_Profile();
                        break;
                    case R.id.menu_post:
                        fragment=null;
                        Intent intent=new Intent(MainActivity.this, PostActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_home:
                        fragment= new Fragment_Home();
                        break;
                }
                if (fragment!=null)
                {
                    FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout,fragment);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.menu_sign_out==item.getItemId())
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout,menu);
        return true;
    }

    public void hide()
    {
        bottom_nav.setVisibility(View.GONE);
    }
    public  void show()
    {
        bottom_nav.setVisibility(View.VISIBLE);
    }

    public void update_status(String status)
    {
        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update_status("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        update_status("offline");
    }
}