package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    ImageView img_close,img_added;
    TextView txt_post;
    EditText edt_description;
    Toolbar toobar_news;
    Uri mAvtUri;
    StorageReference ref;
    StorageTask uploadtask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        iniit();
        ref= FirebaseStorage.getInstance().getReference("uploads");
        img_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        txt_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadtask!=null && uploadtask.isInProgress())
                {
                    Toast.makeText(getApplicationContext(),"Upload task in process",Toast.LENGTH_SHORT).show();
                }else
                {
                    uploadimage(mAvtUri);
                }
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(PostActivity.this, MainActivity.class);
               startActivity(intent);
               finish();
            }
        });
    }
    private void uploadimage(Uri mAvtUri)
    {
        ProgressDialog progressDialog=new ProgressDialog(PostActivity.this);
        progressDialog.setMessage("Posting...");
        progressDialog.show();
        if (this.mAvtUri !=null)
        {
            StorageReference fileReference=ref.child(System.currentTimeMillis()+
                    "."+getFileExtension(this.mAvtUri));
            uploadtask=fileReference.putFile(this.mAvtUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        String des=edt_description.getText().toString();
                        String id_user=  FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Uri uridownload=task.getResult();
                        String mUri=uridownload.toString();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Post");
                        String id_post=reference.push().getKey();
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("imageURL",mUri);
                        map.put("id_post",id_post);
                        map.put("description",des);
                        map.put("id_user", id_user);
                        reference.child(id_post).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Upload thành công",Toast.LENGTH_SHORT).show();
                                }else {
                                    Log.d("Err",task.getException()+"");
                                }
                            }
                         });
                        progressDialog.dismiss();
                        Intent intent=new Intent(PostActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    }else {
                        Toast.makeText(getApplicationContext(),"Failed !",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else
        {
            Toast.makeText(getApplicationContext(),"No image selected !",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
    private void uploadImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    private void iniit() {
        toobar_news=findViewById(R.id.toobar_news);
        img_close=findViewById(R.id.img_close);
        img_added=findViewById(R.id.img_added);
        txt_post=findViewById(R.id.txt_post);
        edt_description=findViewById(R.id.edt_description);
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null)
        {
            mAvtUri=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),mAvtUri);
                img_added.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}