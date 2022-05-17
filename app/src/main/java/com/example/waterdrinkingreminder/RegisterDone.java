package com.example.waterdrinkingreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class RegisterDone extends AppCompatActivity {

    private Button goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_done);

        goToLogin = findViewById(R.id.goToLoginBtn);
        goToLogin.setOnClickListener(v -> goToLoginPage());

    }

    public void goToLoginPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}