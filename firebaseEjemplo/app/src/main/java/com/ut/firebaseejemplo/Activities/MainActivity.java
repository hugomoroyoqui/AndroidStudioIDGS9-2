package com.ut.firebaseejemplo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ut.firebaseejemplo.R;

public class MainActivity extends AppCompatActivity {

    // Declaraciones
    TextView goToSignUp;
    Button login;
    EditText txtEmail, txtPassword;

    private FirebaseAuth mAuth;

    // Declaracion de SharedPreference
    SharedPreferences sharedPreferences;

    Switch switchRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializacion
        goToSignUp = findViewById(R.id.txtGoToSignUp);
        login = findViewById(R.id.btnLogin);
        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPassword);

        switchRemember = findViewById(R.id.switchRemember);

        // Inicializacion de SharedPreference
        sharedPreferences = getSharedPreferences
                ("credentials", Context.MODE_PRIVATE);

        switchRemember.setChecked(sharedPreferences.getBoolean("remember", false));
        txtEmail.setText(sharedPreferences.getString("rememberEmail", ""));
        txtPassword.setText(sharedPreferences.getString("rememberPassword", ""));


        // Enviamos un Toast con el email que se encuentra dentro del objeto
        Toast.makeText(MainActivity.this,
                "" + sharedPreferences.getString("email", ""),
                Toast.LENGTH_SHORT).show();


        mAuth = FirebaseAuth.getInstance();

        // Click para ir al registro
        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Registro.class));
            }
        });

        // Click para iniciar sesion
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
    }

    private void setRemember(boolean isChecked) {
        // Creamos el editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isChecked){
            // Agregar contenido al SharedPreference
            editor.putString("rememberEmail", txtEmail.getText().toString().trim());
            editor.putString("rememberPassword", txtPassword.getText().toString());
            editor.putBoolean("remember", isChecked);
            editor.apply();
        } else {
            editor.clear();
            editor.apply();
        }
    }

    // Metodo para realizar el login
    private void doLogin(){
        String email, password;
        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    setRemember(switchRemember.isChecked());
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

    // Metodo para actualizar la vista
    private void updateUI(FirebaseUser user){
        if (user != null){

            // Creamos el editor
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Agregar contenido al SharedPreference
            editor.putString("email", user.getEmail());
            editor.apply();

            // Borrar contenido de SharedPreference
            //editor.clear();

            goToHome();
        } else {
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.login_text_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Navegar hacia el home
    private void goToHome(){
        startActivity(new Intent(MainActivity.this, Home.class));

        Toast.makeText(MainActivity.this,
                getResources().getString(R.string.login_text_welcome),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            goToHome();
        }
    }
}