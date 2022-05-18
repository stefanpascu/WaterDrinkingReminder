package com.example.waterdrinkingreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    private TextView goToLogin;
    private Button registerBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseStore;
    private EditText emailInput;
    private EditText pwdInput;
    String userID;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        goToLogin = (TextView) findViewById(R.id.goToLogin);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        emailInput = (EditText) findViewById(R.id.emailInput);
        pwdInput = (EditText) findViewById(R.id.pwdInput);

        goToLogin.setOnClickListener(v -> goToLoginPage());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStore = FirebaseFirestore.getInstance();


        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString().trim();
                String password = pwdInput.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    emailInput.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    pwdInput.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6){
                    pwdInput.setError("Password Must be >= 6 Characters");
                    return;
                }

                // register the user in firebase

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // send verification link

                            FirebaseUser fuser = firebaseAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                }
                            });

                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("height", "");
                            user.put("weight", "");
                            user.put("birthDate", "");
                            user.put("sex", "");
                            user.put("intake", "");
                            user.put("intakeLeft", "");
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Measurements.class));

                        }else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }




    public void goToLoginPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}