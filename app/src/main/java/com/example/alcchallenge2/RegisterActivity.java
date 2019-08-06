package com.example.alcchallenge2;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText email;
    private EditText name;
    private EditText password;
    private Button reg;
    private Button login;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = findViewById(R.id.progressBar2);
        email = findViewById(R.id.reg_email);
        name = findViewById(R.id.reg_name);
        password = findViewById(R.id.reg_passwod);
        reg = findViewById(R.id.regbtn);
        login = findViewById(R.id.regloginbtn);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = email.getText().toString();
                String pass = password.getText().toString();
                String user = name.getText().toString();

                if(!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pass) & !TextUtils.isEmpty(user)){
                    progressBar.setVisibility(View.VISIBLE);

                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("name", user);
                    postMap.put("timestamp", FieldValue.serverTimestamp());

                    firestore.collection("Users").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(RegisterActivity.this, "Saving Data...", Toast.LENGTH_LONG).show();


                            } else {

                                Toast.makeText(RegisterActivity.this, "Data not saved...", Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                    mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                Intent setupIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(setupIntent);
                                finish();

                                Toast.makeText(RegisterActivity.this, "Welcome!", Toast.LENGTH_LONG).show();

                            } else {

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                            }

                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });
                }else {

                    Toast.makeText(RegisterActivity.this, "Blank field(s), Please complete form.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
