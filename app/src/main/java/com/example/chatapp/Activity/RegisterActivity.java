package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText edt_name,edt_email,edt_password,edt_cf_password;
    AppCompatButton btn_register;
    private FirebaseAuth auth;
    ProgressDialog progressDialog;
    DatabaseReference firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        iniit();
      progressDialog=new ProgressDialog(RegisterActivity.this);
      progressDialog.setMessage("Loading");
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=edt_name.getText().toString();
                String email=edt_email.getText().toString();
                String pass=edt_password.getText().toString();
                String cf_pass=edt_cf_password.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    edt_email.setError("Invalid Email");
                    edt_email.setFocusable(true);
                }else if (pass.length()<6)
                {
                    edt_email.setError("Password lenght at least 6 characters");
                    edt_email.setFocusable(true);
                }else if(TextUtils.isEmpty(cf_pass) || !pass.equals(cf_pass))
                {
                   Toast.makeText(getApplicationContext(),"mật khẩu không trùng khớp vui lòng nhập lại",Toast.LENGTH_SHORT).show();
                   edt_cf_password.setText("");
                }
                else {
                    register(name,email,pass);
                }
            }
        });
    }

    private void register(String username,String email, String password) {
        progressDialog.show();
      auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful())
              {
                  progressDialog.dismiss();
                  FirebaseUser user = auth.getCurrentUser();
                  String user_id=user.getUid();
                  firebaseDatabase=FirebaseDatabase.getInstance().getReference().child("User").child(user_id);
                  HashMap<String,String>hashMap=new HashMap<>();
                  hashMap.put("id",user_id);
                  hashMap.put("username",username);
                  hashMap.put("status","online");
                  hashMap.put("imageURL","default");
                  hashMap.put("search",username.toLowerCase());
                  firebaseDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful())
                          {
                              Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                              startActivity(intent);
                              finish();
                          }
                      }
                  });
                  Toast.makeText(RegisterActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();

              }else
              {
                  progressDialog.dismiss();
                  Log.d("C",task.getException()+"");
                  Toast.makeText(RegisterActivity.this, "fail",
                          Toast.LENGTH_SHORT).show();
              }
          }
      });

    }

    private void iniit() {

        auth=FirebaseAuth.getInstance();
        edt_name=findViewById(R.id.edt_name);
        edt_email=findViewById(R.id.edt_email);
        edt_password=findViewById(R.id.edt_password);
        edt_cf_password=findViewById(R.id.edt_cf_password);
        btn_register=findViewById(R.id.btn_register);
    }
    //khi đăng kí thì thêm key status oneline
    //gọi onstart khi online
    // onpause khi off

}