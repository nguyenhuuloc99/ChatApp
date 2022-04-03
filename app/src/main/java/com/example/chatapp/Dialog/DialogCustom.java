package com.example.chatapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DialogCustom extends AppCompatDialogFragment {
    private EditText mEdtInput, mEdtConfirm;
    private String mType;
    private Context mContext;

    public DialogCustom(String mType, Context mContext) {
        this.mType = mType;
        this.mContext = mContext;
    }

    public DialogCustom(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        if (mType!=null)
        {
            View view = layoutInflater.inflate(R.layout.dialog_custom, null);
            mEdtInput = view.findViewById(R.id.edt_dialog);
            mEdtConfirm = view.findViewById(R.id.edt_dialog_confirm);
            if (mType.equals("rename"))
            {
                mEdtConfirm.setVisibility(View.GONE);
                mEdtInput.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                mEdtInput.setHint("Nhập tên mới");
                    builder.setView(view).setTitle("Rename")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                 if (!mEdtInput.getText().equals(""))
                                 {
                                     FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                                     String user_id=firebaseUser.getUid();
                                     DatabaseReference reference=FirebaseDatabase.getInstance().getReference("User").child(user_id);
                                     reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                                             String username=mEdtInput.getText().toString();
                                             Map<String,Object>map=new HashMap<>();
                                             map.put("username",username);
                                             reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if(task.isSuccessful())
                                                     {
                                                         Toast.makeText(mContext,"Thành công",Toast.LENGTH_SHORT).show();
                                                     }else
                                                     {
                                                         Toast.makeText(mContext,"Thất bại",Toast.LENGTH_SHORT).show();
                                                     }
                                                 }
                                             });
                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError error) {

                                         }
                                     });
                                 }

                                }
                            });
            }else if (mType.equals("change_password"))
            {
                builder.setView(view).setTitle("Đổi mật khẩu")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String pass_old=mEdtInput.getText().toString().trim();
                                String cfpass=mEdtConfirm.getText().toString().trim();


                                if (!pass_old.equals("") && !cfpass.equals(""))
                                {

                                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

                                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),pass_old);
                                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseUser.updatePassword(cfpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            Toast.makeText(mContext, "Password is updated", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(mContext, "Password update failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        }else
        {
            View view = layoutInflater.inflate(R.layout.dialog_about, null);
            builder.setView(view)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
        return builder.create();
    }


}
