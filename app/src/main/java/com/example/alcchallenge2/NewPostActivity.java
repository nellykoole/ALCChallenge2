package com.example.alcchallenge2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.DocumentTransform;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private FloatingActionButton post;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private DocumentSnapshot documentSnapshot;
    private FirebaseAuth firebaseAuth;

    private String user_id;
    private Uri mainImageURI = null;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        final EditText title = findViewById(R.id.title);
        final EditText desc = findViewById(R.id.desc);
        image = findViewById(R.id.iUrl);
        final EditText Price = findViewById(R.id.price);
        post = findViewById(R.id.post_btn);
        final ProgressBar newPostProgress = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = firebaseAuth.getCurrentUser().getUid();
        newPostProgress.setVisibility(View.INVISIBLE);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String t = title.getText().toString();
                final String d = desc.getText().toString();
                final String p = Price.getText().toString();

                if (!TextUtils.isEmpty(t) && !TextUtils.isEmpty(d) & !TextUtils.isEmpty(p)) {

                    newPostProgress.setVisibility(View.VISIBLE);

                    final StorageReference ref = storageReference.child("product_images").child(user_id + ".jpg");
                    UploadTask uploadTask = ref.putFile(mainImageURI);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("title", t);
                                userMap.put("desc", d);
                                userMap.put("price", p);
                                userMap.put("image", downloadUri.toString());
                                userMap.put("time", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Posts").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(NewPostActivity.this, "Posted", Toast.LENGTH_LONG).show();
                                            Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                            startActivity(mainIntent);
                                            finish();

                                        } else {

                                            String error = task.getException().getMessage();
                                            Toast.makeText(NewPostActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                                        }

                                        newPostProgress.setVisibility(View.INVISIBLE);

                                    }
                                });
                            }else {
                                Toast.makeText(NewPostActivity.this, "Task is not successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(NewPostActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(NewPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }
            }
        });
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(NewPostActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                image.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }



}
