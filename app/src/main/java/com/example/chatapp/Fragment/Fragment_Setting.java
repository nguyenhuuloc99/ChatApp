package com.example.chatapp.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Dialog.DialogCustom;
import com.example.chatapp.Activity.LoginActivity;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Fragment_Setting extends Fragment {
    public static final int MY_REQUEST_CODE = 1;
    CircleImageView mAvatar;
     TextView mUsername;
     Button mBtnAddAvt, mBtnEdtName, mBtnChangePass, mBtnLogout, mBtnTurnOffStatus, mBtnAbout;
     FirebaseUser firebaseUser;
     ProgressBar mProgressBar;
     Uri mAvtUri;
     FirebaseUser mCurrentUser;
     StorageReference ref ;
     private static final int IMAGE_REQUEST=1;
     StorageTask uploadtask;
    DatabaseReference reference;
    AppCompatImageView back;
    public Fragment_Setting() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view=inflater.inflate(R.layout.fragment__setting, container, false);

        iniit(view);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseStorage.getInstance().getReference("uploads");

        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    openImage();
            }
        });
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }
        });
        mBtnEdtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rename();
            }
        });
        mBtnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
        mBtnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about();
            }
        });
        mBtnAddAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }

    private void getUser() {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("User").child(mCurrentUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (user.getImageURL().equals("default"))
                {
                    mAvatar.setImageResource(R.drawable.image);
                }
                Glide.with(getContext()).load(user.getImageURL()).into(mAvatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadimage()
    {
        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        if (mAvtUri!=null)
        {
            StorageReference fileReference=ref .child(System.currentTimeMillis()+
                    "."+getFileExtension(mAvtUri));
            uploadtask=fileReference.putFile(mAvtUri);
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
                        Uri uridownload=task.getResult();
                        String mUri=uridownload.toString();
                        reference= FirebaseDatabase.getInstance().getReference("User").child(mCurrentUser.getUid());
                        HashMap<String,Object>map=new HashMap<>();
                        map.put("imageURL",mUri);
                        reference.updateChildren(map);
                        progressDialog.dismiss();

                    }else {
                        Toast.makeText(getContext(),"Failed !",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else
        {
            Toast.makeText(getContext(),"No image selected !",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
    private void about() {
        DialogCustom dialogCustom=new DialogCustom(getContext());
        dialogCustom.show(getActivity().getSupportFragmentManager(),"");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null)
        {
             mAvtUri=data.getData();
            if (uploadtask!=null &&uploadtask.isInProgress())
            {
                Toast.makeText(getContext(),"Upload task in process",Toast.LENGTH_SHORT).show();
            }else
            {
                uploadimage();
            }
        }
    }



    private void changePassword() {
        DialogCustom dialogCustom=new DialogCustom("change_password",getContext());
        dialogCustom.show(getActivity().getSupportFragmentManager(),"Change Password");
    }
    private void rename() {
        DialogCustom renameDialog = new DialogCustom("rename", getContext());
        renameDialog.show(getActivity().getSupportFragmentManager(), "rename dialog");
    }


    private void iniit(View view) {
        back=view.findViewById(R.id.back);
        mAvatar = view.findViewById(R.id.big_avt);
        mUsername = view.findViewById(R.id.user_name);
        mBtnAddAvt = view.findViewById(R.id.btn_add_avt);
        mBtnEdtName = view.findViewById(R.id.btn_edit_username);
        mBtnChangePass = view.findViewById(R.id.btn_change_password);
        mBtnLogout = view.findViewById(R.id.btn_logout);
        mBtnTurnOffStatus = view.findViewById(R.id.btn_turn_off_status);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mBtnAbout = view.findViewById(R.id.btn_about);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser();
    }
}