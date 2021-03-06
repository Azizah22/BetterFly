package com.example.betterfly;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class osignUp extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText editTextEmail, editTextPassword, editTextRepeatPassword, editTextIdApproval;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osign_up);

        editTextEmail = (EditText) findViewById(R.id.email_signup);
        editTextPassword = (EditText) findViewById(R.id.password_signup);
        editTextRepeatPassword = (EditText) findViewById(R.id.repassword_signup);
        editTextIdApproval = (EditText) findViewById(R.id.Aproval_ID);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.sign_up).setOnClickListener(this);
        //findViewById(R.id.textViewLogin).setOnClickListener(this);

    }

    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();

        ///NAME OF THE ORG
        final String name = null;
        final String password = editTextPassword.getText().toString().trim();
        String repaetPassword = editTextRepeatPassword.getText().toString().trim();
        final String approvalId = editTextIdApproval.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }


        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }


        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (repaetPassword.isEmpty()) {
            editTextRepeatPassword.setError("Re-write your password");
            editTextRepeatPassword.requestFocus();
            return;
        }

        if (!password.equals(repaetPassword)) {
            editTextPassword.setError("Your password does not match");
            editTextPassword.setText("");
            editTextRepeatPassword.setText("");
            return;
        }


        if (approvalId.isEmpty()) {
            editTextIdApproval.setError("Approval ID is required");
            editTextIdApproval.requestFocus();
            return;
        }


        if (approvalId.length() < 4) {
            editTextIdApproval.setError("Minimum length of approval ID should be 4 characters");
            editTextIdApproval.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Organization orgUser = new Organization(
                            name,
                            email,
                            approvalId,
                            Status.PROCESSING

                    );

                    FirebaseDatabase.getInstance().getReference("Organization")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(orgUser).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(osignUp.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                finish();
                                Intent intent = new Intent(osignUp.this, OrgProcessActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            } else {
                                //display a failure message
                                Toast.makeText(osignUp.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                            }


                        }
                    });
                }
                        else {
                        Toast.makeText(osignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                }

        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up:
                registerUser();
                break;

        }
    }
}