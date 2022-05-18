package com.example.waterdrinkingreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Measurements extends AppCompatActivity {
    private EditText heightInput;
    private EditText weightInput;
    private EditText ageInput;
    private RadioGroup radioBtnGroup;
    private Button submitBtn;
    String emailValue;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseStore;
    String userID;
    String selectedSex = "Male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);

        submitBtn = findViewById(R.id.submitBtn);
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        radioBtnGroup = findViewById(R.id.radioBtnGroup);
        ageInput = findViewById(R.id.ageInput);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStore = FirebaseFirestore.getInstance();

        radioBtnGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRadio){
                    selectedSex = "Male";
                }
                else if (checkedId == R.id.femaleRadio){
                    selectedSex = "Female";
                }
            }
        });

        submitBtn.setOnClickListener(v -> storeMeasurements());
    }

    private void storeMeasurements() {
        String height = heightInput.getText().toString().trim();
        String weight = weightInput.getText().toString().trim();
        String bd = ageInput.getText().toString().trim();
        userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference documentReference = firebaseStore.collection("users").document(userID);

        documentReference.update("height", height);
        documentReference.update("weight", weight);
        documentReference.update("birthDate", bd);
        documentReference.update("sex", selectedSex);
        documentReference.update("intake", Integer.toString((Integer.parseInt(weight) * 3 + Integer.parseInt(bd)) * (Integer.parseInt(height)/15)));
        documentReference.update("intakeLeft", Integer.toString((Integer.parseInt(weight) * 3 + Integer.parseInt(bd)) * (Integer.parseInt(height)/15)));

        Toast.makeText(Measurements.this, "Measurements stored", Toast.LENGTH_SHORT).show();
        goToNextActivity();
    }

    public void goToNextActivity(){
        Intent intent = new Intent(this, RegisterDone.class);
        startActivity(intent);
    }
}