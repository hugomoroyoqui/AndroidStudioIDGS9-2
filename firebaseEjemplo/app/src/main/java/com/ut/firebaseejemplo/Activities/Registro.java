package com.ut.firebaseejemplo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ut.firebaseejemplo.R;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    TextView goToLogin;
    Button signup;
    EditText txtEmail;
    EditText txtPassword;

    EditText firstname, lastname, phone;
    RadioGroup gender;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    int genderInt;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dialog = new ProgressDialog(Registro.this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        goToLogin = findViewById(R.id.txtGoToLogin);
        signup = findViewById(R.id.btnSignUp);
        txtEmail = findViewById(R.id.txtSignUpEmail);
        txtPassword = findViewById(R.id.txtSignUpPassword);

        firstname = findViewById(R.id.txtSignUpFirstName);
        lastname = findViewById(R.id.txtSignUpLastName);
        phone = findViewById(R.id.txtSignUpPhone);
        gender = findViewById(R.id.radioGroupGender);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginMethod();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setCancelable(false);
                dialog.setTitle("Registro");
                dialog.setMessage("Validando usuario...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                verifyUser();
            }
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonMale){
                    genderInt = 0;
                } else {
                    genderInt = 1;
                }
            }
        });
    }

    private void verifyUser(){
        db.collection("Users").document(txtEmail.getText().toString().trim())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(Registro.this,
                                "Cuenta registrada anteriormente, inicie sesion",
                                Toast.LENGTH_SHORT).show();
                        goToLoginMethod();
                        dialog.dismiss();
                    } else {
                        addUserToDatabase();
                    }
                } else {
                    Toast.makeText(Registro.this,
                            "Ocurrio un error al verificar",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    private void addUserToDatabase(){
        dialog.setMessage("Agregando usuario...");
        Map<String, Object> user = new HashMap<>();
        user.put("firstname", firstname.getText().toString());
        user.put("lastname", lastname.getText().toString());
        user.put("phone", phone.getText().toString());
        user.put("gender", genderInt);
        user.put("password", txtPassword.getText().toString());

        db.collection("Users").document(txtEmail.getText().toString().trim())
        .set(user)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                signUpUser();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registro.this,
                        "Ocurrio un error al agregar usuario", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    // Hasta aqui llega el onCreate
    private void signUpUser() {
        dialog.setMessage("Registrando usuario...");
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser response) {
        if (response != null){
            Toast.makeText(Registro.this,
                    "Registro Exitoso",
                    Toast.LENGTH_LONG).show();
            dialog.dismiss();
            goToLoginMethod();
        } else {
            Toast.makeText(Registro.this,
                    "Hubo un error en el registro.",
                    Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    private void goToLoginMethod(){
        startActivity(new Intent(Registro.this, MainActivity.class));
    }

}