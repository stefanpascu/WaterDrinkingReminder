package com.example.waterdrinkingreminder;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    String userId;
    String selectedSex;

    TextView weightValue;
    TextView ageValue;
    TextView sexValue;
    TextView heightValue;
    TextView emailValue;
    ImageView profilePic;
    RelativeLayout profilePicWrapper;
    RelativeLayout weightWrapper;
    RelativeLayout ageWrapper;
    RelativeLayout sexWrapper;
    RelativeLayout heightWrapper;

    private Dialog dialog_weight;
    private Dialog dialog_height;
    private Dialog dialog_age;
    private Dialog dialog_sex;
    private Button logoutBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weightValue = getView().findViewById(R.id.weightValue);
        heightValue = getView().findViewById(R.id.heightValue);
        sexValue = getView().findViewById(R.id.sexValue);
        ageValue = getView().findViewById(R.id.ageValue);
        emailValue = getView().findViewById(R.id.emailValue);
        weightWrapper = getView().findViewById(R.id.weightWrapper);
        ageWrapper = getView().findViewById(R.id.ageWrapper);
        sexWrapper = getView().findViewById(R.id.sexWrapper);
        heightWrapper = getView().findViewById(R.id.heightWrapper);
        profilePicWrapper = getView().findViewById(R.id.profilePicWrapper);
        profilePic = getView().findViewById(R.id.profilePic);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userId = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firebaseStore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                weightValue.setText(documentSnapshot.getString("weight") + " kg");
                heightValue.setText(documentSnapshot.getString("height") + " cm");
                sexValue.setText(documentSnapshot.getString("sex"));
                ageValue.setText(documentSnapshot.getString("birthDate") + " y/o");
                emailValue.setText(documentSnapshot.getString("email"));
                selectedSex = documentSnapshot.getString("sex").toString();
            }
        });

        StorageReference profilePicture = storage.getReferenceFromUrl("gs://waterdrinkingreminder-2530b.appspot.com").child("images/" + userId);
        final long ONE_MEGABYTE = 1024 * 1024;
        profilePicture.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePic.setImageBitmap(bitmap);
            }
        });

        dialog_weight = new Dialog(this.getActivity());
        dialog_height = new Dialog(this.getActivity());
        dialog_age = new Dialog(this.getActivity());
        dialog_sex = new Dialog(this.getActivity());
        dialog_weight.setContentView(R.layout.dialog_edit_weight);
        dialog_height.setContentView(R.layout.dialog_edit_height);
        dialog_age.setContentView(R.layout.dialog_edit_age);
        dialog_sex.setContentView(R.layout.dialog_edit_sex);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog_weight.getWindow().setBackgroundDrawable(this.getActivity().getDrawable(R.drawable.input));
            dialog_height.getWindow().setBackgroundDrawable(this.getActivity().getDrawable(R.drawable.input));
            dialog_age.getWindow().setBackgroundDrawable(this.getActivity().getDrawable(R.drawable.input));
            dialog_sex.getWindow().setBackgroundDrawable(this.getActivity().getDrawable(R.drawable.input));
        }
        dialog_weight.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_height.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_age.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_sex.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_weight.getWindow().getAttributes().windowAnimations = R.style.dialog_animation; //Setting the animations to dialog_weight
        dialog_height.getWindow().getAttributes().windowAnimations = R.style.dialog_animation; //Setting the animations to dialog_weight
        dialog_age.getWindow().getAttributes().windowAnimations = R.style.dialog_animation; //Setting the animations to dialog_weight
        dialog_sex.getWindow().getAttributes().windowAnimations = R.style.dialog_animation; //Setting the animations to dialog_weight

        Button okBtn_weight = dialog_weight.findViewById(R.id.okBtn);
        Button okBtn_height = dialog_height.findViewById(R.id.okBtn);
        Button okBtn_age = dialog_age.findViewById(R.id.okBtn);
        Button okBtn_sex = dialog_sex.findViewById(R.id.okBtn);
        Button cancelBtn_weight = dialog_weight.findViewById(R.id.cancelBtn);
        Button cancelBtn_height = dialog_height.findViewById(R.id.cancelBtn);
        Button cancelBtn_age = dialog_age.findViewById(R.id.cancelBtn);
        Button cancelBtn_sex = dialog_sex.findViewById(R.id.cancelBtn);

        logoutBtn = getView().findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> logout());

        weightWrapper.setOnClickListener(v -> {
            dialog_weight.show();
            EditText input = dialog_weight.findViewById(R.id.inputWeight);
            input.setText(weightValue.getText().toString().split(" ")[0]);
        });

        heightWrapper.setOnClickListener(v -> {
            dialog_height.show();
            EditText input = dialog_height.findViewById(R.id.inputHeight);
            input.setText(heightValue.getText().toString().split(" ")[0]);
        });

        ageWrapper.setOnClickListener(v -> {
            dialog_age.show();
            EditText input = dialog_age.findViewById(R.id.inputAge);
            input.setText(ageValue.getText().toString().split(" ")[0]);
        });

        sexWrapper.setOnClickListener(v -> dialog_sex.show());

        okBtn_weight.setOnClickListener(v -> updateWeight());
        okBtn_height.setOnClickListener(v -> updateHeight());
        okBtn_age.setOnClickListener(v -> updateAge());
        okBtn_sex.setOnClickListener(v -> updateSex());
        cancelBtn_weight.setOnClickListener(v -> dialog_weight.dismiss());
        cancelBtn_height.setOnClickListener(v -> dialog_height.dismiss());
        cancelBtn_age.setOnClickListener(v -> dialog_age.dismiss());
        cancelBtn_sex.setOnClickListener(v -> dialog_sex.dismiss());
        profilePicWrapper.setOnClickListener(v -> makePhoto());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();//logout
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }

    private void updateWeight(){
        EditText input = dialog_weight.findViewById(R.id.inputWeight);
        String updatedWeight = input.getText().toString().split(" ")[0];
        DocumentReference documentReference = firebaseStore.collection("users").document(userId);
        documentReference.update("weight", updatedWeight);
        weightValue.setText(updatedWeight);
        dialog_weight.dismiss();
    }

    private void updateHeight(){
        EditText input = dialog_height.findViewById(R.id.inputHeight);
        String updatedHeight = input.getText().toString().split(" ")[0];
        DocumentReference documentReference = firebaseStore.collection("users").document(userId);
        documentReference.update("height", updatedHeight);
        heightValue.setText(updatedHeight);
        dialog_height.dismiss();
    }

    private void updateAge(){
        EditText input = dialog_age.findViewById(R.id.inputAge);
        String updatedAge = input.getText().toString().split(" ")[0];
        DocumentReference documentReference = firebaseStore.collection("users").document(userId);
        documentReference.update("age", updatedAge);
        ageValue.setText(updatedAge);
        dialog_age.dismiss();
    }

    private void updateSex(){
        RadioGroup radioBtnGroup = dialog_sex.findViewById(R.id.radioBtnGroup);
        if (Objects.equals(selectedSex, "Male")){
            radioBtnGroup.check(R.id.maleRadio);
        }else{
            radioBtnGroup.check(R.id.femaleRadio);
        }
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

        DocumentReference documentReference = firebaseStore.collection("users").document(userId);
        documentReference.update("sex", selectedSex);
        sexValue.setText(selectedSex);
        dialog_sex.dismiss();
    }

    private void makePhoto(){
        if(ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{
                    Manifest.permission.CAMERA
            }, 101);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            profilePic.setBackground(null);
            profilePic.setImageBitmap(bitmap);
            Toast.makeText(getContext(), "Proba", Toast.LENGTH_SHORT).show();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "profile", null);
            uploadPhoto(Uri.parse(path));
        }
    }

    private void uploadPhoto(Uri uriPhoto) {
        StorageReference profilePicture = storageReference.child("images/" + userId);
        profilePicture.putFile(uriPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
